// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

/**
 * Class that represents a faucet that can be turned on to provide mechanical
 * energy to other energy system elements.
 *
 * @author John Blanco
 */
public class FaucetAndWater extends EnergySource {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    public static final Vector2D OFFSET_FROM_CENTER_TO_WATER_ORIGIN = new Vector2D( 0.065, 0.08 );
    private static final double MAX_ENERGY_PRODUCTION_RATE = 200; // In joules/second.
    private static final double WATER_FALLING_VELOCITY = 0.07; // In meters/second.
    private static final double FLOW_PER_CHUNK = 0.4;  // Empirically determined to get desired energy chunk emission rate.
    private static final double MAX_WATER_WIDTH = 0.015; // In meters.
    private static final double MIN_WATER_WIDTH = 1E-20; // In meters, basically means that the water is off.
    private static final double MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER = 0.5; // In meters.

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    public final Property<Double> flowProportion = new Property<Double>( 0.0 );
    public final BooleanProperty enabled = new BooleanProperty( true );

    // Shape of the water coming from the faucet.  The shape is specified
    // relative to the water origin.
    public final Property<Shape> waterShape = new Property<Shape>( null );

    // Points, or actually distance-width pairs, that define the shape of the
    // water.
    private final List<DistanceWidthPair> waterShapeDefiningPoints = new ArrayList<DistanceWidthPair>();

