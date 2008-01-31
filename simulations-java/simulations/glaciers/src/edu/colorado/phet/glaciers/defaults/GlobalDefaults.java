/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.defaults;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;


/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * 
 * NOTE! This class is package private, and values herein should only be referenced
 * by the "defaults" classes for each module.  Classes that are module-specific should
 * use the class that corresponds to their module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private! */ class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final int CLOCK_TIME_COLUMNS = 10;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( 1, 100, 1 ); // years
    
    // Valley 
    public static final double VALLEY_X_MIN = 0; // meters
    public static final double VALLEY_X_MAX = 80000; // meters
    
    // Climate
    public static final DoubleRange SNOWFALL_LAPSE_RATE_RANGE = new DoubleRange( 1E-4, 1E-3, 5E-4 ); // meters/year of accumulation per meter of elevation
    public static final DoubleRange TEMPERATURE_OFFSET_RANGE = new DoubleRange( -15, 10, 0 );  // temperature offset from "modern times" temperature
    
    //XXX ???
    public static final DoubleRange EQUILIBRIUM_LINE_ALTITUDE_RANGE = new DoubleRange( 0, 4000, 0 ); // meters
    public static final DoubleRange MASS_BALANCE_SLOPE_RANGE = new DoubleRange( 0, 1000, 0 ); //XXX units?
    public static final DoubleRange MAXIMUM_MASS_BALANCE_RANGE = new DoubleRange( 0, 1000, 0 ); //XXX units?
}
