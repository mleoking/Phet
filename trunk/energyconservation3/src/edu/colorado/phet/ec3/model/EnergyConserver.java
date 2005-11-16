/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model;

import edu.colorado.phet.common.math.AbstractVector2D;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 11:38:49 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class EnergyConserver {
    public void fixEnergy( EnergyConservationModel model, Body body, double desiredMechanicalEnergy ) {
        if( body.getThrust().getMagnitude() != 0 ) {
            return;
        }

        EC3Debug.debug( "body.getSpeed() = " + body.getSpeed() );
        EnergyDebugger.stepFinished( model, body );
//        double speedThreshold = 5;//reduced from 20.
        double speedThreshold = 1;//reduced from 20.
        System.out.println( "body.getSpeed() = " + body.getSpeed() );
        for( int i = 0; i < 10; i++ ) {
            if( body.getSpeed() > speedThreshold ) {
//                System.out.println( "Conserve Via V" );
                conserveEnergyViaV( model, body, desiredMechanicalEnergy );
            }
        }
//        else{
//            System.out.println( "Speed too low to use for conservation." );
//        }
        if( model.getGravity() != 0.0 ) {
            conserveEnergyViaH( model, body, desiredMechanicalEnergy );
        }
//        EnergyDebugger.postProcessed( model, body, origTotalEnergy, "dH" );
        double finalEnergy = model.getTotalMechanicalEnergy( body );
        double deTOT = finalEnergy - desiredMechanicalEnergy;
        EC3Debug.debug( "dETOT=" + deTOT );
    }

    private void conserveEnergyViaV( EnergyConservationModel model, Body body, double origTotalEnergy ) {
        double finalTotalEnergy = model.getTotalMechanicalEnergy( body );
        double dE = finalTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dE = " + dE );
        //how can we put this change in energy back in the system?
        double dv = dE / body.getMass() / body.getSpeed();
        AbstractVector2D dvVector = body.getVelocity().getInstanceOfMagnitude( -dv );
        body.setVelocity( dvVector.getAddedInstance( body.getVelocity() ) );

        double modifiedTotalEnergy = model.getTotalMechanicalEnergy( body );
        double dEMod = modifiedTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dEModV = " + dEMod );
    }

    private void conserveEnergyViaH( EnergyConservationModel model, Body body, double origTotalEnergy ) {
        double finalTotalEnergy = model.getTotalMechanicalEnergy( body );
        double dE = finalTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dE = " + dE );
        double dh = dE / body.getMass() / model.getGravity();
        if( model.getGravity() == 0 ) {
            dh = 0.0;
        }
        body.translate( 0, dh );
        double modifiedTotalEnergy = model.getTotalMechanicalEnergy( body );
        double dEMod = modifiedTotalEnergy - origTotalEnergy;
        EC3Debug.debug( "dEModH = " + dEMod );
    }
}
