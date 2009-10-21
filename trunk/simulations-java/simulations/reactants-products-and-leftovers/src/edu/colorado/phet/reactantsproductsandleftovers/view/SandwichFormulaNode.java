package edu.colorado.phet.reactantsproductsandleftovers.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.BreadCoefficientSpinnerNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.CheeseCoefficientSpinnerNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.IntegerSpinnerNode;
import edu.colorado.phet.reactantsproductsandleftovers.controls.MeatCoefficientSpinnerNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichFormula;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;


public class SandwichFormulaNode extends PhetPNode {
    
    private static final int CONTROL_X_SPACING = 5;
    private static final int TERM_X_SPACING = 20;
    private static final int Y_SPACING = 30;
    private static final double SWING_SCALE = 2.0; //XXX make this go away
    
    private final SandwichFormula formula;
    private final IntegerSpinnerNode breadSpinnerNode, meatSpinnerNode, cheeseSpinnerNode;
    private final PText sandwichesCountNode;
    private final SandwichNode sandwichNode;

    public SandwichFormulaNode( final SandwichFormula formula ) {
        super();
        
        this.formula = formula;
        formula.addChangeListener( new ChangeListener(){
            public void stateChanged( ChangeEvent e ) {
                update();
            } 
        });
        
        PText titleNode = new PText( RPALStrings.LABEL_SANDWICH_FORMULA );
        titleNode.setFont( new PhetFont( 14 ) );
        titleNode.scale( SWING_SCALE );

        breadSpinnerNode = new BreadCoefficientSpinnerNode( formula );
        breadSpinnerNode.scale( SWING_SCALE );
        
        meatSpinnerNode = new MeatCoefficientSpinnerNode( formula );
        meatSpinnerNode.scale( SWING_SCALE );
        
        cheeseSpinnerNode = new CheeseCoefficientSpinnerNode( formula );
        cheeseSpinnerNode.scale( SWING_SCALE );
        
        BreadNode breadNode = new BreadNode();
        MeatNode meatNode = new MeatNode();
        CheeseNode cheeseNode = new CheeseNode();
        sandwichNode = new SandwichNode( formula );
        
        PlusNode plusNode1 = new PlusNode();
        PlusNode plusNode2 = new PlusNode();
        
        PNode arrowNode = new RPALArrowNode();
        
        sandwichesCountNode = new PText( "1" );
        sandwichesCountNode.setFont( new PhetFont() );
        sandwichesCountNode.scale( 2 ); //XXX
        addChild( sandwichesCountNode );
        
        addChild( titleNode );
        addChild( breadSpinnerNode );
        addChild( breadNode );
        addChild( plusNode1 );
        addChild( meatSpinnerNode );
        addChild( meatNode );
        addChild( plusNode2 );
        addChild( cheeseSpinnerNode );
        addChild( cheeseNode );
        addChild( arrowNode );
        addChild( sandwichesCountNode );
        addChild( sandwichNode );
        
        // title
        double x = 0;
        double y = 0;
        titleNode.setOffset( x, y );
        // bread spinner
        x = titleNode.getXOffset();
        y = titleNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        breadSpinnerNode.setOffset( x, y );
        // bread image
        x = breadSpinnerNode.getFullBoundsReference().getMaxX() + CONTROL_X_SPACING;
        y = breadSpinnerNode.getFullBoundsReference().getCenterY() - ( breadNode.getFullBoundsReference().getHeight() / 2 );
        breadNode.setOffset( x, y );
        // plus
        x = breadNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = breadNode.getFullBoundsReference().getCenterY() - ( plusNode1.getFullBoundsReference().getHeight() / 2 );
        plusNode1.setOffset( x, y );
        // meat spinner 
        x = plusNode1.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = breadSpinnerNode.getYOffset();
        meatSpinnerNode.setOffset( x, y );
        // meat image
        x = meatSpinnerNode.getFullBoundsReference().getMaxX() + CONTROL_X_SPACING;
        y = meatSpinnerNode.getFullBoundsReference().getCenterY() - ( meatNode.getFullBoundsReference().getHeight() / 2 );
        meatNode.setOffset( x, y );
        // plus
        x = meatNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = meatNode.getFullBoundsReference().getCenterY() - ( plusNode2.getFullBoundsReference().getHeight() / 2 );
        plusNode2.setOffset( x, y );
        // cheese spinner
        x = plusNode2.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = breadSpinnerNode.getYOffset();
        cheeseSpinnerNode.setOffset( x, y );
        // cheese image
        x = cheeseSpinnerNode.getFullBoundsReference().getMaxX() + CONTROL_X_SPACING;
        y = cheeseSpinnerNode.getFullBoundsReference().getCenterY() - ( cheeseNode.getFullBoundsReference().getHeight() / 2 );
        cheeseNode.setOffset( x, y );
        // arrow
        x = cheeseNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = cheeseNode.getFullBoundsReference().getCenterY() - ( arrowNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( arrowNode );
        arrowNode.setOffset( x, y );
        // product value
        x = arrowNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = arrowNode.getFullBoundsReference().getCenterY() - ( sandwichesCountNode.getFullBoundsReference().getHeight() / 2 );
        sandwichesCountNode.setOffset( x, y );
        // sandwich image
        x = sandwichesCountNode.getFullBoundsReference().getMaxX() + TERM_X_SPACING;
        y = sandwichesCountNode.getFullBoundsReference().getCenterY() - ( sandwichNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( sandwichNode );
        sandwichNode.setOffset( x, y );
        
        update();
    }
    
    private void update() {
        breadSpinnerNode.setValue( formula.getBread() );
        meatSpinnerNode.setValue( formula.getMeat() );
        cheeseSpinnerNode.setValue( formula.getCheese() );
        if ( formula.isReaction() ) {
            sandwichesCountNode.setText( String.valueOf( formula.getSandwiches() ) );
            sandwichNode.setVisible( true );
        }
        else {
            sandwichesCountNode.setText( RPALStrings.LABEL_NO_REACTION );
            sandwichNode.setVisible( false );
        }
    }
}
