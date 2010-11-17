/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit.BatteryCapacitorCircuitChangeAdapter;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Shows the total dielectric charge.
 * Spacing of positive and negative charges remains constant, and they appear in positive/negative pairs.
 * The spacing between the positive/negative pairs changes proportional to E_dielectric.
 * Outside the capacitor, the spacing between the pairs is at a minimum to reprsent no charge.
 * <p>
 * All model coordinates are relative to the dielectric's local coordinate frame,
 * where the origin is at the 3D geometric center of the dielectric.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricTotalChargeNode extends PhetPNode {
    
    private static final int SPACING_BETWEEN_PAIRS = 45; // view coordinates
    private static final DoubleRange SPACING_BETWEEN_CHARGES = new DoubleRange( 0, SPACING_BETWEEN_PAIRS / 2 ); // view coordinates
    private static final double SPACING_BETWEEN_CHARGES_EXPONENT = 1/7d;
    
    private final BatteryCapacitorCircuit circuit;
    private final ModelViewTransform mvt;
    private final PNode parentNode; // parent node for charges

    public DielectricTotalChargeNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        this.parentNode = new PComposite();
        addChild( parentNode );
        
        circuit.addBatteryCapacitorCircuitChangeListener( new BatteryCapacitorCircuitChangeAdapter() {
            @Override
            public void capacitanceChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
            @Override
            public void voltageChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
            @Override
            public void efieldChanged() {
                if ( isVisible() ) {
                    update();
                }
            }
        } );
        
        update();
    }
    
    /**
     * Update the node when it becomes visible.
     */
    @Override
    public void setVisible( boolean visible ) {
        if ( visible != isVisible() ) {
            super.setVisible( visible );
            if ( visible ) {
                update();
            }
        }
    }
    
    private void update() {
        
        // remove existing charges
        parentNode.removeAllChildren();
        
        // spacing between charges
        final double eField = circuit.getDielectricEField();
        final double spacingBetweenCharges = getSpacingBetweenCharges( eField );
        
        // spacing between pairs
        final double spacingBetweenPairs = mvt.viewToModelDelta( SPACING_BETWEEN_PAIRS, 0 ).getX();
        
        // rows and columns
        final double dielectricWidth = circuit.getCapacitor().getPlateSideLength();
        final double dielectricHeight = circuit.getCapacitor().getDielectricHeight();
        final double dielectricDepth = dielectricWidth;
        final int rows = (int) ( dielectricHeight / spacingBetweenPairs );
        final int columns = (int) ( dielectricWidth / spacingBetweenPairs );
        
        // margins and offsets
        final double xMargin = ( dielectricWidth - ( columns * spacingBetweenPairs ) ) / 2;
        final double yMargin = ( dielectricHeight - ( rows * spacingBetweenPairs ) ) / 2;
        final double zMargin = xMargin;
        final double offset = spacingBetweenPairs / 2;
        
        // polarity
        final Polarity polarity = ( eField >= 0 ) ? Polarity.NEGATIVE : Polarity.POSITIVE;
        
        // front face
        double xPlateEdge = -( dielectricWidth / 2 ) + ( dielectricWidth - circuit.getCapacitor().getDielectricOffset() );
        for ( int row = 0; row < rows; row++ ) {
            for ( int column = 0; column < columns; column++ ) {
                
                // front face
                {
                    // charge pair
                    ChargePairNode pairNode = new ChargePairNode();
                    parentNode.addChild( pairNode );

                    // location
                    double x = -( dielectricWidth / 2 ) + offset + xMargin + ( column * spacingBetweenPairs );
                    double y = yMargin + offset + ( row * spacingBetweenPairs );
                    double z = ( -dielectricDepth / 2 );
                    pairNode.setOffset( mvt.modelToView( x, y, z ) );

                    // spacing between charges
                    if ( x <= xPlateEdge ) {
                        pairNode.setSpacing( spacingBetweenCharges, polarity );
                    }
                    else {
                        pairNode.setSpacing( SPACING_BETWEEN_CHARGES.getMin(), polarity );
                    }
                }
                
                // side face
                {
                    // charge pair
                    ChargePairNode pairNode = new ChargePairNode();
                    parentNode.addChild( pairNode );
                    
                    // location
                    double x = ( dielectricWidth / 2 );
                    double y = yMargin + offset + ( row * spacingBetweenPairs );
                    double z = ( -dielectricDepth / 2 ) + offset + zMargin + ( column * spacingBetweenPairs );
                    pairNode.setOffset( mvt.modelToView( x, y, z ) );
                    
                    // spacing between charges
                    if ( circuit.getCapacitor().getDielectricOffset() == 0 ) {
                        pairNode.setSpacing( spacingBetweenCharges, polarity );
                    }
                    else {
                        pairNode.setSpacing( SPACING_BETWEEN_CHARGES.getMin(), polarity );
                    } 
                }
            }
        }
    }
    
    /*
     * Spacing between charges is non-linearly proportion to the E-field.
     */
    private double getSpacingBetweenCharges( double eField ) {
        double absEField = Math.abs( eField );
        double maxEField = BatteryCapacitorCircuit.getMaxDielectricEField();
        double percent = Math.pow( absEField / maxEField, SPACING_BETWEEN_CHARGES_EXPONENT );
        return SPACING_BETWEEN_CHARGES.getMin() + ( percent * SPACING_BETWEEN_CHARGES.getLength() );
    }
    
    /*
     * A positive and negative charge pair.
     * The vertical spacing is variable.
     * Origin is at the geometric center when vertical spacing is zero.
     */
    private static class ChargePairNode extends PComposite {
        
        private final PNode positiveNode, negativeNode;
        
        public ChargePairNode() {
            positiveNode = new PositiveChargeNode();
            addChild( positiveNode );
            negativeNode = new NegativeChargeNode();
            addChild( negativeNode ); // put negative charge on top, so we can see it when they overlap
        }
        
        public void setSpacing( double spacing, Polarity polarity ) {
            double yOffset = ( polarity == Polarity.POSITIVE ) ? -( spacing / 2 ) : ( spacing / 2 );
            positiveNode.setOffset( positiveNode.getXOffset(), yOffset );
            negativeNode.setOffset( negativeNode.getXOffset(), -yOffset );
        }
    }
}
