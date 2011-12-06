// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;
import edu.colorado.phet.common.phetcommon.util.FunctionalUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.chemistry.model.Element.*;
import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.filter;

/**
 * Represents a "real" molecule with exact positions, as opposed to a molecule model (which is VSEPR-based
 * and doesn't include other information).
 * <p/>
 * We display these real molecules to the user in 3D
 */
public class RealMoleculeShape {

    private final String displayName;
    private List<Atom3D> atoms = new ArrayList<Atom3D>();
    private List<Bond<Atom3D>> bonds = new ArrayList<Bond<Atom3D>>();
    private Atom3D centralAtom = null;

    private RealMoleculeShape( String displayName ) {
        this.displayName = displayName;
    }

    public void addAtom( Atom3D atom ) {
        assert !atoms.contains( atom );
        atoms.add( atom );
    }

    public void addBond( Atom3D a, Atom3D b, int order ) {
        bonds.add( new Bond<Atom3D>( a, b, order ) );
    }

    public Collection<Atom3D> getAtoms() {
        return atoms;
    }

    public Collection<Bond<Atom3D>> getBonds() {
        return bonds;
    }

    public void addCentralAtom( Atom3D atom ) {
        addAtom( atom );
        centralAtom = atom;
    }

    public void addRadialAtom( Atom3D atom, int bondOrder ) {
        addAtom( atom );
        addBond( atom, centralAtom, bondOrder );
    }

    public void centerOnCentralAtom() {
        translate( centralAtom.position.get().negated() );
    }

    public void centerOnCentroid() {
        ImmutableVector3D centroid = new ImmutableVector3D();
        for ( Atom3D atom3D : getAtoms() ) {
            centroid = centroid.plus( atom3D.position.get() );
        }
        translate( centroid.times( -1 / (float) getAtoms().size() ) );
    }

