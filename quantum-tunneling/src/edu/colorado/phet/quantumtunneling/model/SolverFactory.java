/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;


/**
 * SolverFactory
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SolverFactory {

    /* Not intended for instantiation */
    private SolverFactory() {}
    
    /**
     * Creates a solver that matches the type of the potential energy.
     * 
     * @param te
     * @param pe
     * @return
     */
    public static AbstractSolver createSolver( TotalEnergy te, AbstractPotentialSpace pe ) {
        AbstractSolver solver = null;
        if ( pe instanceof ConstantPotential ) {
            solver = new ConstantSolver( te, (ConstantPotential) pe );
        }
        else if ( pe instanceof StepPotential ) {
            solver = new StepSolver( te, (StepPotential) pe );
        }
        else if ( pe instanceof BarrierPotential ) {
            BarrierPotential barrier = (BarrierPotential) pe;
            int numberOfBarriers = barrier.getNumberOfBarriers();
            if ( numberOfBarriers == 1 ) {
                solver = new SingleBarrierSolver( te, barrier );
            }
            else if ( numberOfBarriers == 2 ) {
                solver = new DoubleBarrierSolver( te, barrier );
            }
            else {
                throw new IllegalStateException( "no solution for " + numberOfBarriers + " barriers" );
            }
        }
        else {
            throw new IllegalArgumentException( "unsupported potential type: " + pe.getClass().getName() );
        }
        return solver;
    }
}
