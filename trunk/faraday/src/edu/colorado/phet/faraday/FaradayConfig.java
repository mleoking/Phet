/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;


/**
 * FaradayConfig contains global configuration values for the Color Vision
 * application. See FaradayStrings.properties for localized Strings that are
 * visible to the user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayConfig {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Resource bundles
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/FaradayStrings";

    // Clock constants
    public static final double TIME_STEP = 1;
    public static final int FRAME_RATE = 25;  // frame per second
    public static final int WAIT_TIME = ( 1000 / FRAME_RATE );  // milliseconds

    // Layers
    public static final double DEBUG_LAYER = Double.MAX_VALUE - 1;
    public static final double HELP_LAYER = Double.MAX_VALUE;
    
    // Images
    private static final String IMAGES_DIRECTORY = "images/";
    public static final String BAR_MAGNET_IMAGE = IMAGES_DIRECTORY + "barMagnet.png";
    public static final String BATTERY_IMAGE = IMAGES_DIRECTORY + "battery.png";
    public static final String COMPASS_IMAGE = IMAGES_DIRECTORY + "compass.png";
    public static final String ELECTRON_IMAGE = IMAGES_DIRECTORY + "electron.png";
    public static final String FIELD_METER_IMAGE = IMAGES_DIRECTORY + "fieldMeter.png";
    public static final String LIGHTBULB_IMAGE = IMAGES_DIRECTORY + "lightbulb.png";
    public static final String VOLTMETER_IMAGE = IMAGES_DIRECTORY + "voltmeter.png";

    // Dimensions
    public static final int APP_FRAME_WIDTH = 1024;
    public static final int APP_FRAME_HEIGHT = 768;
    public static final int CONTROL_PANEL_MIN_WIDTH = 225;

    // Colors
    public static final Color LABEL_COLOR = Color.BLACK;

    // Fonts
    public static final Font LABEL_FONT = new Font( "SansSerif", Font.PLAIN, 18 );
    
    // EMF parameters
    public static final double MAX_EMF = 4.0E7; // volts
    
    // Bar Magnet parameters
    public static final double BAR_MAGNET_STRENGTH_MAX = 300.0; // Gauss
    public static final double BAR_MAGNET_STRENGTH_MIN = 0.5 * BAR_MAGNET_STRENGTH_MAX; // Gauss
    public static final double BAR_MAGNET_ASPECT_RATIO = 5.0/1.0;
    public static final int BAR_MAGNET_WIDTH_MAX = 350;
    public static final int BAR_MAGNET_WIDTH_MIN = 150;
    private static final int BAR_MAGNET_WIDTH = 250;
    public static final Dimension BAR_MAGNET_SIZE = new Dimension( BAR_MAGNET_WIDTH, (int) ( BAR_MAGNET_WIDTH / BAR_MAGNET_ASPECT_RATIO ) );

    // Electromagnet parameters
    public static final double ELECTROMAGNET_STRENGTH_MAX = 300.0; // Gauss
    public static final double ELECTROMAGNET_STRENGTH_MIN = 0.0;  // must be zero !!!
    public static final int ELECTROMAGNET_LOOPS_MAX = 4;
    public static final int ELECTROMAGNET_LOOPS_MIN = 1;
    
    // Compass Grid parameters
    public static final int GRID_SPACING_MAX = 100;
    public static final int GRID_SPACING_MIN = 35;
    public static final int GRID_SPACING = 40;
    public static final double GRID_NEEDLE_ASPECT_RATIO = 25.0/7.0;
    public static final int GRID_NEEDLE_WIDTH_MAX = 60;
    public static final int GRID_NEEDLE_WIDTH_MIN = 20;
    private static final int GRID_NEEDLE_WIDTH = 25;
    public static final Dimension GRID_NEEDLE_SIZE = new Dimension( GRID_NEEDLE_WIDTH, (int) ( GRID_NEEDLE_WIDTH / GRID_NEEDLE_ASPECT_RATIO ) );
    
    // Pickup Coil parameters
    public static final int MAX_PICKUP_LOOPS = 3;
    public static final int MIN_PICKUP_LOOPS = 1;
    public static final double MAX_PICKUP_RADIUS = 125.0;
    public static final double MIN_PICKUP_RADIUS = 0.5 * MAX_PICKUP_RADIUS;

    // Battery parameters 
    public static final double BATTERY_VOLTAGE_MAX = 100.0;

    // AC parameters
    public static final double AC_AMPLITUDE_MAX = 100.0;
    public static final double AC_FREQUENCY_MAX = 100.0;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * This class is not intended for instantiation.
     */
    private FaradayConfig() {}
}