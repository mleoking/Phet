/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCirucitChangeListener;
import edu.colorado.phet.common.phetcommon.math.Point3D;

/**
 * Model element for the Stored Energy meter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StoredEnergyMeter extends BarMeter {

    public StoredEnergyMeter( final BatteryCapacitorCircuit circuit, World world, Point3D location, boolean visible ) {
        super( world, location, visible, circuit.getCapacitor().getTotalCapacitance() );
        circuit.addBatteryCapacitorCirucitChangeListener( new BatteryCapacitorCirucitChangeListener() {
            public void circuitChanged() {
                setValue( circuit.getStoredEnergy() );
            }
        } );
    }
}
