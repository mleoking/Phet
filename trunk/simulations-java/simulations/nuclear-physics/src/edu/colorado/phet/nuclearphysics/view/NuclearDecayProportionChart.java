/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode;
import edu.colorado.phet.common.piccolophet.nodes.PieChartNode.PieValue;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.common.NucleusDisplayInfo;
import edu.colorado.phet.nuclearphysics.common.NucleusType;
import edu.colorado.phet.nuclearphysics.model.Carbon14Nucleus;
import edu.colorado.phet.nuclearphysics.model.HalfLifeInfo;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * This class represents a chart that shows the proportion of nuclei that have
 * decayed versus time.  This is essentially the exponential display curve.
 *
 * @author John Blanco
 */
public class NuclearDecayProportionChart extends PNode {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
    // Constants for controlling the appearance of the chart.
    private static final Color  BORDER_COLOR = Color.DARK_GRAY;
    private static final float  BORDER_STROKE_WIDTH = 6f;
    private static final Stroke BORDER_STROKE = new BasicStroke( BORDER_STROKE_WIDTH );

    // Constants that control the proportions of the main components of the chart.
    private static final double PIE_CHART_WIDTH_PROPORTION = 0.1;
    private static final double MOVABLE_PERCENT_INDICATOR_WIDTH_PROPORTION = 0.26;
    private static final double MOVABLE_PERCENT_INDICATOR_HEIGHT_PROPORTION = 0.23;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Time span covered by this chart, in milliseconds.
    private double _timeSpan;
    
    // Half life of primary (i.e. decaying) element.
	private double _halfLife; // Half life of decaying element, in milliseconds.

	// Information for displaying the portions of the chart that vary based on
	// the nuclei that are being represented.
	private NucleusDisplayInfo _preDecayNucleusDisplayInfo = NucleusDisplayInfo.DEFAULT_DISPLAY_INFO;
	private NucleusDisplayInfo _postDecayNucleusDisplayInfo = NucleusDisplayInfo.DEFAULT_DISPLAY_INFO;
	
	// Variables that control which of the major elements of the chart are shown.
	private boolean _pieChartEnabled;
	private boolean _showPostDecayCurve;
	private boolean _movablePercentIndicatorEnabled;

	// References to the various components of the chart.
    private PPath _borderNode;
    private ProportionsPieChartNode _pieChart;
    private GraphNode _graph;
    private MovablePercentIndicator _movablePercentIndicator;

    // Decay events that are represented on the graph.
    ArrayList _decayEvents = new ArrayList();
    
    // Parent node that will be non-pickable and will contain all of the
    // non-interactive portions of the chart.
    private PNode _nonPickableChartNode;
    
    // Parent node that will have interactive portions of the chart.
    private PNode _pickableChartNode;

    // Overall size.
    Rectangle2D _boundingRect = new Rectangle2D.Double();
    
    // Rect that is used to keep track of the overall usable area for the chart.
    Rectangle2D _usableAreaRect = new Rectangle2D.Double();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NuclearDecayProportionChart(boolean pieChartEnabled, boolean moveablePercentIndicatorEnabled, boolean showPostDecayCurve){
    	
    	_pieChartEnabled = pieChartEnabled;
    	_movablePercentIndicatorEnabled = moveablePercentIndicatorEnabled;
    	_showPostDecayCurve = showPostDecayCurve;

    	// Many of the following initializations are arbitrary, and the chart
    	// should be set up via method calls before attempting to display
    	// anything.
    	_timeSpan = 1000;
    	_halfLife = 300;
    	
        // Set up the parent node that will contain the non-interactive
        // portions of the chart.
        _nonPickableChartNode = new PNode();
        _nonPickableChartNode.setPickable( false );
        _nonPickableChartNode.setChildrenPickable( false );
        addChild( _nonPickableChartNode );
        
        // Set up the parent node that will contain the interactive portions
        // of the chart.
        _pickableChartNode = new PNode();
        _pickableChartNode.setPickable( true );
        _pickableChartNode.setChildrenPickable( true );
        addChild( _pickableChartNode );

        // Create the border for this chart.
        _borderNode = new PPath();
        _borderNode.setStroke( BORDER_STROKE );
        _borderNode.setStrokePaint( BORDER_COLOR );
        _borderNode.setPaint( NuclearPhysicsConstants.CHART_BACKGROUND_COLOR );
        _nonPickableChartNode.addChild( _borderNode );

        // Create the graph.
        _graph = new GraphNode( this );
        addChild( _graph );
        
        // Add the pie chart (if enabled).
        if ( _pieChartEnabled ){
        	_pieChart = new ProportionsPieChartNode(this);
        	_nonPickableChartNode.addChild( _pieChart );
        }
        
        // Add the movable percentage indicator (if enabled).
        if ( _movablePercentIndicatorEnabled ){
        	_movablePercentIndicator = new MovablePercentIndicator( this );
        	_pickableChartNode.addChild( _movablePercentIndicator );
        }
    }

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    /**
     * Set the time parameters for the graph.  These are set at the same time
     * to avoid problems that can emerge if the two values are at very
     * different scales the methods for layout out the chart are called.
     * 
     * @param totalTimeSpan - Total time period covered by chart.
     * @param halfLife - Half life of the element being represented.
     */
    public void setTimeParameters(double totalTimeSpan, double halfLife){
    	_timeSpan = totalTimeSpan;
		_halfLife = halfLife;
    	updateLayout();
    	
    }
    
    /**
     * Set the display information for the pre-decay element.  Note that the
     * chart will need to be redrawn in order for this change to become visible.
     * 
     */
	public void setPreDecayDisplayInfo(NucleusDisplayInfo displayInfo) {
		_preDecayNucleusDisplayInfo = displayInfo;
    	updateLayout();
	}
	
    /**
     * Set the display information for the post-decay element.  Note that the
     * chart will need to be redrawn in order for this change to become visible.
     * 
     */
	public void setPostDecayDisplayInfo(NucleusDisplayInfo displayInfo) {
		_postDecayNucleusDisplayInfo = displayInfo;
    	updateLayout();
	}
	
	/**
	 * Configure the display information based on the given nucleus type.
	 * Note that the chart must be redrawn before this change will be visible.
	 * 
	 * @param nucleusType
	 */
	public void setDisplayInfoForNucleusType(NucleusType nucleusType){
		
		_preDecayNucleusDisplayInfo = NucleusDisplayInfo.getDisplayInfoForNucleusType(nucleusType);
		_postDecayNucleusDisplayInfo = 
			NucleusDisplayInfo.getDisplayInfoForNucleusType( MultiNucleusDecayModel.getDecayProduct(nucleusType).get(0) );
		
    	clear();
    	updateLayout();
	}
	
	/**
	 * The the location within the coordinate space of the overall chart (not
	 * just the graph) where the graph originates.
	 */
	protected Point2D getGraphOriginPos(){
		
		Point2D originOffsetWithinGraph = _graph.getOriginOffset();
		
		return new Point2D.Double( originOffsetWithinGraph.getX() + _graph.getOffset().getX(),
				originOffsetWithinGraph.getY() + _graph.getOffset().getY());
	}
	
