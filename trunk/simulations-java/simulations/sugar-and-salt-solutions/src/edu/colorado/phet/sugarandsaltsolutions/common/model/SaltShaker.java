// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.geom.Dimension2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Model element for the salt shaker, which includes its position and rotation and adds salt to the model when shaken
 *
 * @author Sam Reid
 */
public class SaltShaker extends Dispenser {
    //True if the shaker is pointed down.  If pointed down and "reloaded" (see below), it will emit salt crystals
    public final Property<Boolean> pointedDown = new Property<Boolean>( false );

    //True if the user has rotated the shaker up, thus "reloading" it for another shake
    public final Property<Boolean> reloaded = new Property<Boolean>( true );
    private final Random random = new Random();

    public void translate( Dimension2D delta ) {
        super.translate( delta );
        if ( angle.get() < Math.PI / 2 * 1.1 ) {
            reloaded.set( true );
        }
        pointedDown.set( angle.get() > Math.PI / 2 && reloaded.get() );
    }

    //Called when the model steps in time, and adds any salt crystals to the sim if the dispenser is pouring
    public void updateModel( SugarAndSaltSolutionModel model ) {
        //Check to see if we should be emitting salt crystals-- if the shaker was shaken up then down it will be ready to emit salt
        if ( enabled.get() && pointedDown.get() ) {
            int numCrystals = random.nextInt( 6 ) + 2;
            for ( int i = 0; i < numCrystals; i++ ) {
                //Determine where the salt should come out
                double randUniform = ( random.nextDouble() - 0.5 ) * 2;
                final ImmutableVector2D outputPoint = rotationPoint.get().plus( ImmutableVector2D.parseAngleAndMagnitude( 0.09, angle.get() + Math.PI / 2 + randUniform * Math.PI / 32 * 1.2 ) );//Hand tuned to match up with the image, will need to be re-tuned if the image changes

                //Add the salt
                model.addSalt( new Salt( outputPoint ) {{
                    //Give the salt an appropriate velocity when it comes out so it arcs
                    velocity.set( getCrystalVelocity( outputPoint ) );
                }} );
            }
            pointedDown.set( false );
            reloaded.set( false );
        }
    }
}