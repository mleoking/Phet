// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;


/**
 * Model class that represents a human who is roughly 10 years old.
 *
 * @author John Blanco
 */
public class AdultMaleHuman extends ImageMass {

    private static final double MASS = 80; // in kg
    private static final double STANDING_HEIGHT = 1.8; // In meters.
    private static final double SITTING_HEIGHT = 1.0; // In meters.

    public AdultMaleHuman() {
        super( MASS, Images.ADULT_MAN_STANDING, STANDING_HEIGHT, new Point2D.Double( 0, 0 ) );
    }

    @Override public void setOnPlank( boolean onPlank ) {
        if ( onPlank ) {
            height = SITTING_HEIGHT;
            if ( getPosition().getX() > 0 ) {
                imageProperty.set( Images.ADULT_MAN_SITTING );
            }
            else {
                // Reverse image if on other side of balance.
                imageProperty.set( BufferedImageUtils.flipX( Images.ADULT_MAN_SITTING ) );
            }
        }
        else {
            height = STANDING_HEIGHT;
            imageProperty.set( Images.ADULT_MAN_STANDING );
        }
    }
}
