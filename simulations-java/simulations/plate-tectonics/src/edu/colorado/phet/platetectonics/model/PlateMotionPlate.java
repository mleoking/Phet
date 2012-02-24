// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.platetectonics.model.behaviors.PlateBehavior;
import edu.colorado.phet.platetectonics.model.regions.Boundary;
import edu.colorado.phet.platetectonics.model.regions.Region;

import static edu.colorado.phet.platetectonics.model.PlateMotionModel.*;

public class PlateMotionPlate extends Plate {
    private final PlateMotionModel model;
    private final TextureStrategy textureStrategy;
    private final boolean isLeftPlate;

    private PlateType plateType = null;

    private PlateBehavior behavior;

    public PlateMotionPlate( final PlateMotionModel model, final TextureStrategy textureStrategy, final boolean isLeftPlate ) {
        this.model = model;
        this.textureStrategy = textureStrategy;
        this.isLeftPlate = isLeftPlate;
        addMantle( new Region( MANTLE_VERTICAL_STRIPS, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                return createMantleSample( xIndex, yIndex, MANTLE_VERTICAL_STRIPS,
                                           SIMPLE_MANTLE_TOP_Y, SIMPLE_MANTLE_BOTTOM_Y,
                                           SIMPLE_MANTLE_TOP_TEMP, SIMPLE_MANTLE_BOTTOM_TEMP );
            }
        } ) );
        addTerrain( textureStrategy, TERRAIN_DEPTH_SAMPLES, model.getBounds().getMinZ(), model.getBounds().getMaxZ() );
    }

    private Sample createMantleSample( int xIndex, int yIndex, int verticalSamples, float mantleTopY, float mantleBottomY, float mantleTopTemp, float mantleBottomTemp ) {
        // TODO: consider adding a higher concentration of samples near the top?
        float x = isLeftPlate ? model.getLeftX( xIndex ) : model.getRightX( xIndex );
        final float yRatio = ( (float) yIndex ) / ( (float) verticalSamples );
        float y = mantleTopY + ( mantleBottomY - mantleTopY ) * yRatio;
        float temp = mantleTopTemp + ( mantleBottomTemp - mantleTopTemp ) * yRatio;
        return new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                           textureStrategy.mapFront( new ImmutableVector2F( x, y ) ) );
    }

    public void droppedCrust( final PlateType type ) {
        plateType = type;
        final float crustTopY = getFreshCrustTop( type );
        final float crustBottomY = getFreshCrustBottom( type );
        final float lithosphereBottomY = getFreshLithosphereBottom( type );

        final float crustDensity = getFreshDensity( type );

        addLithosphere( new Region( LITHOSPHERE_VERTICAL_STRIPS, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                return createMantleSample( xIndex, yIndex, LITHOSPHERE_VERTICAL_STRIPS,
                                           crustBottomY, lithosphereBottomY,
                                           SIMPLE_MANTLE_TOP_TEMP, SIMPLE_LITHOSPHERE_BOUNDARY_TEMP );
            }
        } ) );
        addCrust( new Region( CRUST_VERTICAL_STRIPS, HORIZONTAL_SAMPLES, new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // start with top first
                float x = isLeftPlate ? model.getLeftX( xIndex ) : model.getRightX( xIndex );

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                float y = crustTopY + ( crustBottomY - crustTopY ) * yRatio;

                float temp = getCrustTemperatureFromYRatio( yRatio );
                return new Sample( new ImmutableVector3F( x, y, 0 ), temp, crustDensity,
                                   textureStrategy.mapFront( new ImmutableVector2F( x, y ) ) );
            }
        } ) );

        // set the position/texture coordinates to be the same for the mantle top boundary
