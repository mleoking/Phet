/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.charts;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.ui.RectangleInsets;


public class PositionHistogramChart extends JFreeChart {

    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 255, 200 ); // translucent white

    private static final RectangleInsets INSETS = new RectangleInsets( 0, 10, 10, 10 ); // top,left,bottom,right
    
    private PositionHistogramPlot _plot;
    
    public PositionHistogramChart( PositionHistogramPlot plot ) {
        super( null /* title */, null /* titleFont */, plot, false /* createLegend */ );
        setAntiAlias( true );
        setBorderVisible( false );
        setBackgroundPaint( BACKGROUND_COLOR );
        setPadding( INSETS );
        _plot = plot;
    }
    
    public void clearData() {
        _plot.clearData();
    }
}
