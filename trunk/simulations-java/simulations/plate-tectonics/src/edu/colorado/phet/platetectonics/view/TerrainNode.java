// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.nio.ByteBuffer;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;

import static edu.colorado.phet.platetectonics.PlateTectonicsConstants.*;

/**
 * Displays the top terrain of a plate model
 */
public class TerrainNode extends Geometry {
    private GridMesh gridMesh;
    private final PlateModel model;
    private final PlateTectonicsModule module;
    private TerrainNode.TerrainTextureImage image;

    public TerrainNode( PlateModel model, final PlateTectonicsModule module ) {
        this.model = model;
        this.module = module;
        Vector3f[] positions = computePositions( model );

        // use the gridded mesh to handle the terrain
        gridMesh = new GridMesh( Z_SAMPLES, X_SAMPLES, positions );
        setMesh( gridMesh );

        // use a terrain-style texture. this allows us to blend between three different textures
        setMaterial( new Material( module.getAssetManager(), "Common/MatDefs/Terrain/Terrain.j3md" ) {{

            /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
            setTexture( "Alpha", new Texture2D() {{
                int width = X_SAMPLES;
                int height = Z_SAMPLES;
                image = new TerrainTextureImage( width, height );
                setImage( image );
            }} );

            /** 1.2) Add GRASS texture into the red layer (Tex1). */
            Texture grass = module.getAssetManager().loadTexture( "Textures/Terrain/splat/grass.jpg" );
            grass.setWrap( WrapMode.Repeat );
            setTexture( "Tex1", grass );
            setFloat( "Tex1Scale", 64f );

            /** 1.3) Add DIRT texture into the green layer (Tex2) */
            Texture dirt = module.getAssetManager().loadTexture( "Textures/Terrain/splat/dirt.jpg" );
            dirt.setWrap( WrapMode.Repeat );
            setTexture( "Tex2", dirt );
            setFloat( "Tex2Scale", 32f );

            /** 1.4) Add ROAD texture into the blue layer (Tex3) */
            Texture rock = module.getAssetManager().loadTexture( "Textures/Terrain/splat/road.jpg" );
            rock.setWrap( WrapMode.Repeat );
            setTexture( "Tex3", rock );
            setFloat( "Tex3Scale", 128f );
        }} );

        model.modelChanged.addUpdateListener( new UpdateListener() {
            public void update() {
                gridMesh.updateGeometry( computePositions( TerrainNode.this.model ) );
                image.updateTerrain();
            }
        }, false );
    }

    private double getElevationAtPixel( float xIndex, float zIndex ) {
        return model.getElevation( getModelX( xIndex ), getModelZ( zIndex ) );
    }

    private Vector3f[] computePositions( PlateModel model ) {
        Vector3f[] positions = new Vector3f[X_SAMPLES * Z_SAMPLES];
        for ( int zIndex = 0; zIndex < Z_SAMPLES; zIndex++ ) {
            for ( int xIndex = 0; xIndex < X_SAMPLES; xIndex++ ) {
                float x = getModelX( xIndex );
                float z = getModelZ( zIndex );
                float elevation = (float) model.getElevation( x, z );
                Vector3f modelVector = new Vector3f( x, elevation, z );
                Vector3f viewVector = module.getModelViewTransform().modelToView( modelVector );
                positions[zIndex * X_SAMPLES + xIndex] = viewVector;
            }
        }
        return positions;
    }

    private float getModelX( float xIndex ) {
        // TODO: refactor this to combine constraints with CrossSectionNode!
        // center our x samples, and apply the resolution
        return module.getModelViewTransform().viewToModelDeltaX( ( xIndex - ( (float) X_SAMPLES - 1 ) / 2 ) / RESOLUTION );
    }

    private float getModelZ( float zIndex ) {
        // z samples go into negative z, and apply the resolution
        return module.getModelViewTransform().viewToModelDeltaZ( ( zIndex - ( (float) Z_SAMPLES - 1 ) ) / RESOLUTION );
    }

    /*---------------------------------------------------------------------------*
    * update the texture solely based on elevation
    *----------------------------------------------------------------------------*/
    private class TerrainTextureImage extends com.jme3.texture.Image {
        public TerrainTextureImage( int width, int height ) {
            // TODO: simplify, and make it so that we actually use the entire texture space?
            // TODO: (performance) fix to use less texture space, and ideally less computation related to that
            super( Format.RGBA8, Math.max( width, height ), Math.max( width, height ), ByteBuffer.allocateDirect( 4 * Math.max( width, height ) * Math.max( width, height ) ) );
            updateTerrain();
        }

        public void updateTerrain() {
            ByteBuffer buffer = data.get( 0 );
            buffer.clear();
            for ( int z = 0; z < height; z++ ) {
                for ( int x = 0; x < width; x++ ) {
                    double elevation = getElevationAtPixel( x, z );
                    int stonyness = MathUtil.clamp( 0, (int) ( ( elevation - 15000 ) / 20 ) + 255, 255 ); // fully stony at 10km
                    int beachyness = MathUtil.clamp( 0, (int) ( -( elevation - 1500 ) / 3 ), 255 );
                    buffer.put( (byte) ( 255 - stonyness - beachyness ) ); // grass
                    buffer.put( (byte) stonyness ); // stone
                    buffer.put( (byte) beachyness ); // beach (cobbles right now)
                    buffer.put( (byte) 1 );
                }
            }
            setUpdateNeeded();
        }
    }
}
