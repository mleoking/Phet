/**
 * Class: PeriodicFunction
 * Package: edu.colorado.phet.waves.model
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.waves.model;

public interface PeriodicFunction {

    /**
     * Gives the amplitude of the wave funtion at a specified time
     * @param time
     * @return
     */
    float valueAtTime( float frequency, float maxAmplitude, float time );
}
