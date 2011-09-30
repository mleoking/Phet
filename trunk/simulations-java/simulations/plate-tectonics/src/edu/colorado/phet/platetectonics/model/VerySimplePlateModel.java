// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

public class VerySimplePlateModel implements PlateModel {
    public double getElevation( double x, double z ) {
        return ( x - z ) / 10;
    }

    public double getDensity( double x, double y ) {
        return 3000 - y / 100;
    }

    public double getTemperature( double x, double y ) {
        return 290 - y / 1000;
    }
}
