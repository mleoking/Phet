// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.common.view;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class AbstractFractionsCanvas extends PhetPCanvas {

    //Stage where nodes are added and scaled up and down
    private final PNode rootNode;

    //Size for the stage, should have the right aspect ratio since it will always be visible
    //The dimension was determined by running on Windows and inspecting the dimension of the canvas after menubar and tabs are added
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );
    public static final double INSET = 10;

    public AbstractFractionsCanvas() {

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}