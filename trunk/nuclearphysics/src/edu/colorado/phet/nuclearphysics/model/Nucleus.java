/**
 * Class: Nucleus
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Nucleus extends NuclearModelElement {

    private int numProtons;
    private int numNeutrons;
    private double potential;
    private double radius;
    private PotentialProfile potentialProfile;
    private ArrayList fissionListeners = new ArrayList();

    public Nucleus( Point2D position, int numProtons, int numNeutrons ) {
        super( position, new Vector2D.Double(), new Vector2D.Double(), 0, 0 );
        this.setPosition( position.getX(), position.getY() );
        this.numProtons = numProtons;
        this.numNeutrons = numNeutrons;
        this.potentialProfile = new PotentialProfile( this );

        int numParticles = getNumNeutrons() + getNumProtons();
        double particleArea = ( Math.PI * NuclearParticle.RADIUS * NuclearParticle.RADIUS ) * numParticles;
        radius = Math.sqrt( particleArea / Math.PI ) / 3;
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    public double getRadius() {
        return radius;
    }

    public int getNumProtons() {
        return numProtons;
    }

    public int getNumNeutrons() {
        return numNeutrons;
    }

    public void setNumProtons( int numProtons ) {
        this.numProtons = numProtons;
        notifyObservers();
    }

    public void setNumNeutrons( int numNeutrons ) {
        this.numNeutrons = numNeutrons;
        notifyObservers();
    }

    public double getPotential() {
        return potential;
    }

    public void setPotential( double potential ) {
        this.potential = potential;
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }

    public void addFissionListener( FissionListener listener ) {
        fissionListeners.add( listener );
    }

    public void fission( Neutron neutron ) {
        FissionProducts fissionProducts = getFissionProducts( neutron );
        for( int i = 0; i < fissionListeners.size(); i++ ) {
            FissionListener fissionListener = (FissionListener)fissionListeners.get( i );
            fissionListener.fission( fissionProducts );
        }
    }

    public FissionProducts getFissionProducts( Neutron neutron ) {
        throw new RuntimeException( "Generic nucleus cannot undergo fission" );
    };
}
