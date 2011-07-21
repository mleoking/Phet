// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Beaker;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.CalciumIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.ChlorideIon;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroShaker;

/**
 * This shaker adds calcium chloride to the model when shaken
 *
 * @author Sam Reid
 */
public class CalciumChlorideShaker extends MicroShaker {
    public CalciumChlorideShaker( double x, double y, Beaker beaker, ObservableProperty<Boolean> moreAllowed, String name, double distanceScale, ObservableProperty<DispenserType> selectedType, DispenserType type ) {
        super( x, y, beaker, moreAllowed, name, distanceScale, selectedType, type );
    }

    @Override protected void addCrystal( MicroModel model, ImmutableVector2D outputPoint, double volumePerSolidMole, ImmutableVector2D crystalVelocity ) {
        model.addCalciumChlorideCrystal( new CalciumChlorideCrystal( outputPoint, (CalciumChlorideLattice) new CalciumChlorideLattice().grow( 20 ) ) );

        model.addCalciumChlorideCrystal( new CalciumChlorideCrystal( outputPoint,
                                                                     generateRandomLattice( new Function0<CalciumChlorideLattice>() {
                                                                                                public CalciumChlorideLattice apply() {

                                                                                                    //TODO: can we get rid of this cast?
                                                                                                    return (CalciumChlorideLattice) new CalciumChlorideLattice().grow( 19 );
                                                                                                }
                                                                                            }, new Function1<CalciumChlorideLattice, Boolean>() {
                                                                         public Boolean apply( CalciumChlorideLattice lattice ) {
                                                                             return lattice.count( CalciumIon.class ) == lattice.count( ChlorideIon.class );
                                                                         }
                                                                     }
                                                                     ) ) );
    }
}