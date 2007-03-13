/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.defaults;

import java.awt.geom.Point2D;

import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.util.DoubleRange;


/**
 * PhysicsDefaults contains default settings for the "Physics of Tweezers" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsDefaults {

    /* Not intended for instantiation */
    private PhysicsDefaults() {}
    
    // Clock
    public static final int CLOCK_FRAME_RATE = 25; // fps, frames per second (wall time)
    public static final boolean CLOCK_PAUSED = false ;
    public static final DoubleRange CLOCK_DT_RANGE = new DoubleRange( 1, 10, 5, 1 );
    public static final OTClock CLOCK = new OTClock( CLOCK_FRAME_RATE, CLOCK_DT_RANGE.getDefault() );
    
    // Laser model
    public static final Point2D LASER_POSITION = new Point2D.Double( 1200, 1200 ); // nm
    public static final double LASER_ORIENTATION = Math.toRadians( -90 );
    public static final double LASER_DIAMETER = 1000; // nm
    public static final int LASER_WAVELENGTH = 632; // nm
    public static final DoubleRange LASER_POWER_RANGE = new DoubleRange( 0, 1000, 500, 0 ); // mW
    public static final boolean LASER_RUNNING = false;
    
    // Bead model
    public static final Point2D BEAD_POSITION = new Point2D.Double( 1200, 800 ); // nm
    public static final double BEAD_ORIENTATION = Math.toRadians( 0 );
    public static final double BEAD_DIAMETER = 200; // nm
    
    // Fluid model
    public static final Point2D FLUID_POSITION = new Point2D.Double( 0, 600 ); // nm
    public static final double FLUID_ORIENTATION = Math.toRadians( 0 ); // left-to-right flow direction
    public static final double FLUID_WIDTH = 1000; // nm
    public static final DoubleRange FLUID_SPEED_RANGE = new DoubleRange( 0, 100, 50, 1 ); //XXX units? range?
    public static final DoubleRange FLUID_VISCOSITY_RANGE = new DoubleRange( 0, 100, 50, 1 ); //XXX units? range?
    public static final DoubleRange FLUID_TEMPERATURE_RANGE = new DoubleRange( 0, 100, 50, 1 ); //XXX units? range?
    
    // Control panel settings
    public static final boolean ELECTRIC_FIELD_SELECTED = false;
    public static final boolean BEAD_CHARGES_SELECTED = false;
    public static final boolean ALL_BEAD_CHARGES_SELECTED = true;
    public static final boolean TRAP_FORCE_SELECTED = false;
    public static final boolean WHOLE_BEAD_SELECTED = true;
    public static final boolean FLUID_DRAG_FORCE_SELECTED = false;
    public static final boolean BROWNIAN_FORCE_SELECTED = false;
    public static final boolean RULER_SELECTED = false;
    public static final boolean POSITION_HISTOGRAM_SELECTED = false;
    public static final boolean ADVANCED_VISIBLE = false;
    public static final boolean FLUID_CONTROLS_SELECTED = false;
    public static final boolean MOMENTUM_CHANGE_MODEL_SELECTED = false;
    public static final boolean POTENTIAL_ENERGY_CHART_SELECTED = false;
}
