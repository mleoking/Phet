// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes.conductivitytester;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.IConductivityTester.ConductivityTesterChangeListener;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of the conductivity tester.
 * A simple circuit with a battery and a light bulb, and a probe at each end of the circuit.
 * When the probes are inserted in the solution, the circuit is completed, and the light bulb
 * lights up based on the tester's brightness value.
 * Origin is at the bottom-center of the light bulb.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConductivityTesterNode extends PhetPNode {

    //Strings to be shown on the probes
    public static final String PLUS = "+";
    public static final String MINUS = "-";

    //Images used here
    public static final BufferedImage BATTERY = getBufferedImage( "battery.png" );
    public static final BufferedImage LIGHT_BULB_BASE = getBufferedImage( "lightBulbBase.png" );
    public static final BufferedImage LIGHT_BULB_GLASS = getBufferedImage( "lightBulbGlass.png" );
    public static final BufferedImage LIGHT_BULB_GLASS_MASK = getBufferedImage( "lightBulbGlassMask.png" );

    //Utility method for loading the images used in this tester node
    private static BufferedImage getBufferedImage( String image ) {
        return new PhetResources( "piccolo-phet" ).getImage( image );
    }

    // light bulb properties
    private static final double PERCENT_LIGHT_BULB_ATTACHMENT = 0.12; // percent of light bulb's full height, from bottom of bulb, determines where to attach the probe wire
    private static final LinearFunction BRIGHTNESS_TO_ALPHA_FUNCTION = new LinearFunction( 0, 1, 0.85, 1 ); // alpha of the bulb
    private static final LinearFunction BRIGHTNESS_TO_INTENSITY_FUNCTION = new LinearFunction( 0, 1, 0, 1 ); // intensity of the light rays

    // probe properties
    private static final Color PROBE_STROKE_COLOR = Color.BLACK;
    private static final Stroke PROBE_STROKE = new BasicStroke( 1f );
    private static final Font PROBE_LABEL_FONT = new PhetFont( Font.BOLD, 24 );
    private static final String POSITIVE_PROBE_LABEL = PLUS;
    private static final Color POSITIVE_PROBE_LABEL_COLOR = Color.WHITE;
    private static final String NEGATIVE_PROBE_LABEL = MINUS;
    private static final Color NEGATIVE_PROBE_LABEL_COLOR = Color.WHITE;

    // wire properties
    private static final double BULB_TO_BATTERY_WIRE_LENGTH = 50;
    private static final Stroke WIRE_STROKE = new BasicStroke( 3f );
    private static final int POSITIVE_WIRE_CONTROL_POINT_DX = 25;
    private static final int POSITIVE_WIRE_CONTROL_POINT_DY = -100;
    private static final int NEGATIVE_WIRE_CONTROL_POINT_DX = -POSITIVE_WIRE_CONTROL_POINT_DX;
    private static final int NEGATIVE_WIRE_CONTROL_POINT_DY = POSITIVE_WIRE_CONTROL_POINT_DY;

    private final ModelViewTransform transform;
    private final IConductivityTester tester;
    private final Color connectorWireColor;
    private final Color positiveProbeFillColor, negativeProbeFillColor;
    private final Color positiveWireColor, negativeWireColor;

    private final LightBulbNode lightBulbNode;
    private final LightRaysNode lightRaysNode;
    private final BatteryNode batteryNode;
    private final ProbeNode positiveProbeNode, negativeProbeNode;
    private final CubicWireNode positiveWireNode, negativeWireNode;
    private final ValueNode valueNode;

    //Construct a conductivity tester node with black wires, red positive probe and black negative probe. For use in acid-base-solutions where the background is light.
    public ConductivityTesterNode( final IConductivityTester tester, boolean dev ) {
        this( ModelViewTransform.createIdentity(), tester, dev, Color.black, Color.red, Color.black );
    }

    public ConductivityTesterNode( ModelViewTransform transform, final IConductivityTester tester, boolean dev, Color wireColor, Color positiveProbeFillColor, Color negativeProbeFillColor ) {
        this( transform, tester, dev, wireColor, wireColor, wireColor, positiveProbeFillColor, negativeProbeFillColor );
    }

    /*
     * Constructor.
     *
     * @param tester model element
     * @param dev    whether to enable developer features
     */
    public ConductivityTesterNode( final ModelViewTransform transform, final IConductivityTester tester, boolean dev, Color positiveWireColor, Color negativeWireColor, Color connectorWireColor, Color positiveProbeFillColor, Color negativeProbeFillColor ) {
        this.transform = transform;
        this.tester = tester;
        this.positiveWireColor = positiveWireColor;
        this.negativeWireColor = negativeWireColor;
        this.connectorWireColor = connectorWireColor;
        this.positiveProbeFillColor = positiveProbeFillColor;
        this.negativeProbeFillColor = negativeProbeFillColor;

        // light bulb
        lightBulbNode = new LightBulbNode();
        lightBulbNode.setScale( 0.6 );

        // light rays
        double lightBulbRadius = lightBulbNode.getFullBoundsReference().getWidth() / 2;
        lightRaysNode = new LightRaysNode( lightBulbRadius );

        // battery
        batteryNode = new BatteryNode();

        // wire that connects the light bulb to the battery
        StraightWireNode connectorWireNode = new StraightWireNode( this.connectorWireColor );
        Point2D lightBulbConnectionPoint = new Point2D.Double( 0, 0 );
        Point2D batteryConnectionPoint = new Point2D.Double( 0, BULB_TO_BATTERY_WIRE_LENGTH );
        connectorWireNode.setEndPoints( lightBulbConnectionPoint, batteryConnectionPoint );

        // positive probe
        positiveProbeNode = new ProbeNode( transform.modelToViewSize( tester.getProbeSizeReference() ), this.positiveProbeFillColor, POSITIVE_PROBE_LABEL, POSITIVE_PROBE_LABEL_COLOR );
        positiveProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        positiveProbeNode.addInputEventListener(
                new ProbeDragHandler( transform, positiveProbeNode,
                                      new Function0<Point2D>() {
                                          public Point2D apply() {
                                              return tester.getPositiveProbeLocationReference();
                                          }
                                      },
                                      new VoidFunction1<Point2D>() {
                                          public void apply( Point2D point2D ) {
                                              tester.setPositiveProbeLocation( point2D.getX(), point2D.getY() );
                                          }
                                      }
                ) );

        // negative probe
        negativeProbeNode = new ProbeNode( transform.modelToViewSize( tester.getProbeSizeReference() ), this.negativeProbeFillColor, NEGATIVE_PROBE_LABEL, NEGATIVE_PROBE_LABEL_COLOR );
        negativeProbeNode.addInputEventListener( new CursorHandler( Cursor.N_RESIZE_CURSOR ) );
        negativeProbeNode.addInputEventListener(
                new ProbeDragHandler( transform, negativeProbeNode,
                                      new Function0<Point2D>() {
                                          public Point2D apply() {
                                              return tester.getNegativeProbeLocationReference();
                                          }
                                      },
                                      new VoidFunction1<Point2D>() {
                                          public void apply( Point2D point2D ) {
                                              tester.setNegativeProbeLocation( point2D.getX(), point2D.getY() );
                                          }
                                      }
                ) );

        // positive wire
        positiveWireNode = new CubicWireNode( this.positiveWireColor, POSITIVE_WIRE_CONTROL_POINT_DX, POSITIVE_WIRE_CONTROL_POINT_DY );

        // negative wire
        negativeWireNode = new CubicWireNode( this.negativeWireColor, NEGATIVE_WIRE_CONTROL_POINT_DX, NEGATIVE_WIRE_CONTROL_POINT_DY );

        // brightness value, for debugging
        valueNode = new ValueNode();

        // rendering order
        addChild( lightRaysNode );
        addChild( positiveWireNode );
        addChild( negativeWireNode );
        addChild( positiveProbeNode );
        addChild( negativeProbeNode );
        addChild( connectorWireNode );
        addChild( lightBulbNode );
        addChild( batteryNode );
        if ( dev ) {
            addChild( valueNode );
        }

        // layout 
        double x = 0;
        double y = 0;
        lightBulbNode.setOffset( x, y );
        x = lightBulbNode.getFullBoundsReference().getCenterX();
        y = lightBulbNode.getFullBoundsReference().getMinY() + lightBulbRadius;
        lightRaysNode.setOffset( x, y );
        x = lightBulbNode.getFullBoundsReference().getCenterX() + BULB_TO_BATTERY_WIRE_LENGTH;
        y = lightBulbNode.getFullBounds().getMaxY();
        batteryNode.setOffset( x, y );
        connectorWireNode.setOffset( lightBulbNode.getFullBoundsReference().getCenterX(), lightBulbNode.getFullBoundsReference().getMaxY() );
        x = lightBulbNode.getFullBoundsReference().getCenterX();
        y = batteryNode.getFullBoundsReference().getMaxY() + 3;
        valueNode.setOffset( x, y );

        // location & visibility
        setOffset( tester.getLocationReference() );
        setVisible( tester.isVisible() );


        // Listeners
        tester.addConductivityTesterChangeListener( new ConductivityTesterChangeListener() {

            public void positiveProbeLocationChanged() {
                updatePositiveProbeLocation();
            }

            public void negativeProbeLocationChanged() {
                updateNegativeProbeLocation();
            }

            public void brightnessChanged() {
                updateBrightness();
            }
        } );

        //Synchronize with any state in case it was non-default
        updateBrightness();
        updatePositiveProbeLocation();
        updateNegativeProbeLocation();
    }

    private void updatePositiveProbeLocation() {
        // probe
        Point2D probeLocation = new Point2D.Double( tester.getPositiveProbeLocationReference().getX() - tester.getLocationReference().getX(),
                                                    tester.getPositiveProbeLocationReference().getY() - tester.getLocationReference().getY() );
        Point2D viewLocation = transform.modelToView( probeLocation );
        positiveProbeNode.setOffset( viewLocation );

        // wire
        double x = batteryNode.getFullBoundsReference().getMaxX();
        double y = batteryNode.getFullBoundsReference().getCenterY();
        Point2D batteryConnectionPoint = new Point2D.Double( x, y );
        x = positiveProbeNode.getFullBoundsReference().getCenterX();
        y = positiveProbeNode.getFullBoundsReference().getMinY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        positiveWireNode.setEndPoints( batteryConnectionPoint, probeConnectionPoint );
    }

    private void updateNegativeProbeLocation() {
        // probe
        Point2D probeLocation = new Point2D.Double( tester.getNegativeProbeLocationReference().getX() - tester.getLocationReference().getX(),
                                                    tester.getNegativeProbeLocationReference().getY() - tester.getLocationReference().getY() );
        Point2D viewLocation = transform.modelToView( probeLocation );
        negativeProbeNode.setOffset( viewLocation );

        // wire
        double x = lightBulbNode.getFullBoundsReference().getCenterX();
        double y = lightBulbNode.getFullBoundsReference().getMaxY() - ( lightBulbNode.getFullBoundsReference().getHeight() * PERCENT_LIGHT_BULB_ATTACHMENT );
        Point2D componentConnectionPoint = new Point2D.Double( x, y );
        x = negativeProbeNode.getFullBoundsReference().getCenterX();
        y = negativeProbeNode.getFullBoundsReference().getMinY();
        Point2D probeConnectionPoint = new Point2D.Double( x, y );
        negativeWireNode.setEndPoints( componentConnectionPoint, probeConnectionPoint );
    }

    private void updateBrightness() {
        lightBulbNode.setGlassTransparency( (float) BRIGHTNESS_TO_ALPHA_FUNCTION.evaluate( tester.getBrightness() ) );
        lightRaysNode.setIntensity( BRIGHTNESS_TO_INTENSITY_FUNCTION.evaluate( tester.getBrightness() ) );
        valueNode.setValue( tester.getBrightness() );
    }

    /*
    * Light bulb, origin at bottom center.
    */
    private static class LightBulbNode extends PComposite {

        private final PImage glassNode;

        public LightBulbNode() {

            PNode baseNode = new PImage( LIGHT_BULB_BASE );
            glassNode = new PImage( LIGHT_BULB_GLASS );
            PNode maskNode = new PImage( LIGHT_BULB_GLASS_MASK );

            // rendering order
            addChild( maskNode );
            addChild( glassNode );
            addChild( baseNode );

            // layout
            double x = -baseNode.getFullBoundsReference().getWidth() / 2;
            double y = -baseNode.getFullBoundsReference().getHeight();
            baseNode.setOffset( x, y );
            x = baseNode.getFullBoundsReference().getCenterX() - ( glassNode.getFullBoundsReference().getWidth() / 2 );
            y = baseNode.getFullBoundsReference().getMinY() - glassNode.getFullBoundsReference().getHeight();
            glassNode.setOffset( x, y );
            maskNode.setOffset( glassNode.getOffset() );
        }

        public void setGlassTransparency( float transparency ) {
            glassNode.setTransparency( transparency );
        }
    }

    /*
    * Battery, origin at left center.
    */
    private static class BatteryNode extends PComposite {
        public BatteryNode() {
            PImage imageNode = new PImage( BATTERY );
            addChild( imageNode );
            double x = 0;
            double y = -imageNode.getFullBoundsReference().getHeight() / 2;
            imageNode.setOffset( x, y );
        }
    }

    /*
    * Probe, origin at bottom center.
    */
    private static class ProbeNode extends PhetPNode {

        public ProbeNode( Dimension2D size, Color color, String label, Color labelColor ) {
            System.out.println( "size = " + size );
            PPath pathNode = new PPath( new Rectangle2D.Double( -size.getWidth() / 2, -size.getHeight(), size.getWidth(), Math.abs( size.getHeight() ) ) );
            pathNode.setStroke( PROBE_STROKE );
            pathNode.setStrokePaint( PROBE_STROKE_COLOR );
            pathNode.setPaint( color );
            addChild( pathNode );

            PText labelNode = new PText( label );
            labelNode.setTextPaint( labelColor );
            labelNode.setFont( PROBE_LABEL_FONT );
            addChild( labelNode );

            double x = pathNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 );
            double y = pathNode.getFullBoundsReference().getMaxY() - labelNode.getFullBoundsReference().getHeight() - 3;
            labelNode.setOffset( x, y );
        }
    }

    /* Interface implemented by all wires. */
    private interface IWire {
        public void setEndPoints( Point2D startPoint, Point2D endPoint );
    }

    /*
    * Wire that is drawn as a straight line.
    */
    private static class StraightWireNode extends PPath implements IWire {

        public StraightWireNode( Color color ) {
            super();
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            setPathTo( new Line2D.Double( 0, 0, BULB_TO_BATTERY_WIRE_LENGTH, 0 ) );
        }
    }

    /*
     * Wire that is drawn using a cubic parametric curve.
     */
    private static class CubicWireNode extends PPath implements IWire {

        private final double controlPointDx, controlPointDy;

        public CubicWireNode( Color color, double controlPointDx, double controlPointDy ) {
            this.controlPointDx = controlPointDx;
            this.controlPointDy = controlPointDy;
            setStroke( WIRE_STROKE );
            setStrokePaint( color );
        }

        public void setEndPoints( Point2D startPoint, Point2D endPoint ) {
            Point2D ctrl1 = new Point2D.Double( startPoint.getX() + controlPointDx, startPoint.getY() );
            Point2D ctrl2 = new Point2D.Double( endPoint.getX(), endPoint.getY() + controlPointDy );
            setPathTo( new CubicCurve2D.Double( startPoint.getX(), startPoint.getY(), ctrl1.getX(), ctrl1.getY(), ctrl2.getX(), ctrl2.getY(), endPoint.getX(), endPoint.getY() ) );
        }
    }

    /*
    * Displays the model's brightness value, for debugging.
    */
    private static class ValueNode extends PText {

        private static final DecimalFormat FORMAT = new DecimalFormat( "0.000" );

        public ValueNode() {
            this( 0 );
        }

        public ValueNode( double brightness ) {
            setTextPaint( Color.RED );
            setFont( new PhetFont( 16 ) );
            setValue( brightness );
        }

        public void setValue( double brightness ) {
            setText( "brightness=" + FORMAT.format( brightness ) ); // no i18n needed, this is a dev feature
        }
    }

    private static class ProbeDragHandler extends PBasicInputEventHandler {
        private Point2D.Double relativeGrabPoint;

        private final ModelViewTransform transform;
        private final PNode probeNode;
        private final Function0<Point2D> getModelPosition;
        private final VoidFunction1<Point2D> setModelPosition;

        ProbeDragHandler( ModelViewTransform transform, PNode probeNode, Function0<Point2D> getModelPosition, VoidFunction1<Point2D> setModelPosition ) {
            this.transform = transform;
            this.probeNode = probeNode;
            this.getModelPosition = getModelPosition;
            this.setModelPosition = setModelPosition;
        }

        private void updateGrabPoint( PInputEvent event ) {
            Point2D viewStartingPoint = event.getPositionRelativeTo( probeNode.getParent() );
            ImmutableVector2D viewCoordinateOfObject = transform.modelToView( new ImmutableVector2D( getModelPosition.apply() ) );
            relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
        }

        public void mouseDragged( PInputEvent event ) {
            //Make sure we started the drag already
            if ( relativeGrabPoint == null ) {
                updateGrabPoint( event );
            }

            //Compute the targeted model point for the drag
            final Point2D newDragPosition = event.getPositionRelativeTo( probeNode.getParent() );
            Point2D modelPt = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                     newDragPosition.getY() - relativeGrabPoint.getY() );

            //Find the constrained point for the targeted model point and apply it
            setModelPosition.apply( new Point2D.Double( getModelPosition.apply().getX(), modelPt.getY() ) );
        }
    }
}