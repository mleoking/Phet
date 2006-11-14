package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Point2D;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 11:55:53 AM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class SplineMode implements UpdateMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private double lastX;
    private Body lastState;
    private Body afterNewton;
    private Vector2D.Double lastNormalForce = new Vector2D.Double();

    public SplineMode( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public boolean isUserControlled( Body body ) {
        return body.isUserControlled() || spline.isUserControlled();
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();
        double x1 = lastX;
        pointVelocityAlongSpline( x1, body );
        AbstractVector2D netForceWithoutNormal = getNetForcesWithoutNormal( x1, body );
        new ForceMode( netForceWithoutNormal ).stepInTime( body, dt );
        afterNewton = body.copyState();

        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x1, 0.3, 60, 2 );
        if( x2 <= 0 || x2 >= spline.getLength() - 0.01 ) {//fly off the end of the spline
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
        }
        else if( shouldFlyOff( x2, body ) ) {
            body.setLastFallTime( spline, System.currentTimeMillis() );
            body.setFreeFallMode();
            body.setAngularVelocity( 0.0 );
        }
        else {
            double thermalEnergy = getFrictionForce( ( x1 + x2 ) / 2, body ).getMagnitude() * origState.getPositionVector().getSubtractedInstance( body.getPositionVector() ).getMagnitude();
            body.addThermalEnergy( thermalEnergy );
            lastX = x2;

            //todo: make sure we sank into the spline before applying this change
            //these 2 steps are sometimes changing the energy by a lot!!!
//            Body beforeAttach = body.copyState();
            body.setAttachmentPointPosition( spline.evaluateAnalytical( x2 ) );
            rotateBody( x2, dt, Double.POSITIVE_INFINITY, body );
//            rotateBody( x2, dt, Math.PI/32, body );
//            if( body.getTotalEnergy() > origState.getTotalEnergy() && body.getEnergyDifferenceAbs( origState ) > 1 && body.getKineticEnergy() < body.getEnergyDifferenceAbs( origState ) ) {
//                body.setLastFallTime( spline, System.currentTimeMillis() );
//                body.setFreeFallMode();
//                body.setAngularVelocity( 0.0 );
//                return;
//            }

            if( !isUserControlled( body ) ) {
                fixEnergy( origState, netForceWithoutNormal.getAddedInstance( lastNormalForce ), x2, body, dt );
            }
            lastState = body.copyState();
            lastNormalForce = updateNormalForce( origState, body, netForceWithoutNormal, dt );
        }
    }

    private void pointVelocityAlongSpline( double x1, Body body ) {
        double sign = spline.getUnitParallelVector( x1 ).dot( body.getVelocity() ) > 0 ? 1 : -1;
        body.setVelocity( spline.getUnitParallelVector( x1 ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() * sign ) );
    }

    private void fixEnergy( Body origState, AbstractVector2D netForce, double x2, final Body body, double dt ) {
        boolean fixed = false;
        if( !fixed && body.getSpeed() >= 0.1 ) {
            //increasing the speed threshold from 0.001 to 0.1 causes the moon-sticking problem to go away.
            fixed = fixed || new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15, 0.001 );
        }
        if( !fixed && Math.abs( spline.getUnitNormalVector( x2 ).getY() ) < 0.9 ) {
            double epsilon = 0.001;//1E-8     
            fixed = fixed || fixEnergyOnSpline( origState, x2, body, epsilon );
        }
        if( !fixed ) {
            fixed = fixed || new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 15, 0.001 );
        }
        if( !fixed ) {
            //try to fix with heat
            if( body.getFrictionCoefficient() > 0 ) {
                if( body.getTotalEnergy() > origState.getTotalEnergy() ) {
                    double gainedEnergyValue = origState.getEnergyDifferenceAbs( body );
                    System.out.println( "Energy error: gained " + gainedEnergyValue + " joules" );
                    double gainedHeat = body.getThermalEnergy() - origState.getThermalEnergy();
                    System.out.println( "gained " + gainedHeat + " joules of heat" );
                    if( gainedHeat > gainedEnergyValue ) {
                        body.addThermalEnergy( -gainedEnergyValue );
                        System.out.println( "Reduced heat to solve energy crisis: newError=" + origState.getEnergyDifferenceAbs( body ) );
                        fixed = true;
                    }
                    else {
                        System.out.println( "Had Error, but can't wholly correct with heat, removing what we can." );
                        body.addThermalEnergy( -gainedHeat );
                    }
                }
                else {
                    System.out.println( "Energy error: lost " + origState.getEnergyDifferenceAbs( body ) + " joules" );
                    System.out.println( "The system lost energy while friction was on!!! error=" + origState.getEnergyDifferenceAbs( body ) );

                    //rarely happens
                }
            }
        }

        if( !fixed ) {
            //look for a nearby rotation and/or spline position that conserves energy...?
            //wait until upside up to stop in a well
//            System.out.println( "netForce.getMagnitude() = " + netForce.getMagnitude() + ", absSinRot=" + Math.abs( Math.sin( body.getAttachmentPointRotation() ) ) );
            if( netForce.getMagnitude() < 5000 && ( Math.abs( Math.sin( body.getAttachmentPointRotation() ) ) < 0.1 ) ) {
                System.out.println( "Looks like the bottom of a well: Stopping..." );
                setBodyState( origState, body );
            }
            else {
                if( origState.getEnergyDifferenceAbs( body ) > 1E-6 ) {
                    double finalE = body.getTotalEnergy();
                    double origE = origState.getTotalEnergy();
                    boolean gainedEnergy = finalE > origE;
                    String text = gainedEnergy ? "Gained Energy" : "Lost Energy";
                    System.out.println( text + ", After everything we tried, still have Energy error=" + origState.getEnergyDifferenceAbs( body ) + ". " + ", velocity=" + body.getVelocity() + ", DeltaVelocity=" + body.getVelocity().getSubtractedInstance( origState.getVelocity() ) + ", deltaY=" + ( body.getY() - origState.getY() ) + ", deltaThermal=" + ( body.getThermalEnergy() - origState.getThermalEnergy() ) + ", ke=" + body.getKineticEnergy() + ", pe=" + body.getPotentialEnergy() + ", deltaKE=" + ( body.getKineticEnergy() - origState.getKineticEnergy() ) + ", deltaPE=" + ( body.getPotentialEnergy() - origState.getPotentialEnergy() ) );
                    double xSpeed = ( body.getX() - origState.getX() ) / dt;
//                    System.out.println( "xSpeed = " + xSpeed );
//                    double speedThreshold = 2.0;
//                    xSpeed = xSpeed > 0 ? Math.min( xSpeed, speedThreshold ) : Math.max( xSpeed, -speedThreshold );
                    //have to push the body to move to the surface of the spline, otherwise gets stuck easily

                    body.setVelocity( origState.getVelocity() );
                    pointVelocityAlongSpline( lastX, body );

//                    xSpeed = body.getVelocity().getX() > 0 ? speedThreshold : -speedThreshold;
                    xSpeed = 0.0;
                    body.setAttachmentPointPosition( origState.getAttachPoint().getX() + xSpeed * dt, origState.getAttachPoint().getY() );

                    body.setThermalEnergy( origState.getThermalEnergy() );
                    body.setAttachmentPointRotation( origState.getAttachmentPointRotation() );
                    //setBodyState( origState, body );
                }
            }
            //maybe could fix by rotation?, i think no.
            //could fix with friction, if friction is enabled.
        }
    }

    boolean fixEnergyOnSpline( final Body origState, double x2, final Body body, double epsilon ) {
        Body beforeFix = body.copyState();
        //look for an adjacent position with a more accurate energy
        double x3 = getDistAlongSplineBinarySearch( x2, epsilon, 60, 5, new AbstractSpline.SplineCriteria() {
            public double evaluate( Point2D loc ) {
                body.setAttachmentPointPosition( loc );
                return Math.abs( body.getTotalEnergy() - origState.getTotalEnergy() );
            }
        } );
        body.setAttachmentPointPosition( spline.evaluateAnalytical( x3 ) );
        double origError = Math.abs( origState.getTotalEnergy() - beforeFix.getTotalEnergy() );
        double newError = Math.abs( origState.getTotalEnergy() - body.getTotalEnergy() );
        return newError == 0;//probably never
//        System.out.println( "x2=" + x2 + ", x3=" + x3 + ", origEnergy=" + origState.getTotalEnergy() + ", beforeFix=" + beforeFix.getTotalEnergy() + ", after fix=" + body.getTotalEnergy() +", origError="+origError+", newError="+newError);
    }

    private double getDistAlongSplineBinarySearch( double center, double epsilon, int numPts, int numIterations, AbstractSpline.SplineCriteria splineCriteria ) {
        double best = 0;
        for( int i = 0; i < numIterations; i++ ) {
            best = spline.minimizeCriteria( Math.max( center - epsilon, 0 ), Math.min( spline.getLength(), center + epsilon ), numPts, splineCriteria );
            center = best;
            epsilon = epsilon / numPts * 2;
        }
        return best;
    }

    static final Random random = new Random();

    private void setBodyState( Body state, Body body ) {
        body.setVelocity( state.getVelocity() );
        body.setAttachmentPointPosition( state.getAttachPoint() );
        body.setAttachmentPointRotation( state.getAttachmentPointRotation() );
        body.setThermalEnergy( state.getThermalEnergy() );
    }

    private boolean shouldFlyOff( double x, Body body ) {
        boolean flyOffTop = afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) > 0 && isSplineTop( spline, x, body );
        boolean flyOffBottom = afterNewton.getVelocity().dot( spline.getUnitNormalVector( x ) ) < 0 && !isSplineTop( spline, x, body );
        return ( flyOffTop || flyOffBottom ) && !spline.isRollerCoasterMode();
    }

    private Vector2D.Double updateNormalForce( Body origState, Body body, AbstractVector2D netForce, double dt ) {
        //numerically unstable, since we divide by dt^2
        //2m/t^2 (x1-x0-v0t)-Fa
        Vector2D.Double vec = new Vector2D.Double();
        vec.add( body.getPositionVector() );
        vec.subtract( origState.getPositionVector() );
        vec.subtract( origState.getVelocity() );
        vec.scale( 2 * body.getMass() / dt / dt );
        vec.subtract( netForce );
        return vec;
    }

    private void rotateBody( double x, double dt, double maxRotationDTheta, Body body ) {
        double bodyAngle = body.getAttachmentPointRotation();
        double dA = spline.getUnitParallelVector( x ).getAngle() - bodyAngle;
        if( dA > Math.PI ) {
            dA -= Math.PI * 2;
        }
        else if( dA < -Math.PI ) {
            dA += Math.PI * 2;
        }
        if( dA > maxRotationDTheta ) {
            dA = maxRotationDTheta;
        }
        else if( dA < -maxRotationDTheta ) {
            dA = -maxRotationDTheta;
        }
        double offset = isSplineTop( spline, x, body ) ? 0.0 : Math.PI;
        body.rotateAboutAttachmentPoint( dA + offset );
    }

    public static boolean isSplineTop( AbstractSpline spline, double x, Body body ) {
        Vector2D.Double cmVector = new Vector2D.Double( body.getAttachPoint(), body.getCenterOfMass() );
        Vector2D.Double attachVector = new Vector2D.Double( body.getAttachPoint(), body.getCenterOfMass() );
        return cmVector.dot( spline.getUnitNormalVector( x ) ) > 0 && attachVector.dot( spline.getUnitNormalVector( x ) ) > 0;
    }

    private double getDistAlongSplineSearch( Point2D attachPoint, double center, double epsilon, int numPts, int numIterations ) {
        double best = 0;
        for( int i = 0; i < numIterations; i++ ) {
            best = getDistAlongSpline( attachPoint, Math.max( center - epsilon, 0 ), Math.min( spline.getLength(), center + epsilon ), numPts );
            center = best;
            epsilon = epsilon / numPts * 2;
        }
        return best;
    }

    private double getDistAlongSpline( Point2D attachPoint ) {
        return getDistAlongSpline( attachPoint, 0, spline.getLength(), 100 );
    }

    public void init( Body body ) {
        body.convertToSpline();
        lastX = getDistAlongSpline( body.getAttachPoint() );
        lastState = body.copyState();
        lastNormalForce = new Vector2D.Double();
    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        return spline.getDistAlongSpline( pt, min, max, numPts );
    }

    private AbstractVector2D getNetForcesWithoutNormal( double x, Body body ) {
        //todo: normal should opposed both gravity and thrust when applicable
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                body.getThrust(),
                getFrictionForce( x, body )
        };
        Vector2D.Double sum = new Vector2D.Double();
        for( int i = 0; i < forces.length; i++ ) {
            AbstractVector2D force = forces[i];
            sum.add( force );
        }
        if( Double.isNaN( sum.getX() ) ) {
            System.out.println( "nan" );
        }
        return sum;
    }

    private AbstractVector2D getFrictionForce( double x, Body body ) {
        //todo kind of a funny workaround for getting friction on the ground.
        double coefficient = Math.max( body.getFrictionCoefficient(), spline.getFrictionCoefficient() );
        double fricMag = coefficient * lastNormalForce.getMagnitude() / 10.0;//todo should the normal force be computed as emergent?
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag );
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    public UpdateMode copy() {
        SplineMode splineMode = new SplineMode( model, spline );
        splineMode.lastX = lastX;
        splineMode.lastNormalForce = lastNormalForce == null ? null : new Vector2D.Double( lastNormalForce );
        //todo: shouldn't we have to do some portion of copying the body states?
        return splineMode;
    }

    public void setSpline( AbstractSpline spline ) {
        this.spline = spline;
    }
}
