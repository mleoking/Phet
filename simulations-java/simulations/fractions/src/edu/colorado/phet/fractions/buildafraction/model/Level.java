// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.F;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractions.common.math.Fraction;

/**
 * Base class for different kinds of levels (build with numbers or build with shapes).
 *
 * @author Sam Reid
 */
public class Level {

    //Fractions the user has created in the play area, which may match a target
    public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );

    //Targets the user must match, usually 3 or 4 of them
    public final List<MixedFraction> targets;

    //For keeping score, the number of the 3 or 4 collection boxes the user has filled
    public final IntegerProperty filledTargets = new IntegerProperty( 0 );
    public final int numTargets;

    protected Level( final List<MixedFraction> targets ) {
        this.targets = targets;
        this.numTargets = targets.length();
    }

    public void resetAll() {
        createdFractions.reset();
        filledTargets.reset();
    }

    public void dispose() { createdFractions.removeAllObservers(); }

    public void addMatchListener( final BooleanProperty userCreatedMatch ) {
        createdFractions.addObserver( new VoidFunction1<List<Fraction>>() {
            public void apply( final List<Fraction> fractions ) {
                boolean anyMatches = fractions.exists( new F<Fraction, Boolean>() {
                    @Override public Boolean f( final Fraction fraction ) {
                        return targets.exists( new F<MixedFraction, Boolean>() {
                            @Override public Boolean f( final MixedFraction mixedFraction ) {
                                return mixedFraction.approxEquals( fraction );
                            }
                        } );
                    }
                } );
                if ( anyMatches ) { userCreatedMatch.set( true ); }
            }
        } );
    }
}