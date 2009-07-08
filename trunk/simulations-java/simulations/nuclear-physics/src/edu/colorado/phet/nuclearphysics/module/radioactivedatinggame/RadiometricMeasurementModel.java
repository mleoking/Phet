/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * This class defines a model (in the model-view-controller paradigm) that
 * includes one or more animated datable items as well as a meter than can be
 * used to date the items.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementModel implements ModelContainingDatableItems {

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
	
	// Enumeration that defines the main modes for this model.
	public enum SIMULATION_MODE { TREE, ROCK };
	private SIMULATION_MODE DEFAULT_MODE = SIMULATION_MODE.TREE;
	
	// Constants that control the initial size and position of some of the
	// model elements.
	private static final Point2D INITIAL_TREE_POSITION = new Point2D.Double(-12, -2);
	private static final double  INITIAL_TREE_WIDTH = 7;
	private static final Point2D INITIAL_VOLCANO_POSITION = new Point2D.Double(13, 0);
	private static final double  INITIAL_VOLCANO_WIDTH = 12;
	private static final Point2D INITIAL_AGING_ROCK_POSITION = new Point2D.Double(13, 3.5);
	private static final double  INITIAL_AGING_ROCK_WIDTH = 1;
	private static final Point2D INITIAL_PROBE_TIP_POSITION = new Point2D.Double(0, 10);
	
    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

	private RadiometricDatingMeter _meter;
	private SIMULATION_MODE _simulationMode;
	private ConstantDtClock _clock;
	private ArrayList<Listener> _listeners = new ArrayList<Listener>();
	private ArrayList<AnimatedDatableItem> _animatedModelElements = new ArrayList<AnimatedDatableItem>();
	private boolean _agingRockAdded = false;
    private AnimatedDatableItem.ClosureListener _closureListener;
    private RadiometricClosureState _closureState = RadiometricClosureState.CLOSURE_NOT_POSSIBLE;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public RadiometricMeasurementModel(NuclearPhysicsClock clock)
    {
    	_clock = clock;
    	
    	// Register for clock ticks.
    	_clock.addClockListener(new ClockAdapter(){
    	    public void clockTicked( ClockEvent clockEvent ) {
    	    	handleClockTicked();
    	    }
    	});
    	
    	// Add the meter and register for user-initiated movements.
    	_meter = new RadiometricDatingMeter( this, INITIAL_PROBE_TIP_POSITION );
    	
    	_meter.getProbeModel().addObserver(new SimpleObserver(){
			public void update() {
				getDatableItemAtLocation( _meter.getProbeModel().getTipLocation() );
			}
    	});
    	
        // Create a listener for handling notifications of radiometric closure
    	// from the individual datable items.
        _closureListener = new AnimatedDatableItem.ClosureListener(){
			public void closureStateChanged(AnimatedDatableItem datableItem) {
				handleClosureStateChanged(datableItem);
			}
        };
        
    	// Set the initial simulation mode, which will add the initial
    	// model element(s).
    	setSimulationMode(DEFAULT_MODE);
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
    protected void handleClosureStateChanged(AnimatedDatableItem datableItem) {
		// TODO Does no validation.  Probably needs to do that at some point.
    	if (_closureState != datableItem.getClosureState()){
    		_closureState = datableItem.getClosureState();
    		notifyClosureStateChanged();
    	}
	}

	public RadiometricDatingMeter getMeter(){
    	return _meter;
    }

    /**
     * Get a collection of all model elements that currently exist in the
     * simulation.
     */
    public ArrayList getModelElements(){
    	return new ArrayList(_animatedModelElements);
    }
    
    /**
     * Get the Y location of the ground.  Note that the ground is assumed to
     * extend infinitely in the positive and negative X directions.
     */
    public double getGroundLevelY(){
    	// At least for now, the ground is always assumed to be at Y location
    	// zero.
    	return 0;
    }
    
    /**
     * Get the mode value that indicates what is being simulated.
     */
    public SIMULATION_MODE getSimulationMode() {
		return _simulationMode;
	}

    /**
     * Set the mode (e.g. "TREE") that defines what the model will simulate.
     * 
     * @param mode
     */
	public void setSimulationMode(SIMULATION_MODE mode) {
		if ( _simulationMode != mode){
			_simulationMode = mode;
			
			// Stop and reset the clock.
			_clock.stop();
			_clock.resetSimulationTime();
			
			// Reset state.
			_agingRockAdded = false;
			_closureState = RadiometricClosureState.CLOSURE_NOT_POSSIBLE;
			
			// Remove all existing model elements.
			Iterator<AnimatedDatableItem> itr = _animatedModelElements.iterator();
			while (itr.hasNext()) {
				AnimatedDatableItem datableItem = itr.next();
				datableItem.removeClosureListener(_closureListener);
				itr.remove();
				notifyModelElementRemoved();
			}
			
			// Add the appropriate model elements based on the simulation mode.
			switch( _simulationMode ){
			case TREE:
				// No model elements added until the tree is planted.
				break;
			case ROCK:
				_animatedModelElements.add(new EruptingVolcano(_clock, INITIAL_VOLCANO_POSITION, INITIAL_VOLCANO_WIDTH));
				notifyModelElementAdded();
				break;
			}
			
			notifySimulationModeChanged();
		}
	}
	
	/**
	 * Start simulating based on the currently selected mode.  This is ignored
	 * if a simulation is already in progress.
	 */
	public void startOperation(){
		
		if ( _clock.getSimulationTime() != 0 ){
			// The simulation has already been started, so print an error and
			// exit.
			System.err.println(getClass().getName() + " - Warning: Command received to start operation when already started.");
			return;
		}
		
		switch (_simulationMode){
		case TREE:
			plantTree();
			break;
		case ROCK:
			break;
		}
		
		// Start the clock, which the model elements will listen to and do
		// their thing.
		_clock.start();
	}
	
	/**
	 * Reset the state of the current operation.  For instance, if a growing
	 * tree is being simulated, go back to the state prior to the planting of
	 * the tree.
	 */
	public void resetOperation(){
		
		// This is a little bit of a tricky way to do this, but it gets the
		// job done and avoids duplicated code.
		SIMULATION_MODE simMode = _simulationMode;
		_simulationMode = null;
		setSimulationMode(simMode);
	}
	
	private void handleClockTicked(){
		if (_simulationMode == SIMULATION_MODE.ROCK){
			// In this mode, additional model elements are added at various times.
			if (_clock.getSimulationTime() > 1000 && !_agingRockAdded){
				// Add the aging rock to the sim.
				AgingRock agingRock = new AgingRock(_clock, INITIAL_AGING_ROCK_POSITION, INITIAL_AGING_ROCK_WIDTH);
				_animatedModelElements.add(agingRock);
				agingRock.addClosureListener(_closureListener);
				notifyModelElementAdded();
				_agingRockAdded = true;
			}
		}
	}
	
	/**
	 * Start simulating the life of a tree be creating a tree and adding it
	 * to the model.
	 */
	private void plantTree(){
		// At the time of this writing, no other animated model elements
		// should exist when the tree is planted.
		assert _animatedModelElements.size() == 0;
		
		// Add the tree.
		AgingTree agingTree = new AgingTree(_clock, INITIAL_TREE_POSITION, INITIAL_TREE_WIDTH);
		_animatedModelElements.add(agingTree);
		
		// Register for closure notifications.
		agingTree.addClosureListener(_closureListener);
		
		// Let everyone know about the new tree!
		notifyModelElementAdded();
	}
	
	/**
	 * Get the current radiometric closure state, which indicates whether the
	 * primary object has begun aging (in a radiometric sense).
	 */
	public RadiometricClosureState getRadiometricClosureState(){
		return _closureState;
	}
	
	/**
	 * Force radiometric closure to occur, which should cause the radiometric
	 * elements in the datable item(s) to begin decaying.
	 */
	public void forceClosure(){
		System.err.println("Fill me in!!!");
		
	}
	
	public ConstantDtClock getClock() {
		return _clock;
	}
	
	public void setClock(ConstantDtClock clock) {
		_clock = clock;
	}
	
    //------------------------------------------------------------------------
    // Other Methods
    //------------------------------------------------------------------------

    public void addListener(Listener listener){
        if (!_listeners.contains( listener ))
        {
        	_listeners.add( listener );
        }
    }
    
    public boolean removeListener(Listener listener){
    	return _listeners.remove(listener);
    }
    
	/**
     * Get the datable item at the specified model location, or null if there
     * isn't anything there.
     */
    public DatableItem getDatableItemAtLocation( Point2D probeLocation ){

    	DatableItem datableItem = null;
    	
    	for ( AnimatedModelElement modelElement : _animatedModelElements ){
    		if ( modelElement instanceof DatableItem ){
    			if ( ((DatableItem)modelElement).contains(probeLocation) ){
    				datableItem = (DatableItem)modelElement;
    			}
    		}
    	}
    	
    	return datableItem;
    }
    
    protected void notifySimulationModeChanged() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).simulationModeChanged();
        }
    }
    
    protected void notifyModelElementAdded() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).modelElementAdded();
        }        
    }

    protected void notifyModelElementRemoved() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).modelElementRemoved();
        }
    }
    
    protected void notifyClosureStateChanged() {
        for (int i = 0; i < _listeners.size(); i++){
            _listeners.get( i ).closureStateChanged();
        }
    }
    
    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------

    public static interface Listener {
    	public void simulationModeChanged();
    	public void modelElementAdded();
    	public void modelElementRemoved();
    	public void closureStateChanged();
    }
    
    public static class Adapter implements Listener {
		public void simulationModeChanged() {}
		public void modelElementAdded() {}
		public void modelElementRemoved() {}
		public void closureStateChanged() {}
    }
}
