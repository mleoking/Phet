/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.model;

import java.awt.geom.Point2D;
import java.util.EventListener;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.CLConstants;


/**
 * Model of a capacitor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Capacitor {
    
    private final EventListenerList listeners;
    
    // immutable properties
    private final Point2D location;
    private final double plateThickness;
    
    // mutable properties
    private double plateSize;
    private double plateSeparation;
    private DielectricMaterial dielectricMaterial;
    private double dielectricOffset;

    public Capacitor( Point2D location, double plateSize, double plateSeparation, DielectricMaterial dielectricMaterial, double dielectricOffset ) {
        
        listeners = new EventListenerList();
        
        this.location = new Point2D.Double( location.getX(), location.getY() );
        this.plateThickness = CLConstants.PLATE_THICKNESS;
        
        this.plateSize = plateSize;
        this.plateSeparation = plateSeparation;
        this.dielectricMaterial = dielectricMaterial;
        this.dielectricOffset = dielectricOffset;
    }
    
    public Point2D getLocationReference() {
        return location;
    }
    
    public double getPlateThickness() {
        return plateThickness;
    }
    
    public void setPlateSize( double plateSize ) {
        if ( plateSize != this.plateSize ) {
            this.plateSize = plateSize;
            firePlateSizeChanged();
        }
    }
    
    public double getPlateSize() {
        return plateSize;
    }
    
    public void setPlateSeparation( double plateSeparation ) {
        if ( plateSeparation != this.plateSeparation ) {
            this.plateSeparation = plateSeparation;
            firePlateSeparationChanged();
        }
    }
    
    public double getPlateSeparation() {
        return plateSeparation;
    }
    
    public void setDielectricMaterial( DielectricMaterial dielectricMaterial ) {
        if ( ( dielectricMaterial == null &&  this.dielectricMaterial != null ) || dielectricMaterial.equals( this.dielectricMaterial ) ) {
            this.dielectricMaterial = dielectricMaterial;
            fireDielectricMaterialChanged();
        }
    }
    
    public DielectricMaterial getDielectricMaterial() {
        return dielectricMaterial;
    }
    
    public void setDielectricOffset( double dielectricOffset ) {
        if ( dielectricOffset != this.dielectricOffset ) {
            this.dielectricOffset = dielectricOffset;
            fireDielectricOffsetChanged();
        }
    }
    
    public double getDielectricOffset() {
        return dielectricOffset;
    }
    
    public interface CapacitorChangeListener extends EventListener {
        public void plateSizeChanged();
        public void plateSeparationChanged();
        public void dielectricMaterialChanged();
        public void dielectricOffsetChanged();
    }
    
    public static class CapacitorChangeAdapter implements CapacitorChangeListener {
        public void plateSizeChanged() {}
        public void plateSeparationChanged() {}
        public void dielectricMaterialChanged() {}
        public void dielectricOffsetChanged() {}
    }
    
    public void addCapacitorChangeListener( CapacitorChangeListener listener ) {
        listeners.add( CapacitorChangeListener.class, listener );
    }
    
    public void removeCapacitorChangeListener( CapacitorChangeListener listener ) {
        listeners.remove( CapacitorChangeListener.class, listener );
    }
    
    private void firePlateSizeChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.plateSizeChanged();
        }
    }
    
    private void firePlateSeparationChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.plateSeparationChanged();
        }
    }
    
    private void fireDielectricMaterialChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.dielectricMaterialChanged();
        }
    }
    
    private void fireDielectricOffsetChanged() {
        for ( CapacitorChangeListener listener : listeners.getListeners( CapacitorChangeListener.class ) ) {
            listener.dielectricOffsetChanged();
        }
    }
}
