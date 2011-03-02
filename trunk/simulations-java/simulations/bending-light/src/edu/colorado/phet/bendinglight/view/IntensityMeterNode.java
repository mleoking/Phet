// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.bendinglight.BendingLightApplication;
import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class IntensityMeterNode extends PNode {
    private final ModelViewTransform transform;
    private final IntensityMeter intensityMeter;
    public PImage bodyNode;
    public PImage sensorNode;

    public IntensityMeterNode( final ModelViewTransform transform, final IntensityMeter intensityMeter ) {
        this.transform = transform;
        this.intensityMeter = intensityMeter;
        intensityMeter.enabled.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( intensityMeter.enabled.getValue() );
            }
        } );

        sensorNode = new PImage( BendingLightApplication.RESOURCES.getImage( "intensity_meter_probe.png" ) ) {{
            intensityMeter.sensorPosition.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double sensorViewPoint = transform.modelToView( intensityMeter.sensorPosition.getValue() ).toPoint2D();
                    setOffset( sensorViewPoint.getX() - getFullBounds().getWidth() / 2, sensorViewPoint.getY() - getFullBounds().getHeight() * 0.32 );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    intensityMeter.translateSensor( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }
            } );
        }};
        final PhetPPath sensorHotSpotDebugger = new PhetPPath( new BasicStroke( 1 ), Color.green ) {{
            intensityMeter.sensorPosition.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( intensityMeter.getSensorShape() ) );
                }
            } );
            setPickable( false );
            setChildrenPickable( false );
        }};

        bodyNode = new PImage( BendingLightApplication.RESOURCES.getImage( "intensity_meter_box.png" ) ) {{
            intensityMeter.bodyPosition.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( transform.modelToView( intensityMeter.bodyPosition.getValue() ).toPoint2D() );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    intensityMeter.translateBody( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }
            } );
        }};
        bodyNode.addChild( new PText( "Intensity" ) {{
            setFont( new PhetFont( 22 ) );
            setTextPaint( Color.white );
            setOffset( bodyNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getHeight() * 0.1 );
        }} );

        bodyNode.addChild( new PText( "-" ) {{
            setFont( new PhetFont( 30 ) );
            intensityMeter.reading.addObserver( new SimpleObserver() {
                public void update() {
                    setTransform( new AffineTransform() );
                    setText( intensityMeter.reading.getValue().getString() );
                    setOffset( bodyNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getHeight() * 0.44 );
                }
            } );
        }} );
        bodyNode.addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) {{
            setOffset( bodyNode.getFullBounds().getWidth() - getFullBounds().getWidth() - 10, 10 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    intensityMeter.enabled.setValue( false );
                }
            } );
        }} );

        addChild( new WireNode( sensorNode, bodyNode, Color.gray ) );

        addChild( bodyNode );
        addChild( sensorNode );
//        addChild( sensorHotSpotDebugger );
    }

    private void doDrag( PInputEvent event ) {
        final Dimension2D delta = transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) );
        doTranslate( delta );
    }

    public void doTranslate( Dimension2D delta ) {
        intensityMeter.translateAll( delta );
    }

    public Rectangle2D getSensorGlobalFullBounds() {
        return sensorNode.getGlobalFullBounds();

    }

    public Rectangle2D getBodyGlobalFullBounds() {
        return bodyNode.getGlobalFullBounds();
    }
}
