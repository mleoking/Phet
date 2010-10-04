/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.Dimension;

/**
 * Interface for all strategies used to determine the size of the grid
 * used to represent plate charge.
 */
public interface IPlateChargeGridSizeStrategy {
    
    public Dimension getGridSize( int numberOfObjects, double width, double height );
    
    /**
     * This factory determines the strategy used throughout the application.
     */
    public static class GridSizeStrategyFactory {
        public static IPlateChargeGridSizeStrategy createStrategy() {
            return new CCKStrategyWithRounding();
        }
    }
    
    /**
     * Strategy borrowed from CCK's CapacitorNode.
     * When the plate's aspect ration gets large, this strategy creates grid sizes 
     * where one of the dimensions is zero (eg, 8x0, 0x14).
     */
    public static class CCKStrategy implements IPlateChargeGridSizeStrategy {
        
        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            double alpha = Math.sqrt( numberOfObjects / width / height );
            // casting here may result in some charges being thrown out, but that's OK
            int columns = (int)( width * alpha );
            int rows = (int)( height * alpha );
            return new Dimension( columns, rows );
        }
    }
    
    /**
     * Workaround for one of the known issues with CCKGridSizeStrategy.
     * Ensures that we don't have a grid size where exactly one of the dimensions is zero.
     * This introduces a new problem: If numberOfCharges is kept constant, a plate with smaller
     * area but larger aspect ratio will display more charges.
     * For example, if charges=7, a 5x200mm plate will display 7 charges,
     * while a 200x200mm plate will only display 4 charges.
     */
    public static class ModifiedCCKStrategy extends CCKStrategy {
        
        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            Dimension gridSize = super.getGridSize( numberOfObjects, width, height );
            if ( gridSize.width == 0 && gridSize.height != 0 ) {
                gridSize.setSize( 1, numberOfObjects );
            }
            else if ( gridSize.width != 0 && gridSize.height == 0 ) {
                gridSize.setSize( numberOfObjects, 1 );
            }
            return gridSize;
        }
    }

    /**
     * Strategy developed by Sam Reid, here's how he described it:
     * The main change is to use rounding instead of clamping to get the rows and columns.  
     * Also, for one row or column, it should be exact (similar to the intent of the ModifiedCCKGridSizeStrategy subclass).
     * It looks like it exhibits better (though understandably imperfect) behavior in the problem cases.  
     * Also, as opposed to the previous versions, the visible number of objects can exceed the specified numberOfObjects.
     * This may be the best we can do if we are showing a rectangular grid of charges.  We could get the count exactly 
     * right if we show some (or one) of the columns having different numbers of charges than the others, but then 
     * it may look nonuniform (and would require more extensive changes to the sim).
     *
     * @author Sam Reid
     */
    public static class CCKStrategyWithRounding implements IPlateChargeGridSizeStrategy {
        
        private static final boolean DEBUG_OUTPUT_ENABLED = false;

        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            double alpha = Math.sqrt( numberOfObjects / width / height );
            // casting here may result in some charges being thrown out, but that's OK
            int columns = (int) ( Math.round( width * alpha ) );
            int oldrows = (int) ( Math.round( height * alpha ) );
            int rows = (int) Math.round( numberOfObjects / (double) columns );
            if ( oldrows != rows ) {
                int err1 = Math.abs( numberOfObjects - rows * columns );
                int err2 = Math.abs( numberOfObjects - oldrows * columns );
                if ( err2 < err1 ) {
                    rows = oldrows; // choose whichever had the better behavior
                }
                if ( DEBUG_OUTPUT_ENABLED ) {
                    boolean err1Wins = err1 < err2;
                    System.out.println( "CCKGridSizeStrategyWithRounding.getGridSize err1Wins=" + err1Wins + " rows=" + rows + " oldrows=" + oldrows + " err1=" + err1 + " err2=" + err2 );
                }
            }
            if ( columns == 0 ) {
                columns = 1;
                rows = numberOfObjects;
            }
            else if ( rows <= 1 ) {
                rows = 1;
                columns = numberOfObjects;
            }
            return new Dimension( columns, rows );
        }
    }
}