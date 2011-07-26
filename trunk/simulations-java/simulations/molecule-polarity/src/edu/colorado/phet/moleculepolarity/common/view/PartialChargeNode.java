// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.MPConstants;
import edu.colorado.phet.moleculepolarity.common.model.Bond;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of partial charge, a delta followed by either + or -.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PartialChargeNode extends PComposite {

    private static final char DELTA = '\u03B4';

    private static final double REF_MAGNITUDE = MPConstants.ELECTRONEGATIVITY_RANGE.getLength();
    private static final double REF_SCALE = 1;

    public PartialChargeNode( Bond bond, final boolean positivePolarity ) {

        final PText textNode = new PText() {{
            setFont( new PhetFont( 40 ) );
            setTextPaint( Color.BLACK );
        }};
        addChild( textNode );

        bond.dipoleMagnitude.addObserver( new VoidFunction1<Double>() {
            public void apply( Double magnitude ) {

                textNode.setVisible( magnitude != 0 ); // invisible if dipole is zero

                if ( magnitude != 0 ) {

                    // d+ or d-
                    boolean negative = ( !positivePolarity && magnitude < 0 ) || ( positivePolarity && magnitude > 0 );
                    if ( negative ) {
                        textNode.setText( DELTA + "-" );
                    }
                    else {
                        textNode.setText( DELTA + "+" );
                    }

                    // size proportional to bond dipole magnitude
                    final double scale = Math.abs( REF_SCALE * magnitude / REF_MAGNITUDE );
                    if ( scale != 0 ) {
                        textNode.setScale( scale );
                        textNode.setOffset( -textNode.getFullBoundsReference().getWidth() / 2, -textNode.getFullBoundsReference().getHeight() / 2 ); // origin at center
                    }
                }
            }
        } );
    }
}
