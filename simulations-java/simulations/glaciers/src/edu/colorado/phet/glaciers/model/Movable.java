/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;


public abstract class Movable {
    
    private static final Point2D DEFAULT_POSITION = new Point2D.Double( 0, 0 );
    private static final double DEFAULT_ORIENTATION = 0;

    private Point2D _position;
    private double _orientation;
    private ArrayList _listeners; // list of MovableListener
    
    public Movable() {
        this( DEFAULT_POSITION, DEFAULT_ORIENTATION );
    }
    
    public Movable( Point2D position ) {
        this( position, DEFAULT_ORIENTATION );
    }
    
    public Movable( Point2D position, double orientation ) {
        _position = new Point2D.Double( position.getX(), position.getY() );
        _orientation = orientation;
        _listeners = new ArrayList();
    }
    
    public void cleanup() {}
    
    public void setPosition( Point2D position ) {
        setPosition( position.getX(), position.getY() );
    }
    
    public void setPosition( double x, double y ) {
        if ( x != _position.getX() || y != _position.getY() ) {
            _position.setLocation( x, y );
            notifyPositionChanged();
        }
    }
    
    public void translate( double dx, double dy ) {
        if ( dx != 0 || dy != 0 ) {
            setPosition( getX() + dx, getY() + dy );
        }
    }
    
    public Point2D getPosition() {
        return new Point2D.Double( _position.getX(), _position.getY() );
    }
    
    public Point2D getPositionReference() {
        return _position;
    }
    
    public double getX() {
        return _position.getX();
    }
    
    public double getY() {
        return _position.getY();
    }
    
    public void setOrientation( double orientation ) {
        if ( orientation != _orientation ) {
            _orientation = orientation;
            notifyOrientationChanged();
        }
    }
    
    public interface MovableListener {
        public void positionChanged();
        public void orientationChanged();
    }

    public static class MovableAdapter implements MovableListener {
        public void positionChanged() {}
        public void orientationChanged() {}
    }

    public void addMovableListener( MovableListener listener ) {
        _listeners.add( listener );
    }

    public void removeMovableListener( MovableListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyPositionChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (MovableListener) _listeners.get( i ) ).positionChanged();
        }
    }
    
    private void notifyOrientationChanged() {
        for ( int i = 0; i < _listeners.size(); i++ ) {
            ( (MovableListener) _listeners.get( i ) ).orientationChanged();
        }
    }
}
