// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model.behaviors;

import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.PlateMotionPlate;
import edu.colorado.phet.platetectonics.model.Sample;

public class TransformBehavior extends PlateBehavior {

    private final boolean towardsFront;

    private float timeElapsed = 0;
    private static final float FAULT_VALLEY_TIME_FACTOR = 0.3f;
    private static final float VALLEY_DIP_DISTANCE = 5000;

    public TransformBehavior( PlateMotionPlate plate, PlateMotionPlate otherPlate, boolean towardsFront ) {
        super( plate, otherPlate );
        this.towardsFront = towardsFront;
    }

    @Override public void stepInTime( float millionsOfYears ) {

        float timeBefore = timeElapsed;
        timeElapsed += millionsOfYears;
        float timeAfter = timeElapsed;

        getPlate().shiftZ( 30000f / 2 * ( towardsFront ? millionsOfYears : -millionsOfYears ) );


        float riftValleyAmount = (float) ( Math.exp( -timeBefore * FAULT_VALLEY_TIME_FACTOR ) - Math.exp( -timeAfter * FAULT_VALLEY_TIME_FACTOR ) );

        // add in the rift valley
        final float delta = -riftValleyAmount * VALLEY_DIP_DISTANCE;
        shiftIndexElevation( 0, delta );
        shiftIndexElevation( 1, delta / 4 );
        shiftIndexElevation( 2, -delta / 4 );

        getPlate().getTerrain().elevationChanged.updateListeners();
    }

    public void shiftIndexElevation( int index, float delta ) {
        getTerrain().shiftColumnElevation( getOppositeSide().getFromIndex( getTerrain().getNumColumns(), index ), delta );
        final Sample sample = getOppositeSide().getFromEnd( getCrust().getTopBoundary().samples, index );
        sample.setPosition( sample.getPosition().plus( ImmutableVector3F.Y_UNIT.times( delta ) ) );
    }
}