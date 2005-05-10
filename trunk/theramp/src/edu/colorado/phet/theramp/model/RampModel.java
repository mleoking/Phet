/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:12:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampModel implements ModelElement {
    private Ramp ramp;
    private Block block;
    private double gravity = 9.8;
    private SimpleObservable peObservers = new SimpleObservable();
    private SimpleObservable keObservers = new SimpleObservable();
    private double lastTick;
    private ForceVector wallForce;
    private ForceVector appliedForce;
    private ForceVector gravityForce;
    private ForceVector totalForce;
    private ForceVector frictionForce;
    private ForceVector normalForce;
    private double appliedWork = 0.0;
    private double frictiveWork = 0.0;
    private double gravityWork = 0.0;
    private ArrayList listeners = new ArrayList();
    private double zeroPointY = 0.0;
    private double thermalEnergy = 0.0;
    private boolean userAddingEnergy = false;

    public RampModel() {
        ramp = new Ramp();
        block = new Block( ramp );
        wallForce = new ForceVector();
        gravityForce = new ForceVector();
        totalForce = new ForceVector();
        frictionForce = new ForceVector();
        appliedForce = new ForceVector();
        normalForce = new ForceVector();
    }

    public Ramp getRamp() {
        return ramp;
    }

    public Block getBlock() {
        return block;
    }

    public double currentTimeSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

    public void stepInTime( double dt ) {
        if( lastTick != 0.0 ) {
            dt = currentTimeSeconds() - lastTick;
            dt = MathUtil.clamp( 1 / 30.0, dt, 1 / 5.0 );

            double origBlockPosition = block.getPosition();
            double origBlockEnergy = block.getKineticEnergy();
            double origPotEnergy = getPotentialEnergy();
            double origMech = getMechanicalEnergy();

            gravityForce.setX( 0 );
            gravityForce.setY( gravity * block.getMass() );
            double fa = block.getFrictionForce( gravity, appliedForce.getParallelComponent() + gravityForce.getParallelComponent() );
            frictionForce.setParallel( fa );

            double force = appliedForce.getParallelComponent() + gravityForce.getParallelComponent() + frictionForce.getParallelComponent();
            normalForce.setPerpendicular( gravityForce.getPerpendicularComponent() );
            totalForce.setParallel( force );

            double acceleration = force / block.getMass();
//            System.out.println( "gravityForce = " + gravityForce );
//            System.out.println( "frictionForce= " + frictionForce );
//            System.out.println( "appliedForce= " + appliedForce );
//            System.out.println( "force = " + force );
//            System.out.println( "acceleration = " + acceleration );
//            System.out.println( "" );
            block.setAcceleration( acceleration );
            block.stepInTime( dt );

            double newBlockPosition = block.getPosition();
            double blockDX = newBlockPosition - origBlockPosition;
//            double dAppliedWork = Math.abs( appliedForce.getMagnitude() * blockDX );
            double dAppliedWork = ( appliedForce.getParallelComponent() * blockDX );
            double dFrictiveWork = ( frictionForce.getParallelComponent() * blockDX );
            double dGravityWork = ( gravityForce.getParallelComponent() * blockDX );
            appliedWork += dAppliedWork;
            frictiveWork += dFrictiveWork;
            gravityWork += dGravityWork;
            double newKE = block.getKineticEnergy();
            if( newKE != origBlockEnergy ) {
                keObservers.notifyObservers();
            }
            double newPE = getPotentialEnergy();
            if( newPE != origPotEnergy ) {
                peObservers.notifyObservers();
            }
            if( userIsAddingEnergy() ) {
                thermalEnergy += Math.abs( dFrictiveWork );//this is close, but not exact.
            }
            else {
                double finalMech = getMechanicalEnergy();
                double dE = Math.abs( finalMech ) - Math.abs( origMech );
                if( dE <= 0 ) {
                    thermalEnergy += Math.abs( dE );
                }
                else {
                    new RuntimeException( "Gained Energy, dE=" + dE ).printStackTrace();
                }
            }
        }
        lastTick = currentTimeSeconds();
    }

    private double getMechanicalEnergy() {
        return block.getKineticEnergy() + getPotentialEnergy();
    }

    private boolean userIsAddingEnergy() {
        return userAddingEnergy;
    }

    public void setUserIsAddingEnergy( boolean userAddingEnergy ) {
        this.userAddingEnergy = userAddingEnergy;
    }

    public double getPotentialEnergy() {
        double height = getBlockHeight();
        return block.getMass() * height * gravity;
    }

    private double getBlockHeight() {
        return block.getLocation2D().getY() - zeroPointY;
    }

    public void setAppliedForce( double appliedForce ) {
        this.appliedForce.setParallel( appliedForce );
        notifyAppliedForceChanged();
    }

    private void notifyAppliedForceChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.appliedForceChanged();
        }
    }

    public void addKEObserver( SimpleObserver simpleObserver ) {
        keObservers.addObserver( simpleObserver );
    }

    public void addPEObserver( SimpleObserver simpleObserver ) {
        peObservers.addObserver( simpleObserver );
    }

    public ForceVector getWallForce() {
        return wallForce;
    }

    public ForceVector getAppliedForce() {
        return appliedForce;
    }

    public ForceVector getGravityForce() {
        return gravityForce;
    }

    public ForceVector getTotalForce() {
        return totalForce;
    }

    public ForceVector getFrictionForce() {
        return frictionForce;
    }

    public ForceVector getNormalForce() {
        return normalForce;
    }

    public void reset() {
        block.setPosition( 10.0 );
        block.setAcceleration( 0.0 );
        block.setVelocity( 0.0 );
        ramp.setAngle( Math.PI / 16 );
        appliedWork = 0;
        frictiveWork = 0;
        gravityWork = 0;
        thermalEnergy = 0.0;
        peObservers.notifyObservers();
        keObservers.notifyObservers();
    }

    public double getFrictiveWork() {
        return frictiveWork;
    }

    public double getGravityWork() {
        return gravityWork;
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void setZeroPointY( double zeroPointY ) {
        this.zeroPointY = zeroPointY;
        //TODO updates.
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.zeroPointChanged();
        }
        peObservers.notifyObservers();
    }

    public double getZeroPointY() {
        return zeroPointY;
    }

    public double getThermalEnergy() {
        return thermalEnergy;//TODO good code
    }

    public double getTotalEnergy() {
        return getPotentialEnergy() + getBlock().getKineticEnergy() + getThermalEnergy();
    }

    public class ForceVector extends Vector2D.Double {

        public void setParallel( double parallel ) {
            setX( Math.cos( -ramp.getAngle() ) * parallel );
            setY( Math.sin( -ramp.getAngle() ) * parallel );
//            System.out.println( "parallel = " + parallel + " magnitude=" + getMagnitude() );
        }

        public double getParallelComponent() {
            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -ramp.getAngle() );
            double result = dir.dot( this );
            return result;
        }

        public double getPerpendicularComponent() {
            AbstractVector2D dir = Vector2D.Double.parseAngleAndMagnitude( 1, -ramp.getAngle() );
            dir = dir.getNormalVector();
            double result = dir.dot( this );
            return result;
        }

        public void setPerpendicular( double perpendicularComponent ) {
            setX( Math.sin( ramp.getAngle() ) * perpendicularComponent );
            setY( Math.cos( ramp.getAngle() ) * perpendicularComponent );
//            System.out.println( "perp= " + perpendicularComponent + " magnitude=" + getMagnitude() );
        }

        public Vector2D toParallelVector() {
            ForceVector fv = new ForceVector();
            fv.setParallel( getParallelComponent() );
            return fv;
        }

        public Vector2D toPerpendicularVector() {
            ForceVector fv = new ForceVector();
            fv.setPerpendicular( -getPerpendicularComponent() );
            return fv;
        }

        public Vector2D toXVector() {
            return new Vector2D.Double( getX(), 0 );
        }

        public Vector2D toYVector() {
            return new Vector2D.Double( 0, getY() );
        }
    }

    public double getAppliedWork() {
        return appliedWork;
    }

    public static interface Listener {
        public void appliedForceChanged();

        void zeroPointChanged();
    }
}
