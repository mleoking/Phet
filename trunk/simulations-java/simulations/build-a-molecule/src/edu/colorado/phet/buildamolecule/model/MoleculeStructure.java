// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.*;

import edu.colorado.phet.chemistry.model.Atomic;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.chemistry.utils.ChemUtils;

/**
 * Represents a general molecular structure (without position or instance information).
 * <p/>
 */
public class MoleculeStructure {
    private Set<Atomic> atoms = new HashSet<Atomic>();
    private Set<Bond> bonds = new HashSet<Bond>();

    private static int nextMoleculeId = 0;
    private int moleculeId; // used for molecule identification and ordering for optimization

    /**
     * Map of atom => all bonds that contain that atom TODO: consider removing permanently. removed temporarily due to memory concerns
     */
//    private Map<Atom, Set<Bond>> bondMap = new HashMap<Atom, Set<Bond>>();
    public MoleculeStructure() {
        moleculeId = nextMoleculeId++;
    }

    /**
     * @param atom An atom to add
     * @return The atom that was added
     */
    public Atomic addAtom( Atomic atom ) {
        assert ( !atoms.contains( atom ) );
        atoms.add( atom );
//        bondMap.put( atom, new HashSet<Bond>() );
        return atom;
    }

    public void addBond( Bond bond ) {
        assert ( atoms.contains( bond.a ) );
        assert ( atoms.contains( bond.b ) );
        bonds.add( bond );
//        bondMap.get( bond.a ).add( bond );
//        bondMap.get( bond.b ).add( bond );
    }

    public void addBond( Atomic a, Atomic b ) {
        addBond( new Bond( a, b ) );
    }

    public Set<Bond> getBondsInvolving( Atomic atom ) {
        Set<Bond> result = new HashSet<Bond>();
        for ( Bond bond : bonds ) {
            if ( bond.contains( atom ) ) {
                result.add( bond );
            }
        }
        return result;
    }

    /**
     * @return A complete molecule that matches, or null.
     */
    public CompleteMolecule getMatchingCompleteMolecule() {
        return MoleculeList.getMasterInstance().findMatchingCompleteMolecule( this );
    }

    public boolean isAllowedStructure() {
        // shortcut, since we always allow single atoms!
        if ( getAtoms().size() < 2 ) {
            return true;
        }
        return MoleculeList.getMasterInstance().isAllowedStructure( this );
    }

    public String getHillSystemFormulaFragment() {
        return ChemUtils.hillOrderedSymbol( atoms );
    }

    private static final Map<String, String> formulaExceptions = new HashMap<String, String>();

    static {
        formulaExceptions.put( "H3N", "NH3" ); // treated as if it is organic
        formulaExceptions.put( "CHN", "HCN" ); // not considered organic
    }

    /**
     * Our best attempt at getting a general molecular naming algorithm that handles organic and non-organic compounds.
     * <p/>
     *
     * @return Text which is the molecular formula
     */
    public String getGeneralFormula() {
        boolean containsCarbon = containsAtomOfType( Element.C );
        boolean containsHydrogen = containsAtomOfType( Element.H );

        boolean organic = containsCarbon && containsHydrogen;

        List<Atomic> sortedAtoms = new LinkedList<Atomic>( atoms );
        if ( organic ) {
            // carbon first, then hydrogen, then others alphabetically
            Collections.sort( sortedAtoms, new Comparator<Atomic>() {
                public int compare( Atomic a, Atomic b ) {
                    return new Double( organicSortValue( a ) ).compareTo( organicSortValue( b ) );
                }
            } );
        }
        else {
            // sort by increasing electronegativity
            Collections.sort( sortedAtoms, new Comparator<Atomic>() {
                public int compare( Atomic a, Atomic b ) {
                    return new Double( electronegativeSortValue( a ) ).compareTo( electronegativeSortValue( b ) );
                }
            } );
        }

        // grab our formula out
        String formula = ChemUtils.createSymbolWithoutSubscripts( sortedAtoms.toArray( new Atomic[sortedAtoms.size()] ) );

        // return the formula, unless it is in our exception list (in which case, handle the exception case)
        return formulaExceptions.containsKey( formula ) ? formulaExceptions.get( formula ) : formula;
    }

