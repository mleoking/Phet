/**
 * Class: CollimatedBeam
 * Package: edu.colorado.phet.lasers.model.photon
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.photon;


import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;
import edu.colorado.phet.lasers.model.LaserModel;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.Random;

/**
 * A CollimatedBeam is a collection of photons that all have identical
 * velocities. The beam has a height, and the photons are randomly distributed
 * across that height.
 */
public class CollimatedBeam extends Particle {

    private static Random gaussianGenerator = new Random();

    private double nextTimeToProducePhoton = 0;
    private int wavelength;
    private Rectangle2D bounds;
    private Vector2D velocity;
    // The rate at which the beam produces photons
    private double timeSinceLastPhotonProduced = 0;
    // Used to deterimine when photons should be produced
    private double photonsPerSecond = 30;
    // Is the collimated beam currently generating photons?
    private boolean isActive;
    private LaserModel model;
    private SubscriptionService bulletinBoard = new SubscriptionService();
    private LinkedList photons = new LinkedList();

    public interface Listener {
        void photonCreated( CollimatedBeam beam, Photon photon );
    }

    public CollimatedBeam( LaserModel model, int wavelength, Point2D origin, double height, double width, Vector2D direction ) {
        this.model = model;
        this.wavelength = wavelength;
        this.bounds = new Rectangle2D.Double( origin.getX(), origin.getY(), width, height );
        this.setPosition( origin );
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.s_speed );
    }

    public void setBounds( Rectangle2D rect ) {
        this.bounds = rect;
        this.setPosition( new Point2D.Double( rect.getX(), rect.getY() ) );
    }

    public void setDirection( Vector2D.Double direction ) {
        this.velocity = new Vector2D.Double( direction ).normalize().scale( Photon.s_speed );
    }

    public void addListener( Listener listener ) {
        bulletinBoard.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        bulletinBoard.removeListener( listener );
    }

    public double getHeight() {
        return bounds.getHeight();
    }

    public void setHeight( double height ) {
        this.bounds.setRect( bounds.getX(), bounds.getY(), bounds.getWidth(), height );
    }

    public double getWidth() {
        return bounds.getHeight();
    }

    public void setWidth( double width ) {
        this.bounds.setRect( bounds.getX(), bounds.getY(), width, bounds.getHeight() );
    }

    public double getPhotonsPerSecond() {
        return photonsPerSecond;
    }

    public void setPhotonsPerSecond( double photonsPerSecond ) {

        // The following if statement prevents the system from sending out a big
        // wave of photons if it has been set at a rate of 0 for awhile.
        if( this.photonsPerSecond == 0 ) {
            timeSinceLastPhotonProduced = 0;
        }
        this.photonsPerSecond = photonsPerSecond;
        nextTimeToProducePhoton = getNextTimeToProducePhoton();
    }

    public int getWavelength() {
        return wavelength;
    }

    public void addPhoton() {
        final Photon newPhoton = Photon.create( this );
        newPhoton.setPosition( genPositionX(), genPositionY() /* + newPhoton.getRadius() */ );
        newPhoton.setVelocity( new Vector2D.Double( velocity ) );
        newPhoton.setWavelength( this.wavelength );
        model.addModelElement( newPhoton );
        photons.add( newPhoton );
        bulletinBoard.notifyListeners( new SubscriptionService.Notifier() {
            public void doNotify( Object obj ) {
                ( (Listener)obj ).photonCreated( CollimatedBeam.this, newPhoton );
            }
        } );
    }

    public void removePhoton( Photon photon ) {
        photons.remove( photon );
        model.removeModelElement( photon );
    }


    public class PhotonEmittedEvent extends EventObject {
        public PhotonEmittedEvent( Object source ) {
            super( source );
        }
    }

    public interface PhotonEmittedEventListener extends EventListener {
        public void photonEmittedEventOccurred( PhotonEmittedEvent event );
    }

    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // Produce photons
        if( isActive() ) {
            timeSinceLastPhotonProduced += dt;
            if( nextTimeToProducePhoton < timeSinceLastPhotonProduced ) {
                timeSinceLastPhotonProduced = 0;
                this.addPhoton();
                nextTimeToProducePhoton = getNextTimeToProducePhoton();
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive( boolean active ) {
        isActive = active;
        timeSinceLastPhotonProduced = 0;
    }

    private double genPositionY() {
        double yDelta = velocity.getX() != 0 ? Math.random() * bounds.getHeight() : 0;
        return this.getPosition().getY() + yDelta;
    }

    private double genPositionX() {
        double xDelta = velocity.getY() != 0 ?
                        Math.random() * bounds.getWidth() : 0;
        return this.getPosition().getX() + xDelta;
    }

    private double getNextTimeToProducePhoton() {
        double temp = ( gaussianGenerator.nextGaussian() + 1.0 );
        temp = 1;
        return temp / ( photonsPerSecond / 1000 );
    }
}
