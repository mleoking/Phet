// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view.molecules;

import edu.colorado.phet.balancingchemicalequations.model.Atom.C;
import edu.colorado.phet.balancingchemicalequations.model.Atom.H;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.BigAtomNode;
import edu.colorado.phet.balancingchemicalequations.view.molecules.AtomNode.SmallAtomNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * C2H4 molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class C2H4Node extends PComposite {

    public C2H4Node() {

        AtomNode atomBigLeft = new BigAtomNode( new C() );
        AtomNode atomBigRight = new BigAtomNode( new C() );
        AtomNode atomSmallTopLeft = new SmallAtomNode( new H() );
        AtomNode atomSmallTopRight = new SmallAtomNode( new H() );
        AtomNode atomSmallBottomLeft = new SmallAtomNode( new H() );
        AtomNode atomSmallBottomRight = new SmallAtomNode( new H() );

        // rendering order
        PComposite parentNode = new PComposite();
        addChild( parentNode );
        parentNode.addChild( atomSmallTopRight );
        parentNode.addChild( atomSmallBottomLeft );
        parentNode.addChild( atomBigRight );
        parentNode.addChild( atomBigLeft );
        parentNode.addChild( atomSmallTopLeft );
        parentNode.addChild( atomSmallBottomRight );

        // layout
        final double offsetSmall = atomSmallTopLeft.getFullBoundsReference().getWidth() / 4;
        double x = 0;
        double y = 0;
        atomBigLeft.setOffset( x, y );
        x = atomBigLeft.getFullBoundsReference().getMaxX() + ( 0.25 * atomBigRight.getFullBoundsReference().getWidth() );
        y = atomBigLeft.getYOffset();
        atomBigRight.setOffset( x, y );
        x = atomBigLeft.getFullBoundsReference().getMinX() + offsetSmall;
        y = atomBigLeft.getFullBoundsReference().getMinY() + offsetSmall;
        atomSmallTopLeft.setOffset( x, y );
        x = atomBigRight.getFullBoundsReference().getMaxX() - offsetSmall;
        y = atomBigRight.getFullBoundsReference().getMinY() + offsetSmall;
        atomSmallTopRight.setOffset( x, y );
        x = atomBigLeft.getFullBoundsReference().getMinX() + offsetSmall;
        y = atomBigLeft.getFullBoundsReference().getMaxY() - offsetSmall;
        atomSmallBottomLeft.setOffset( x, y );
        x = atomBigRight.getFullBoundsReference().getMaxX() - offsetSmall;
        y = atomBigRight.getFullBoundsReference().getMaxY() - offsetSmall;
        atomSmallBottomRight.setOffset( x, y );

        // move origin to geometric center
        parentNode.setOffset( -PNodeLayoutUtils.getOriginXOffset( parentNode ), -PNodeLayoutUtils.getOriginYOffset( parentNode ) );
    }
}
