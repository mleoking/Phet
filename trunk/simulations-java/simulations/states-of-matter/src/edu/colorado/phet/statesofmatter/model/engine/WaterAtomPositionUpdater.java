/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.engine;

import java.awt.geom.Point2D;

import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.MoleculeForceAndMotionDataSet;

/**
 * This class updates the positions of atoms in a water molecule based on the
 * position and rotation information for the molecule.
 * 
 * @author John Blanco
 */
public class WaterAtomPositionUpdater implements AtomPositionUpdater {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final double BONDED_PARTICLE_DISTANCE = 0.9;  // In particle diameters.
	
	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public void updateAtomPositions( MoleculeForceAndMotionDataSet moleculeDataSet ) {
		
		// Make sure this is not being used on an inappropriate data set.
		assert moleculeDataSet.getAtomsPerMolecule() == 3;
		
		// Get direct references to the data in the data set.
		Point2D [] atomPositions = moleculeDataSet.getAtomPositions();
		Point2D [] moleculeCenterOfMassPositions = moleculeDataSet.getMoleculeCenterOfMassPositions();
		double [] moleculeRotationAngles = moleculeDataSet.getMoleculeRotationAngles();
		
        double xPos, yPos, cosineTheta, sineTheta;
        
        for (int i = 0; i < moleculeDataSet.getNumberOfMolecules(); i++){
            cosineTheta = Math.cos( moleculeRotationAngles[i] );
            sineTheta = Math.sin( moleculeRotationAngles[i] );
            xPos = moleculeCenterOfMassPositions[i].getX() + cosineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            yPos = moleculeCenterOfMassPositions[i].getY() + sineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            atomPositions[i * 2].setLocation( xPos, yPos );
            xPos = moleculeCenterOfMassPositions[i].getX() - cosineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            yPos = moleculeCenterOfMassPositions[i].getY() - sineTheta * (BONDED_PARTICLE_DISTANCE / 2);
            atomPositions[i * 2 + 1].setLocation( xPos, yPos );
        }
        
        for (int i = 0; i < moleculeDataSet.getNumberOfMolecules(); i++){
            cosineTheta = Math.cos( moleculeRotationAngles[i] );
            sineTheta = Math.sin( moleculeRotationAngles[i] );
            for (int j = 0; j < 3; j++){
                xPos = moleculeCenterOfMassPositions[i].getX() + cosineTheta * 
                        StatesOfMatterConstants.H2O_MOLECULE_STRUCTURE_X[j] - 
                        sineTheta * StatesOfMatterConstants.H2O_MOLECULE_STRUCTURE_Y[j];
                yPos = moleculeCenterOfMassPositions[i].getY() + sineTheta * 
                StatesOfMatterConstants.H2O_MOLECULE_STRUCTURE_X[j] + 
                        cosineTheta * StatesOfMatterConstants.H2O_MOLECULE_STRUCTURE_Y[j];
                atomPositions[i * 3 + j].setLocation( xPos, yPos );
            }
        }
	}
}
