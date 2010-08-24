/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.ion;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

/**
 * Iodine
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Iodide extends Ion {
    public static final double RADIUS = 6;
    private static IonProperties ionProperties = new IonProperties( 53, -1, RADIUS );

    public Iodide() {
        super( ionProperties );
    }

    public Iodide( Point2D position, Vector2DInterface velocity, Vector2DInterface acceleration ) {
        super( position, velocity, acceleration, ionProperties );
    }
}
