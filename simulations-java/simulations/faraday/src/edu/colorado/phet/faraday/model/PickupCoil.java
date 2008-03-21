/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.util.Vector2D;


/**
 * PickupCoil is the model of a pickup coil.
 * It's behavior follows Faraday's Law for electromagnetic induction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PickupCoil extends AbstractCoil implements ModelElement, SimpleObserver {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* Number of sample points above the center of the coil. */
    private static final int SAMPLE_POINTS_ABOVE = FaradayConstants.PICKUP_SAMPLE_POINTS / 2;
    /*  Number of sample points below the center of the coil. */
    private static final int SAMPLE_POINTS_BELOW = SAMPLE_POINTS_ABOVE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractMagnet _magnetModel;
    // Determines how the magnetic field decreases with the distance from the magnet.
    private final double _distanceExponent;
    
    private double _flux; // in webers
    private double _deltaFlux; // in webers
    private double _emf; // in volts
    private double _biggestEmf; // in volts
    private ArrayList _samplePoints; // array of Point2D
    
    // Reusable objects
    private AffineTransform _someTransform;
    private Point2D _samplePoint;
    private Vector2D _sampleVector;
    private Vector2D _fieldVector;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param magnetModel the magnet that is affecting the coil
     */
    public PickupCoil( AbstractMagnet magnetModel, double distanceExponent ) {
        super();
        
        assert( magnetModel != null );
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _distanceExponent = distanceExponent;
        
        _flux = 0.0;
        _deltaFlux = 0.0;
        _emf = 0.0;
        _biggestEmf = 0.0;
        
        _samplePoints = new ArrayList();
        
        // Reusable objects
        _someTransform = new AffineTransform();
        _samplePoint = new Point2D.Double();
        _fieldVector = new Vector2D();
        _sampleVector = new Vector2D();
        
        // loosely packed loops
        setLoopSpacing( 1.5 * getWireWidth() );
        
        updateSamplePoints();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the magnetic flux.
     * 
     * @return the flux, in Webers
     */
    public double getFlux() {
        return _flux;
    }
    
    /**
     * Gets the change in magnetic flux.
     * 
     * @return change in magnetic flux, in Webers
     */
    public double getDeltaFlux() {
        return _deltaFlux;
    }
    
    /**
     * Gets the emf.
     * 
     * @return the emf
     */
    public double getEmf() {
        return _emf;
    }
    
    /**
     * Gets the biggest emf that the pickup coil has seen.
     * 
     * @return the biggest emf
     */
    public double getBiggestEmf() {
        return _biggestEmf;
    }
    
    /**
     * Gets the sample points used to calculate emf.
     * 
     * @return
     */
    public Point2D[] getSamplePoints() {
        return (Point2D[]) _samplePoints.toArray( new Point2D[_samplePoints.size()] );
    }
    
    /**
     * When the coil changes, update the sample points.
     */
    public void notifySelf() {
        super.notifySelf();
        updateSamplePoints();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        // Do nothing, handled by stepInTime
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the simulation clock.
     * Calculates the induced emf using Faraday's Law.
     * Performs median smoothing of data if isSmoothingEnabled.
     * 
     * @param dt time delta
     */
    public void stepInTime( double dt ) {
        if ( isEnabled() ) {
            updateEmf( dt );
        }
    }
    
    /*
     * Updates the sample points used to calculate the emf.
     * A fixed number of points is evenly distributed along a vertical line 
     * that goes through the center of the coil.
     */
    private void updateSamplePoints() {
        
        _samplePoints.clear();
        
        // Center point.
        _samplePoints.add( getLocation() );
        
        // all the sample points have the same x coordinate
        final double x = getX();
        
        // Points above the center
        for ( int i = 0; i < SAMPLE_POINTS_ABOVE; i++ ) {
            double y = getY() - ( ( i + 1 ) * ( getRadius() / SAMPLE_POINTS_ABOVE ) );
            _samplePoints.add( new Point2D.Double( x, y ) );
        }
        
        // Points below the center
        for ( int i = 0; i < SAMPLE_POINTS_BELOW; i++ ) {
            double y = getY() + ( ( i + 1 ) * ( getRadius() / SAMPLE_POINTS_BELOW ) );
            _samplePoints.add( new Point2D.Double( x, y ) );
        }
    }
    
    /**
     * Updates the induced emf, using Faraday's Law.
     */
    private void updateEmf( double dt ) {
        
        // Sum the B-field sample points.
        _fieldVector.setMagnitudeAngle( 0, 0 );
        Iterator i = _samplePoints.iterator();
        while ( i.hasNext() ) {
            
            Point2D p = (Point2D)i.next();
            _samplePoint.setLocation( p.getX(), p.getY() );
            if ( getDirection() != 0 ) {
                // Adjust for rotation.
                _someTransform.setToIdentity();
                _someTransform.rotate( getDirection(), getX(), getY() );
                _someTransform.transform( _samplePoint, _samplePoint /* output */);
            }
            
            // Find the B field vector at that point.
            _magnetModel.getStrength( _samplePoint, _sampleVector /* output */, _distanceExponent  );
            
            // Accumulate a sum of the sample points.
            _fieldVector.add( _sampleVector );
        }
        
        // Average the B-field sample points.
        double scale = 1.0 / _samplePoints.size();
        _fieldVector.scale( scale );
        
        // Flux in one loop.
        double B = _fieldVector.getMagnitude();
        double A = getLoopArea();
        double theta = Math.abs( _fieldVector.getAngle() );//- getDirection() );
        double loopFlux = B * A * Math.cos( theta ); 
        
        // Flux in the coil.
        double flux = getNumberOfLoops() * loopFlux;
        
        // Change in flux.
        _deltaFlux = flux - _flux;
        _flux = flux;
        
        // Induced emf.
        double emf = -( _deltaFlux / dt );
        
        // If the emf has changed, set the current in the coil and notify observers.
        if ( emf != _emf ) {
            _emf = emf;
            
            // Current amplitude is proportional to emf amplitude.
            double amplitude = MathUtil.clamp( -1,  emf / FaradayConstants.MAX_PICKUP_EMF, +1 );
            setCurrentAmplitude( amplitude ); // calls notifyObservers
        }
        
        // Keep track of the biggest emf seen by the pickup coil.
        if ( Math.abs( _emf ) > Math.abs( _biggestEmf ) ) {
            _biggestEmf = _emf;
            if ( FaradayConstants.DEBUG_PICKUP_COIL_EMF ) {
                System.out.println( "PickupCoil.updateEmf: biggestEmf=" + _biggestEmf );
            }
        }
    }
}