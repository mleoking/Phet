// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.SlowMotionNormalTimeControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.intro.model.Thermometer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.normalMotionRadioButton;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.slowMotionRadioButton;

/**
 * Piccolo canvas for the "Intro" tab of the Energy Forms and Changes
 * simulation.
 *
 * @author John Blanco
 */
public class EFACIntroCanvas extends PhetPCanvas implements Resettable {

    public static Dimension2D STAGE_SIZE = CenteredStage.DEFAULT_STAGE_SIZE;
    private static double EDGE_INSET = 10;
    private static final Color CONTROL_PANEL_COLOR = new Color( 255, 255, 224 );

    private final EFACIntroModel model;
    private ThermometerToolBox thermometerToolBox;
    private BooleanProperty normalSimSpeed = new BooleanProperty( true );

    /**
     * Constructor.
     *
     * @param model
     */
    public EFACIntroCanvas( final EFACIntroModel model ) {
        this.model = model;

        // Set up the canvas-screen transform.
        setWorldTransformStrategy( new CenteredStage( this ) );

        // Set up the model-canvas transform.
        //
        // IMPORTANT NOTES: The multiplier factors for the 2nd point can be
        // adjusted to shift the center right or left, and the scale factor
        // can be adjusted to zoom in or out (smaller numbers zoom out, larger
        // ones zoom in).
        final ModelViewTransform mvt = ModelViewTransform.createSinglePointScaleInvertedYMapping(
                new Point2D.Double( 0, 0 ),
                new Point( (int) Math.round( STAGE_SIZE.getWidth() * 0.5 ), (int) Math.round( STAGE_SIZE.getHeight() * 0.85 ) ),
                2200 ); // "Zoom factor" - smaller zooms out, larger zooms in.

        setBackground( new Color( 245, 246, 247 ) );

        // Set up a root node for our scene graph.
        final PNode rootNode = new PNode();
        addWorldChild( rootNode );

        // Create some PNodes that will act as layers in order to create the
        // needed Z-order behavior.
        final PNode backLayer = new PNode();
        rootNode.addChild( backLayer );
        PNode blockLayer = new PNode();
        rootNode.addChild( blockLayer );
        final PNode beakerFrontLayer = new PNode();
        rootNode.addChild( beakerFrontLayer );
        final PNode energyChuckLayer = new PNode();
        rootNode.addChild( energyChuckLayer );
        final PNode thermometerLayer = new PNode();
        rootNode.addChild( thermometerLayer );

        // Add the control for showing/hiding object energy. TODO: i18n
        {
            PropertyCheckBox showEnergyCheckBox = new PropertyCheckBox( EnergyFormsAndChangesSimSharing.UserComponents.showEnergyCheckBox,
                                                                        "Show energy of objects",
                                                                        model.energyChunksVisible );
            showEnergyCheckBox.setFont( new PhetFont( 20 ) );
            backLayer.addChild( new ControlPanelNode( new PSwing( showEnergyCheckBox ), CONTROL_PANEL_COLOR ) {{
                setOffset( STAGE_SIZE.getWidth() - getFullBoundsReference().width - EDGE_INSET, EDGE_INSET );
            }} );
        }

        // Add the lab bench surface.
        final PNode labBenchSurface = new PImage( EnergyFormsAndChangesResources.Images.SHELF_LONG );
        labBenchSurface.setOffset( mvt.modelToViewX( 0 ) - labBenchSurface.getFullBoundsReference().getWidth() / 2,
                                   mvt.modelToViewY( 0 ) - labBenchSurface.getFullBoundsReference().getHeight() / 2 + 10 ); // Slight tweak factor here due to nature of image.
        backLayer.addChild( labBenchSurface );

        // Add a node that will act as the background below the lab bench
        // surface, basically like the side of the bench.
        {
            double width = labBenchSurface.getFullBoundsReference().getWidth() * 0.95;
            double height = 1000; // Arbitrary large number, user should never see the bottom of this.
            Shape benchSupportShape = new Rectangle2D.Double( labBenchSurface.getFullBoundsReference().getCenterX() - width / 2,
                                                              labBenchSurface.getFullBoundsReference().getCenterY(),
                                                              width,
                                                              height );
            PhetPPath labBenchSide = new PhetPPath( benchSupportShape, new Color( 120, 120, 120 ) );
            backLayer.addChild( labBenchSide );
            labBenchSide.moveToBack(); // Must be behind bench top.
        }

        // Calculate the vertical center between the lower edge of the top of
        // the bench and the bottom of the canvas.  This is for layout.
        double centerYBelowSurface = ( STAGE_SIZE.getHeight() + labBenchSurface.getFullBoundsReference().getMaxY() ) / 2;

        // Add the clock controls. TODO: i18n
        {
            PNode clockControl = new SlowMotionNormalTimeControlPanel( slowMotionRadioButton, "Slow Motion", "Normal",
                                                                       normalMotionRadioButton, normalSimSpeed, model.getClock() );
            clockControl.centerFullBoundsOnPoint( STAGE_SIZE.getWidth() / 2, centerYBelowSurface );
            normalSimSpeed.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean normalSimSpeed ) {
                    ConstantDtClock clock = (ConstantDtClock) model.getClock();
                    clock.setDt( normalSimSpeed ? EFACIntroModel.SIM_TIME_PER_TICK_NORMAL : EFACIntroModel.SIM_TIME_PER_TICK_SLOW_MOTION );
                }
            } );
            backLayer.addChild( clockControl );
        }

        // Add the reset button.
        {
            ResetAllButtonNode resetButton = new ResetAllButtonNode( this, this, 20, Color.black, new Color( 255, 153, 0 ) );
            resetButton.setConfirmationEnabled( false );
            resetButton.setOffset( STAGE_SIZE.getWidth() - resetButton.getFullBoundsReference().width - 20,
                                   centerYBelowSurface - resetButton.getFullBoundsReference().getHeight() / 2 );
            backLayer.addChild( resetButton );
        }

        // Add the burners.
        backLayer.addChild( new BurnerNode( model.getLeftBurner(), mvt ) );
        backLayer.addChild( new BurnerNode( model.getRightBurner(), mvt ) );

        // Add the movable objects.
        final PNode brickNode = new BlockNode( model, model.getBrick(), mvt );
        blockLayer.addChild( brickNode );
        final PNode leadNode = new BlockNode( model, model.getIronBlock(), mvt );
        blockLayer.addChild( leadNode );
        BeakerView beakerView = new BeakerView( model, this, mvt );
        beakerFrontLayer.addChild( beakerView.getFrontNode() );
        backLayer.addChild( beakerView.getBackNode() );

        // Monitor the model for the comings and goings of energy chunks and
        // add/remove view representations accordingly.
        model.energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( EnergyChunk energyChunk ) {
                final PNode energyChunkNode = new EnergyChunkNode( energyChunk, mvt );
                energyChuckLayer.addChild( energyChunkNode );
                model.energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk energyChunk ) {
                        energyChuckLayer.removeChild( energyChunkNode );
                        model.energyChunkList.removeElementRemovedObserver( this );
                    }
                } );
            }
        } );

        // Add the tool box for the thermometers.
        thermometerToolBox = new ThermometerToolBox( model, mvt, CONTROL_PANEL_COLOR );
        thermometerToolBox.setOffset( EDGE_INSET, EDGE_INSET );
        backLayer.addChild( thermometerToolBox );

        // Add the thermometers.
        for ( Thermometer thermometer : model.getThermometers() ) {
            thermometerToolBox.putThermometerInOpenSpot( thermometer );
            // Add one thermometer node to the front layer and one to the back,
            // and control the visibility based on whether the thermometer is
            // in the tool box.
            final ThermometerNode frontThermometerNode = new ThermometerNode( thermometer, mvt );
            thermometerLayer.addChild( frontThermometerNode );
            final ThermometerNode backThermometerNode = new ThermometerNode( thermometer, mvt );
            backLayer.addChild( backThermometerNode );
            frontThermometerNode.addInputEventListener( new PBasicInputEventHandler() {

                @Override public void mouseReleased( PInputEvent event ) {
                    if ( frontThermometerNode.getFullBoundsReference().intersects( thermometerToolBox.getFullBoundsReference() ) ) {
                        // Released over tool box, so put the thermometer into it.
                        thermometerToolBox.putThermometerInOpenSpot( frontThermometerNode.getThermometer() );
                    }
                }
            } );

            // Monitor the thermometer's position and move it to the back of
            // the z-order when over the tool box.
            thermometer.position.addObserver( new VoidFunction1<ImmutableVector2D>() {
                public void apply( ImmutableVector2D position ) {
                    if ( mvt.viewToModel( thermometerToolBox.getFullBoundsReference() ).contains( position.toPoint2D() ) && frontThermometerNode.getTransparency() > 0 ) {
                        frontThermometerNode.setTransparency( 0 );
                    }
                    else if ( !mvt.viewToModel( thermometerToolBox.getFullBoundsReference() ).contains( position.toPoint2D() ) && frontThermometerNode.getTransparency() == 0 ) {
                        frontThermometerNode.setTransparency( 1 );
                    }
                }
            } );
        }

        // Create an observer that updates the Z-order of the blocks when the
        // user controlled state changes.
        SimpleObserver blockChangeObserver = new SimpleObserver() {
            public void update() {
                if ( model.getIronBlock().isStackedUpon( model.getBrick() ) ) {
                    brickNode.moveToBack();
                }
                else if ( model.getBrick().isStackedUpon( model.getIronBlock() ) ) {
                    leadNode.moveToBack();
                }
                else if ( model.getIronBlock().getRect().getMinX() >= model.getBrick().getRect().getMaxX() ||
                          model.getIronBlock().getRect().getMinY() >= model.getBrick().getRect().getMaxY() ) {
                    leadNode.moveToFront();
                }
                else if ( model.getBrick().getRect().getMinX() >= model.getIronBlock().getRect().getMaxX() ||
                          model.getBrick().getRect().getMinY() >= model.getIronBlock().getRect().getMaxY() ) {
                    brickNode.moveToFront();
                }
            }
        };

        // Update the Z-order of the blocks whenever the "userControlled" state
        // of either changes.
        model.getBrick().position.addObserver( blockChangeObserver );
        model.getIronBlock().position.addObserver( blockChangeObserver );
    }

    public void reset() {
        model.reset();
        normalSimSpeed.reset();
        // Put the thermometers in the tool box.
        for ( Thermometer thermometer : model.getThermometers() ) {
            thermometerToolBox.putThermometerInOpenSpot( thermometer );
        }
    }

    // Class that defines the thermometer tool box.
    private static class ThermometerToolBox extends PNode {

        private static Font TITLE_FONT = new PhetFont( 20, false );
        private static int NUM_THERMOMETERS_SUPPORTED = 2;

        private final EFACIntroModel model;
        private final ModelViewTransform mvt;

        private ThermometerToolBox( EFACIntroModel model, ModelViewTransform mvt, Color backgroundColor ) {
            this.model = model;
            this.mvt = mvt;
            PNode title = new PhetPText( "Tool Box", TITLE_FONT );
            addChild( title );
            double thermometerHeight = EnergyFormsAndChangesResources.Images.THERMOMETER_BACK.getHeight( null );
            double thermometerWidth = EnergyFormsAndChangesResources.Images.THERMOMETER_BACK.getWidth( null );
            PhetPPath thermometerRegion = new PhetPPath( new Rectangle2D.Double( 0, 0, thermometerHeight * 1.1, thermometerWidth * 3 ), new Color( 0, 0, 0, 0 ) );
            addChild( new ControlPanelNode( new VBox( 0, title, thermometerRegion ), backgroundColor ) );
        }

        public void putThermometerInOpenSpot( Thermometer thermometer ) {
            // This is a little tweaky due to the relationship between the
            // thermometer in the model and the view representation.
            double xPos = 30;
            double yPos = getFullBoundsReference().getMaxY() - 30;
            boolean openLocationFound = false;
            for ( int i = 0; i < NUM_THERMOMETERS_SUPPORTED && !openLocationFound; i++ ) {
                xPos = getFullBoundsReference().width / NUM_THERMOMETERS_SUPPORTED * i + 20;
                openLocationFound = true;
                for ( Thermometer modelThermometer : model.getThermometers() ) {
                    if ( modelThermometer.position.get().distance( new ImmutableVector2D( mvt.viewToModel( xPos, yPos ) ) ) < 1E-3 ) {
                        openLocationFound = false;
                        break;
                    }
                }
            }
            thermometer.position.set( new ImmutableVector2D( mvt.viewToModel( xPos, yPos ) ) );
        }
    }

    // Event handler that returns thermometer to tool box if released above it.
    private static class ThermometerReturner extends PBasicInputEventHandler {

        private final ThermometerToolBox toolBox;
        private final ThermometerNode thermometerNode;

        private ThermometerReturner( ThermometerToolBox toolBox, ThermometerNode thermometerNode ) {
            this.thermometerNode = thermometerNode;
            this.toolBox = toolBox;
        }

        @Override public void mouseReleased( PInputEvent event ) {
            if ( thermometerNode.getFullBoundsReference().intersects( toolBox.getFullBoundsReference() ) ) {
                toolBox.putThermometerInOpenSpot( thermometerNode.getThermometer() );
            }
        }
    }
}
