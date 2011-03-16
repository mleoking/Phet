// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.moretools.VelocitySensorNode;
import edu.colorado.phet.bendinglight.modules.moretools.WaveSensor;
import edu.colorado.phet.bendinglight.modules.moretools.WaveSensorNode;
import edu.colorado.phet.bendinglight.view.BendingLightCanvas;
import edu.colorado.phet.bendinglight.view.IntensityMeterNode;
import edu.colorado.phet.bendinglight.view.ProtractorModel;
import edu.colorado.phet.bendinglight.view.ProtractorNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.bendinglight.model.BendingLightModel.CHARACTERISTIC_LENGTH;
import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;

/**
 * @author Sam Reid
 */
public class ToolboxNode extends VBox {

    public ToolboxNode( final BendingLightCanvas canvas,
                        final ModelViewTransform transform,
                        final BooleanProperty showProtractor,
                        final BooleanProperty showNormal,
                        final IntensityMeter intensityMeter,
                        final VelocitySensor velocitySensor,
                        final WaveSensor waveSensor,
                        final ResetModel resetModel ) {
        super( 2 );
        final PText titleLabel = new PText( "Toolbox" ) {{
            setFont( BendingLightCanvas.labelFont );
        }};
        addChild( titleLabel );
        final int ICON_WIDTH = 110;
        addChild( new Tool( multiScaleToWidth( RESOURCES.getImage( "protractor.png" ), ICON_WIDTH ), showProtractor,
                            transform, this, canvas, new Tool.NodeFactory() {
                    public DraggableNode createNode( ModelViewTransform transform, Property<Boolean> showTool, Point2D model ) {
                        return new ProtractorNode( transform, showTool, new ProtractorModel( model.getX(), model.getY() ), new Function2<Shape, Shape, Shape>() {
                            public Shape apply( Shape innerBar, final Shape outerCircle ) {
                                return new Area( innerBar ) {{add( new Area( outerCircle ) );}};
                            }
                        }, new Function2<Shape, Shape, Shape>() {
                            public Shape apply( Shape innerBar, Shape outerCircle ) {
                                return new Rectangle2D.Double( 0, 0, 0, 0 );//empty shape since shouldn't be rotatable in this tab
                            }
                        }, 1 );
                    }
                }, resetModel ) );

        if ( velocitySensor != null ) {
            final VelocitySensorNode velocitySensorNode = new VelocitySensorNode( transform, new VelocitySensor() );
            final Property<Boolean> showVelocitySensor = new Property<Boolean>( false );
            resetModel.addResetListener( new VoidFunction0() {
                public void apply() {
                    showVelocitySensor.reset();
                }
            } );
            addChild( new Tool( velocitySensorNode.toImage( ICON_WIDTH, (int) ( velocitySensorNode.getFullBounds().getHeight() / velocitySensorNode.getFullBounds().getWidth() * ICON_WIDTH ), new Color( 0, 0, 0, 0 ) ),
                                showVelocitySensor,
                                transform, this, canvas, new Tool.NodeFactory() {
                        public DraggableNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D model ) {
                            velocitySensor.position.setValue( new ImmutableVector2D( model ) );
                            return new VelocitySensorNode( transform, velocitySensor ) {{
                                showTool.addObserver( new VoidFunction1<Boolean>() {
                                    public void apply( Boolean visible ) {
                                        setVisible( visible );
                                    }
                                } );
                            }};
                        }
                    }, resetModel ) );
        }

        if ( waveSensor != null ) {
            final Function1.Constant<ImmutableVector2D, Option<Double>> value = new Function1.Constant<ImmutableVector2D, Option<Double>>( new Option.None<Double>() );
            final WaveSensorNode waveSensorNode = new WaveSensorNode( transform, new WaveSensor( new ConstantDtClock(), value, value ) );
            resetModel.addResetListener( new VoidFunction0() {
                public void apply() {
                    waveSensor.visible.reset();
                }
            } );
            addChild( new Tool( waveSensorNode.toImage( ICON_WIDTH, (int) ( waveSensorNode.getFullBounds().getHeight() / waveSensorNode.getFullBounds().getWidth() * ICON_WIDTH ), new Color( 0, 0, 0, 0 ) ),
                                waveSensor.visible,
                                transform, this, canvas, new Tool.NodeFactory() {
                        public DraggableNode createNode( ModelViewTransform transform, final Property<Boolean> showTool, final Point2D model ) {
                            waveSensor.translateToHotSpot( model );
                            return new WaveSensorNode( transform, waveSensor ) {{
                                showTool.addObserver( new SimpleObserver() {
                                    public void update() {
                                        setVisible( showTool.getValue() );
                                    }
                                } );
                            }};
                        }
                    }, resetModel ) );
        }

