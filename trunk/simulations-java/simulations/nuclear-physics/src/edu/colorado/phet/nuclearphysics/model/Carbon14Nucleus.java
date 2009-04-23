/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.Random;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;
import edu.colorado.phet.nuclearphysics.common.model.AbstractDecayNucleus;
import edu.colorado.phet.nuclearphysics.common.view.AtomicNucleusNode;

/**
 * This class represents a non-composite Carbon 14 nucleus.  Because it is
 * non-composite, this nucleus does not create or keep track of any 
 * constituent nucleons.
 *
 * @author John Blanco
 */
public class Carbon14Nucleus extends AbstractDecayNucleus {
    
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    // Number of neutrons and protons in the nucleus upon construction.
    public static final int ORIGINAL_NUM_PROTONS = 6;
    public static final int ORIGINAL_NUM_NEUTRONS = 8;
    
    // Half life for Carbon 14.
    public static double HALF_LIFE = 31.536E9; // 5,730 years, converted into milliseconds.
    
    // Random number generator used for calculating decay time based on decay constant.
    private static final Random RAND = new Random();
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
    
    public Carbon14Nucleus(NuclearPhysicsClock clock, Point2D position){

        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
    }
    
    public Carbon14Nucleus(NuclearPhysicsClock clock){

        this(clock, new Point2D.Double(0, 0));
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------
    
    public double getHalfLife(){
    	return HALF_LIFE;
    }
    
    //------------------------------------------------------------------------
    // Other Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any fission has
     * occurred.
     */
    public void reset(){
    	
    	super.reset();
        
        if ((_numNeutrons != ORIGINAL_NUM_NEUTRONS) || (_numProtons != ORIGINAL_NUM_PROTONS)){
            // Decay has occurred.
            _numNeutrons = ORIGINAL_NUM_NEUTRONS;
            _numProtons = ORIGINAL_NUM_PROTONS;
            
            // Notify all listeners of the change to our atomic weight.
            notifyNucleusChangeEvent(null);
        }
    }
    
    /**
     * Activate the nucleus, meaning that it will now decay after some amount
     * of time.
     */
    public void activateDecay(){
    	
    	// Only allow activation if the nucleus hasn't already decayed.
    	if (_numNeutrons == ORIGINAL_NUM_NEUTRONS){
    		_decayTime = _clock.getSimulationTime() + calcDecayTime();
    	}
    }
    
    /**
     * Return a value indicating whether or not the nucleus has decayed.
     */
    public boolean hasDecayed(){
    	
    	if (_numProtons != ORIGINAL_NUM_PROTONS){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    //------------------------------------------------------------------------
    // Private and Protected Methods
    //------------------------------------------------------------------------

    /**
     * Caculate a random decay time based on the decay constant for this
     * type of nucleus.
     * 
     * @return
     */
    private double calcDecayTime(){
        double randomValue = RAND.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double decayMilliseconds = (-(Math.log( 1 - randomValue ) / (0.693 / HALF_LIFE)));
        return decayMilliseconds;
    }
    
    /**
     * This main function is used to provide stand-alone testing of the class.
     * 
     * @param args - Unused.
     */
    public static void main(String [] args){
        Carbon14Nucleus nucleus = new Carbon14Nucleus(new NuclearPhysicsClock(24, 10));
        AtomicNucleusNode nucleusNode = new AtomicNucleusNode(nucleus);
        nucleus.setPosition(400, 300);
        
        JFrame frame = new JFrame();
        PhetPCanvas canvas = new PhetPCanvas();
        frame.setContentPane( canvas );
        canvas.addWorldChild( nucleusNode );
        frame.setSize( 800, 600 );
        frame.setVisible( true );
    }

	protected void decay(ClockEvent clockEvent) {
		
		// Decay into N14.
        _numNeutrons -= 1;
        _numProtons += 1;

        // Set the final value of the time that this nucleus existed prior to
        // decaying.
        _activatedLifetime += clockEvent.getSimulationTimeChange();
        
        // Send out the decay event to all listeners.
        notifyNucleusChangeEvent(null);
        
        // Set the decay time to 0 to indicate that decay has occurred and
        // should not occur again.
        _decayTime = 0;
	}
}
