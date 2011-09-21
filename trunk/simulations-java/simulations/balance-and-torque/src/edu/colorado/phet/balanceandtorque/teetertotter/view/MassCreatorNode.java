// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.text.DecimalFormat;
import java.text.Format;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * @author John Blanco
 */
public abstract class MassCreatorNode extends ModelElementCreatorNode {

    private static Format MASS_FORMATTER = new DecimalFormat( "###" );

    public MassCreatorNode( final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas, double mass ) {
        super( model, mvt, canvas );
        // TODO: i18n - units.
        setCaption( MASS_FORMATTER.format( mass ) + " " + "kg" );
    }
}
