/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;

/**
 * This class defines the behavior of the nucleus that is used to demonstrate
 * alpha radiation behavior.
 *
 * @author John Blanco
 */
public class AlphaRadiationNucleus extends AtomicNucleus {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Number of neutrons and protons in the nucleus upon construction.  The
    // values below are for Polonium-211.
    public static final int ORIGINAL_NUM_PROTONS = 84;
    public static final int ORIGINAL_NUM_NEUTRONS = 127;

    // The "agitation factor" for the various types of nucleus.  The amount of
    // agitation controls how dynamic the nucleus looks on the canvas.  Values
    // must be in the range 0-9.
    private static final int POLONIUM_211_AGITATION_FACTOR = 8;
    private static final int LEAD_207_AGITATION_FACTOR = 3;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------

    // Variable for deciding when alpha decay should occur.
    private double _alphaDecayTime = 0;

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public AlphaRadiationNucleus(NuclearPhysics2Clock clock, Point2D position){
        super(clock, position, ORIGINAL_NUM_PROTONS, ORIGINAL_NUM_NEUTRONS);
        
        // Decide when alpha decay will occur.
        _alphaDecayTime = calcPolonium211DecayTime();
    }
    
    //------------------------------------------------------------------------
    // Public Methods
    //------------------------------------------------------------------------
    
    /**
     * Resets the nucleus to its original state, before any alpha decay has
     * occurred.
     * 
     * @param alpha - Particle that had previously tunneled out of this nucleus.
     */
    public void reset(AlphaParticle alpha){
        
        // Reset the decay time.
        _alphaDecayTime = calcPolonium211DecayTime();
        
        if (alpha != null){
            // Add the tunneled particle back to our list.
            _constituents.add( 0, alpha );
            _numAlphas++;
            alpha.resetTunneling();

            // Update our agitation level.
            updateAgitationFactor();
            
            // Let the listeners know that the atomic weight has changed.
            int totalNumProtons = _numProtons + _numAlphas * 2;
            int totalNumNeutrons= _numNeutrons + _numAlphas * 2;
            for (int i = 0; i < _listeners.size(); i++){
                ((Listener)_listeners.get( i )).atomicWeightChanged( totalNumProtons, totalNumNeutrons,  null);
            }
            
            // If the original numbers don't match the current one, some bug
            // exists that should be resolved.
            assert totalNumNeutrons == ORIGINAL_NUM_NEUTRONS;
            assert totalNumProtons == ORIGINAL_NUM_PROTONS;
        }
    }

    //------------------------------------------------------------------------
    // Private Methods
    //------------------------------------------------------------------------
    
    /**
     * This method lets this model element know that the clock has ticked.  In
     * response, the nucleus generally 'agitates' a bit, may also perform some
     * sort of decay, and may move.
     */
    protected void handleClockTicked(ClockEvent clockEvent)
    {
        super.handleClockTicked( clockEvent );
        
        // See if alpha decay should occur.
        if ((_alphaDecayTime != 0) && (clockEvent.getSimulationTime() >= _alphaDecayTime ))
        {
            // Pick an alpha particle to tunnel out and make it happen.
            for (int i = 0; i < _constituents.size(); i++)
            {
                if (_constituents.get( i ) instanceof AlphaParticle){
                    
                    // This one will do.  Make it tunnel.
                    AlphaParticle tunnelingParticle = (AlphaParticle)_constituents.get( i );
                    _constituents.remove( i );
                    _numAlphas--;
                    tunnelingParticle.tunnelOut( _position, TUNNEL_OUT_RADIUS + 1.0 );
                    
                    // Update our agitation factor.
                    updateAgitationFactor();
                    
                    // Notify listeners of the change of atomic weight.
                    int totalNumProtons = _numProtons + _numAlphas * 2;
                    int totalNumNeutrons= _numNeutrons + _numAlphas * 2;
                    ArrayList byProducts = new ArrayList(1);
                    byProducts.add( tunnelingParticle );
                    for (int j = 0; j < _listeners.size(); j++){
                        ((Listener)_listeners.get( j )).atomicWeightChanged( totalNumProtons, totalNumNeutrons, 
                                byProducts );
                    }
                    break;
                }
            }
            
            // Set the decay time to 0 to indicate that no more tunneling out
            // should occur.
            _alphaDecayTime = 0;
        }
    }
    
    @Override
    protected void updateAgitationFactor() {
        // Determine the amount of agitation that should be exhibited by this
        // particular nucleus.  This obviously doesn't handle every possible
        // nucleus, so add more if and when they are needed.
        
        int _totalNumProtons = _numProtons + (_numAlphas * 2);
        int _totalNumNeutrons = _numNeutrons + (_numAlphas * 2);
        
        switch (_totalNumProtons){
        
        case 84:
            // Polonium.
            if (_totalNumNeutrons == 127){
                // Polonium 211.
                _agitationFactor = POLONIUM_211_AGITATION_FACTOR;
            }
            break;
            
        case 82:
            // Lead
            if (_totalNumNeutrons == 125){
                // Lead 207
                _agitationFactor = LEAD_207_AGITATION_FACTOR;
            }
            break;

        }        
    }
    
    /**
     * This method generates a value indicating the number of milliseconds for
     *  a Polonium 211 nucleus to decay.  This calculation is based on the 
     * exponential decay formula and uses the decay constant for Polonium 211.
     * 
     * @return
     */
    private double calcPolonium211DecayTime(){
        double randomValue = _rand.nextDouble();
        if (randomValue > 0.999){
            // Limit the maximum time for decay so that the user isn't waiting
            // around forever.
            randomValue = 0.999;
        }
        double tunnelOutMilliseconds = (-(Math.log( 1 - randomValue ) / 1.343)) * 1000;
        System.out.println("randomValue = " + randomValue + ", tunnelOutMilliseconds = " + tunnelOutMilliseconds);
        return tunnelOutMilliseconds;
    }
}
