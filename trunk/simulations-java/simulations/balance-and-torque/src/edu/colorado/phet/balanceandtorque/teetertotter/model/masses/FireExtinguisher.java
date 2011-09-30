// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a fire extinguisher.
 *
 * @author John Blanco
 */
public class FireExtinguisher extends ImageMass {

    private static final double MASS = 5; // in kg
    private static final double HEIGHT = 0.4; // In meters.

    public FireExtinguisher() {
        super( MASS, Images.FIRE_EXTINGUISHER, HEIGHT, new Point2D.Double( 0, 0 ), true );
    }
}