	/**
	 * Get the max x position value for the graph within the overall chart's
	 * coordinate system.
	 */
	protected double getGraphMaxX(){
		return _graph.getFullBoundsReference().getMaxX();
	}
	
	/**
	 * Get the value of the data displayed on the graph given an X position
	 * in the overall chart's coordinate frame.
	 */
	protected double getDataValueForXPixelPos(double xPixelPos){
		return _graph.getDataValueForXPixelPos(xPixelPos - _graph.getOffset().getX());
	}

	/**
	 * Get the equivalent value for the time as displayed on the graph given
	 * an X position in the overall chart's coordinate frame.
	 */
	protected double getTimeValueForXPixelPos(double xPixelPos){
		return _graph.getTimeValueForXPixelPos(xPixelPos - _graph.getOffset().getX());
	}

    /**
     * This method is called to re-scale the chart, which generally occurs
     * when the overall size of the simulation is changed.
     */
    private void updateBounds( Rectangle2D rect ) {
    	
    	if ( ( rect.getHeight() <= 0 ) || ( rect.getWidth() <= 0 ) ){
    		// This happens sometimes during initialization.  Don't know why,
    		// but we just ignore it if it does.
    		return;
    	}
    	
    	// Save the bounding rectangle.
    	_boundingRect = rect;
    	if (rect.getX() != 0 || rect.getY() != 0){
    		System.err.println(this.getClass().getName() + ": Warning - Got a non-zero offset for chart bounds.");
    	}

        // Recalculate the usable area for the chart.
        _usableAreaRect.setRect( rect.getX() + BORDER_STROKE_WIDTH, rect.getY() + BORDER_STROKE_WIDTH,
        		rect.getWidth() - ( BORDER_STROKE_WIDTH * 2 ), rect.getHeight() - ( BORDER_STROKE_WIDTH * 2 ) );

        // Redraw the chart based on these recalculated values.
        updateLayout();
    }

    /**
     * Redraw the chart based on the current state.  This is basically the
     * place where the chart gets laid out.
     */
    private void updateLayout() {
    	
    	if (_usableAreaRect.getWidth() <= 0 || _usableAreaRect.getHeight() <= 0){
    		// Ignore if the size makes no sense.
    		return;
    	}
    	
        // Set up the border for the chart.
        _borderNode.setPathTo( new RoundRectangle2D.Double( _boundingRect.getX(), _boundingRect.getY(), 
        		_boundingRect.getWidth(), _boundingRect.getHeight(), 20, 20 ) );
        
        // Position the pie chart if enabled.
        double graphLeftEdge = 0;
        if ( _pieChartEnabled ){
        	_pieChart.scale( 1 );
        	_pieChart.scale( _usableAreaRect.getWidth() * PIE_CHART_WIDTH_PROPORTION 
        			/ _pieChart.getFullBoundsReference().getWidth() );
        	
        	// Position the pie chart so that it is a little ways in from the
        	// left of the chart and vertically centered.
        	_pieChart.setOffset( BORDER_STROKE_WIDTH * 2, _usableAreaRect.getCenterY() 
        			- (_pieChart.getFullBoundsReference().getHeight() / 2));
        	
        	graphLeftEdge = _pieChart.getFullBoundsReference().getMaxX();
        	
        }
        
        // Position the graph.
        if (!_movablePercentIndicatorEnabled){
            _graph.update( (_usableAreaRect.getWidth() - graphLeftEdge) * 0.95, _usableAreaRect.getHeight() * 0.9 );
            _graph.setOffset( graphLeftEdge + 5, _usableAreaRect.getCenterY() - (_graph.getFullBoundsReference().height / 2 ) );
        }
        else{
        	// Leave room above the graph for the movable percentage indicator.
            _graph.update( (_usableAreaRect.getWidth() - graphLeftEdge) * 0.95, 
            		_usableAreaRect.getHeight() * (1 - MOVABLE_PERCENT_INDICATOR_HEIGHT_PROPORTION) );
            _graph.setOffset( graphLeftEdge + 5, 
            		_usableAreaRect.getMaxY() - _graph.getFullBoundsReference().height);
        }
        
        // Size and position the movable percentage indicator if enabled.
        // Note that its X-position is set elsewhere based on where it was
        // positioned by the user.
        if ( _movablePercentIndicatorEnabled ){
        	_movablePercentIndicator.updateLayout(
        			(int)(Math.round(_usableAreaRect.getWidth() * MOVABLE_PERCENT_INDICATOR_WIDTH_PROPORTION)),
        			(int)(Math.round(_usableAreaRect.getHeight() * MOVABLE_PERCENT_INDICATOR_HEIGHT_PROPORTION * 0.85)),
        			(int)(Math.round(_graph.getFullBoundsReference().getY())),		
        			(int)(Math.round(_graph.getOriginOffset().getY() + _graph.getOffset().getY())));
        }
    }

    /**
     * This method causes the chart to resize itself based on the (presumably
     * different) size of the overall canvas on which it appears.
     * 
     * @param rect - Position on the canvas where this chart should appear.
     */
    public void componentResized( Rectangle2D rect ) {
        updateBounds( rect );
    }

    /**
     * Clear the chart.
     */
    public void clear() {
        _decayEvents.clear();
        _graph.clearData();
        if ( _pieChart != null ){
        	_pieChart.reset();
        }
    }
    
    /**
     * Add a data point to the chart by specifying the time and the number of
     * decayed and undecayed elements.  Note that these points are connected,
     * so the should be added in chronological order or the chart will end up
     * looking weird.
     * 
     * @param time
     * @param numUndecayed
     * @param numDecayed
     */
    public void addDataPoint(double time, int numUndecayed, int numDecayed){
    	
		// Update the pie chart if it is present.
		if ( _pieChart != null ){
			_pieChart.setAmounts(numUndecayed, numDecayed);
		}
		
		// Update the graph.
    	Point2D decayEvent = new Point2D.Double( time, 100 * (double)numUndecayed/(double)(numDecayed + numUndecayed));
		_decayEvents.add( decayEvent );
		_graph.graphDecayEvent( decayEvent );
    }
    
    /**
     * This class defines a node that consists of a pie chart and the various
     * labels needed for this chart.  It assumes only two sections exist, one
     * for the amount of pre-decay element and one for the amount of post-
     * decay.
     */
    private static class ProportionsPieChartNode extends PNode {

    	// Constants that control chart appearance and behavior.
    	private static final double INITIAL_ASPECT_RATIO = 0.75;
    	private static final double INITIAL_OVERALL_WIDTH = 100;
    	private static final double INITIAL_OVERALL_HEIGHT = INITIAL_OVERALL_WIDTH / INITIAL_ASPECT_RATIO;
    	private static final double PIE_CHART_WIDTH_PROPORTION = 0.9;
    	private static final int INITIAL_PIE_CHART_WIDTH = (int)(Math.round(INITIAL_OVERALL_WIDTH * PIE_CHART_WIDTH_PROPORTION));
    	private static final Font LABEL_FONT = new PhetFont(18, true);
    	
    	// The various elements of the chart.
    	private PieChartNode _pieChartNode;
    	private NuclearDecayProportionChart _chart; // The chart upon which this node resides.
    	private HTMLNode _preDecayLabel;
    	private HTMLNode _postDecayLabel;
    	private PText _numberPreDecayRemaining;
    	private PText _numberPostDecayRemaining;
    	
