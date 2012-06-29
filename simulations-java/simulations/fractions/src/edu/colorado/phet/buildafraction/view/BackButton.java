// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractions.common.view.SpinnerButtonNode;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class BackButton extends PNode {
    public BackButton( final VoidFunction0 pressed ) {
        addChild( new SpinnerButtonNode( scale( Images.LEFT_BUTTON_UP ), scale( Images.LEFT_BUTTON_PRESSED ), scale( Images.LEFT_BUTTON_GRAY ), new VoidFunction1<Boolean>() {
            public void apply( final Boolean spinning ) {
                pressed.apply();
            }
        } ) );
    }

    private BufferedImage scale( final BufferedImage image ) {
        return BufferedImageUtils.multiScaleToWidth( image, 50 );
    }
}