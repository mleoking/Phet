/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * tree that can be dated by radiometric means, and that grows, dies, and
 * falls over as time goes by.
 *
 * @author John Blanco
 */
public class AgingTree extends AnimatedDatableItem {

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingTree( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Tree", Arrays.asList( "tree_1.png", "tree_dead_2.png" ), center, width, 0, 0, clock, MultiNucleusDecayModel.convertYearsToMs( 1000 ) / 5000 );
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the tree
    // will change as it ages.
    //-----------------------------------------------------------------------

    protected AnimationSequence getAnimationSequence() {
    	
    	// Vars needed in the sequence.
        double growthRate = 1.04;
        double verticalGrowthCompensation = 0.11;
        RadiometricClosureEvent closureOccurredEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSED);
        RadiometricClosureEvent closurePossibleEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSURE_POSSIBLE);
        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 25 ) );

        // Create the sequence.
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        
        // Add the individual animation deltas to the sequence.
        
        // Signal that closure is possible.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, growthRate, 0, 0, 0, 
        		closurePossibleEvent ) );

        // Grow.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        		new Point2D.Double(0, verticalGrowthCompensation), 0, growthRate, 0, 0, 0, null ) );
        verticalGrowthCompensation = verticalGrowthCompensation * growthRate;

        // Fade from the live to the dead tree.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1.0, 0, 0, 0.2, null ) );
        
        // Signal that closure has occurred.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, growthRate, 0, 0, 0, 
        		closureOccurredEvent ) );
        
        // Pause.
        timeUpdater.updateTime();
        timeUpdater.updateTime();
        timeUpdater.updateTime();
        timeUpdater.updateTime();

        // Sway back and forth a bit.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.05, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.05, 1.0, 0, 0, 0, null ) );
        timeUpdater.updateTime();
        timeUpdater.updateTime();
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, -0.05, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, -0.05, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, -0.05, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, -0.05, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.05, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0.05, 1.0, 0, 0, 0, null ) );
        
        // Fall over.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.3, -0.7), 0.3, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.3, -0.7), 0.3, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.3, -0.7), 0.3, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.3, -0.7), 0.3, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.3, -0.7), 0.3, 1.0, 0, 0, 0, null ) );
        
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.1, -1.0), 0.0, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.1, -1.0), 0.0, 1.0, 0, 0, 0, null ) );
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), new Point2D.Double(0.1, -1.0), 0.0, 1.0, 0, 0, 0, null ) );
        
        // Bounce.

        return new StaticAnimationSequence(animationSequence);
    }
}
