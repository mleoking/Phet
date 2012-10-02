// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.fractions.common.view.SpinnerButtonPanel;

/**
 * Node that shows a single number (numerator or denominator) and a control to change the value within a limiting range.
 *
 * @author Sam Reid
 */
public class NumeratorWithSpinner extends FractionNumberNode {
    //Allow numerator to go to 0
    private static final int MIN_VALUE = 0;

    public NumeratorWithSpinner( final IntegerProperty numerator, IntegerProperty denominator ) {
        super( numerator );

//        Max amount of things will be 6
//        Max numerator will be a function of the denominator
//        n / d <= 6 , so n<=6d
        //Limit the numerator based on the denominator so that the entire number is less than or equal to 6
        addChild( new SpinnerButtonPanel( new VoidFunction0() {
            public void apply() {
                numerator.set( numerator.get() + 1 );
            }
        }, numerator.lessThan( denominator.times( 6 ) ), new VoidFunction0() {
            public void apply() {
                numerator.set( numerator.get() - 1 );
            }
        }, numerator.greaterThanOrEqualTo( MIN_VALUE + 1 )
        ) {{
            setOffset( biggestNumber.getFullBounds().getMinX() - getFullBounds().getWidth(), biggestNumber.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
    }
}