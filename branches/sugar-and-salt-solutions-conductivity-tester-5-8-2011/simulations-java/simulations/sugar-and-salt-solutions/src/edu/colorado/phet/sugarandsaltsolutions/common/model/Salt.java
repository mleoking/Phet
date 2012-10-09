// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Salt crystal
 *
 * @author Sam Reid
 */
public class Salt extends Crystal {
    //Create a salt crystal with 1E-6 moles of salt
    public Salt( ImmutableVector2D position ) {
        this( position, 1E-6 );
    }

    public Salt( ImmutableVector2D position, double moles ) {
        super( position, moles );
    }
}