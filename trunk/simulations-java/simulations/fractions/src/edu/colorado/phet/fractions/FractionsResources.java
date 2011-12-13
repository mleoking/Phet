// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources (images and translated strings) for "Fractions" are loaded eagerly to make sure everything exists on sim startup, see #2967.
 * Automatically generated by edu.colorado.phet.buildtools.preprocessor.ResourceGenerator
 */
public class FractionsResources {
    public static final String NAME = "fractions";
    public static final PhetResources RESOURCES = new PhetResources( NAME );

    //Strings
    public static class Strings {

    }

    //Images
    public static class Images {
        public static final BufferedImage BALANCE_BASE = RESOURCES.getImage( "balance-base.png" );
        public static final BufferedImage BALANCE_PLATFORM = RESOURCES.getImage( "balance-platform.png" );
        public static final BufferedImage ROUND_BUTTON_DOWN = RESOURCES.getImage( "round_button_down.png" );
        public static final BufferedImage ROUND_BUTTON_DOWN_GRAY = RESOURCES.getImage( "round_button_down_gray.png" );
        public static final BufferedImage ROUND_BUTTON_DOWN_PRESSED = RESOURCES.getImage( "round_button_down_pressed.png" );
        public static final BufferedImage ROUND_BUTTON_UP = RESOURCES.getImage( "round_button_up.png" );
        public static final BufferedImage ROUND_BUTTON_UP_GRAY = RESOURCES.getImage( "round_button_up_gray.png" );
        public static final BufferedImage ROUND_BUTTON_UP_PRESSED = RESOURCES.getImage( "round_button_up_pressed.png" );
        public static final BufferedImage WATER_GLASS = RESOURCES.getImage( "water_glass.png" );
    }
}