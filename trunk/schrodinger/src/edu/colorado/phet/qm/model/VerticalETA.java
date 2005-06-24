/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.operators.PyValue;
import edu.colorado.phet.qm.model.operators.YValue;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 10:44:51 AM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class VerticalETA extends DiscreteModel.Adapter {
    private int time = 0;
    private double eta;
    private ArrayList listeners = new ArrayList();

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public static interface Listener {
        public void arrived();
    }

    public void finishedTimeStep( DiscreteModel model ) {
        time++;
        if( time == Math.floor( eta ) ) {
            System.out.println( "The time has come!" );
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.arrived();
            }
        }
        else {
//            System.out.println( "time = " + time );
        }
    }

    public void particleFired( DiscreteModel discreteModel ) {
        double py = new PyValue().compute( discreteModel.getWavefunction() );
        double y0 = new YValue().compute( discreteModel.getWavefunction() ) * discreteModel.getWavefunction().getWidth();
        double d = 0 - y0;
        System.out.println( "py = " + py );
        eta = d / py;
        System.out.println( "eta=" + eta );
        eta += eta / 8;//add some
        time = 0;
    }
}