        //TODO: some constants copied from BendingLightModel
        //TODO: functionality and some code copied from protractor toolbox item above
        final double modelWidth = CHARACTERISTIC_LENGTH * 62;
        final double modelHeight = modelWidth * 0.7;
        final IntensityMeterNode iconNode = new IntensityMeterNode( transform, new IntensityMeter( modelWidth * 0.3, -modelHeight * 0.3, modelWidth * 0.4, -modelHeight * 0.3 ) {{
            enabled.setValue( true );
        }} );
        int sensorIconHeight = (int) ( iconNode.getFullBounds().getHeight() / iconNode.getFullBounds().getWidth() * ICON_WIDTH );
        final PImage sensorThumbnail = new PImage( iconNode.toImage( ICON_WIDTH, sensorIconHeight, new Color( 0, 0, 0, 0 ) ) ) {{
            final PImage sensorThumbnailRef = this;
            addInputEventListener( new PBasicInputEventHandler() {
                IntensityMeterNode node = null;
                boolean intersect = false;

                public void mousePressed( PInputEvent event ) {
                    intensityMeter.enabled.setValue( true );
                    if ( node == null ) {
                        node = new IntensityMeterNode( transform, intensityMeter );
                        intensityMeter.sensorPosition.setValue( new ImmutableVector2D( modelWidth * 0.3, -modelHeight * 0.3 ) );
                        intensityMeter.bodyPosition.setValue( new ImmutableVector2D( modelWidth * 0.4, -modelHeight * 0.3 ) );
                        final PropertyChangeListener pcl = new PropertyChangeListener() {
                            public void propertyChange( PropertyChangeEvent evt ) {
                                if ( node != null ) {
                                    final Rectangle2D sensorBounds = node.getSensorGlobalFullBounds();
                                    final Rectangle2D bodyBounds = node.getBodyGlobalFullBounds();
                                    intersect = ToolboxNode.this.getGlobalFullBounds().contains( sensorBounds.getCenterX(), sensorBounds.getCenterY() ) ||
                                                ToolboxNode.this.getGlobalFullBounds().contains( bodyBounds.getCenterX(), bodyBounds.getCenterY() );
                                }
                            }
                        };
                        intensityMeter.enabled.addObserver( new SimpleObserver() {
                            public void update() {
                                sensorThumbnailRef.setVisible( !intensityMeter.enabled.getValue() );
                                if ( !intensityMeter.enabled.getValue() && node != null ) {//user closed it with the red 'x' button on the sensor body (also called when dragged back to toolbox, but that's okay)
                                    node.removePropertyChangeListener( pcl );
                                    intensityMeter.enabled.setValue( false );
                                    sensorThumbnailRef.setVisible( true );
                                    canvas.removeChildBehindLight( node );
                                    node = null;//signify that we should create + init a new one on next drag so that it drags from the right location.
                                }
                            }
                        } );
                        final ImmutableVector2D modelPt = new ImmutableVector2D( transform.viewToModel( event.getPositionRelativeTo( getParent().getParent().getParent() ) ) );
                        final ImmutableVector2D delta = modelPt.minus( intensityMeter.sensorPosition.getValue() );
                        intensityMeter.translateAll( new PDimension( delta.getX(), delta.getY() ) );
                        node.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, pcl );
                        node.addInputEventListener( new PBasicInputEventHandler() {
                            public void mouseReleased( PInputEvent event ) {
                                if ( intersect && node != null ) {
                                    node.removePropertyChangeListener( pcl );
                                    canvas.removeChildBehindLight( node );
                                    intensityMeter.enabled.setValue( false );
                                    sensorThumbnailRef.setVisible( true );
                                    node = null;
                                }
                            }
                        } );

                        canvas.addChildBehindLight( node );
                    }
                }

                public void mouseDragged( PInputEvent event ) {
                    node.doTranslate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }

                public void mouseReleased( PInputEvent event ) {
                    if ( intersect ) {
                        intensityMeter.enabled.setValue( false );
                        sensorThumbnailRef.setVisible( true );
//                        node.removePropertyChangeListener( pcl );//TODO: what to do about this?
                        canvas.removeChildBehindLight( node );
                        node = null;
                    }
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};
        addChild( sensorThumbnail );

        final PImage normalLineThumbnail = new PImage( new NormalLine( transform, modelHeight, 9, 30, 30 ).toImage( 5, 67, new Color( 0, 0, 0, 0 ) ) );

        final PSwing showNormalCheckBox = new PSwing( new PropertyCheckBox( "Show Normal", showNormal ) {{
            setFont( BendingLightCanvas.labelFont );
            setBackground( new Color( 0, 0, 0, 0 ) );
        }} );
        normalLineThumbnail.setOffset( Math.max( sensorThumbnail.getFullBounds().getMaxX(), showNormalCheckBox.getFullBounds().getMaxX() ) + 8, sensorThumbnail.getFullBounds().getMaxY() + 5 );
        showNormalCheckBox.setOffset( 0, normalLineThumbnail.getFullBounds().getCenterY() - showNormalCheckBox.getFullBounds().getHeight() / 2 );
        addChild( showNormalCheckBox );
        addChild( normalLineThumbnail );
        titleLabel.setOffset( getFullBounds().getWidth() / 2 - titleLabel.getFullBounds().getWidth() / 2, 0 );
    }

}