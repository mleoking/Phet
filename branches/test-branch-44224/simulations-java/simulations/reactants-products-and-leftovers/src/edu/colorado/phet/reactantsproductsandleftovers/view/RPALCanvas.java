/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Base class for all Piccolo canvases in this project.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RPALCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // View 
    private PNode rootNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RPALCanvas() {
        super( RPALConstants.CANVAS_RENDERING_SIZE );

        setBackground( RPALConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );
    }

    //----------------------------------------------------------------------------
    // 
    //----------------------------------------------------------------------------

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        if ( node != null && rootNode.indexOfChild( node ) != -1 ) {
            rootNode.removeChild( node );
        }
    }

    protected void centerRootNode() {
        centerNode( rootNode );
    }
    
    protected void centerNode( PNode node ) {
        if ( node != null ) {
            Dimension2D worldSize = getWorldSize();
            PBounds b = node.getFullBoundsReference();
            double xOffset = ( worldSize.getWidth() - b.getWidth() - PNodeLayoutUtils.getOriginXOffset( node ) ) / 2;
            double yOffset = ( worldSize.getHeight() - b.getHeight() - PNodeLayoutUtils.getOriginYOffset( node ) ) / 2;
            node.setOffset( xOffset, yOffset );
        }
    }
    
    /*
     * Centers the root node on the canvas when the canvas size changes.
     */
    @Override
    protected void updateLayout() {
        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() > 0 && worldSize.getHeight() > 0 ) {
            centerRootNode();
        }
    }

}
