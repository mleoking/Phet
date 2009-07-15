/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.radioactivedatinggame;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.nuclearphysics.module.alphadecay.multinucleus.MultiNucleusDecayModel;

/**
 * This class implements the behavior of a model element that represents a
 * rock that can be dated by radiometric means, and that starts off looking
 * hot and then cools down.
 *
 * @author John Blanco
 */
public class AgingRock extends AnimatedDatableItem {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final int FLY_COUNT = 50; // Controls how long it takes the rock to fly out and then hit the ground.
	private static final double FINAL_X_TRANSLATION = -20; // Model units, roughly meters.
	private static final double FINAL_ROCK_WIDTH = 10; // Model units, roughly meters.
	private static final double ARC_HEIGHT = 10; // Model units, roughly meters.
	private static final double ARC_HEIGHT_FACTOR = 0.03; // Higher for higher arc.
	private static final double AGE_OF_NATURAL_DEATH = MultiNucleusDecayModel.convertYearsToMs(1E9);
	private static final double ROTATION_PER_STEP = Math.PI/20; // Controls rate of rotation when flying.
	private static final Random RAND = new Random();
	private static final double GRAV = 1;
	private static final int COOLING_START_PAUSE_STEPS = 20; // Length of pause before after landing & before starting to cool.
	private static final int COOLING_STEPS = 75; // Number of steps to 

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private int _flyCounter = FLY_COUNT;
	private double _growthPerStep;
	private Point2D _initialLocation;
	private double _deltaTime;
	private double _coolingStartPauseCounter = COOLING_START_PAUSE_STEPS;
	private double _coolingCounter = COOLING_STEPS;
	private boolean _closurePossibleSent = false;
	private boolean _closureOccurredSent = false;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public AgingRock( ConstantDtClock clock, Point2D center, double width ) {
        super( "Aging Rock", Arrays.asList( "molten_rock_large.png", "rock_volcanic_larger.png" ), center, width, 0, 
        		0, clock, EruptingVolcano.VOLCANO_AGE_FACTOR );
    
        _initialLocation = center;
        
        // Calculate the amount of growth needed per step in order to reach
        // the right size by the end of the flight.
    	_growthPerStep = Math.pow( FINAL_ROCK_WIDTH / width, 1 / (double)FLY_COUNT );
    	
    	double T = (Math.sqrt(2 * ARC_HEIGHT) + Math.sqrt(2 * ARC_HEIGHT + 4 * _initialLocation.getY())) / 2 * GRAV;
    	_deltaTime = T / (double)FLY_COUNT;
    }
    
    //------------------------------------------------------------------------
    // Methods.
    //------------------------------------------------------------------------
    
    @Override
    protected void handleClockTicked() {
    	super.handleClockTicked();
    	animate(getClock().getSimulationTime() * getTimeConversionFactor() - getBirthTime());
    }
    
