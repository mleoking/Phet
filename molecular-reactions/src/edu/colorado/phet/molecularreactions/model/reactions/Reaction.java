/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.reactions;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionAgent;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;
import edu.colorado.phet.molecularreactions.MRConfig;

/**
 * Reaction
 * <p/>
 * This class encapsulates all the criteria for whether a reaction will
 * occur or not.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
abstract public class Reaction {
    private EnergyProfile energyProfile;
    private ReactionCriteria reactionCriteria;

    /**
     * @param energyProfile
     * @param reactionCriteria
     */
    protected Reaction( EnergyProfile energyProfile, ReactionCriteria reactionCriteria ) {
        this.energyProfile = energyProfile;
        this.reactionCriteria = reactionCriteria;
    }

    public EnergyProfile getEnergyProfile() {
        return energyProfile;
    }

    public ReactionCriteria getReactionCriteria() {
        return reactionCriteria;
    }

    public boolean areCriteriaMet( Molecule bodyA, Molecule bodyB, MoleculeMoleculeCollisionSpec collisionSpec ) {
        boolean result = false;
        if( this.moleculesAreProperTypes( bodyA, bodyB ) ) {
            double energyThreshold = getThresholdEnergy( bodyA, bodyB );
            result = reactionCriteria.criteriaMet( bodyA, bodyB, collisionSpec, energyThreshold );
        }
        return result;
    }

    //--------------------------------------------------------------------------------------------------
    // Abstract and template methods
    //--------------------------------------------------------------------------------------------------

    /**
     * Checks to see if two molecules are the right types for the reaction
     *
     * @param molecule1
     * @param molecule2
     * @return true if the molecules are of the correct type for the reaction
     */
    public boolean moleculesAreProperTypes( Molecule molecule1, Molecule molecule2 ) {
        return getReactionCriteria().moleculesAreProperTypes( molecule1, molecule2 );
    }

    abstract public SimpleMolecule getMoleculeToRemove( CompositeMolecule compositeMolecule, SimpleMolecule moleculeAdded );

    abstract public SimpleMolecule getMoleculeToKeep( CompositeMolecule compositeMolecule, SimpleMolecule moleculeAdded );

    abstract public double getThresholdEnergy( Molecule mA, Molecule mB );

    //--------------------------------------------------------------------------------------------------
    // Reaction criteria
    //--------------------------------------------------------------------------------------------------

    public interface ReactionCriteria {
        boolean criteriaMet( Molecule bodyA,
                             Molecule bodyB,
                             MoleculeMoleculeCollisionSpec collisionSpec,
                             double energyThreshold );

        boolean moleculesAreProperTypes( Molecule molecule1, Molecule molecule2 );
    }

    /**
     * Combines two simple molecules of different types into one compound molecule
     */
    class SimpleMoleculeSimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec collisionSpec, double energyThreshold ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > energyProfile.getPeakLevel()
                   && m1 instanceof SimpleMolecule && m2 instanceof SimpleMolecule
                   && m1.getClass() != m2.getClass();
        }

        public boolean moleculesAreProperTypes( Molecule molecule1, Molecule molecule2 ) {
            return true;
        }
    }

    /**
     * Combines any two molecules together
     */
    class SimpleMoleculeReactionCriteria implements ReactionCriteria {
        public boolean criteriaMet( Molecule m1, Molecule m2, MoleculeMoleculeCollisionSpec collisionSpec, double energyThreshold ) {
            return m1.getKineticEnergy() + m2.getKineticEnergy() > energyProfile.getPeakLevel();
        }

        public boolean moleculesAreProperTypes( Molecule molecule1, Molecule molecule2 ) {
            return true;
        }
    }
}
