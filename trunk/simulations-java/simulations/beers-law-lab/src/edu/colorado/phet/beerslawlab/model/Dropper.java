// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the dropper, contains solute in solution form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Dropper extends Movable {

    public final Property<Solute> solute;
    public final Property<Boolean> on; // true if the dropper is dispensing solution
    public final Property<Boolean> enabled;
    private final Property<Double> flowRate;

    public Dropper( ImmutableVector2D location, PBounds bounds, Property<Solute> solute, final double maxFlowRate ) {
        super( location, bounds );
        assert ( bounds.contains( location.toPoint2D() ) );

        this.solute = solute;
        this.on = new Property<Boolean>( false );
        this.enabled = new Property<Boolean>( true );
        this.flowRate = new Property<Double>( 0d );

        // turn off the dropper when it's disabled
        this.enabled.addObserver( new SimpleObserver() {
            public void update() {
                if ( !Dropper.this.enabled.get() ) {
                    on.set( false );
                }
            }
        } );

        // Toggle the flow rate when the dropper is turned on/off.
        this.on.addObserver( new SimpleObserver() {
            public void update() {
                if ( Dropper.this.on.get() ) {
                    flowRate.set( maxFlowRate );
                }
                else {
                    flowRate.set( 0d );
                }
            }
        } );
    }

    public double getFlowRate() {
        return flowRate.get();
    }

    public void addFlowRateObserver( SimpleObserver observer ) {
        flowRate.addObserver( observer );
    }
}
