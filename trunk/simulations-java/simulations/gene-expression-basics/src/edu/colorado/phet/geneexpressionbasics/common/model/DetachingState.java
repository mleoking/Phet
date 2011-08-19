// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * State where the biomolecule is detaching from an attachment site.  It moves
 * away certain way (e.g. moves up from the DNA) and is unavailable for other
 * attachments for a while.
 *
 * @author John Blanco
 */
public class DetachingState extends BiomoleculeBehaviorState {

    private static final double DRIFT_AWAY_TIME = 3; // In seconds.

    private final IMotionStrategy motionStrategy;

    private double detachTime = DRIFT_AWAY_TIME;

    /**
     * Constructor for state that will cause the molecule to wander in a general
     * direction after detaching.
     *
     * @param detachDirection
     */
    public DetachingState( ImmutableVector2D detachDirection ) {
        motionStrategy = new WanderInGeneralDirectionMotionStrategy( detachDirection );
    }

    /**
     * Constructor for state that will cause the molecule to wander randomly
     * after detaching.
     */
    public DetachingState() {
        motionStrategy = new RandomWalkMotionStrategy();
    }

    @Override public BiomoleculeBehaviorState stepInTime( double dt, MobileBiomolecule biomolecule ) {
        biomolecule.setPosition( motionStrategy.getNextLocation( dt, biomolecule.getPosition() ) );
        detachTime -= dt;
        if ( detachTime <= 0 ) {
            // Done detaching - move to next state.
            return new UnattachedButUnavailableState();
        }
        else {
            // No state change.
            return this;
        }
    }

    @Override public BiomoleculeBehaviorState considerAttachment( AttachmentSite attachmentSite, MobileBiomolecule biomolecule ) {
        // While detaching requests for new attachments are ignored.
        return this;
    }

    @Override public BiomoleculeBehaviorState movedByUser() {
        // Go directly to the unattached and available state.
        detachTime = 0;
        return new UnattachedAndAvailableState();
    }
}
