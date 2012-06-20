package edu.colorado.phet.common.phetcommon.math;

/**
 * Two-dimensional ray (position with a direction)
 */
public class Ray2D {
    // the position where the ray is pointed from
    public final ImmutableVector2D pos;

    // the unit vector direction in which the ray is pointed
    public final ImmutableVector2D dir;

    public Ray2D( ImmutableVector2D pos, ImmutableVector2D dir ) {
        this.pos = pos;

        // normalize dir if needed
        this.dir = dir.getMagnitude() == 1 ? dir : dir.getNormalizedInstance();
    }

    // a ray whose position is shifted by the specified distance in the direction of the ray
    public Ray2D shifted( double distance ) {
        return new Ray2D( pointAtDistance( distance ), dir );
    }

    public ImmutableVector2D pointAtDistance( double distance ) {
        return pos.plus( dir.times( distance ) );
    }

    @Override public String toString() {
        return pos.toString() + " => " + dir.toString();
    }
}
