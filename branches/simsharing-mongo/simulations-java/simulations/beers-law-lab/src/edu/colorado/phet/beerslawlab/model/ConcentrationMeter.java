// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model of the concentration meter.
 * <p/>
 * NOTE: Determining when the probe is in one of the various fluids is handled in the view,
 * where testing node intersections simplifies the process. Otherwise we'd need to
 * model the shapes of the various fluids, an unnecessary complication.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationMeter implements Resettable {

    private final Property<Double> value;
    public final Movable body;
    public final Movable probe;

    public ConcentrationMeter( ImmutableVector2D bodyLocation, PBounds bodyDragBounds,
                               ImmutableVector2D probeLocation, PBounds probeDragBounds ) {
        this.value = new Property<Double>( null );
        this.body = new Movable( bodyLocation, bodyDragBounds );
        this.probe = new Movable( probeLocation, probeDragBounds );
    }

    public void setValue( Double value ) {
        this.value.set( value );
    }

    // Gets the value to be displayed by the meter, null if the meter is not reading a value.
    public Double getValue() {
        return value.get();
    }

    public void addValueObserver( SimpleObserver observer ) {
        value.addObserver( observer );
    }

    public void reset() {
        this.value.reset();
        this.body.reset();
        this.probe.reset();
    }
}