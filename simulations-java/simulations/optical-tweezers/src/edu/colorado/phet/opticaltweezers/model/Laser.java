/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.opticaltweezers.util.Vector2D;

/**
 * Laser is the model of the laser.
 * Position indicates where the center of the trap is located.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Laser extends MovableObject implements ModelElement {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_POWER = "power";
    public static final String PROPERTY_RUNNING = "running";
    
    // fudge factor for potential energy model
    private static final double ALPHA = 1; // nm^2/sec
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _running;
    private final double _diameterAtObjective; // nm
    private final double _diameterAtWaist; // nm
    private final double _distanceFromObjectiveToWaist;
    private final double _distanceFromObjectiveToControlPanel; // nm
    private final double _wavelength; // nm
    private final double _visibleWavelength; // nm
    private double _power; // mW
    private final DoubleRange _powerRange; // mW
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param position position of the laser's objective (nm)
     * @param orientation orientation of the beam (radians)
     * @param diameterAtObjective diameter of the beam at the objective (nm)
     * @param diameterAtWaist diameter at the waist of the beam (nm)
     * @param distanceFromObjectiveToWaist distance from the objective to the waist (nm)
     * @param distanceFromObjectiveToControlPanel distance from objective to control panel (nm)
     * @param wavelength wavelength (nm)
     * @param visibleWavelength wavelength (nm) to use in views, since actual wavelength is likely IR
     * @param power power (mW)
     */
    public Laser( Point2D position, double orientation, 
            double diameterAtObjective, double diameterAtWaist, double distanceFromObjectiveToWaist, double distanceFromObjectiveToControlPanel,
            double wavelength, double visibleWavelength, DoubleRange powerRange ) {
        super( position, orientation, 0 /* speed */ );
        
        _running = false;
        _diameterAtObjective = diameterAtObjective;
        _diameterAtWaist = diameterAtWaist;
        _distanceFromObjectiveToWaist = distanceFromObjectiveToWaist;
        _distanceFromObjectiveToControlPanel = distanceFromObjectiveToControlPanel;
        _wavelength = wavelength;
        _visibleWavelength = visibleWavelength;
        _powerRange = new DoubleRange( powerRange );
        
        reset();
    }
    
    /**
     * Resets the model to its initial state.
     */
    public void reset() {
        setPower( _powerRange.getDefault() );
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    /**
     * Turns the laser on and off.
     * 
     * @param running true or false
     */
    public void setRunning( boolean running ) {
        if ( running != _running ) {
            _running = running;
            notifyObservers( PROPERTY_RUNNING );
        }
    }
    
    /**
     * Is the laser running?
     * 
     * @return true or false
     */
    public boolean isRunning() {
        return _running;
    }
    
    /**
     * Gets the laser's diameter at the microscope objective.
     * 
     * @return nm
     */
    public double getDiameterAtObjective() {
        return _diameterAtObjective;
    }
    
    /**
     * Gets the laser's diameter at the trap waist.
     * 
     * @return nm
     */
    public double getDiameterAtWaist() {
        return _diameterAtWaist;
    }

    /**
     * Gets the distance from the microscope objective to the trap waist.
     * 
     * @return nm
     */
    public double getDistanceFromObjectiveToWaist() {
        return _distanceFromObjectiveToWaist;
    }
    
    /**
     * Gets the distance from the microscope objective to the control panel.
     * 
     * @return nm
     */
    public double getDistanceFromObjectiveToControlPanel() {
        return _distanceFromObjectiveToControlPanel;
    }
    
    /**
     * Gets the laser's wavelength.
     * 
     * @return nm
     */
    public double getWavelength() {
        return _wavelength;
    }
    
    /**
     * Gets the wavelength that used to display the laser in the view.
     * The laser's actual wavelength is likely not in the visible spectrum,
     * so this wavelength should be a visible wavelength.
     * 
     * @return nm
     */
    public double getVisibleWavelength() {
        return _visibleWavelength;
    }
    
    /**
     * Gets the laser's power.
     * 
     * @return mW
     */
    public double getPower() {
        return _power;
    }
    
    /**
     * Sets the laser's power.
     * 
     * @param power power (mW)
     */
    public void setPower( double power ) {
        if ( power < _powerRange.getMin() || power > _powerRange.getMax() ) {
            throw new IllegalArgumentException( "power out of range: " + power );
        }
        if ( power != _power ) {
            _power = power;
            notifyObservers( PROPERTY_POWER );
        }
    }
    
    /**
     * Gets the laser's power range.
     * 
     * @return DoubleRange (mW)
     */
    public DoubleRange getPowerRange() {
        return _powerRange;
    }
    
    //----------------------------------------------------------------------------
    // Radius (shape) model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the beam radius at a specified distance from the center of the waist.
     * The center of the waist is at (0,0).  If we draw a line through the center
     * of the waist, in the direction that the beam is pointing, then z is a 
     * distance on this line.
     * 
     * @param yOffset vertical distance from the center of the waist (nm)
     * @return radius at y (nm)
     */
    public double getRadius( final double yOffset ) {
        final double yOffsetAbs = Math.abs( yOffset );
        final double r0 = _diameterAtWaist / 2;
        final double rMax = _diameterAtObjective / 2;
        final double zr = ( Math.PI * r0 * r0 ) / _wavelength;
        final double A = ( _distanceFromObjectiveToWaist / zr ) / Math.sqrt( ( ( rMax / r0 ) * ( rMax / r0 ) ) - 1 );
        final double t1 = yOffsetAbs / ( A * zr );
        return r0 * Math.sqrt( 1 + ( t1 * t1 ) );
    }
    
    /**
     * Is a specified point inside the laser beam's shape?
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm)
     * @return true or false
     */
    public boolean contains( final double x, final double y ) {
        assert( getOrientation() == Math.toRadians( -90 ) ); // laser beam must point up
        boolean b = false;
        final double xOffset = x - getX();
        final double yOffset = y - getY();
        // Is y on the out-going side of the objective?
        if ( yOffset <= _distanceFromObjectiveToWaist ) {
            final double radius = getRadius( yOffset );
            if ( radius <= Math.abs( xOffset ) ) {
                b = true;
            }
        }
        return b;
    }
    
    //----------------------------------------------------------------------------
    // Intensity model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the intensity at a point.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm)
     * @return intensity (mW/nm^2)
     */
    public double getIntensity( final double x, final double y ) {
        final double power = ( _running ) ? _power : 0;
        return getIntensity( x, y, power );
    }

    /**
     * Gets the intensity at a point for a specific power.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm)
     * @param power power (mW)
     * @return intensity (mW/nm^2)
     */
    public double getIntensity( final double x, final double y, final double power ) {
        final double xOffset = x - getX();
        final double yOffset = y - getY();
        final double radius = getRadius( yOffset );
        return getIntensityOnRadius( xOffset, radius, power ); 
    }
    
    /**
     * Gets the intensity for a specified location, radius and power.
     * The radius can be calculated using getBeamRadiusAt.
     * 
     * @param xOffset horizontal distance from the center of the waist (nm)
     * @param radius beam radius (nm)
     * @param power power (mW)
     * @return intensity (mW/nm^2)
     */
    public static double getIntensityOnRadius( final double xOffset, final double radius, final double power ) {
        if ( radius <= 0 ) {
            throw new IllegalArgumentException( "radius must be > 0: " + radius );
        }
        if ( power < 0 ) {
            throw new IllegalArgumentException( "power must be >= 0 : " + power );
        }
        final double t1 = power / ( Math.PI * ( ( radius * radius ) / 2 ) );
        final double t2 = Math.exp( ( -2 * xOffset * xOffset ) / ( radius * radius ) );
        return t1 * t2;
    }
    
    /**
     * Gets the maximum intensity of the laser, at the center of the trap with max power.
     * 
     * @return intensity (mW/nm^2)
     */
    public double getMaxIntensity() {
        return getIntensity( getX(), getY(), _powerRange.getMax() );
    }
    
    //----------------------------------------------------------------------------
    // Trap Force model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the trap force at a point.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm) 
     * @param trap force (pN)
     */
    public Vector2D getTrapForce( final double x, final double y ) {
        final double xOffset = x - getX();
        final double yOffset = y - getY();
        final double power = ( _running ) ? _power : 0;
        return getTrapForce( xOffset, yOffset, power );
    }
    
    /**
     * Gets the maximum trap force.
     * 
     * @return trap force (pN)
     */
    public Vector2D getMaxTrapForce() {
        double xOffset = _diameterAtWaist / 4; // halfway between center and edge of waist
        double yOffset = 0;
        double maxPower = _powerRange.getMax();
        return getTrapForce( xOffset, yOffset, maxPower );
    }
    
    /*
     * Gets the trap force for at a specific offset from the center of the 
     * trap, and a specific power value.
     * 
     * @param xOffset horizontal distance from the center of the trap  (nm)
     * @param yOffset vertical distance from the center of the trap (nm)
     * @param power a power value (mW)
     * @return trap force (pN)
     */
    private Vector2D getTrapForce( final double xOffset, final double yOffset, final double power ) {
        if ( power < 0 ) {
            throw new IllegalArgumentException( "power must be >= 0 : " + power );
        }
        
        final double ry = getRadius( yOffset ); // radius at yOffset, nm
        final double intensity = getIntensityOnRadius( xOffset, ry, power );
        
        // x component
        final double Kx = 582771.6; // provided by Tom Perkins
        final double fx = -1 * Kx * ( xOffset / ( ry * ry ) ) * intensity;

        // y component
        final double Ky = Kx / 5.6;
        final double r0 = getRadius( 0 ); // radius at waist, nm
        final double fy = -1 * Ky * ( yOffset / ( r0 * r0 ) ) * intensity;

        return new Vector2D.Cartesian( fx, fy );
    }
    
    //----------------------------------------------------------------------------
    // Potential Energy model
    //----------------------------------------------------------------------------
    
    /**
     * Gets the potential energy at a point.
     * 
     * @param x coordinate relative to global model origin (nm)
     * @param y coordinate relative to global model origin (nm) 
     * @return potential energy (mJ)
     */
    public double getPotentialEnergy( final double x, final double y ) {
        final double power = ( _running ) ? _power : 0;
        return getPotentialEnergy( x, y, power );
    }
    
    /**
     * Gets the potential energy at a point for a hypothetical power value.
     * 
     * @param x
     * @param y
     * @param power
     * @return potential energy (mJ)
     */
    private double getPotentialEnergy( final double x, final double y, final double power ) {
        return -1 * ALPHA * getIntensity( x, y, power );
    }
    
    /**
     * Gets the minimum potential energy for the laser, at the center of the trap with max power..
     * 
     * @return minimum potential energy (mJ)
     */
    public double getMinPotentialEnergy() {
        return getPotentialEnergy( getX(), getY(), _powerRange.getMax() );
    }
    
    /**
     * Gets the maximum potential energy for the laser.
     * The maximum potential energy will approach zero.
     * 
     * @return 0 (mJ)
     */
    public double getMaxPotentialEnergy() {
        return 0;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    public void stepInTime( double dt ) {
        // do nothing
    }
}
