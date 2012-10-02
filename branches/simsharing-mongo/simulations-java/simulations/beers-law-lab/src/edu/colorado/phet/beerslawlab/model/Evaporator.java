// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * The thing that is used to evaporate solvent from the solution in the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Evaporator implements Resettable {

    public final double maxEvaporationRate; // L/sec
    public final Property<Double> evaporationRate; // L/sec
    public final Property<Boolean> enabled;

    public Evaporator( double maxEvaporationRate, final Solution solution ) {

        this.maxEvaporationRate = maxEvaporationRate;
        this.evaporationRate = new Property<Double>( 0d );
        this.enabled = new Property<Boolean>( true );

        solution.volume.addObserver( new SimpleObserver() {
            public void update() {
                enabled.set( solution.volume.get() > 0 );
            }
        } );

        // when disabled, set the rate to zero
        enabled.addObserver( new SimpleObserver() {
            public void update() {
                if ( !enabled.get() ) {
                    evaporationRate.set( 0d );
                }
            }
        } );
    }

    public void reset() {
        evaporationRate.reset();
        enabled.reset();
    }
}