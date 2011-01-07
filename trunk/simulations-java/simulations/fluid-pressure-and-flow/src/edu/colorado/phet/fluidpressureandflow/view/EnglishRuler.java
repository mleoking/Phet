// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class EnglishRuler extends FluidPressureAndFlowRuler {
    public EnglishRuler( ModelViewTransform transform, final ObservableProperty<Boolean> visible, final Property<Boolean> setVisible, Point2D.Double rulerModelOrigin ) {
        super( transform, visible, setVisible, Math.abs( transform.modelToViewDeltaY( Units.FEET.toSI( 10 ) ) ), new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }, "ft", rulerModelOrigin );
    }
}
