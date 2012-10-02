// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import edu.colorado.phet.lwjglphet.GLMaterial;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.math.Arrow2F;

import static org.lwjgl.opengl.GL11.*;

// TODO: extend into a general paint/fill container!!!!
public class ArrowNode extends GLNode {
    private final Arrow2F arrow;
    private ArrowBodyNode body;
    private ArrowOutlineNode outline;

    public ArrowNode( Arrow2F arrow ) {
        this.arrow = arrow;

        body = new ArrowBodyNode( arrow ) {
            @Override public void renderSelf( GLOptions options ) {
                // TODO: extend into a general paint/fill container!!!!
                glEnable( GL_POLYGON_OFFSET_FILL );
                glPolygonOffset( 1, 1 );
                super.renderSelf( options );
                glPolygonOffset( 0, 0 );
                glDisable( GL_POLYGON_OFFSET_FILL );
            }
        };
        addChild( body );
        outline = new ArrowOutlineNode( arrow );
        addChild( outline );
    }

    public void setFillMaterial( GLMaterial material ) {
        body.setMaterial( material );
    }

    public void setStrokeMaterial( GLMaterial material ) {
        outline.setMaterial( material );
    }

    @Override public void setMaterial( GLMaterial material ) {
        super.setMaterial( material );
        body.setMaterial( null );
        outline.setMaterial( null );
    }
}