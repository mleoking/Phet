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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.LightBulb;


/**
 * LightBulbGraphic is the graphical representation of a lightbulb.
 * The bulb's relative intensity can be set.
 * Registration point is at bottom center.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LightBulbGraphic extends CompositePhetGraphic implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final double RAYS_LAYER = 0;
    private static final double BULB_LAYER = 1;
    private static final int MAX_RAYS = 40; // maximum number of light rays
    private static final int MAX_RAY_LENGTH = 300;
    private static final double MAX_RAY_WIDTH = 3.5;
    private static final double MIN_RAY_WIDTH = 0.5;
    private static final Point2D RAYS_ORIGIN = new Point2D.Double( 0, -50 );
    private static final double BULB_RADIUS = 25.0;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LightBulb _lightBulbModel;
    private AbstractMagnet _magnetModel;
    private double _previousIntensity;
    private ArrayList _rays; // array of PhetShapeGraphic
    private Color _rayColor;
    private Stroke _rayStroke;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     */
    public LightBulbGraphic( Component component, LightBulb lightBulbModel, AbstractMagnet magnetModel ) {
        super( component );
        
        assert( component != null );
        assert( lightBulbModel !=  null );
        assert( magnetModel != null );
        
        _lightBulbModel = lightBulbModel;
        _lightBulbModel.addObserver( this );
        _magnetModel = magnetModel; // No need to observer magnet.
        _rays = new ArrayList();
       
        // Light bulb
        {
            PhetImageGraphic lightBulb = new PhetImageGraphic( component, FaradayConfig.LIGHTBULB_IMAGE );
            super.addGraphic( lightBulb, BULB_LAYER );
            int rx = lightBulb.getImage().getWidth() / 2;
            int ry = lightBulb.getImage().getHeight();
            lightBulb.setRegistrationPoint( rx, ry );
        }
        
        update();
    }
    
    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _lightBulbModel.removeObserver( this );
        _lightBulbModel = null;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Synchronize the view with the model.
     * <p>
     * The algorithm for rendering the light rays was adapted from 
     * edu.colorado.phet.cck3.circuit.components.LightBulbGraphic.setIntensity.
     * 
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        
        setVisible( _lightBulbModel.isEnabled() );
        
        if ( isVisible() ) {
            
            // Get the light intensity, a value in the range 0...+1.
            double intensity = _lightBulbModel.getIntensity();
            
            // Rescale the intensity to improve the visual effect.
            intensity = FaradayUtils.rescale( intensity, _magnetModel.getStrength() );
            intensity = MathUtil.clamp( 0, intensity, 1 );
            if ( intensity == Double.NaN ) {
                System.out.println( "WARNING - LightBulbGraphic.update: intensity=NaN" );
                return;
            }

            // If the intensity hasn't changed, do nothing.
            if ( _previousIntensity == intensity ) {
                return;
            }
            _previousIntensity = intensity;

            // Remove existing rays.
            for ( int i = 0; i < _rays.size(); i++ ) {
                removeGraphic( (PhetShapeGraphic) _rays.get(i) );
            }
            _rays.clear();

            // If intensity is zero, we're done.
            if ( intensity == 0 ) {
                repaint();
                return;
            }

            // Number of rays is a function of intensity.
            int numberOfRays = (int) ( intensity * MAX_RAYS );

            // Ray color is yellow, with alpha channel based on the intensity.
            int alpha = (int) ( intensity * 255 );
            _rayColor = new Color( Color.YELLOW.getRed(), Color.YELLOW.getGreen(), Color.YELLOW.getBlue(), alpha );

            // Ray dimensions are a function of intensity.
            double rayLength = intensity * MAX_RAY_LENGTH;
            double rayWidth = MIN_RAY_WIDTH + ( intensity * MAX_RAY_WIDTH );
            _rayStroke = new BasicStroke( (float) rayWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );

            // Rays fill a pie from 135 degrees to 45 degrees, clockwise.
            double angle = Math.toRadians( 135 ); // starting angle
            double deltaAngle = Math.toRadians( 270 ) / ( numberOfRays - 1 );

            // Create the rays.
            for ( int i = 0; i < numberOfRays; i++ ) {
                
                // Each ray's shape is a line.
                AbstractVector2D vec = ImmutableVector2D.Double.parseAngleAndMagnitude( BULB_RADIUS, angle );
                AbstractVector2D vec1 = ImmutableVector2D.Double.parseAngleAndMagnitude( rayLength + BULB_RADIUS, angle );
                Point2D end = vec.getDestination( RAYS_ORIGIN );
                Point2D end2 = vec1.getDestination( RAYS_ORIGIN );
                Line2D.Double line = new Line2D.Double( end, end2 );
                
                // Each ray is a PhetShapeGraphic.
                PhetShapeGraphic ray = new PhetShapeGraphic( getComponent() );
                ray.setShape( line );
                ray.setPaint( _rayColor );
                ray.setStroke( _rayStroke );
                
                // Add the ray to the composite graphic and to the array.
                addGraphic( ray, RAYS_LAYER );
                _rays.add( ray );

                // Increment the angle.
                angle += deltaAngle;
            }
            
            repaint();
        }
        
    } // update
}
