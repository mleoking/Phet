/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck3.circuit.analysis;

import edu.colorado.phet.cck.mna.MNACircuit;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 19, 2006
 * Time: 1:32:44 AM
 * Copyright (c) Jun 19, 2006 by Sam Reid
 */

public class MNASolver extends CircuitSolver {
    public void apply( Circuit circuit ) {
        if( circuit.numBranches() < 2 ) {
            clearCircuit();
            return;
        }
        MNACircuit mnaCircuit = new MNACircuit();
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            int startJunction = circuit.indexOf( branch.getStartJunction() );
            int endJunction = circuit.indexOf( branch.getEndJunction() );
            mnaCircuit.addComponent( toMNAComponent( branch, i, startJunction, endJunction ) );
        }
        MNACircuit.MNASolution solution = mnaCircuit.getSolution();
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch branch = circuit.branchAt( i );
            int startJunction = circuit.indexOf( branch.getStartJunction() );
            int endJunction = circuit.indexOf( branch.getEndJunction() );
//            mnaCircuit.addComponent( toMNAComponent( branch, i, startJunction, endJunction ) );
            double startVoltage = solution.getVoltage( startJunction );
            double endVoltage = solution.getVoltage( endJunction );
            if( !( branch instanceof Battery ) ) {
                branch.setKirkhoffEnabled( false );
                branch.setVoltageDrop( endVoltage - startVoltage );
                branch.setCurrent( branch.getVoltageDrop() / branch.getResistance() );
                branch.setKirkhoffEnabled( true );
            }
        }
        for( int i = 0; i < solution.getNumCurrents(); i++ ) {
            batteryAt( circuit, i ).setKirkhoffEnabled( false );
            batteryAt( circuit, i ).setCurrent( -solution.getCurrent( i ) );//todo there is a minus sign here
            batteryAt( circuit, i ).setKirkhoffEnabled( true );
        }
        fireCircuitSolved();
    }

    private void clearCircuit() {
        //todo clear the circuit
    }

    private Branch batteryAt( Circuit circuit, int i ) {
        return getBatteries( circuit )[i];
    }

    private Battery[] getBatteries( Circuit circuit ) {
        ArrayList all = new ArrayList();
        Branch[] branches = circuit.getBranches();
        for( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
            if( branch instanceof Battery ) {
                all.add( branch );
            }
        }
        return (Battery[])all.toArray( new Battery[0] );
    }

    private MNACircuit.MNAComponent toMNAComponent( Branch branch, int index, int start, int end ) {
        if( branch instanceof Battery ) {
            return new MNACircuit.MNAVoltageSource( "v_" + index, start, end, branch.getVoltageDrop() );
        }
        else if( branch instanceof Resistor ) {
            return new MNACircuit.MNAResistor( "r_" + index, start, end, branch.getResistance() );
        }
        else if( branch instanceof Branch ) {
            return new MNACircuit.MNAResistor( "r_" + index, start, end, branch.getResistance() );
        }
        throw new RuntimeException( "Component not recognized: " + branch.getClass() );
    }
}