    	/**
    	 * Constructor.
    	 * 
    	 * @param chart
    	 */
		public ProportionsPieChartNode(NuclearDecayProportionChart chart) {
			
			_chart = chart;
			
			// Create and add the main chart.
			PieChartNode.PieValue[] pieChartValues = new PieValue[]{
	                new PieChartNode.PieValue( 100, _chart._preDecayNucleusDisplayInfo.getDisplayColor() ),
	                new PieChartNode.PieValue( 0, _chart._postDecayNucleusDisplayInfo.getDisplayColor() )};
	        _pieChartNode = new PieChartNode( pieChartValues, new Rectangle(INITIAL_PIE_CHART_WIDTH, INITIAL_PIE_CHART_WIDTH) );
	        _pieChartNode.setOffset(INITIAL_OVERALL_WIDTH / 2 - _pieChartNode.getFullBoundsReference().width / 2,
	        		INITIAL_OVERALL_HEIGHT / 2 - _pieChartNode.getFullBounds().height / 2);
	        addChild( _pieChartNode );
	        
	        // Create and add the labels.
	        PText dummySizingNode = new PText("9999");
	        dummySizingNode.setFont(LABEL_FONT);
	        
	        _preDecayLabel = new HTMLNode();
	        _preDecayLabel.setFont(LABEL_FONT);
	        _preDecayLabel.setOffset(0, 0);
	        addChild(_preDecayLabel);
	        _postDecayLabel = new HTMLNode();
	        _postDecayLabel.setFont(LABEL_FONT);
	        _postDecayLabel.setOffset(0, INITIAL_OVERALL_HEIGHT - dummySizingNode.getFullBoundsReference().height);
	        addChild(_postDecayLabel);
	        _numberPreDecayRemaining = new PText();
	        _numberPreDecayRemaining.setFont(LABEL_FONT);
	        _numberPreDecayRemaining.setOffset(
	        		INITIAL_OVERALL_WIDTH - dummySizingNode.getFullBoundsReference().width,
	        		0);
	        addChild(_numberPreDecayRemaining);
	        _numberPostDecayRemaining = new PText();
	        _numberPostDecayRemaining.setFont(LABEL_FONT);
	        _numberPostDecayRemaining.setOffset(
	        		INITIAL_OVERALL_WIDTH - dummySizingNode.getFullBoundsReference().width,
	        		INITIAL_OVERALL_HEIGHT - dummySizingNode.getFullBoundsReference().height);
	        addChild(_numberPostDecayRemaining);
	        
	        // Reset the node.
	        reset();
		}
	
		/**
		 * The percentage of undecayed vs decayed nuclei.
		 * 
		 * @param percentageDecayed
		 */
		public void setDecayedPercentage( double percentageDecayed ){
			// Validate input.
			if ((percentageDecayed < 0) || (percentageDecayed > 100)){
				throw new IllegalArgumentException("Error: Percentage must be between 0 and 100.");
			}
			
			PieChartNode.PieValue[] pieChartValues = new PieValue[]{
	                new PieChartNode.PieValue( 100 - percentageDecayed, 
	                		_chart._preDecayNucleusDisplayInfo.getDisplayColor() ),
	                new PieChartNode.PieValue( percentageDecayed, 
	                		_chart._postDecayNucleusDisplayInfo.getDisplayColor() )
	        };
			
			_pieChartNode.setPieValues(pieChartValues);
		}
		
		public void setAmounts(int numUndecayed, int numDecayed){
			
			// Set the text on the labels.
			_numberPreDecayRemaining.setText(Integer.toString(numUndecayed));
			_numberPostDecayRemaining.setText(Integer.toString(numDecayed));

			if ((numUndecayed == 0) && (numDecayed == 0)){
				// For the special case where both are zero, set undecayed to
				// be one so that the pie chart is all one color.
				numUndecayed = 1;
			}
			
			// Set the proportions of the pie chart.
			PieChartNode.PieValue[] pieChartValues = new PieValue[]{
	                new PieChartNode.PieValue( numUndecayed, _chart._preDecayNucleusDisplayInfo.getDisplayColor() ),
	                new PieChartNode.PieValue( numDecayed, _chart._postDecayNucleusDisplayInfo.getDisplayColor() )};
			
			_pieChartNode.setPieValues(pieChartValues);
		}
		
		/**
		 * Reset this node, which will set the proportions back to 100% non-
		 * decayed and will also reset the labels.
		 */
		public void reset(){
			
			setAmounts(0, 0);
			
			// Set the colors of the labels.
			
			_preDecayLabel.setHTMLColor(_chart._preDecayNucleusDisplayInfo.getDisplayColor());
			_preDecayLabel.setHTML("<html><sup><font size=-2>" + 
					_chart._preDecayNucleusDisplayInfo.getIsotopeNumberString() + " </font></sup>" 
					+ _chart._preDecayNucleusDisplayInfo.getChemicalSymbol() + "</html>");
			_postDecayLabel.setHTMLColor(_chart._postDecayNucleusDisplayInfo.getDisplayColor());
			_postDecayLabel.setHTML("<html><sup><font size=-2>" + 
					_chart._postDecayNucleusDisplayInfo.getIsotopeNumberString() + " </font></sup>" 
					+ _chart._postDecayNucleusDisplayInfo.getChemicalSymbol() + "</html>");
		}
    }
    
    /**
     * This class represents the graph portion of this chart, meaning the
     * portion with the axes, data curves, etc.
     */
    private static class GraphNode extends PNode {

    	// Constants that control the appearance of the graph.
        private static final float  THICK_AXIS_LINE_WIDTH = 2.5f;
        private static final Stroke THICK_AXIS_STROKE = new BasicStroke( THICK_AXIS_LINE_WIDTH );
        private static final float  THIN_AXIS_LINE_WIDTH = 0.75f;
        private static final Stroke THIN_AXIS_STROKE = new BasicStroke( THIN_AXIS_LINE_WIDTH );
        private static final  Color  AXES_LINE_COLOR = Color.BLACK;
        private static final double TICK_MARK_LENGTH = 3;
        private static final float  TICK_MARK_WIDTH = 2;
        private static final Stroke TICK_MARK_STROKE = new BasicStroke( TICK_MARK_WIDTH );
        private static final Color  TICK_MARK_COLOR = AXES_LINE_COLOR;
        private static final Font   BOLD_LABEL_FONT = new PhetFont( Font.BOLD, 18 );
        private static final float  HALF_LIFE_LINE_STROKE_WIDTH = 2.0f;
        private static final Stroke HALF_LIFE_LINE_STROKE = new BasicStroke( HALF_LIFE_LINE_STROKE_WIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3.0f, 3.0f }, 0 );
        private static final Color  HALF_LIFE_LINE_COLOR = new Color (238, 0, 0);
        private static final float  DATA_CURVE_LINE_WIDTH_PROPORTION = 0.02f;

        // Constants that control other proportionate aspects of the graph.
        private static final double GRAPH_TEXT_HEIGHT_PROPORTION = 0.10;
        
        // For enabling/disabling the sizing rectangle.
        private static final boolean SIZING_RECT_VISIBLE = true;
        
        // The chart on which this graph will be appearing.
        NuclearDecayProportionChart _chart;

