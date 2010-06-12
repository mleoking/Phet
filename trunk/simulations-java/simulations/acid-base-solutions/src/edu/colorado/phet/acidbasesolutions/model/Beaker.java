/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model of a beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Beaker extends ABSModelElement {
    
    private Point2D location;
    private PDimension size;
    private Rectangle2D bounds;

    public Beaker( Point2D location, boolean visible, PDimension size ) {
        super( location, visible );
        this.location = new Point2D.Double( location.getX(), location.getY() );
        this.size = new PDimension( size );
        bounds = new Rectangle2D.Double();
        updateBounds();
    }
    
    @Override
    public void setLocation( Point2D location ) {
        super.setLocation( location );
        updateBounds();
    }
    
    public Point2D getLocationReference() {
        return location;
    }
    
    public double getX() {
        return location.getX();
    }
    
    public double getY() {
        return location.getY();
    }
    
    public PDimension getSizeReference() {
        return size;
    }
    
    public double getWidth() {
        return size.getWidth();
    }
    
    public double getHeight() {
        return size.getHeight();
    }
    
    public boolean inSolution( Point2D p ) {
        return bounds.contains( p );
    }
    
    private void updateBounds() {
        double x = location.getX() - ( size.width / 2 );
        double y = location.getY() - size.height;
        bounds.setRect( x, y, size.getWidth(), size.getHeight() );
    }
}
