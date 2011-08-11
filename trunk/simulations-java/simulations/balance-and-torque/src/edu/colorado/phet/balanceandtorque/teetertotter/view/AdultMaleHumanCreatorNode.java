// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.AdultMaleHuman;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents an adult male human in a tool box.  When the user
 * clicks on this node, the corresponding model element is added to the model at
 * the user's mouse location.
 *
 * @author John Blanco
 */
public class AdultMaleHumanCreatorNode extends ImageMassCreatorNode {

    // Model-view transform for scaling the node used in the tool box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 100 );

    public AdultMaleHumanCreatorNode( final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
        ImageMass adultMaleHumanNode = new AdultMaleHuman();
        setSelectionNode( new ImageModelElementNode( SCALING_MVT, adultMaleHumanNode, canvas ) );
        setPositioningOffset( 0, getSelectionNode().getFullBoundsReference().height * 0.75 );
        // TODO: i18n (units)
        setCaption( adultMaleHumanNode.getMass() + " kg" );
    }

    @Override protected ImageMass createImageMassInstance() {
        return new AdultMaleHuman();
    }
}
