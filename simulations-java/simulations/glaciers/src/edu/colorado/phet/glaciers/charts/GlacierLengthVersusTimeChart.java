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
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.Glacier.GlacierAdapter;
import edu.colorado.phet.glaciers.model.Glacier.GlacierListener;

/**
 * GlacierLengthVersusTimeChart displays a "Glacier Length versus Time" chart.
 * The chart updates as the glacier evolves.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlacierLengthVersusTimeChart extends JDialog {
    
    private Glacier _glacier;
    private GlacierListener _glacierListener;
    private XYSeries _series;
    
    public GlacierLengthVersusTimeChart( Frame owner, Dimension size, Glacier glacier ) {
        super( owner );
        
        setSize( size );
        setResizable( false );
        
        _glacier = glacier;
        _glacierListener = new GlacierAdapter() {
            public void iceThicknessChanged() {
                update();
            }
        };
        _glacier.addGlacierListener( _glacierListener );
        
        // create the chart
        _series = new XYSeries( "glacierLengthVersusTime" );
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( _series );
        JFreeChart chart = ChartFactory.createXYLineChart(
            GlaciersStrings.TITLE_GLACIER_LENGTH_VERSUS_TIME, // title
            GlaciersStrings.AXIS_TIME, // x axis label
            GlaciersStrings.AXIS_GLACIER_LENGTH,  // y axis label
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
        System.out.println( "GlacierLengthVersusTimeChart.cleanup" );//XXX
        _glacier.removeGlacierListener( _glacierListener );
    }
    
    public void clear() {
        _series.clear();
    }
    
    private void update() {
        //XXX add a data point every time the glacier's length changes
    }
}