        // Various components that make up the graph.
    	private PPath _sizingRect;
        private ArrayList<PPath> _halfLifeLines = new ArrayList<PPath>();
        private ArrayList<PText> _halfLifeLineLabels = new ArrayList<PText>();
        private final PPath _lowerXAxisOfGraph;
        private final PText _lowerXAxisLabel;
        private final PPath _upperXAxisOfGraph;
        private final PPath _yAxisOfGraph;
        private ArrayList<PhetPPath> _xAxisTickMarks = new ArrayList<PhetPPath>();
        private ArrayList<PText> _xAxisTickMarkLabels = new ArrayList<PText>();
        private ArrayList _yAxisGridLines;
        private ArrayList<PText> _yAxisTickMarkLabels = new ArrayList<PText>();
        private final HTMLNode _yAxisLabel;
        private final PText _upperXAxisLabel;
        private final PNode _pickableGraphLayer;
        private final PNode _nonPickableGraphLayer;
        private final PNode _dataPresentationLayer;
        private PPath _preDecayProportionCurve;
        private PPath _postDecayProportionCurve;
        private Stroke _dataCurveStroke = new BasicStroke();

        // Factor for converting milliseconds to pixels.
        double _msToPixelsFactor = 1; // Arbitrary init val, updated later.

        // Rectangle that defines the size and position of the graph excluding
        // all the labels and such.
        private final Rectangle2D _graphRect = new Rectangle2D.Double();
        
        // Controls relative size of the labels.
		private double _labelScalingFactor;

		/**
		 * Constructor
		 * 
		 * @param parentChart
		 */
		public GraphNode( NuclearDecayProportionChart parentChart ) {

			_chart = parentChart;
			
			// Set up the layers (so to speak) for the graph.
			
			_nonPickableGraphLayer = new PNode();
			_nonPickableGraphLayer.setPickable(false);
			_nonPickableGraphLayer.setChildrenPickable(false);
			addChild( _nonPickableGraphLayer );
			
	        // Set up the parent node that will contain the data points.
			_dataPresentationLayer = new PNode();
			_dataPresentationLayer.setPickable( false );
			_dataPresentationLayer.setChildrenPickable( false );
	        addChild( _dataPresentationLayer );
			
			_pickableGraphLayer = new PNode();
			_pickableGraphLayer.setPickable(true);
			_pickableGraphLayer.setChildrenPickable(true);
			addChild( _pickableGraphLayer );
			
			// Create the labels at the left of the graph, since they will
			// affect the position of the origin.
			
	        _lowerXAxisOfGraph = new PPath();
	        _lowerXAxisOfGraph.setStroke( THICK_AXIS_STROKE );
	        _lowerXAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
	        _lowerXAxisOfGraph.setPaint( AXES_LINE_COLOR );
	        _nonPickableGraphLayer.addChild( _lowerXAxisOfGraph );

	        // Create the upper x axis of the graph.
	        _upperXAxisOfGraph = new PPath();
	        _upperXAxisOfGraph.setStroke( THIN_AXIS_STROKE );
	        _upperXAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
	        _upperXAxisOfGraph.setPaint( AXES_LINE_COLOR );
	        _nonPickableGraphLayer.addChild( _upperXAxisOfGraph );
	        
	        // Add the X axis label.
	        _lowerXAxisLabel = new PText();
	        _lowerXAxisLabel.setFont(BOLD_LABEL_FONT);
	        _nonPickableGraphLayer.addChild(_lowerXAxisLabel);

	        // Create the y axis of the graph.
	        _yAxisOfGraph = new PPath();
	        _yAxisOfGraph.setStroke( THIN_AXIS_STROKE );
	        _yAxisOfGraph.setStrokePaint( AXES_LINE_COLOR );
	        _yAxisOfGraph.setPaint( AXES_LINE_COLOR );
	        _nonPickableGraphLayer.addChild( _yAxisOfGraph );
	        
	        // Add the text for the Y axis tick marks.
	        PText tempText = new PText( NuclearPhysicsStrings.TWENTY_FIVE_PER_CENT );
	        tempText.setFont(BOLD_LABEL_FONT);
	        _nonPickableGraphLayer.addChild(tempText);
	        _yAxisTickMarkLabels.add(tempText);
	        tempText = new PText( NuclearPhysicsStrings.FIFTY_PER_CENT );
	        tempText.setFont(BOLD_LABEL_FONT);
	        _nonPickableGraphLayer.addChild(tempText);
	        _yAxisTickMarkLabels.add(tempText);
	        tempText = new PText( NuclearPhysicsStrings.SEVENTY_FIVE_PER_CENT );
	        tempText.setFont(BOLD_LABEL_FONT);
	        _nonPickableGraphLayer.addChild(tempText);
	        _yAxisTickMarkLabels.add(tempText);
	        tempText = new PText( NuclearPhysicsStrings.ONE_HUNDRED_PER_CENT );
	        tempText.setFont(BOLD_LABEL_FONT);
	        _nonPickableGraphLayer.addChild(tempText);
	        _yAxisTickMarkLabels.add(tempText);
	        
	        // Add the Y axis label.
	        _yAxisLabel = new HTMLNode(NuclearPhysicsStrings.DECAY_PROPORTIONS_Y_AXIS_LABEL);
	        _yAxisLabel.rotate(-Math.PI / 2);
	        _nonPickableGraphLayer.addChild(_yAxisLabel);
	        
	        // Add the label for the upper X axis.
	        _upperXAxisLabel = new PText( NuclearPhysicsStrings.HALF_LIVES_LABEL );
	        _upperXAxisLabel.setFont( BOLD_LABEL_FONT );
	        _nonPickableGraphLayer.addChild( _upperXAxisLabel );
	        
			_sizingRect = new PPath();
			_sizingRect.setStroke(THICK_AXIS_STROKE);
			_sizingRect.setStrokePaint(Color.red);
			addChild(_sizingRect);
			_sizingRect.setVisible(SIZING_RECT_VISIBLE);
		}
		
