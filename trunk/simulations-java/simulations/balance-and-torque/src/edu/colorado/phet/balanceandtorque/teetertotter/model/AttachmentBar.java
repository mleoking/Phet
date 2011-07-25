// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author John Blanco
 */
public class AttachmentBar extends ShapeModelElement {
    private static final double WIDTH = 0.05; // In meters.

    /**
     * Constructor.
     *
     * @param plank The plank to which this bar is attached.  The fulcrum is
     *              not necessary, since the plank keeps track of both the pivot point and
     *              the attachment point.
     */
    public AttachmentBar( final Plank plank ) {
        super( generateShape( plank.getPivotPoint(), plank.attachmentPointProperty.get() ) );
        plank.attachmentPointProperty.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D point2D ) {
                setShapeProperty( generateShape( plank.getPivotPoint(), plank.attachmentPointProperty.get() ) );
            }
        } );
    }

    private static Shape generateShape( Point2D pivotPoint, Point2D attachmentPoint ) {
        DoubleGeneralPath path = new DoubleGeneralPath( pivotPoint );
        path.lineTo( attachmentPoint );
        return path.getGeneralPath();
    }
}
