// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.beerslawlab.model.PrecipitateParticle;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Visual representation of a precipitate particle.
 * We use the same representation for all solutes, but vary the size and orientation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticleNode extends PPath {

    public PrecipitateParticleNode( PrecipitateParticle particle ) {
        setPaint( particle.getColor() );
        setStrokePaint( particle.getColor().darker() );
        setPathTo( new Rectangle2D.Double( 0, 0, particle.getSize(), particle.getSize() ) ); // square
        setRotation( Math.random() * 2 * Math.PI );
        setOffset( particle.getLocation() );
    }
}
