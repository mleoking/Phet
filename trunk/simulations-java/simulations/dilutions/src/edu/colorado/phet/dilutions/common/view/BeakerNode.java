// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.dilutions.DilutionsResources.Symbols;
import edu.colorado.phet.dilutions.common.model.Solute;
import edu.colorado.phet.dilutions.common.model.Solution;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a beaker.
 * 3D perspective is provided by an image (see BeakerImageNode).
 * Other elements (ticks, label, ...) are added programmatically.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerNode extends PComposite {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( BeakerNode.class.getCanonicalName() );

    // tick mark properties
    private static final Color TICK_COLOR = Color.GRAY;
    private static final double MINOR_TICK_SPACING = 0.1; // L
    private static final int MINOR_TICKS_PER_MAJOR_TICK = 5;
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );
    private static final Stroke MINOR_TICK_STROKE = new BasicStroke( 2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL );

    private final BeakerImageNode beakerImageNode;
    private final LabelNode labelNode;

    public BeakerNode( final Solution solution, double maxVolume, double imageScaleX, double imageScaleY, PDimension labelSize, Font labelFont ) {
        this( solution, maxVolume, imageScaleX, imageScaleY, labelSize, labelFont, null );
    }

    // Beaker with a label that dynamically updates to match a solution's solute.
    public BeakerNode( final Solution solution, double maxVolume, double imageScaleX, double imageScaleY, PDimension labelSize, Font labelFont, final String alternateLabel ) {
        this( maxVolume, imageScaleX, imageScaleY, labelSize, labelFont, "" );

        SimpleObserver observer = new SimpleObserver() {
            public void update() {
                // update solute label
                if ( solution.volume.get() == 0 ) {
                    setLabelText( "" );
                }
                else if ( solution.getConcentration() == 0 ) {
                    setLabelText( Symbols.WATER );
                }
                else if ( alternateLabel != null ) {
                    setLabelText( alternateLabel );
                }
                else {
                    setLabelText( solution.solute.get().formula );
                }
            }
        };
        solution.addConcentrationObserver( observer );
        solution.volume.addObserver( observer );
        solution.solute.addObserver( observer );
    }

    // Beaker with a static label.
    private BeakerNode( double maxVolume, final double imageScaleX, final double imageScaleY, PDimension labelSize, Font labelFont, String labelText ) {

        // this node is not interactive
        setPickable( false );
        setChildrenPickable( false );

        // the glass beaker
        beakerImageNode = new BeakerImageNode() {{
            getTransformReference( true ).scale( imageScaleX, imageScaleY );
        }};
        final PDimension cylinderSize = beakerImageNode.getCylinderSize();
        final Point2D cylinderOffset = beakerImageNode.getCylinderOffset();
        final double cylinderEndHeight = beakerImageNode.getCylinderEndHeight();
        beakerImageNode.setOffset( -cylinderOffset.getX(), -cylinderOffset.getY() );

        // inside bottom line
        PPath bottomNode = new PPath() {{
            setPathTo( new Arc2D.Double( 0, cylinderSize.getHeight() - ( cylinderEndHeight / 2 ), cylinderSize.getWidth(), cylinderEndHeight,
                                         5, 170, Arc2D.OPEN ) );
            setStroke( new BasicStroke( 2f ) );
            setStrokePaint( new Color( 150, 150, 150, 100 ) );
        }};

        addChild( bottomNode );
        addChild( beakerImageNode );

        // tick marks, arcs that wrap around the edge of the beaker's cylinder
        PComposite ticksNode = new PComposite();
        addChild( ticksNode );
        int numberOfTicks = (int) Math.round( maxVolume / MINOR_TICK_SPACING );
        final double bottomY = cylinderSize.getHeight(); // don't use bounds or position will be off because of stroke width
        double deltaY = cylinderSize.getHeight() / numberOfTicks;
        for ( int i = 1; i <= numberOfTicks; i++ ) {
            final double y = bottomY - ( i * deltaY ) - ( cylinderEndHeight / 2 );
            if ( i % MINOR_TICKS_PER_MAJOR_TICK == 0 ) {
                // major tick, no label
                PPath tickNode = new PPath( new Arc2D.Double( 0, y, cylinderSize.getWidth(), cylinderEndHeight, 195, 30, Arc2D.OPEN ) ) {{
                    setStroke( MAJOR_TICK_STROKE );
                    setStrokePaint( TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );
            }
            else {
                // minor tick, no label
                PPath tickNode = new PPath( new Arc2D.Double( 0, y, cylinderSize.getWidth(), cylinderEndHeight, 195, 15, Arc2D.OPEN ) ) {{
                    setStroke( MINOR_TICK_STROKE );
                    setStrokePaint( TICK_COLOR );
                }};
                ticksNode.addChild( tickNode );
            }
        }

        // label on the beaker
        labelNode = new LabelNode( labelText, labelSize, labelFont );
        addChild( labelNode );
        labelNode.setOffset( ( cylinderSize.getWidth() / 2 ), ( 0.25 * cylinderSize.getHeight() ) );
    }

    // Sets the label text on the beaker
    public void setLabelText( String text ) {
        labelNode.setText( text );
    }

    public PDimension getCylinderSize() {
        return beakerImageNode.getCylinderSize();
    }

    public double getCylinderEndHeight() {
        return beakerImageNode.getCylinderEndHeight();
    }

    /*
     * Label that appears on the beaker in a frosty, translucent frame.
     * Since we're very tight on horizontal space in the play area, and the label size is static, text is scaled to fit.
     * Origin at geometric center.
     */
    private static class LabelNode extends PComposite {

        private final HTMLNode htmlNode;
        private final PPath backgroundNode;

        public LabelNode( String text, final PDimension labelSize, final Font labelFont ) {

            // nodes
            htmlNode = new HTMLNode( "?" ) {{
                setFont( labelFont );
            }};
            backgroundNode = new PPath() {{
                setPaint( ColorUtils.createColor( Color.WHITE, 150 ) );
                setStrokePaint( Color.LIGHT_GRAY );
                setPathTo( new RoundRectangle2D.Double( -labelSize.getWidth() / 2, -labelSize.getHeight() / 2, labelSize.getWidth(), labelSize.getHeight(), 10, 10 ) );
            }};

            // rendering order
            addChild( backgroundNode );
            addChild( htmlNode );

            setText( text );
        }

        public void setText( String text ) {
            htmlNode.setHTML( text );
            // scale to fit the background with some margin
            final double margin = 2;
            final double scaleX = ( backgroundNode.getFullBoundsReference().getWidth() - ( 2 * margin ) ) / htmlNode.getFullBoundsReference().getWidth();
            final double scaleY = ( backgroundNode.getFullBoundsReference().getHeight() - ( 2 * margin ) ) / htmlNode.getFullBoundsReference().getHeight();
            if ( scaleX < 1 || scaleY < 1 ) {
                double scale = Math.min( scaleX, scaleY );
                LOGGER.info( "text \"" + text + "\" won't fit in beaker label, scaling by " + scale );
                htmlNode.setScale( scale );
            }
            // center in the background
            htmlNode.setOffset( -htmlNode.getFullBoundsReference().getWidth() / 2, -htmlNode.getFullBoundsReference().getHeight() / 2 );
        }
    }

    // test
    public static void main( String[] args ) {
        Solute solute = new Solute( "MySolute", "MyFormula", 5.0, Color.RED, 1, 200 );
        Solution solution = new Solution( solute, 1, 0.5 );
        // beaker
        final BeakerNode beakerNode = new BeakerNode( solution, 1, 0.75, 0.75, new PDimension( 180, 70 ), new PhetFont( Font.BOLD, 28 ) ) {{
            setOffset( 200, 200 );
        }};
        // red dot at beaker's origin
        final PPath originNode = new PPath( new Ellipse2D.Double( -3, -3, 6, 6 ) ) {{
            setPaint( Color.RED );
            setOffset( beakerNode.getOffset() );
        }};
        // canvas
        final PCanvas canvas = new PCanvas() {{
            getLayer().addChild( beakerNode );
            getLayer().addChild( originNode );
            setPreferredSize( new Dimension( 600, 600 ) );
        }};
        // frame
        JFrame frame = new JFrame() {{
            setContentPane( canvas );
            pack();
            setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        }};
        frame.setVisible( true );
    }
}
