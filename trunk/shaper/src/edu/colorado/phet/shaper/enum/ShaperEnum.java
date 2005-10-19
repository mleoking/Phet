/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.enum;


/**
 * ShaperEnum is the base class for enumerations.  
 * We are using the typesafe enum pattern as described in 
 * Item 21 of "Effective Java" by Joshua Bloch.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ShaperEnum {

    private String _name;
    
    protected ShaperEnum( String name ) {
        _name = name;
    }
    
    public String getName() {
        return _name;
    }
    
    public boolean isNamed( String name ) {
        return _name.equals( name );
    }
    
    public String toString() { 
        return _name;
    }
}
