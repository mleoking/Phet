// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.threeatoms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.moleculepolarity.common.model.MPModel;
import edu.colorado.phet.moleculepolarity.common.model.TriatomicMolecule;

/**
 * Model for the "Two Atoms" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ThreeAtomsModel extends MPModel {

    public final TriatomicMolecule molecule;

    public ThreeAtomsModel( IClock clock ) {

        molecule = new TriatomicMolecule( new ImmutableVector2D( 425, 390 ) ); //TODO revisit this and make sure it's accounted for in canvas layout

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                if ( eField.enabled.get() && !molecule.isDragging() ) {
                    // if the E-field is on and the user isn't controlling the molecule's orientation...
                    updateMoleculeOrientation();
                }
            }
        } );
    }

    @Override public void reset() {
        super.reset();
        molecule.reset();
    }

    /*
     * Rotate the molecule one step towards alignment with the E-field.
     * Angular velocity proportional to the different in electronegativity between the 2 atoms.
     */
    private void updateMoleculeOrientation() {
        //TODO align molecular dipole with E-Field, see what can be stolen from TwoAtomsModel
    }
}