    /**
     * Return the molecular formula, with structural information available if possible. Currently handles alcohol structure based on
     * https://secure.wikimedia.org/wikipedia/en/wiki/Alcohols#Common_Names
     *
     * @return Text which is the structural formula
     */
    public String getStructuralFormula() {

        // scan for alcohols (OH bonded to C)
        int alcoholCount = 0;

        // we pull of the alcohols so we can get that molecular formula (and we append the alcohols afterwards)
        MoleculeStructure structureWithoutAlcohols = getCopy();
        for ( Atomic oxygenAtom : atoms ) {
            // only process if it is an oxygen atom
            if ( oxygenAtom.isOxygen() ) {
                List<Atomic> neighbors = getNeighbors( oxygenAtom );

                // for an alcohol subgroup (hydroxyl) we need:
                if ( neighbors.size() == 2 && // 2 neighbors
                     // 1st carbon, 2nd hydrogen
                     ( neighbors.get( 0 ).isCarbon() && neighbors.get( 1 ).isHydrogen() )
                     // OR 2nd carbon, 1st hydrogen
                     || ( neighbors.get( 0 ).isHydrogen() && neighbors.get( 1 ).isCarbon() ) ) {
                    alcoholCount++;

                    // pull off the hydrogen
                    structureWithoutAlcohols = structureWithoutAlcohols.getCopyWithAtomRemoved( neighbors.get( neighbors.get( 0 ).isHydrogen() ? 0 : 1 ) );

                    // and pull off the oxygen
                    structureWithoutAlcohols = structureWithoutAlcohols.getCopyWithAtomRemoved( oxygenAtom );
                }
            }
        }

        if ( alcoholCount == 0 ) {
            // no alcohols, use the regular formula
            return getGeneralFormula();
        }
        else if ( alcoholCount == 1 ) {
            // one alcohol, tag it at the end
            return structureWithoutAlcohols.getGeneralFormula() + "OH";
        }
        else {
            // more than one alcohol. use a count at the end
            return structureWithoutAlcohols.getGeneralFormula() + "(OH)" + alcoholCount;
        }
    }

    /**
     * Use the above general molecular formula, but return it with HTML subscripts
     *
     * @return Molecular formula with HTML subscripts
     */
    public String getGeneralFormulaFragment() {
        return ChemUtils.toSubscript( getGeneralFormula() );
    }

    /**
     * Use the above structural molecular formula, but return it with HTML subscripts
     *
     * @return Molecular formula with HTML subscripts
     */
    public String getStructuralFormulaFragment() {
        return ChemUtils.toSubscript( getStructuralFormula() );
    }

    private static String uglyHackAddSpaceBeforeSubscripts( String str ) {
        // this adds just a touch of space before the subscripts so it isn't so cramped
        return str.replace( "<sub", "<font size=\"0\"> </font><sub" );
    }

    private static double electronegativeSortValue( Atomic atom ) {
        return atom.getElectronegativity();
    }

    private static double organicSortValue( Atomic atom ) {
        if ( atom.isCarbon() ) {
            return 0;
        }
        if ( atom.isHydrogen() ) {
            return 1;
        }
        return alphabeticSortValue( atom );
    }

    private static double alphabeticSortValue( Atomic atom ) {
        int value = 1000 * ( (int) atom.getSymbol().charAt( 0 ) );
        if ( atom.getSymbol().length() > 1 ) {
            value += (int) atom.getSymbol().charAt( 1 );
        }
        return value;
    }

    public Set<Atomic> getAtoms() {
        return atoms;
    }

    public Set<Bond> getBonds() {
        return bonds;
    }

