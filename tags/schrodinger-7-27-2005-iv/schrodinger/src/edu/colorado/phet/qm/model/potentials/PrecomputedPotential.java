/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.potentials;

import edu.colorado.phet.qm.model.Potential;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 8:58:00 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class PrecomputedPotential implements Potential {
    private Potential potential;
    private double[][] potentialValues;

    public PrecomputedPotential( Potential potential, int width, int height ) {
        this.potential = potential;
        update( width, height );
    }

    public void update( int width, int height ) {
        potentialValues = new double[width][height];
        for( int i = 0; i < width; i++ ) {
            for( int j = 0; j < height; j++ ) {
                potentialValues[i][j] = potential.getPotential( i, j, 0 );
            }
        }
    }

    public double getPotential( int x, int y, int timestep ) {
        return potentialValues[x][y];
    }
}
