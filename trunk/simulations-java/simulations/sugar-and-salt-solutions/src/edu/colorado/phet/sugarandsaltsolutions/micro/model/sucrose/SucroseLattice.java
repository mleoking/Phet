package edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Bond;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.BondType;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ImmutableList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Lattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.LatticeSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SquareLattice;

/**
 * Data structures and algorithms for creating and modeling a sugar crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SucroseLattice extends SquareLattice<Sucrose> {

    public SucroseLattice() {
        super( new ImmutableList<Sucrose>( new Sucrose() ), new ImmutableList<Bond<Sucrose>>() );
    }

    public SucroseLattice( ImmutableList<Sucrose> components, ImmutableList<Bond<Sucrose>> bonds ) {
        super( components, bonds );
    }

    @Override public Lattice<Sucrose> drop( final Sucrose component ) {
        return new SucroseLattice( components.drop( component ), bonds.drop( new Function1<Bond<Sucrose>, Boolean>() {
            public Boolean apply( Bond<Sucrose> particleBond ) {
                return particleBond.contains( component );
            }
        } ) );
    }

    @Override protected void testAddSite( ArrayList<LatticeSite<Sucrose>> latticeSites, Sucrose component, ArrayList<Bond<Sucrose>> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            latticeSites.add( new SucroseSite( component, type ) );
        }
    }
}