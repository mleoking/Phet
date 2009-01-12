/* Copyright 2007, University of Colorado */

package edu.colorado.phet.acidbasesolutions.defaults;



/**
 * GlobalDefaults contains default settings that are common to 2 or more modules.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlobalDefaults {

    /* Not intended for instantiation */
    private GlobalDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = true;
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final double CLOCK_DT = 1;
    public static final int CLOCK_TIME_COLUMNS = 10;
}
