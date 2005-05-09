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
 * HarmonicSeries
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicSeries extends SimpleObservable {

    private static final double DEFAULT_FUNDAMENTAL_FREQUENCY = 440; // Hz  (A above middle C)
    
    public double _fundamentalFrequency; // Hz
    public ArrayList _harmonics; // array of Harmonic
    
    public HarmonicSeries() {
        _fundamentalFrequency = DEFAULT_FUNDAMENTAL_FREQUENCY;
        _harmonics = new ArrayList();
    }
  
    public void setFundamentalFrequency( double fundamentalFrequency ) {
        if ( fundamentalFrequency != _fundamentalFrequency ) {
            _fundamentalFrequency = fundamentalFrequency;  
            notifyObservers();
        }
    }
    
    public double getFundamentalFrequency() {
        return _fundamentalFrequency;
    }
    
    public void setNumberOfHarmonics( int numberOfHarmonics ) {
        int currentNumber = _harmonics.size();
        
        // Add or remove harmonics.
        if ( numberOfHarmonics < currentNumber ) {
            int numberToRemove = currentNumber - numberOfHarmonics;
            for ( int i = currentNumber; i > numberToRemove; i-- ) {
                _harmonics.remove( i - 1 );
            }
        }
        else {
            int numberToAdd = numberOfHarmonics - currentNumber;
            for ( int i = 0; i < numberToAdd; i++ ) {
                Harmonic harmonic = new Harmonic( currentNumber + i );
                _harmonics.add( harmonic );
            }
        }
        
        notifyObservers();
    }
    
    public int getNumberOfHarmonics() {
        return _harmonics.size();
    }
    
    public Harmonic getHarmonic( int order ) {
        assert( order >= 0 && order < _harmonics.size() );
        return (Harmonic) _harmonics.get( order );
    }
}
