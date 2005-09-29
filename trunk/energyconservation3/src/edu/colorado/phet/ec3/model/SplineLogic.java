/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

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
 * Date: Sep 29, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class SplineLogic {
    private Body body;

    public SplineLogic( Body body ) {
        this.body = body;
    }

    public double guessPositionAlongSpline( AbstractSpline spline ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        Shape bodyShape = body.getLocatedShape();
        //find all segments that overlap.
        ArrayList overlap = new ArrayList();
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment segment = segmentPath.segmentAt( i );
            if( bodyShape.getBounds2D().intersects( segment.getShape().getBounds2D() ) ) {//make sure we need areas
                Area a = new Area( bodyShape );
                a.intersect( new Area( segment.getShape() ) );
                if( !a.isEmpty() ) {
                    overlap.add( segment );
                }
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
        return getClosestScalar( spline, center );
    }

    public double getClosestScalar( AbstractSpline spline, Point2D center ) {
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
