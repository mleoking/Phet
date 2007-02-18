package edu.colorado.phet.ec3.test.phys1d;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 18, 2007
 * Time: 11:16:29 AM
 * Copyright (c) Feb 18, 2007 by Sam Reid
 */
public class Particle1D {

    private double alpha = 0.25;
    private CubicSpline2D cubicSpline;
    private double velocity = 0;
    private ArrayList listeners = new ArrayList();
    private UpdateStrategy updateStrategy = new Verlet();
    private double g = 9.8 * 100000;//in pixels per time squared
    private double mass = 1.0;
    private double totalDE = 0;

    public Particle1D( CubicSpline2D cubicSpline ) {
        this.cubicSpline = cubicSpline;
    }

    public double getX() {
        return cubicSpline.evaluate( alpha ).getX();
    }

    public double getY() {
        return cubicSpline.evaluate( alpha ).getY();
    }

    public void stepInTime( double dt ) {
        double initEnergy = getEnergy();
        int N = 10;
//            int N=4;
        for( int i = 0; i < N; i++ ) {
            updateStrategy.stepInTime( dt / N );
        }

        double dEUpdate = getNormalizedEnergyDiff( initEnergy );
        totalDE += dEUpdate;

//        fixEnergy( initEnergy );
        double dEFix = getNormalizedEnergyDiff( initEnergy );
//            System.out.println( "dEUpdate = " + dEUpdate + "\tdEFix=" + dEFix + ", totalDE=" + totalDE + ", RC=" + getRadiusOfCurvature() );
        System.out.println( "dEUpdate = " + dEUpdate + "\tdEFix=" + dEFix + ", totalDE=" + totalDE );// + ", RC=" + getRadiusOfCurvature() );
//            System.out.println( "dEAfter = " + ( getEnergy() - initEnergy ) / initEnergy );
        //look for an adjacent location that will give the correct energy

        for( int i = 0; i < listeners.size(); i++ ) {
            Particle1DNode particle1DNode = (Particle1DNode)listeners.get( i );
            particle1DNode.update();
        }
    }

    public void addListener( Particle1DNode particle1DNode ) {
        listeners.add( particle1DNode );
    }

    public Point2D getLocation() {
        return new Point2D.Double( getX(), getY() );
    }

    public UpdateStrategy getUpdateStrategy() {
        return updateStrategy;
    }

    public void setUpdateStrategy( UpdateStrategy updateStrategy ) {
        this.updateStrategy = updateStrategy;
    }

    public UpdateStrategy createVerlet() {
        return new Verlet();
    }

    public UpdateStrategy createConstantVelocity() {
        return new ConstantVelocity();
    }

    public void setVelocity( double v ) {
        this.velocity = v;
    }

    public UpdateStrategy createEuler() {
        return new Euler();
    }

    public double getEnergy() {
        return 0.5 * mass * velocity * velocity - mass * g * getY();
    }

    public UpdateStrategy createVerletOffset() {
        return new VerletOffset();
    }

    public double getAlpha() {
        return alpha;
    }

    public void resetEnergyError() {
        totalDE = 0.0;
    }

    public AbstractVector2D getVelocity() {
        return cubicSpline.getUnitParallelVector( alpha ).getInstanceOfMagnitude( velocity );
    }

    public void setCubicSpline2D( CubicSpline2D spline ) {
        cubicSpline = spline;
    }

    public interface UpdateStrategy {
        void stepInTime( double dt );
    }

    private void clampAndBounce() {
        alpha = MathUtil.clamp( 0, alpha, 1.0 );

        if( alpha <= 0 ) {
            velocity *= -1;
        }
        if( alpha >= 1 ) {
            velocity *= -1;
        }
    }

    private void fixEnergy( final double initEnergy ) {
        int count = 0;

        if( Math.abs( velocity ) > 1 ) {
            for( int i = 0; i < 1; i++ ) {
                double dE = getNormalizedEnergyDiff( initEnergy ) * Math.abs( initEnergy );
                double dv = dE / mass / velocity;
                velocity += dv;
            }
        }

//            while( getNormalizedEnergyDiff( initEnergy ) < -1E-6 ) {
//                velocity *= 1.001;
//                System.out.println( "getNormalizedEnergyDiff( ) = " + getNormalizedEnergyDiff( initEnergy ) );
//                count++;
//                if( count > 1000 ) {
//                    break;
//                }
//            }
//
//            while( getNormalizedEnergyDiff( initEnergy ) > 1E-6 ) {
//                velocity *= 0.999;
//                System.out.println( "reducing energy...getNormalizedEnergyDiff( ) = " + getNormalizedEnergyDiff( initEnergy ) );
//                count++;
//                if( count > 1000 ) {
//                    break;
//                }
//            }

//            double dE=finalEnergy-initEnergy;
//            System.out.println( "dE = " + dE );
//            double arg = 2.0 / mass * ( initEnergy + mass * g * getY() );
//            if( arg > 0 ) {
//                double deltaV = Math.abs( Math.sqrt( arg ) - velocity );
//                velocity+=deltaV;
////                if( finalEnergy > initEnergy ) {
////                    velocity = ( Math.abs( velocity ) + deltaV ) * MathUtil.getSign( velocity );
////                }
////                else {
////                    velocity = ( Math.abs( velocity ) - deltaV ) * MathUtil.getSign( velocity );
////                }
//            }
    }

    private double getNormalizedEnergyDiff( double initEnergy ) {
        return ( getEnergy() - initEnergy ) / Math.abs( initEnergy );
    }

    double getRadiusOfCurvature() {
        double epsilon = 0.001;
        double dtds = ( cubicSpline.getAngle( alpha - epsilon ) - cubicSpline.getAngle( alpha + epsilon ) ) / epsilon;
        return 1.0 / dtds;
    }

    public class VerletOffset implements UpdateStrategy {

        double L = 50;

        public void stepInTime( double dt ) {
            double R = getRadiusOfCurvature();
            double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
//                double aOrig = g * Math.cos( origAngle );
            double aOrig = Math.pow( R / ( R + L ), 2 ) * g * Math.cos( origAngle ) * ( 1 + L / R );
            double ds = velocity * dt - 0.5 * aOrig * dt * dt;

            alpha += cubicSpline.getFractionalDistance( alpha, ds );
            double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
//                double accel = g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0;
            double accel = Math.pow( R / ( R + L ), 2 ) * g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2 * ( 1 + L / R );
            velocity = velocity + accel * dt;

            clampAndBounce();
        }
    }

    public class Verlet implements UpdateStrategy {

        public void stepInTime( double dt ) {
            double origAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
            double ds = velocity * dt - 0.5 * g * Math.cos( origAngle ) * dt * dt;

            alpha += cubicSpline.getFractionalDistance( alpha, ds );
            double newAngle = Math.PI / 2 - cubicSpline.getAngle( alpha );
            velocity = velocity + g * ( Math.cos( origAngle ) + Math.cos( newAngle ) ) / 2.0 * dt;

            clampAndBounce();
        }
    }

    public class ConstantVelocity implements UpdateStrategy {

        public void stepInTime( double dt ) {
            alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt );

            clampAndBounce();
        }
    }

    public class Euler implements UpdateStrategy {

        public void stepInTime( double dt ) {
            Vector2D.Double gVector = new Vector2D.Double( 0, g );
            double a = cubicSpline.getUnitParallelVector( alpha ).dot( gVector );
            alpha += cubicSpline.getFractionalDistance( alpha, velocity * dt + 1 / 2 * a * dt * dt );
            velocity += a * dt;

            clampAndBounce();
        }
    }

}
