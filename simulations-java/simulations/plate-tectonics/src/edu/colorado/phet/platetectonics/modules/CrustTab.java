// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing;
import edu.colorado.phet.platetectonics.control.DraggableTool2D;
import edu.colorado.phet.platetectonics.control.LegendPanel;
import edu.colorado.phet.platetectonics.control.MyCrustPanel;
import edu.colorado.phet.platetectonics.control.ViewOptionsPanel;
import edu.colorado.phet.platetectonics.control.ZoomPanel;
import edu.colorado.phet.platetectonics.model.CrustModel;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.ColorMode;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.colorado.phet.platetectonics.view.labels.RangeLabelNode;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.CONTINENTAL_CRUST;
import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.OCEANIC_CRUST;

/**
 * Represents the 1st tab, which has a modifiable section of crust surrounded by oceanic and continental crusts, all
 * on top of the mantle.
 */
public class CrustTab extends PlateTectonicsTab {

    // relative scale multiplier of how large items at the origin appear to be
    private Property<Float> scaleProperty = new Property<Float>( 1f );

    private final Property<Boolean> showLabels = new Property<Boolean>( false );
    private OrthoPiccoloNode optionsPiccoloNode;

    public CrustTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.CRUST_TAB, 2 ); // 0.5 km => 1 distance in view

        zoomRatio.addObserver( new SimpleObserver() {
            public void update() {
                scaleProperty.set( getSceneDistanceZoomFactor() );
            }
        } );
    }

    @Override public void initialize() {
        super.initialize();

        getClock().start();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -1500000, 1500000,
                                     -150000, 15000,
                                     -2000000, 0 ),
                512, 512, 64 );

        // create the model and terrain
        setModel( new CrustModel( grid ) );

        sceneLayer.addChild( new PlateView( getModel(), this ) );

        final Function1<Vector3F, Vector3F> flatModelToView = new Function1<Vector3F, Vector3F>() {
            public Vector3F apply( Vector3F v ) {
                return getModelViewTransform().transformPosition( PlateModel.convertToRadial( v ) );
            }
        };

        GLNode layerLabels = new GLNode() {{
            showLabels.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showLabels.get() );
                }
            } );
        }};
        sceneLayer.addChild( layerLabels );

        /*---------------------------------------------------------------------------*
        * cross-section labels
        *----------------------------------------------------------------------------*/

        // crust label
        layerLabels.addChild( new RangeLabelNode( new Property<Vector3F>( new Vector3F() ) {{
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    set( flatModelToView.apply( new Vector3F( -10000, (float) getCrustModel().getCenterCrustElevation(), 0 ) ) );
                }
            }, true );
        }}, new Property<Vector3F>( new Vector3F() ) {{
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    set( flatModelToView.apply( new Vector3F( -10000, (float) getCrustModel().getCenterCrustBottomY(), 0 ) ) );
                }
            }, true );
        }}, Strings.CRUST, scaleProperty, colorMode, true
        ) );

        final Property<Vector3F> upperMantleTop = new Property<Vector3F>( new Vector3F() ) {{
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    set( flatModelToView.apply( new Vector3F( 0, (float) getCrustModel().getCenterCrustBottomY(), 0 ) ) );
                }
            }, true );
        }};
        final Property<Vector3F> upperMantleBottom = new Property<Vector3F>( flatModelToView.apply( new Vector3F( 0, CrustModel.UPPER_LOWER_MANTLE_BOUNDARY_Y, 0 ) ) );

        // mantle
        layerLabels.addChild( new RangeLabelNode(
                upperMantleTop,
                upperMantleBottom,
                Strings.MANTLE, scaleProperty,
                colorMode, true,
                getLabelPosition( upperMantleTop, upperMantleBottom, scaleProperty )
        ) );

        Property<Vector3F> lowerMantleTop = new Property<Vector3F>( flatModelToView.apply( new Vector3F( 150000, CrustModel.UPPER_LOWER_MANTLE_BOUNDARY_Y, 0 ) ) );
        Property<Vector3F> lowerMantleBottom = new Property<Vector3F>( flatModelToView.apply( new Vector3F( 150000, CrustModel.MANTLE_CORE_BOUNDARY_Y, 0 ) ) );

        // lower mantle
        layerLabels.addChild( new RangeLabelNode(
                lowerMantleTop,
                lowerMantleBottom,
                Strings.LOWER_MANTLE, scaleProperty,
                colorMode, true,
                getLabelPosition( lowerMantleTop, lowerMantleBottom, scaleProperty )
        ) );

        Property<Vector3F> outerCoreTop = new Property<Vector3F>( flatModelToView.apply( new Vector3F( -250000, CrustModel.MANTLE_CORE_BOUNDARY_Y, 0 ) ) );
        Property<Vector3F> outerCoreBottom = new Property<Vector3F>( flatModelToView.apply( new Vector3F( -250000, CrustModel.INNER_OUTER_CORE_BOUNDARY_Y, 0 ) ) );

        // outer core
        layerLabels.addChild( new RangeLabelNode(
                outerCoreTop,
                outerCoreBottom,
                Strings.OUTER_CORE, scaleProperty,
                colorMode, false,
                getLabelPosition( outerCoreTop, outerCoreBottom, scaleProperty )
        ) );

        Property<Vector3F> innerCoreTop = new Property<Vector3F>( flatModelToView.apply( new Vector3F( 250000, CrustModel.INNER_OUTER_CORE_BOUNDARY_Y, 0 ) ) );
        Property<Vector3F> innerCoreBottom = new Property<Vector3F>( flatModelToView.apply( new Vector3F( 250000, -PlateModel.EARTH_RADIUS, 0 ) ) );

        // inner core
        layerLabels.addChild( new RangeLabelNode(
                innerCoreTop,
                innerCoreBottom,
                Strings.INNER_CORE, scaleProperty,
                colorMode, false,
                getLabelPosition( innerCoreTop, innerCoreBottom, scaleProperty )
        ) );

        /*---------------------------------------------------------------------------*
        * my crust
        *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode( new ControlPanelNode( new MyCrustPanel( getCrustModel() ) ), CrustTab.this, getCanvasTransform(), new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new Vector2D(
                            Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                            10 ) ); // offset from top
                }
            } );

            updateOnEvent( beforeFrameRender );

            zoomRatio.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( zoomRatio.get() == 1 );
                }
            } );
        }} );

        /*---------------------------------------------------------------------------*
        * zoom control
        *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode( new ControlPanelNode( new ZoomPanel( zoomRatio ) ), CrustTab.this, getCanvasTransform(), new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            // top right
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new Vector2D(
                            getStageSize().width - getComponentWidth() - 10,
                            10
                    ) );
                }
            } );

            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
         * options panel
         *----------------------------------------------------------------------------*/
        optionsPiccoloNode = new OrthoPiccoloNode(
                new ControlPanelNode( new ViewOptionsPanel( this, showLabels, colorMode ) ),
                CrustTab.this, getCanvasTransform(),
                new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new Vector2D( getStageSize().width - getComponentWidth() - 10,
                                                getStageSize().height - getComponentHeight() - 10 ) );
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( optionsPiccoloNode );

        /*---------------------------------------------------------------------------*
        * legend
        *----------------------------------------------------------------------------*/

        addGuiNode( new LegendPiccoloNode( ColorMode.DENSITY, (float) optionsPiccoloNode.position.get().getX() ) );
        addGuiNode( new LegendPiccoloNode( ColorMode.TEMPERATURE, (float) optionsPiccoloNode.position.get().getX() ) );

        /*---------------------------------------------------------------------------*
        * crust labels labels
        *----------------------------------------------------------------------------*/

        // "oceanic crust" label
        guiLayer.addChild(
                new OrthoPiccoloNode( new PText( OCEANIC_CRUST ) {{
                    setFont( new PhetFont( 16, true ) );
                }},
                                      this,
                                      getCanvasTransform(),
                                      new Property<Vector2D>( new Vector2D( 30, getStageSize().getHeight() * 0.38 ) ),
                                      mouseEventNotifier ) {{
                    zoomRatio.addObserver( new SimpleObserver() {
                        public void update() {
                            setVisible( zoomRatio.get() == 1 );
                        }
                    } );
                }} );

        // "continental crust" label
        guiLayer.addChild( new OrthoPiccoloNode( new PText( CONTINENTAL_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, this, getCanvasTransform(), new Property<Vector2D>( new Vector2D() ), mouseEventNotifier ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new Vector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                        getStageSize().getHeight() * 0.38 ) );
            zoomRatio.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( zoomRatio.get() == 1 );
                }
            } );
        }} );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        // if we zoom in such a way that a tool is not visible, move it into the toolbox
        zoomRatio.addObserver( new SimpleObserver() {
            public void update() {
                // TODO: make getCameraRay invertible so we can actually compute this better
                Vector2F viewBottom = getViewPositionOnZPlane( 0.5f, 0 );
                Vector2F viewLeft = getViewPositionOnZPlane( 0, 0.5f );
                Vector2F viewRight = getViewPositionOnZPlane( 1, 0.5f );

                for ( GLNode glNode : new ArrayList<GLNode>( toolLayer.getChildren() ) ) {
                    DraggableTool2D tool = (DraggableTool2D) glNode;
                    Vector3F sensorViewPosition = tool.getSensorViewPosition();
                    if ( sensorViewPosition.getY() < viewBottom.getY()
                         || sensorViewPosition.getX() > viewRight.getX()
                         || sensorViewPosition.getX() < viewLeft.getX() ) {
                        toolDragHandler.putToolBackInToolbox( tool );
                    }
                }
            }
        } );
    }

    @Override public void resetAll() {
        super.resetAll();

        showLabels.reset();
    }

    public CrustModel getCrustModel() {
        return (CrustModel) getModel();
    }

    public IUserComponent getUserComponent() {
        return PlateTectonicsSimSharing.UserComponents.crustTab;
    }

    private class LegendPiccoloNode extends OrthoPiccoloNode {
        public LegendPiccoloNode( final ColorMode myColorMode, final float optionsRightX ) {
            super( new ControlPanelNode( new LegendPanel( myColorMode ) ), CrustTab.this, CrustTab.this.getCanvasTransform(), new Property<Vector2D>( new Vector2D() ), CrustTab.this.mouseEventNotifier );

            // NOTE: no updating is required on this node, since it doesn't change
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new Vector2D( optionsRightX - getComponentWidth() - 20,
                                                getStageSize().height - getComponentHeight() - 10 ) );
                }
            } );
            colorMode.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( colorMode.get() == myColorMode );
                }
            } );
        }
    }
}
