/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:08:02 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class WaveSetup {
    private Wave wave;

    protected WaveSetup() {
    }

    public Wave getWave() {
        return wave;
    }

    protected void setWave( Wave wave ) {
        this.wave = wave;
    }

    public WaveSetup( Wave wave ) {
        this.wave = wave;
    }

    public void initialize( Wavefunction wavefunction ) {
        initialize( wavefunction, 0 );
    }

    public void initialize( Wavefunction wavefunction, double time ) {
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, wave.getValue( i, k, time ) );
            }
        }
    }
}
