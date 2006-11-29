/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergySkateParkModel;
import edu.colorado.phet.piccolo.help.MotionHelpBalloon;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 1, 2006
 * Time: 11:34:47 PM
 * Copyright (c) Jun 1, 2006 by Sam Reid
 */

public class WiggleMeInSpace {
    private EnergySkateParkModule module;
    private MotionHelpBalloon hintNode;
    private boolean hintDone = false;

    public WiggleMeInSpace( final EnergySkateParkModule module ) {
        this.module = module;
        hintNode = new MotionHelpBalloon( module.getDefaultHelpPane(), EnergySkateParkStrings.getString( "html.press.the.arrow.keys.br.to.apply.thrust.html" ) );
        hintNode.setTextColor( Color.white );
        hintNode.setShadowTextColor( Color.darkGray );
        hintNode.setShadowTextOffset( 1 );
        module.getEnergyConservationModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                if( module.getEnergyConservationModel().getGravity() == 0.0 && !hintDone ) {
                    startHint();
                }
                else {
                    closeHint();
                }
            }
        } );
        hintNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                hintNode.setVisible( false );
            }
        } );
    }

    private void closeHint() {
//        hintNode.setVisible( false );
        getRootNode().removeScreenChild( hintNode );
    }

    private void startHint() {
        module.getEnergyConservationCanvas().requestFocus();
        getRootNode().addScreenChild( hintNode );
        hintNode.setOffset( module.getEnergyConservationCanvas().getWidth() / 2, hintNode.getFullBounds().getHeight() / 2 );
        hintNode.animateTo( module.getEnergyConservationCanvas().getWidth() / 2, (int)( module.getEnergyConservationCanvas().getHeight() * 1.0 / 4.0 ) );
        module.getEnergyConservationModel().bodyAt( 0 ).addListener( new Body.Listener() {
            public void thrustChanged() {
                hintNode.setVisible( false );
                hintDone = true;
            }

            public void doRepaint() {
            }
        } );
//        hintNode.st
    }

    private EnergySkateParkRootNode getRootNode() {
        return module.getEnergyConservationCanvas().getRootNode();
    }

    public void start() {
    }
}
