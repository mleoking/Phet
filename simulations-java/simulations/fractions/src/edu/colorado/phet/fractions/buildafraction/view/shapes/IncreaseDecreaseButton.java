// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.fractions.FractionsResources.Images.*;

/**
 * Button that can be used to add or remove additional containers.
 *
 * @author Sam Reid
 */
class IncreaseDecreaseButton extends PNode {

    private final SingleButton subtractButton;
    private final SingleButton addButton;

    public IncreaseDecreaseButton( final VoidFunction0 add, VoidFunction0 subtract ) {
        subtractButton = new SingleButton( multiScaleToWidth( MINUS_BUTTON, 50 ), multiScaleToWidth( MINUS_BUTTON_PRESSED, 50 ), subtract );
        addButton = new SingleButton( multiScaleToWidth( PLUS_BUTTON, 50 ), multiScaleToWidth( PLUS_BUTTON_PRESSED, 50 ), add );
        addChild( new VBox( addButton, subtractButton ) );
        subtractButton.setTransparency( 0 );
        subtractButton.setAllPickable( false );
    }

    //Hide the increase button.  Return the activity in case client needs to attach a delegate e.g., to listen for completion
    public PInterpolatingActivity hideIncreaseButton() {
        addButton.setAllPickable( false );
        return addButton.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
    }

    //Hide the decrease button.  Return the activity in case client needs to attach a delegate e.g., to listen for completion
    public PInterpolatingActivity hideDecreaseButton() {
        subtractButton.setAllPickable( false );
        return subtractButton.animateToTransparency( 0, BuildAFractionModule.ANIMATION_TIME );
    }

    //Show the increase button.  Return the activity in case client needs to attach a delegate e.g., to listen for completion
    public PInterpolatingActivity showIncreaseButton() {
        addButton.setAllPickable( true );
        return addButton.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
    }

    //Show the decrease button.  Return the activity in case client needs to attach a delegate e.g., to listen for completion
    public PInterpolatingActivity showDecreaseButton() {
        subtractButton.setAllPickable( true );
        return subtractButton.animateToTransparency( 1, BuildAFractionModule.ANIMATION_TIME );
    }

}