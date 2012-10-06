// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a barrel.
 *
 * @author John Blanco
 */
public class Barrel extends ImageMass {

    private static final double MASS = 90; // in kg
    private static final double HEIGHT = 0.75; // In meters.

    public Barrel( boolean isMystery ) {
        super( MASS, Images.BARREL, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}