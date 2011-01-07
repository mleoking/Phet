// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.titration.module.polyprotic;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.titration.TitrationConstants;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Polyprotic Acids" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PolyproticCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private PolyproticModel model;
    
    // View 
    private PNode _rootNode;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public PolyproticCanvas( PolyproticModel model ) {
        super( TitrationConstants.CANVAS_RENDERING_SIZE );
        
        this.model = model;
        
        setBackground( TitrationConstants.CANVAS_BACKGROUND );
        
        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Canvas layout
    //----------------------------------------------------------------------------
    
    /*
     * Updates the layout of stuff on the canvas.
     */
    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( TitrationConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "TitrateCanvas.updateLayout worldSize=" + worldSize );//XXX
        }
        
        //XXX lay out nodes
    }
}
