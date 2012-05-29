// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * A movable model element that contains thermal energy and that, at least in
 * the model, has an overall shape that can be represented as a rectangle.
 *
 * @author John Blanco
 */
public abstract class RectangularThermalMovableModelElement extends UserMovableModelElement implements ThermalEnergyContainer {

    protected final ObservableList<EnergyChunk> energyChunkList = new ObservableList<EnergyChunk>();
    public final BooleanProperty energyChunksVisible;
    protected double energy = 0; // In Joules.
    protected final double specificHeat; // In J/kg-K
    protected final double mass; // In kg

    /**
     * Constructor.
     */
    public RectangularThermalMovableModelElement( ConstantDtClock clock, ImmutableVector2D initialPosition, double mass, double specificHeat, BooleanProperty energyChunksVisible ) {
        super( initialPosition );
        this.mass = mass;
        this.specificHeat = specificHeat;
        this.energyChunksVisible = energyChunksVisible;

        energy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;

        // Hook up to the clock for time dependent behavior.
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockTicked( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );

        // Update positions of contained energy chunks when this element moves.
        position.addObserver( new ChangeObserver<ImmutableVector2D>() {
            public void update( ImmutableVector2D newPosition, ImmutableVector2D oldPosition ) {
                ImmutableVector2D movement = newPosition.getSubtractedInstance( oldPosition );
                for ( EnergyChunk energyChunk : energyChunkList ) {
                    energyChunk.position.set( energyChunk.position.get().getAddedInstance( movement ) );
                }
            }
        } );

        // Add the initial energy chunks.
        addInitialEnergyChunks();
    }

    /**
     * Get the rectangle that defines this elements position and shape in
     * model space.
     */
    public abstract Rectangle2D getRect();

    public void changeEnergy( double deltaEnergy ) {
        energy += deltaEnergy;
    }

    public double getEnergy() {
        return energy;
    }

    public double getTemperature() {
        return energy / ( mass * specificHeat );
    }

    @Override public void reset() {
        super.reset();
        energy = mass * specificHeat * EFACConstants.ROOM_TEMPERATURE;
        addInitialEnergyChunks();
    }

    protected void stepInTime( double dt ) {
        // TODO: Update the positions of the energy chunks.
    }

    public ObservableList<EnergyChunk> getEnergyChunkList() {
        return energyChunkList;
    }

    public boolean needsEnergyChunk() {
        return calculateNeededNumEnergyChunks() > energyChunkList.size();
    }

    public boolean hasExcessEnergyChunks() {
        return calculateNeededNumEnergyChunks() < energyChunkList.size();
    }

    public void addEnergyChunk( EnergyChunk ec ) {
        energyChunkList.add( ec );
    }

    public EnergyChunk removeEnergyChunk() {
        return energyChunkList.remove( energyChunkList.size() - 1 );
    }

    protected int calculateNeededNumEnergyChunks() {
        return (int) Math.round( Math.max( energy - EFACConstants.MIN_ENERGY, 0 ) * EFACConstants.ENERGY_CHUNK_MULTIPLIER );
    }

    protected void addInitialEnergyChunks() {
        int targetNumChunks = calculateNeededNumEnergyChunks();
        Rectangle2D energyChunkBounds = getThermalContactArea().getBounds();
        while ( targetNumChunks != getEnergyChunkList().size() ) {
            // Add a chunk at a random location in the block.
            addEnergyChunk( new EnergyChunk( EnergyChunkDistributor.generateRandomLocation( energyChunkBounds ), energyChunksVisible ) );
            System.out.println( "Added a chunk" );
        }
        EnergyChunkDistributor.distribute( getRect(), getEnergyChunkList() );
    }
}
