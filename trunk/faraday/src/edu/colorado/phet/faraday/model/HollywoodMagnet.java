/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.math.AbstractVector2D;


/**
 * HollywoodMagnet is a magnet that does not correspond to any real-world
 * physical model.  The behavior of the magnetic field is faked to 
 * provide results that look like a rough approximation of a magnetic field.
 * (According to the physicists, it's "pretty close".)
 * <p>
 * This class was used for testing while I was waiting to get the
 * actual physical models from PhET physicists.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HollywoodMagnet extends AbstractMagnet {
  
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public HollywoodMagnet() {
        super();
    }

    //----------------------------------------------------------------------------
    // AbstractMagnet implementation
    //----------------------------------------------------------------------------
    
    /**
     * @see edu.colorado.phet.faraday.model.IMagnet#getStrengthVector(java.awt.geom.Point2D)
     */
    public AbstractVector2D getStrength( Point2D p ) {
        
        // Magnitude
        double magnitude = 0.0;
        {
            double strength = super.getStrength();
            double distance = getLocation().distance( p );

            // HACK Assume that magnet "strength" is the radius (in pixels) of the magnetic field.
            if ( distance > strength ) {
                magnitude = 0;
            }
            else {
                magnitude = strength - ( strength * ( distance / strength ) );
            }
        }
        
        // Angle, in radians
        double angle = Math.toRadians( 0.0 );
        if ( magnitude > 0 )
        {
            double fieldDirection = 0.0;

            // Magnet paramters
            double x = super.getX();
            double y = super.getY();
            double w = super.getWidth();
            double h = super.getHeight();
            double direction = super.getDirection();

            if( p.getX() <= x - w / 2 ) {
                // Point is to left of magnet
                double opposite = y - p.getY();
                double adjacent = ( x - w / 2 ) - p.getX();
                double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
                fieldDirection = direction + theta;
            }
            else if( p.getX() >= x + w / 2 ) {
                // Point is to right of magnet...
                double opposite = p.getY() - y;
                double adjacent = p.getX() - ( x + w / 2 );
                double theta = Math.toDegrees( Math.atan( opposite / adjacent ) );
                fieldDirection = direction + theta;
            }
            else if( p.getY() <= y - h / 2 ) {
                // Point is above the magnet...
                double multiplier = ( x + w / 2 - p.getX() ) / w;
                fieldDirection = direction - 90 - ( multiplier * 180 );
            }
            else if( p.getY() >= y + h / 2 ) {
                // Point is below the magnet...
                double multiplier = ( x + w / 2 - p.getX() ) / w;
                fieldDirection = direction + 90 + ( multiplier * 180 );
            }
            else {
                // Point is inside the magnet...
                fieldDirection = direction + 180;
            }

            fieldDirection = fieldDirection % 360;
            angle = Math.toRadians( fieldDirection );
        }
        
        // Vector
        return AbstractVector2D.Double.parseAngleAndMagnitude( magnitude, angle );
    }
}