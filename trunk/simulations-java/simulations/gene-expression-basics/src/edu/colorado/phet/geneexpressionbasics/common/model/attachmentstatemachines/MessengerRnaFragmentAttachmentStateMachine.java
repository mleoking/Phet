// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.RandomWalkMotionStrategy;
import edu.colorado.phet.geneexpressionbasics.common.model.motionstrategies.StillnessMotionStrategy;

/**
 * Attachment state machine for messenger RNA fragments.  These fragments
 * start their life as being attached to an mRNA destroyer, and and then
 * released into the cytoplasm to wander and fade.
 *
 * @author John Blanco
 */
public class MessengerRnaFragmentAttachmentStateMachine extends AttachmentStateMachine {

    public MessengerRnaFragmentAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );
        setState( new AttachedToDestroyerState() );
    }

    @Override public void detach() {
        setState( new UnattachedAndFadingState() );
    }

    @Override public void forceImmediateUnattached() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected class AttachedToDestroyerState extends AttachmentState {

        @Override public void entered( AttachmentStateMachine asm ) {
            biomolecule.setMotionStrategy( new StillnessMotionStrategy() );
        }
    }

    protected class UnattachedAndFadingState extends AttachmentState {

        @Override public void stepInTime( AttachmentStateMachine asm, double dt ) {
            // TODO: Fades from existence.
        }

        @Override public void entered( AttachmentStateMachine asm ) {
            biomolecule.setMotionStrategy( new RandomWalkMotionStrategy( biomolecule.motionBoundsProperty ) );
        }
    }
}
