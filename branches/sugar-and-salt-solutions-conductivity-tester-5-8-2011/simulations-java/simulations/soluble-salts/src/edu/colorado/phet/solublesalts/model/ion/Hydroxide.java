// Copyright 2002-2011, University of Colorado

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

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Hydroxide
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class Hydroxide extends Ion {
    public static final double RADIUS = 8;
    private static IonProperties ionProperties = new IonProperties( 9, -1, RADIUS );

    public Hydroxide() {
        super( ionProperties );
    }

    public Hydroxide( Point2D position, Vector2D velocity, Vector2D acceleration ) {
        super( position, velocity, acceleration, ionProperties );
    }
}