// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.*;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * A node that represents a brick in the view.
 *
 * @author John Blanco
 */
public class BrickStackNode extends PNode {
    public BrickStackNode( final ModelViewTransform mvt, final ShapeMass weight ) {
        addChild( new PhetPPath( new Color( 205, 38, 38 ), new BasicStroke( 1 ), Color.BLACK ) {{
            weight.shapeProperty.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {
                    // Set the shape of the node to the scaled shape of the
                    // model element.  Note that this handles changes to
                    // position and rotation as well as what we generally
                    // think of as the "shape".
                    setPathTo( mvt.modelToView( shape ) );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WeightDragHandler( weight, this, mvt ) );
    }
}
