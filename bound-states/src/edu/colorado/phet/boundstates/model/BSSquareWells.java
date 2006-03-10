/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.boundstates.BSConstants;
import edu.colorado.phet.boundstates.enum.WellType;


/**
 * BSSquareWell is the model of a potential composed of one or more Square wells.
 * <p>
 * Our model supports these parameters:
 * <ul>
 * <li>number of wells
 * <li>spacing
 * <li>offset
 * <li>width
 * <li>depth
 * </ul>
 * Offset, width, depth and spacing are identical for each well.
 * Spacing is irrelevant if the number of wells is 1.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSSquareWells extends BSAbstractPotential {
   
    private double _width;
    private double _depth;

    public BSSquareWells( int numberOfWells ) {
        this( numberOfWells, 
                BSConstants.DEFAULT_SQUARE_SPACING, 
                BSConstants.DEFAULT_SQUARE_WIDTH, 
                BSConstants.DEFAULT_SQUARE_DEPTH, 
                BSConstants.DEFAULT_SQUARE_OFFSET, 
                BSConstants.DEFAULT_WELL_CENTER );
    }
    
    public BSSquareWells( int numberOfWells, double spacing, double width, double depth, double offset, double center ) {
        super( numberOfWells, spacing, offset, center );
        setWidth( width );
        setDepth( depth );
        setCenter( 1 );
    }
    
    public double getWidth() {
        return _width;
    }

    public void setWidth( double width ) {
        if ( width <= 0 ) {
            throw new IllegalArgumentException( "invalid width: " + width );
        }
        _width = width;
        notifyObservers();
    }

    public double getDepth() {
        return _depth;
    }

    public void setDepth( double depth ) {
        if ( depth > 0 ) {
            throw new IllegalArgumentException( "invalid depth: " + depth );
        }
        _depth = depth;
        notifyObservers();
    }
    
    public WellType getWellType() {
        return WellType.SQUARE;
    }
    
    public int getStartingIndex() {
        return 1;
    }
    
    //XXX Hack -- creates 10 eigenstates between offset and depth
    public BSEigenstate[] getEigenstates() {
        final int numberOfEigenstates = 10;
        BSEigenstate[] eigenstates = new BSEigenstate[ numberOfEigenstates ];
        final double maxEnergy = getOffset();
        final double minEnergy = getOffset() + getDepth();
        final double deltaEnergy = ( maxEnergy - minEnergy ) / numberOfEigenstates;
        for ( int i = 0; i < eigenstates.length; i++ ) {
            eigenstates[i] = new BSEigenstate( minEnergy + ( i * deltaEnergy ) );
        }
        return eigenstates;
    }
    
    public double solve( double x ) {
        double value = 0;
        
        final int n = getNumberOfWells();
        final double offset = getOffset();
        final double c = getCenter();
        final double s = getSpacing();
        final double w = getWidth();
        final double d = getDepth();
        
        for ( int i = 1; i <= n; i++ ) {
            final double xi = s * ( i - ( ( n + 1  ) / 2.0 ) );
            if ( ( (x-c) >= xi - ( w / 2) ) && ( (x-c) <= xi + ( w / 2 ) ) ) {
                value = offset + d;
                break;
            }
        }
        
        return offset + value;
    }
}
