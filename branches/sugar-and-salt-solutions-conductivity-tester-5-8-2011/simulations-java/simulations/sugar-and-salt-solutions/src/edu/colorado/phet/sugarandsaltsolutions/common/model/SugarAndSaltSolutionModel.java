// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.property5.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser.SALT;

/**
 * @author Sam Reid
 */
public class SugarAndSaltSolutionModel {
    public final double width = 1.04;//visible width in meters
    public final double height = 0.7;//visible height in meters

    //Center the beaker's base at x=0 and have it go halfway up the screen
    public final double beakerWidth = width * 0.6;
    public final double beakerX = -beakerWidth / 2;
    public final double beakerHeight = height * 0.5;

    public final Beaker beaker = new Beaker( beakerX, 0, beakerWidth, beakerHeight );//The beaker into which you can add water, salt and sugar.
    public final Water water = new Water( beaker );
    public final Property<Double> inputFlowRate = new Property<Double>( 0.0 );//rate that water flows into the beaker in m^3/s
    public final Property<Double> outputFlowRate = new Property<Double>( 0.0 );//rate that water flows out of the beaker in m^3/s
    public final ConstantDtClock clock;

    //Sugar and its listeners
    public final ArrayList<Sugar> sugarList = new ArrayList<Sugar>();//The sugar crystals that haven't been dissolved
    public final Notifier<Sugar> sugarAdded = new Notifier<Sugar>();//Listeners for when sugar crystals are added

    //Salt and its listeners
    public final ArrayList<Salt> saltList = new ArrayList<Salt>();//The salt crystals that haven't been dissolved
    public final Notifier<Salt> saltAdded = new Notifier<Salt>();//Listeners for when salt crystals are added

    private ImmutableVector2D gravity = new ImmutableVector2D( 0, -9.8 );//Force due to gravity near the surface of the earth

    private static final double FLOW_SCALE = 0.02;//Flow controls vary between 0 and 1, this scales it down to a good model value
    public final Property<Dispenser> dispenser = new Property<Dispenser>( SALT );//Which dispenser the user has selected

    //Listeners which are notified when the sim is reset.
    private ArrayList<VoidFunction0> resetListeners = new ArrayList<VoidFunction0>();

    //Model for the conductivity tester, which can be dipped into the liquid to indicate the conductivity by lighting a light bulb
    public final ConductivityTester conductivityTester = new ConductivityTester();

    public SugarAndSaltSolutionModel() {
        clock = new ConstantDtClock( 30 );

        //Wire up to the clock so we can update when it ticks
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateModel( clockEvent.getSimulationTimeChange() );
            }
        } );
    }

    //Adds the specified Sugar crystal to the model
    public void addSugar( final Sugar sugar ) {
        sugarList.add( sugar );
        sugarAdded.updateListeners( sugar );
    }

    //Adds the specified salt crystal to the model
    public void addSalt( Salt salt ) {
        this.saltList.add( salt );
        saltAdded.updateListeners( salt );
    }

    //Update the model when the clock ticks
    private void updateModel( double dt ) {
        //Change the water volume based on input and output flow
        double inVolume = dt * inputFlowRate.getValue() * FLOW_SCALE;
        double outVolume = dt * outputFlowRate.getValue() * FLOW_SCALE;

        //Compute the new water volume, but making sure it doesn't overflow or underflow
        double newVolume = water.volume.getValue() + inVolume - outVolume;
        if ( newVolume > beaker.getMaxFluidVolume() ) {
            inVolume = beaker.getMaxFluidVolume() + outVolume - water.volume.getValue();
        }
        else if ( newVolume < 0 ) {
            outVolume = inVolume + water.volume.getValue();
        }

        //Set the true value of the new volume based on clamped inputs and outputs
        newVolume = water.volume.getValue() + inVolume - outVolume;

        //Turn off the input flow if the beaker would overflow
        if ( newVolume >= beaker.getMaxFluidVolume() ) {
            inputFlowRate.setValue( 0.0 );
            //TODO: make the cursor drop the slider?
        }

        //Update the water volume
        water.volume.setValue( newVolume );
        waterExited( outVolume );

        //Move about the sugar and salt crystals
        updateCrystals( dt, saltList );
        updateCrystals( dt, sugarList );
    }

    //Called when water (with dissolved solutes) flows out of the beaker, so that subclasses can update concentrations if necessary.
    protected void waterExited( double outVolume ) {
    }

    //Propagate the sugar and salt crystals, and absorb them if they hit the water
    private void updateCrystals( double dt, final ArrayList<? extends Crystal> crystalList ) {
        ArrayList<Crystal> hitTheWater = new ArrayList<Crystal>();
        for ( Crystal crystal : crystalList ) {
            //slow the motion down a little bit or it moves too fast
            crystal.stepInTime( gravity.times( crystal.mass ), dt / 10 );

            //If the salt hits the water, absorb it
            if ( water.getShape().getBounds2D().contains( crystal.position.getValue().toPoint2D() ) ) {
                hitTheWater.add( crystal );
            }
        }
        //Remove the salt crystals that hit the water
        removeCrystals( crystalList, hitTheWater );

        //increase concentration in the water for crystals that hit
        for ( Crystal crystal : hitTheWater ) {
            crystalAbsorbed( crystal );
        }
    }

    //Remove the specified crystals.  Note that the toRemove
    private void removeCrystals( ArrayList<? extends Crystal> crystalList, ArrayList<? extends Crystal> toRemove ) {
        for ( Crystal crystal : new ArrayList<Crystal>( toRemove ) ) {
            crystal.remove();
            crystalList.remove( crystal );
        }
    }

    //Called when a crystal is absorbed by the water.
    // For instance, in the first tab it computes the resulting concentration change
    protected void crystalAbsorbed( Crystal crystal ) {
        removeSaltAndSugar();
    }

    //Called when the user presses a button to clear the solutes, removes all solutes from the sim
    public void removeSaltAndSugar() {
        removeCrystals( sugarList, sugarList );
        removeCrystals( saltList, saltList );
    }

    public void reset() {
        //Reset the model state
        removeSaltAndSugar();
        water.reset();
        inputFlowRate.reset();
        outputFlowRate.reset();

        //Notify listeners that registered for a reset message
        for ( VoidFunction0 resetListener : resetListeners ) {
            resetListener.apply();
        }
    }

    //Adds a listener that will be notified when the model is reset
    public void addResetListener( VoidFunction0 listener ) {
        resetListeners.add( listener );
    }
}