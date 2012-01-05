// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.beerslawlab.BLLConstants;
import edu.colorado.phet.beerslawlab.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.model.Solution;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;

/**
 * Button that removes all solute from a solution.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RemoveSoluteButtonNode extends TextButtonNode {

    public RemoveSoluteButtonNode( final Solution solution ) {
        super( Strings.REMOVE_SOLUTE, new PhetFont( BLLConstants.CONTROL_FONT_SIZE ), Color.ORANGE );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                solution.soluteAmount.set( 0d );
            }
        } );
        solution.soluteAmount.addObserver( new VoidFunction1<Double>() {
            public void apply( Double soluteAmount ) {
                setEnabled( soluteAmount > 0 );
            }
        } );
    }
}

