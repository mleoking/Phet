// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.view;

import java.awt.Dimension;

import edu.colorado.phet.balancingchemicalequations.model.Equation;
import edu.colorado.phet.balancingchemicalequations.model.EquationTerm;

/**
 * Encapsulates the strategy used horizontally aligning terms in an equation
 * with columns of molecules in the "box" view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class HorizontalAligner {

    private final Dimension boxSize;
    private final double boxSeparation;

    /**
     * Constructor.
     * @param boxWidth width of the box
     * @param boxSeparation horizontal separation between the left and right boxes
     */
    public HorizontalAligner( Dimension boxSize, double boxSeparation ) {
        this.boxSize = new Dimension( boxSize );
        this.boxSeparation = boxSeparation;
    }

    public Dimension getBoxSizeReference() {
        return boxSize;
    }

    public double getBoxSeparation() {
        return boxSeparation;
    }

    /**
     * Gets the x offset of the center between left and right hand sides.
     * @return
     */
    public double getCenterXOffset() {
        return boxSize.getWidth() + ( boxSeparation / 2 );
    }

    /**
     * Gets the offsets for an equation's reactant terms.
     * @param equation
     * @return
     */
    public double[] getReactantXOffsets( Equation equation ) {
        return getXOffsets( equation.getReactants(), 0 /* xAdjustment */ );
    }

    /**
     * Gets the offsets for an equation's product terms.
     * @param equation
     * @return
     */
    public double[] getProductXOffsets( Equation equation ) {
        return getXOffsets( equation.getProducts(), boxSize.getWidth() + boxSeparation /* xAdjustment */ );
    }

    /*
     * Gets the x offsets for a set of terms.
     * The box is divided up into columns and terms are centered in the columns.
     */
    private double[] getXOffsets( EquationTerm[] terms, double xAdjustment ) {
        final int numberOfTerms = terms.length;
        final double columnWidth = boxSize.getWidth() / Math.max( 2, numberOfTerms );
        double x = xAdjustment + columnWidth / 2;
        double[] xOffsets = new double[ numberOfTerms ];
        for ( int i = 0; i < numberOfTerms; i++ ) {
            xOffsets[i] = x;
            x += columnWidth;
        }
        return xOffsets;
    }
}
