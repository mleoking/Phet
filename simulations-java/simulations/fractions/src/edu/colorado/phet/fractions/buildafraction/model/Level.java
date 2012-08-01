// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

/**
 * Base class for different kinds of levels (build with numbers or build with shapes).
 *
 * @author Sam Reid
 */
public class Level {

    //Fractions the user has created in the play area, which may match a target
    public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );

    //Number of targets the user must match, usually 3 or 4
    public final int numTargets;

    //For keeping score
    public final IntegerProperty filledTargets = new IntegerProperty( 0 );

    public Level( final int numTargets ) { this.numTargets = numTargets; }

    public void resetAll() {
        createdFractions.reset();
        filledTargets.reset();
    }

    public void incrementFilledTargets() { filledTargets.increment(); }

    public void dispose() { createdFractions.removeAllObservers(); }
}