/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.IdentityHashMap;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsConstants;
import edu.colorado.phet.nuclearphysics.view.NuclearDecayProportionChart;
import edu.umd.cs.piccolo.PNode;

/**
 * This class represents the canvas upon which the view of the model is
 * displayed for the Single Nucleus Alpha Decay tab of this simulation.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	// Initial size of the reference coordinates that are used when setting up
	// the canvas transform strategy.  These were empirically determined to
	// match the expected initial size of the canvas.
    private static final int INITIAL_INTERMEDIATE_COORD_WIDTH = 786;
    private static final int INITIAL_INTERMEDIATE_COORD_HEIGHT = 786;
    private static final Dimension INITIAL_INTERMEDIATE_DIMENSION = new Dimension( INITIAL_INTERMEDIATE_COORD_WIDTH,
    		INITIAL_INTERMEDIATE_COORD_HEIGHT );
    
    // Fraction of canvas width used for the proportions chart.
    private static final double PROPORTIONS_CHART_WIDTH_FRACTION = 0.9;
    
    // Proportions of the meter on the canvas.
    private static final double PROPORTIONS_METER_WIDTH_FRACTION = 0.23;
    private static final double PROPORTIONS_METER_HEIGHT_FRACTION = 0.3;
    
    // Resolution of the decay chart.
    private static final double NUM_SAMPLES_ON_DECAY_CHART = 250;
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private ModelViewTransform2D _mvt;
    private RadiometricMeasurementModel _model;
    private PNode _backgroundLayer;
    private NuclearDecayProportionChart _proportionsChart;
    private RadiometricDatingMeterNode _meterNode;
    private PNode _referenceNode; // For positioning other nodes.
    private IdentityHashMap<DatableItem, PNode> _mapDatableItemsToNodes = new IdentityHashMap<DatableItem, PNode>();

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public RadiometricMeasurementCanvas(RadiometricMeasurementModel radiometricMeasurementModel, 
    		ProbeTypeModel probeTypeModel) {

    	_model = radiometricMeasurementModel;

    	setWorldTransformStrategy(new PhetPCanvas.CenterWidthScaleHeight(this, INITIAL_INTERMEDIATE_DIMENSION));
        _mvt = new ModelViewTransform2D(
        		new Point2D.Double(0, 0), 
        		new Point(INITIAL_INTERMEDIATE_COORD_WIDTH / 2, 
        				(int)Math.round(INITIAL_INTERMEDIATE_COORD_HEIGHT * 0.25)),
        		20,
        		true);
        
        // Register with the meter in the model in order to know when the user
        // changes any of the meter settings, since they need to be propagated
        // to the chart.
        _model.getMeter().addListener(new RadiometricDatingMeter.Adapter(){
        	public void datingElementChanged(){
        		configureProportionsChart();
        		drawDecayCurveOnChart();
        		update();
        	};
        });
        
        // Register with the meter so that we know when the user touches or
        // stops touching something.
        _model.getMeter().addListener(new RadiometricDatingMeter.Adapter(){
        	public void touchedStateChanged(){
        		handleMeterTouchStateChanged();
        	};
        });

        // Add a reference node that will be used when positioning other nodes later.
        _referenceNode = new PNode();
        addWorldChild(_referenceNode);
        
        // Set the background color.
        setBackground( NuclearPhysicsConstants.CANVAS_BACKGROUND );

        // Create the layer where the background will be placed.
        _backgroundLayer = new PNode();
        addWorldChild(_backgroundLayer);

        // Create and add the node that represents the sky.
        // TODO
        
        // Create and add the node that represents the ground.
        // TODO
        
        // Add the nodes that represent the items on which the user can
        // perform radiometric dating.
        	
        // Create the radiometric measuring device.
        _meterNode = new RadiometricDatingMeterNode( _model.getMeter(), probeTypeModel,
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_METER_WIDTH_FRACTION, 
        		INITIAL_INTERMEDIATE_COORD_HEIGHT * PROPORTIONS_METER_HEIGHT_FRACTION,
        		_mvt );
        _meterNode.setMeterBodyOffset( 0,
        		INITIAL_INTERMEDIATE_COORD_HEIGHT - _meterNode.getFullBoundsReference().height - 4);
        setUpComboBox();
        addWorldChild( _meterNode );
        
        // Create the chart that will display relative decay proportions.
        _proportionsChart = new NuclearDecayProportionChart(false, true, false);
        configureProportionsChart();
        addWorldChild(_proportionsChart);
        
        // Set the size and position of the chart.  There are some "tweak
        // factors" in here to make things look good.
        // TODO: If I end up leaving this as a world child, I need to change this
        // resizing call to be a part of the constructor for the chart (I think).
        _proportionsChart.componentResized( new Rectangle2D.Double( 0, 0, 
        		INITIAL_INTERMEDIATE_COORD_WIDTH * PROPORTIONS_CHART_WIDTH_FRACTION,
        		_meterNode.getFullBoundsReference().height));
        _proportionsChart.setOffset(
        		_meterNode.getFullBoundsReference().getMaxX() + 4,
        		_meterNode.getOffset().getY());

        // Draw the decay curve on the chart.
        drawDecayCurveOnChart();
    }

    //Workaround to get PComboBox to show popup in the right spot.
    private void setUpComboBox() {
		_meterNode.getComboBox().setEnvironment(_meterNode.getComboBoxPSwing(), this);
	}

	//------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    private void drawDecayCurveOnChart(){
        double halfLife = _model.getMeter().getHalfLifeForDating();
    	_proportionsChart.clear();
    	double timeSpan = halfLife * 3.2;
    	double timeIncrement = timeSpan / NUM_SAMPLES_ON_DECAY_CHART;
    	double lambda = Math.log(2)/halfLife;
    	for ( double time = 0; time < timeSpan; time += timeIncrement ){
    		// Calculate the proportion of the element that should be decayed at this point in time.
    		double amountDecayed = NUM_SAMPLES_ON_DECAY_CHART - (NUM_SAMPLES_ON_DECAY_CHART * Math.exp(-time*lambda));
    		_proportionsChart.addDataPoint(time, (int)Math.round(NUM_SAMPLES_ON_DECAY_CHART - amountDecayed), 
    				(int)Math.round(amountDecayed));
    	}
    	_proportionsChart.updateMarkerText();
    }
    
    /**
     * Handle a notification from the meter that indicates that it has started
     * or stopped touching a datable item.
     */
    private void handleMeterTouchStateChanged(){
    	
    	DatableItem itemBeingTouched = _model.getMeter().getItemBeingTouched();
    	
    	if (itemBeingTouched != null){
        	// TODO
    	}
    	else {
    	}
    }
    
    private void configureProportionsChart(){
    	
        double halfLife = _model.getMeter().getHalfLifeForDating();
        _proportionsChart.setTimeParameters(halfLife * 3.2, halfLife);
        _proportionsChart.setDisplayInfoForNucleusType(_model.getMeter().getNucleusTypeUsedForDating());
    }
}