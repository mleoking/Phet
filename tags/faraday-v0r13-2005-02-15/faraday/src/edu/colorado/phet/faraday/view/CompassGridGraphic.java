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

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.faraday.model.AbstractMagnet;


/**
 * CompassGridGraphic is the graphical representation of a "compass grid".
 * As an alternative to a field diagram, the grid shows the strength
 * and orientation of a magnet field at various points in 2D space.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CompassGridGraphic extends PhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // The magnet model element that the grid is observing.
    private AbstractMagnet _magnetModel;
    
    // The spacing between compass needles, in pixels.
    private int _xSpacing, _ySpacing;
    
    // The size of the compass needles, in pixels.
    private Dimension _needleSize;
    
    // The compass needles that are in the grid (array of FastGridNeedle).
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
        for ( int i = 0; i < xCount; i++ ) {
            for ( int j = 0; j < yCount; j++ ) {
                FastGridNeedle needle = new FastGridNeedle();
                needle.setLocation( i * xSpacing, j * ySpacing );
                needle.setSize( _needleSize );
                _needles.add( needle );
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
            FastGridNeedle needle = (FastGridNeedle) _needles.get(i);
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
     * Since this graphic does not handle location, override it to throw an exception.
     */
    public void setLocation( int x, int y ) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Since this graphic does not handle location, override it to throw an exception.
     */
    public void setLocation( Point p ) {
        setLocation( p.x, p.y );
    }
    
    /**
     * Updates when we become visible.
     * 
     * @param visible true for visible, false for invisible
     */
    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        update();
    }
    
    /**
     * Draws all of the needles in the grid.
     * <p>
     * This method is optimized with the following assumptions:
     * <ul>
     * <li>the grid's location is (0,0)
     * <li>the grid's registration point is (0,0)
     * <li>the grid has no transforms applied to it
     * </ul>
     * 
     * @param g2 the graphics context
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {       
            super.saveGraphicsState( g2 );
            g2.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
            // Draw the needles.
            for ( int i = 0; i < _needles.size(); i++ ) {
                FastGridNeedle needle = (FastGridNeedle)_needles.get(i);
                needle.paint( g2 );
            }
            super.restoreGraphicsState();
            setBoundsDirty();
        }
    }
    
    /**
     * Determines the bounds of the grid.
     * Take the union of all the bounds of all needles in the grid.
     * 
     * @return the bounds of the grid
     */
    protected Rectangle determineBounds() {
        Rectangle r = new Rectangle();
        for ( int i = 0; i < _needles.size(); i++ ) {
            FastGridNeedle needle = (FastGridNeedle) _needles.get(i);
            r = r.union( needle.getBounds() );
        }
        return r;
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
                FastGridNeedle needle = (FastGridNeedle)_needles.get(i);

                // Get the magnetic field information at the needle's location.
                Point2D p = needle.getLocation();
                AbstractVector2D fieldStrength = _magnetModel.getStrength( p );
                double angle = fieldStrength.getAngle();
                double magnitude = fieldStrength.getMagnitude();
                
                // Set the needle's direction.
                needle.setDirection( angle );
                
                // Set the needle's strength.
                {
                    // Convert the field strength to a value in the range 0...+1.
                    double scale = ( magnitude / magnetStrength );
                    
                    // Adjust the scale to improve the visual effect.
                    scale = Rescaler.rescale( scale, magnetStrength );
                    scale = MathUtil.clamp( 0, scale, 1 );
                    
                    // Set the needle strength.
                    needle.setStrength( scale );
                }
            }
            repaint();
        }
    }
    
    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * FastGridNeedle draw a compass needle.
     * It is not a descendant of PhetGraphic, so that we can avoid the overhead
     * of computing AffineTransforms. (This overhead is built into PhetGraphic,
     * specifically in PhetGraphic.getNetTransform.)
     * <p>
     * This class assumes that CompassGridGraphic will handle saving/restoring the
     * graphics context.  And it assumes that the grid will be positioned at the
     * origin of its parent component.  These assumptions allow us to bypass most
     * of the expensive transforms in PhetGraphic.
     *
     * @author Chris Malley (cmalley@pixelzoom.com)
     * @version $Revision$
     */
    private class FastGridNeedle {

        private Point2D _location;
        private Dimension _size;
        private double _strength;
        private double _direction;
        private Shape _northShape, _southShape;
        private Color _northColor, _southColor;

        public FastGridNeedle() {
            _location = new Point2D.Double( 0, 0 );
            _size = new Dimension( 40, 20 );
            _direction = 0.0;
        }

        public void setLocation( Point2D p ) {
            setLocation( p.getX(), p.getY() );
        }

        public void setLocation( double x, double y ) {
            _location.setLocation( x, y );
            updateShapes();
        }

        public void setSize( Dimension size ) {
            _size.setSize( size );
            updateShapes();
        }

        public Dimension getSize() {
            return _size;
        }

        public Point2D getLocation() {
            return _location;
        }

        public void setStrength( double strength ) {
            _strength = strength;
            int alpha = (int) ( 255 * _strength );
            _northColor = new Color( 255, 0, 0, alpha );
            _southColor = new Color( 255, 255, 255, alpha );
        }

        public double getStrength() {
            return _strength;
        }

        public void setDirection( double direction ) {
            _direction = direction;
            updateShapes();
        }

        public double getDirections() {
            return _direction;
        }

        public Rectangle getBounds() {
            Rectangle r = _northShape.getBounds();
            r.union( _southShape.getBounds() );
            return r;
        }
        
        private void updateShapes() {
            AffineTransform transform = new AffineTransform();
            transform.translate( _location.getX(), _location.getY() );
            transform.rotate( _direction );

            GeneralPath northPath = new GeneralPath();
            northPath.moveTo( 0, -( _size.height / 2 ) );
            northPath.lineTo( ( _size.width / 2 ), 0 );
            northPath.lineTo( 0, ( _size.height / 2 ) );
            northPath.closePath();
            _northShape = transform.createTransformedShape( northPath );

            GeneralPath southPath = new GeneralPath();
            southPath.moveTo( 0, -( _size.height / 2 ) );
            southPath.lineTo( 0, ( _size.height / 2 ) );
            southPath.lineTo( -( _size.width / 2 ), 0 );
            southPath.closePath();
            _southShape = transform.createTransformedShape( southPath );
        }

        public void paint( Graphics2D g2 ) {
            g2.setPaint( _northColor );
            g2.fill( _northShape );
            g2.setPaint( _southColor );
            g2.fill( _southShape );
        }
    }
}
