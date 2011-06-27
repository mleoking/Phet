// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.umd.cs.piccolo.PNode;

import static java.awt.Color.lightGray;

/**
 * @author Sam Reid
 */
public class BeakerNodeWithoutTicks extends PNode {
    public BeakerNodeWithoutTicks( ModelViewTransform transform, Beaker beaker ) {
        addChild( new PhetPPath( transform.modelToView( beaker.getWallShape() ), lightGray ) );
    }
}
