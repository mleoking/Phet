// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Model element for the Stored Energy meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StoredEnergyMeter extends BarMeter {

    public StoredEnergyMeter( ICircuit circuit, WorldBounds worldBounds, Point3D location, boolean visible ) {
        super( circuit, worldBounds, location, visible, new Function1<ICircuit, Double>() {
            public Double apply( ICircuit circuit ) {
                return circuit.getStoredEnergy();
            }
        } );
    }
}