		/**
		 * Update the layout of the graph based on the supplied dimensions.
		 * 
		 * @param newWidth
		 * @param newHeight
		 */
		public void update( double newWidth, double newHeight ){
			
			double scale;
	        double graphLabelHeight = newHeight * GRAPH_TEXT_HEIGHT_PROPORTION;
	        PText dummyTextNode = new PText("Dummy");
	        dummyTextNode.setFont(BOLD_LABEL_FONT);
	        _labelScalingFactor = graphLabelHeight / dummyTextNode.getFullBoundsReference().height;
			_sizingRect.setPathTo( new Rectangle2D.Double(0, 0, newWidth, newHeight ) );

			// Set the needed vars that are based proportionately on the size
			// of the graph.
	        _dataCurveStroke = new BasicStroke( (float)(newHeight * DATA_CURVE_LINE_WIDTH_PROPORTION ),
	        		BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

	        // Position and size the Y axis label, since it will affect the
	        // graph's origin.  There is a tweak factor in here that controls
	        // the relative size of the label.
	        _yAxisLabel.setScale(1);
	        scale = ((newHeight * 0.6) / _yAxisLabel.getFullBoundsReference().getHeight());
	        _yAxisLabel.setScale(scale);

	        _yAxisLabel.setOffset(0, newHeight / 2 - _yAxisLabel.getFullBoundsReference().width / 2);
	        
	        // Set the size of Y axis tick mark labels, since they will affect
	        // the location of the graph's origin.
	        double maxYAxisLabelWidth = 0;
	        for (PText yAxisTickMarkLabel : _yAxisTickMarkLabels){
	        	yAxisTickMarkLabel.setScale(1);
		        yAxisTickMarkLabel.setScale(_labelScalingFactor);
		        maxYAxisLabelWidth = Math.max(yAxisTickMarkLabel.getFullBoundsReference().width, maxYAxisLabelWidth);
	        }

	        // Create a rectangle that defines where the graph itself is,
	        // excluding all labels and such.  There are some "fudge factors"
	        // in this calculation to account for the fact that there is often
	        // some spacing between the labels and the graphs.

	        _graphRect.setRect(
	        		_yAxisLabel.getFullBoundsReference().width + maxYAxisLabelWidth,
	        		graphLabelHeight * 1.2, 
	        		newWidth - _yAxisLabel.getFullBoundsReference().getWidth() - maxYAxisLabelWidth,
	        		newHeight - 3.2 * graphLabelHeight);
	        
	        // Reposition the Y axis label now that we know the vertical size
	        // and position of the graph.
	        _yAxisLabel.setOffset(_yAxisLabel.getOffset().getX(), 
	        		_graphRect.getCenterY() + _yAxisLabel.getFullBoundsReference().height / 2);
	        
	        // Position the y-axis tick mark labels now that we know the
	        // vertical size of the graph.  Note that this assumes that there
	        // is no label for zero and that the labels are in increasing order.
	        int yAxisTickMarkCount = 1;
	        double yAxisTickMarkSpacing = _graphRect.getHeight() / (_yAxisTickMarkLabels.size());
	        for (PText yAxisTickMarkLabel : _yAxisTickMarkLabels){
	        	yAxisTickMarkLabel.setOffset( 
	        			_yAxisLabel.getFullBoundsReference().getWidth() + maxYAxisLabelWidth 
	        				- yAxisTickMarkLabel.getFullBoundsReference().width,
	        			_graphRect.getMaxY() - yAxisTickMarkCount * yAxisTickMarkSpacing 
	        				- yAxisTickMarkLabel.getFullBoundsReference().height / 2);
	        	yAxisTickMarkCount++;
	        }

	        // Update the multiplier used for converting from pixels to
	        // milliseconds.  Use the multiplier to tweak the span of the x axis.
	        _msToPixelsFactor = _graphRect.getWidth() / _chart._timeSpan;	        

	        // Create the graph axes.
	        
			_lowerXAxisOfGraph.setPathTo( new Line2D.Double(_graphRect.getX(), _graphRect.getMaxY(), 
					_graphRect.getMaxX(), _graphRect.getMaxY() ) );
	        
	        _upperXAxisOfGraph.setPathTo( new Line2D.Double(_graphRect.getX(), _graphRect.getY(), 
	        		_graphRect.getMaxX(), _graphRect.getY() ) );
	        
	        _yAxisOfGraph.setPathTo( new Line2D.Double(_graphRect.getX(), _graphRect.getY(), 
	        		_graphRect.getX(), _graphRect.getMaxY() ) ); 

	        // Position and size labels for the upper X axis.
	        _upperXAxisLabel.setScale( 1 );
	        _upperXAxisLabel.setScale(_labelScalingFactor);
	        _upperXAxisLabel.setOffset(_graphRect.getX() + 5, _graphRect.getY() - 
	        		_upperXAxisLabel.getFullBoundsReference().height - 5 );
	        
	        // Add the X axis tick marks and labels.  Note that this won't
	        // handle all values of time span, so add more if needed.
	        updateXAxisTickMarksAndLabels();
	        
	        // Update the half life lines.
	        updateHalfLifeLines();
	        
	        // Reposition the data curve(s)
	        updateDecayCurves();
	        
	        // Position the tick marks and their labels on the X axis.
	        // TODO: Position tick marks and labels.

	        // Update the text for the Y axis labels.
	        // Set text for Y axis.
	        
	        // Position the labels for the axes.
	        // TODO: Lower X axis label
	        // TODO: Y axis label
	        // TODO: Upper axis label
		}
		
		public void clearData(){
	        _dataPresentationLayer.removeAllChildren();
	        _preDecayProportionCurve = null;
	        _postDecayProportionCurve = null;
		}
		
		/**
		 * Get the offset of the graph's origin with respect the upper left
		 * corner of the graph.  This is in screen coordinates (i.e. pixels).
		 */
		public Point2D getOriginOffset(){
			return new Point2D.Double(_graphRect.getX(), _graphRect.getMaxY());
		}
		
		/**
		 * Obtain the data value at a given X position in pixels.  Note
		 * that this is pretty unusual given the way we generally think about
		 * graphs.  The caller provides a value in pixels from the left side
		 * of the graph, and the data value at the corresponding location is
		 * returned.  If the pixel value is outside of the graph range (e.g.
		 * it is where the labels are), the closest value is still returned. 
		 */
		public double getDataValueForXPixelPos(double xPixelPos){
			
			double returnValue = 0;
			
			double time = ( xPixelPos - _graphRect.getX() ) / _msToPixelsFactor;
			double deltaTime = Double.POSITIVE_INFINITY;
			
			// Find the closest data point for a given time value.
	    	for ( Iterator it = _chart._decayEvents.iterator(); it.hasNext();){
	    		Point2D decayEvent = (Point2D)it.next();
	    		if (Math.abs(time - decayEvent.getX()) < deltaTime){
	    			deltaTime = Math.abs( time - decayEvent.getX() );
	    			returnValue = decayEvent.getY();
	    		}
	    	}

			return returnValue;
		}
		
		/**
		 * Get the time value corresponding to the given pixel position in
		 * the X dimension.  Note that the provided position information is
		 * not for just the data display portion of the graph, but for the
		 * entire graph node (including labels and such), so the value could
		 * be off the edge.  When that happens, the closest value is returned.
		 */
		public double getTimeValueForXPixelPos(double xPixelPos){
			
			double returnValue = 0;

			if (xPixelPos < _graphRect.getX()){
				returnValue = 0;
			}
			else if (xPixelPos > _graphRect.getMaxX()){
				returnValue = _graphRect.getWidth() / _msToPixelsFactor;
			}
			else{
				returnValue = ( xPixelPos - _graphRect.getX() ) / _msToPixelsFactor;
			}

			return returnValue;
		}
		
	    /**
	     * Add the vertical lines and the labels that depict the half life
	     * intervals to the chart.  This does some sanity testing to make sure
	     * that there isn't a ridiculous number of half life lines on the
	     * graph.
	     */
	    private void updateHalfLifeLines(){
	    	
	    	int numHalfLifeLines = (int)Math.floor( _chart._timeSpan / _chart._halfLife );
	    	
	    	if (numHalfLifeLines > 10){
	    		// Too many line.  Ignore this.
	    		System.err.println(this.getClass().getName() + " - Warning: Too many half life lines, ignoring request to draw them.");
	    		return;
	    	}
	    	
	    	if ( numHalfLifeLines != _halfLifeLines.size() ){
	    		// Either this is the first time through, or something has changed
	    		// that requires the half life lines to be reallocated.  First,
	    		// remove the existing lines.
	    		for ( Iterator it = _halfLifeLines.iterator(); it.hasNext(); ){
	    			PNode halfLifeLine = (PNode)it.next();
	    			_nonPickableGraphLayer.removeChild( halfLifeLine );
	    			it.remove();
	    		}
	    		
	    		// Remove the existing labels.
	    		for ( Iterator it = _halfLifeLineLabels.iterator(); it.hasNext(); ){
	    			PNode halfLifeLabel = (PNode)it.next();
	    			_nonPickableGraphLayer.removeChild( halfLifeLabel );
	    			it.remove();
	    		}
	    		
	    		// Allocate the correct number of new lines and labels.
	    		for ( int i = 0; i < numHalfLifeLines; i++ ){
	    			PPath halfLifeLine = new PPath();
	    			halfLifeLine.setStroke(HALF_LIFE_LINE_STROKE);
	    			halfLifeLine.setStrokePaint(HALF_LIFE_LINE_COLOR);
	    			_nonPickableGraphLayer.addChild( halfLifeLine );
	    			_halfLifeLines.add( halfLifeLine );
	    			PText halfLifeLabel = new PText(Integer.toString(i+1));
	    			halfLifeLabel.setFont(BOLD_LABEL_FONT);
	    			halfLifeLabel.setScale(_labelScalingFactor);
	    			_halfLifeLineLabels.add( halfLifeLabel );
	    			_nonPickableGraphLayer.addChild(halfLifeLabel);
	    		}
	    	}
	    	
	    	// Set the size and location for each of lines and labels.
			for ( int i = 0; i < _halfLifeLines.size(); i++ ){
				PPath halfLifeLine = (PPath)_halfLifeLines.get(i);
				halfLifeLine.setPathTo( new Line2D.Double(0, 0, 0, _graphRect.getHeight() ) );
				double xPos = (i + 1) * _chart._halfLife * _msToPixelsFactor + _graphRect.getX();
				halfLifeLine.setOffset( xPos, _graphRect.getY() );
				PText halfLifeLabel = (PText)_halfLifeLineLabels.get(i);
				halfLifeLabel.setOffset(xPos - halfLifeLabel.getFullBoundsReference().width/2, 
						_graphRect.getY() - halfLifeLabel.getFullBoundsReference().height);
			}
	    }
	    
	    /**
	     * Add the tick marks and labels to the X axis, which represents time.
	     * Note that this won't handle all time spans, so add more if needed.
	     */
	    private void updateXAxisTickMarksAndLabels(){
	    	
	    	// Remove the existing tick marks and labels.
	    	for (PNode tickMark : _xAxisTickMarks){
	    		_nonPickableGraphLayer.removeChild(tickMark);
	    	}
	    	for (PNode tickMarkLabel : _xAxisTickMarkLabels){
	    		_nonPickableGraphLayer.removeChild(tickMarkLabel);
	    	}
	    	_xAxisTickMarks.clear();
	    	_xAxisTickMarkLabels.clear();
	    	
	    	// Add the label for zero time.
			PText tickMarkLabel = new PText();
			tickMarkLabel.setText("0.0");  // Note: Not making this translatable since other labels aren't.
			tickMarkLabel.setFont(BOLD_LABEL_FONT);
			tickMarkLabel.setScale(_labelScalingFactor);
			tickMarkLabel.setOffset(_graphRect.getX() - tickMarkLabel.getFullBoundsReference().width / 2,
					_graphRect.getMaxY() + (tickMarkLabel.getFullBoundsReference().height * 0.1));
			_nonPickableGraphLayer.addChild(tickMarkLabel);
			_xAxisTickMarkLabels.add(tickMarkLabel);
	    	
	    	int numTickMarks = 0;
	    	if (_chart._timeSpan < MultiNucleusDecayModel.convertYearsToMs(1E9)){
	    		// Tick marks are 5000 yrs apart.  This is generally used for
	    		// the Carbon 14 range.
	    		numTickMarks = (int)(_chart._timeSpan / MultiNucleusDecayModel.convertYearsToMs(5000));
	    		
	    		for (int i = 0; i < numTickMarks; i++){
	    			addXAxisTickMark((i + 1) * MultiNucleusDecayModel.convertYearsToMs(5000),
	    					Integer.toString((i + 1) * 5000));
	    		}
	    	}
	    	else{
	    		// Space the tick marks four billion years apart.
	    		numTickMarks = (int)(_chart._timeSpan / MultiNucleusDecayModel.convertYearsToMs(4E9));
	    		
	    		for (int i = 0; i < numTickMarks; i++){
	    			addXAxisTickMark((i + 1) * MultiNucleusDecayModel.convertYearsToMs(4E9),
	    					String.format("%.1f", (float)((i + 1) * 4)));
	    		}
	    	}
	    	
	        // Position and size the label for the lower X axis.
	    	double unitsLabelYPos = _graphRect.getMaxY() + 5;
	    	if (_xAxisTickMarkLabels.size() > 0){
	    		unitsLabelYPos = _xAxisTickMarkLabels.get(0).getFullBoundsReference().getMaxY();
	    	}
	        _lowerXAxisLabel.setText(getXAxisUnitsText());
	        _lowerXAxisLabel.setFont(BOLD_LABEL_FONT);
	        _lowerXAxisLabel.setScale(1);
	        _lowerXAxisLabel.setScale(_labelScalingFactor);
	        _lowerXAxisLabel.setOffset(_graphRect.getCenterX() - _lowerXAxisLabel.getFullBoundsReference().height / 2,
	        		unitsLabelYPos);
	    }
	    
	    /**
	     * Convenience method for adding tick marks and their labels.
	     * 
	     * @param time
	     * @param label
	     */
	    private void addXAxisTickMark(double time, String label){
	    	
			PhetPPath tickMark = new PhetPPath(TICK_MARK_COLOR);
			tickMark.setPathTo(new Line2D.Double(0, 0, 0, -TICK_MARK_LENGTH));
			tickMark.setStroke(TICK_MARK_STROKE);
			tickMark.setOffset(_graphRect.getX() + (time * _msToPixelsFactor), _graphRect.getMaxY());
			_nonPickableGraphLayer.addChild(tickMark);
			_xAxisTickMarks.add(tickMark);
			PText tickMarkLabel = new PText();
			tickMarkLabel.setText(label);
			tickMarkLabel.setFont(BOLD_LABEL_FONT);
			tickMarkLabel.setScale(_labelScalingFactor);
			tickMarkLabel.setOffset(
					tickMark.getOffset().getX() - tickMarkLabel.getFullBoundsReference().width / 2,
					_graphRect.getMaxY() + (tickMarkLabel.getFullBoundsReference().height * 0.1));
			_nonPickableGraphLayer.addChild(tickMarkLabel);
			_xAxisTickMarkLabels.add(tickMarkLabel);
	    }
	    
	    private void updateDecayCurves(){
	    	
	    	// Get rid of the existing curves.
	    	_dataPresentationLayer.removeAllChildren();
	    	_preDecayProportionCurve = null;
	    	_postDecayProportionCurve = null;
	    	
	    	// Add new nodes in the correct positions.
	    	for ( Iterator it = _chart._decayEvents.iterator(); it.hasNext();){
	    		graphDecayEvent((Point2D)it.next());
	    	}
	    }
	    
	    /**
	     * Add the specified location to the graph of decay events.
	     *
	     * @param decayEventLocation - x represents time, y represents percentage
	     * of element remaining after this event.
	     */
	    private void graphDecayEvent( Point2D decayEventLocation ){
	    	
	    	float xPos = (float)(_msToPixelsFactor * decayEventLocation.getX() + _graphRect.getX());
	    	float yPosPreDecay = (float)( _graphRect.getMaxY() - ( ( decayEventLocation.getY() / 100 ) 
	    			* _graphRect.getHeight() ) );
	    	float yPosPostDecay = (float)( _graphRect.getMaxY() - ( ( ( 100 - decayEventLocation.getY() ) / 100 ) 
	    			* _graphRect.getHeight() ) );
	    	
	    	if ( _preDecayProportionCurve == null ){
	    		// Curve doesn't exist - create it.
	    		_preDecayProportionCurve = new PPath();
	    		_preDecayProportionCurve.moveTo( xPos, yPosPreDecay );
	    		_preDecayProportionCurve.setStroke( _dataCurveStroke );
	    		_preDecayProportionCurve.setStrokePaint( _chart._preDecayNucleusDisplayInfo.getDisplayColor() );
	        	_dataPresentationLayer.addChild( _preDecayProportionCurve );
	    	}
	    	else{
	    		// Add the next segment to the curve.
	    		_preDecayProportionCurve.lineTo(xPos, yPosPreDecay);
	    	}
	    	
	    	if ( _postDecayProportionCurve == null ){
	    		// Curve doesn't exist - create it.
	    		_postDecayProportionCurve = new PPath();
	    		_postDecayProportionCurve.moveTo( xPos, yPosPostDecay );
	    		_postDecayProportionCurve.setStroke( _dataCurveStroke );
	    		_postDecayProportionCurve.setStrokePaint( _chart._postDecayNucleusDisplayInfo.getDisplayColor() );
	        	_dataPresentationLayer.addChild( _postDecayProportionCurve );
	        	_postDecayProportionCurve.setVisible(_chart._showPostDecayCurve);
	    	}
	    	else{
	    		// Add the next segment to the curve.
	    		_postDecayProportionCurve.lineTo(xPos, yPosPostDecay);
	    	}
	    }
	    
	    /**
	     * Get the units string for the x axis label.  Note that this does not
	     * handle all ranges of time.  Feel free to add new ranges as needed.
	     */
	    private String getXAxisUnitsText(){
	    	
	    	String unitsText;
	    	if (_chart._timeSpan > MultiNucleusDecayModel.convertYearsToMs(100000)){
	    		unitsText = NuclearPhysicsStrings.DECAY_PROPORTIONS_TIME_UNITS_BILLION_YEARS;
	    	}
	    	else{
	    		unitsText = NuclearPhysicsStrings.DECAY_PROPORTIONS_TIME_UNITS_YEARS;
	    	}
	    	
	    	return unitsText;
	    }
    }
    
