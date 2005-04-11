/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

/**
 * FluorescentLightsConfig
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FluorescentLightsConfig {

    // Object locations and dimensions
    public static final Point CIRCUIT_ULC = new Point( 50, 100 );
    public static final int ELECTRODE_Y_LOCATION = 450;
    public static final Point CATHODE_LOCATION = new Point( 170, ELECTRODE_Y_LOCATION );
    public static final double CATHODE_LENGTH = 300;
    public static final Line2D CATHODE_LINE = new Line2D.Double( CATHODE_LOCATION.getX(),
                                                                 CATHODE_LOCATION.getY() - CATHODE_LENGTH / 2,
                                                                 CATHODE_LOCATION.getX(),
                                                                 CATHODE_LOCATION.getY() + CATHODE_LENGTH / 2 );

    public static final Point ANODE_LOCATION = new Point( 710, ELECTRODE_Y_LOCATION );
    public static final double ANODE_LENGTH = 300;
    public static final Line2D ANODE_LINE = new Line2D.Double( ANODE_LOCATION.getX(),
                                                               ANODE_LOCATION.getY() - ANODE_LENGTH / 2,
                                                               ANODE_LOCATION.getX(),
                                                               ANODE_LOCATION.getY() + ANODE_LENGTH / 2 );
    public static final Insets ELECTRODE_INSETS = new Insets( 15, 30, 15, 30 );
    public static final Point2D TUBE_ULC = new Point2D.Double( CATHODE_LOCATION.getX() - ELECTRODE_INSETS.left,
                                                               CATHODE_LOCATION.getY() - CATHODE_LENGTH / 2 - ELECTRODE_INSETS.top);
    public static final double TUBE_LENGTH = FluorescentLightsConfig.ANODE_LOCATION.getX()
                                             - FluorescentLightsConfig.CATHODE_LOCATION.getX()
                                             + ELECTRODE_INSETS.left + ELECTRODE_INSETS.right;
    public static final double TUBE_HEIGHT = FluorescentLightsConfig.CATHODE_LENGTH + ELECTRODE_INSETS.top + ELECTRODE_INSETS.bottom;

    public static final double ELECTRON_RADIUS = 3;

    // Images
    public static final String IMAGE_FILE_DIRECTORY = "images";
    public static final String ELECTRON_IMAGE_FILE_NAME = IMAGE_FILE_DIRECTORY + "/" + "electron.gif";

    // Assigned graphic layers
    public static final double ELECTRON_LAYER = 100;
    public static final double TUBE_LAYER = 110;
    public static final double CIRCUIT_LAYER = 120;
}
