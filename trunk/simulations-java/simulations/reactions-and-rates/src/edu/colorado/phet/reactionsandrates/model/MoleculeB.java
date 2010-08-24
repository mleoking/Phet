/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import edu.colorado.phet.common.phetcommon.math.Vector2DInterface;

import java.awt.geom.Point2D;

/**
 * MoleculeA
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeB extends SimpleMolecule {
    private static double RADIUS = 10;

    public static double getRADIUS() {
        return RADIUS;
    }

    public MoleculeB() {
        super( MoleculeB.RADIUS );
    }

    public MoleculeB( Point2D location, Vector2DInterface velocity, Vector2DInterface acceleration, double mass, double charge ) {
        super( MoleculeB.RADIUS, location, velocity, acceleration, mass, charge );
    }


    public Object clone() {
        return super.clone();
    }
}
