/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * Fluid is the model of fluid that contains the glass bead.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Fluid extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final double WATER_VISCOSITY = 1E-3; // Pa*sec
    
    public static final String PROPERTY_VISCOSITY = "viscosity";
    public static final String PROPERTY_TEMPERATURE = "temperature";
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final DoubleRange _speedRange; // nm/sec
    private final DoubleRange _viscosityRange; // Pa*sec
    private final DoubleRange _temperatureRange; // Kelvin
    
    private final double _height; // nm
    private double _viscosity; // Pa*sec
    private double _temperature; // Kelvin
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position at the center of the fluid "stream" (nm)
     * @param orientation direction that the fluid stream flows (radians)
     * @param height height of the fluid stream (nm)
     * @param speedRange speed of the fluid stream (nm/sec)
     * @param viscosityRange (Pa*sec)
     * @param temperatureRange (Kelvin)
     */
    public Fluid( Point2D position, double orientation, double height, 
            DoubleRange speedRange, DoubleRange viscosityRange, DoubleRange temperatureRange ) {
        super( position, orientation, speedRange.getDefault() );
        
        _height = height;
        
        _speedRange = speedRange;
        _viscosityRange = viscosityRange;
        _temperatureRange = temperatureRange;
        
        _viscosity = viscosityRange.getDefault();
        _temperature = temperatureRange.getDefault();
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the height of the fluid.
     * 
     * @return height (nm)
     */
    public double getHeight() {
        return _height;
    }

    /**
     * Gets the minimum y (top) boundary of the fluid.
     * 
     * @return top boundary (nm)
     */
    public double getMinY() {
        return getY() - ( _height / 2 );
    }
    
    /**
     * Gets the maximum y (bottom) boundary of the fluid.
     * 
     * @return bottom boundary (nm)
     */
    public double getMaxY() {
        return getY() + ( _height / 2 );
    }

    /**
     * Gets the fluid speed.
     * 
     * @return speed (nm/sec)
     */
    public double getSpeed() {
        return super.getSpeed();
    }
    
    /**
     * Sets the fluid speed. 
     * This is a scalar quantity because our model only supports left-to-right fluid flow.
     * 
     * @param speed speed (nm/sec)
     */
    public void setSpeed( double speed ) {
        if ( speed < _speedRange.getMin() || speed > _speedRange.getMax() ) {
            throw new IllegalArgumentException( "speed out of range: " + speed );
        }
        super.setSpeed( speed );
    }
    
    /**
     * Get the fluid velocity, a vector describing speed (nm/sec) and orientation (radians).
     * 
     * @return Vector2D
     */
    public Vector2D getVelocity() {
        return new Vector2D( getSpeed(), getOrientation() );
    }
    
    /**
     * Sets the fluid velocity, a vector describing speed (nm/sec) and orientation (radians).
     * 
     * @param Vector2D
     */
    public void setVelocity( Vector2D velocity ) {
        setSpeed( velocity.getMagnitude() );
        setOrientation( velocity.getAngle() );
    }
    
    /**
     * Gets the fluid viscosity.
     * 
     * @return viscosity (Pa*sec)
     */
    public double getViscosity() {
        return _viscosity;
    }
    
    /**
     * Sets the fluid viscosity.
     * 
     * @param viscosity viscosity (Pa*sec)
     */
    public void setViscosity( double viscosity ) {
        if ( viscosity < _viscosityRange.getMin() || viscosity > _viscosityRange.getMax() ) {
            throw new IllegalArgumentException( "viscosity out of range: " + viscosity );
        }
        if ( viscosity != _viscosity ) {
            _viscosity = viscosity;
            notifyObservers( PROPERTY_VISCOSITY );
        }
    }
    
    /**
     * Gets the fluid temperature.
     * 
     * @return temperature (Kelvin)
     */
    public double getTemperature() {
        return _temperature;
    }

    /**
     * Sets the fluid temperature.
     * 
     * @param temperature temperature (Kelvin)
     */
    public void setTemperature( double temperature ) {
        if ( temperature < _temperatureRange.getMin() || temperature > _temperatureRange.getMax() ) {
            throw new IllegalArgumentException( "temperature out of range: " + temperature );
        }
        if ( temperature != _temperature ) {
            _temperature = temperature;
            notifyObservers( PROPERTY_TEMPERATURE );
        }
    }
    
    /**
     * Gets the fluid speed range.
     * 
     * @return DoubleRange (nm/sec)
     */
    public DoubleRange getSpeedRange() {
        return _speedRange;
    }
    
    /**
     * Gets the fluid viscosity range.
     * 
     * @return DoubleRange (Pa*sec)
     */
    public DoubleRange getViscosityRange() {
        return _viscosityRange;
    }
    
    /**
     * Gets the fluid temperature range.
     * 
     * @return DoubleRange (Kelvin)
     */
    public DoubleRange getTemperatureRange() {
        return _temperatureRange;
    }

    //----------------------------------------------------------------------------
    // Drag force model
    //----------------------------------------------------------------------------

    /**
     * Gets the drag force acting on a bead that is moving at a specified velocity.
     * 
     * @param beadVelocity
     * @return drag force (pN)
     */
    public Vector2D getDragForce( Vector2D beadVelocity ) {
        double mobility = getMobility();
        Vector2D velocity = getVelocity();
        double Fx = ( velocity.getX() - beadVelocity.getX() ) / mobility;
        double Fy = ( velocity.getY() - beadVelocity.getY() ) / mobility;
        return new Vector2D( Fx, Fy );
    }
    
    /**
     * Gets mobility.
     * 
     * @return double (nm/sec)/pN
     */
    public double getMobility() {
        double normalizedViscosity = getDimensionlessNormalizedViscosity();
        return ( 600000 / normalizedViscosity ); // (nm/sec)/pN
    }
    
    /**
     * Gets the dimensionless normalized viscosity, where the viscosity of water is 1.
     * 
     * @return double (dimensionless)
     */
    public double getDimensionlessNormalizedViscosity() {
        return _viscosity / WATER_VISCOSITY;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------

    public void stepInTime( double dt ) {
        // do nothing
    }
}
