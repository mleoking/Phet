package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;

/**
 * Created by: Sam
 * Dec 28, 2007 at 7:50:57 PM
 */
public class PlatformNode2 extends PNode {
    private RotationPlatform platform;
    private ArrayList segments = new ArrayList();

    public PlatformNode2( RotationPlatform platform ) {
        addChild( new PhetPPath( new Rectangle2D.Double( -0.1, -0.1, 0.2, 0.2 ), Color.red ) );
        this.platform = platform;
        for ( int quadrant = 0; quadrant < 4; quadrant++ ) {
            for ( int layer = 3; layer >= 1; layer-- ) {
                final double startAngle = quadrant * Math.PI * 2 / 4;
//                PlatformSegment segment = new PlatformSegment( this, startAngle, startAngle + Math.PI / 2, layer + 0.1, layer + 1 - 0.1, -0.3, -0.3, layer == 3 );
                PlatformSegment segment = new PlatformSegment( this, startAngle, startAngle + Math.PI / 2, layer + 0.1, layer + 1 - 0.1, -0.3, -0.3, true );
                addSegment( segment );
            }
        }
//        PlatformSegment segment = new PlatformSegment( this, 0, 0 + Math.PI / 2, 2, 3, -0.3, -0.3 );
//        addSegment( segment );
        platform.getPositionVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                update();
            }
        } );
        platform.addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                update();
            }

            public void innerRadiusChanged() {
                update();
            }
        } );

        update();
    }

    private void addSegment( PlatformSegment segment ) {
        segments.add( segment );
        addChild( segment );
    }

    private void update() {
        for ( int i = 0; i < segments.size(); i++ ) {
            PlatformSegment platformSegment = (PlatformSegment) segments.get( i );
            platformSegment.update();
        }
    }

    private class PlatformSegment extends PNode {
        private double startAngle;
        private double endAngle;
        private double innerRadius;
        private double outerRadius;
        private PhetPPath body;
        private PhetPPath bottomPanel;
        private PhetPPath northPanel;
        private double edgeDX;
        private double edgeDY;
        private PhetPPath southPanel;

        public PlatformSegment( PlatformNode2 platformNode2, double startAngle, double endAngle, double innerRadius, double outerRadius, double edgeDX, double edgeDY, boolean showEdge ) {
            this.edgeDX = edgeDX;
            this.edgeDY = edgeDY;
            this.startAngle = startAngle + 0.1;
            this.endAngle = endAngle - 0.1;
            this.innerRadius = innerRadius;
            this.outerRadius = outerRadius;

            bottomPanel = new PhetPPath( Color.red );
            addChild( bottomPanel );

            northPanel = new PhetPPath( Color.magenta );
            southPanel = new PhetPPath( Color.cyan );
            if ( showEdge ) {
                addChild( northPanel );
                addChild( southPanel );
            }

            body = new PhetPPath( new Rectangle2D.Double( innerRadius, 0, 1, 1 ), new Color( 0f, 0, 1f, 0.5f ), new BasicStroke( 0.03f ), Color.black );
            addChild( body );


        }

        public void update() {

            final double extent = ( endAngle - startAngle ) * 360 / 2 / Math.PI;
            Arc2D.Double outerArc = new Arc2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2, startAngle * 360 / 2 / Math.PI + platform.getPosition(), extent, Arc2D.Double.OPEN );
            Arc2D.Double outerArcRev = new Arc2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2, startAngle * 360 / 2 / Math.PI + platform.getPosition() + extent, -extent, Arc2D.Double.OPEN );
            Arc2D.Double innerArc = new Arc2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2, startAngle * 360 / 2 / Math.PI + extent + platform.getPosition(), -extent, Arc2D.Double.OPEN );
            Arc2D.Double innerArcRev = new Arc2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2, startAngle * 360 / 2 / Math.PI + platform.getPosition(), extent, Arc2D.Double.OPEN );
            GeneralPath path = toPathSegment( outerArc, innerArc );
            body.setPathTo( path );

            Arc2D.Double outerArcDepth = new Arc2D.Double( -outerRadius + edgeDX, -outerRadius + edgeDY, outerRadius * 2, outerRadius * 2, startAngle * 360 / 2 / Math.PI + platform.getPosition(), extent, Arc2D.Double.OPEN );
            Arc2D.Double innerArcDepth = new Arc2D.Double( -innerRadius + edgeDX, -innerRadius + edgeDY, innerRadius * 2, innerRadius * 2, startAngle * 360 / 2 / Math.PI + extent + platform.getPosition(), -extent, Arc2D.Double.OPEN );

//            Shape p2 = path.createTransformedShape( AffineTransform.getTranslateInstance( edgeDX, edgeDY ) );
            bottomPanel.setPathTo( toPathSegment( outerArcDepth, innerArcDepth ) );
            northPanel.setPathTo( toPathSegment( outerArcRev, outerArcDepth ) );
            southPanel.setPathTo( toPathSegment( innerArcRev, innerArcDepth ) );
        }

        private GeneralPath toPathSegment( Arc2D.Double outerArc, Arc2D.Double innerArc ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( (float) outerArc.getStartPoint().getX(), (float) outerArc.getStartPoint().getY() );
            path.append( outerArc, true );
            path.append( innerArc, true );
            path.closePath();
            return path;
        }
    }

    public static void main( String[] args ) {
        PhetApplication phetApplication = new PhetApplication( new PhetApplicationConfig( args, new FrameSetup.CenteredWithSize( 800, 600 ), PhetResources.forProject( "rotation" ) ) );
        phetApplication.addModule( new TestModule( "test", new ConstantDtClock( 30, 1 ) ) );
        phetApplication.startApplication();
    }

    static class TestModule extends Module {
        private PCanvas panel;

        public TestModule( String name, IClock clock ) {
            super( name, clock );
            panel = new PCanvas();
            panel.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    updateTx();
                }
            } );
            panel.setZoomEventHandler( new PZoomEventHandler() );
            panel.setPanEventHandler( new PPanEventHandler() );
//            panel.getPhetRootNode().scaleWorldAboutPoint( 100, new Point2D.Double( ) );
            final RotationPlatform rotationPlatform = new RotationPlatform();
            final PlatformNode2 node2 = new PlatformNode2( rotationPlatform );
            panel.getLayer().addChild( node2 );
//            panel.getCamera().animateViewToPanToBounds( node2.getGlobalFullBounds(), 1000 );
//            panel.getCamera().animateViewToCenterBounds( node2.getGlobalFullBounds(), true, 1000 );

            setSimulationPanel( panel );
            updateTx();
            rotationPlatform.setVelocity( 1.0 / Math.PI / 2.0 * 3 );
            rotationPlatform.setVelocityDriven();
            getClock().addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
//                    System.out.println( "rotationPlatform.getPosition() = " + rotationPlatform.getPosition() );
                    rotationPlatform.stepInTime( clockEvent.getSimulationTime(), clockEvent.getSimulationTimeChange() );
                }
            } );
        }

        private void updateTx() {
            panel.getCamera().setViewTransform( new AffineTransform() );
            panel.getCamera().translateView( panel.getWidth() / 2, panel.getHeight() / 2 );
            panel.getCamera().scaleView( 50 );
        }
    }
}