    private double flowSinceLastChunk = 0;
    private final BooleanProperty energyChunksVisible;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected FaucetAndWater( BooleanProperty energyChunksVisible ) {
        super( EnergyFormsAndChangesResources.Images.FAUCET_ICON );
        this.energyChunksVisible = energyChunksVisible;

        // Set the initial water shape.  By design, there must always be at
        // least two shape-defining points - one at the top and one at the
        // bottom.
        waterShapeDefiningPoints.add( new DistanceWidthPair( MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER, MIN_WATER_WIDTH ) );
        waterShapeDefiningPoints.add( new DistanceWidthPair( 0, MIN_WATER_WIDTH ) );
        waterShape.set( createWaterShape( waterShapeDefiningPoints ) );

        flowProportion.addObserver( new SimpleObserver() {
            public void update() {
//                updateWaterShape();
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt ) {

        if ( isActive() ) {

            // Update the shape of the water.  This is done here - in the time
            // step method - so that the water appears to fall at the start
            // and end of the flow.
            for ( DistanceWidthPair waterShapeDefiningPoint : waterShapeDefiningPoints ) {
                waterShapeDefiningPoint.setDistance( Math.min( waterShapeDefiningPoint.getDistance() + WATER_FALLING_VELOCITY * dt,
                                                               MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) );
            }

            double waterWidth = Math.max( flowProportion.get() * MAX_WATER_WIDTH, MIN_WATER_WIDTH );
            // Update the points that define the top of the water shape.
            if ( waterShapeDefiningPoints.get( 0 ).width == waterWidth &&
                 waterShapeDefiningPoints.get( waterShapeDefiningPoints.size() - 1 ).width == waterWidth ) {
                // Flow hasn't changed, so just move the top.
                waterShapeDefiningPoints.get( waterShapeDefiningPoints.size() - 1 ).setDistance( 0 );
            }
            else {
                // Add another point for the new flow rate.
                waterShapeDefiningPoints.add( new DistanceWidthPair( 0, waterWidth ) );
            }

            // Update the points that define the bottom of the water shape.
            List<DistanceWidthPair> copyOfShapeDefiningPoints = new ArrayList<DistanceWidthPair>( waterShapeDefiningPoints );
            for ( int i = 0; i < copyOfShapeDefiningPoints.size() - 1; i++ ) {
                if ( copyOfShapeDefiningPoints.get( i ).distance >= MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER &&
                     copyOfShapeDefiningPoints.get( i + 1 ).distance >= MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) {
                    // This point is no longer needed.
                    waterShapeDefiningPoints.remove( copyOfShapeDefiningPoints.get( i ) );
                }
            }

            System.out.println( "--------------------------" );
            System.out.println( "waterShapeDefiningPoints.size() = " + waterShapeDefiningPoints.size() );

            // TODO: Optimize to update only when changes occur.
            waterShape.set( createWaterShape( waterShapeDefiningPoints ) );


            // Check if time to emit an energy chunk and, if so, do it.
            flowSinceLastChunk += flowProportion.get() * dt;
            if ( flowSinceLastChunk > FLOW_PER_CHUNK ) {
                energyChunkList.add( new EnergyChunk( EnergyType.MECHANICAL,
                                                      getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ),
                                                      new Vector2D( 0, -WATER_FALLING_VELOCITY ),
                                                      energyChunksVisible ) );
                flowSinceLastChunk = 0;
            }

            // Update energy chunk positions.
            for ( EnergyChunk energyChunk : new ArrayList<EnergyChunk>( energyChunkList ) ) {

                // Make the chunk fall.
                energyChunk.translateBasedOnVelocity( dt );

                // Remove it if it is out of visible range.
                if ( getPosition().plus( OFFSET_FROM_CENTER_TO_WATER_ORIGIN ).distance( energyChunk.position.get() ) > MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER ) {
                    energyChunkList.remove( energyChunk );
                }
            }
        }

        // Generate the appropriate amount of energy.
        return new Energy( EnergyType.MECHANICAL, MAX_ENERGY_PRODUCTION_RATE * flowProportion.get() * dt, -Math.PI / 2 );
    }

    @Override public void deactivate() {
        super.deactivate();
        enabled.set( false );
    }

    @Override public void activate() {
        super.activate();
        enabled.set( true );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectFaucetButton;
    }

    private static Shape createWaterShape( List<DistanceWidthPair> distanceWidthPairs ) {

        if ( distanceWidthPairs.size() < 2 ) {
            // Not enough pairs to create a shape, so return a shape this is
            // basically invisible.
            return new Rectangle2D.Double( 0, 0, 1E-7, 1E-7 );
        }

        List<DistanceWidthPair> copyOfDistanceWidthPairs = new ArrayList<DistanceWidthPair>( distanceWidthPairs );
        DoubleGeneralPath path = new DoubleGeneralPath( -copyOfDistanceWidthPairs.get( 0 ).getWidth() / 2, -copyOfDistanceWidthPairs.get( 0 ).getDistance() );
        for ( int i = 1; i < copyOfDistanceWidthPairs.size(); i++ ) {
            path.lineTo( -copyOfDistanceWidthPairs.get( i ).getWidth() / 2, -copyOfDistanceWidthPairs.get( i ).getDistance() );
        }
        Collections.reverse( copyOfDistanceWidthPairs );
        for ( int i = 0; i < copyOfDistanceWidthPairs.size(); i++ ) {
            path.lineTo( copyOfDistanceWidthPairs.get( i ).getWidth() / 2, -copyOfDistanceWidthPairs.get( i ).getDistance() );
        }
        return path.getGeneralPath();
    }

    private void updateWaterShape() {
        if ( flowProportion.get() == 0 ) {
            waterShape.set( new Rectangle2D.Double( 0, 0, 1E-7, 1E-7 ) );
        }
        else {
            waterShapeDefiningPoints.clear();
            double waterWidth = flowProportion.get() * MAX_WATER_WIDTH;
            waterShapeDefiningPoints.add( new DistanceWidthPair( MAX_DISTANCE_FROM_FAUCET_TO_BOTTOM_OF_WATER, waterWidth ) );
            waterShapeDefiningPoints.add( new DistanceWidthPair( 0, waterWidth ) );
            waterShape.set( createWaterShape( waterShapeDefiningPoints ) );
        }
    }

    private static class DistanceWidthPair {
        private double distance;
        private final double width;

        private DistanceWidthPair( double distance, double width ) {
            this.distance = distance;
            this.width = width;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance( double distance ) {
            this.distance = distance;
        }

        public double getWidth() {
            return width;
        }
    }
}
