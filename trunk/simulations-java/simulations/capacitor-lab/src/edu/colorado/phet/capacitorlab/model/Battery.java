/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.util.EventListener;

import javax.swing.event.EventListenerList;

/**
 * Simple model of a DC battery.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Battery {

    private double voltage;
    private final EventListenerList listeners;
    
    public Battery( double voltage ) {
        this.voltage = voltage;
        listeners = new EventListenerList();
    }
    
    public void setVoltage( double voltage ) {
        if ( voltage != this.voltage ) {
            double oldVoltage = this.voltage;
            this.voltage = voltage;
            fireVoltageChanged( oldVoltage, voltage );
        }
    }
    
    public double getVoltage() {
        return voltage;
    }
    
    public interface BatteryChangeListener extends EventListener {
        public void voltageChanged( double oldVoltage, double newVoltage );
    }
    
    public void addBatteryChangeListener( BatteryChangeListener listener ) {
        listeners.add( BatteryChangeListener.class, listener );
    }
    
    public void removeBatteryChangeListener( BatteryChangeListener listener ) {
        listeners.remove( BatteryChangeListener.class, listener );
    }
    
    private void fireVoltageChanged( double oldVoltage, double newVoltage ) {
        for ( BatteryChangeListener listener : listeners.getListeners( BatteryChangeListener.class ) ) {
            listener.voltageChanged( oldVoltage, newVoltage );
        }
    }
}
