/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.test;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.EventListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.capacitorlab.util.GridPanel;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Test harness for plate charge layout in Capacitor Lab simulation.
 * Charges are arranged in a grid.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestPlateChargeLayout extends JFrame {

    private static final Dimension CANVAS_SIZE = new Dimension( 1024, 768 );
    private static final IntegerRange NUMBER_OF_CHARGES_RANGE = new IntegerRange( 0, 625, 0 );
    private static final IntegerRange PLATE_WIDTH_RANGE = new IntegerRange( 100, 500, 200 );
    private static final IntegerRange PLATE_HEIGHT_RANGE = new IntegerRange( 100, 500, 200 );
    private static final double PLUS_MINUS_WIDTH = 7;
    private static final double PLUS_MINUS_HEIGHT = 1;
    
    public interface ModelChangeListener extends EventListener {
        public void numberOfChargesChanged();
        public void plateSizeChanged();
    }
    
    public static class TestModel {
        
        private final EventListenerList listeners;
        private int numberOfCharges;
        private Dimension plateSize;
        
        public TestModel() {
            listeners = new EventListenerList();
            numberOfCharges = NUMBER_OF_CHARGES_RANGE.getDefault();
            plateSize = new Dimension( PLATE_WIDTH_RANGE.getDefault(), PLATE_HEIGHT_RANGE.getDefault() );
        }
        
        public void setNumberOfCharges( int numberOfCharges ) {
            if ( numberOfCharges != this.numberOfCharges ) {
                this.numberOfCharges = numberOfCharges;
                fireNumberOfChargesChanged();
            }
        }
        
        public int getNumberOfCharges() {
            return numberOfCharges;
        }
        
        public void setPlateWidth( int width ) {
            if ( width != plateSize.width ) {
                plateSize.setSize( width, plateSize.height );
                firePlateSizeChanged();
            }
        }
        
        public void setPlateHeight( int height ) {
            if ( height != plateSize.height ) {
                plateSize.setSize( plateSize.width, height );
                firePlateSizeChanged();
            }
        }
        
        public int getPlateWidth() {
            return plateSize.width;
        }
        
        public int getPlateHeight() {
            return plateSize.height;
        }
        
        public void addModelChangeListener( ModelChangeListener listener ) {
            listeners.add( ModelChangeListener.class, listener );
        }
        
        public void removeModelChangeListener( ModelChangeListener listener ) {
            listeners.remove( ModelChangeListener.class, listener );
        }
        
        private void fireNumberOfChargesChanged() {
            for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
                listener.numberOfChargesChanged();
            }
        }
        
        private void firePlateSizeChanged() {
            for ( ModelChangeListener listener : listeners.getListeners( ModelChangeListener.class ) ) {
                listener.plateSizeChanged();
            }
        }
    }
    
    public static class TestControlPanel extends GridPanel {
        
        public TestControlPanel( final TestModel model ) {
            setBorder( new LineBorder( Color.BLACK ) );
            
            // number of charges
            final IntegerValueControl numberOfChargesControl = new IntegerValueControl( "# charges:", NUMBER_OF_CHARGES_RANGE.getMin(), NUMBER_OF_CHARGES_RANGE.getMax(), model.getNumberOfCharges(), "" );
            numberOfChargesControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setNumberOfCharges( numberOfChargesControl.getValue() );
                }
            });
            
            // plate width
            final IntegerValueControl plateWidthControl = new IntegerValueControl( "plate width:", PLATE_WIDTH_RANGE.getMin(), PLATE_WIDTH_RANGE.getMax(), model.getPlateWidth(), "mm" );
            plateWidthControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setPlateWidth( plateWidthControl.getValue() );
                }
            });
            
            // plate height
            final IntegerValueControl plateHeightControl = new IntegerValueControl( "plate height:", PLATE_HEIGHT_RANGE.getMin(), PLATE_HEIGHT_RANGE.getMax(), model.getPlateHeight(), "mm" );
            plateHeightControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    model.setPlateHeight( plateHeightControl.getValue() );
                }
            });
            
            // layout
            int row = 0;
            int column = 0;
            setAnchor( Anchor.WEST );
            setInsets( new Insets( 5, 5, 5, 5 ) );
            add( numberOfChargesControl, row++, column );
            add( plateWidthControl, row++, column );
            add( plateHeightControl, row++, column );
            
            // model change listener
            model.addModelChangeListener( new ModelChangeListener() {

                public void numberOfChargesChanged() {
                    numberOfChargesControl.setValue( model.getNumberOfCharges() );
                }

                public void plateSizeChanged() {
                    plateWidthControl.setValue( model.getPlateWidth() );
                    plateHeightControl.setValue( model.getPlateHeight() );
                }
            } );
        }
        
        /*
         * A slider with integrated title, value display and units.
         * Layout => title: slider value units
         */
        public static class IntegerValueControl extends JPanel {
            
            private final JSlider slider;
            private final JLabel valueLabel;
            
            public IntegerValueControl( String title, int min, int max, int value, String units ) {
                // components
                JLabel titleLabel = new JLabel( title );
                slider = new JSlider( min, max, value );
                valueLabel = new JLabel( String.valueOf( slider.getValue() ) );
                JLabel unitsLabel = new JLabel( units );
                // layout
                add( titleLabel );
                add( slider );
                add( valueLabel );
                add( unitsLabel );
                // keep value display in sync with slider
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        valueLabel.setText( String.valueOf( slider.getValue() ) );
                    }
                } );
            }
            
            public int getValue() {
                return slider.getValue();
            }
            
            public void setValue( int value ) {
                slider.setValue( value );
            }
            
            public void addChangeListener( ChangeListener listener ) {
                // warning: ChangeEvent.getSource will return the JSlider, not the IntegerValueControl
                slider.addChangeListener( listener );
            }
            
            public void removeChangeListener( ChangeListener listener ) {
                slider.addChangeListener( listener );
            }
        }
    }
    
    public static class TestCanvas extends PCanvas {
        
        private final TestModel model;
        private final PPath plateNode;
        private final PComposite parentChargesNode;
        private final PText gridInfoNode;
        
        public TestCanvas( final TestModel model ) {
            setPreferredSize( CANVAS_SIZE );
            
            // plate
            plateNode = new PPath();
            plateNode.setPaint( Color.LIGHT_GRAY );
            plateNode.setStroke( new BasicStroke( 1f ) );
            plateNode.setStrokePaint( Color.BLACK );

            // parent node for charges on the plate
            parentChargesNode = new PComposite();
            
            // info about the grid
            gridInfoNode = new PText();
            gridInfoNode.setFont( new PhetFont( 18 ) );
            
            // rendering order
            addChild( plateNode );
            addChild( parentChargesNode );
            addChild( gridInfoNode );
            
            // layout
            plateNode.setOffset( ( PLATE_WIDTH_RANGE.getMax() / 2 ) + 100, ( PLATE_HEIGHT_RANGE.getMax() / 2 ) + 100 );
            parentChargesNode.setOffset( plateNode.getOffset() );
            gridInfoNode.setOffset( plateNode.getXOffset() + ( PLATE_WIDTH_RANGE.getMax() / 2 ) + 50, plateNode.getYOffset() );
            
            // model change listener
            this.model = model;
            model.addModelChangeListener( new ModelChangeListener() {

                public void plateSizeChanged() {
                    update();
                }
                
                public void numberOfChargesChanged() {
                    update();
                }
            });
            
            update();
        }
        
        // convenience method for adding nodes to the canvas
        public void addChild( PNode child ) {
            getLayer().addChild( child );
        }
        
        private void update() {
            updatePlate();
            updateCharges();
        }
        
        /*
         * Updates the plate geometry to match the model.
         * Origin is at the geometric center.
         */
        private void updatePlate() {
            double width = model.getPlateWidth();
            double height = model.getPlateHeight();
            plateNode.setPathTo( new Rectangle2D.Double( -width / 2, -height / 2, width, height ) );
        }
        
        /*
         * Updates the charges to match the model.
         * Charges are arranged in a grid, whose size is adjusted dynamically.
         */
        private void updateCharges() {
            
            // get model values
            final int numberOfCharges = model.getNumberOfCharges();
            final double plateWidth = model.getPlateWidth();
            final double plateHeight = model.getPlateHeight();
            
            // clear the grid of existing charges
            parentChargesNode.removeAllChildren();
            
            int rows = 0;
            int columns = 0;
            if ( numberOfCharges > 0 ) {
                
                // compute the grid dimensions
                final double alpha = Math.sqrt( numberOfCharges / plateWidth / plateHeight );
                rows = (int) Math.max( 1, plateHeight * alpha ); // casting may result in some charges being thrown out, but that's OK
                columns = (int) Math.max( 1, plateWidth * alpha );

                // populate the grid with charges
                double dx = plateWidth / columns;
                double dy = plateHeight / rows;
                double xOffset = dx / 2;
                double yOffset = dy / 2;
                for ( int row = 0; row < rows; row++ ) {
                    for ( int column = 0; column < columns; column++ ) {
                        // add a charge
                        PNode chargeNode = new PlusNode( PLUS_MINUS_WIDTH, PLUS_MINUS_HEIGHT, Color.RED );
                        parentChargesNode.addChild( chargeNode );

                        // position the charge in cell in the grid
                        double x = -( plateWidth / 2 ) + xOffset + ( column * dx );
                        double y = -( plateHeight / 2 ) + yOffset + ( row * dy );
                        chargeNode.setOffset( x, y );
                    }
                }
            }
            
            // display grid dimensions and the actual number of charges rendered
            gridInfoNode.setText( rows + "x" + columns + ", " + ( rows * columns ) + " charges" );
        }
    }
    
    public TestPlateChargeLayout() {
        
        // MVC
        TestModel model = new TestModel();
        TestCanvas canvas = new TestCanvas( model );
        TestControlPanel controlPanel = new TestControlPanel( model );
        
        // layout like a simulation
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        panel.add( controlPanel, BorderLayout.EAST );
        setContentPane( panel );
        
        pack();
    }
    
    public static void main( String[] args ) {
        JFrame frame = new TestPlateChargeLayout();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
