/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

/**
 * Class: MiddleEnergyState
 * Package: edu.colorado.phet.lasers.model
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
public class HighEnergyState extends AtomicState {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    protected static HighEnergyState instance = new HighEnergyState();

    public static HighEnergyState instance() {
        return instance;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //

    /**
     * We need to define a next higher state that allows the HighEnergyState to be adjusted upward a reasonable
     * way
     */
    private AtomicState nextHigherState = new AtomicState() {
        public void collideWithPhoton( Atom atom, Photon photon ) {
            // noop
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }

        public double getWavelength() {
            // The hard-coded number here is a hack so the energy level graphic can be adjusted up to the top of
            // the window. This is not great programming
            return minWavelength - 80;
        }

        public double getEnergyLevel() {
            return Photon.wavelengthToEnergy( getWavelength() );
        }
    };

    private HighEnergyState() {
        setEnergyLevel( Photon.wavelengthToEnergy( Photon.BLUE ) );
        determineEmittedPhotonWavelength();
    }

    public void collideWithPhoton( Atom atom, Photon photon ) {

        // If the photon has the same energy as the difference
        // between this level and the ground state, then emit
        // a photon of that energy
        if( isStimulatedBy( photon ) ) {

            // Place the replacement photon beyond the atom, so it doesn't collide again
            // right away
            Vector2D vHat = new Vector2D.Double( photon.getVelocity() ).normalize();
            vHat.scale( atom.getRadius() + 10 );
            Point2D position = new Point2D.Double( atom.getPosition().getX() + vHat.getX(),
                                                   atom.getPosition().getY() + vHat.getY() );
            photon.setPosition( position );
            Photon emittedPhoton = Photon.createStimulated( photon, position, atom );
            atom.emitPhoton( emittedPhoton );

            // Change state
            atom.setState( GroundState.instance() );
        }

        // If the photon has the same energy level as the difference between
        // this state and the high energy one, then we go to that state
        if( photon.getEnergy() == HighEnergyState.instance().getEnergyLevel() - this.getEnergyLevel() ) {

            // Absorb the photon and change state
            photon.removeFromSystem();

            // Change state
            atom.setState( HighEnergyState.instance() );
        }
    }

    public AtomicState getNextLowerEnergyState() {
        return MiddleEnergyState.instance();
    }

    public AtomicState getNextHigherEnergyState() {
        return nextHigherState;
    }

    public void setNextHigherEnergyState( AtomicState state ) {
        nextHigherState = state;
    }
}