    private void translate( ImmutableVector3D offset ) {
        for ( Atom3D atom : getAtoms() ) {
            atom.position.set( atom.position.get().plus( offset ) );
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    public Atom3D getCentralAtom() {
        return centralAtom;
    }

    public int getCentralLonePairCount() {
        return getCentralAtom().lonePairCount;
    }

    public int getCentralAtomCount() {
        final Atom3D centralAtom = getCentralAtom();
        return FunctionalUtils.map( filter( getBonds(), new Function1<Bond<Atom3D>, Boolean>() {
            public Boolean apply( Bond<Atom3D> bond ) {
                return bond.contains( centralAtom );
            }
        } ), new Function1<Bond<Atom3D>, Atom3D>() {
            public Atom3D apply( Bond<Atom3D> bond ) {
                return bond.getOtherAtom( centralAtom );
            }
        } ).size();
    }

    @Override public String toString() {
        return displayName;
    }

    public static final RealMoleculeShape BERYLLIUM_CHLORIDE = new RealMoleculeShape( "BeCl2" ) {{ // TODO: more accurate numbers?
        addCentralAtom( new Atom3D( Be, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.8, 0, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.8, 0, 0 ), 3 ), 1 );
    }};

    public static final RealMoleculeShape BORON_TRIFLUORIDE = new RealMoleculeShape( "BF3" ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D() ) );
        double angle = 2 * Math.PI / 3;
        double bondLength = 1.313;
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( bondLength * Math.cos( 0 * angle ), bondLength * Math.sin( 0 * angle ), 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( bondLength * Math.cos( 1 * angle ), bondLength * Math.sin( 1 * angle ), 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( bondLength * Math.cos( 2 * angle ), bondLength * Math.sin( 2 * angle ), 0 ), 3 ), 1 );
    }};

    public static final RealMoleculeShape BROMINE_PENTAFLUORIDE = new RealMoleculeShape( "BrF5" ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D(), 1 ) );
        double axialBondLength = 1.689;
        double radialBondLength = 1.774;
        double angle = Math.toRadians( 84.8 );
        double radialDistance = Math.sin( angle ) * radialBondLength;
        double axialDistance = Math.cos( angle ) * radialBondLength;
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, -axialBondLength, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( radialDistance, -axialDistance, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, -axialDistance, radialDistance ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -radialDistance, -axialDistance, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, -axialDistance, -radialDistance ), 3 ), 1 );
    }};

    public static final RealMoleculeShape METHANE = new RealMoleculeShape( "CH4" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        double bondLength = 1.087;
        for ( ImmutableVector3D v : GeometryConfiguration.getConfiguration( 4 ).unitVectors ) {
            addRadialAtom( new Atom3D( H, v.times( bondLength ), 0 ), 1 );
        }
    }};

    public static final RealMoleculeShape CHLORINE_TRIFLUORIDE = new RealMoleculeShape( "ClF3" ) {{
        addCentralAtom( new Atom3D( Cl, new ImmutableVector3D(), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, -1.598, 0 ), 3 ), 1 );
        double radialAngle = Math.toRadians( 87.5 );
        double radialBondLength = 1.698;
        double radialDistance = Math.sin( radialAngle ) * radialBondLength;
        double axialDistance = Math.cos( radialAngle ) * radialBondLength;
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( radialDistance, -axialDistance, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -radialDistance, -axialDistance, 0 ), 3 ), 1 );
    }};

    public static final RealMoleculeShape CARBON_DIOXIDE = new RealMoleculeShape( "CO2" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.163, 0, 0 ), 2 ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.163, 0, 0 ), 2 ), 2 );
    }};

    public static final RealMoleculeShape WATER = new RealMoleculeShape( "H2O" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D(), 2 ) );
        double radialBondLength = 0.957;
        double radialAngle = Math.toRadians( 104.5 ) / 2;
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( Math.sin( radialAngle ), -Math.cos( radialAngle ), 0 ).times( radialBondLength ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -( Math.sin( radialAngle ) ), -Math.cos( radialAngle ), 0 ).times( radialBondLength ) ), 1 );
    }};

    public static final RealMoleculeShape AMMONIA = new RealMoleculeShape( "NH3" ) {{
        addCentralAtom( new Atom3D( N, new ImmutableVector3D(), 1 ) );
        double radialBondLength = 1.017;

        // to solve a "axial" angle (from the axis of symmetry), Solve[Cos[\[Beta]] == Cos[\[Alpha]]^2*Cos[\[Theta]] + Sin[\[Alpha]]^2, \[Alpha]]  where beta is our given intra-bond angle, alpha is solved for, and theta = 2 pi / n where n is our number of bonds (3 in this case)
        double axialAngle = 1.202623030417028; // lots of precision, from Mathematica
        double radialAngle = 2 * Math.PI / 3;
        double radialDistance = Math.sin( axialAngle ) * radialBondLength;
        double axialDistance = Math.cos( axialAngle ) * radialBondLength;
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( radialDistance * Math.cos( 0 * radialAngle ), -axialDistance, radialDistance * Math.sin( 0 * radialAngle ) ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( radialDistance * Math.cos( 1 * radialAngle ), -axialDistance, radialDistance * Math.sin( 1 * radialAngle ) ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( radialDistance * Math.cos( 2 * radialAngle ), -axialDistance, radialDistance * Math.sin( 2 * radialAngle ) ) ), 1 );
    }};

    public static final RealMoleculeShape PHOSPHORUS_PENTACHLORIDE = new RealMoleculeShape( "PCl5" ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 2.14, 0, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -2.14, 0, 0 ), 3 ), 1 );
        double radialAngle = 2 * Math.PI / 3;
        double radialBondLength = 2.02;
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0, Math.cos( 0 * radialAngle ), Math.sin( 0 * radialAngle ) ).times( radialBondLength ), 3 ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0, Math.cos( 1 * radialAngle ), Math.sin( 1 * radialAngle ) ).times( radialBondLength ), 3 ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0, Math.cos( 2 * radialAngle ), Math.sin( 2 * radialAngle ) ).times( radialBondLength ), 3 ), 1 );
    }};

    public static final RealMoleculeShape SULFUR_TETRAFLUORIDE = new RealMoleculeShape( "SF4" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D(), 1 ) );
        double largeAngle = Math.toRadians( 173.1 ) / 2;
        double smallAngle = Math.toRadians( 101.6 ) / 2;

        addRadialAtom( new Atom3D( F, new ImmutableVector3D( Math.sin( largeAngle ), -Math.cos( largeAngle ), 0 ).times( 1.646 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -Math.sin( largeAngle ), -Math.cos( largeAngle ), 0 ).times( 1.646 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, -Math.cos( smallAngle ), Math.sin( smallAngle ) ).times( 1.545 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, -Math.cos( smallAngle ), -Math.sin( smallAngle ) ).times( 1.545 ), 3 ), 1 );
    }};

    public static final RealMoleculeShape SULFUR_HEXAFLUORIDE = new RealMoleculeShape( "SF6" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D() ) );
        for ( ImmutableVector3D vector : GeometryConfiguration.getConfiguration( 6 ).unitVectors ) {
            addRadialAtom( new Atom3D( F, vector.times( 1.564 ), 3 ), 1 );
        }
    }};

    public static final RealMoleculeShape SULFUR_DIOXIDE = new RealMoleculeShape( "SO2" ) {{
        double bondAngle = Math.toRadians( 119 ) / 2;
        double bondLength = 1.431;
        addCentralAtom( new Atom3D( S, new ImmutableVector3D(), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( Math.sin( bondAngle ), Math.cos( bondAngle ), 0 ).times( bondLength ), 2 ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -Math.sin( bondAngle ), Math.cos( bondAngle ), 0 ).times( bondLength ), 2 ), 2 );
    }};

    public static final RealMoleculeShape XENON_DIFLUORIDE = new RealMoleculeShape( "XeF2" ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D(), 3 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.977, 0, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.977, 0, 0 ), 3 ), 1 );
    }};

    public static final RealMoleculeShape XENON_TETRAFLUORIDE = new RealMoleculeShape( "XeF4" ) {{
        double bondLength = 1.953;
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D(), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( bondLength, 0, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -bondLength, 0, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, 0, bondLength ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, 0, -bondLength ), 3 ), 1 );
    }};

    public static final RealMoleculeShape[] TAB_2_MOLECULES = new RealMoleculeShape[] {
            BERYLLIUM_CHLORIDE,
            BORON_TRIFLUORIDE,
            BROMINE_PENTAFLUORIDE,
            METHANE,
            CHLORINE_TRIFLUORIDE,
            CARBON_DIOXIDE,
            WATER,
            AMMONIA,
            PHOSPHORUS_PENTACHLORIDE,
            SULFUR_TETRAFLUORIDE,
            SULFUR_HEXAFLUORIDE,
            SULFUR_DIOXIDE,
            XENON_DIFLUORIDE,
            XENON_TETRAFLUORIDE
    };
}
