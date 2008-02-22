/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.charts;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;

/**
 * AccumulationVersusElevationChart displays a "Accumulation versus Elevation" chart.
 * The chart updates as climate is changed.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AccumulationVersusElevationChart extends JDialog {
    
    private static final Range ELEVATION_RANGE = new Range( 0, 10E3 ); // meters
    private static final double DELTA_ELEVATION = 100; // meters
    
    private Climate _climate;
    private ClimateListener _climateListener;
    private XYSeries _series;
    
    public AccumulationVersusElevationChart( Frame owner, Dimension size, Climate climate ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _climate = climate;
        _climateListener = new ClimateAdapter() {
            public void snowfallChanged() {
                update();
            }
            public void snowfallReferenceElevationChanged() {
                update();
            }
        };
        _climate.addClimateListener( _climateListener );
        
        // create the chart
        _series = new XYSeries( "accumulationVersusElevation" );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_ACCUMULATION_VERSUS_ELEVATION, // title
            GlaciersStrings.AXIS_ACCUMULATION, // x axis label
            GlaciersStrings.AXIS_ELEVATION,  // y axis label
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        rangeAxis.setRange( ELEVATION_RANGE );
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setMouseZoomable( false );
        setContentPane( chartPanel );
        
        addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                cleanup();
            }
            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                cleanup();
            }
        });
        
        update();
    }
    
    private void cleanup() {
        System.out.println( "AccumulationVersusElevationChart.cleanup" );//XXX
        _climate.removeClimateListener( _climateListener );
    }
    
    private void update() {
        _series.clear();
        double elevation = ELEVATION_RANGE.getLowerBound();
        double accumulation = 0;
        final double maxElevation = ELEVATION_RANGE.getUpperBound();
        while ( elevation <=  maxElevation ) {
            accumulation = _climate.getAccumulation( elevation );
            _series.add( accumulation, elevation );
            elevation += DELTA_ELEVATION;
        }
    }
}
