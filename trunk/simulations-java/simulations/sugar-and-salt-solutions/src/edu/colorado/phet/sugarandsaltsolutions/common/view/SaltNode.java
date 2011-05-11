// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Crystal;

/**
 * Graphical representation of a salt crystal
 *
 * @author Sam Reid
 */
public class SaltNode extends CrystalNode {
    public SaltNode( final ModelViewTransform transform, final Crystal crystal ) {
        super( transform, crystal );
    }
}
