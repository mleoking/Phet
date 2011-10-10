// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.Color;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.dilutions.control.ShowValuesNode;
import edu.colorado.phet.dilutions.control.SoluteControlNode;
import edu.colorado.phet.dilutions.model.MolarityModel;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for the "Molarity" tab.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MolarityCanvas extends AbstractDilutionsCanvas implements Resettable {

    private static final double BEAKER_HEIGHT = 400; // the height of controls and displays is based on the height of the beaker

    private final Property<Boolean> valuesVisible = new Property<Boolean>( false );

    public MolarityCanvas( MolarityModel model, Frame parentFrame ) {

        // nodes
        SoluteControlNode soluteControlNode = new SoluteControlNode( model.getSolutes(), model.solution.solute );
        ShowValuesNode showValuesNode = new ShowValuesNode( valuesVisible );
        ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable[] { this, model }, parentFrame, 18, Color.BLACK, Color.YELLOW ) {{
            setConfirmationEnabled( false );
        }};
        ConcentrationDisplayNode concentrationDisplayNode = new ConcentrationDisplayNode( new PDimension( 40, BEAKER_HEIGHT ), model.solution, model.getConcentrationRange() );

        // rendering order
        {
            addChild( concentrationDisplayNode );
            addChild( showValuesNode );
            addChild( resetAllButtonNode );
            addChild( soluteControlNode ); // combo box on top
        }

        // layout
        {
            soluteControlNode.setOffset( 100, 100 );
            concentrationDisplayNode.setOffset( 600, 100 );
            showValuesNode.setOffset( concentrationDisplayNode.getFullBoundsReference().getMaxX() - 10,
                                      concentrationDisplayNode.getFullBoundsReference().getMaxY() + 10 );
            resetAllButtonNode.setOffset( showValuesNode.getXOffset(), showValuesNode.getFullBoundsReference().getMaxY() + 10 );
        }
    }

    public void reset() {
        valuesVisible.reset();
    }
}
