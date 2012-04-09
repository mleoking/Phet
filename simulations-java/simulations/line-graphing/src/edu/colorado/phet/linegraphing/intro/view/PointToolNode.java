// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.LGResources.Images;
import edu.colorado.phet.linegraphing.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.LGSimSharing.UserComponents;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Tool that displays the (x,y) coordinates of a point on the graph.
 * Origin is at the tip of the tool (bottom center in the image file.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointToolNode extends PhetPNode {

    private static final String COORDINATES_PATTERN = "({0},{1})";
    private static final NumberFormat COORDINATES_FORMAT = new DefaultDecimalFormat( "0" );
    private static final double COORDINATES_Y_CENTER = 28; // center of the display area, measured from the top of the unscaled image file

    public PointToolNode( Property<ImmutableVector2D> point, final ModelViewTransform mvt, Rectangle2D dragBounds ) {

        // tool body
        final PNode bodyNode = new PImage( Images.POINT_TOOL );
        bodyNode.setOffset( -bodyNode.getFullBoundsReference().getWidth() / 2, -bodyNode.getFullBoundsReference().getHeight() );

        // coordinate display
        final PText coordinatesNode = new PText();
        coordinatesNode.setFont( new PhetFont( 20 ) );

        // rendering order
        addChild( bodyNode );
        addChild( coordinatesNode );

        scale( 0.75 ); //TODO resize image file, or use BufferedImage.multiScale

        // location and display
        point.addObserver( new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D point ) {
                setOffset( mvt.modelToView( point ).toPoint2D() );
                coordinatesNode.setText( MessageFormat.format( COORDINATES_PATTERN, COORDINATES_FORMAT.format( point.getX() ), COORDINATES_FORMAT.format( point.getY() ) ) );
                coordinatesNode.setOffset( bodyNode.getFullBoundsReference().getCenterX() - ( coordinatesNode.getFullBoundsReference().getWidth() / 2 ),
                                           bodyNode.getFullBoundsReference().getMinY() + COORDINATES_Y_CENTER - ( coordinatesNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        } );

        // dragging
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PointToolDragHandler( this, point, mvt, dragBounds ) );
    }

    // Drag handler for the point tool, constrained to the range of the graph, snaps to grid.
    private static class PointToolDragHandler extends SimSharingDragHandler {

        private final PNode dragNode;
        private final Property<ImmutableVector2D> point;
        private final ModelViewTransform mvt;
        private final Rectangle2D dragBounds; // drag bounds, in view coordinate frame
        private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public PointToolDragHandler( PNode dragNode, Property<ImmutableVector2D> point, ModelViewTransform mvt, Rectangle2D dragBounds ) {
            super( UserComponents.pointTool, UserComponentTypes.sprite, true );
            this.dragNode = dragNode;
            this.point = point;
            this.mvt = mvt;
            this.dragBounds = dragBounds;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            clickXOffset = pMouse.getX() - mvt.modelToViewX( point.get().getX() );
            clickYOffset = pMouse.getY() - mvt.modelToViewY( point.get().getY() );
        }

        @Override protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            final double viewX = pMouse.getX() - clickXOffset;
            final double viewY = pMouse.getY() - clickYOffset;
            ImmutableVector2D pView = constrainToBounds( viewX, viewY, dragBounds );
            point.set( mvt.viewToModel( pView ) );
        }

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            // snap to the grid
            point.set( new ImmutableVector2D( MathUtil.round( point.get().getX() ), MathUtil.round( point.get().getY() ) ) );
        }

        @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return new ParameterSet().
                    with( ParameterKeys.x, COORDINATES_FORMAT.format( point.get().getX() ) ).
                    with( ParameterKeys.y, COORDINATES_FORMAT.format( point.get().getY() ) ).
                    with( super.getParametersForAllEvents( event ) );
        }

        // Constrains xy view coordinates to be within some view bounds.
        private static ImmutableVector2D constrainToBounds( double x, double y, Rectangle2D dragBounds ) {
            return new ImmutableVector2D( Math.max( dragBounds.getMinX(), Math.min( dragBounds.getMaxX(), x ) ),
                                          Math.max( dragBounds.getMinY(), Math.min( dragBounds.getMaxY(), y ) ) );
        }
    }
}
