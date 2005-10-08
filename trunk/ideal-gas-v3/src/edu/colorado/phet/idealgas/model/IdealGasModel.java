/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.collision.*;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.geom.Area;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 *
 */
public class IdealGasModel extends BaseModel implements Gravity.ChangeListener {

    // The default energy given to a molecule
    public static final float DEFAULT_ENERGY = 15000;
    // The distance that a molecule travels out from the box before it
    // is removed from the system.
    private static double s_escapeOffset = -30;
    private static final float s_pressureAdjustmentFactor = 0.05f;

    public static final int CONSTANT_NONE = 0, CONSTANT_VOLUME = 1, CONSTANT_PRESSURE = 2, CONSTANT_TEMPERATURE = 3;

    private Gravity gravity;
    private double heatSource;
    private PressureSensingBox box;
    private int constantProperty = CONSTANT_NONE;
    private double targetPressure = 0;
    private double averageMoleculeEnergy;
    // Accumulates kinetic energy deliberately added to the system in a single time step.
    private double deltaKE = 0;
    // Accumulates potential energy deliberately added to the system in a single time step.
    private double deltaPE = 0;
    private ArrayList externalForces = new ArrayList();
    private List prepCommands = Collections.synchronizedList( new ArrayList() );
    // A utility list used in stepInTime
    private ArrayList removeList = new ArrayList();

    private ArrayList bodies = new ArrayList();
    private CollisionGod collisionGod;
    // Flag used to tell if energy being added to the system should be tracked. If energy
    // is added while within the scope of doYourThing(), it must be tracked
    private boolean currentlyInStepInTimeMethod;

    private List collisionExperts = new ArrayList();
    private double averageHeavySpeciesSpeed;
    private double averageLightSpeciesSpeed;
    private int heavySpeciesCnt;
    private int lightSpeciesCnt;
    private Shape modelBounds;
//    private Rectangle2D modelBounds;
    private double targetTemperature;

    /**
     * @param dt
     */
    public IdealGasModel( double dt ) {
        // Add a collision collisionGod
        collisionGod = new CollisionGod( this, dt,
                                         new Rectangle2D.Double( 0, 0,
                                                                 600,
                                                                 600 ),
                                         10, 10 );
    }

    /**
     * Adds a CollisionExpert to those that are consulted when the model steps
     *
     * @param expert
     */
    public void addCollisionExpert( CollisionExpert expert ) {
        collisionExperts.add( expert );
    }

    //----------------------------------------------------------------
    // Methods related to the constant gas property
    //----------------------------------------------------------------

    public boolean isConstantVolume() {
        return constantProperty == CONSTANT_VOLUME;
    }

    public boolean isConstantPressure() {
        return constantProperty == CONSTANT_PRESSURE;
    }

    public boolean isConstantTemperature() {
        return constantProperty == CONSTANT_TEMPERATURE;
    }

    public boolean isConstantNone() {
        return !( isConstantPressure()
                  || isConstantTemperature()
                  || isConstantVolume() );
    }

    public void setConstantProperty( int constantProperty ) {
        switch( constantProperty ) {
            case CONSTANT_NONE:
                box.setVolumeFixed( false );
                SphereBoxCollision.setWorkDoneByMovingWall( true );
                break;
            case CONSTANT_VOLUME:
                box.setVolumeFixed( true );
                SphereBoxCollision.setWorkDoneByMovingWall( true );
                break;
            case CONSTANT_PRESSURE:
                box.setVolumeFixed( false );
                this.targetPressure = box.getPressure();
                SphereBoxCollision.setWorkDoneByMovingWall( false );
                break;
            case CONSTANT_TEMPERATURE:
                box.setVolumeFixed( false );
                double t = getTemperature();
                this.targetTemperature = !Double.isNaN( t ) ? t : IdealGasModel.DEFAULT_ENERGY;
                SphereBoxCollision.setWorkDoneByMovingWall( true );
                break;
            default:
                throw new RuntimeException( "Invalid constantProperty" );
        }
        this.constantProperty = constantProperty;
    }

