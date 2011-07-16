package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import java.util.ArrayList;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.*;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Component.SodiumIon;

/**
 * Data structures and algorithms for creating and modeling a salt crystal lattice.  Instances are immutable.
 *
 * @author Sam Reid
 */
public class SodiumChlorideLattice extends SquareLattice<SodiumChlorideLattice> {

    public SodiumChlorideLattice() {
        super( new ImmutableList<Component>( new SodiumIon() ), new ImmutableList<Bond>() );
    }

    public SodiumChlorideLattice( ImmutableList<Component> components, ImmutableList<Bond> bonds ) {
        super( components, bonds );
    }

    @Override protected void testAddSite( ArrayList<OpenSite<SodiumChlorideLattice>> openSites, Component component, ArrayList<Bond> bonds, BondType type ) {
        if ( !containsBondType( bonds, type ) ) {
            openSites.add( new SodiumChlorideSite( component, type ) );
        }
    }

    //Sample main to test lattice construction
    public static void main( String[] args ) {
        Lattice lattice = new SodiumChlorideLattice().grow( 100 );
        System.out.println( "saltLattice = " + lattice );
    }
}