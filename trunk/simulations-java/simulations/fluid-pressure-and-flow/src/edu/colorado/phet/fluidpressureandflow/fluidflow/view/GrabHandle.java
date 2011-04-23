// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Grab handle that lets the user translate the top or bottom of the pipe to deform the pipe.
 *
 * @author Sam Reid
 */
public class GrabHandle extends PNode {
    public GrabHandle( final ModelViewTransform transform, final PipeControlPoint controlPoint, final Function1<Point2D, Point2D> constraint ) {
        double arrowLength = 20;
        addChild( new DoubleArrowNode( new Point2D.Double( 0, -arrowLength ), new Point2D.Double( 0, arrowLength ), 16, 16, 8 ) {{
            setPaint( Color.green );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.black );
            controlPoint.point.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( transform.modelToView( controlPoint.point.getValue().toPoint2D() ) );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new RelativeDragHandler( this, transform, controlPoint.point, constraint ) );
        }} );
    }
}