    /**
     * Adjusts the model to keep the specified constant parameter constant
     */
    private void updateFreeParameter() {
        switch( constantProperty ) {
            case CONSTANT_PRESSURE:
                double currPressure = box.getPressure();
                double diffPressure = ( currPressure - targetPressure ) / targetPressure;
                if( currPressure > 0 && diffPressure > s_pressureAdjustmentFactor ) {
                    box.setBounds( box.getMinX() - 1,
                                   box.getMinY(),
                                   box.getMaxX(),
                                   box.getMaxY() );
                }
                else if( currPressure > 0 && diffPressure < -s_pressureAdjustmentFactor ) {
                    box.setBounds( box.getMinX() + 1,
                                   box.getMinY(),
                                   box.getMaxX(),
                                   box.getMaxY() );
                }
                break;
            case CONSTANT_TEMPERATURE:
                double currTemp = getTemperature();
                // Factor of 100 here is just a convenient number to get this working right. The variable set
                // here is used to control how much the system should react to changes in temperature.
                double diffTemp = 0;
                // Check for currTemp not being a good number. This happens when there are currently no molecules
                // in the model
                if( !Double.isNaN( currTemp ) ) {
                    diffTemp = 100 * ( targetTemperature - currTemp ) / targetTemperature;
                }
                setHeatSource( diffTemp );
                break;
        }
    }

    /**
     *
     */
    public Gravity getGravity() {
        return gravity;
    }

    /**
     *
     */
    public void setHeatSource( double value ) {
        heatSource = value;
        heatSourceChangeListenerProxy.heatSourceChanged( new HeatSourceChangeEvent( this ) );
    }

    /**
     * Returns the amount of heat added to or removed from the model in each time step
     *
     * @return
     */
    public double getHeatSource() {
        return heatSource;
    }

    /**
     *
     */
    public void addBox( PressureSensingBox box ) {
        this.box = box;
        this.addModelElement( box );
    }

    /**
     *
     */
    public PressureSensingBox getBox() {
        return box;
    }

    /**
     * @param modelElement
     */
    public void addModelElement( ModelElement modelElement ) {
        if( modelElement instanceof Gravity ) {
            addExternalForce( modelElement );
            this.gravity = (Gravity)modelElement;
            adjustEnergyForGravity( gravity.getAmt() );
            gravity.addListener( this );
        }
        else {
            super.addModelElement( modelElement );
            if( modelElement instanceof Body ) {
                Body body = (Body)modelElement;
                // Since model elements are added outside the doYourThing() loop, their energy
                // is accounted for already, and doesn't need to be added here
                if( currentlyInStepInTimeMethod ) {
                    addKineticEnergyToSystem( body.getKineticEnergy() );
                }
                bodies.add( body );
            }
            if( modelElement instanceof HeavySpecies ) {
                heavySpeciesCnt++;
            }
            if( modelElement instanceof LightSpecies ) {
                lightSpeciesCnt++;
            }
            if( modelElement instanceof Box2D ) {
                Box2D box = (Box2D)modelElement;
                box.addChangeListener( new Box2D.ChangeListenerAdapter() {
                    public void boundsChanged( Box2D.ChangeEvent event ) {
                        setModelBounds( null );
                    }
                } );
            }
        }
    }

    /**
     * @param modelElement
     */
    public void removeModelElement( ModelElement modelElement ) {
        // Account for change in the energy of the system
        if( modelElement instanceof Body ) {
            if( currentlyInStepInTimeMethod ) {
                addKineticEnergyToSystem( -( (Body)modelElement ).getKineticEnergy() );
            }
            bodies.remove( modelElement );
        }

        if( modelElement instanceof GasMolecule ) {
            ( (GasMolecule)modelElement ).removeYourselfFromSystem();
        }
        if( modelElement instanceof HeavySpecies ) {
            HeavySpecies.removeParticle( (HeavySpecies)modelElement );
            heavySpeciesCnt--;
        }
        if( modelElement instanceof LightSpecies ) {
            LightSpecies.removeParticle( (LightSpecies)modelElement );
            lightSpeciesCnt--;
        }

        // Handle the special case where the model element is gravity
        if( modelElement instanceof Gravity ) {
            removeExternalForce( modelElement );
            this.gravity = null;
        }
        else {
            super.removeModelElement( modelElement );
        }
    }

