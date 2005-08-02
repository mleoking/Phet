/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PBounds;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleInsets;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Aug 2, 2005
 * Time: 2:17:06 PM
 * Copyright (c) Aug 2, 2005 by Sam Reid
 */

public class TimePlotSuitePNode extends PNode {
    private PCanvas pCanvas;
    private Range2D range;
    private TimeSeriesModel timeSeriesModel;
    private XYDataset dataset;
    private XYPlot plot;
    private BufferedImage bufferedImage;
    private PImage child;

    public TimePlotSuitePNode( PCanvas pCanvas, Range2D range, String name, TimeSeriesModel timeSeriesModel ) {
        this.pCanvas = pCanvas;
        this.range = range;
        this.timeSeriesModel = timeSeriesModel;
        dataset = createDataset();
        JFreeChart chart = createChart( dataset, name );
        this.plot = (XYPlot)chart.getPlot();
        bufferedImage = chart.createBufferedImage( 800, 300 );
        child = new PImage( bufferedImage );
        addChild( child );
    }

    private static JFreeChart createChart( XYDataset dataset, String title ) {
        JFreeChart chart = ChartFactory.createXYLineChart( title,
                                                           "Time (seconds)", // x-axis label
                                                           "Energy", // y-axis label
                                                           dataset, // data
                                                           PlotOrientation.VERTICAL,
                                                           true, // create legend?
                                                           false, // generate tooltips?
                                                           false               // generate URLs?
        );

        chart.setBackgroundPaint( Color.lightGray );

        XYPlot plot = (XYPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.white );
        plot.setDomainGridlinePaint( Color.gray );
        plot.setRangeGridlinePaint( Color.gray );
        plot.setAxisOffset( new RectangleInsets( 5.0, 5.0, 5.0, 5.0 ) );
        plot.setDomainCrosshairVisible( true );
        plot.setRangeCrosshairVisible( true );

        XYItemRenderer r = plot.getRenderer();
        if( r instanceof XYLineAndShapeRenderer ) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)r;

            renderer.setDefaultShapesVisible( true );
            renderer.setDefaultShapesFilled( true );

//            renderer.setDefaultShapesVisible( false );
//            renderer.setDefaultShapesFilled( false );
//            renderer.setBaseStroke( new BasicStroke( 2));
            renderer.setStroke( new BasicStroke( 3 ) );
        }
        return chart;

    }

    private static XYDataset createDataset() {
        XYSeries xySeries = new XYSeries( new Integer( 0 ) );
        XYDataset xyDataset = new XYSeriesCollection( xySeries );
        xySeries.add( 0, 0 );
        xySeries.add( 20, 30000 );
        return xyDataset;
    }

    public void addTimeSeries( TimeSeriesPNode timeSeriesPNode ) {
    }

    public void reset() {
        System.out.println( "TODO" );
    }

    Point2D.Double lastScreenPoint = null;

    public void addPoint( double x1, double y1 ) {
//        System.out.println( "x1 = " + x1 + ", y1=" + y1 );

        Graphics2D graphics2D = bufferedImage.createGraphics();

        // calculate the data area...
        Rectangle2D dataArea = plot.getDataArea();
        if( dataArea == null ) {
            throw new RuntimeException( "Null data area" );
        }

        double transX1 = plot.getDomainAxisForDataset( 0 ).valueToJava2D( x1, dataArea, plot.getDomainAxisEdge() );
        double transY1 = plot.getRangeAxisForDataset( 0 ).valueToJava2D( y1, dataArea, plot.getRangeAxisEdge() );
        Point2D.Double pt = new Point2D.Double( transX1, transY1 );
//        System.out.println( "pt = " + pt );

        if( lastScreenPoint != null ) {
            Line2D.Double screenLine = new Line2D.Double( lastScreenPoint, pt );
            graphics2D.setColor( Color.blue );

            graphics2D.setClip( dataArea );
            graphics2D.setStroke( new BasicStroke( 2 ) );
            graphics2D.draw( screenLine );

            Rectangle2D bounds = screenLine.getBounds2D();
            bounds = RectangleUtils.expand( bounds, 1, 1 );
            child.repaintFrom( new PBounds( bounds ), child );
        }

        lastScreenPoint = new Point2D.Double( pt.getX(), pt.getY() );
    }
}
