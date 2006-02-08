/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.solublesalts.model.salt;

import edu.colorado.phet.solublesalts.model.crystal.Lattice_new_new;
import edu.colorado.phet.solublesalts.model.crystal.PlainCubicLattice;
import edu.colorado.phet.solublesalts.model.ion.Bromine;
import edu.colorado.phet.solublesalts.model.ion.Mercury;

import java.util.ArrayList;

/**
 * MercuryBromine
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MercuryBromide extends Salt {

    static private Lattice_new_new lattice = new PlainCubicLattice( Mercury.RADIUS + Bromine.RADIUS );
//    static private Lattice lattice = new PlainCubicLattice( Mercury.RADIUS + Bromine.RADIUS );
    static private ArrayList components = new ArrayList();

    static {
        components.add( new Component( Mercury.class, new Integer( 1 ) ) );
        components.add( new Component( Bromine.class, new Integer( 1 ) ) );
    }

    public MercuryBromide() {
        super( components, lattice, Mercury.class, Bromine.class, 1.3E-22 );
    }
}