    /**
     * Implement the next steps in the animation of the rock based on a
     * number of factors, such as its age, whether closure has occurred,
     * etc.
     * 
     * @param time
     */
    private void animate(double time){

    	if (_flyCounter > 0){

    		// Move along the arc.
    		double flightXTranslation = FINAL_X_TRANSLATION / FLY_COUNT;
    		double flightYTranslation = (_flyCounter - (FLY_COUNT * 0.6)) * ARC_HEIGHT_FACTOR;
    		setPosition(getPosition().getX() + flightXTranslation, getPosition().getY() + flightYTranslation);
//    		double deltaXPos = FINAL_X_TRANSLATION / FLY_COUNT;
//    		double n = FLY_COUNT - _flyCounter;
//    		double yPos = _initialLocation.getY() + (Math.sqrt(2 * GRAV * ARC_HEIGHT) * n * _deltaTime) -
//    			(GRAV * n * n * _deltaTime * _deltaTime);
//    		setPosition(getPosition().getX() + deltaXPos, yPos);
    		
    		// Grow.
			Dimension2D size = getSize();
			size.setSize(size.getWidth() * _growthPerStep, size.getHeight() * _growthPerStep );
			setSize(size);
			
			// Rotate.
			setRotationalAngle(getRotationalAngle() + ROTATION_PER_STEP);
    		
			// Move to the next step.
    		_flyCounter--;
    	}
    	else if (_flyCounter <= 0 && !_closurePossibleSent){
    		// The rock has started cooling, so it is now possible to start
    		// closure if desired.
    		setClosureState(RadiometricClosureState.CLOSURE_POSSIBLE);
    		_closurePossibleSent = true;
    	}
    	else if (_coolingStartPauseCounter > 0){
    		_coolingStartPauseCounter--;
    	}
    	else if (_coolingCounter > 0){
    		setFadeFactor(Math.min(getFadeFactor() + (1 / (double)COOLING_STEPS), 1));
    		_coolingCounter--;
    	}
    	else if (!_closureOccurredSent){
    		// The rock has finished cooling, so closure occurs and the rock
    		// begins radiometrically aging.
    		setClosureState(RadiometricClosureState.CLOSED);
    		_closureOccurredSent = true;
    	}

    	/*
    	// Handle flying out of volcano.
    	double flightYTranslation = arcHeightControl * (((double)flightSteps * 0.42) - i);
    	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
    			new Point2D.Double( flightXTranslation, flightYTranslation ), rotationPerStep, growthPerStep, 0, 
    			0, 0, null ) );
    	
    	Dimension2D size = getSize();
    	if (( size.getHeight() < FULL_GROWN_TREE_HEIGHT && getClosureState() != RadiometricClosureState.CLOSED )){
    		
    		// Grow a little bit.
    		setSize(new PDimension(size.getWidth() * GROWTH_RATE, size.getHeight() * GROWTH_RATE));
    		
    		// Shift up a bit so that it looks like the tree is growing up out
    		// of the ground.
    		Point2D centerPos = getPosition();
    		setPosition(centerPos.getX(), centerPos.getY() + size.getHeight() * 0.012);
    	}
    	
    	if (!_closurePossibleSent){
    		// At the moment of birth for the tree, closure is possible.  If
    		// we haven't set the state to indicate this, do it now.
    		setClosureState(RadiometricClosureState.CLOSURE_POSSIBLE);
    		_closurePossibleSent = true;
    	}
    	
    	// Handle death by natural causes.
    	if (getClosureState() != RadiometricClosureState.CLOSED && time > AGE_OF_NATURAL_DEATH){
    		// Time to die, a.k.a. to radiometrically "close".
    		setClosureState(RadiometricClosureState.CLOSED);
    	}

    	// Handle the post-closure animation.
    	if ( getClosureState() == RadiometricClosureState.CLOSED ){
    		
    		if (getFadeFactor() < 1.0){
    			// Handle fading from live to dead image.
	    		double currentFadeFactor = getFadeFactor();
	    		
	    		double fadeRate = 0.025;
	    		if (time < AGE_OF_NATURAL_DEATH){
	    			// Fade faster if closer was forced so that users don't get
	    			// impatient.
	    			fadeRate *= 1.5;
	    		}
				setFadeFactor(Math.min(currentFadeFactor + 0.02, 1.0));
    		}
    		else if (_swayCounter > 0){
    			
    			// Set the angle for the sway.
    			double swayDeflection = 
    				Math.cos(((double)(_swayCounter - SWAY_COUNT) / (double)SWAY_COUNT) * Math.PI * 2) * MAX_SWAY_DEFLECTION; 
    			
    			rotateAboutBottomCenter(swayDeflection);
    			
    			// Move to the next step in the cycle.
    			_swayCounter--;
    		}
    		else if (_fallCounter > 0){
    			
    			rotateAboutBottomCenter(Math.PI / 2 /(double)FALL_COUNT);
    			
    			// Move to the next step in the cycle.
    			_fallCounter--;
    		}
    		else if (_bounceCounter > 0){
    			
    			double yTranslation = -Math.sin(((double)(_bounceCounter - BOUNCE_COUNT) / (double)BOUNCE_COUNT) * Math.PI * 2) 
    				* (BOUNCE_PROPORTION * getSize().getWidth());
    			setPosition(getPosition().getX(), getPosition().getY() + yTranslation);
    			
    			// Give it a little random rotation to make it look a bit
    			// more like a real bounce.
    			if ((BOUNCE_COUNT - _bounceCounter) % 4 == 0){
    				_previousAngle = getRotationalAngle();
    				setRotationalAngle(_previousAngle + (RAND.nextDouble() * Math.PI / 24));
    			}
    			else if ((BOUNCE_COUNT - _bounceCounter) % 2 == 0){
    				setRotationalAngle(_previousAngle);
    			}
    			_bounceCounter--;
    		}
    	}
    	*/
    }

    //------------------------------------------------------------------------
    // The animation sequence that defines how the appearance of the rock
    // will change as it ages.
    //------------------------------------------------------------------------
    protected AnimationSequence createAnimationSequence() {
        TimeUpdater timeUpdater = new TimeUpdater( 0, MultiNucleusDecayModel.convertYearsToMs( 10E6 ) );
        ArrayList<ModelAnimationDelta> animationSequence = new ArrayList<ModelAnimationDelta>();
        RadiometricClosureEvent closureOccurredEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSED);
        RadiometricClosureEvent closurePossibleEvent = 
        	new RadiometricClosureEvent(this, RadiometricClosureState.CLOSURE_POSSIBLE);
        
        // Rock flies out of volcano in a parabolic arc and gets larger in
        // order to look like it is getting closer.  Also spins.
        int flightSteps = 50;
        double totalGrowthFactor = 10;
        double growthPerStep = Math.pow(totalGrowthFactor, 1/(double)flightSteps);
        double totalRotation = -8 * Math.PI;
        double rotationPerStep = totalRotation / flightSteps;
        double arcHeightControl = 0.04; // Higher for higher arc, lower for lower arc.
        double flightXTranslation = -0.38; // Higher positive or negative number move further.
        for (int i = 0; i < flightSteps; i++){
        	double flightYTranslation = arcHeightControl * (((double)flightSteps * 0.42) - i);
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), 
        			new Point2D.Double( flightXTranslation, flightYTranslation ), rotationPerStep, growthPerStep, 0, 
        			0, 0, null ) );
        }
        
        // Rock should be sitting on the ground now, so closure is possible.
        animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, 0, closurePossibleEvent ) );

        // Pause for a while.
        for (int i = 0; i < 50; i++){
        	timeUpdater.updateTime();
        }
        
        // Rock cools down.
        int coolSteps = 70;
        double coolFadePerStep = 1 / (double)coolSteps;
        for (int i = 0; i < coolSteps; i++){
        	animationSequence.add( new ModelAnimationDelta( timeUpdater.updateTime(), null, 0, 1, 0, 0, coolFadePerStep, null ) );
        }
        
        // Done cooling, so radiometric closure occurs.
        double tempTime = timeUpdater.updateTime();
        setClosureAge(tempTime);
        animationSequence.add( new ModelAnimationDelta( tempTime, null, 0, 1, 0, 0, 0, closureOccurredEvent ) );
        
        return new StaticAnimationSequence(animationSequence);
    }
}
