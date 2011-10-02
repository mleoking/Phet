// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;

import edu.colorado.phet.platetectonics.model.PlateModel;

public class VerySimplePlateModel extends PlateModel {
    @Override public double getElevation( double x, double z ) {
        return ( x - z ) / 10 + 1000 * ( Math.cos( x / 1000 ) - Math.sin( z / 1000 ) );
    }

    @Override public double getDensity( double x, double y ) {
        return 3000 - y / 100;
    }

    @Override public double getTemperature( double x, double y ) {
        return 290 - y / 1000;
    }
}
