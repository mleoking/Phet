/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.view.util.FrameSetup;

import java.awt.*;

/**
 *
 */
public class IdealGasConfig {

    public static final String localizedStringsPath = "localization/IdealGasStrings";

    public static boolean fastPaint;
    public static boolean regionTest;
    public static boolean heatOnlyFromFloor;
    public static boolean jStartTest;

    // Images
    public static final String IMAGE_DIRECTORY = "images/";
    public static final String HELP_ITEM_ICON_IMAGE_FILE = IMAGE_DIRECTORY + "help-item-icon.gif";
    public static Color BACKGROUND_COLOR = Color.white;

    public String getTitle() {
        return TITLE;
    }

    public float getTimeStep() {
        return s_timeStep;
    }

    public int getWaitTime() {
        return s_waitTime;
    }

    // Physical constants
//    public static final float s_timeStep = 0.061f;
    public static final float s_timeStep = 0.1f;
    public static final int s_waitTime = 40;
    public static final double temperatureScaleFactor = 20;
    public static final double MAX_GAUGE_PRESSURE = 6.0;
//    public static final double MAX_GAUGE_PRESSURE = 3.0;
    public static final int s_maxGravity = 40;

    // Title, description and version number
    public static final String TITLE = "Ideal Gas Law";
    public static final String DESCRIPTION = "<html>A simulation for investigating"
                                             + "<br>the model of gasses.</html>";
    public static final String VERSION = "0.01";

    // Images
    public static final String BLUE_PARTICLE_IMAGE_FILE = IMAGE_DIRECTORY + "particle-blue-xsml.gif";
    public static final String RED_PARTICLE_IMAGE_FILE = IMAGE_DIRECTORY + "particle-red-xsml.gif";
    public static final String GREEN_PARTICLE_IMAGE_FILE = IMAGE_DIRECTORY + "particle-green-xsml.gif";

    public static final String PARTICLE_IMAGE_FILE = IMAGE_DIRECTORY + "molecule.gif";
    public static final String PUMP_IMAGE_FILE = IMAGE_DIRECTORY + "bicycle-pump.gif";
    public static final String HANDLE_IMAGE_FILE = IMAGE_DIRECTORY + "handle.gif";
    public static final String BOX_IMAGE_FILE = IMAGE_DIRECTORY + "box.gif";

    public static final String STOVE_IMAGE_FILE = IMAGE_DIRECTORY + "stove.gif";
    public static final String FLAMES_IMAGE_FILE = IMAGE_DIRECTORY + "flames.gif";
    public static final String ICE_IMAGE_FILE = IMAGE_DIRECTORY + "ice.gif";
    public static final String DOOR_IMAGE_FILE = IMAGE_DIRECTORY + "knob-and-door.gif";
    public static final String STOVE_AND_FLAME_ICON_FILE = IMAGE_DIRECTORY + "stove-and-flames-small.gif";
    public static final String STOVE_ICON_FILE = IMAGE_DIRECTORY + "stove-small.gif";
    public static final String STOVE_AND_ICE_ICON_FILE = IMAGE_DIRECTORY + "stove-and-ice-small.gif";
    public static final String HOT_AIR_BALLOON_FLAMES_IMAGE_FILE = IMAGE_DIRECTORY + "hot-air-balloon-flames.gif";

    public static final String THERMOMETER_IMAGE_FILE = IMAGE_DIRECTORY + "thermometer.gif";

    public static final String RULER_IMAGE_FILE = IMAGE_DIRECTORY + "10-nanometer-stick.png";
//    public static final String RULER_IMAGE_FILE = IMAGE_DIRECTORY + "meter-stick.gif";

    // Animation images
    public static final String ANIMATION_DIRECTORY = IMAGE_DIRECTORY + "animations/";
    public static final int NUM_PUSHER_ANIMATION_FRAMES = 19;
    public static final String PUSHER_ANIMATION_IMAGE_FILE_PREFIX = ANIMATION_DIRECTORY + "pusher/pusher-3";
    public static final int NUM_LEANER_ANIMATION_FRAMES = 15;
    public static final String LEANER_ANIMATION_IMAGE_FILE_PREFIX = ANIMATION_DIRECTORY + "pusher-leaning/pusher-leaning";
    public static final String PUSHER_ANIMATION_IMAGE_FILE_TYPE = "png";

    // Offset for locating objects in the apparatus panel
    public static final int X_BASE_OFFSET = 30;
    public static final int Y_BASE_OFFSET = -100;
    public static final int X_STOVE_OFFSET = 247;
    public static final int Y_STOVE_OFFSET = 545;

    // Dimensions of control panel
    public static final int CONTROL_PANEL_WIDTH = 125;

    // Frame setup for the application
    public static final FrameSetup FRAME_SETUP = new FrameSetup.CenteredWithSize( 920, 700 );

    // Colors
    public static final Color helpColor = new Color( 50, 150, 50 );

    // Sounds
    public static final String SOUND_DIRECTORY = "http://sounds/";
    public static final String BONG_SOUND_FILE = SOUND_DIRECTORY + "bond.au";
    public static final String BOING_SOUND_FILE = SOUND_DIRECTORY + "boing.au";

    // Graphic layer specs
    public static final double readoutLayer = 100;
}
