/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;


/**
 * CompassGridGraphic is the graphical representation of a "compass grid".
 * As an alternative to a field diagram, the grid shows the strength
 * and orientation of a magnet field at various points in 2D space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The magnet model element that the grid is observing.
    private AbstractMagnet _magnetModel;
    
    // The spacing between compass needles, in pixels.
    private int _xSpacing, _ySpacing;
    
    // The size of the compass needles, in pixels.
    private Dimension _needleSize;
    
    // The compass needles that are in the grid (array of CompassNeedleGraphic).
    private ArrayList _needles;
    
    // The original aspect ratio of the parent component, prior to any resizing.
    private double _aspectRatio;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param magnetModel the magnet model
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public CompassGridGraphic( Component component, AbstractMagnet magnetModel, int xSpacing, int ySpacing) {
        super( component );
        assert( component != null );
        assert( magnetModel != null );
        
        _magnetModel = magnetModel;
        _magnetModel.addObserver( this );
        
        _needleSize = new Dimension( 40, 20 );
        _needles = new ArrayList();
        _aspectRatio = 0.0;
        
        setSpacing( xSpacing, ySpacing );
        
        // Need to reset the grid when the parent component is resized.
        component.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if ( _aspectRatio == 0.0 ) {
                    // Save the original aspect ratio of the parent component.
                    _aspectRatio = ((double)getComponent().getWidth()) / ((double)getComponent().getHeight());
                }
                resetSpacing();
            }
        });
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _magnetModel.removeObserver( this );
        _magnetModel = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the spacing between points on the grid.
     * 
     * @param xSpacing space between grid points in the X direction
     * @param ySpacing space between grid points in the Y direction
     */
    public void setSpacing( int xSpacing, int ySpacing ) {
        
        // Save the spacing, for use by getters and restore.
        _xSpacing = xSpacing;
        _ySpacing = ySpacing;
        
        // Clear existing needles.
        _needles.clear();
        super.clear();
        
        // Determine the dimensions of the parent component.
        Component component = getComponent();
        double width = component.getWidth();
        double height = component.getHeight();
        
        // Account for potential scaling by the parent component.
        double aspectRatio = width / height;
        if ( aspectRatio < _aspectRatio ) {
            width = width * (1/aspectRatio);
            height = height * (1/aspectRatio);
        }
        else
        {
            width = width * aspectRatio;
            height = height * aspectRatio;
        }
        
        // Determine how many compasses are needed to fill the parent component.
        int xCount = (int)(width / xSpacing) + 4;  // HACK
        int yCount = (int)(height / ySpacing) + 4;  // HACK
        //System.out.println( "CompassGridGraphic.setSpacing - grid is " + xCount + "x" + yCount ); // DEBUG
        
        // Create the compasses.
        CompassNeedleGraphic needle;
        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                needle = new CompassNeedleGraphic( component );
                needle.setLocation( i * xSpacing, j * ySpacing );
                needle.setSize( _needleSize );
                _needles.add( needle );
                super.addGraphic( needle );
            }
        }
        
        update();
    }
    
    /**
     * Resets the grid spacing.
     * This should be called when the parent container is resized.
     */
    public void resetSpacing() {
        setSpacing( _xSpacing, _ySpacing );
    }
    
    /**
     * Gets the spacing between grid point in the X direction.
     * 
     * @return X spacing
     */
    public int getXSpacing() {
        return _xSpacing;
    }
    
    /**
     * Gets the spacing between grid point in the Y direction.
     * 
     * @return Y spacing
     */
    public int getYSpacing() {
        return _ySpacing;
    }

    /**
     * Sets the size of all compass needles.
     * 
     * @param needleSize the needle size
     */
    public void setNeedleSize( final Dimension needleSize ) {
        assert( needleSize != null );
        _needleSize = new Dimension( needleSize );
        for ( int i = 0; i < _needles.size(); i++ ) {
            CompassNeedleGraphic needle = (CompassNeedleGraphic)_needles.get(i);
            needle.setSize( _needleSize );
        }
        update();
    }
    
    /**
     * Gets the size of all compass needles.
     * 
     * @return the size
     */
    public Dimension getNeedleSize() {
        return new Dimension( _needleSize );
    }
    
    //----------------------------------------------------------------------------
    // Override inherited methods
    //----------------------------------------------------------------------------
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {
        if ( isVisible() ) {
            double magnetStrength = _magnetModel.getStrength();
            for( int i = 0; i < _needles.size(); i++ ) {

                // Next compass needle...
                CompassNeedleGraphic needle = (CompassNeedleGraphic) _needles.get( i );

                // Get the magnetic field information at the needle's location.
                Point2D p = needle.getLocation();
                AbstractVector2D fieldStrength = _magnetModel.getStrength( p );
                double angle = fieldStrength.getAngle();
                double magnitude = fieldStrength.getMagnitude();
                
                // Set the needle's direction.
                needle.setDirection( Math.toDegrees( angle ) );
                
                // Set the needle's strength.
                {
                    double distance = p.distance( _magnetModel.getLocation() );
                    double scale = ( magnitude / magnetStrength );
                    
                    /*
                     * Since the magnitude drops off rather quickly, we need to adjust the 
                     * scale so that (a) more compasses are visible, and (b) the number of
                     * visible compasses increases as the magnet strength increases.
                     * <p>
                     * The algorithm is as follows (courtesy of Mike Dubson):
                     * <ul>
                     * <li>Bo is some cutoff value
                     * <li>if B > Bo, scale = 1
                     * <li>if B <= Bo, scale = (B/Bo)**N
                     * <li>N is between 0.3-0.8 and is adjusted for magnet strength
                     * </ul>
                     */
                    final double CUTOFF = 0.8; // Bo
                    final double MIN_EXPONENT = 0.3;
                    final double MAX_EXPONENT = 0.8;
                    if ( scale > CUTOFF ) {
                        scale = 1.0;
                    }
                    else {
                        double min = FaradayConfig.MAGNET_STRENGTH_MIN;
                        double max = FaradayConfig.MAGNET_STRENGTH_MAX;
                        double exponent = MAX_EXPONENT - ( ( (magnetStrength - min) / (max - min) ) * (MAX_EXPONENT - MIN_EXPONENT) );
                        scale = Math.pow( scale / CUTOFF, exponent );
                    }

                    scale = MathUtil.clamp( 0, scale, 1 );
                    needle.setStrength( scale );
                }
            }
            repaint();
        }
    }
}
