/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.util;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * GridPanel is a JPanel that arranges Components in a grid.
 * It uses GridBagLayout and GridBagConstraints internally, but hides their interfaces.
 * <p>
 * If you hate GridBagLayout, this is an alternative.
 * And it's less awkward and more object-oriented than EasyGridBagLayout, which was another alternative.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GridPanel extends JPanel {
    
    /** @see setAnchor */
    public static enum Anchor { CENTER, NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST };
    
    /** @see setFill */
    public static enum Fill { HORIZONTAL, VERTICAL, BOTH, NONE };
    
    // Define these constants to fully hide GridBagConstraints.
    public static final int RELATIVE = GridBagConstraints.RELATIVE;
    public static final int REMAINDER = GridBagConstraints.REMAINDER;
    
    // maps between enums and GridBagConstraint constants
    private static final ConstraintMap CONSTRAINT_MAP = new ConstraintMap();
    
    private final GridBagConstraints constraints;
    
    /**
     * Constructor that specifies no layout properties, you get default settings.
     * You can use setters to change the defaults, or specify properties when a component is added.
     */
    public GridPanel() {
        this( new GridBagConstraints() ); // defaults are identical to GridBagConstraints
    }
    
    /*
     * This constructor should remain private, so we don't expose GridBagConstraints.
     * It's provides for internal use, so that we can set default to be identical to GridBagConstraints.
     */
    private GridPanel( GridBagConstraints c ) {
        this( c.gridx, c.gridy, c.gridwidth, c.gridheight, c.weightx, c.weighty, CONSTRAINT_MAP.toAnchor( c.anchor ), CONSTRAINT_MAP.toFill( c.fill ), c.insets, c.ipadx, c.ipady );
    }
    
    /**
     * Constructor that lets you specify default values for all layout properties.
     * See setters for description of properties.
     */
    public GridPanel( int gridX, int gridY, int gridWidth, int gridHeight, double weightX, double weightY, Anchor anchor, Fill fill, Insets insets, int internalPaddingX, int internalPaddingY ) {
        super( new GridBagLayout() );
        
        this.constraints = new GridBagConstraints();
        
        setGridX( gridX );
        setGridY( gridY );
        setGridWidth( gridWidth );
        setGridHeight( gridHeight );
        setWeightX( weightX );
        setWeightY( weightY );
        setAnchor( anchor );
        setFill( fill );
        setInsets( insets );
        setInternalPaddingX( internalPaddingX );
        setInternalPaddingY( internalPaddingY );
    }
    
    @Override
    /**
     * A GridPanel must have a GridLayout layout manager, or what's the point?
     */
    public void setLayout( LayoutManager manager ) {
        if ( ! ( manager instanceof GridBagLayout ) ) {
        throw new UnsupportedOperationException( "layout manager must be a GridBagLayout" );
        }
        else {
            super.setLayout( manager );
        }
    }
    
    /**
     * Puts a component in a specific grid cell, and specifies a complete set of layout properties.
     * See setters for description of properties.
     */
    public void addComponent( Component component, int gridX, int gridY, int gridWidth, int gridHeight, double weightX, double weightY, 
            Anchor anchor, Fill fill, Insets insets, int internalPaddingX, int internalPaddingY ) {
        
        // save constraints
        GridBagConstraints savedConstraints = copy( constraints );
        
        // set temporary constraints
        constraints.gridx = gridX;
        constraints.gridy = gridY;
        constraints.gridwidth = gridWidth;
        constraints.gridheight = gridHeight;
        constraints.anchor = CONSTRAINT_MAP.toInt( anchor );
        constraints.fill = CONSTRAINT_MAP.toInt( fill );
        constraints.insets = insets;
        constraints.ipadx = internalPaddingX;
        constraints.ipady = internalPaddingY;
        constraints.weightx = weightX;
        constraints.weighty = weightY;

        // put the component in a grid cell
        super.add( component, constraints );
        
        // restore constraints
        setConstraints( savedConstraints, constraints );
    }
    
    /**
     * Add a component using the default layout properties.
     */
    public Component add( Component component ) {
        addComponent( component, getGridX(), getGridY(), getGridWidth(), getGridHeight(), getWeightX(), getWeightY(), getAnchor(), getFill(), getInsetsReference(), getInternalPaddingX(), getInternalPaddingY() );
        return component;
    }
    
    // convenience method
    public void add( Component component, int row, int column ) {
        addComponent( component, column, row, getGridWidth(), getGridHeight(), getWeightX(), getWeightY(), getAnchor(), getFill(), getInsetsReference(), getInternalPaddingX(), getInternalPaddingY() );
    }
    
    // convenience method
    public void addComponent( Component component, int row, int column, int widthInColumns, int heightInRows ) {
        addComponent( component, column, row, widthInColumns, heightInRows, getWeightX(), getWeightY(), getAnchor(), getFill(), getInsetsReference(), getInternalPaddingX(), getInternalPaddingY() );
    }
    
    // convenience method
    public void add( Component component, int row, int column, Anchor anchor ) {
        addComponent( component, column, row, getGridWidth(), getGridHeight(), getWeightX(), getWeightY(), anchor, getFill(), getInsetsReference(), getInternalPaddingX(), getInternalPaddingY() );
    }
    
    // convenience method
    public void add( Component component, int row, int column, Fill fill ) {
        addComponent( component, column, row, getGridWidth(), getGridHeight(), getWeightX(), getWeightY(), getAnchor(), fill, getInsetsReference(), getInternalPaddingX(), getInternalPaddingY() );
    }
    
    /*
     * GridBagConstraints lacks a copy constructor, this method fills that void.
     * Used for saving state.
     */
    private static GridBagConstraints copy( GridBagConstraints c ) {
        return new GridBagConstraints( c.gridx, c.gridy, c.gridwidth, c.gridheight, c.weightx, c.weighty, c.anchor, c.fill, c.insets, c.ipadx, c.ipady );
    }
    
    /*
     * Copies constraints from one object to another.
     * Used to restore state.
     */
    private static void setConstraints( GridBagConstraints source, GridBagConstraints destination ) {
        destination.gridx = source.gridx;
        destination.gridy = source.gridy;
        destination.gridwidth = source.gridwidth;
        destination.gridheight = source.gridheight;
        destination.weightx = source.weightx;
        destination.weighty = source.weighty;
        destination.anchor = source.anchor;
        destination.fill = source.fill;
        destination.insets = source.insets;
        destination.ipadx = source.ipadx;
        destination.ipady = source.ipady;
    }
    
    /**
     * Specifies the column in which to place an added component.
     * Use RELATIVE to specifies that the component is to be placed 
     * immediately following the previously-added component.
     * @param gridX
     */
    public void setGridX( int gridX ) {
        constraints.gridx = gridX;
    }
    
    public int getGridX() {
        return constraints.gridx;
    }
    
    /**
     * Specifies the row in which to place an added component.
     * Use RELATIVE to specifies that the component is to be placed 
     * immediately following the previously-added component. 
     * @param gridY
     */
    public void setGridY( int gridY ) {
        constraints.gridy = gridY;
    }
    
    public int getGridY() {
        return constraints.gridy;
    }
    
    /**
     * Specifies how many columns a component will fill horizontally.
     * Use REMAINDER to fill to the end of the row.
     * Use RELATIVE to fill to the next occupied cell in the row.
     * @param width
     */
    public void setGridWidth( int width ) {
        constraints.gridwidth = width;
    }
    
    public int getGridWidth() {
        return constraints.gridwidth;
    }

    /**
     * Specifies how many rows a component will fill vertically.
     * Use REMAINDER to fill to the end of the column.
     * Use RELATIVE to fill to the next occupied cell in the column.
     * @param width
     */
    public void setGridHeight( int height ) {
        constraints.gridheight = height;
    }

    public int getGridHeight() {
        return constraints.gridheight;
    }
    
    /**
     * Specifies how to distribute extra horizontal space.
     * Extra space is distributed to each column in proportion to its weight, 
     * as compared to the weight of other columns.
     * A column that has a weight of zero receives no extra space.
     * @param weightX
     */
    public void setWeightX( double weightX ) {
        constraints.weightx = weightX;
    }
    
    public double getWeightX() { 
        return constraints.weightx;
    }
    
    /**
     * Specifies how to distribute extra vertical space. 
     * Extra space is distributed to each row in proportion to its weight,
     * as compared to the weight of other rows. 
     * A row that has a weight of zero receives no extra space.
     * @param weightY
     */
    public void setWeightY( double weightY ) {
        constraints.weighty = weightY;
    }
    
    public double getWeightY() { 
        return constraints.weighty;
    }

    /**
     * If a component is smaller than its grid cell, this property determines where
     * to place the component within the cell. In the language of GridBagConstraint,
     * these values specify "orientation relative" positions that interpreted relative
     * to the container's component orientation.
     * @param anchor
     */
    public void setAnchor( Anchor anchor ) {
        constraints.anchor = CONSTRAINT_MAP.toInt( anchor );
    }

    public Anchor getAnchor() {
        return CONSTRAINT_MAP.toAnchor( constraints.anchor );
    }

    /**
     * If a component is smaller than its grid cell, this property determines whether to resize the component, 
     * and if so, which dimensions of the grid cell should be considered when doing the resizing. 
     * @param fill
     */
    public void setFill( Fill fill ) {
        constraints.fill = CONSTRAINT_MAP.toInt( fill );
    }

    public Fill getFill() {
        return CONSTRAINT_MAP.toFill( constraints.fill );
    }
    
    /**
     * Specifies the external padding of the component, the  minimum amount of space between 
     * the component and the edges of the cells that it occupies. 
     * Because Insets are mutable, the specified Insets are copies.
     */
    public void setInsets( Insets insets ) {
        constraints.insets = new Insets( insets.top, insets.left, insets.bottom, insets.right );
    }

    /**
     * Gets the insets.
     * Because Insets are mutable, a copy is returned.
     */
    public Insets getInsets() {
        return new Insets( constraints.insets.top, constraints.insets.left, constraints.insets.bottom, constraints.insets.right );
    }
    
    private Insets getInsetsReference() {
        return constraints.insets;
    }
    
    /**
     * Specifies how much space to add to the minimum width of the component. 
     * The width of the component is at least its minimum width plus ipadx pixels.
     * @param ipadx
     */
    public void setInternalPaddingX( int ipadx ) {
        constraints.ipadx = ipadx;
    }

    public int getInternalPaddingX() {
        return constraints.ipadx;
    }
    
    /**
     * Specifies how much space to add to the minimum height of the component. 
     * The height of the component is at least its minimum height plus ipady pixels.
     * @param ipady
     */
    public void setInternalPaddingY( int ipady ) {
        constraints.ipady = ipady;
    }

    public int getInternalPaddingY() {
        return constraints.ipady;
    }

    /**
     * Sets the minimum width for a column.
     *
     * @param column the column
     * @param width minimum width, in pixels
     */
    public void setMinimumWidth( int column, int width ) {
        GridBagLayout layout = (GridBagLayout) getLayout();
        int[] widths = layout.columnWidths;
        if ( widths == null ) {
            widths = new int[column + 1];
        }
        else if ( widths.length < column + 1 ) {
            widths = new int[column + 1];
            System.arraycopy( layout.columnWidths, 0, widths, 0, layout.columnWidths.length );
        }
        widths[column] = width;
        layout.columnWidths = widths;
    }

    /**
     * Sets the minimum height for a row.
     *
     * @param row the row
     * @param height minimum height, in pixels
     */
    public void setMinimumHeight( int row, int height ) {
        GridBagLayout layout = (GridBagLayout) getLayout();
        int[] heights = layout.rowHeights;
        if ( heights == null ) {
            heights = new int[row + 1];
        }
        else if ( heights.length < row + 1 ) {
            heights = new int[row + 1];
            System.arraycopy( layout.rowHeights, 0, heights, 0, layout.rowHeights.length );
        }
        heights[row] = height;
        layout.rowHeights = heights;
    }

    /*
     * Maps between enums and ints for fill and anchor constraints.
     * GridBagConstraint uses ints for constraint values, and has no type checking.
     * We've introduced type checking using Fill and Anchor enums.
     */
    private static class ConstraintMap {
        
        private final HashMap<Anchor,Integer> anchorMap;
        private final HashMap<Fill,Integer> fillMap;
        
        public ConstraintMap() {
            
            anchorMap = new HashMap<Anchor, Integer>();
            anchorMap.put( Anchor.CENTER, new Integer( GridBagConstraints.CENTER ) );
            anchorMap.put( Anchor.NORTH, new Integer( GridBagConstraints.NORTH ) );
            anchorMap.put( Anchor.NORTHEAST, new Integer( GridBagConstraints.NORTHEAST ) );
            anchorMap.put( Anchor.EAST, new Integer( GridBagConstraints.EAST ) );
            anchorMap.put( Anchor.SOUTHEAST, new Integer( GridBagConstraints.SOUTHEAST ) );
            anchorMap.put( Anchor.SOUTH, new Integer( GridBagConstraints.SOUTH ) );
            anchorMap.put( Anchor.SOUTHWEST, new Integer( GridBagConstraints.SOUTHWEST ) );
            anchorMap.put( Anchor.WEST, new Integer( GridBagConstraints.WEST ) );
            anchorMap.put( Anchor.NORTHWEST, new Integer( GridBagConstraints.NORTHWEST ) );
            assert( anchorMap.size() == Anchor.values().length ); // is this map complete?
            
            fillMap = new HashMap<Fill, Integer>();
            fillMap.put( Fill.HORIZONTAL, new Integer( GridBagConstraints.HORIZONTAL ) );
            fillMap.put( Fill.VERTICAL, new Integer( GridBagConstraints.VERTICAL ) );
            fillMap.put( Fill.BOTH, new Integer( GridBagConstraints.BOTH ) );
            fillMap.put( Fill.NONE, new Integer( GridBagConstraints.NONE ) );
            assert( fillMap.size() == Fill.values().length );  // is this map complete?
        }
        
        /**
         * Converts a Fill to a GridBagConstraint constant.
         */
        public int toInt( Fill fill ) {
            return fillMap.get( fill ).intValue();
        }
        
        /**
         * Converts a GridBagConstraint constant to a Fill.
         */
        public Fill toFill( int fill ) {
            Fill fillObject = null;
            Iterator<Fill> i = fillMap.keySet().iterator(); // wish we had a 2-way map...
            while ( i.hasNext() ) {
                Fill next = i.next();
                if ( toInt( next ) == fill ) {
                    fillObject = next;
                    break;
                }
            }
            if ( fillObject == null ) {
                throw new IllegalArgumentException( "unknown fill value: " + fill );
            }
            return fillObject;
        }
        
        /**
         * Converts an Anchor to a GridBagConstraint constant.
         */
        public int toInt( Anchor anchor ) {
            return anchorMap.get( anchor ).intValue();
        }
        
        /**
         * Converts a GridBagConstraint constant to an Anchor.
         */
        public Anchor toAnchor( int anchor ) {
            Anchor anchorObject = null;
            Iterator<Anchor> i = anchorMap.keySet().iterator(); // wish we had a 2-way map...
            while ( i.hasNext() ) {
                Anchor next = i.next();
                if ( toInt( next ) == anchor ) {
                    anchorObject = next;
                    break;
                }
            }
            if ( anchorObject == null ) {
                throw new IllegalArgumentException( "unknown anchor value: " + anchor );
            }
            return anchorObject;
        }
    }
    
    // incomplete test harness
    public static void main( String[] args ) {
        
        GridPanel panel = new GridPanel();
        int row = 0;
        int column = 0;
        panel.add( new JLabel( "------------------------" ), row++, column );
        panel.add( new JLabel( "WEST" ), row++, column, Anchor.WEST);
        panel.add( new JLabel( "CENTER" ), row++, column, Anchor.CENTER );
        panel.add( new JLabel( "EAST" ), row++, column, Anchor.EAST );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