    /**
     * It seems that things work right when this doesn't do anything
     *
     * @param change
     */
    private void adjustEnergyForGravity( double change ) {
        double deltaPE = 0;

        // DO NOTHING!!!
        if( true ) {
            return;
        }

        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof Body ) {
                Body body = (Body)element;
                // Don't consider bodies that have infinite mass. That indicates that
                // they are imobile
                if( body.getMass() != Double.POSITIVE_INFINITY ) {
                    deltaPE = body.getPosition().getY() * change * body.getMass();
                }
            }
        }
        deltaKE += deltaPE;
    }

    public synchronized /* 3/11/04 */ void addExternalForce( ModelElement force ) {
        externalForces.add( force );
    }

    public synchronized /* 3/11/04 */ void removeExternalForce( ModelElement force ) {
        externalForces.remove( force );
    }

    /**
     * To be called by objects that deliberately add energy to the system within the doYourThing() method
     *
     * @param dKE
     */
    public void addKineticEnergyToSystem( double dKE ) {
        deltaKE += dKE;
    }

    public void addPotentialEnergyToSystem( double dPE ) {
        deltaPE += dPE;
    }

    /**
     * Returns the total energy OF THE GAS PARTICLES in the model. Note that this
     * does not include the energy of any balloons, etc.
     *
     * @return
     */
    public double getTotalGasEnergy() {
        double eTotal = 0;
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof GasMolecule ) {
                Body body = (Body)element;
                eTotal += getBodyEnergy( body );
            }
        }
        return eTotal;
    }

    /**
     * Returns the total energy in the model.
     *
     * @return
     */
    public double getTotalEnergy() {
        double eTotal = 0;
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof Body ) {
                Body body = (Body)element;
                eTotal += getBodyEnergy( body );
            }
        }
        return eTotal;
    }

    /**
     * Gets the temperature of the gas in the model
     *
     * @return
     */
    public double getTemperature() {
        double totalKE = 0;
        int numBodies = 0;
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof GasMolecule ) {
                numBodies++;
                GasMolecule body = (GasMolecule)element;
                double ke = body.getKineticEnergy();
                if( Double.isNaN( ke ) ) {
                    System.out.println( "Total kinetic energy in system NaN: " + body.getClass() );
                }
                else {
                    totalKE += ke;
                }
            }
        }
        return totalKE / numBodies;
    }

    /**
     * @return
     */
    public double getTotalKineticEnergy() {
        double totalKE = 0;
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement element = this.modelElementAt( i );
            if( element instanceof Body ) {
                Body body = (Body)element;
                double ke = body.getKineticEnergy();
                if( Double.isNaN( ke ) ) {
                    System.out.println( "Total kinetic energy in system NaN: " + body.getClass() );
                }
                else {
                    totalKE += ke;
                }
            }
        }
        return totalKE;
    }

    /**
     * Step the model through a tick of the clock
     *
     * @param dt
     */
    public void stepInTime( double dt ) {

        // Managing energy step 1: Get the total energy in the system
        // before anything happens
        currentlyInStepInTimeMethod = true;
        double totalEnergyPre = this.getTotalEnergy();
        double totalKEPre = this.getTotalKineticEnergy();

        // Clear the accelerations on the bodies in the model
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            body.setAccelerationNoUpdate( 0, 0 );
        }

        // Apply external forces (e.g., gravity )
        for( int i = 0; i < externalForces.size(); i++ ) {
            ModelElement me = (ModelElement)externalForces.get( i );
            me.stepInTime( dt );
        }

        // Add or remove heat depending on the state of the stove
        addHeatFromStove();

        // Call the superclass behavior
        super.stepInTime( dt );

        // Detect and handle collisions
        collisionGod.doYourThing( dt, collisionExperts );

        // Managing energy, step 2: Get the total energy in the system,
        // and adjust it if neccessary
        double totalEnergyPost = this.getTotalEnergy();
        double totalKEPost = this.getTotalKineticEnergy();
        double dE = totalEnergyPost - ( totalEnergyPre + deltaKE );
        double r0 = dE / totalKEPost;
        double ratio = Math.sqrt( 1 - r0 );

        // Clear the added-energy accumulator
        deltaKE = 0;

        if( totalEnergyPre != 0 && ratio != 1 ) {
            for( int i = 0; i < this.numModelElements(); i++ ) {
                ModelElement element = this.modelElementAt( i );
                if( element instanceof Body ) {
                    Body body = (Body)element;
                    double vx = body.getVelocity().getX();
                    double vy = body.getVelocity().getY();
                    vx *= ratio;
                    vy *= ratio;
                    if( !Double.isNaN( ratio ) && body.getKineticEnergy() > 0 ) {
                        body.setVelocity( vx, vy );
                    }
                }
            }
        }

        // Remove any molecules from the system that have escaped the box
        // The s_escapeOffset in the if statement is to let the molecules float outside
        // the box before they go away completely
        for( int i = 0; i < this.numModelElements(); i++ ) {
            ModelElement body = this.modelElementAt( i );
            if( body instanceof GasMolecule ) {
                GasMolecule gasMolecule = (GasMolecule)body;
                if( !modelBounds.contains( gasMolecule.getPosition() ) ) {
                    removeList.add( gasMolecule );
                }
            }
        }
        for( int i = 0; i < removeList.size(); i++ ) {
            GasMolecule gasMolecule = (GasMolecule)removeList.get( i );
            this.bodies.remove( gasMolecule );
            removeModelElement( gasMolecule );
        }
        removeList.clear();

        // Compute some useful statistics
        int numGasMolecules = 0;
        int totalEnergy = 0;
        double totalLightSpeed = 0;
        double totalHeavySpeed = 0;
        for( int i = 0; i < numModelElements(); i++ ) {
            Object body = modelElementAt( i );
            if( body instanceof GasMolecule ) {
                GasMolecule gasMolecule = (GasMolecule)body;
                totalEnergy += this.getBodyEnergy( gasMolecule );
                if( body instanceof HeavySpecies ) {
                    totalHeavySpeed += gasMolecule.getSpeed();
                }
                if( body instanceof LightSpecies ) {
                    totalLightSpeed += gasMolecule.getSpeed();
                }
                numGasMolecules++;
            }
        }
        averageMoleculeEnergy = numGasMolecules != 0 ? totalEnergy / numGasMolecules : 0;
        averageHeavySpeciesSpeed = getHeavySpeciesCnt() > 0 ? totalHeavySpeed / getHeavySpeciesCnt() : 0;
        averageLightSpeciesSpeed = getLightSpeciesCnt() > 0 ? totalLightSpeed / getLightSpeciesCnt() : 0;
        currentlyInStepInTimeMethod = false;

        // Update either pressure or volume
        updateFreeParameter();

        notifyObservers();
    }

    /**
     *
     */
    private void addHeatFromStove() {
        if( heatSource != 0 ) {
            for( int i = 0; i < numModelElements(); i++ ) {
                Object modelElement = modelElementAt( i );
                if( modelElement instanceof CollidableBody && !IdealGasConfig.HEAT_ONLY_FROM_FLOOR ) {
                    CollidableBody body = (CollidableBody)modelElement;
                    double preKE = body.getKineticEnergy();
                    body.setVelocity( body.getVelocity().scale( 1 + heatSource / 10000 ) );
                    double incrKE = body.getKineticEnergy() - preKE;
                    if( currentlyInStepInTimeMethod ) {
                        this.addKineticEnergyToSystem( incrKE );
                    }
                }
            }
        }
    }

    /**
     * @param body
     * @return
     */
    public double getBodyEnergy( Body body ) {
        double energy = body.getKineticEnergy() + getPotentialEnergy( body );
        return energy;
    }

    /**
     * @param body
     * @return
     */
    public double getPotentialEnergy( Body body ) {
        double pe = 0;
        if( this.gravity != null ) {
            double gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {
                double origin = this.getBox().getMaxY();
                if( body.getMass() != Double.POSITIVE_INFINITY ) {
                    pe = ( origin - body.getPosition().getY() ) * gravity * body.getMass();
                }
            }
        }
        return pe;
    }

    /**
     * Moves a body to a y coordinate while preserving its total energy
     */
    public void relocateBodyY( Body body, double newY ) {

        double currY = body.getPosition().getY();

        // This was commented out, 9/14/04
        //        relocateBodyY( body, newY );

        // Adjust the body's kinetic energy to compensate for any change we may have
        // made in its potential ential
        if( this.gravity != null ) {
            double gravity = this.getGravity().getAmt();
            if( gravity != 0 ) {

                // Note that the inverted y axis that we use requires this
                // subtraction to be performed in the order shown.
                double dY = currY - newY;

                // What is the change in energy represented by moving the body?
                double dE = -( gravity * dY * body.getMass() );
                double currSpeed = body.getVelocity().getMagnitude();
                double newSpeed = (float)Math.sqrt( ( 2 * dE ) / body.getMass()
                                                    + currSpeed * currSpeed );

                // Flip the sign because our y axis is positive downward
                if( Double.isNaN( newSpeed ) ) {
                    newSpeed = currSpeed;
                }
                body.getVelocity().scale( newSpeed / currSpeed );
            }
        }
    }

    public double getAverageMoleculeEnergy() {
        return averageMoleculeEnergy;
    }

    public void addPrepCmd( Command command ) {
        prepCommands.add( command );
    }

    public List getBodies() {
        return bodies;
    }

    /**
     * Returns the average energy of the gas molecules in the model
     *
     * @return
     */
    public double getAverageGasEnergy() {
        return getTotalGasEnergy() / getNumMolecules();
    }

    public int getNumMolecules() {
        return ( getHeavySpeciesCnt() + getLightSpeciesCnt() );
    }

    public int getHeavySpeciesCnt() {
        return heavySpeciesCnt;
    }

    public int getLightSpeciesCnt() {
        return lightSpeciesCnt;
    }

    public double getHeavySpeciesAveSpeed() {
        return averageHeavySpeciesSpeed;
    }

    public double getLightSpeciesAveSpeed() {
        return averageLightSpeciesSpeed;
    }

    public void removeAllMolecules() {
        ArrayList removalList = new ArrayList();
        // Collect the elements to be removed from the system
        for( int i = 0; i < bodies.size(); i++ ) {
            ModelElement modelElement = (ModelElement)bodies.get( i );
            if( modelElement instanceof GasMolecule ) {
                removalList.add( modelElement );
            }
        }
        // Remove them from the system
        for( int i = 0; i < removalList.size(); i++ ) {
            ModelElement modelElement = (ModelElement)removalList.get( i );
            GasMolecule gm = (GasMolecule)modelElement;
            this.removeModelElement( gm );
        }
    }


    public void setModelBounds( Rectangle2D modelBounds ) {
        this.modelBounds = modelBounds;

        Shape s = box.getBoundsInternal();
        this.modelBounds = s;
        Point2D p1 = box.getOpening()[0];
        Point2D p2 = box.getOpening()[1];
        Rectangle2D r = new Rectangle2D.Double( p1.getX(), 0, p2.getX() - p1.getX(), p1.getY() );
        Area a = new Area( s );
        a.add( new Area( r ) );
        this.modelBounds = a;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Event handling
    //
    public void gravityChanged( Gravity.ChangeEvent event ) {
        adjustEnergyForGravity( event.getChange() );
    }

    public void enableParticleParticleInteractions( boolean enableInteraction ) {
        for( int i = 0; i < collisionExperts.size(); i++ ) {
            CollisionExpert collisionExpert = (CollisionExpert)collisionExperts.get( i );
            if( collisionExpert instanceof SphereSphereExpert ) {
                SphereSphereExpert.setIgnoreGasMoleculeInteractions( !enableInteraction );
            }
        }
    }

    //----------------------------------------------------------------
    // Event and Listener definitions
    //----------------------------------------------------------------
    
    private EventChannel heatSourceChangeChannel = new EventChannel( HeatSourceChangeListener.class );
    private HeatSourceChangeListener heatSourceChangeListenerProxy = (HeatSourceChangeListener)heatSourceChangeChannel.getListenerProxy();

    public void addHeatSourceChangeListener( HeatSourceChangeListener listener ) {
        heatSourceChangeChannel.addListener( listener );
    }

    public void removeHeatSourceChangeListener( HeatSourceChangeListener listener ) {
        heatSourceChangeChannel.removeListener( listener );
    }

    public class HeatSourceChangeEvent extends EventObject {
        public HeatSourceChangeEvent( Object source ) {
            super( source );
        }

        public double getHeatSource() {
            return heatSource;
        }
    }

    public interface HeatSourceChangeListener extends EventListener {
        void heatSourceChanged( HeatSourceChangeEvent event );
    }
}

