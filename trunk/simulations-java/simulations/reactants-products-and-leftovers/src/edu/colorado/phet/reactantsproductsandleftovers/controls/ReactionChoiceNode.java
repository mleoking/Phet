package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModel;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Controls for selecting a real reaction.
 * Gets the collection of real reactions from the model, creates a radio button for each reaction,
 * sets the corresponding reaction when a radio button is selected.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactionChoiceNode extends PhetPNode {
    
    public ReactionChoiceNode( final RealReactionModel model ) {
        super();
        
        JPanel panel = new JPanel();
        panel.setBackground( new Color( 0, 0, 0, 0 ) ); // transparent
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        
        ButtonGroup group = new ButtonGroup();
        final ChemicalReaction[] reactions = model.getReactions();
        for ( int i = 0; i < reactions.length; i++ ) {
            
            final ChemicalReaction reaction = reactions[i];
            
            // radio button
            JRadioButton radioButton = new JRadioButton( reaction.getName() );
            group.add( radioButton );
            layout.addComponent( radioButton, row++, column );
            if ( i == 0 ) {
                radioButton.setSelected( true );
                model.setReaction( reactions[i] );
            }
            
            // set the desired reaction when this radio button is selected
            radioButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.setReaction( reaction );
                }
            } );
        }
        
        addChild( new PSwing( panel ) );
    }
}
