/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.model;

import java.util.ArrayList;

import edu.colorado.phet.common.util.SimpleObservable;


/**
 * FourierSeries
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierSeries extends SimpleObservable {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double DEFAULT_FUNDAMENTAL_FREQUENCY = 440; // Hz  (A above middle C)
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    public double _fundamentalFrequency; // Hz
    public ArrayList _components; // array of FourierComponent
    public ArrayList _availableComponents; // array of FourierComponent
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public FourierSeries() {
        _fundamentalFrequency = DEFAULT_FUNDAMENTAL_FREQUENCY;
        _components = new ArrayList();
        _availableComponents = new ArrayList();
    }
  
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the fundamental frequency of the series.
     * 
     * @param fundamentalFrequency the fundamental frequency, in Hz
     */
    public void setFundamentalFrequency( double fundamentalFrequency ) {
        assert( fundamentalFrequency > 0 );
        if ( fundamentalFrequency != _fundamentalFrequency ) {
            _fundamentalFrequency = fundamentalFrequency;  
            notifyObservers();
        }
    }
    
    /**
     * Gets the fundamental frequency of the series.
     * 
     * @return the fundamental frequency, in Hz
     */
    public double getFundamentalFrequency() {
        return _fundamentalFrequency;
    }
    
    /**
     * Sets the number of components in the series.
     * 
     * @param numberOfComponents the number of components
     */
    public void setNumberOfComponents( int numberOfComponents ) {
        assert( numberOfComponents > 0 );
        
        int currentNumber = _components.size();
        if ( numberOfComponents != currentNumber ) {
            if ( numberOfComponents < currentNumber ) {
                // Remove components.
                int numberToRemove = currentNumber - numberOfComponents;
                for ( int i = currentNumber-1; i > currentNumber - numberToRemove - 1; i-- ) {
                    // Move the component to the "available" list.
                    _availableComponents.add( _components.get(i) );
                    _components.remove( i );
                }
            }
            else {
                // Add harmonics.
                int numberToAdd = numberOfComponents - currentNumber;
                for ( int i = 0; i < numberToAdd; i++ ) {
                    FourierComponent component = null;
                    int numberAvailable = _availableComponents.size();
                    if ( numberAvailable > 0 ) {
                        // Get a component from the "available" list.
                        component = (FourierComponent) _availableComponents.get( numberAvailable - 1 );
                        component.setOrder( currentNumber + i );
                        _availableComponents.remove( numberAvailable - 1 );
                    }
                    else {
                        component = new FourierComponent( currentNumber + i );   
                    }
                    _components.add( component );
                }
            }
            notifyObservers();
        }
    }
    
    /**
     * Gets the number of components in the series.
     * 
     * @return the number of components
     */
    public int getNumberOfComponents() {
        return _components.size();
    }
    
    /**
     * Gets a specific component in the series.
     * The index of the fundamental component is zero.
     * 
     * @param order the index
     * @return
     */
    public FourierComponent getComponent( int order ) {
        assert( order >= 0 && order < _components.size() );
        return (FourierComponent) _components.get( order );
    }
}
