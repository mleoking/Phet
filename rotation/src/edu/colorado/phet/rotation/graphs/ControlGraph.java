package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.nodes.PhetPPath;
import edu.colorado.phet.piccolo.nodes.ShadowPText;
import edu.colorado.phet.rotation.model.SimulationVariable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 10:38:00 PM
 * Copyright (c) Dec 28, 2006 by Sam Reid
 */

public class ControlGraph extends PNode {
    private GraphControlNode graphControlNode;
    private ChartSlider chartSlider;
    private ZoomSuiteNode zoomControl;

    private ArrayList listeners = new ArrayList();
    private XYSeries xySeries;
    private double xPad = 0;
    private JFreeChartNode jFreeChartNode;
    private PNode titleNode;

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String abbr, String title, double range ) {
        this( pSwingCanvas, simulationVariable, abbr, title, range, Color.black );
    }

    public ControlGraph( PSwingCanvas pSwingCanvas, final SimulationVariable simulationVariable, String abbr, String title, double range, Color color ) {
        xySeries = new XYSeries( "series_1" );

        XYDataset dataset = new XYSeriesCollection( xySeries );
        JFreeChart jFreeChart = ChartFactory.createXYLineChart( title + ", " + abbr, null, null, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.setTitle( (String)null );
        jFreeChart.getXYPlot().getRangeAxis().setAutoRange( false );
        jFreeChart.getXYPlot().getRangeAxis().setRange( -range, range );
        jFreeChart.setBackgroundPaint( null );
        jFreeChartNode = new JFreeChartNode( jFreeChart );
        jFreeChartNode.setBounds( 0, 0, 300, 400 );
        graphControlNode = new GraphControlNode( pSwingCanvas, abbr, simulationVariable, new DefaultGraphTimeSeries(), color );
        chartSlider = new ChartSlider( jFreeChartNode, new PText( "THUMB" ) );
        zoomControl = new ZoomSuiteNode();

        titleNode = new PNode();
        ShadowPText titlePText = new ShadowPText( title );
        titlePText.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        titlePText.setTextPaint( color );
        titleNode.addChild( new PhetPPath( RectangleUtils.expand( titlePText.getFullBounds(), 2, 2 ), Color.white, new BasicStroke(), Color.black ) );
        titleNode.addChild( titlePText );

        addChild( graphControlNode );
        addChild( chartSlider );
        addChild( jFreeChartNode );
        addChild( zoomControl );
        addChild( titleNode );

        simulationVariable.addListener( new SimulationVariable.Listener() {
            public void valueChanged() {
                chartSlider.setValue( simulationVariable.getValue() );
            }
        } );
        chartSlider.addListener( new ChartSlider.Listener() {
            public void valueChanged() {
                simulationVariable.setValue( chartSlider.getValue() );
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                notifyListeners();
            }
        } );
        jFreeChartNode.updateChartRenderingInfo();
        relayout();
    }

    public JFreeChartNode getJFreeChartNode() {
        return jFreeChartNode;
    }

    public boolean setBounds( double x, double y, double width, double height ) {
        relayout();
        jFreeChartNode.setBounds( 0, 0, width - xPad, height );
        relayout();
        setOffset( x, y );
        return super.setBounds( x, y, width, height );
    }

    private void relayout() {
        double dx = 5;
        graphControlNode.setOffset( 0, 0 );
        chartSlider.setOffset( graphControlNode.getFullBounds().getMaxX() + dx, 0 );
        jFreeChartNode.setOffset( chartSlider.getFullBounds().getMaxX(), 0 );
        zoomControl.setOffset( jFreeChartNode.getFullBounds().getMaxX(), jFreeChartNode.getFullBounds().getCenterY() - zoomControl.getFullBounds().getHeight() / 2 );
//        titleNode.setOffset( jFreeChartNode.getFullBounds().getX(), jFreeChartNode.getFullBounds().getY() );
        double xMin = jFreeChartNode.getChart().getXYPlot().getDomainAxis().getLowerBound();
        double xMax = jFreeChartNode.getChart().getXYPlot().getDomainAxis().getUpperBound();
        double yMin = jFreeChartNode.getChart().getXYPlot().getRangeAxis().getLowerBound();
        double yMax = jFreeChartNode.getChart().getXYPlot().getRangeAxis().getUpperBound();
        Rectangle2D.Double r = new Rectangle2D.Double();
        r.setFrameFromDiagonal( xMin, yMin, xMax, yMax );
        Rectangle2D d = jFreeChartNode.plotToNode( r );
        System.out.println( "d = " + d );
        titleNode.setOffset( d.getX() + jFreeChartNode.getOffset().getX(), d.getY() + jFreeChartNode.getOffset().getY() );

        this.xPad = jFreeChartNode.getFullBounds().getX() + zoomControl.getFullBounds().getWidth();
    }

    public void addValue( double time, double value ) {
        xySeries.add( time, value );
    }

    public static interface Listener {
        void mousePressed();

        void valueChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.mousePressed();
        }
    }
}
