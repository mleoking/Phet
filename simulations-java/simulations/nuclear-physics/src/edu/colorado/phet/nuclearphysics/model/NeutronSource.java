/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.nuclearphysics.common.model.Neutron;

/**
 * This class models the behavior of a neutron source, i.e. some sort of
 * device that can generate neutrons.
 *
 * @author John Blanco
 */
public class NeutronSource {
    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // Velocity of neutrons generated by this source.
    public static final double NEUTRON_VELOCITY = 1.75;

    //------------------------------------------------------------------------
    // Instance data
    //------------------------------------------------------------------------
    private ArrayList _listeners = new ArrayList();
    
    // Location in space of this particle.
    private Point2D.Double _position;
    
    // Angle, in radians, at which the neutron should be fired.
    private double _firingAngle = 0;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    public NeutronSource(double xPos, double yPos)
    {
        _position = new Point2D.Double(xPos, yPos);
    }
    
    //------------------------------------------------------------------------
    // Accessor Methods
    //------------------------------------------------------------------------

    public Point2D getPosition(){
        return new Point2D.Double(_position.getX(), _position.getY());
    }
    
    public void setPosition( double xPos, double yPos ){
        if ((xPos != _position.getX()) || (yPos != _position.getY())){
            _position.setLocation( xPos, yPos );
            notifyPositionChanged();
        }
    }
    
    public double getFiringAngle(){
        return _firingAngle;
    }
    
    public void setFiringAngle(double angle){
        if (angle != _firingAngle){
            _firingAngle = angle;
            notifyOrientationChanged();
        }
    }

    //------------------------------------------------------------------------
    // Listener Support
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
        void orientationChanged();
        void neutronGenerated(Neutron newNeutron);
    }
    
    public static class Adapter implements Listener {
        public void positionChanged(){};
        public void orientationChanged(){};
        public void neutronGenerated(Neutron newNeutron){};
    }
    
    public void notifyPositionChanged(){
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ((NeutronSource.Listener)_listeners.get( i )).positionChanged();            
        }
    }

    public void notifyOrientationChanged(){
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ((NeutronSource.Listener)_listeners.get( i )).orientationChanged();            
        }
    }

    public void notifyNeutronGenerated(Neutron newNeutron){
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ((NeutronSource.Listener)_listeners.get( i )).neutronGenerated( newNeutron );            
        }
    }

    //------------------------------------------------------------------------
    // Other methods
    //------------------------------------------------------------------------

    /**
     * Commands the neutron source to generate a new neutron.
     */
    public void generateNeutron(){
        
        Neutron newNeutron = new Neutron(_position.x, _position.y, NEUTRON_VELOCITY * Math.cos( _firingAngle ),
                NEUTRON_VELOCITY * Math.sin( _firingAngle ), false);
        
        notifyNeutronGenerated( newNeutron );
    }
}
