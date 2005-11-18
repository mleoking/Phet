/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import java.util.Observable;


/**
 * QTObservable
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTObservable extends Observable {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private boolean _notifyEnabled;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public QTObservable() {
        super();
        _notifyEnabled = true;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public void setNotifyEnabled( boolean enabled ) {
        _notifyEnabled = enabled;
        notifyObservers();
    }
    
    public boolean isNotifyEnabled() {
        return _notifyEnabled;
    }
    
    //----------------------------------------------------------------------------
    // Observable overrides
    //----------------------------------------------------------------------------
    
    public void notifyObservers() {
        if ( _notifyEnabled ) {
            setChanged();
            super.notifyObservers();
            clearChanged();
        }
    }
    
    public void notifyObservers( Object arg ) {
        if ( _notifyEnabled ) {
            setChanged();
            super.notifyObservers( arg );
            clearChanged();
        }
    }
}
