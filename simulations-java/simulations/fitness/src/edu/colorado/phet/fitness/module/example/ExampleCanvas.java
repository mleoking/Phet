/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fitness.module.example;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.fitness.FitnessConstants;
import edu.colorado.phet.fitness.defaults.ExampleDefaults;
import edu.colorado.phet.fitness.view.ExampleNode;
import edu.umd.cs.piccolo.PNode;

/**
 * ExampleCanvas is the canvas for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleCanvas extends PhetPCanvas {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private ExampleModel _model;

    // View 
    private PNode _rootNode;
    private ExampleNode _exampleNode;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleCanvas( ExampleModel model ) {
        super( ExampleDefaults.VIEW_SIZE );

        _model = model;

        setBackground( FitnessConstants.CANVAS_BACKGROUND );

        // Root of our scene graph
        _rootNode = new PNode();
        addWorldChild( _rootNode );

        _exampleNode = new ExampleNode();
        _rootNode.addChild( _exampleNode );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public ExampleNode getExampleNode() {
        return _exampleNode;
    }

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
        else if ( FitnessConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "PhysicsCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
