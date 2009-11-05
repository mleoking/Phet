
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.view.*;


public class SandwichShopCanvas extends RPALCanvas {
    
    private final SandwichFormulaNode formulaNode;
    private final SandwichShopBeforeNode beforeNode;
    private final RPALArrowNode arrowNode;
    private final SandwichShopAfterNode afterNode;

    public SandwichShopCanvas( SandwichShopModel model ) {
        super();
        
        formulaNode = new SandwichFormulaNode( model.getReaction(), model.getCoefficientRange() );
        addChild( formulaNode );
        
        beforeNode = new SandwichShopBeforeNode( RPALStrings.LABEL_BEFORE_SANDWICH, model.getReaction(), model.getQuantityRange() );
        addChild( beforeNode );
        
        arrowNode = new RPALArrowNode();
        addChild( arrowNode );
        
        afterNode = new SandwichShopAfterNode( RPALStrings.LABEL_AFTER_SANDWICH, model.getReaction(), model.getQuantityRange() );
        addChild( afterNode );
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

        // formula
        double x = 0;
        double y = 0;
        formulaNode.setOffset( x, y );
        
        // Before
        x = formulaNode.getFullBoundsReference().getMinX();
        y = formulaNode.getFullBoundsReference().getMaxY() - PNodeLayoutUtils.getOriginYOffset( beforeNode ) + 30;
        beforeNode.setOffset( x, y );
        
        // arrow
        x = beforeNode.getFullBoundsReference().getMaxX() + 20;
        y = beforeNode.getYOffset() + 150;
        arrowNode.setOffset( x, y );
        
        // After
        x = arrowNode.getFullBoundsReference().getMaxX() + 20;
        y = beforeNode.getYOffset();
        afterNode.setOffset( x, y );
        
        centerRootNode();
    }
}
