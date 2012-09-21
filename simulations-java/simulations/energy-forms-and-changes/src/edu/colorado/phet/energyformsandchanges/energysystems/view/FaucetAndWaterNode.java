// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.FaucetAndWater;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo node that represents a faucet and flowing water in the view.
 *
 * @author John Blanco
 */
public class FaucetAndWaterNode extends PositionableFadableModelElementNode {

    private static final double FAUCET_NODE_HORIZONTAL_LENGTH = 700; // In screen coords, which are close to pixels.
    private static final double WATER_NODE_VERTICAL_LENGTH = 700; // In screen coords, which are close to pixels.

    public FaucetAndWaterNode( FaucetAndWater faucet, final ModelViewTransform mvt ) {
        super( faucet, mvt );

        // Create the faucet.
        final FaucetNode faucetNode = new FaucetNode( EnergyFormsAndChangesSimSharing.UserComponents.faucet,
                                                      faucet.flowProportion,
                                                      faucet.enabled,
                                                      FAUCET_NODE_HORIZONTAL_LENGTH,
                                                      false );
        faucetNode.setScale( 0.9 ); // Make it a little smaller than default.  Looks better in this sim.
        faucetNode.setOffset( -faucetNode.getGlobalOutputCenter().getX() + mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                              -faucetNode.getGlobalOutputCenter().getY() + mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );
        // Create the water.
        final PPath waterNode = new PhetPPath( EFACConstants.WATER_COLOR, new BasicStroke( 1 ), ColorUtils.darkerColor( EFACConstants.WATER_COLOR, 0.5 ) );
        waterNode.setOffset( mvt.modelToViewDeltaX( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getX() ),
                             mvt.modelToViewDeltaY( FaucetAndWater.OFFSET_FROM_CENTER_TO_WATER_ORIGIN.getY() ) );

        // Update the shape of the water based on the flow setting.
        faucet.flowProportion.addObserver( new VoidFunction1<Double>() {
            public void apply( Double flowProportion ) {
                double waterWidth = faucetNode.getGlobalOutputSize().getWidth() * 0.75 * flowProportion;
                Rectangle2D waterShape = new Rectangle2D.Double( -waterWidth / 2, 0, waterWidth, WATER_NODE_VERTICAL_LENGTH );
                waterNode.setPathTo( waterShape );
                waterNode.setPaint( new GradientPaint( (float) waterShape.getX(), (float) waterShape.getY(), ColorUtils.darkerColor( EFACConstants.WATER_COLOR, 0.3 ),
                                                       (float) waterShape.getMaxX(), (float) waterShape.getY(), ColorUtils.brighterColor( EFACConstants.WATER_COLOR, 0.5 ) ) );
                waterNode.setVisible( waterWidth > 0 );
            }
        } );

        // Create the energy chunk layer.
        PNode energyChunkLayer = new EnergyChunkLayer( faucet.energyChunkList, faucet.getObservablePosition(), mvt );

        // Add the nodes in the order that creates the desired layering.
        addChild( waterNode );
        addChild( energyChunkLayer );
        addChild( faucetNode );
    }
}