    /**
     * This class represents the movable indicator that allows the user to
     * obtain detailed information about the relationship between time and
     * percentage of the element that has decayed.
     */
    private static class MovablePercentIndicator extends PNode {
    	
    	private static final Color INDICATOR_OUTLINE_COLOR = new Color(85, 175, 205);
    	private static final Color INDICATOR_BACKGROUND_COLOR = 
    		ColorUtils.brighterColor(NuclearPhysicsConstants.CHART_BACKGROUND_COLOR, 0.5);
    	private static Stroke INDICATOR_STROKE = new BasicStroke( 3 );
    	private static final Font DEFAULT_FONT = new PhetFont(12, true);
    	
    	private PPath _readoutRect;
    	private PPath _indicatorHandle;
    	private PPath _indicatorLine;
    	private PDragEventHandler _dragEventHandler;
    	private NuclearDecayProportionChart _chart;
    	private HTMLNode _percentageText;
    	private PText _timeText;
    	
    	/**
    	 * Constructor.
    	 */
    	public MovablePercentIndicator(NuclearDecayProportionChart chart){
    		
    		_chart = chart;
    		
    		// Make sure everything is pickable so that the user can move it.
    		setPickable(true);
    		setChildrenPickable(true);
    		
    		// Create the drag event handler that will handle mouse drags by
    		// the user.
    		_dragEventHandler = new PDragEventHandler(){
                public void startDrag( PInputEvent event) {
                    super.startDrag(event);
                    handleMouseStartDragEvent( event );
                }
                
                public void drag(PInputEvent event){
                    handleMouseDragEvent( event );
                }
                
                public void endDrag( PInputEvent event ){
                    super.endDrag(event);     
                    handleMouseEndDragEvent( event );
                }
    		};
    		
    		// Create the rectangle over which the readout will appear.
    		_readoutRect = new PhetPPath( INDICATOR_BACKGROUND_COLOR, INDICATOR_STROKE, INDICATOR_OUTLINE_COLOR );
    		_readoutRect.setPickable(true);
    		_readoutRect.addInputEventListener(_dragEventHandler);

    		addChild(_readoutRect);
    		
    		// Create and add the readout text.  Note that these are set up as
    		// children of the readout rectangle.
    		_percentageText = new HTMLNode("---");
    		_percentageText.setFont(DEFAULT_FONT);
    		_percentageText.setPickable(true);
    		_readoutRect.addChild(_percentageText);
    		_timeText = new PText("---");
    		_timeText.setFont(DEFAULT_FONT);
    		_timeText.setPickable(true);
    		_readoutRect.addChild(_timeText);
    		
    		// Create and add the "handle", which is a small ellipse just
    		// below the readout.
    		_indicatorHandle = new PhetPPath( INDICATOR_OUTLINE_COLOR, INDICATOR_STROKE, INDICATOR_OUTLINE_COLOR );
    		_indicatorHandle.setPickable(true);
    		_indicatorHandle.addInputEventListener(_dragEventHandler);
    		addChild(_indicatorHandle);
    		
    		// Create and add the line that indicates the position on the chart.
    		_indicatorLine = new PhetPPath( INDICATOR_OUTLINE_COLOR, INDICATOR_STROKE, INDICATOR_OUTLINE_COLOR );
    		_indicatorLine.setPickable(true);
    		_indicatorLine.addInputEventListener(_dragEventHandler);
    		addChild(_indicatorLine);

    		// Add a cursor handler to the entire node to present a cursor to
    		// the user when they mouse over.
    		addInputEventListener(new CursorHandler(Cursor.E_RESIZE_CURSOR));
    	}
    	
