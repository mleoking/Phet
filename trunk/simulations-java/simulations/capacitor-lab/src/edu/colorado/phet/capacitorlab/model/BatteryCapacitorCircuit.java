// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.model;

import java.awt.Shape;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeListener;
import edu.colorado.phet.capacitorlab.model.Wire.BottomWire;
import edu.colorado.phet.capacitorlab.model.Wire.TopWire;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Model of a circuit with a battery connected to a capacitor.
 * <p>
 * Variable names used in this implementation where chosen to match the specification
 * in the design document, and therefore violate Java naming conventions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit implements ICircuit {

    // immutable instance data
    private final CLClock clock;
    private final Wire topWire, bottomWire;
    private final EventListenerList listeners;
    private final Battery battery;
    private final Capacitor capacitor;

    // observable properties
    private Property<Double> currentAmplitudeProperty; // dV/dt, rate of voltage change

    // mutable instance data
    private boolean batteryConnected;
    private double disconnectedPlateCharge; // charge set manually by the user, used when battery is disconnected
    private double previousTotalCharge;

    public BatteryCapacitorCircuit( CLClock clock, final Battery battery, final Capacitor capacitor, boolean batteryConnected, CLModelViewTransform3D mvt ) {

        this.clock = clock;
        this.listeners = new EventListenerList();
        this.battery = battery;
        this.capacitor = capacitor;
        this.batteryConnected = batteryConnected;
        this.disconnectedPlateCharge = getTotalCharge();
        this.previousTotalCharge = getTotalCharge();
        this.currentAmplitudeProperty = new Property<Double>( 0d );

        // update current amplitude on each clock tick
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateCurrentAmplitude();
            }
        });

        // Create the wires
        topWire = new TopWire( battery, capacitor, CLConstants.WIRE_THICKNESS, mvt );
        bottomWire = new BottomWire( battery, capacitor, CLConstants.WIRE_THICKNESS, mvt );

        // observe battery
        battery.addVoltageObserver( new SimpleObserver() {
            public void update() {
                if ( isBatteryConnected() ) {
                    updateVoltages();
                }
            }
        } );

        // observe capacitor
        capacitor.addCapacitorChangeListener( new CapacitorChangeListener() {
            public void capacitorChanged() {
                fireCircuitChanged();
            }
        } );
    }

    //----------------------------------------------------------------------------------
    //
    // Circuit components
    //
    //----------------------------------------------------------------------------------

    public Battery getBattery() {
        return battery;
    }

    public Capacitor getCapacitor() {
        return capacitor;
    }

    public Wire getTopWire() {
        return topWire;
    }

    public Wire getBottomWire() {
        return bottomWire;
    }

    //----------------------------------------------------------------------------------
    //
    // Battery connectivity
    //
    //----------------------------------------------------------------------------------

    /**
     * Is the battery connected to the capacitor?
     * @return
     */
    public boolean isBatteryConnected() {
        return batteryConnected;
    }

    /**
     * Determines whether the battery is connected to the capacitor.
     * @param batteryConnected
     */
    public void setBatteryConnected( boolean batteryConnected ) {
        if ( batteryConnected != this.batteryConnected ) {
            /*
             * When disconnecting the battery, set the disconnected plate charge to
             * whatever the total plate charge was with the battery connected.
             */
            if ( !batteryConnected ) {
                disconnectedPlateCharge = getTotalCharge();
            }
            this.batteryConnected = batteryConnected;
            updateVoltages();
        }
    }

    /*
     * Updates the capacitor and wire voltages, depending on whether the battery is connected.
     */
    private void updateVoltages() {
        double V = battery.getVoltage();
        if ( !batteryConnected ) {
            V = disconnectedPlateCharge / capacitor.getTotalCapacitance(); // V = Q/C
        }
        capacitor.setPlatesVoltage( V );
        topWire.setVoltage( V );
        bottomWire.setVoltage( 0 );
    }

    //----------------------------------------------------------------------------------
    //
    // Plate Voltage (V)
    //
    //----------------------------------------------------------------------------------

    // @see ICircuit.getVoltageBetween
    public double getVoltageBetween( Shape positiveShape, Shape negativeShape ) {
        return getVoltage( positiveShape ) - getVoltage( negativeShape );
    }

    /*
     * Gets the voltage at a Shape.
     * @param p
     * @return
     */
    private double getVoltage( Shape s ) {
        double voltage = Double.NaN;
        if ( isBatteryConnected() && topWire.intersects( s ) ) {
            voltage = topWire.getVoltage();
        }
        else if ( isBatteryConnected() && bottomWire.intersects( s ) ) {
            voltage = bottomWire.getVoltage();
        }
        if ( isBatteryConnected() && battery.intersectsTopTerminal( s ) ) {
            voltage = battery.getVoltage();
        }
        else if ( capacitor.intersectsTopPlateShape( s ) ) {
            voltage = capacitor.getPlatesVoltage();
        }
        else if ( capacitor.intersectsBottomPlateShape( s ) ) {
            voltage = 0;
        }
        return voltage;
    }

    //----------------------------------------------------------------------------------
    //
    // Capacitance
    //
    //----------------------------------------------------------------------------------

    // @see ICircuit.getTotalCapacitance
    public double getTotalCapacitance() {
        return capacitor.getTotalCapacitance();
    }

    //----------------------------------------------------------------------------------
    //
    // Plate Charge (Q)
    //
    //----------------------------------------------------------------------------------

    /**
     * Sets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @param disconnectedPlateCharge Coulombs
     */
    public void setDisconnectedPlateCharge( double disconnectedPlateCharge ) {
        if ( disconnectedPlateCharge != this.disconnectedPlateCharge ) {
            this.disconnectedPlateCharge = disconnectedPlateCharge;
            if ( !isBatteryConnected() ) {
                updateVoltages();
                fireCircuitChanged();
            }
        }
    }

    /**
     * Gets the value used for plate charge when the battery is disconnected.
     * (design doc symbol: Q_total)
     *
     * @return charge, in Coulombs
     */
    public double getDisconnectedPlateCharge() {
        return disconnectedPlateCharge;
    }

    // @see ICircuit.getTotalCharge
    public double getTotalCharge() {
        return capacitor.getTotalPlateCharge();
    }

    //----------------------------------------------------------------------------------
    //
    // E-Field (E)
    //
    //----------------------------------------------------------------------------------

    // @see ICircuit.getEffectiveEFieldAt
    public double getEffectiveEFieldAt( Point3D location ) {
        double eField = 0;
        if ( capacitor.isBetweenPlatesShape( location ) ) {
            eField = capacitor.getEffectiveEfield();
        }
        return eField;
    }

    // @see ICircuit.getPlatesDielectricEFieldAt
    public double getPlatesDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
            eField = capacitor.getPlatesDielectricEField();
        }
        else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
            eField = capacitor.getPlatesAirEField();
        }
        return eField;
    }

    // @see ICircuit.getDielectricEFieldAt
    public double getDielectricEFieldAt( Point3D location ) {
        double eField = 0;
        if ( capacitor.isInsideDielectricBetweenPlatesShape( location ) ) {
   eField = capacitor.getDielectricEField();
        }
        else if ( capacitor.isInsideAirBetweenPlatesShape( location ) ) {
            eField = capacitor.getAirEField();
        }
        return eField;
    }

    //----------------------------------------------------------------------------------
    //
    // Stored Energy (U)
    //
    //----------------------------------------------------------------------------------

    // @see ICircuit.getStoredEnergy
    public double getStoredEnergy() {
        double C_total = capacitor.getTotalCapacitance(); // F
        double V_plates = capacitor.getPlatesVoltage(); // V
        return 0.5 * C_total * V_plates * V_plates; // Joules (J)
    }

    //----------------------------------------------------------------------------------
    //
    // Current
    //
    //----------------------------------------------------------------------------------

    public double getCurrentAmplitude() {
        return currentAmplitudeProperty.getValue();
    }

    /*
     * Current amplitude is proportional to dQ/dt, the change in charge (Q_total) over time.
     */
    private void updateCurrentAmplitude() {
        double Q = getTotalCharge();
        double dQ = Q - previousTotalCharge;
        double dt = clock.getDt();
        previousTotalCharge = Q;
        currentAmplitudeProperty.setValue( dQ / dt );
    }

    public void addCurrentAmplitudeObserver( SimpleObserver o ) {
        currentAmplitudeProperty.addObserver( o );
    }

    //----------------------------------------------------------------------------------
    //
    // CircuitChangeListeners
    //
    //----------------------------------------------------------------------------------

    // @see ICircuit.addCircuitChangeListener
    public void addCircuitChangeListener( CircuitChangeListener listener ) {
        listeners.add( CircuitChangeListener.class, listener );
    }

    // @see ICircuit.removeCircuitChangeListener
    public void removeCircuitChangeListener( CircuitChangeListener listener ) {
        listeners.remove( CircuitChangeListener.class, listener );
    }

    public void fireCircuitChanged() {
        for ( CircuitChangeListener listener : listeners.getListeners( CircuitChangeListener.class ) ) {
            listener.circuitChanged();
        }
    }
}
