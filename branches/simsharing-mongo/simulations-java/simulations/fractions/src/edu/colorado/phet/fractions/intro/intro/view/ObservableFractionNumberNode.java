// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.Font;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class ObservableFractionNumberNode extends PNode {

    public static final Font FONT = new PhetFont( 64 );
    private final PhetPText biggestNumber = new PhetPText( "12", FONT );

    public ObservableFractionNumberNode( final ObservableProperty<Integer> value ) {
        final PhetPText numberText = new PhetPText( value.get() + "", FONT ) {{
            value.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setText( integer + "" );
                    centerFullBoundsOnPoint( biggestNumber.getFullBounds().getCenterX(), biggestNumber.getFullBounds().getCenterY() );
                }
            } );
        }};
        addChild( numberText );
    }
}