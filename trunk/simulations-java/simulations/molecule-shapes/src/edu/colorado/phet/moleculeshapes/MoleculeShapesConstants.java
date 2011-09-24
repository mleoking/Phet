// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculeshapes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import com.jme3.math.ColorRGBA;

/**
 * Contains global constants and some dynamic global variables (like colors)
 */
public class MoleculeShapesConstants {

    /* Not intended for instantiation. */
    private MoleculeShapesConstants() {
    }

    public static final String PROJECT_NAME = "molecule-shapes";

    /*---------------------------------------------------------------------------*
    * colors
    *----------------------------------------------------------------------------*/

    public static final ColorRGBA SUN_COLOR = new ColorRGBA( 0.8f, 0.8f, 0.8f, 1f );
    public static final ColorRGBA MOON_COLOR = new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f );

    public static final Property<Color> REMOVE_BUTTON_TEXT_COLOR = new Property<Color>( Color.BLACK );
    public static final Property<Color> REMOVE_BUTTON_BACKGROUND_COLOR = new Property<Color>( Color.ORANGE );

    public static final Color MAXIMIZE_GREEN = new Color( 30, 220, 30 );
    public static final Color MINIMIZE_RED = new Color( 220, 30, 30 );

    public static final Color BOND_ANGLE_ARC_COLOR = Color.RED;
    public static final Color BOND_ANGLE_SWEEP_COLOR = Color.GRAY;
    public static final Color BOND_ANGLE_READOUT_COLOR = Color.WHITE;

    /*---------------------------------------------------------------------------*
    * fonts
    *----------------------------------------------------------------------------*/

    public static final Font CHECKBOX_FONT = new PhetFont( 14 );
    public static final Font CONTROL_PANEL_TITLE_FONT = new PhetFont( 16, true );
    public static final Font GEOMETRY_NAME_FONT = new PhetFont( 16 );
    public static final Font EXAMPLE_MOLECULAR_FORMULA_FONT = new PhetFont( 16, true );
    public static final Font REMOVE_BUTTON_FONT = new PhetFont( 15 );
    public static final Font BOND_ANGLE_READOUT_FONT = new PhetFont( 16 );

    /*---------------------------------------------------------------------------*
    * visual constants
    *----------------------------------------------------------------------------*/

    public static final int BOND_ANGLE_SAMPLES = 25; // how many segments to use on the bond-angle arcs

    public static final float MODEL_ATOM_RADIUS = 2f;
    public static final float MODEL_BOND_RADIUS = MODEL_ATOM_RADIUS / 4;

    public static final float MOLECULE_ATOM_RADIUS = 0.4f;
    public static final float MOLECULE_BOND_RADIUS = MOLECULE_ATOM_RADIUS / 4;
    public static final float MOLECULE_SCALE = 14.0f;

    /*---------------------------------------------------------------------------*
    * panel constants
    *----------------------------------------------------------------------------*/

    public static final float CONTROL_PANEL_BORDER_WIDTH = 2; // width of the control panel line border
    public static final float OUTSIDE_PADDING = 10; // padding between the outside of the sim and the control panels

    public static final double RIGHT_MIN_WIDTH = 160; // width of the inner parts of the main control panel
}
