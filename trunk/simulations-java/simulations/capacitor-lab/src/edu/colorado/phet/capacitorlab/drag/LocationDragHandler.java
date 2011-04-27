// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.drag;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Base class for all drag handlers that set a model element's location.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LocationDragHandler extends PDragSequenceEventHandler {

    private final PNode dragNode;
    private final CLModelViewTransform3D mvt;

    private double clickXOffset, clickYOffset;

    /**
     * Constructor.
     *
     * @param dragNode the node being dragged
     * @param mvt      transform between model and view coordinate frames
     */
    public LocationDragHandler( PNode dragNode, CLModelViewTransform3D mvt ) {
        this.dragNode = dragNode;
        this.mvt = mvt;
    }

    // Gets the current value of the model location that we're controlling.
    protected abstract Point3D getModelLocation();

    // Sets the new value of the model location that we're controlling.
    protected abstract void setModelLocation( Point3D location );

    // When we start the drag, compute offset from dragNode's origin.
    @Override
    protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        Point2D pOrigin = mvt.modelToViewDelta( getModelLocation() );
        clickXOffset = pMouse.getX() - pOrigin.getX();
        clickYOffset = pMouse.getY() - pOrigin.getY();
    }

    // As we drag, compute the new location and update the model.
    @Override
    protected void drag( final PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
        double xView = pMouse.getX() - clickXOffset;
        double yView = pMouse.getY() - clickYOffset;
        setModelLocation( mvt.viewToModel( xView, yView ) );
    }
}
