/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.faraday.FaradayConfig;


/**
 * Rescaler is a collection of static functions for rescaling values.
 * Since the magnetic field drops off at the rate of the distance cubed,
 * the visual effect is not very useful.  These functions rescale the 
 * a value so that it is a bit more linear.
 * <p>
 * Some places where this is used include:
 * <ul>
 * <li>display of field strength by compass grid needles
 * <li>lightbulb's rays
 * <li>voltmeter reading
 * <li>electron speed in the pickup coil
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Rescaler {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /*
     * WARNING! These constants determine rescaling throughout the simulation.
     */
    
    // Values below this value are rescaled.
    private static final double RESCALE_THRESHOLD = 0.8;
    
    // Approach this rescaling exponent as value approaches 1.
    private static final double RESCALE_MAX_EXPONENT = 0.8;
    
    // Approach this rescaling exponent as value approaches 0.
    private static final double RESCALE_MIN_EXPONENT = 0.3;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /* Not intended for instantiation */
    private Rescaler() {}
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /**
     * Rescales a value to be more visually useful.
     * This method is intended to be used with values that are
     * in the range 0-1.
     * 
     * @param value a value in the range 0...1 inclusive
     * @param magnetStrength the magnet strength
     * @return rescaled value
     */
    public static double rescale( double value, double magnetStrength ) {
        return rescale( value, RESCALE_THRESHOLD, magnetStrength );
    }
    
    /**
     * Since the EMF magnitude drops off rather quickly (distance cubed),
     * we need to rescale values so that they are more visually useful.
     * This method rescales values so (a) they are a bit more linear, and 
     * (b) the extent to which they are rescaled is a function of the 
     * magnet strength. 
     * <p>
     * The algorithm used is as follows (courtesy of Mike Dubson):
     * <ul>
     * <li>Bo is some threshold value
     * <li>if B > Bo, scale = 1
     * <li>if B <= Bo, scale = (B/Bo)**N
     * <li>exponent N is between 0.3-0.8 and is a function of magnet strength
     * </ul>
     */
    public static double rescale( double value, double threshold, double magnetStrength ) {
        assert ( value >= 0 );
        assert ( threshold > 0 );
        assert ( magnetStrength > 0 );
        
        double newValue;
        if ( value > threshold ) {
            newValue = 1.0;
        }
        else {
            double min = FaradayConfig.MAGNET_STRENGTH_MIN;
            double max = FaradayConfig.MAGNET_STRENGTH_MAX;
            double exponent = RESCALE_MAX_EXPONENT - ( ( ( magnetStrength - min ) / ( max - min ) ) * ( RESCALE_MAX_EXPONENT - RESCALE_MIN_EXPONENT ) );
            newValue = Math.pow( value / threshold, exponent );
        }
        return newValue;
    }
}