    	/**
    	 * Update the layout of this node.
    	 * 
    	 * @param topRectWidth - Width of the rectangular readout portion of this node.
    	 * @param topRectHeight - Height of the rectangular readout portion of this node.
    	 * @param topOfGraphPosY - Y position of the top of the graph.  The readout sits above this and the
    	 * tail is below.
    	 * @param bottomOfGraphPosY - Y position of the bottom of the graph, which will also be the bottom
    	 * of the tail.
    	 */
    	public void updateLayout( int topRectWidth, int topRectHeight, int topOfGraphPosY, int bottomOfGraphPosY ) {
    		
    		// Size the readout rectangle, but don't position it yet.
    		_readoutRect.setPathTo(new RoundRectangle2D.Double(0, 0, topRectWidth, topRectHeight, 10, 10));
    		
    		// Resize and position the readout text.
    		updateReadoutText();
    		updateReadoutTextLayout();

    		// Set the position of the readout rectangle.
    		// TODO: Need to work out how to do initial horizontal positioning.
    		_readoutRect.setOffset(200, _chart._usableAreaRect.getX());
    		
    		// Set the size and position of the handle.
    		_indicatorHandle.setPathToEllipse(0, 0, topRectWidth / 4, topRectHeight / 5);
    		_indicatorHandle.setOffset(
    				_readoutRect.getOffset().getX() + _readoutRect.getWidth() / 2 - _indicatorHandle.getWidth() / 2, 
    				_readoutRect.getOffset().getY() + _readoutRect.getHeight());
    		
    		// Set the size and position of the indicator line.
    		_indicatorLine.setPathTo(new Line2D.Double(0, 0, 0, bottomOfGraphPosY - topOfGraphPosY));
    		_indicatorLine.setOffset(_readoutRect.getOffset().getX() + _readoutRect.getWidth() / 2, 
    				_readoutRect.getOffset().getY() + _readoutRect.getHeight());
    	}
    	
