/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.module.NeuronDefaults;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Chart for depicting the membrane potential.  This is a PNode, and as such
 * is intended for use primarily in the play area.
 * 
 * Originally, this chart was designed to scroll once there was enough data
 * the fill the chart half way, but this turned out to be too CPU intensive,
 * so it was changed to draw one line of data across the screen, then clear
 * and draw the next line.

 * Author: John Blanco
 */

public class MembranePotentialChart extends PNode {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	private static final double TIME_SPAN = 25; // In milliseconds.
	
	// This value sets the frequency of chart updates, which helps to reduce
	// the processor consumption.
	private static final double UPDATE_PERIOD = 1 * NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT; // In seconds
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
    private JFreeChart chart;
    private JFreeChartNode jFreeChartNode;
    private AxonModel axonModel;
	private XYSeries dataSeries = new XYSeries("0");

	private static NumberAxis xAxis;
	private static NumberAxis yAxis;
	
	private double updateCountdownTimer = 0;  // Init to zero to an update occurs right away.
	
	private boolean recording = false;
	private double timeIndexOfFirstDataPt = 0;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
    public MembranePotentialChart( Dimension2D size, String title, final AxonModel axonModel ) {
    	
        this.axonModel = axonModel;
        
        if (axonModel != null){
        	
        	// Register for clock ticks so that we can update.
        	axonModel.getClock().addClockListener(new ClockAdapter(){
        	    public void clockTicked( ClockEvent clockEvent ) {
        	    	updateChart(clockEvent);
        	    }
        	    public void simulationTimeReset( ClockEvent clockEvent ) {
        	    	recording = false;
        	    	clearChart();
        	    }
        	});
        	
        	// Register for model events that are important to us.
        	axonModel.addListener(new AxonModel.Adapter(){
        		
        		public void stimulusPulseInitiated() {
        			if (!MembranePotentialChart.this.axonModel.isPotentialChartVisible()){
        				// If the chart is not visible, we clear any previous
        				// recording.
        				clearChart();
        			}
        			// Start recording, if it isn't already happening.
        			recording = true;
        		}
        	});
        }
        
        // Create the chart.
        XYDataset dataset = new XYSeriesCollection( dataSeries );
        chart = createXYLineChart( title, NeuronStrings.MEMBRANE_POTENTIAL_X_AXIS_LABEL,
        		NeuronStrings.MEMBRANE_POTENTIAL_Y_AXIS_LABEL, dataset, PlotOrientation.VERTICAL);
        chart.getXYPlot().getRangeAxis().setTickLabelsVisible( true );
        chart.getXYPlot().getRangeAxis().setRange( -100, 100 );
        jFreeChartNode = new JFreeChartNode( chart, false );
        jFreeChartNode.setBounds( 0, 0, size.getWidth(), size.getHeight() );

        chart.getXYPlot().getDomainAxis().setRange( 0, TIME_SPAN );

        jFreeChartNode.updateChartRenderingInfo();

        // Add the chart to this node.
        addChild( jFreeChartNode );
        
        // Add the chart cursor, which will allow the user to move back and
        // forth through time.
        ChartCursor chartCursor = new ChartCursor(jFreeChartNode);
        Point2D topLeftOfPlotArea = jFreeChartNode.plotToNode( new Point2D.Double( 0, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
        chartCursor.setOffset(topLeftOfPlotArea);
        addChild(chartCursor);
        
		// Add the button that will allow the user to close the chart.
		ImageIcon imageIcon = new ImageIcon( 
				PhetCommonResources.getInstance().getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON) );
		JButton closeButton = new JButton( imageIcon );
		closeButton.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				axonModel.setPotentialChartVisible(false);
			}
		} );
		
		PSwing closePSwing = new PSwing( closeButton );
		closePSwing.setOffset(size.getWidth() - closeButton.getBounds().width - 2, 2);
		closePSwing.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
		addChild(closePSwing);
		
        // Create a button for clearing the chart.
        JButton clearButton = new JButton(NeuronStrings.MEMBRANE_POTENTIAL_CLEAR_CHART);
        clearButton.setFont(new PhetFont(14));
        clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// If an action potential is in progress, start or continue
				// recording.
				if (axonModel.isStimulusInitiationLockedOut()){
					recording = true;
				}
				else{
					recording = false;
				}
				// Clear the chart.
				clearChart();
			}
		});
        PSwing clearButtonPSwing = new PSwing(clearButton);
        clearButtonPSwing.setOffset(
        		closePSwing.getFullBoundsReference().getMinX() - clearButtonPSwing.getFullBoundsReference().width - 10,
        		0);
        addChild(clearButtonPSwing);
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------

    /**
     * Add a data point to the graph.
     * 
     * @param time - Time in milliseconds.
     * @param voltage - Voltage in volts.
     */
    private void addDataPoint(double time, double voltage, boolean update){

    	if (dataSeries.getItemCount() == 0){
    		// This is the first data point added since the last time the
    		// chart was cleared or since it was created.  Record the time
    		// index for future reference.
    		timeIndexOfFirstDataPt = time;
    	}
    	
    	// If the chart isn't full, add the data point to the data series.
    	// Note that internally we work in millivolts, not volts.
    	assert (time - timeIndexOfFirstDataPt >= 0);
    	if (time - timeIndexOfFirstDataPt <= TIME_SPAN){
    		dataSeries.add(time - timeIndexOfFirstDataPt, voltage * 1000, update);
    	}
    }
    
    /**
     * Create the JFreeChart chart that will show the data and that will be
     * contained by this node.
     * 
     * @param title
     * @param xAxisLabel
     * @param yAxisLabel
     * @param dataset
     * @param orientation
     * @return
     */
    private static JFreeChart createXYLineChart(String title, String xAxisLabel, String yAxisLabel,
    		XYDataset dataset, PlotOrientation orientation) {

    	if (orientation == null) {
    		throw new IllegalArgumentException("Null 'orientation' argument.");
    	}

    	xAxis = new NumberAxis(xAxisLabel);
    	xAxis.setLabelFont(new PhetFont(18));
    	yAxis = new NumberAxis(yAxisLabel);
    	yAxis.setLabelFont(new PhetFont(18));

        JFreeChart chart = ChartFactory.createXYLineChart(
            title,
            xAxisLabel,
            yAxisLabel,
            dataset,
            PlotOrientation.VERTICAL,
            false, // legend
            false, // tooltips
            false  // urls
        );

        // Set the stroke for the data line to be larger than the default.
        XYPlot plot = chart.getXYPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setStroke(new BasicStroke(3f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL));

    	return chart;
    }
    
    /**
     * Update the chart based on the current time and the model that is being
     * monitored.
     * 
     * @param clockEvent
     */
    private void updateChart(ClockEvent clockEvent){
    	
    	if (recording){
    		if (!chartIsFull()){
    			updateCountdownTimer -= clockEvent.getSimulationTimeChange();
    			
    			double timeInMilliseconds = clockEvent.getSimulationTime() * 1000;
    			
    			if (updateCountdownTimer <= 0){
    				addDataPoint(timeInMilliseconds, axonModel.getMembranePotential(), true);
    				updateCountdownTimer = UPDATE_PERIOD;
    			}
    			else{
    				addDataPoint(timeInMilliseconds, axonModel.getMembranePotential(), false);
    			}
    		}
    	}
    	else{
    		// The chart is not currently recording.  Is there data?
    		if (dataSeries.getItemCount() > 0){
    			// Yes there is, so it should be cleared.
    			dataSeries.clear();
    		}
    	}
    }
    
    private boolean chartIsFull(){
    	boolean chartIsFull = false;
    	if (dataSeries.getItemCount() >= 2){
    		if (dataSeries.getDataItem(dataSeries.getItemCount() - 1).getX().doubleValue() >= TIME_SPAN){
    			chartIsFull = true;
    		}
    	}
    	return chartIsFull;
    }

    /**
     * Clear all data from the chart.
     *  
     * @param initialTime
     */
    private void clearChart(){
    	dataSeries.clear();
    }
    
	/**
	 * Test framework, do whatever is needed here to test the behavior of
	 * this node.
	 * 
	 * @param args
	 */
    public static void main(String[] args) {
    	
    	// Set up the main frame for the application.
    	Dimension2D size = new PDimension(800, 600);
		JFrame frame = new JFrame();
        frame.setSize( (int)size.getWidth(), (int)size.getHeight() );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        // Create the chart.
        final MembranePotentialChart membranePotentialChart = 
        	new MembranePotentialChart(size, "Test Chart", null);

        // Create the canvas and add the chart to it.
        PhetPCanvas phetPCanvas = new PhetPCanvas();
        phetPCanvas.addScreenChild( membranePotentialChart );
        
        // Create and add a button that will add a new data point each time
        // when it is pressed.
        JButton button = new JButton("Add Data Point");
        button.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				membranePotentialChart.addDataPoint(200, 70, true);
				membranePotentialChart.addDataPoint(300, -20, true);
			}
		});
        PSwing buttonPSwing = new PSwing(button);
        phetPCanvas.addScreenChild(buttonPSwing);
        
        // Associate the canvas and the frame and display it.
        frame.setContentPane(phetPCanvas);
        frame.setVisible(true);
	}

    public JFreeChartNode getJFreeChartNode() {
        return jFreeChartNode;
    }
    
	//----------------------------------------------------------------------------
	// Inner Classes and Interfaces
	//----------------------------------------------------------------------------
    
    private static class ChartCursor extends PPath {

    	private static final double WIDTH_PROPORTION = 0.013;
    	private static final Color FILL_COLOR = new Color( 50, 50, 200, 80 );
    	private static final Color STROKE_COLOR = Color.DARK_GRAY;
    	private static final Stroke STROKE = new BasicStroke( 1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{10.0f, 5.0f}, 0 );
    	
		public ChartCursor(JFreeChartNode jFreeChartNode) {
			
			// Set up the general appearance.
			setStroke(STROKE);
			setStrokePaint(STROKE_COLOR);
			setPaint(FILL_COLOR);
			
	        Point2D topOfPlotArea = jFreeChartNode.plotToNode( new Point2D.Double( 0, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
	        Point2D bottomOfPlotArea = jFreeChartNode.plotToNode( new Point2D.Double( 0, jFreeChartNode.getChart().getXYPlot().getRangeAxis().getRange().getLowerBound() ) );
	        
			// Set the shape.  The shape is created so that it is centered
			// around an offset of 0 in the x direction and the top edge is
			// at 0 in the y direction.
			double width = jFreeChartNode.getFullBoundsReference().width * WIDTH_PROPORTION;
			setPathTo(new Rectangle2D.Double(-width / 2, 0, width, bottomOfPlotArea.getY() - topOfPlotArea.getY()));
			
			// Set a cursor handler for this node.
			addInputEventListener(new CursorHandler());
		}
    }
}
