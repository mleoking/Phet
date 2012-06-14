// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Drag handler for the slope manipulator, usable with both slope-intercept and point-slope lines.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeDragHandler extends LineManipulatorDragHandler {

    private final Property<DoubleRange> riseRange, runRange;
    private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

    /**
     * Constructor
     * @param userComponent sim-sharing component identifier
     * @param componentType sim-sharing component type
     * @param manipulatorNode the node being manipulated by the user
     * @param mvt  transform between model and view coordinate frames
     * @param line the line being manipulated
     * @param riseRange
     * @param runRange
     */
    public SlopeDragHandler( IUserComponent userComponent, IUserComponentType componentType,
                             LineManipulatorNode manipulatorNode, ModelViewTransform mvt, Property<StraightLine> line,
                             Property<DoubleRange> riseRange,
                             Property<DoubleRange> runRange ) {
        super( userComponent, componentType, manipulatorNode, mvt, line );
        this.riseRange = riseRange;
        this.runRange = runRange;
    }

    @Override protected void startDrag( PInputEvent event ) {
        super.startDrag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        clickXOffset = pMouse.getX() - mvt.modelToViewDeltaX( line.get().x1 + line.get().run );
        clickYOffset = pMouse.getY() - mvt.modelToViewDeltaY( line.get().y1 + line.get().rise );
    }

    @Override protected void drag( PInputEvent event ) {
        super.drag( event );
        Point2D pMouse = event.getPositionRelativeTo( manipulatorNode.getParent() );
        double run = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaX( pMouse.getX() - clickXOffset ) - line.get().x1, runRange.get() ) );
        double rise = MathUtil.roundHalfUp( MathUtil.clamp( mvt.viewToModelDeltaY( pMouse.getY() - clickYOffset ) - line.get().y1, riseRange.get() ) );
        if ( !( run == 0 && rise == 0 ) ) {
            // avoid slope=0/0
            updateLine( rise, run, line.get().x1, line.get().y1 );
        }
    }
}
