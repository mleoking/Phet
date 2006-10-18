package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Oct 17, 2006
 * Time: 11:55:53 AM
 * Copyright (c) Oct 17, 2006 by Sam Reid
 */

public class FreeSplineMode2 implements UpdateMode {
    private EnergyConservationModel model;
    private AbstractSpline spline;
    private double savedLocation;
    private Body lastState;
    private Body afterNewton;
    private Vector2D.Double lastNormalForce;

    public FreeSplineMode2( EnergyConservationModel model, AbstractSpline spline ) {
        this.model = model;
        this.spline = spline;
    }

    public void stepInTime( Body body, double dt ) {
        Body origState = body.copyState();
        double x = savedLocation;
        double sign = spline.getUnitParallelVector( x ).dot( body.getVelocity() ) > 0 ? 1 : -1;
        body.setVelocity( spline.getUnitParallelVector( x ).getInstanceOfMagnitude( body.getVelocity().getMagnitude() * sign ) );
        AbstractVector2D netForce = getNetForcesWithoutNormal( body, x );
        new ForceMode( netForce ).stepInTime( body, dt );
        afterNewton = body.copyState();

        double x2 = getDistAlongSplineSearch( body.getAttachPoint(), x, 0.3, 60, 2 );
//        System.out.println( "x2=" + x2 );
        if( x2 <= 0 || x2 >= spline.getLength() - 0.01 ) {//fly off the end of the spline
            body.setFreeFallMode();
            return;
        }
        if( afterNewton.getVelocity().dot( spline.getUnitNormalVector( x2 ) ) > 0 ) {
            body.setFreeFallMode();
            return;
        }
        double thermalEnergy = getFrictionForce( body, ( x + x2 ) / 2 ).getMagnitude() * origState.getPositionVector().getSubtractedInstance( body.getPositionVector() ).getMagnitude();
        body.addThermalEnergy( thermalEnergy );

        savedLocation = x2;
        Point2D splineLocation = spline.evaluateAnalytical( x2 );
        //make sure we sank into the spline before applying this change
        body.setAttachmentPointPosition( splineLocation );
        rotateBody( body, x2, dt, Double.POSITIVE_INFINITY );
        boolean fixed = new EnergyConserver().fixEnergyWithVelocity( body, origState.getTotalEnergy(), 10 );
        if( !fixed ) {
            //maybe could fix by rotation?, i think no.
            //could fix with friction, if friction is enabled.
        }
        //could still have an 
        lastState = body.copyState();

        lastNormalForce = updateNormalForce( origState, body, netForce, dt );
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

    private void rotateBody( Body body, double x, double dt, double maxRotationDTheta ) {
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
        body.rotateAboutAttachmentPoint( dA );
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
        savedLocation = getDistAlongSpline( body.getAttachPoint() );
        lastState = body.copyState();
        lastNormalForce = new Vector2D.Double();
    }

    private double getDistAlongSpline( Point2D pt, double min, double max, double numPts ) {
        return spline.getDistAlongSpline( pt, min, max, numPts );
    }

    private AbstractVector2D getNetForcesWithoutNormal( Body body, double x ) {
        //todo: normal should opposed both gravity and thrust when applicable
        AbstractVector2D[] forces = new AbstractVector2D[]{
                body.getGravityForce(),
                body.getThrust(),
                getFrictionForce( body, x )
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

    private AbstractVector2D getFrictionForce( Body body, double x ) {
        double fricMag = body.getFrictionCoefficient() * lastNormalForce.getMagnitude() / 10.0;//todo should the normal force be computed as emergent?
        if( body.getVelocity().getMagnitude() > 0 ) {
            return body.getVelocity().getInstanceOfMagnitude( -fricMag );
        }
        else {
            return new ImmutableVector2D.Double( 0, 0 );
        }
    }

    public static class GrabSpline {
        private EnergyConservationModel energyConservationModel;

        public GrabSpline( EnergyConservationModel energyConservationModel ) {
            this.energyConservationModel = energyConservationModel;
        }

        public void doGrab( Body body ) {
            body.convertToSpline();
            double bestScore = Double.POSITIVE_INFINITY;
            AbstractSpline bestSpline = null;
            ArrayList allSplines = energyConservationModel.getAllSplines();
            for( int i = 0; i < allSplines.size(); i++ ) {
                AbstractSpline splineSurface = (AbstractSpline)allSplines.get( i );
                double score = getGrabScore( splineSurface, body );
                if( score < bestScore ) {
                    bestScore = score;
                    bestSpline = splineSurface;
                }
            }
            if( bestSpline != null ) {
                body.setSplineMode( energyConservationModel, bestSpline );
            }
            else {
                body.convertToFreefall();
            }
        }

        private double getGrabScore( AbstractSpline splineSurface, Body body ) {

            double x = splineSurface.getDistAlongSpline( body.getAttachPoint(), 0, splineSurface.getLength(), 100 );
            Point2D pt = splineSurface.evaluateAnalytical( x );
            double dist = pt.distance( body.getAttachPoint() );
            if( dist < 0.4 && correctSide( body, x, pt, splineSurface ) ) {
                return dist;
            }
            else {
                return Double.POSITIVE_INFINITY;
            }
        }

        private boolean correctSide( Body body, double x, Point2D splineAttachPoint, AbstractSpline abstractSpline ) {
            Point2D cm = body.getCenterOfMass();
            Vector2D.Double cmVector = new Vector2D.Double( splineAttachPoint, cm );
            Vector2D.Double attachVector = new Vector2D.Double( body.getAttachPoint(), cm );
            return cmVector.dot( abstractSpline.getUnitNormalVector( x ) ) > 0 && attachVector.dot( abstractSpline.getUnitNormalVector( x ) ) > 0;
        }

        boolean intersectsOrig( AbstractSpline spline, Body body ) {
            Area area = new Area( body.getShape() );
            area.intersect( spline.getArea() );
            return !area.isEmpty();
        }


    }
}
