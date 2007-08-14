/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.persistence;

import edu.colorado.phet.opticaltweezers.defaults.GlobalDefaults;

/**
 * DNAConfig is a JavaBean-compliant data structure that stores
 * configuration information related to DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAConfig implements OTSerializable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Module
    private boolean _active; // is the module active?
    
    // Clock
    private boolean _clockRunning;
    private double _clockDt;
    
    // Laser
    private double _laserX;
    private boolean _laserRunning;
    private double _laserPower;
    
    // Bead
    private double _beadX, _beadY;
    
    // Fluid
    private double _fluidSpeed;
    private double _fluidViscosity;
    private double _fluidTemperature;
    
    // Control panel settings
    private boolean _trapForceSelected;
    private boolean _dragForceSelected;
    private boolean _dnaForceSelected;
    private boolean _positionHistogramSelected;
    private boolean _potentialEnergySelected;
    private boolean _rulerSelected;
    private boolean _fluidControlsSelected;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public DNAConfig() {}
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public boolean isActive() {
        return _active;
    }

    
    public void setActive( boolean active ) {
        _active = active;
    }
    
    
    public boolean isClockRunning() {
        return _clockRunning;
    }

    
    public void setClockRunning( boolean clockRunning ) {
        _clockRunning = clockRunning;
    }

    
    public double getBeadX() {
        return _beadX;
    }

    
    public void setBeadX( double beadX ) {
        _beadX = beadX;
    }

    
    public double getBeadY() {
        return _beadY;
    }

    
    public void setBeadY( double beadY ) {
        _beadY = beadY;
    }

    
    public double getClockDt() {
        return _clockDt;
    }

    
    public void setClockDt( double clockDt ) {
        if ( clockDt < GlobalDefaults.SLOW_DT_RANGE.getMin() || clockDt > GlobalDefaults.FAST_DT_RANGE.getMax() ) {
            System.err.println( "WARNING: clockDt (" + clockDt + ") is out of range, default will be used" );
            clockDt = GlobalDefaults.DEFAULT_DT;
        }
        else {
            _clockDt = clockDt;
        }
    }

    
    public boolean isFluidControlsSelected() {
        return _fluidControlsSelected;
    }

    
    public void setFluidControlsSelected( boolean fluidControlsVisible ) {
        _fluidControlsSelected = fluidControlsVisible;
    }

    
    public boolean isDragForceSelected() {
        return _dragForceSelected;
    }

    
    public void setDragForceSelected( boolean fluidDragForceVisible ) {
        _dragForceSelected = fluidDragForceVisible;
    }

    
    public double getFluidSpeed() {
        return _fluidSpeed;
    }

    
    public void setFluidSpeed( double fluidSpeed ) {
        if ( !GlobalDefaults.FLUID_SPEED_RANGE.contains( fluidSpeed ) ) {
            System.err.println( "WARNING: fluidSpeed (" + fluidSpeed + ") is out of range, default will be used" );
            _fluidSpeed = GlobalDefaults.FLUID_SPEED_RANGE.getDefault();
        }
        else {
            _fluidSpeed = fluidSpeed;
        }
    }

    
    public double getFluidTemperature() {
        return _fluidTemperature;
    }

    
    public void setFluidTemperature( double fluidTemperature ) {
        if ( !GlobalDefaults.FLUID_TEMPERATURE_RANGE.contains( fluidTemperature ) ) {
            System.err.println( "WARNING: fluidTemperature (" + fluidTemperature + ") is out of range, default will be used" );
            _fluidTemperature = GlobalDefaults.FLUID_TEMPERATURE_RANGE.getDefault();
        }
        else {
            _fluidTemperature = fluidTemperature;
        }
    }

    
    public double getFluidViscosity() {
        return _fluidViscosity;
    }

    
    public void setFluidViscosity( double fluidViscosity ) {
        if ( !GlobalDefaults.FLUID_VISCOSITY_RANGE.contains( fluidViscosity ) ) {
            System.err.println( "WARNING: fluidViscosity ("  + fluidViscosity + ") is out of range, default will be used" );
            _fluidViscosity = GlobalDefaults.FLUID_VISCOSITY_RANGE.getDefault();
        }
        else {
            _fluidViscosity = fluidViscosity;
        }
    }
    
    public double getLaserPower() {
        return _laserPower;
    }

    
    public void setLaserPower( double laserPower ) {
        if ( !GlobalDefaults.LASER_POWER_RANGE.contains( laserPower ) ) {
            System.err.println( "WARNING: laserPower (" + laserPower + ") is out of range, default will be used" );
            _laserPower = GlobalDefaults.LASER_POWER_RANGE.getDefault();
        }
        else {
            _laserPower = laserPower;
        }
    }

    
    public boolean isLaserRunning() {
        return _laserRunning;
    }

    
    public void setLaserRunning( boolean laserRunning ) {
        _laserRunning = laserRunning;
    }

    
    public double getLaserX() {
        return _laserX;
    }

    
    public void setLaserX( double laserX ) {
        _laserX = laserX;
    }

    
    public boolean isTrapForceSelected() {
        return _trapForceSelected;
    }

    
    public void setTrapForceSelected( boolean opticalTrapForceVisible ) {
        _trapForceSelected = opticalTrapForceVisible;
    }

    
    public boolean isPositionHistogramSelected() {
        return _positionHistogramSelected;
    }

    
    public void setPositionHistogramSelected( boolean positionHistogramVisible ) {
        _positionHistogramSelected = positionHistogramVisible;
    }

    
    public boolean isPotentialEnergySelected() {
        return _potentialEnergySelected;
    }

    
    public void setPotentialEnergySelected( boolean potentialEnergyChartVisible ) {
        _potentialEnergySelected = potentialEnergyChartVisible;
    }

    
    public boolean isRulerSelected() {
        return _rulerSelected;
    }

    
    public void setRulerSelected( boolean rulerVisible ) {
        _rulerSelected = rulerVisible;
    }

    
    public boolean isDnaForceSelected() {
        return _dnaForceSelected;
    }

    
    public void setDnaForceSelected( boolean dnaForceSelected ) {
        _dnaForceSelected = dnaForceSelected;
    }
}
