/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial.CustomDielectricChangeListener;

/**
 * Physical model of a circuit with a battery connected to a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryCapacitorCircuit {
    
    // constants
    private static final double E0 = CLConstants.EPSILON_0;
    private static final double E_AIR = CLConstants.EPSILON_AIR;
    private static final double E_VACUUM = CLConstants.EPSILON_VACUUM;
    
    // immutable instance data
    private final EventListenerList listeners;
    private final Battery battery;
    private final Capacitor capacitor;
    
    // mutable instance data
    private boolean batteryConnected;
    private double manualPlateCharge; // charge set manually by the user, used when battery is disconnected
    private CustomDielectricMaterial customDielectric;
    private CustomDielectricChangeListener customDielectricChangeListener;

    public BatteryCapacitorCircuit( Battery battery, Capacitor capacitor, boolean batteryConnected, double manualPlateCharge ) {
        
        listeners = new EventListenerList();
        
        this.batteryConnected = batteryConnected;
        this.manualPlateCharge = manualPlateCharge;
        
        // respond to battery changes
        this.battery = battery;
        battery.addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void voltageChanged() {
                handleBattertVoltageChanged();
            }
        });
        
        // respond to capacitor changes
        this.capacitor = capacitor;
        capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {

            @Override
            public void capacitanceChanged() {
                handleCapacitanceChanged();
            }
            
            @Override
            public void dielectricMaterialChanged() {
                handleDielectricMaterialChanged();
            }
        });
        
        // change listener to be used with custom dielectric materials
        customDielectric = null;
        customDielectricChangeListener = new CustomDielectricChangeListener() {
            public void dielectricConstantChanged() {
                fireCapacitanceChanged();
                fireChargeChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
        };
        updateCustomDielectric();
    }
    
    //----------------------------------------------------------------------------------
    //
    // Circuit components
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the battery that is associated with this circuit.
     * @return
     */
    public Battery getBattery() {
        return battery;
    }
    
    /**
     * Gets the capacitor that is associated with this circuit.
     * @return
     */
    public Capacitor getCapacitor() {
        return capacitor;
    }
    
    //----------------------------------------------------------------------------------
    //
    // Plate Voltage (V)
    //
    //----------------------------------------------------------------------------------

    /**
     * Determines whether the battery is connected to the capacitor.
     * @param connected
     */
    public void setBatteryConnected( boolean connected ) {
        if ( connected != this.batteryConnected ) {
            this.batteryConnected = connected;
            fireBatteryConnectedChanged();
            fireChargeChanged();
            fireEfieldChanged();
            fireEnergyChanged();
        }
    }
    
    /**
     * Is the battery connected to the capacitor?
     * @return
     */
    public boolean isBatteryConnected() {
        return batteryConnected;
    }
    
    /**
     * Gets the plate voltage.
     * If the battery is connected, then the battery voltage is used.
     * If the battery is not connected, then the plate charge is set directly
     * by the user, and voltage is computed.
     * 
     * @return voltage, in volts (V)
     */
    public double getPlateVoltage() {
        double voltage = 0;
        if ( isBatteryConnected() ) {
            voltage = battery.getVoltage();
        }
        else {
            voltage = getManualPlateCharge() / capacitor.getTotalCapacitance();
        }
        return voltage;
    }
    
    //----------------------------------------------------------------------------------
    //
    // Plate Charge (Q)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Sets the value used for plate charge when the battery is disconnected.
     * 
     * @param manualPlateCharge Coulombs
     */
    public void setManualPlateCharge( double manualPlateCharge ) {
        if ( manualPlateCharge != this.manualPlateCharge ) {
            this.manualPlateCharge = manualPlateCharge;
            if ( !isBatteryConnected() ) {
                fireChargeChanged();
                fireVoltageChanged();
                fireEfieldChanged();
                fireEnergyChanged();
            }
        }
    }
    
    /**
     * Gets the value used for plate charge when the battery is disconnected.
     * 
     * @return charge, in Coulombs
     */
    public double getManualPlateCharge() {
        return manualPlateCharge;
    }
    
    /**
     * Gets the charge for the portion of the top plate contacting the air.
     * @return charge, in Coulombs
     */
    public double getAirPlateCharge() {
        return capacitor.getAirCapacitance() * getPlateVoltage();
    }
    
    /**
     * Gets the charge for the portion of the top plate contacting the dielectric.
     * @return charge, in Coulombs
     */
    public double getDielectricPlateCharge() {
        return capacitor.getDieletricCapacitance() * getPlateVoltage();
    }
    
    /**
     * Gets the total charge on the top plate.
     * @return charge, in Coulombs
     */
    public double getTotalPlateCharge() {
        return getDielectricPlateCharge() + getAirPlateCharge();
    }
    
    /**
     * Gets the excess plate charge due to plates contacting air.
     * 
     * @return excess charge, in Coulombs
     */
    public double getExcessAirPlateCharge() {
        double Er = E_AIR;
        double A = capacitor.getAirContactArea();
        double d = capacitor.getPlateSeparation();
        double V = getPlateVoltage();
        return getExcessPlateCharge( Er, A, d, V );
    }
    
    /**
     * Gets the excess plate charge due to plates contacting the dielectric.
     * 
     * @return excess charge, in Coulombs
     */
    public double getExcessDielectricPlateCharge() {
        double Er = capacitor.getDielectricConstant();
        double A = capacitor.getDielectricContactArea();
        double d = capacitor.getPlateSeparation();
        double V = getPlateVoltage();
        return getExcessPlateCharge( Er, A, d, V );
    }
    
    /*
     * General solution for excess plate charge.
     * 
     * @param Er dielectric constant, dimensionless
     * @param A contact area between the top plate and the dielectric, meters^2
     * @param d distance between the plates, meters
     * @param v plate voltage, volts
     * @return charge, in Coulombs (C)
     */
    private static double getExcessPlateCharge( double Er, double A, double d, double V ) {
        return ( Er - E_VACUUM ) * E0 * ( A / d ) * V; // Coulombs (1C = 1F * 1V)
    }
    
    /**
     * Gets the maximum charge on the top plate.
     * Maximum charge occurs when the battery is connected, 
     * plate separate and dielectric offset are at their minimums,
     * and all other quantities are at their maximums.
     * 
     * @return charge, in Coulombs
     */
    public static double getMaxPlateCharge() {
        DielectricMaterial material = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax() );
        Capacitor capacitor = new Capacitor( new Point2D.Double(), 
                CLConstants.PLATE_SIZE_RANGE.getMax(), CLConstants.PLATE_SEPARATION_RANGE.getMin(),
                material, CLConstants.DIELECTRIC_OFFSET_RANGE.getMin() );
        Battery battery = new Battery( new Point2D.Double(), CLConstants.BATTERY_VOLTAGE_RANGE.getMax() );
        BatteryCapacitorCircuit circuit = new BatteryCapacitorCircuit( battery, capacitor, true /* batteryConnected */, 0 /* manualPlateCharge, don't care */ );
        return circuit.getTotalPlateCharge();
    }
    
    //----------------------------------------------------------------------------------
    //
    // E-Field (E)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the effective (net) field between the plates.
     * This is uniform everywhere between the plates.
     * 
     * @return volts/meter
     */
    public double getEffectiveEfield() {
        return getPlateVoltage() / capacitor.getPlateSeparation();
    }
    
    /**
     * Gets the E-field due to the plates alone.
     * 
     * @return E-field, in Volts/meter
     */
    public double getPlatesEField() {
        double Q = getTotalPlateCharge();
        double A = capacitor.getPlateArea();
        return Q / ( A * E0 );
    }
    
    /**
     * Gets the field in capacitor volume that contains air.
     * 
     * @return E-field, in Volts/meter
     */
    public double getAirEField() {
        return getEField( getAirSurfaceDensityCharge(), E_AIR );
    }
    
    /**
     * Gets the field in capacitor volume that contains the dielectric.
     * 
     * @return E-field, in Volts/meter
     */
    public double getDielectricEField() {
        return getEField( getDielectricSurfaceDensityCharge(), capacitor.getDielectricConstant() );
    }
    
    /*
     * General solution for the E-field due to some dielectric.
     * 
     * @param sigma surface charge density, Coulombs/meters^2
     * @param Er dielectric constant, dimensionless
     * @return E-field, in Volts/meter
     */
    private static double getEField( double sigma, double Er ) {
        return sigma / ( Er * E0 );
    }
    
    //----------------------------------------------------------------------------------
    //
    // Surface Density Charge (sigma)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the surface density charge between the plates and the dielectric.
     * @return Coulombs/meters^2
     */
    public double getDielectricSurfaceDensityCharge() {
        double Q = getDielectricPlateCharge(); // Coulombs
        double A = capacitor.getDielectricContactArea(); // meters^2
        return getSurfaceDensityCharge( Q, A );
    }
    
    /**
     * Gets the surface density charge between the plates and air.
     * @return Coulombs/meters^2
     */
    public double getAirSurfaceDensityCharge() {
        double Q = getAirPlateCharge(); // Coulombs
        double A = capacitor.getAirContactArea(); // meters^2
        return getSurfaceDensityCharge( Q, A );
    }
    
    /*
     * General computation of surface charge density.
     * 
     * @param charge charge, in Coulombs
     * @param area area, in meters^2
     * @return Coulombs/meters^2
     */
    private static double getSurfaceDensityCharge( double charge, double area ) {
        double sigma = 0;
        if ( area > 0 ) {
            sigma = charge / area;
        }
        return sigma;
    }
    
    //----------------------------------------------------------------------------------
    //
    // Stored Energy (U)
    //
    //----------------------------------------------------------------------------------
    
    /**
     * Gets the energy stored in the capacitor.
     * @return energy, in joules (J)
     */
    public double getStoredEnergy() {
        double C = capacitor.getTotalCapacitance(); // F
        double V = getPlateVoltage(); // V
        double U = 0.5 * C * V * V; // Joules (J)
        return U;
    }
    
    //----------------------------------------------------------------------------------
    //
    // Model update handlers
    //
    //----------------------------------------------------------------------------------
    
    private void handleBattertVoltageChanged() {
        fireVoltageChanged();
        fireChargeChanged();
        fireEfieldChanged();
        fireEnergyChanged();
    }
    
    private void handleCapacitanceChanged() {
        fireCapacitanceChanged();
        fireChargeChanged();
        fireEfieldChanged();
        fireEnergyChanged();
    }
    
    private void handleDielectricMaterialChanged() {
        updateCustomDielectric();
    }
    
    /*
     * Manages change notification for custom dielectrics.
     */
    private void updateCustomDielectric() {
        // unregister for notification from previous custom dielectric
        if ( customDielectric != null ) {
            customDielectric.removeCustomDielectricChangeListener( customDielectricChangeListener );
            customDielectric = null;
        }
        // register for notification from current custom dielectric
        DielectricMaterial material = capacitor.getDielectricMaterial();
        if ( material instanceof CustomDielectricMaterial ) {
            customDielectric = (CustomDielectricMaterial) material;
            customDielectric.addCustomDielectricChangeListener( customDielectricChangeListener );
        }
    }
    
    //----------------------------------------------------------------------------------
    //
    // Notification mechanisms
    //
    //----------------------------------------------------------------------------------
    
    public interface BatteryCapacitorCircuitChangeListener extends EventListener {
        public void voltageChanged();
        public void capacitanceChanged();
        public void chargeChanged();
        public void efieldChanged();
        public void energyChanged();
        public void batteryConnectedChanged();
    }
    
    public static class BatteryCapacitorCircuitChangeAdapter implements BatteryCapacitorCircuitChangeListener {
        public void voltageChanged() {}
        public void capacitanceChanged() {}
        public void chargeChanged() {}
        public void efieldChanged() {}
        public void energyChanged() {}
        public void batteryConnectedChanged() {}
    }
    
    public void addBatteryCapacitorCircuitChangeListener( BatteryCapacitorCircuitChangeListener listener ) {
        listeners.add(  BatteryCapacitorCircuitChangeListener.class, listener );
    }
    
    public void removeBatteryCapacitorCircuitChangeListener( BatteryCapacitorCircuitChangeListener listener ) {
        listeners.remove(  BatteryCapacitorCircuitChangeListener.class, listener );
    }
    
    public void fireVoltageChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.voltageChanged();
        }
    }
    
    public void fireCapacitanceChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.capacitanceChanged();
        }
    }
    
    public void fireChargeChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.chargeChanged();
        }
    }
    
    public void fireEfieldChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.efieldChanged();
        }
    }
    
    public void fireEnergyChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.energyChanged();
        }
    }
    
    private void fireBatteryConnectedChanged() {
        for ( BatteryCapacitorCircuitChangeListener listener : listeners.getListeners( BatteryCapacitorCircuitChangeListener.class ) ) {
            listener.batteryConnectedChanged();
        }
    }
}
