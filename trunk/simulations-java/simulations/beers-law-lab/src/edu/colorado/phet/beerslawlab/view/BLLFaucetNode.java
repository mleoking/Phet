// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.beerslawlab.BLLSimSharing.Parameters;
import edu.colorado.phet.beerslawlab.model.Faucet;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponent;
import edu.colorado.phet.common.piccolophet.nodes.faucet.FaucetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragSequenceEventHandler.DragFunction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Faucet with sim-sharing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BLLFaucetNode extends PNode {

    private final FaucetNode faucetNode;

    public BLLFaucetNode( final UserComponent userComponent, final Faucet faucet ) {

        // use composition, so we can move the origin to the center of the output pipe
        faucetNode = new FaucetNode( faucet.getMaxFlowRate(), faucet.flowRate, faucet.enabled, faucet.getInputPipeLength(), true );
        addChild( faucetNode );
        Point2D originOffset = globalToLocal( faucetNode.getGlobalOutputCenter() );
        faucetNode.setOffset( faucet.getLocation().getX() - originOffset.getX(), faucet.getLocation().getY() - originOffset.getY() );

        // sim-sharing
        faucetNode.getDragHandler().setStartEndDragFunction( new DragFunction() {
            public void apply( UserAction action, Parameter xParameter, Parameter yParameter, PInputEvent event ) {
                SimSharingManager.sendUserMessage( userComponent, action, Parameter.param( Parameters.flowRate, faucet.flowRate.get() ) );
            }
        } );
    }

    public double getFluidWidth() {
        return globalToLocal( faucetNode.getGlobalOutputSize() ).getWidth();
    }
}
