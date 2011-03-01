// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Base class for all chemical equations.
 * A chemical equation has 2 sets of terms, reactants and products.
 * During the chemical reaction represented by the equation, reactants are transformed into products.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Equation {

    public final String name;
    public final EquationTerm[] reactants, products;
    public final Property<Boolean> balancedProperty;
    public final Property<Boolean> balancedWithLowestCoefficientsProperty;

    public Equation( final EquationTerm[] reactants, final EquationTerm[] products ) {
        this( createName( reactants, products ), reactants, products );
    }

    /**
     * Constructor.
     * @param name user-visible name for the equation
     * @param reactants
     * @param products
     */
    public Equation( String name, final EquationTerm[] reactants, final EquationTerm[] products ) {

        // check arguments
        if ( !( ( reactants.length > 1 && products.length > 0 ) || ( reactants.length > 0 && products.length > 1 ) ) ) {
            throw new IllegalArgumentException( "equation requires at least 2 reactants and 1 product, or 1 reactant and 2 products" );
        }

        this.name = name;
        this.reactants = reactants;
        this.products = products;
        this.balancedProperty = new Property<Boolean>( false );
        this.balancedWithLowestCoefficientsProperty = new Property<Boolean>( false );

        // equation is balanced if all terms are balanced.
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                updateBalancedProperties();
            }
        };
        for ( EquationTerm term : reactants ) {
            term.getActualCoefficientProperty().addObserver( o );
        }
        for ( EquationTerm term : products ) {
            term.getActualCoefficientProperty().addObserver( o );
        }
    }

    public void reset() {
        for ( EquationTerm term : reactants ) {
            term.reset();
        }
        for ( EquationTerm term : products ) {
            term.reset();
        }
        // balanced properties are automatically reset when terms are reset
    }

    /*
     * An equation is balanced if all of its terms have a coefficient that is the
     * same integer multiple of the term's balanced coefficient.  If the integer
     * multiple is 1, then the term is balanced with lowest possible coefficients.
     */
    private void updateBalancedProperties() {

        // Get integer multiplier from the first reactant term.
        final int multiplier = (int)( reactants[0].getActualCoefficient() / reactants[0].getBalancedCoefficient() );

        boolean balanced = ( multiplier > 0 );

        // Check each term to see if the actual coefficient is the same integer multiple of the balanced coefficient.
        for ( EquationTerm reactant : reactants ) {
            balanced = balanced && ( reactant.getActualCoefficient() == multiplier * reactant.getBalancedCoefficient() );
        }
        for ( EquationTerm product : products ) {
            balanced = balanced && ( product.getActualCoefficient() == multiplier * product.getBalancedCoefficient() );
        }

        balancedWithLowestCoefficientsProperty.setValue( balanced && ( multiplier == 1 ) ); // set the more specific property first
        balancedProperty.setValue( balanced );
    }

    public String getName() {
        return name;
    }

    /**
     * Gets the reactants, the terms on the left side of the equation.
     * @return
     */
    public EquationTerm[] getReactants() {
        return reactants;
    }

    /**
     * Gets the products, the terms on the left side of the equation.
     * @return
     */
    public EquationTerm[] getProducts() {
        return products;
    }

    public boolean isBalanced() {
        return balancedProperty.getValue();
    }

    public Property<Boolean> getBalancedProperty() {
        return balancedProperty;
    }

    public boolean isBalancedWithLowestCoefficients() {
        return balancedWithLowestCoefficientsProperty.getValue();
    }

    public Property<Boolean> getBalancedWithLowestCoefficientsProperty() {
        return balancedWithLowestCoefficientsProperty;
    }

    public void balance() {
        for ( EquationTerm term : reactants ) {
            term.setActualCoefficient( term.getBalancedCoefficient() );
        }
        for ( EquationTerm term : products ) {
            term.setActualCoefficient( term.getBalancedCoefficient() );
        }
    }

    public boolean isAllCoefficientsZero() {
        boolean allZero = true;
        for ( EquationTerm term : reactants ) {
            if ( term.getActualCoefficient() > 0 ) {
                allZero = false;
                break;
            }
        }
        if ( allZero ) {
            for ( EquationTerm term : products ) {
                if ( term.getActualCoefficient() > 0 ) {
                    allZero = false;
                }
            }
        }
        return allZero;
    }

    /**
     * Returns a count of each type of atom.
     * <p>
     * The order of atoms will be the same order that they are encountered in the reactant terms.
     * For example, if the left-hand side of the equation is CH4 + O2, then the order of atoms
     * will be [C,H,O].
     */
    public ArrayList<AtomCount> getAtomCounts() {
        ArrayList<AtomCount> atomCounts = new ArrayList<AtomCount>();
        setAtomCounts( atomCounts, reactants, true /* isReactants */ );
        setAtomCounts( atomCounts, products, false /* isReactants */ );
        return atomCounts;
    }

    /*
     * Sets atom counts for on collection of terms (reactants or products).
     * This is a brute force algorithm, but our number of terms is always small,
     * and this is easy to implement and understand.
     *
     * @param atomCounts
     * @param terms
     * @param isReactants true if the terms are the reactants, false if they are the products
     */
    private static void setAtomCounts(  ArrayList<AtomCount> atomCounts, EquationTerm[] terms, boolean isReactants ) {
        for ( EquationTerm term : terms ) {
            for ( Atom atom : term.getMolecule().getAtoms() ) {
                boolean found = false;
                for ( AtomCount count : atomCounts ) {
                    // add to an existing count
                    if ( count.getAtom().getClass().equals( atom.getClass() ) ) {
                        if ( isReactants ) {
                            count.setReactantsCount( count.getReactantsCount() + term.getActualCoefficient() );
                        }
                        else {
                            count.setProductsCount( count.getProductsCount() + term.getActualCoefficient() );
                        }
                        found = true;
                        break;
                    }
                }
                // if no existing count was found, create one.
                if ( !found ) {
                    if ( isReactants ) {
                        atomCounts.add( new AtomCount( atom, term.getActualCoefficient(), 0 ) );
                    }
                    else {
                        atomCounts.add( new AtomCount( atom, 0, term.getActualCoefficient() ) );
                    }
                }
            }
        }
    }

    /**
     * Convenience method for adding an observer to all coefficients.
     */
    public void addCoefficientsObserver( SimpleObserver observer ) {
        for ( EquationTerm term : reactants ) {
            term.getActualCoefficientProperty().addObserver( observer );
        }
        for ( EquationTerm term : products ) {
            term.getActualCoefficientProperty().addObserver( observer );
        }
    }

    /**
     * Convenience method for removing an observer from all coefficients.
     */
    public void removeCoefficientsObserver( SimpleObserver observer ) {
        for ( EquationTerm term : reactants ) {
            term.getActualCoefficientProperty().removeObserver( observer );
        }
        for ( EquationTerm term : products ) {
            term.getActualCoefficientProperty().removeObserver( observer );
        }
    }

    /*
     * Creates an HTML string that shows the equation formula.
     * Used for equations that don't have a more general name (eg, "make water").
     */
    private static String createName( EquationTerm[] reactants, final EquationTerm[] products ) {
        StringBuffer b = new StringBuffer();
        for ( int i = 0; i < reactants.length; i++ ) {
            b.append( reactants[i].getMolecule().getSymbol() );
            if ( i <  reactants.length - 1 ) {
                b.append( " + " );
            }
        }
        b.append( " \u2192 " ); // right arrow
        for ( int i = 0; i < products.length; i++ ) {
            b.append( products[i].getMolecule().getSymbol() );
            if ( i <  products.length - 1 ) {
                b.append( " + " );
            }
        }
        return b.toString();
    }
}