    	public void updateReadoutTextLayout(){
    		
    		// Scale the text.
    		_percentageText.setScale(1);
    		_timeText.setScale(1);
    		double scale = _readoutRect.getHeight() * 0.5 / _percentageText.getFullBoundsReference().getHeight();
			_percentageText.setScale(scale);
			_timeText.setScale(scale);
			
			// Position the text centered in the readout rectangle.
			double centerX = _readoutRect.getWidth() / 2;
			double centerY = _readoutRect.getHeight() / 2;
			_percentageText.setOffset(centerX - _percentageText.getFullBoundsReference().width / 2, 
					centerY - _percentageText.getFullBoundsReference().height);
			_timeText.setOffset(centerX - _timeText.getFullBoundsReference().width / 2, centerY);
    	}
    	
    	private void updateReadoutText(){

    		// Figure out and format the percentage information.
    		double percentage = _chart.getDataValueForXPixelPos(_readoutRect.getFullBoundsReference().getCenterX());
    		
    		String percentageString;
    		if (percentage >= 0 && percentage <= 100){
    			percentageString = String.format("%.1f", percentage) + " %";
    		}
    		else{
    			percentageString = "---";
    		}
			
    		_percentageText.setHTML("<html><sup><font size=-2>" 
    				+ _chart._preDecayNucleusDisplayInfo.getIsotopeNumberString() + " </font></sup>" 
					+ _chart._preDecayNucleusDisplayInfo.getChemicalSymbol() + "= " + percentageString + "</html>");
			
			// Figure out and format the time information.  This does
			// different things based on the scale of time, and doesn't
			// handle all time ranges.  Enhance if needed.
			double time = _chart.getTimeValueForXPixelPos( _readoutRect.getFullBoundsReference().getCenterX() );
			String timeString;
    		if ( time < MultiNucleusDecayModel.convertYearsToMs(1E6)){
    			// Use individual years.
    			timeString = NuclearPhysicsStrings.TIME_ABBREVIATION + " = " 
    				+ Integer.toString((int)MultiNucleusDecayModel.convertMsToYears(time)) 
    				+ " " + NuclearPhysicsStrings.TIME_GRAPH_UNITS_YRS;
    		}
    		else{
    			// Use billions of years.
    			timeString = NuclearPhysicsStrings.TIME_ABBREVIATION + " = "
    				+ String.format("%.1f", MultiNucleusDecayModel.convertMsToYears(time) / 1E9) 
					+ " " + NuclearPhysicsStrings.DECAY_PROPORTIONS_TIME_UNITS_BILLION_YEARS_ABBREV;
    		}
    		_timeText.setText(timeString);
    		
    		// Update the layout.
    		updateReadoutTextLayout();
    	}
    	
    	/**
    	 * Handle and event indicating that the user has dragged the mouse,
    	 * intending to move the marker.  This updates the position of the
    	 * marker and the values displayed.
    	 * 
    	 * @param event
    	 */
        private void handleMouseDragEvent(PInputEvent event){
            PNode draggedNode = event.getPickedNode();
            PDimension d = event.getDeltaRelativeTo(draggedNode);
            draggedNode.localToParent(d);
            double newXPos = _readoutRect.getOffset().getX() + d.width;
            double newCenterXPos = newXPos + ( _readoutRect.getFullBoundsReference().width / 2 );
            if (newCenterXPos < _chart.getGraphOriginPos().getX()){
            	// Limit the position from going too far to the left.
            	newXPos = _chart.getGraphOriginPos().getX() - ( _readoutRect.getFullBoundsReference().width / 2 );
            }
            else if (newCenterXPos > _chart.getGraphMaxX()){
            	// Limit the position from going too far to the right.
            	newXPos = _chart.getGraphMaxX() - ( _readoutRect.getFullBoundsReference().width / 2 );
            }
            _readoutRect.setOffset(newXPos, _readoutRect.getOffset().getY());
            updateReadoutText();
            _indicatorHandle.setOffset(newXPos + _readoutRect.getWidth() / 2 - _indicatorHandle.getWidth() / 2,
            		_indicatorHandle.getOffset().getY());
    		_indicatorLine.setOffset(newXPos + _readoutRect.getWidth() / 2 - _indicatorLine.getWidth() / 2,
    				_indicatorHandle.getOffset().getY());

        }
        
        private void handleMouseStartDragEvent(PInputEvent event){
            // TODO: Is this needed for anything?
        }

        private void handleMouseEndDragEvent(PInputEvent event){
            // TODO: Is this needed for anything?
        }
    }

    /**
     * Main routine for stand alone testing.
     * 
     * @param args
     */
    public static void main(String [] args){
    	
        final NuclearDecayProportionChart proportionsChart = new NuclearDecayProportionChart(true, true, false); 

        double halfLife = HalfLifeInfo.getHalfLifeForNucleusType(NucleusType.CARBON_14);
        proportionsChart.setTimeParameters(halfLife * 3.2, halfLife);
        proportionsChart.setDisplayInfoForNucleusType(NucleusType.CARBON_14);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addScreenChild( proportionsChart );
        frame.setSize( 1000, 600 );
        proportionsChart.componentResized(new Rectangle2D.Double(0, 0, 800, 400));
        proportionsChart.setOffset(100, 100);
        frame.setVisible( true );
        
        for (int i = 0; i < 5; i++){
        	// Put some data points on the chart to test its behavior.
        	proportionsChart.addDataPoint((Carbon14Nucleus.HALF_LIFE / 10) * i, i, 5 - i);
        }
        
        canvas.addComponentListener( new ComponentListener(){

			public void componentHidden(ComponentEvent e) {
			}

			public void componentMoved(ComponentEvent e) {
			}

			public void componentResized(ComponentEvent e) {
				proportionsChart.componentResized(e.getComponent().getBounds());
			}

			public void componentShown(ComponentEvent e) {
			}
        	
        });
    }
}
