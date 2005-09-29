/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.Segment;
import edu.colorado.phet.ec3.model.spline.SegmentPath;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 7:33:54 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class FreeSplineMode extends ForceMode {
    private AbstractSpline spline;
    private Body body;

    public FreeSplineMode( AbstractSpline spline, Body body ) {
        super( new Vector2D.Double() );
        this.spline = spline;
        this.body = body;
    }

    public void stepInTime( EnergyConservationModel model, Body body, double dt ) {
        double position = guessPositionAlongSpline( getSpline() );
        if( position == 0 ) {
            body.setFreeFallMode();
            super.setNetForce( new Vector2D.Double( 0, 0 ) );
            super.stepInTime( model, body, dt );

        }
        else {
            Segment segment = spline.getSegmentPath().getSegmentAtPosition( position );//todo this duplicates much work.
            body.setAngle( segment.getAngle() );

            double fgy = 9.8 * body.getMass();

            AbstractVector2D normalForce = segment.getUnitNormalVector().getScaledInstance( fgy * Math.cos( segment.getAngle() ) );
            double fy = fgy + normalForce.getY();
            double fx = normalForce.getX();
            super.setNetForce( new Vector2D.Double( fx, fy ) );


            super.stepInTime( model, body, dt );
            //just kill the perpendicular part of velocity, if it is through the track.  this should be lost to friction.
            //or to a bounce.
            double vParallel = segment.getUnitDirectionVector().dot( body.getVelocity() );
            double vPerp = segment.getUnitNormalVector().dot( body.getVelocity() );
            System.out.println( "body.getVelocity() = " + body.getVelocity() );
            System.out.println( "vParallel = " + vParallel );
            System.out.println( "vPerp = " + vPerp );
//            if( vPerp < 0 ) {
//                vPerp = -vPerp;
//            }
            Vector2D.Double newVelocity = new Vector2D.Double( vParallel, vPerp );
            newVelocity.rotate( segment.getAngle() );
            System.out.println( "newVelocity = " + newVelocity );
            body.setVelocity( newVelocity );

        }
    }

    public AbstractSpline getSpline() {
        return spline;
    }

    private double guessPositionAlongSpline( AbstractSpline spline ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        Shape bodyShape = body.getLocatedShape();
        //find all segments that overlap.
        ArrayList overlap = new ArrayList();
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment segment = segmentPath.segmentAt( i );
            Area a = new Area( bodyShape );
            a.intersect( new Area( segment.getShape() ) );
            if( !a.isEmpty() ) {
                overlap.add( segment );
            }
        }

        //return the centroid.
        Rectangle2D rect = null;
        for( int i = 0; i < overlap.size(); i++ ) {
            Segment segment = (Segment)overlap.get( i );
            if( rect == null ) {
                rect = segment.toLine2D().getBounds2D();
            }
            else {
                rect = rect.createUnion( segment.toLine2D().getBounds2D() );
            }
        }
        if( rect == null ) {
            return 0.0;
        }
        Point2D center = new PBounds( rect ).getCenter2D();
        return getClosestScalar( center );
    }

    private double getClosestScalar( Point2D center ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        double closestPosition = Double.POSITIVE_INFINITY;
        Segment bestSegment = null;
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment seg = segmentPath.segmentAt( i );
            double distance = center.distance( seg.getCenter2D() );
            if( distance < closestPosition ) {
                bestSegment = seg;
                closestPosition = distance;
            }
        }
        return segmentPath.getScalarPosition( bestSegment );
    }
}
