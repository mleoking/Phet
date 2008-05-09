/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.glaciers.model.Climate.ClimateAdapter;
import edu.colorado.phet.glaciers.model.Climate.ClimateListener;
import edu.colorado.phet.glaciers.view.MountainsAndValleyNode;

/**
 * Thermometer is the model of a thermometer.
 * It measure temperature of the climate at an elevation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Thermometer extends AbstractTool {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Glacier _glacier;
    private ClimateListener _climateListener;
    private double _temperature; // units=Celcius
    private ArrayList _listeners; // list of ThermometerListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public Thermometer( Point2D position, Glacier glacier ) {
        super( position );
        
        _glacier = glacier;
        _climateListener = new ClimateAdapter() {
            public void temperatureChanged() {
                updateTemperature();
            }
        };
        _glacier.getClimate().addClimateListener( _climateListener );
        
        _listeners = new ArrayList();
    }
    
    public void cleanup() {
        super.cleanup();
        _glacier.getClimate().removeClimateListener( _climateListener );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    private void setTemperature( double temperature ) {
        if ( temperature != _temperature ) {
            _temperature = temperature;
            notifyTemperatureChanged();
        }
    }
    
    public double getTemperature() {
        return _temperature;
    }
    
    public double getTemperatureFahrenheit() {
        return ( (9./5.) * _temperature ) + 32.;
    }
    
    //----------------------------------------------------------------------------
    // AbstractTool overrides
    //----------------------------------------------------------------------------
    
    /*
     * If the tool is below the surface of the ice, snap it just above the ice.
     */
    protected void constrainDrop() {
        double surfaceElevation = _glacier.getSurfaceElevation( getX() );
        if ( getY() <= surfaceElevation ) {
            setPosition( getX(), surfaceElevation + 100 );
        }
    }
    
    protected void handlePositionChanged() {
        updateTemperature();
    }

    private void updateTemperature() {
        final double elevation = getElevation();
        final double temperature = _glacier.getClimate().getTemperature( elevation );
        setTemperature( temperature );
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    public interface ThermometerListener {
        public void temperatureChanged();
    }

    public void addThermometerListener( ThermometerListener listener ) {
        _listeners.add( listener );
    }

    public void removeThermometerListener( ThermometerListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification of changes
    //----------------------------------------------------------------------------
    
    private void notifyTemperatureChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ThermometerListener) i.next() ).temperatureChanged();
        }
    }
}
