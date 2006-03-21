/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;


/**
 * ProbabilityDensityPlot is the plot that displays probability density.
 * Its data series is managed by WaveFunctionPlot.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ProbabilityDensityPlot extends QTXYPlot {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ProbabilityDensityPlot( XYSeries probabilityDensitySeries ) {
        super();
        
        // Labels (localized)
        String probabilityDensityLabel = SimStrings.get( "axis.probabilityDensity" );
        
        // Dataset
        XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries( probabilityDensitySeries );
        
        // Renderer
        XYItemRenderer renderer = new StandardXYItemRenderer();
        renderer.setSeriesPaint( 0, QTConstants.PROBABILITY_DENSITY_COLOR );
        renderer.setSeriesStroke( 0, QTConstants.PROBABILITY_DENSITY_STROKE );
        
        // X axis 
        PositionAxis xAxis = new PositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( probabilityDensityLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.DEFAULT_PROBABILITY_DENSITY_RANGE );
        yAxis.setTickLabelPaint( QTConstants.TICKS_COLOR );
        yAxis.setTickMarkPaint( QTConstants.TICKS_COLOR );
        
        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.CHART_COLOR );
        setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainGridlinePaint( QTConstants.GRIDLINES_COLOR );
        setRangeGridlinePaint( QTConstants.GRIDLINES_COLOR );
        setDataset( data );
        setRenderer( renderer );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis );
    }
}
