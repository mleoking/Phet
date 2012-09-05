// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildamolecule;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

public class BuildAMoleculeSimSharing {
    public enum UserComponent implements IUserComponent {
        atom
    }

    public enum ParameterKey implements IParameterKey {
        atomId, atomElement,

        atomWasInKitArea,
        atomDroppedInKitArea,

        bondOccurs,
        bondAtomA,
        bondAtomB,
        bondDirection,

        bondMoleculeDestroyedA,
        bondMoleculeDestroyedB,
        bondMoleculeCreated,

        moleculeStructureDestroyedA,
        moleculeStructureDestroyedB,
        moleculeStructureCreated
    }

    public enum ModelComponent implements IModelComponent {
        atom,
        molecule
    }

    public enum ModelAction implements IModelAction {
        atomDropped,
        bondAttempt,
        bonding
    }

}
