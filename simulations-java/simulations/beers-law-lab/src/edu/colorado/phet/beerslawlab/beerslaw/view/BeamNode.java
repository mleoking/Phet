// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.beerslawlab.beerslaw.model.Beam;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Representation of light as a beam.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeamNode extends PhetPNode {

    public BeamNode( final Beam beam, ModelViewTransform mvt ) {

        setPickable( false );
        setChildrenPickable( false );

        final PNode parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( new SegmentNode( beam.leftShape, beam.leftPaint, mvt ) );
        parentNode.addChild( new SegmentNode( beam.centerShape, beam.centerPaint, mvt ) );
        parentNode.addChild( new SegmentNode( beam.rightShape, beam.rightPaint, mvt ) );

        // Make this node visible when beam is visible.
        RichSimpleObserver visibilityObserver = new RichSimpleObserver() {
            @Override public void update() {
                setVisible( beam.visible.get() );
            }
        };
        visibilityObserver.observe( beam.visible );

        // put a gray outline around the beam, so that lighter colors are still visible
        PPath outlineNode = new PPath() {{
            setStroke( new BasicStroke( 0.25f ) );
            setStrokePaint( ColorUtils.createColor( Color.LIGHT_GRAY, 200 ) );
            parentNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setPathTo( parentNode.getFullBoundsReference() );
                }
            } );
        }};
        addChild( outlineNode );
    }

    // A segment of the beam
    private static class SegmentNode extends PPath {

        public SegmentNode( Property<ImmutableRectangle2D> shape, Property<Paint> paint, final ModelViewTransform mvt ) {
            setStroke( new BasicStroke( 0.25f ) );

            // Shape
            shape.addObserver( new VoidFunction1<ImmutableRectangle2D>() {
                public void apply( ImmutableRectangle2D r ) {
                    setPathTo( mvt.modelToView( r ).toRectangle2D() );
                }
            } );

            // Paint
            paint.addObserver( new VoidFunction1<Paint>() {
                public void apply( Paint paint ) {
                    setBeamPaint( paint );
                }
            } );
        }

        public void setBeamPaint( Paint paint ) {
            setPaint( paint );
            if ( paint instanceof Color ) {
                setStrokePaint( ColorUtils.darkerColor( (Color)paint, 0.5 ) ); // use a darker color for Color strokes, preserve alpha
            }
            else {
                setStrokePaint( null );
            }
        }
    }
}
