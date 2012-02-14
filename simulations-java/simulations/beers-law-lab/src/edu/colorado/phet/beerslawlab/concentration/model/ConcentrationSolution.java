// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.model.IFluid;
import edu.colorado.phet.beerslawlab.common.model.Solute;
import edu.colorado.phet.beerslawlab.common.model.Solution;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Solution model for the "Concentration" module.
 * This module has a single solution that is mutated by changing the solute, solute amount, and volume.
 * Concentration is derived via M=mol/L.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationSolution extends Solution implements IFluid, Resettable {

    public final Property<Solute> solute;
    public final Property<Double> soluteAmount; // moles
    public final Property<Double> volume; // L
    public final CompositeProperty<Double> concentration; // M (derived property)
    public final CompositeProperty<Double> precipitateAmount; // moles (derived property)
    public final CompositeProperty<Color> fluidColor; // derived from solute color and concentration

    public ConcentrationSolution( Property<Solute> solute, double soluteAmount, double volume ) {

        this.solute = solute;
        this.soluteAmount = new Property<Double>( soluteAmount );
        this.volume = new Property<Double>( volume );

        // derive concentration
        this.concentration = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                final double volume = ConcentrationSolution.this.volume.get();
                final double soluteAmount = ConcentrationSolution.this.soluteAmount.get();
                if ( volume > 0 ) {
                    return Math.min( getSaturatedConcentration(), soluteAmount / volume ); // M = mol/L
                }
                else {
                    return 0d;
                }
            }
        }, this.solute, this.soluteAmount, this.volume );

        // derive amount of precipitate
        this.precipitateAmount = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                final double volume = ConcentrationSolution.this.volume.get();
                final double soluteAmount = ConcentrationSolution.this.soluteAmount.get();
                if ( volume > 0 ) {
                    return Math.max( 0, volume * ( ( ConcentrationSolution.this.soluteAmount.get() / volume ) - getSaturatedConcentration() ) );
                }
                else {
                    return soluteAmount;
                }
            }
        }, this.solute, this.soluteAmount, this.volume );

        // derive the solution color
        this.fluidColor = new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return createColor( solvent, ConcentrationSolution.this.solute.get(), concentration.get() );
            }
        }, this.solute, this.concentration );
    }

    // Convenience method
    public double getSaturatedConcentration() {
        return solute.get().saturatedConcentration;
    }

    public boolean isSaturated() {
        boolean saturated = false;
        if ( volume.get() > 0 ) {
            saturated = ( soluteAmount.get() / volume.get() ) > getSaturatedConcentration();
        }
        return saturated;
    }

    public Color getFluidColor() {
        return fluidColor.get();
    }

    public void addFluidColorObserver( SimpleObserver observer ) {
        fluidColor.addObserver( observer );
    }

    public int getNumberOfPrecipitateParticles() {
        int numberOfParticles = (int) ( solute.get().particlesPerMole * precipitateAmount.get() );
        if ( numberOfParticles == 0 && precipitateAmount.get() > 0 ) {
            numberOfParticles = 1;
        }
        return numberOfParticles;
    }

    public void reset() {
        soluteAmount.reset();
        volume.reset();
    }
}