    /**
     * @param atom An atom
     * @return All neighboring atoms that are connected by bonds to the passed in atom
     */
    public List<Atomic> getNeighbors( Atomic atom ) {
        List<Atomic> ret = new LinkedList<Atomic>();
        for ( Bond bond : getBondsInvolving( atom ) ) {
            ret.add( bond.getOtherAtom( atom ) );
        }
        return ret;
    }

    public double getApproximateMolecularWeight() {
        // TODO: verify the accuracy of this
        double result = 0;
        for ( Atomic atom : atoms ) {
            result += atom.getAtomicWeight();
        }
        return result;
    }

    public boolean isValid() {
        return !hasWeirdHydrogenProperties() && !hasLoopsOrIsDisconnected();
    }

    public boolean hasWeirdHydrogenProperties() {
        // check for hydrogens that are bonded to more than 1 atom
        for ( Atomic atom : atoms ) {
            if ( atom.isHydrogen() ) {
                if ( getNeighbors( atom ).size() > 1 ) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasLoopsOrIsDisconnected() {
        Set<Atomic> visitedAtoms = new HashSet<Atomic>();
        Set<Atomic> dirtyAtoms = new HashSet<Atomic>();

        // pull one atom out. doesn't matter which one
        dirtyAtoms.add( atoms.iterator().next() );

        while ( !dirtyAtoms.isEmpty() ) {
            // while atoms are dirty, pull one out
            Atomic atom = dirtyAtoms.iterator().next();

            // for each neighbor, make "unvisited" atoms dirty and count "visited" atoms
            int visitedCount = 0;
            for ( Atomic otherAtom : getNeighbors( atom ) ) {
                if ( visitedAtoms.contains( otherAtom ) ) {
                    visitedCount += 1;
                }
                else {
                    dirtyAtoms.add( otherAtom );
                }
            }

            // if a dirty atom has two visited neighbors, it means there was a loop somewhere
            if ( visitedCount > 1 ) {
                return true;
            }

            // move our atom from dirty to visited
            dirtyAtoms.remove( atom );
            visitedAtoms.add( atom );
        }

        // since it has no loops, now we check to see if we reached all atoms. if not, the molecule must not be connected
        return visitedAtoms.size() != atoms.size();
    }

    /**
     * Combines molecules together by bonding their atoms A and B
     *
     * @param molA Molecule A
     * @param molB Molecule B
     * @param a    Atom A
     * @param b    Atom B
     * @return A completely new molecule with all atoms in A and B, where atom A is joined to atom B
     */
    public static MoleculeStructure getCombinedMoleculeFromBond( MoleculeStructure molA, MoleculeStructure molB, Atomic a, Atomic b ) {
        MoleculeStructure ret = new MoleculeStructure();
        for ( Atomic atom : molA.getAtoms() ) {
            ret.addAtom( atom );
        }
        for ( Atomic atom : molB.getAtoms() ) {
            ret.addAtom( atom );
        }
        for ( Bond bond : molA.getBonds() ) {
            ret.addBond( bond );
        }
        for ( Bond bond : molB.getBonds() ) {
            ret.addBond( bond );
        }
        ret.addBond( a, b );
        return ret;
    }

    /**
     * Split a bond in a molecule, and return the remaining molecule structure(s)
     *
     * @param structure The molecule
     * @param bond      The bond to break
     * @return A list of remaining structures
     */
    public static List<MoleculeStructure> getMoleculesFromBrokenBond( MoleculeStructure structure, final Bond bond ) {
        // TODO: in the future when we have loops, we can't assume that this will break a molecule into two separate molecules!
        final MoleculeStructure molA = new MoleculeStructure();
        final MoleculeStructure molB = new MoleculeStructure();

        /*---------------------------------------------------------------------------*
        * separate out which atoms belong in which remaining molecule
        * TODO: separate out code into a "get connected submolecule" code?
        *----------------------------------------------------------------------------*/

        Set<Atomic> atomsInA = new HashSet<Atomic>() {{
            add( bond.a );
        }};

        // atoms left after removing atoms
        Set<Atomic> remainingAtoms = new HashSet<Atomic>( structure.getAtoms() );
        remainingAtoms.remove( bond.a );
        Set<Atomic> dirtyAtoms = new HashSet<Atomic>() {{
            add( bond.a );
        }};
        while ( !dirtyAtoms.isEmpty() ) {
            Atomic atom = dirtyAtoms.iterator().next();
            dirtyAtoms.remove( atom );

            // for all neighbors that don't use our "bond"
            for ( Bond otherBond : structure.bonds ) {
                if ( otherBond != bond && otherBond.contains( atom ) ) {
                    Atomic neighbor = otherBond.getOtherAtom( atom );

                    // pick out our neighbor, mark it as in "A", and mark it as dirty so we can process its neighbors
                    if ( remainingAtoms.contains( neighbor ) ) {
                        remainingAtoms.remove( neighbor );
                        dirtyAtoms.add( neighbor );
                        atomsInA.add( neighbor );
                    }
                }
            }
        }

        /*---------------------------------------------------------------------------*
        * construct our two molecules
        *----------------------------------------------------------------------------*/

        for ( Atomic atom : structure.getAtoms() ) {
            if ( atomsInA.contains( atom ) ) {
                molA.addAtom( atom );
            }
            else {
                molB.addAtom( atom );
            }
        }

        for ( Bond otherBond : structure.getBonds() ) {
            if ( otherBond != bond ) {
                if ( atomsInA.contains( otherBond.a ) ) {
                    assert atomsInA.contains( otherBond.b );
                    molA.addBond( otherBond );
                }
                else {
                    molB.addBond( otherBond );
                }
            }
        }

        System.out.println( "splitting " + structure.toSerial() + " into:" );
        System.out.println( molA.toSerial() );
        System.out.println( molB.toSerial() );

        // return our two molecules
        return new LinkedList<MoleculeStructure>() {{
            add( molA );
            add( molB );
        }};
    }

    /**
     * @return A serialized form of this structure. It is |-separated tokens, with the format:
     *         atom quantity
     *         bond quantity
     *         for each atom, it's symbol
     *         for each bond, two zero-indexed indices of atoms above
     */
    public String toSerial() {
        String ret = atoms.size() + "|" + bonds.size();
        List<Atomic> atoms = new LinkedList<Atomic>( getAtoms() );
        for ( Atomic atom : atoms ) {
            ret += "|" + atom.getSymbol();
        }

        for ( Bond bond : bonds ) {
            int a = atoms.indexOf( bond.a );
            int b = atoms.indexOf( bond.b );
            ret += "|" + a + "|" + b;
        }

        return ret;
    }

    /**
     * Reads in the serialized form produced above
     *
     * @param str Serialized form of a structure
     * @return Molecule structure
     */
    public static MoleculeStructure fromSerial( String str ) {
        MoleculeStructure structure = new MoleculeStructure();
        StringTokenizer t = new StringTokenizer( str, "|" );
        int atomCount = Integer.parseInt( t.nextToken() );
        int bondCount = Integer.parseInt( t.nextToken() );
        Atomic[] atoms = new Atomic[atomCount];

        for ( int i = 0; i < atomCount; i++ ) {
            atoms[i] = AtomModel.createAtomBySymbol( t.nextToken() );
            structure.addAtom( atoms[i] );
        }

        for ( int i = 0; i < bondCount; i++ ) {
            structure.addBond( atoms[Integer.parseInt( t.nextToken() )], atoms[Integer.parseInt( t.nextToken() )] );
        }

        return structure;
    }

    public String getDebuggingDump() {
        String str = "Molecule\n";
        for ( Atomic atom : atoms ) {
            str += "atom: " + atom.getSymbol() + " " + atom.hashCode() + "\n";
        }
        for ( Bond bond : bonds ) {
            str += "bond: " + bond.a.hashCode() + " - " + bond.b.hashCode() + "\n";
        }
        return str;
    }

    private boolean containsAtomOfType( Atomic referenceAtom ) {
        for ( Atomic atom : atoms ) {
            if ( atom.isSameElement( referenceAtom ) ) {
                return true;
            }
        }
        return false;
    }

    public Bond getBond( Atomic a, Atomic b ) {
        for ( Bond bond : bonds ) {
            if ( bond.contains( a ) && bond.contains( b ) ) {
                return bond;
            }
        }
        throw new RuntimeException( "Could not find bond!" );
    }

    public AtomHistogram getHistogram() {
        return new AtomHistogram( this );
    }

    public static class Bond {
        public Atomic a;
        public Atomic b;

        public Bond( Atomic a, Atomic b ) {
            this.a = a;
            this.b = b;
            assert ( a != b );
        }

        @Override
        public int hashCode() {
            return a.hashCode() + b.hashCode();
        }

        @Override
        public boolean equals( Object ob ) {
            if ( ob instanceof Bond/*, James Bond*/ ) {
                Bond other = (Bond) ob;
                return ( this.a == other.a && this.b == other.b ) || ( this.a == other.b && this.b == other.a );
            }
            else {
                return false;
            }
        }

        public boolean contains( Atomic atom ) {
            return atom == a || atom == b;
        }

        public Atomic getOtherAtom( Atomic atom ) {
            assert ( contains( atom ) );
            return ( a == atom ? b : a );
        }
    }

    public int getMoleculeId() {
        return moleculeId;
    }

    public MoleculeStructure getCopy() {
        MoleculeStructure ret = new MoleculeStructure();
        for ( Atomic atom : atoms ) {
            ret.addAtom( atom );
        }
        for ( Bond bond : bonds ) {
            ret.addBond( bond );
        }
        return ret;
    }

    public MoleculeStructure getCopyWithAtomRemoved( Atomic atomToRemove ) {
        MoleculeStructure ret = new MoleculeStructure();
        for ( Atomic atom : atoms ) {
            if ( atom != atomToRemove ) {
                ret.addAtom( atom );
            }
        }
        for ( Bond bond : bonds ) {
            if ( !bond.contains( atomToRemove ) ) {
                ret.addBond( bond );
            }
        }
        return ret;
    }

    /**
     * Check whether the molecular structure is equivalent to another structure. Not terribly efficient, and will
     * probably fail for cyclic graphs.
     * <p/>
     *
     * @param other Another molecular structure
     * @return True, if there is an isomorphism between the two molecular structures
     */
    public boolean isEquivalent( MoleculeStructure other ) {
        if ( this == other ) {
            // same instance
            return true;
        }
        if ( this.getAtoms().size() != other.getAtoms().size() ) {
            // must have same number of atoms
            return false;
        }
        if ( !getHistogram().equals( other.getHistogram() ) ) {
            // different molecular formula
            return false;
        }
        Set<Atomic> myVisited = new HashSet<Atomic>();
        Set<Atomic> otherVisited = new HashSet<Atomic>();
        Atomic firstAtom = atoms.iterator().next(); // grab the 1st atom
        for ( Atomic otherAtom : other.getAtoms() ) {
            if ( checkEquivalency( other, myVisited, otherVisited, firstAtom, otherAtom ) ) {
                // we found an isomorphism with firstAtom => otherAtom
                return true;
            }
        }
        return false;
    }

    /**
     * @param other Other molecule
     * @return Whether the two molecules are isomers (have the same molecular formula)
     */
    public boolean isIsomer( MoleculeStructure other ) {
        return getHillSystemFormulaFragment().equals( other.getHillSystemFormulaFragment() );
    }

    /**
     * @param atom         An atom
     * @param exclusionSet A set of atoms that should not be in the return value
     * @return All neighboring atoms that are connected by bonds to the passed in atom AND aren't in the exclusionSet
     */
    public List<Atomic> getNeighborsNotInSet( Atomic atom, Set<Atomic> exclusionSet ) {
        List<Atomic> ret = new LinkedList<Atomic>();
        for ( Atomic otherAtom : getNeighbors( atom ) ) {
            if ( !exclusionSet.contains( otherAtom ) ) {
                ret.add( otherAtom );
            }
        }
        return ret;
    }

    private boolean checkEquivalency( MoleculeStructure other, Set<Atomic> myVisited, Set<Atomic> otherVisited, Atomic myAtom, Atomic otherAtom ) {
        if ( !myAtom.isSameElement( otherAtom ) ) {
            // if the atoms are of different types, bail. subtrees can't possibly be equivalent
            return false;
        }
        List<Atomic> myUnvisitedNeighbors = getNeighborsNotInSet( myAtom, myVisited );
        List<Atomic> otherUnvisitedNeighbors = other.getNeighborsNotInSet( otherAtom, otherVisited );
        if ( myUnvisitedNeighbors.size() != otherUnvisitedNeighbors.size() ) {
            return false;
        }
        if ( myUnvisitedNeighbors.isEmpty() ) {
            // no more unmatched atoms
            return true;
        }
        int size = myUnvisitedNeighbors.size();

        // for now, add visiting atoms to the visited set. we NEED to revert this before returning!
        myVisited.add( myAtom );
        otherVisited.add( otherAtom );

        /*
          equivalency matrix. each entry is basically whether the subtree in the direction of the "my" atom is
          equivalent to the subtree in the direction of the "other" atom, for all possible my and other atoms
        */
        boolean[][] equivalences = new boolean[size][size];

        // keep track of available indices for the following matrix equivalency check
        List<Integer> availableIndices = new LinkedList<Integer>();

        // for the love of god, this matrix is NOT symmetric. It computes whether each tree branch for A is equivalent to each tree branch for B
        for ( int myIndex = 0; myIndex < size; myIndex++ ) {
            availableIndices.add( myIndex );
            for ( int otherIndex = 0; otherIndex < size; otherIndex++ ) {
                equivalences[myIndex][otherIndex] = checkEquivalency( other, myVisited, otherVisited, myUnvisitedNeighbors.get( myIndex ), otherUnvisitedNeighbors.get( otherIndex ) );
            }
        }

        // remove the atoms from the visited sets, to hold our contract
        myVisited.remove( myAtom );
        otherVisited.remove( otherAtom );

        // return whether we can find a successful permutation matching from our equivalency matrix
        return checkEquivalencyMatrix( equivalences, 0, availableIndices );
    }

    /**
     * Given a matrix of equivalencies, can we find a permutation of the "other" atoms that are equivalent to
     * their respective "my" atoms?
     *
     * @param equivalences          Equivalence Matrix
     * @param myIndex               Index for the row (index into our atoms). calls with myIndex + 1 to children
     * @param otherRemainingIndices Remaining available "other" indices
     * @return Whether a successful matching permutation was found
     */
    public static boolean checkEquivalencyMatrix( boolean[][] equivalences, int myIndex, List<Integer> otherRemainingIndices ) {
        // should be inefficient, but not too bad (computational complexity is not optimal)
        for ( Integer otherIndex : new ArrayList<Integer>( otherRemainingIndices ) ) { // loop over all remaining others
            if ( equivalences[myIndex][otherIndex] ) { // only follow path if it is true (equivalent)

                // remove the index from consideration for checking the following submatrix
                otherRemainingIndices.remove( otherIndex );

                boolean success = ( myIndex == equivalences.length - 1 ) // there are no more permutations to check
                                  || checkEquivalencyMatrix( equivalences, myIndex + 1, otherRemainingIndices ); // or we can find a good combination of the remaining indices

                // add it back in so the calling function's contract for otherRemainingIndices is satisfied
                otherRemainingIndices.add( otherIndex );

                if ( success ) {
                    return true;
                }
            }
        }
        return false;
    }

}
