// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.nuclearphysics.defaults;

import edu.colorado.phet.nuclearphysics.common.NuclearPhysicsClock;

/**
 * This class contains the default settings for the Radiometric Measurement
 * module.
 *
 * @author John Blanco
 */
public class RadiometricMeasurementDefaults {

    /* Not intended for instantiation */
    private RadiometricMeasurementDefaults() {}
    
    // Clock
    public static final boolean CLOCK_RUNNING = false;
    public static final int CLOCK_FRAME_RATE = 25; // Frames per second.
    public static final double CLOCK_DT = 40; // Milliseconds per tick.
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;
    public static final NuclearPhysicsClock CLOCK = new NuclearPhysicsClock( CLOCK_FRAME_RATE, CLOCK_DT );
}
