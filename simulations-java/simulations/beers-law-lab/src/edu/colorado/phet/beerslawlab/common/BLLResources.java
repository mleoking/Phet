// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.common;

import java.awt.Image;

import edu.colorado.phet.chemistry.utils.ChemUtils;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Resources for this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLResources {

    public static final String PROJECT_NAME = "beers-law-lab";
    private static final PhetResources RESOURCES = new PhetResources( PROJECT_NAME );

    // Localized strings
    public static class Strings {
        public static final String ABSORBANCE = RESOURCES.getLocalizedString( "absorbance" );
        public static final String BEAM = RESOURCES.getLocalizedString( "beam" );
        public static final String COBALT_II_NITRATE = RESOURCES.getLocalizedString( "cobaltIINitrate" );
        public static final String COBALT_CHLORIDE = RESOURCES.getLocalizedString( "cobaltChloride" );
        public static final String CONCENTRATION = RESOURCES.getLocalizedString( "concentration" );
        public static final String COPPER_SULFATE = RESOURCES.getLocalizedString( "copperSulfate" );
        public static final String EVAPORATION = RESOURCES.getLocalizedString( "evaporation" );
        public static final String KOOL_AID = RESOURCES.getLocalizedString( "koolAid" );
        public static final String LAMBDA_MAX = RESOURCES.getLocalizedString( "lambdaMax" );
        public static final String LIGHT_VIEW = RESOURCES.getLocalizedString( "lightView" );
        public static final String LOTS = RESOURCES.getLocalizedString( "lots" );
        public static final String NICKEL_II_CHLORIDE = RESOURCES.getLocalizedString( "nickelIIChloride" );
        public static final String NONE = RESOURCES.getLocalizedString( "none" );
        public static final String PATTERN_0LABEL = RESOURCES.getLocalizedString( "pattern.0label" );
        public static final String PATTERN_0VALUE_1UNITS = RESOURCES.getLocalizedString( "pattern.0value.1units" );
        public static final String PATTERN_PARENTHESES_0TEXT = RESOURCES.getLocalizedString( "pattern.parentheses.0text" );
        public static final String PERCENT_TRANSMITTANCE = RESOURCES.getLocalizedString( "percentTransmittance" );
        public static final String PHOTONS = RESOURCES.getLocalizedString( "photons" );
        public static final String POTASSIUM_CHROMATE = RESOURCES.getLocalizedString( "potassiumChromate" );
        public static final String POTASSIUM_DICHROMATE = RESOURCES.getLocalizedString( "potassiumDichromate" );
        public static final String POTASSIUM_PERMANGANATE = RESOURCES.getLocalizedString( "potassiumPermanganate" );
        public static final String REMOVE_SOLUTE = RESOURCES.getLocalizedString( "removeSolute" );
        public static final String SATURATED = RESOURCES.getLocalizedString( "saturated" );
        public static final String SOLID = RESOURCES.getLocalizedString( "solid" );
        public static final String SOLUTE = RESOURCES.getLocalizedString( "solute" );
        public static final String SOLUTION = RESOURCES.getLocalizedString( "solution" );
        public static final String TAB_BEERS_LAW = RESOURCES.getLocalizedString( "tab.beersLaw" );
        public static final String TAB_CONCENTRATION = RESOURCES.getLocalizedString( "tab.concentration" );
        public static final String UNITS_LITERS = RESOURCES.getLocalizedString( "units.liters" );
        public static final String UNITS_MOLES_PER_LITER = RESOURCES.getLocalizedString( "units.molesPerLiter" );
        public static final String UNITS_NANOMETERS = RESOURCES.getLocalizedString( "units.nanometers" );
        public static final String VARIABLE = RESOURCES.getLocalizedString( "variable" );
        public static final String WATER = RESOURCES.getLocalizedString( "water" );
        public static final String WAVELENGTH = RESOURCES.getLocalizedString( "wavelength" );
    }

    // Images
    public static class Images {
        public static final Image CONCENTRATION_METER_BODY_LEFT = RESOURCES.getImage( "concentration-meter-body-left.png" );
        public static final Image CONCENTRATION_METER_BODY_CENTER = RESOURCES.getImage( "concentration-meter-body-center.png" );
        public static final Image CONCENTRATION_METER_BODY_RIGHT = RESOURCES.getImage( "concentration-meter-body-right.png" );
        public static final Image CONCENTRATION_METER_PROBE = RESOURCES.getImage( "concentration-meter-probe.png" );
        public static final Image DROPPER_FOREGROUND = RESOURCES.getImage( "dropper_foreground.png" );
        public static final Image DROPPER_BACKGROUND = RESOURCES.getImage( "dropper_background.png" );
        public static final Image DROPPER_ICON = RESOURCES.getImage( "dropper-icon.png" );
        public static final Image LIGHT = RESOURCES.getImage( "light.png" );
        public static final Image SHAKER = RESOURCES.getImage( "shaker.png" );
        public static final Image SHAKER_ICON = RESOURCES.getImage( "shaker-icon.png" );
    }
}