//        getMantle().getTopBoundary().borrowPositionTemperatureAndTexture( getLithosphere().getBottomBoundary() );

        // essentially re-set all of the mantle samples based on our new information
        for ( int yIndex = 0; yIndex < getMantle().getBoundaries().size(); yIndex++ ) {
            Boundary boundary = getMantle().getBoundaries().get( yIndex );

            for ( int xIndex = 0; xIndex < boundary.samples.size(); xIndex++ ) {
                final Sample sample = boundary.samples.get( xIndex );
                final Sample desiredSample = createMantleSample( xIndex, yIndex, MANTLE_VERTICAL_STRIPS,
                                                                 lithosphereBottomY, SIMPLE_MANTLE_BOTTOM_Y,
                                                                 SIMPLE_LITHOSPHERE_BOUNDARY_TEMP, SIMPLE_MANTLE_BOTTOM_TEMP );

                sample.setPosition( desiredSample.getPosition() );
                sample.setTemperature( desiredSample.getTemperature() );
                sample.setDensity( desiredSample.getDensity() );
                sample.setTextureCoordinates( desiredSample.getTextureCoordinates() );
            }
        }
    }

    public float getSimpleChunkWidth() {
        // TODO: consider a direct model query with this, as if we change to uneven spacing this will break
        return model.getLeftX( 1 ) - model.getLeftX( 0 );
    }

    public void addLeftSection() {
        final float width = model.getLeftX( 1 ) - model.getLeftX( 0 );
        final float xOffset = -width;
        final float crustTopY = getFreshCrustTop( plateType );
        final float crustMantleBoundaryY = getFreshCrustBottom( plateType );
        final float lithosphereBottomY = getFreshLithosphereBottom( plateType );

        final float x = getCrust().getTopBoundary().getFirstSample().getPosition().x + xOffset;

        // TODO: clean up code duplication
        getCrust().addLeftRow( new ArrayList<Sample>() {{
            float topY = crustTopY;
            float bottomY = crustMantleBoundaryY;
            for ( int yIndex = 0; yIndex < getCrust().getBoundaries().size(); yIndex++ ) {
                final Sample mySample = getCrust().getBoundaries().get( yIndex ).getFirstSample();

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                float y = topY + ( bottomY - topY ) * yRatio;

                float temp = getCrustTemperatureFromYRatio( yRatio );
                final float x = mySample.getPosition().x + xOffset;
                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, getFreshDensity( plateType ),
                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );

        getLithosphere().addLeftRow( new ArrayList<Sample>() {{
            float topY = crustMantleBoundaryY;
            float bottomY = lithosphereBottomY;
            for ( int yIndex = 0; yIndex < getLithosphere().getBoundaries().size(); yIndex++ ) {
                final Sample mySample = getLithosphere().getBoundaries().get( yIndex ).getFirstSample();

                final float yRatio = ( (float) yIndex ) / ( (float) LITHOSPHERE_VERTICAL_STRIPS );
                float y = topY + ( bottomY - topY ) * yRatio;

                float temp = getLithosphereTemperatureFromYRatio( yRatio );
                final float x = mySample.getPosition().x + xOffset;
                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );

        getTerrain().addToLeft( x, new ArrayList<TerrainSample>() {{
            for ( int zIndex = 0; zIndex < getTerrain().getZSamples(); zIndex++ ) {
                final TerrainSample mySample = getTerrain().getSample( 0, zIndex );
                // elevation to be fixed later
                // TODO: fix texture coordinates on newly added terrain
                add( new TerrainSample( getCrust().getTopBoundary().getFirstSample().getPosition().y, mySample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );
    }

    public void addRightSection() {
        final float width = model.getRightX( 1 ) - model.getRightX( 0 );
        final float xOffset = width;
        final float crustTopY = getFreshCrustTop( plateType );
        final float crustMantleBoundaryY = getFreshCrustBottom( plateType );
        final float lithosphereBottomY = getFreshLithosphereBottom( plateType );

        final float x = getCrust().getTopBoundary().getLastSample().getPosition().x + xOffset;

        // TODO: clean up code duplication
        getCrust().addRightRow( new ArrayList<Sample>() {{
            float topY = crustTopY;
            float bottomY = crustMantleBoundaryY;
            for ( int yIndex = 0; yIndex < getCrust().getBoundaries().size(); yIndex++ ) {
                final Sample mySample = getCrust().getBoundaries().get( yIndex ).getLastSample();

                final float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                float y = topY + ( bottomY - topY ) * yRatio;

                float temp = getCrustTemperatureFromYRatio( yRatio );
                final float x = mySample.getPosition().x + xOffset;
                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, getFreshDensity( plateType ),
                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );

        getLithosphere().addRightRow( new ArrayList<Sample>() {{
            float topY = crustMantleBoundaryY;
            float bottomY = lithosphereBottomY;
            for ( int yIndex = 0; yIndex < getLithosphere().getBoundaries().size(); yIndex++ ) {
                final Sample mySample = getLithosphere().getBoundaries().get( yIndex ).getLastSample();

                final float yRatio = ( (float) yIndex ) / ( (float) LITHOSPHERE_VERTICAL_STRIPS );
                float y = topY + ( bottomY - topY ) * yRatio;

                float temp = getLithosphereTemperatureFromYRatio( yRatio );
                final float x = mySample.getPosition().x + xOffset;
                add( new Sample( new ImmutableVector3F( x, y, 0 ), temp, SIMPLE_MANTLE_DENSITY,
                                 mySample.getTextureCoordinates().plus( textureStrategy.mapFrontDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );

        getTerrain().addToRight( x, new ArrayList<TerrainSample>() {{
            for ( int zIndex = 0; zIndex < getTerrain().getZSamples(); zIndex++ ) {
                final TerrainSample mySample = getTerrain().getSample( getTerrain().getNumColumns() - 1, zIndex );
                // elevation to be fixed later
                // TODO: fix texture coordinates on newly added terrain
                add( new TerrainSample( getCrust().getTopBoundary().getLastSample().getPosition().y, mySample.getTextureCoordinates().plus( textureStrategy.mapTopDelta( new ImmutableVector2F( xOffset, 0 ) ) ) ) );
            }
        }} );
    }

    public void removeLeftSection() {
        getCrust().removeLeftRow();
        getLithosphere().removeLeftRow();
        getTerrain().removeLeft();
    }

    public void removeRightSection() {
        getCrust().removeRightRow();
        getLithosphere().removeRightRow();
        getTerrain().removeRight();
    }

    public void fullSyncTerrain() {
        for ( int column = 0; column < getTerrain().getNumColumns(); column++ ) {
            // left side
            ImmutableVector3F position = ( plateType != null ? getCrust().getTopBoundary() : getMantle().getTopBoundary() ).samples.get( column ).getPosition();
            for ( int row = 0; row < TERRAIN_DEPTH_SAMPLES; row++ ) {
                // set the elevation for the whole column
                getTerrain().getSample( column, row ).setElevation( position.y );
            }
            getTerrain().xPositions.set( column, position.x );
        }
        getTerrain().elevationChanged.updateListeners();
    }

    private static float getCrustTemperatureFromYRatio( float ratioFromTop ) {
        // TODO: young/old oceanic crust differences!
        return SIMPLE_CRUST_TOP_TEMP + ( SIMPLE_CRUST_BOTTOM_TEMP - SIMPLE_CRUST_TOP_TEMP ) * ratioFromTop;
    }

    private static float getLithosphereTemperatureFromYRatio( float ratioFromTop ) {
        return SIMPLE_MANTLE_TOP_TEMP + ( SIMPLE_LITHOSPHERE_BOUNDARY_TEMP - SIMPLE_MANTLE_TOP_TEMP ) * ratioFromTop;
    }

    // only for the transform boundary, so we can simplify a few things here
    public void addMiddleSide( Plate otherPlate ) {
        assert getCrust() != null && getMantle() != null;

        /*---------------------------------------------------------------------------*
        * mantle
        *----------------------------------------------------------------------------*/
        regions.add( new Region( MANTLE_VERTICAL_STRIPS, getTerrain().getNumRows(), new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // we're in the center exactly
                float x = 0;

                // grab the y location from the mantle cross section boundary
                final Sample sample = getMantle().getBoundaries().get( yIndex ).samples.get( 0 );
                float y = sample.getPosition().y;

                // grab the Z from the terrain. we also reverse the index for the side of the left plate, so the sidedness is correct
                float z = getTerrain().zPositions.get( isLeftPlate ? getTerrain().getNumRows() - xIndex - 1 : xIndex );

                // sample copied from other one. we reverse texture coordinates also for the left plate, so it wraps over the edge nicely
                return new Sample( new ImmutableVector3F( x, y, z ), sample.getTemperature(), sample.getDensity(),
                                   textureStrategy.mapFront( new ImmutableVector2F( isLeftPlate ? -z : z, y ) ) );
            }
        } ) );

        /*---------------------------------------------------------------------------*
        * lithosphere
        *----------------------------------------------------------------------------*/
        regions.add( new Region( LITHOSPHERE_VERTICAL_STRIPS, getTerrain().getNumRows(), new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // we're in the center exactly
                float x = 0;

                // grab the y location from the lithosphere cross section boundary
                final Sample sample = getLithosphere().getBoundaries().get( yIndex ).samples.get( 0 );
                float y = sample.getPosition().y;

                // grab the Z from the terrain. we also reverse the index for the side of the left plate, so the sidedness is correct
                float z = getTerrain().zPositions.get( isLeftPlate ? getTerrain().getNumRows() - xIndex - 1 : xIndex );

                // sample copied from other one. we reverse texture coordinates also for the left plate, so it wraps over the edge nicely
                return new Sample( new ImmutableVector3F( x, y, z ), sample.getTemperature(), sample.getDensity(),
                                   textureStrategy.mapFront( new ImmutableVector2F( isLeftPlate ? -z : z, y ) ) );
            }
        } ) );

        /*---------------------------------------------------------------------------*
        * crust
        *----------------------------------------------------------------------------*/

        // do a few calculations so we don't cover up the exposed-to-air part of the earth
        final float myTopY = getCrust().getTopBoundary().samples.get( 0 ).getPosition().y;
        final float myBottomY = getCrust().getBottomBoundary().samples.get( 0 ).getPosition().y;
        final float otherTopY = otherPlate.getCrust().getTopBoundary().samples.get( 0 ).getPosition().y;
        float topEarthY = Math.min( myTopY, otherTopY );
        final float ratioSpread = ( topEarthY - myBottomY ) / ( myTopY - myBottomY );
        System.out.println( "ratioSpread = " + ratioSpread );
        regions.add( new Region( CRUST_VERTICAL_STRIPS, getTerrain().getNumRows(), new Function2<Integer, Integer, Sample>() {
            public Sample apply( Integer yIndex, Integer xIndex ) {
                // we're in the center exactly
                float x = 0;

                // grab the y location from the mantle cross section boundary
                final Sample sample = getCrust().getBoundaries().get( yIndex ).samples.get( 0 );

                float yRatio = ( (float) yIndex ) / ( (float) CRUST_VERTICAL_STRIPS );
                System.out.println( "yRatio before = " + yRatio );
                yRatio = ( yRatio * ratioSpread + ( 1 - ratioSpread ) ); // compensate for the difference
                System.out.println( "yRatio after  = " + yRatio );
                float y = myTopY + ( myBottomY - myTopY ) * yRatio;

                // grab the Z from the terrain. we also reverse the index for the side of the left plate, so the sidedness is correct
                float z = getTerrain().zPositions.get( isLeftPlate ? getTerrain().getNumRows() - xIndex - 1 : xIndex );

                float temp = getCrustTemperatureFromYRatio( yRatio );
                // sample copied from other one. we reverse texture coordinates also for the left plate, so it wraps over the edge nicely
                return new Sample( new ImmutableVector3F( x, y, z ), temp, sample.getDensity(),
                                   textureStrategy.mapFront( new ImmutableVector2F( isLeftPlate ? -z : z, y ) ) );
            }
        } ) );
    }

    public PlateBehavior getBehavior() {
        return behavior;
    }

    public void setBehavior( PlateBehavior behavior ) {
        this.behavior = behavior;
    }

    public boolean isLeftPlate() {
        return isLeftPlate;
    }

    public TextureStrategy getTextureStrategy() {
        return textureStrategy;
    }
}
