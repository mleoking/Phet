package edu.colorado.phet.common.motion.tests;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.MotionModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestTimeSeriesGraphSetNode {
    private JFrame frame;
    private TimeSeriesGraphSetNode timeSeriesGraphSetNode;
    private PhetPCanvas pSwingCanvas;
    private IClock clock;

    public TestTimeSeriesGraphSetNode() {
        frame = new JFrame();
        frame.setSize( 1024, 768 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        pSwingCanvas = new PhetPCanvas();
        frame.setContentPane( pSwingCanvas );
        final TestMotionModel testMotionModel = new TestMotionModel();
        timeSeriesGraphSetNode = new TimeSeriesGraphSetNode( new GraphSetModel( new TestGraphSet( pSwingCanvas, testMotionModel ).getGraphSuite( 0 ) ), new TimeSeriesModel( new TestTimeSeries.MyRecordableModel(), 1.0 ) );
        pSwingCanvas.getLayer().addChild( timeSeriesGraphSetNode );
        pSwingCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        clock = new SwingClock( 30, 1 );
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                testMotionModel.stepInTime( clockEvent.getSimulationTimeChange() );
            }
        });
    }

    class TestGraphSet extends GraphSuiteSet {
        private MinimizableControlGraph positionGraph;

        public TestGraphSet( PhetPCanvas pSwingCanvas, final TestMotionModel motionModel ) {
            super( new CursorModel( motionModel.getTimeSeriesModel() ) );
            positionGraph = new MinimizableControlGraph( UnicodeUtil.THETA, toControlGraph( pSwingCanvas, "X", "Position", -Math.PI * 3, Math.PI * 3, Color.blue, motionModel.getXVariable(), new PImage( loadArrow( "blue-arrow.png" ) ), true, cursorModel, motionModel ) );
            addGraphSuite( new GraphSuite( new MinimizableControlGraph[]{positionGraph} ) );

            motionModel.addListener( new MotionModel.Listener() {
                public void steppedInTime() {
                    positionGraph.addValue( motionModel.getTime(), motionModel.getPosition() );
                }
            } );
            positionGraph.addControlGraphListener( new ControlGraph.Adapter() {
                public void controlFocusGrabbed() {
                    motionModel.setPositionDriven();
                }
            } );
        }
    }

    class TestMotionModel extends MotionModel {
    }

    private void relayout() {
        timeSeriesGraphSetNode.setBounds( 0, 0, pSwingCanvas.getWidth(), pSwingCanvas.getHeight() );
    }

    public static void main( String[] args ) {
        new TestTimeSeriesGraphSetNode().start();
    }

    private void start() {
        frame.setVisible( true );
        clock.start();
    }
}
