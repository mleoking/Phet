/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 12:37:46 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class LimbNode extends PNode {
    private Point pivot;

    public LimbNode( String imageLoc, Point pivot ) {
        final PImage arm = PImageFactory.create( imageLoc );
        addChild( arm );
        this.pivot = pivot;
        addInputEventListener( new RotationHandler( this ) );
    }

    /**
     * Determines the angle of the input point to the pivot point in global coordinates.
     *
     * @param x
     * @param y
     */
    public double getAngleGlobal( double x, double y ) {
        Point2D temp = new Point2D.Double( pivot.x, pivot.y );
        localToGlobal( temp );
        Vector2D.Double vec = new Vector2D.Double( new Point2D.Double( x, y ), temp );
        return vec.getAngle();
    }

    public void rotateAboutPivot( double angle ) {
        rotateAboutPoint( angle, pivot );
    }
}
