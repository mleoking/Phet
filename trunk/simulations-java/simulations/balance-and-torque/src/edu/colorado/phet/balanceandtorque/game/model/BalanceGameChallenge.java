// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.game.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.balanceandtorque.teetertotter.model.ColumnState;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.Mass;

/**
 * Base class for a single "challenge" (a.k.a. problem) that is presented to
 * the user during the balance game.
 *
 * @author John Blanco
 */
public abstract class BalanceGameChallenge {

    // List of masses that will initially be sitting on the balance, and which
    // the user will not manipulate.
    public final List<MassDistancePair> fixedMassDistancePairs = new ArrayList<MassDistancePair>();

    // List of masses that the user will move into the appropriate positions
    // in order to balance out the other masses.
    public final List<Mass> movableMasses = new ArrayList<Mass>();

    // A configuration in which the movable masses balance the fixed masses.
    // For some challenges, this is what will be displayed to the user if they
    // ask to see a correct answer.
    public final List<MassDistancePair> balancedConfiguration = new ArrayList<MassDistancePair>();

    // State of the support column or columns when the challenge is initially
    // presented to the user.
    public final ColumnState initialColumnState;

    /**
     * Constructor.
     *
     * @param initialColumnState
     */
    public BalanceGameChallenge( ColumnState initialColumnState ) {
        this.initialColumnState = initialColumnState;
    }

    /**
     * Returns true if the specified challenge uses the same masses as this
     * challenge.  Note that "same masses" means the same classes, not just
     * the same values.  For example, if both challenges have a movable mass
     * that weigh 60kg but one is a rock and the other is a person, this will
     * return false.
     *
     * @param that
     * @return
     */
    public boolean usesSameMasses( BalanceGameChallenge that ) {
        if ( this == that ) {
            return true;
        }

        if ( movableMasses.size() != that.movableMasses.size() ) {
            return false;
        }

        if ( !movableMasses.containsAll( that.movableMasses ) ) {
            return false;
        }

        List<Mass> thisFixedMasses = getFixedMassList();
        List<Mass> thatFixedMasses = that.getFixedMassList();

        if ( thisFixedMasses.size() != thatFixedMasses.size() ) {
            return false;
        }

        if ( !thisFixedMasses.containsAll( thatFixedMasses ) ) {
            return false;
        }

        // If we made it to here, all masses are the same.
        return true;
    }

    @Override public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        BalanceGameChallenge that = (BalanceGameChallenge) o;

        for ( MassDistancePair massDistancePair : fixedMassDistancePairs ) {
            if ( !that.fixedMassDistancePairs.contains( massDistancePair ) ) {
                return false;
            }
        }

        if ( movableMasses.size() != that.movableMasses.size() ) {
            return false;
        }

        List<Mass> copyOfThatMovableMasses = new ArrayList<Mass>( that.movableMasses );
        for ( Mass thisMass : movableMasses ) {
            for ( Mass thatMass : new ArrayList<Mass>( copyOfThatMovableMasses ) ) {
                if ( thisMass.getMass() == thatMass.getMass() ) {
                    if ( copyOfThatMovableMasses.contains( thatMass ) ) {
                        copyOfThatMovableMasses.remove( thatMass );
                        break;
                    }
                }
            }
        }

        if ( copyOfThatMovableMasses.size() != 0 ) {
            return false;
        }

        if ( !balancedConfiguration.equals( that.balancedConfiguration ) ) {
            return false;
        }

        return true;
    }

    /**
     * Get configuration information about how this challenge should be
     * displayed in the view.
     *
     * @return
     */
    public abstract ChallengeViewConfig getChallengeViewConfig();

    /**
     * Get a list of the fixed masses for this challenge (without their
     * distances).
     *
     * @return
     */
    public List<Mass> getFixedMassList() {
        List<Mass> fixedMassList = new ArrayList<Mass>();
        for ( MassDistancePair fixedMassDistancePair : fixedMassDistancePairs ) {
            fixedMassList.add( fixedMassDistancePair.mass );
        }
        return fixedMassList;
    }

    /**
     * Get the sum of the mass for all the fixed masses in this challenge.
     *
     * @return
     */
    public double getFixedMassValueTotal() {
        double totalMass = 0;
        for ( MassDistancePair massDistancePair : fixedMassDistancePairs ) {
            totalMass += massDistancePair.mass.getMass();
        }
        return totalMass;
    }
}
