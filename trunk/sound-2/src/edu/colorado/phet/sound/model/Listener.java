/**
 * Class: Listener
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 6, 2004
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Point2D;

/**
 * This is a placeholder class in the model for the person listening to the sound
 */
public class Listener extends SimpleObservable implements ModelElement {
    private Point2D.Double location = new Point2D.Double();
    private Point2D.Double origin;
    private SoundModel model;
    private double frequencyHeard;
    private double amplitudeHeard;
    private double octaveAmplitudeHeard;

    public Listener( SoundModel model, Point2D.Double soundOrigin ) {
        this.model = model;
        model.addModelElement( this );
        this.origin = soundOrigin;
    }

    public Point2D.Double getLocation() {
        return location;
    }

    public void setLocation( Point2D.Double location ) {
        this.location.setLocation( location );
        notifyObservers();
    }

    int cnt = 0;
    public void stepInTime( double dt ) {
        int distFromSource = (int)this.location.distance( origin );
        double currentFrequency = model.getPrimaryWavefront().getFrequencyAtTime( distFromSource );
        double currentAmplitude = model.getPrimaryWavefront().getMaxAmplitudeAtTime( distFromSource );
        double currentOctaveAmplitude = model.getOctaveWavefront().getMaxAmplitudeAtTime( distFromSource );
        boolean notifyFlag = false;

        boolean flag = false;
        if (currentOctaveAmplitude != octaveAmplitudeHeard) {
            System.out.println( "cnt = " + cnt++ );
            flag = true;
        }
        if( currentFrequency != frequencyHeard
            || currentAmplitude != amplitudeHeard
            || currentOctaveAmplitude != octaveAmplitudeHeard ) {
            notifyFlag = true;
        }
        frequencyHeard = currentFrequency;
        amplitudeHeard = currentAmplitude;
        octaveAmplitudeHeard = currentOctaveAmplitude;
        if( notifyFlag ) {
            if( flag )
                System.out.println( "!!!" );
            notifyObservers();
        }
    }

    public double getFrequencyHeard() {
        return frequencyHeard;
    }

    public double getAmplitudeHeard() {
        return amplitudeHeard;
    }
}
