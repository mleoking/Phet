/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.nuclearreactor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Constants;
import edu.colorado.phet.nuclearphysics2.util.SimpleChartPanel;

/**
 * This class represents a panel which tracks and displays the energy released
 * per second and the total energy released by the nuclear reactor.
 *
 * @author John Blanco
 */
public class NuclearReactorEnergyGraphPanel extends JPanel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Constants that control the ranges for the graph.  These were set up by
    // trial and error, but it may make sense to coordinate them with the
    // nuclear reactor model eventually.
    private static final double TOTAL_ENERGY_GRAPH_RANGE = 0.11E-7;
    private static final double ENERGY_PER_SECOND_GRAPH_RANGE = TOTAL_ENERGY_GRAPH_RANGE / 6;
    
    // Keys for creating and manipulating data sets for charts.
    private static final String TOTAL_ENERGY_ROW_KEY         = new String("Total");
    private static final String TOTAL_ENERGY_COLUMN_KEY      = new String("Energy");
    private static final String PER_SECOND_ENERGY_ROW_KEY    = new String("Instantaneous");
    private static final String PER_SECOND_ENERGY_COLUMN_KEY = new String("Power");
    
    // Fonts for the graphs.
    private static final Font LABEL_FONT = new PhetFont(Font.BOLD, 14);
    
    // Initial size for charts.
    private static final int INITIAL_CHART_WIDTH = 100;
    private static final int INITIAL_CHART_HEIGHT = 300;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private NuclearReactorModel    _nuclearReactorModel;
    private DefaultCategoryDataset _energyPerSecondDataSet;
    private DefaultCategoryDataset _totalEnergyDataSet;
    private JFreeChart             _energyPerSecondChart;
    private JFreeChart             _totalEnergyChart;
    
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public NuclearReactorEnergyGraphPanel( NuclearReactorModel nuclearReactorModel) {
        
        _nuclearReactorModel = nuclearReactorModel;
        
        // Do local initialization.
        _energyPerSecondDataSet = new DefaultCategoryDataset();
        _totalEnergyDataSet     = new DefaultCategoryDataset();
        
        // Register as a listener for energy changes within the reactor.
        _nuclearReactorModel.addListener( new NuclearReactorModel.Adapter(){
            public void energyChanged(){
                updateLevels();
            }
        });
        
        // Add the border around the chart.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                "Energy Graphs",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridLayout(0, 2) );
        
        // Create and add the energy charts.
        _energyPerSecondChart = createEnergyPerSecondChart();
        SimpleChartPanel energyPerSecondChartPanel = new SimpleChartPanel(_energyPerSecondChart, INITIAL_CHART_WIDTH, 
                INITIAL_CHART_HEIGHT);
        add(energyPerSecondChartPanel);
        
        _totalEnergyChart = createTotalEnergyChart();
        SimpleChartPanel totalEnergyChartPanel = new SimpleChartPanel(_totalEnergyChart, INITIAL_CHART_WIDTH, 
                INITIAL_CHART_HEIGHT);
        add(totalEnergyChartPanel);
        
        // Set initial levels.
        updateLevels();
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    private void updateLevels(){
        _totalEnergyDataSet.setValue(_nuclearReactorModel.getTotalEnergyReleased(), TOTAL_ENERGY_ROW_KEY,
                TOTAL_ENERGY_COLUMN_KEY);

        _energyPerSecondDataSet.setValue(_nuclearReactorModel.getEnergyReleasedPerSecond(), 
                PER_SECOND_ENERGY_ROW_KEY, PER_SECOND_ENERGY_COLUMN_KEY);
    }
    
    private JFreeChart createEnergyPerSecondChart(){
        
        _energyPerSecondDataSet.setValue(ENERGY_PER_SECOND_GRAPH_RANGE, PER_SECOND_ENERGY_ROW_KEY, 
                PER_SECOND_ENERGY_COLUMN_KEY);
        
        // TODO: JPB TBD - the strings used here need to be found in or added to the properties file.
        JFreeChart chart = ChartFactory.createBarChart(null, null, "joules/sec", _energyPerSecondDataSet, 
                PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint( NuclearPhysics2Constants.CONTROL_PANEL_COLOR );
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.darkGray );
        plot.setRangeGridlinePaint(Color.white);
        
        // Set the range for the Y axis.
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange( 0, ENERGY_PER_SECOND_GRAPH_RANGE );
        rangeAxis.setTickLabelsVisible( false );
        rangeAxis.setLabelFont( LABEL_FONT );
        
        // Set the font for the X axis.
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont( LABEL_FONT );
        
        // Disable bar outlines.
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.01);
        
        // Set up gradient paint.
        GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.orange, 
            0.0f, 0.0f, Color.lightGray
        );

        renderer.setSeriesPaint(0, gp0);
        
        return chart;
    }
    
    private JFreeChart createTotalEnergyChart(){
        
        _totalEnergyDataSet.setValue(TOTAL_ENERGY_GRAPH_RANGE, TOTAL_ENERGY_ROW_KEY, TOTAL_ENERGY_COLUMN_KEY);
        
        // TODO: JPB TBD - the strings used here need to be found in or added to the properties file.
        JFreeChart chart = ChartFactory.createBarChart(null, null, "joules", _totalEnergyDataSet, PlotOrientation.VERTICAL,
                false, false, false);
        chart.setBackgroundPaint( NuclearPhysics2Constants.CONTROL_PANEL_COLOR );
        CategoryPlot plot = (CategoryPlot)chart.getPlot();
        plot.setBackgroundPaint( Color.DARK_GRAY );
        plot.setRangeGridlinePaint(Color.white);
        
        // Set the range for the Y axis.
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange( 0, TOTAL_ENERGY_GRAPH_RANGE );
        rangeAxis.setTickLabelsVisible( false );
        rangeAxis.setLabelFont( LABEL_FONT );
        
        // Set the font for the X axis.
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont( LABEL_FONT );
        
        // Disable bar outlines.
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setItemMargin(0.01);
        
        // Set up gradient paint.
        GradientPaint gp0 = new GradientPaint(
            0.0f, 0.0f, Color.green, 
            0.0f, 0.0f, Color.lightGray
        );

        renderer.setSeriesPaint(0, gp0);
        
        return chart;
    }
}
