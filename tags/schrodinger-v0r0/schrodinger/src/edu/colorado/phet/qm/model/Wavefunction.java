/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 1:46:56 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class Wavefunction {
    public static void normalize( Complex[][] wavefunction ) {
        double totalProbability = new ProbabilityValue().compute( wavefunction );
//        System.out.println( "totalProbability = " + totalProbability );
        double scale = 1.0 / Math.sqrt( totalProbability );
        scale( wavefunction, scale );
        double postProb = new ProbabilityValue().compute( wavefunction );
//        System.out.println( "postProb = " + postProb );

        double diff = 1.0 - postProb;
        if( !( Math.abs( diff ) < 0.0001 ) ) {
            System.out.println( "Error in probability normalization." );
            throw new RuntimeException( "Error in probability normalization." );
        }
    }

    public static void scale( Complex[][] wavefunction, double scale ) {

        for( int i = 0; i < wavefunction.length; i++ ) {
            for( int j = 0; j < wavefunction[i].length; j++ ) {
                Complex complex = wavefunction[i][j];
                complex.scale( scale );
            }
        }
    }
}
