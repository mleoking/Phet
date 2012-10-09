// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.drag.WorldLocationDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.capacitorlab.view.meters.ZoomButton.ZoomInButton;
import edu.colorado.phet.capacitorlab.view.meters.ZoomButton.ZoomOutButton;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.Vector2DNode;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PClip;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * Body of the E-Field Detector, origin at upper-left corner of bounding rectangle.
 * The body includes a set of controls and a vector display.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class EFieldDetectorBodyNode extends PhetPNode {

    private static final Font TITLE_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color TITLE_COLOR = Color.WHITE;

    private static final Color BODY_COLOR = new Color( 75, 75, 75 );
    private static final double BODY_CORNER_RADIUS = 15;
    private static final int BODY_X_MARGIN = 5;
    private static final int BODY_Y_MARGIN = 5;
    private static final int BODY_X_SPACING = 2;
    private static final int BODY_Y_SPACING = 4;

    private static final PDimension VECTOR_DISPLAY_SIZE = new PDimension( 200, 200 );
    private static final Color VECTOR_DISPLAY_BACKGROUND = Color.WHITE;
    private static final double VECTOR_REFERENCE_LENGTH = 3 * VECTOR_DISPLAY_SIZE.getHeight();
    private static final Dimension VECTOR_ARROW_HEAD_SIZE = new Dimension( 30, 20 );
    private static final int VECTOR_ARROW_TAIL_WIDTH = 10;

    private static final Font VALUE_FONT = new PhetFont( 14 );
    private static final NumberFormat VALUE_FORMAT = new DecimalFormat( "0" );
    private static final Color VALUE_COLOR = Color.BLACK;

    private static final Font CONTROL_FONT = new PhetFont( Font.BOLD, 16 );
    private static final Color CONTROL_COLOR = Color.WHITE;

    private static final int ZOOM_FACTOR = 5;
    private static final IntegerRange ZOOM_LEVEL_RANGE = new IntegerRange( 0, 4, 4 ); // start fully zoomed in

    private final PSwing showVectorsPSwing;
    private final VectorDisplayNode vectorDisplayNode;
    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body
    private final ZoomPanel zoomPanel;

    public EFieldDetectorBodyNode( final EFieldDetector detector, final CLModelViewTransform3D mvt, double vectorReferenceMagnitude ) {

        // title that appears at the top
        PText titleNode = new PText( CLStrings.ELECTRIC_FIELD ) {{
            setTextPaint( TITLE_COLOR );
            setFont( TITLE_FONT );
        }};

        // close button
        PImage closeButtonNode = new PImage( CLImages.CLOSE_BUTTON ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseReleased( PInputEvent event ) {
                    detector.visibleProperty.setValue( false );
                }
            } );
        }};


        // display area for vectors and values
        vectorDisplayNode = new VectorDisplayNode( detector, vectorReferenceMagnitude );

        // Vector controls
        showVectorsPSwing = new PSwing( new ShowVectorsPanel( detector ) );

        // Zoom controls
        zoomPanel = new ZoomPanel( ZOOM_FACTOR, ZOOM_LEVEL_RANGE );
        PSwing zoomPSwing = new PSwing( zoomPanel );

        // Show Values check box
        PSwing showValuesPSwing = new PSwing( new DetectorCheckBox( CLStrings.SHOW_VALUES, detector.valuesVisibleProperty, CONTROL_COLOR ) );

        // background
        double maxControlWidth = Math.max( showVectorsPSwing.getFullBoundsReference().getWidth(), showValuesPSwing.getFullBoundsReference().getWidth() );
        double width = maxControlWidth + vectorDisplayNode.getFullBoundsReference().getWidth() + ( 2 * BODY_X_MARGIN ) + BODY_X_SPACING;
        final double controlsHeight = showVectorsPSwing.getFullBoundsReference().getHeight() + showValuesPSwing.getFullBoundsReference().getHeight();
        double height = titleNode.getFullBoundsReference().getHeight() + BODY_Y_SPACING + Math.max( controlsHeight, vectorDisplayNode.getFullBoundsReference().getHeight() ) + ( 2 * BODY_Y_MARGIN );
        PPath backgroundNode = new PPath( new RoundRectangle2D.Double( 0, 0, width, height, BODY_CORNER_RADIUS, BODY_CORNER_RADIUS ) );
        backgroundNode.setPaint( BODY_COLOR );
        backgroundNode.setStroke( null );

        // rendering order
        addChild( backgroundNode );
        addChild( titleNode );
        addChild( closeButtonNode );
        addChild( showVectorsPSwing );
        addChild( zoomPSwing );
        addChild( showValuesPSwing );
        addChild( vectorDisplayNode );

        // layout
        {
            double x = 0;
            double y = 0;
            backgroundNode.setOffset( x, y );
            // title
            x = backgroundNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
            y = BODY_Y_MARGIN;
            titleNode.setOffset( x, y );
            // close button
            x = backgroundNode.getFullBoundsReference().getMaxX() - closeButtonNode.getFullBoundsReference().getWidth() - BODY_X_MARGIN;
            y = backgroundNode.getFullBoundsReference().getMinY() + BODY_Y_MARGIN;
            closeButtonNode.setOffset( x, y );
            // vector controls in upper left
            x = BODY_X_MARGIN;
            y = titleNode.getFullBoundsReference().getMaxY() + BODY_Y_SPACING;
            showVectorsPSwing.setOffset( x, y );
            // zoom controls below vector controls
            x = showVectorsPSwing.getFullBoundsReference().getMinX();
            y = showVectorsPSwing.getFullBoundsReference().getMaxY() + ( 2 * BODY_Y_SPACING );
            zoomPSwing.setOffset( x, y );
            // "Show values" control at lower left
            x = BODY_X_MARGIN;
            y = backgroundNode.getFullBoundsReference().getMaxY() - showValuesPSwing.getFullBoundsReference().getHeight() - BODY_Y_MARGIN;
            showValuesPSwing.setOffset( x, y );
            // vectors
            x = BODY_X_MARGIN + maxControlWidth + BODY_X_SPACING;
            y = showVectorsPSwing.getYOffset();
            vectorDisplayNode.setOffset( x, y );
        }

        // wire connects to the left center of the detector body
        connectionOffset = new Point2D.Double( 0, getFullBoundsReference().getHeight() / 2 );

        // interactivity
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WorldLocationDragHandler( detector.bodyLocationProperty, this, mvt ) );

        // observers
        {
            // location
            detector.bodyLocationProperty.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( mvt.modelToView( detector.bodyLocationProperty.getValue() ) );
                }
            } );

            // when the zoom level changes, update the vector display
            zoomPanel.addZoomLevelObserver( new SimpleObserver() {
                public void update() {
                    vectorDisplayNode.setScaleFactor( zoomPanel.getScaleFactor() );
                }
            } );
        }
    }

    public void reset() {
        zoomPanel.reset();
    }

    /**
     * Calling this with true provides a simplified E-Field detector,
     * with fewer controls and fewer visible vectors.
     *
     * @param simplified
     */
    public void setSimplified( boolean simplified ) {
        showVectorsPSwing.setVisible( !simplified );
        vectorDisplayNode.setSimplified( simplified );
    }

    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }

    /*
     * Panel with check boxes for vectors.
     */
    private static class ShowVectorsPanel extends GridPanel {

        public ShowVectorsPanel( final EFieldDetector detector ) {
            setBackground( BODY_COLOR );

            // components
            JLabel showVectorsLabel = new JLabel( CLStrings.SHOW_VECTORS ) {{
                setFont( CONTROL_FONT );
                setForeground( CONTROL_COLOR );
            }};
            JCheckBox plateCheckBox = new DetectorCheckBox( CLStrings.PLATE, detector.plateVisibleProperty, CLPaints.PLATE_EFIELD_VECTOR );
            JCheckBox dielectricCheckBox = new DetectorCheckBox( CLStrings.DIELECTRIC, detector.dielectricVisibleProperty, CLPaints.DIELECTRIC_EFIELD_VECTOR );
            JCheckBox sumCheckBox = new DetectorCheckBox( CLStrings.SUM, detector.sumVisibleProperty, CLPaints.SUM_EFIELD_VECTOR );

            // layout
            setAnchor( Anchor.WEST );
            int row = 0;
            int column = 0;
            add( showVectorsLabel, row++, column );
            add( plateCheckBox, row++, column );
            add( dielectricCheckBox, row++, column );
            add( sumCheckBox, row++, column );
        }
    }

    /*
     * Rectangular area where the vectors are displayed.
     * Vectors are clipped to this area.
     * Vector size is computed relative to a specified reference magnitude.
     */
    private static final class VectorDisplayNode extends PClip {

        private final EFieldDetector detector;
        private final FieldVectorNode plateVectorNode, dielectricVectorNode, sumVectorNode;
        private final FieldValueNode plateValueNode, dielectricValueNode, sumValueNode;
        private double scaleFactor;
        private boolean simplified;

        public VectorDisplayNode( final EFieldDetector detector, double vectorReferenceMagnitude ) {

            setPathTo( new Rectangle2D.Double( 0, 0, VECTOR_DISPLAY_SIZE.getWidth(), VECTOR_DISPLAY_SIZE.getHeight() ) );
            setPaint( VECTOR_DISPLAY_BACKGROUND );
            setStroke( null );

            this.detector = detector;
            scaleFactor = 1;
            simplified = false;

            // vectors
            plateVectorNode = new FieldVectorNode( CLPaints.PLATE_EFIELD_VECTOR, vectorReferenceMagnitude );
            dielectricVectorNode = new FieldVectorNode( CLPaints.DIELECTRIC_EFIELD_VECTOR, vectorReferenceMagnitude );
            sumVectorNode = new FieldVectorNode( CLPaints.SUM_EFIELD_VECTOR, vectorReferenceMagnitude );

            // values
            plateValueNode = new FieldValueNode( CLStrings.PLATE, CLPaints.PLATE_EFIELD_VECTOR );
            dielectricValueNode = new FieldValueNode( CLStrings.DIELECTRIC, CLPaints.DIELECTRIC_EFIELD_VECTOR );
            sumValueNode = new FieldValueNode( CLStrings.SUM, CLPaints.SUM_EFIELD_VECTOR );

            // rendering order
            addChild( plateVectorNode );
            addChild( dielectricVectorNode );
            addChild( sumVectorNode );
            addChild( plateValueNode );
            addChild( dielectricValueNode );
            addChild( sumValueNode );

            // observe vector changes
            SimpleObserver vectorsObserver = new SimpleObserver() {
                public void update() {
                    updateVectors();
                }
            };
            detector.addPlateVectorObserver( vectorsObserver );
            detector.addDielectricVectorObserver( vectorsObserver );
            detector.addSumVectorObserver( vectorsObserver );

            // observer visibility changes
            SimpleObserver visibilityObserver = new SimpleObserver() {
                public void update() {
                    updateVisibility();
                }
            };
            detector.plateVisibleProperty.addObserver( visibilityObserver );
            detector.dielectricVisibleProperty.addObserver( visibilityObserver );
            detector.sumVisibleProperty.addObserver( visibilityObserver );
            detector.valuesVisibleProperty.addObserver( visibilityObserver );

            updateLayout();
        }

        /**
         * A simplified detector shows less stuff.
         *
         * @param simplified
         */
        public void setSimplified( boolean simplified ) {
            if ( simplified != this.simplified ) {
                this.simplified = simplified;
                updateVisibility();
            }
        }

        /**
         * Vectors will be scaled by this factor.
         *
         * @param scaleFactor
         */
        public void setScaleFactor( double scaleFactor ) {
            if ( scaleFactor != this.scaleFactor ) {
                this.scaleFactor = scaleFactor;
                updateVectors();
            }
        }

        // Updates vectors and numeric values.
        private void updateVectors() {

            plateVectorNode.setXY( 0, scaleFactor * detector.getPlateVector() );
            plateValueNode.setValue( detector.getPlateVector() );

            dielectricVectorNode.setXY( 0, scaleFactor * -detector.getDielectricVector() ); // change sign because dielectric vector points in opposite direction
            dielectricValueNode.setValue( detector.getDielectricVector() );

            sumVectorNode.setXY( 0, scaleFactor * detector.getSumVector() );
            sumValueNode.setValue( detector.getSumVector() );

            updateLayout();
        }

        /*
         * Updates visibility of vectors and numeric values.
         * When the vector display is simplified, only the Plate vector is shown.
         */
        private void updateVisibility() {

            // vectors
            final boolean plateVisible = detector.plateVisibleProperty.getValue();
            plateVectorNode.setVisible( plateVisible );
            plateValueNode.setVisible( plateVisible );

            final boolean dielectricVisible = detector.dielectricVisibleProperty.getValue();
            dielectricVectorNode.setVisible( dielectricVisible );
            dielectricValueNode.setVisible( dielectricVisible );

            boolean sumVisible = detector.sumVisibleProperty.getValue();
            sumVectorNode.setVisible( sumVisible );
            sumValueNode.setVisible( sumVisible );

            // values
            final boolean valuesVisible = detector.valuesVisibleProperty.getValue();
            plateValueNode.setValueVisible( valuesVisible );
            dielectricValueNode.setValueVisible( valuesVisible );
            sumValueNode.setValueVisible( valuesVisible );

            // When the vector display is simplified, only the Plate vector is shown.
            if ( simplified ) {
                dielectricVectorNode.setVisible( false );
                dielectricValueNode.setVisible( false );
                sumVectorNode.setVisible( false );
                sumValueNode.setVisible( false );
            }

            updateLayout();
        }

        // dynamic layout
        private void updateLayout() {

            double x, y;

            // vectors
            {
                /*
                 * Horizontal spacing between plate and dielectric vector centers.
                 * This is zero for the simplified case, since only the Plate vector
                 * is visible, and we want it to be horizontally centered in the display.
                 */
                final double xSpacing = simplified ? 0 : this.getBoundsReference().getWidth() / 4;

                // plate vector is vertically centered
                x = this.getBoundsReference().getCenterX() - xSpacing;
                y = this.getBoundsReference().getCenterY() - ( plateVectorNode.getFullBoundsReference().getHeight() / 2 ) - PNodeLayoutUtils.getOriginYOffset( plateVectorNode );
                plateVectorNode.setOffset( x, y );

                // sum vector is aligned with tail of plate vector
                x = this.getBoundsReference().getCenterX() + xSpacing;
                y = plateVectorNode.getYOffset();
                sumVectorNode.setOffset( x, y );

                // dielectric vector fills in space above or below sum vector
                x = sumVectorNode.getXOffset();
                if ( sumVectorNode.getVector().getY() < 0 ) {
                    y = sumVectorNode.getFullBoundsReference().getMinY() - dielectricVectorNode.getFullBoundsReference().getHeight(); // above
                }
                else {
                    y = sumVectorNode.getFullBoundsReference().getMaxY() + dielectricVectorNode.getFullBoundsReference().getHeight(); // below
                }
                dielectricVectorNode.setOffset( x, y );
            }

            // labeled values, all placed at vector tails, horizontally centered
            {
                final double ySpacing = 2; // space between vector tail and label

                // plate label
                x = plateVectorNode.getFullBoundsReference().getCenterX();
                if ( plateVectorNode.getVector().getY() >= 0 ) {
                    y = plateVectorNode.getFullBoundsReference().getMinY() - plateValueNode.getFullBoundsReference().getHeight() - ySpacing;
                }
                else {
                    y = plateVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                }
                plateValueNode.setOffset( x, y );

                // sum label
                x = sumVectorNode.getFullBoundsReference().getCenterX();
                if ( sumVectorNode.getVector().getY() >= 0 ) {
                    y = sumVectorNode.getFullBoundsReference().getMinY() - sumValueNode.getFullBoundsReference().getHeight() - ySpacing;
                }
                else {
                    y = sumVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                }
                sumValueNode.setOffset( x, y );

                // dielectric label
                x = dielectricVectorNode.getFullBoundsReference().getCenterX();
                if ( dielectricVectorNode.getVector().getY() > 0 || ( dielectricVectorNode.getVector().getY() == 0 && sumVectorNode.getVector().getY() < 0 ) ) {
                    // above vector
                    y = dielectricVectorNode.getFullBoundsReference().getMinY() - dielectricValueNode.getFullBoundsReference().getHeight() - ySpacing;
                }
                else {
                    // below vector
                    y = dielectricVectorNode.getFullBoundsReference().getMaxY() + ySpacing;
                }
                dielectricValueNode.setOffset( x, y );
            }
        }
    }

    /*
     * Encapsulates the "look" of check boxes in the detector.
     */
    private static class DetectorCheckBox extends PropertyCheckBox {
        public DetectorCheckBox( String text, Property<Boolean> property, Color foreground ) {
            super( text, property );
            setOpaque( false );
            setFont( CONTROL_FONT );
            setForeground( foreground );
        }
    }

    /*
     * Field vector.
     */
    private static class FieldVectorNode extends Vector2DNode {

        public FieldVectorNode( Color color, double vectorReferenceMagnitude ) {
            super( 0, 0, vectorReferenceMagnitude, VECTOR_REFERENCE_LENGTH );
            setArrowFillPaint( color );
            setHeadSize( VECTOR_ARROW_HEAD_SIZE );
            setTailWidth( VECTOR_ARROW_TAIL_WIDTH );
        }
    }

    /*
     * Displays a labeled vector value.
     * Label is above value, and they are horizontally centered.
     * Origin is at the top center of the bounding rectangle.
     */
    private static class FieldValueNode extends PComposite {

        private final PText labelNode, valueNode;

        public FieldValueNode( String label, Color backgroundColor ) {

            labelNode = new PText( label );
            labelNode.setPaint( backgroundColor );
            labelNode.setTextPaint( VALUE_COLOR );
            labelNode.setFont( VALUE_FONT );
            addChild( labelNode );

            valueNode = new PText();
            valueNode.setPaint( backgroundColor );
            valueNode.setTextPaint( VALUE_COLOR );
            valueNode.setFont( VALUE_FONT );
            addChild( valueNode );

            setValue( 0 );
        }

        public void setValue( double value ) {
            String valueString = VALUE_FORMAT.format( Math.abs( value ) );
            valueNode.setText( MessageFormat.format( CLStrings.PATTERN_VALUE_UNITS, valueString, CLStrings.VOLTS_PER_METER ) );
            updateLayout();
        }

        /*
         * Changes visibility of the value by adding/removing it from the scenegraph,
         * so that it only contributes to bounds computations when visible.
         */
        public void setValueVisible( boolean valueVisible ) {
            if ( valueVisible ) {
                addChild( valueNode );
            }
            else {
                if ( indexOfChild( valueNode ) != -1 ) {
                    removeChild( valueNode );
                }
            }
        }

        private void updateLayout() {
            double x = -labelNode.getFullBoundsReference().getWidth() / 2;
            double y = 0;
            labelNode.setOffset( x, y );
            x = -valueNode.getFullBoundsReference().getWidth() / 2;
            y = labelNode.getFullBoundsReference().getMaxY() - 1;
            valueNode.setOffset( x, y );
        }
    }

    /*
     * Panel with zoom controls.
     */
    private static class ZoomPanel extends GridPanel {

        private final double zoomFactor;
        private final IntegerRange zoomLevelRange;
        private final Property<Integer> zoomLevelProperty;

        public ZoomPanel( double zoomFactor, final IntegerRange zoomLevelRange ) {
            setOpaque( false );

            this.zoomFactor = zoomFactor;
            this.zoomLevelRange = zoomLevelRange;
            this.zoomLevelProperty = new Property<Integer>( zoomLevelRange.getDefault() );

            JLabel label = new JLabel( CLStrings.ZOOM );
            label.setFont( CONTROL_FONT );
            label.setForeground( CONTROL_COLOR );

            final JButton zoomInButton = new ZoomInButton();
            zoomInButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomLevelProperty.setValue( zoomLevelProperty.getValue() + 1 );
                }
            } );

            final JButton zoomOutButton = new ZoomOutButton();
            zoomOutButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    zoomLevelProperty.setValue( zoomLevelProperty.getValue() - 1 );
                }
            } );

            // layout
            setAnchor( Anchor.WEST );
            setInsets( new Insets( 0, 0, 1, 10 ) );
            add( label, 0, 0 );
            add( zoomInButton, 0, 1 );
            add( zoomOutButton, 1, 1 );

            // Disable buttons at the extremes of the zoom range.
            zoomLevelProperty.addObserver( new SimpleObserver() {
                public void update() {
                    zoomInButton.setEnabled( zoomLevelProperty.getValue() != zoomLevelRange.getMax() );
                    zoomOutButton.setEnabled( zoomLevelProperty.getValue() != zoomLevelRange.getMin() );
                }
            } );
        }

        public void addZoomLevelObserver( SimpleObserver o ) {
            zoomLevelProperty.addObserver( o );
        }

        public double getScaleFactor() {
            return ( 1 / Math.pow( zoomFactor, zoomLevelRange.getMax() - zoomLevelProperty.getValue() ) );
        }

        public void reset() {
            zoomLevelProperty.reset();
        }
    }
}