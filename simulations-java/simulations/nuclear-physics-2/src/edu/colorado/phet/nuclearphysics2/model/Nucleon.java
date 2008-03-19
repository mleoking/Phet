/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

/**
 * Class the implements the behavior of nucleon (i.e. proton and neutron)
 * model elements.
 *
 * @author John Blanco
 */
public class Nucleon implements AtomicNucleusConstituent {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Random number generator, used for creating some random behavior.
    Random _rand = new Random();
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public Nucleon(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);
    }
    
    //------------------------------------------------------------------------
    // Accessor methods
    //------------------------------------------------------------------------
    
    public Point2D getPosition()
    {
        return new Point2D.Double(_position.getX(), _position.getY());
    }
    
    //------------------------------------------------------------------------
    // Behavior methods
    //------------------------------------------------------------------------
    
    /**
     * This method simulates the quantum tunneling behavior, which means that
     * it causes the particle to move to some new random location within the
     * confines of the supplied parameters.
     * 
     * @param minDistance - Minimum distance from origin (0,0).  This is
     * generally 0.
     * @param nucleusRadius - Radius of the nucleus where this particle resides.
     * @param tunnelRadius - Radius at which this particle could tunnel out of nucleus.
     */
    public void tunnel(double minDistance, double nucleusRadius, double tunnelRadius)
    {
        // Create a probability distribution that will cause the particles to
        // be fairly evenly spread around the core of the nucleus and appear
        // occasionally at the outer reaches.

        double multiplier = _rand.nextDouble();
        
        if (multiplier > 0.8){
            // Cause the distribution to tail off in the outer regions of the
            // nucleus.
            multiplier = _rand.nextDouble() * _rand.nextDouble();
        }
        
        double newRadius = minDistance + (multiplier * (nucleusRadius - minDistance));
        
        // Calculate the new angle, in radians, from the origin.
        double newAngle = _rand.nextDouble() * 2 * Math.PI;
        
        // Convert from polar to Cartesian coordinates.
        double xPos = Math.cos( newAngle ) * newRadius;
        double yPos = Math.sin( newAngle ) * newRadius;
        
        // Save the new position.
        _position.setLocation( xPos, yPos );

        // Notify all listeners of the position change.
        for (int i = 0; i < _listeners.size(); i++)
        {
            ((Listener)_listeners.get( i )).positionChanged(); 
        }        
    }
    
    //------------------------------------------------------------------------
    // Listener support
    //------------------------------------------------------------------------

    public void addListener(Listener listener)
    {
        if (_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        _listeners.add( listener );
    }
    
    public static interface Listener {
        void positionChanged();
    }
}
