package edu.colorado.phet.gravityandorbits.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * see http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
 *
 * @author Sam Reid
 */
public class ModelState {
    ArrayList<BodyState> bodyStates;

    public ModelState( ArrayList<BodyState> bodyStates ) {
        this.bodyStates = bodyStates;
    }

    public ModelState getNextState( double dt, int numSteps ) {
        ModelState state = this;
        for ( int i = 0; i < numSteps; i++ ) {
            state = state.getNextState( dt / numSteps );
        }
        return state;
    }

    public ModelState getNextState( double dt ) {
        //See http://www.fisica.uniud.it/~ercolessi/md/md/node21.html
        ArrayList<BodyState> newState = new ArrayList<BodyState>();
        for ( BodyState bodyState : bodyStates ) {
            //Velocity Verlet
            ImmutableVector2D newPosition = bodyState.position.getAddedInstance( bodyState.velocity.getScaledInstance( dt ) ).getAddedInstance( bodyState.acceleration.getScaledInstance( dt * dt / 2 ) );
            ImmutableVector2D newVelocityHalfStep = bodyState.velocity.getAddedInstance( bodyState.acceleration.getScaledInstance( dt / 2 ) );
            ImmutableVector2D newAcceleration = getForce( bodyState, newPosition ).getScaledInstance( -1.0 / bodyState.mass );
            ImmutableVector2D newVelocity = newVelocityHalfStep.getAddedInstance( newAcceleration.getScaledInstance( dt / 2.0 ) );
            newState.add( new BodyState( newPosition, newVelocity, newAcceleration, bodyState.mass ) );

            //Euler
//            ImmutableVector2D acceleration = getForce( bodyState, bodyState.position).getScaledInstance( -1/bodyState.mass );
//            ImmutableVector2D newVelocity = bodyState.velocity.getAddedInstance( acceleration.getScaledInstance( dt ) );
//            ImmutableVector2D newPosition = bodyState.position.getAddedInstance( newVelocity.getScaledInstance( dt ) ).getAddedInstance( acceleration.getScaledInstance( dt * dt / 2 ) );
//            newState.add( new BodyState( newPosition, newVelocity, acceleration, bodyState.mass ));
        }
//        System.out.println( "getForce(bodyStates.get(2) = " + getForce( bodyStates.get( 1 ), bodyStates.get( 2 ), bodyStates.get( 2 ).position ) );
        return new ModelState( newState );
    }

    //TODO: limit distance so forces don't become infinite

    private ImmutableVector2D getForce( BodyState source, BodyState target, ImmutableVector2D newTargetPosition ) {
        if ( source.position.equals( newTargetPosition ) ) {//If they are on top of each other, force should be infinite, but ignore it since we want to have semi-realistic behavior
            return new ImmutableVector2D();
        }
        else {
            return getUnitVector( source, newTargetPosition ).getScaledInstance( GravityAndOrbitsModule.G * source.mass * target.mass / source.distanceSquared( newTargetPosition ) );
        }
    }

    private ImmutableVector2D getUnitVector( BodyState source, ImmutableVector2D newPosition ) {
        return newPosition.getSubtractedInstance( source.position ).getNormalizedInstance();
    }

    /**
     * Get the force on body at its proposed new position
     *
     * @param target
     * @param newTargetPosition
     * @return
     */
    public ImmutableVector2D getForce( BodyState target, ImmutableVector2D newTargetPosition ) {
        ImmutableVector2D sum = new ImmutableVector2D();
        for ( BodyState source : bodyStates ) {
            if ( source != target ) {
                sum = sum.getAddedInstance( getForce( source, target, newTargetPosition ) );
            }
        }
        return sum;
    }

    public BodyState getBodyState( int index ) {
        return bodyStates.get( index );
    }
}
