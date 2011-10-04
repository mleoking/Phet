// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.flow.model.Particle;
import edu.umd.cs.piccolo.PNode;

import static java.awt.Color.red;

//REVIEW replace with (or subclass) piccolo-phet SphericalNode?

/**
 * Graphic that shows a circular particle.
 *
 * @author Sam Reid
 */
public class ParticleNode extends PNode {
    public ParticleNode( final ModelViewTransform transform, final Particle p ) {
        double viewRadius = transform.modelToViewDeltaX( p.getRadius() );
        addChild( new PhetPPath( new Ellipse2D.Double( -viewRadius, -viewRadius, viewRadius * 2, viewRadius * 2 ), red ) );
        p.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( transform.modelToView( p.getX(), p.getY() ) );
            }
        } );
    }
}