// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Water;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class WaterNode extends PNode {
    public WaterNode( final ModelViewTransform transform, final Water water ) {
        addChild( new PhetPPath( Color.blue ) {{
            water.volume.addObserver( new VoidFunction0() {
                public void apply() {
                    setPathTo( transform.modelToView( water.getShape() ) );
                }
            } );
        }} );
    }
}
