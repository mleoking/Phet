/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.mri.MriConfig;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

/**
 * DipoleOrientationAgent
 * <p>
 * Sets the spins of all dipoles in the model
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

/**
 * Sets the spins of dipoles based on the field strength of the fading magnet
 */
public class DipoleOrientationAgent implements Electromagnet.ChangeListener /*, ModelElement*/ {
    private Random random = new Random();
    private double maxUpPFraction = 0.9;
    private double fractionUp;
    private MriModel model;
//    private SpinDeterminationStrategy spinDeterminationStrategy = new Fixed();
    private SpinDeterminationPolicy spinDeterminationPolicy = MriConfig.InitialConditions.SPIN_DETERMINATION_POLICY;

    public DipoleOrientationAgent( MriModel model ) {
        this.model = model;
    }

//    public void stepInTime( double dt ) {
//        updateSpins();
//    }

    private void updateSpins() {
        List dipoles = model.getDipoles();
        spinDeterminationPolicy.setSpins( dipoles, fractionUp );
    }

    /**
     * When the field changes, the number of dipoles with each spin changes
     *
     * @param event
     */
    public void stateChanged( Electromagnet.ChangeEvent event ) {
        double fieldStrength = model.getUpperMagnet().getFieldStrength();
        fractionUp = 0.5 + ( 0.5 * fieldStrength / MriConfig.MAX_FADING_COIL_FIELD );
        fractionUp *= maxUpPFraction;
        updateSpins();
    }

    public void setPolicy( SpinDeterminationPolicy policy ) {
        spinDeterminationPolicy = policy;
    }

    //----------------------------------------------------------------
    // Strategies for determining the spins of dipoles in the model
    //----------------------------------------------------------------

    public static interface SpinDeterminationPolicy {
        void setSpins( List dipoles, double fractionUp );
    }

    /**
     * Sets a fixed number of dipoles to each orientation
     */
    public static class DeterministicPolicy implements SpinDeterminationPolicy {
        Random random = new Random();

        public void setSpins( List dipoles, double fractionUp ) {

            if( dipoles.size() > 0 ) {
                ArrayList upDipoles = new ArrayList();
                ArrayList downDipoles = new ArrayList();

                // Determine the fraction of dipoles that are current up
                for( int i = 0; i < dipoles.size(); i++ ) {
                    Dipole dipole = (Dipole)dipoles.get( i );
                    List list = dipole.getSpin() == Spin.UP ? upDipoles : downDipoles;
                    list.add( dipole );
                }
                double fractionCurrentlyUp = ( (double)upDipoles.size() ) / dipoles.size();

                if( fractionCurrentlyUp > fractionUp ) {
                    while( fractionCurrentlyUp > fractionUp ) {
                        Dipole dipole = (Dipole)upDipoles.get( random.nextInt( upDipoles.size() ) );
                        dipole.setSpin( Spin.DOWN );
                        upDipoles.remove( dipole );
                        downDipoles.add( dipole );
                        fractionCurrentlyUp = ( (double)upDipoles.size() ) / dipoles.size();
                    }
                }
                else if( fractionCurrentlyUp < fractionUp ) {

                    while( fractionCurrentlyUp < fractionUp ) {
                        Dipole dipole = (Dipole)downDipoles.get( random.nextInt( downDipoles.size() ) );
                        dipole.setSpin( Spin.UP );
                        downDipoles.remove( dipole );
                        upDipoles.add( dipole );
                        fractionCurrentlyUp = ( (double)upDipoles.size() ) / dipoles.size();
                    }
                }
            }

//
//            for( int i = 0; i < dipoles.size(); i++ ) {
//                Dipole dipole = (Dipole)dipoles.get( i );
//                Spin spin = ( (double)i ) / dipoles.size() < fractionUp ? Spin.UP : Spin.DOWN;
////                Spin spin = random.nextDouble() < fractionUp ? Spin.UP : Spin.DOWN;
//                dipole.setSpin( spin );
//            }
        }
    }

    public void setFractionUp( double fractionUp ) {
        if( fractionUp < 0 || fractionUp > 1 ) {
            throw new IllegalArgumentException();
        }
        this.fractionUp = fractionUp;
    }

    /**
     *
     */
    public static class StocasticPolicy implements SpinDeterminationPolicy {
        Random random = new Random();

        public void setSpins( List dipoles, double fractionUp ) {
            for( int i = 0; i < dipoles.size(); i++ ) {
                Dipole dipole = (Dipole)dipoles.get( i );
                boolean up = fractionUp > random.nextDouble();
                if( up && dipole.getSpin() != Spin.UP ) {
                    dipole.setSpin( Spin.UP );
                }
                else if( !up && dipole.getSpin() != Spin.DOWN ) {
                    dipole.setSpin( Spin.DOWN );
                }
            }
        }
    }
}
