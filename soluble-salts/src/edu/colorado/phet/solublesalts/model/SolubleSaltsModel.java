/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model;

import edu.colorado.phet.collision.CollisionExpert;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.control.SolubleSaltsControlPanel;
import edu.colorado.phet.solublesalts.model.crystal.Crystal;
import edu.colorado.phet.solublesalts.model.salt.Salt;
import edu.colorado.phet.solublesalts.model.ion.Ion;
import edu.colorado.phet.solublesalts.model.ion.IonListener;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

/**
 * SolubleSaltsModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SolubleSaltsModel extends BaseModel {

    //----------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------

    private static final double MIN_SPEED = 1E-3;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    // The world bounds of the model
    private double scale = 1;
//    Rectangle2D bounds = new Rectangle2D.Double( 0, 0, 9000, 6000 );
    Rectangle2D bounds = new Rectangle2D.Double( 0, 0, 900, 600 );

    // Ksp for the model
    double ksp;

    // The vessel
    private Vessel vessel;
    private Point2D vesselLoc = new Point2D.Double( SolubleSaltsConfig.VESSEL_ULC.getX() * scale,
                                                    SolubleSaltsConfig.VESSEL_ULC.getY() * scale );
    private double vesselWidth = SolubleSaltsConfig.VESSEL_SIZE.getWidth() * scale;
    private double vesselDepth = SolubleSaltsConfig.VESSEL_SIZE.getHeight() * scale;
    private double vesselWallThickness = SolubleSaltsConfig.VESSEL_WALL_THICKNESS * scale;

    // The faucet and drain
    private WaterSource waterSource;
    private Drain drain;

    // Collision mechanism objects
    IonIonCollisionExpert ionIonCollisionExpert = new IonIonCollisionExpert( this );

    private IonTracker ionTracker;
    private HeatSource heatSource;
    private boolean nucleationEnabled;
    private Shaker shaker;
    private LatticeTracker latticeTracker;
    private Vector2D accelerationOutOfWater = new Vector2D.Double( 0, SolubleSaltsConfig.DEFAULT_LATTICE_ACCELERATION );
    private Vector2D accelerationInWater = new Vector2D.Double();

    //---------------------------------------------------------------
    // Constructor and lifecycle methods
    //---------------------------------------------------------------
    
    public SolubleSaltsModel() {

        // Add an agent that will track the ions of various classes
        ionTracker = new IonTracker();
//        addIonListener( ionTracker );

        // Add an agent that will track the lattices that come and go from the model
        latticeTracker = new LatticeTracker();
        addLatticeListener( latticeTracker );

        // Add an agent that will track the creation and destruction of salt lattices
        Crystal.addInstanceLifetimeListener( new LatticeLifetimeTracker() );

        // Create a vessel
        vessel = new Vessel( vesselWidth, vesselDepth, vesselWallThickness, vesselLoc );
        vessel.setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL * scale );
        addModelElement( vessel );

        // Create the faucet and drain
        waterSource = new WaterSource( this );
        waterSource.setPosition( vessel.getLocation().getX() + 35, vessel.getLocation().getY() - 10 );
//        waterSource.setPosition( vessel.getLocation().getX() + 350, vessel.getLocation().getY() - 100 );
        addModelElement( waterSource );
        drain = new Drain( this );
        drain.setPosition( vessel.getLocation().getX() - vessel.getWallThickness(),
                           vessel.getLocation().getY() + vessel.getDepth() - 50 );
//                           vessel.getLocation().getY() + vessel.getDepth() - 500 );
        addModelElement( drain );

        // Add a model element that will handle collisions between ions and the vessel
        addModelElement( new IonVesselCollisionAgent() );

        // Add a heat source/sink
        heatSource = new HeatSource( this );
        addModelElement( heatSource );

        // Add a model element that will flip nucleation on and off depending on the
        // concentration of solutes and the Ksp
        addModelElement( new NucleationMonitorAgent() );

        // Add a shaker
        shaker = new Shaker( this );
        shaker.setPosition( vessel.getLocation().getX() + vessel.getWidth() / 2,
                            vessel.getLocation().getY() - 10 );
    }

    //Not allowed to mess with the way we call our abstract method.
    public void stepInTime( double dt ) {
        super.stepInTime( dt );

        // If a lattice is not in the water, it accelerates downward. If it's in the water, it moves
        // at a constant speed
        List lattices = latticeTracker.getLattices();
        for( int i = 0; i < lattices.size(); i++ ) {
            Crystal crystal = (Crystal)lattices.get( i );
            if( !vessel.getWater().getBounds().contains( crystal.getPosition() ) &&
                !crystal.getAcceleration().equals( accelerationOutOfWater )) {
                crystal.setAcceleration( accelerationOutOfWater );
            }
            else if( vessel.getWater().getBounds().contains( crystal.getPosition() ) &&
                !crystal.getAcceleration().equals( accelerationInWater )) {
                crystal.setAcceleration( accelerationInWater );
                crystal.setVelocity( 0, SolubleSaltsConfig.DEFAULT_LATTICE_SPEED );
            }
        }
    }

    /**
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        super.addModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            Ion ion = (Ion)modelElement;
            ionTracker.ionAdded( ion );
        }

        if( modelElement instanceof Crystal ) {
            latticeListenerProxy.latticeAdded( new LatticeEvent( modelElement ) );
        }
    }

    /**
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        super.removeModelElement( modelElement );

        if( modelElement instanceof Ion ) {
            Ion ion = (Ion)modelElement;
            ionTracker.ionRemoved( ion );
            if( ion.isBound() ) {
                ion.getBindingLattice().removeIon( ion );
            }
        }

        if( modelElement instanceof Crystal ) {
            latticeListenerProxy.latticeRemoved( new LatticeEvent( modelElement ) );
        }
    }

    public void reset() {
        List ions = ionTracker.getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            removeModelElement( ion );
        }
        vessel.setWaterLevel( SolubleSaltsConfig.DEFAULT_WATER_LEVEL * scale );
        waterSource.setFlow( 0 );
        drain.setFlow( 0 );
        heatSource.setHeatChangePerClockTick( 0 );
    }
    
    //----------------------------------------------------------------
    // Getters and setters
    //----------------------------------------------------------------

    public boolean isNucleationEnabled() {
        return nucleationEnabled;
    }

    public double getScale() {
        return scale;
    }

    public Rectangle2D getBounds() {
        return bounds;
    }

    public double getKsp() {
        return ksp;
    }

    public void setKsp( double ksp ) {
        this.ksp = ksp;
    }

    public Vessel getVessel() {
        return vessel;
    }

    public WaterSource getFaucet() {
        return waterSource;
    }

    public Drain getDrain() {
        return drain;
    }

    public int getNumIonsOfType( Class ionClass ) {
        return ionTracker.getNumIonsOfType( ionClass );
    }

    public List getIonsOfType( Class ionClass ) {
        return ionTracker.getIonsOfType( ionClass );
    }

    public int getNumFreeIonsOfType( Class ionClass ) {
        return ionTracker.getNumFreeIonsOfType( ionClass );
    }

    public List getIons() {
        return ionTracker.getIons();
    }

    public HeatSource getHeatSource() {
        return heatSource;
    }

    public double getConcentration() {
        int numIons = ionTracker.getIons().size();
        double volume = vessel.getVolume();
        return ( (double)numIons ) / volume;
    }

    public Shaker getShaker() {
        return shaker;
    }

    /**
     * Adds kinetic energy to all the ions in the system
     *
     * @param heat
     */
    public void addHeat( double heat ) {
        List ions = getIons();
        for( int i = 0; i < ions.size(); i++ ) {
            Ion ion = (Ion)ions.get( i );
            double speed0 = ion.getVelocity().getMagnitude();
            double speed1 = Math.sqrt( Math.max( MIN_SPEED, speed0 * speed0 + ( 2 * heat / ion.getMass() ) ) );
            if( ion.getVelocity().getMagnitude() > 0 ) {
                ion.setVelocity( ion.getVelocity().normalize().scale( speed1 ) );
            }
        }
    }

    public double getIonConcentration( Class ionClass ) {
        List ions = ionTracker.getIonsOfType( ionClass );
        double result = 0;
        if( ions != null ) {
            for( int i = 0; i < ions.size(); i++ ) {
                Ion ion = (Ion)ions.get( i );
                if( !ion.isBound() ) {
                    result++;
                }
            }
        }
        return result;
    }

    //-----------------------------------------------------------------
    // Change events and listeners
    //-----------------------------------------------------------------
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject  {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public  SolubleSaltsModel getModel() {
            return (SolubleSaltsModel)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void stateChanged( ChangeEvent event );
    }

    //----------------------------------------------------------------
    // Events and listeners for Lattices
    //----------------------------------------------------------------
    private EventChannel latticeEventChannel = new EventChannel( LatticeListener.class );
    private LatticeListener latticeListenerProxy = (LatticeListener)latticeEventChannel.getListenerProxy();

    public void addLatticeListener( LatticeListener listener ) {
        latticeEventChannel.addListener( listener );
    }

    public void removeLatticeListener( LatticeListener listener ) {
        latticeEventChannel.removeListener( listener );
    }

    public void addIonListener( IonListener listener ) {
        ionTracker.addIonListener( listener );
    }

    public void removeIonListener( IonListener listener ) {
        ionTracker.removeIonListener( listener );
    }

    public void setCurrentSalt( Salt salt ) {
        shaker.setCurrentSalt( salt );
        changeListenerProxy.stateChanged( new ChangeEvent( this ) );
    }

    public Salt getCurrentSalt() {
        return shaker.getCurrentSalt();
    }

    public class LatticeEvent extends EventObject {
        public LatticeEvent( Object source ) {
            super( source );
        }

        public Crystal getLattice() {
            return (Crystal)getSource();
        }
    }

    public interface LatticeListener extends EventListener {
        void latticeAdded( LatticeEvent event );

        void latticeRemoved( LatticeEvent event );
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Detects and handles collisions between ions and the vessel
     */
    private class IonVesselCollisionAgent implements ModelElement {
        IonVesselCollisionExpert ionVesselCollisionExpert = new IonVesselCollisionExpert( SolubleSaltsModel.this );

        public void stepInTime( double dt ) {

            // Look for collisions between ions
            for( int i = 0; i < numModelElements(); i++ ) {
                if( modelElementAt( i ) instanceof Ion ) {
                    Ion ion = (Ion)modelElementAt( i );
                    ionVesselCollisionExpert.detectAndDoCollision( ion, vessel );

                    for( int j = 0; j < numModelElements(); j++ ) {
                        if( modelElementAt( i ) != modelElementAt( j )
                            && modelElementAt( j ) instanceof Ion ) {
                            ionIonCollisionExpert.detectAndDoCollision( (Ion)modelElementAt( i ),
                                                                        (Ion)modelElementAt( j ) );
                        }
                    }
                }
            }
        }
    }

    /**
     * Tracks the creation and destruction of lattice instances
     */
    private class LatticeLifetimeTracker implements Crystal.InstanceLifetimeListener {
        public void instanceCreated( Crystal.InstanceLifetimeEvent event ) {
            addModelElement( event.getInstance() );
        }

        public void instanceDestroyed( Crystal.InstanceLifetimeEvent event ) {
            removeModelElement( event.getInstance() );
        }
    }

    /**
     * Turns nucleation on and off depending on the concentration of solutes and Ksp
     */
    private class NucleationMonitorAgent implements ModelElement, Crystal.InstanceLifetimeListener {
        List lattices = new ArrayList();

        public NucleationMonitorAgent() {
            Crystal.addInstanceLifetimeListener( this );
        }

        public void stepInTime( double dt ) {
            nucleationEnabled = getConcentration() > ksp;

            if( !nucleationEnabled ) {
                while( !lattices.isEmpty() ) {
                    Crystal crystal = (Crystal)lattices.get( 0 );
                    List ions = crystal.getIons();
                    for( int i = 0; i < ions.size(); i++ ) {
                        Ion ion = (Ion)ions.get( i );
                        ion.unbindFrom( crystal );
                    }
                }
            }
        }

        public void instanceCreated( Crystal.InstanceLifetimeEvent event ) {
            lattices.add( event.getInstance() );
        }

        public void instanceDestroyed( Crystal.InstanceLifetimeEvent event ) {
            lattices.remove( event.getInstance() );
        }
    }
}
