/**
 * Class: Photon
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.photon;

import edu.colorado.phet.collision.Collidable;
import edu.colorado.phet.collision.CollidableAdapter;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.coreadditions.SubscriptionService;
import edu.colorado.phet.lasers.model.atom.Atom;

import javax.swing.event.EventListenerList;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;

/**
 *
 */
public class Photon extends Particle implements Collidable {

    ////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    static public double s_speed = 1;
    static public double s_radius = 10;
    static public int RED = 680;
    static public int DEEP_RED = 640;
    static public int BLUE = 400;
    static public int GRAY = Integer.MAX_VALUE;

    // Free pool of photons. We do this so we don't have to use the heap
    // at run-time
    static private int freePoolSize = 2000;
    static private ArrayList freePool = new ArrayList( freePoolSize );

    // Populate the free pool
    static {
        for( int i = 0; i < freePoolSize; i++ ) {
            freePool.add( new Photon() );
        }
    }

    static public Photon create() {
        Photon newPhoton = null;
        /*if( !freePool.isEmpty() ) {
            newPhoton = (Photon)freePool.remove( 0 );
        }
        else */{
            newPhoton = new Photon();
            //            freePool.add( new Photon() );
        }
        return newPhoton;
    }

    //    static public Photon create( Photon photon ) {
    //        Photon newPhoton = create();
    //        newPhoton.setVelocity( new Vector2D.Double( photon.getVelocity() ));
    //        newPhoton.setWavelength( photon.getWavelength() );
    //        newPhoton.numStimulatedPhotons = photon.numStimulatedPhotons;
    //        return newPhoton;
    //    }

    static public Photon createStimulated( Photon stimulatingPhoton ) {
        stimulatingPhoton.numStimulatedPhotons++;
        if( stimulatingPhoton.numStimulatedPhotons > 1 ) {
            //            System.out.println( "!!!" );
        }

        Photon newPhoton = create();
        newPhoton.setVelocity( new Vector2D.Double( stimulatingPhoton.getVelocity() ) );
        newPhoton.setWavelength( stimulatingPhoton.getWavelength() );
        int yOffset = stimulatingPhoton.numStimulatedPhotons * 8;
        newPhoton.setPosition( stimulatingPhoton.getPosition().getX(),
                               stimulatingPhoton.getPosition().getY() - yOffset );
        //                               stimulatingPhoton.getPosition().getY() - stimulatingPhoton.getRadius() );

        return newPhoton;
    }

    /**
     * If the photon is created by a CollimatedBeam, it should use this method,
     * so that the photon can tell the CollimatedBeam if it is leaving the system.
     *
     * @param beam
     */
    static public Photon create( CollimatedBeam beam ) {
        Photon newPhoton = create();
        newPhoton.beam = beam;
        newPhoton.setWavelength( beam.getWavelength() );
        return newPhoton;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Inner classes & interfaces
    //
    public class LeftSystemEvent extends EventObject {
        public LeftSystemEvent( Object source ) {
            super( source );
        }

        public Photon getPhoton() {
            return (Photon)source;
        }
    }

    public interface LeftSystemEventListener extends EventListener {
        public void leftSystemEventOccurred( LeftSystemEvent event );
    }

    /////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private EventListenerList listenerList = new EventListenerList();
    private int numObservers;
    private int numStimulatedPhotons;
    // If this photon was produced by the stimulation of another, this
    // is a reference to that photon.
    private Photon parentPhoton;
    // If this photon has stimulated the production of another photon, this
    // is a reference to that photon
    private Photon childPhoton;
    private CollidableAdapter collidableAdapter;
    private boolean isCollidable;

    public void addLeftSystemEventListener( LeftSystemEventListener listener ) {
        listenerList.add( LeftSystemEventListener.class, listener );
    }

    public void removeLeftSystemEventListener( LeftSystemEventListener listener ) {
        listenerList.remove( LeftSystemEventListener.class, listener );
    }

    private void fireLeftSystemEvent( LeftSystemEvent event ) {
        EventListener[] listeners = listenerList.getListeners( LeftSystemEventListener.class );
        for( int i = 0; i < listeners.length; i++ ) {
            LeftSystemEventListener listener = (LeftSystemEventListener)listeners[i];
            listener.leftSystemEventOccurred( event );
        }
    }

    public synchronized void addObserver( SimpleObserver o ) {
        super.addObserver( o );
        if( numObservers > 0 ) {
            System.out.println( "$$$" );
        }
        numObservers++;
    }


    private double wavelength;
    private CollimatedBeam beam;
    // This list keeps track of atoms that the photon has collided with
    private ArrayList contactedAtoms = new ArrayList();

    /**
     * Constructor is private so that clients of the class must use static create()
     * methods. This allows us to manage a free pool of photons and not hit the
     * heap so hard.
     */
    private Photon() {
        collidableAdapter = new CollidableAdapter( this );
        setVelocity( s_speed, 0 );
        //        setMass( 1 );
    }

    private SubscriptionService bulletinBoard = new SubscriptionService();

    public interface Listener {
        void leavingSystem( Photon photon );
    }

    public void addListener( Listener listener ) {
        bulletinBoard.addListener( listener );
    }

    public void removeListener( Listener listener ) {
        bulletinBoard.removeListener( listener );
    }

    /**
     * Rather than use the superclass behavior, the receiver
     * puts itself in the class free pool, so it can be used
     * again. This helps prevent us from flogging the heap.
     */
    public void removeFromSystem() {
        bulletinBoard.notifyListeners( new SubscriptionService.Notifier() {
            public void doNotify( Object obj ) {
                ( (Listener)obj ).leavingSystem( Photon.this );
            }
        } );
        if( beam != null ) {
            beam.removePhoton( this );
        }
        this.removeAllObservers();
        //        freePool.add( this );
        //        setChanged();
        //        notifyObservers( Particle.S_REMOVE_BODY );
    }

    public double getWavelength() {
        return wavelength;
    }

    public void setWavelength( double wavelength ) {
        this.wavelength = wavelength;
    }

    public double getEnergy() {
        // Some function based on wavelength
        return ( 1 / getWavelength() );
    }

    public boolean hasCollidedWithAtom( Atom atom ) {
        return contactedAtoms.contains( atom );
    }

    public Photon getParentPhoton() {
        return parentPhoton;
    }

    public void setParentPhoton( Photon parentPhoton ) {
        this.parentPhoton = parentPhoton;
    }

    public Photon getChildPhoton() {
        return childPhoton;
    }

    public void setChildPhoton( Photon childPhoton ) {
        this.childPhoton = childPhoton;
    }

    public Vector2D getVelocityPrev() {
        return collidableAdapter.getVelocityPrev();
    }

    public Point2D getPositionPrev() {
        return collidableAdapter.getPositionPrev();
    }

    public void stepInTime( double dt ) {
        collidableAdapter.stepInTime( dt );
        super.stepInTime( dt );
    }
}
