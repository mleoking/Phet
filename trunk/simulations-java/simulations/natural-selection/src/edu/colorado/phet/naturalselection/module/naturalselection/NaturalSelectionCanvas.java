package edu.colorado.phet.naturalselection.module.naturalselection;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.view.SpritesNode;
import edu.colorado.phet.naturalselection.view.NaturalSelectionBackgroundNode;
import edu.umd.cs.piccolo.PNode;

public class NaturalSelectionCanvas extends PhetPCanvas {

    public static final double HORIZON = 120.0;

    private NaturalSelectionModel model;

    private PNode rootNode;
    public SpritesNode bunnies;
    public NaturalSelectionBackgroundNode backgroundNode;

    public NaturalSelectionCanvas( NaturalSelectionModel _model ) {

        super( NaturalSelectionDefaults.VIEW_SIZE );

        model = _model;

        setBackground( NaturalSelectionConstants.COLOR_CONTROL_PANEL );
        setBorder( null );

        rootNode = new PNode();
        addWorldChild( rootNode );

        backgroundNode = new NaturalSelectionBackgroundNode( model.getClimate() );
        rootNode.addChild( backgroundNode );

        bunnies = new SpritesNode();
        rootNode.addChild( bunnies );

    }

    public void reset() {
        bunnies.reset();
        backgroundNode.reset();
    }

    protected void updateLayout() {

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }
        else if ( NaturalSelectionConstants.DEBUG_CANVAS_UPDATE_LAYOUT ) {
            System.out.println( "ExampleCanvas.updateLayout worldSize=" + worldSize );//XXX
        }

        //XXX lay out nodes
    }
}
