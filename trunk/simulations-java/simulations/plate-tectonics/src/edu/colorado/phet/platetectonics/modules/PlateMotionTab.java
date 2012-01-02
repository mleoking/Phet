// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.control.CrustChooserPanel;
import edu.colorado.phet.platetectonics.control.CrustPiece;
import edu.colorado.phet.platetectonics.control.OptionsPanel;
import edu.colorado.phet.platetectonics.control.PlayModePanel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel.PlateType;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionTab extends PlateTectonicsTab {

    public final Property<Boolean> isAutoMode = new Property<Boolean>( false );
    private CrustChooserPanel crustChooserPanel;
    private OrthoPiccoloNode crustChooserNode;
    private GuiNode crustPieceLayer;

    private final Property<Boolean> showLabels = new Property<Boolean>( false );
    private final Property<Boolean> showWater = new Property<Boolean>( false );

    public PlateMotionTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.PLATE_MOTION_TAB, 0.5f );
    }

    @Override public void initialize() {
        super.initialize();

        crustPieceLayer = new GuiNode( this );
        rootNode.addChild( crustPieceLayer );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -700000, 700000,
                                     -400000, 15000,
                                     -250000, 0 ),
                256, 256, 32 );

        // create the model and terrain
//        model = new AnimatedPlateModel( grid );
        setModel( new PlateMotionModel( grid.getBounds() ) );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        sceneLayer.addChild( new PlateView( getModel(), this, showWater ) );

        /*---------------------------------------------------------------------------*
         * manual / automatic switch
         *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode( new ControlPanelNode( new PlayModePanel( isAutoMode ) ), this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D( 10, 10 ) ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
         * crust chooser
         *----------------------------------------------------------------------------*/
        crustChooserPanel = new CrustChooserPanel();
        crustChooserNode = new OrthoPiccoloNode( new ControlPanelNode( crustChooserPanel ), this, getCanvasTransform(),
                                                 new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            getStageSize().width - getComponentWidth() - 10, // right side
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from bottom
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( crustChooserNode );

        // continental piece
        final CrustPiece continentalPiece = new CrustPiece( PlateType.CONTINENTAL, CrustChooserPanel.CRUST_AREA_MAX_HEIGHT, 0.8f );
        OrthoPiccoloNode continentalPieceNode = new OrthoPiccoloNode(
                continentalPiece, this, getCanvasTransform(),
                new Property<ImmutableVector2D>(
                        new ImmutableVector2D(
                                getContinentalOffset().x - continentalPiece.getFullBounds().getWidth() / 2,
                                getContinentalOffset().y - continentalPiece.getFullBounds().getHeight() / 2 ) ),
                mouseEventNotifier );
        crustPieceLayer.addChild( continentalPieceNode );
        guiNodes.add( 0, continentalPieceNode );

        // young oceanic piece
        final CrustPiece youngOceanicPiece = new CrustPiece( PlateType.YOUNG_OCEANIC, 35, 0.5f );
        OrthoPiccoloNode youngOceanicPieceNode = new OrthoPiccoloNode(
                youngOceanicPiece, this, getCanvasTransform(),
                new Property<ImmutableVector2D>(
                        new ImmutableVector2D(
                                getYoungOceanicOffset().x - youngOceanicPiece.getFullBounds().getWidth() / 2,
                                getYoungOceanicOffset().y - youngOceanicPiece.getFullBounds().getHeight() / 2 ) ),
                mouseEventNotifier );
        crustPieceLayer.addChild( youngOceanicPieceNode );
        guiNodes.add( 0, youngOceanicPieceNode );

        // old oceanic piece
        final CrustPiece oldOceanicPiece = new CrustPiece( PlateType.OLD_OCEANIC, 35, 0.4f );
        OrthoPiccoloNode oldOceanicPieceNode = new OrthoPiccoloNode(
                oldOceanicPiece, this, getCanvasTransform(),
                new Property<ImmutableVector2D>(
                        new ImmutableVector2D(
                                getOldOceanicOffset().x - oldOceanicPiece.getFullBounds().getWidth() / 2,
                                getOldOceanicOffset().y - oldOceanicPiece.getFullBounds().getHeight() / 2 ) ),
                mouseEventNotifier );
        crustPieceLayer.addChild( oldOceanicPieceNode );
        guiNodes.add( 0, oldOceanicPieceNode );

        /*---------------------------------------------------------------------------*
        * options panel
        *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode(
                new ControlPanelNode( new OptionsPanel( showLabels, true, showWater ) ),
                this, getCanvasTransform(),
                new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                @Override public void update() {
                    int center = (int) ( ( toolbox.position.get().getX() + toolbox.getComponentWidth() )
                                         + ( crustChooserNode.position.get().getX() ) ) / 2;
                    position.set( new ImmutableVector2D( center - getComponentWidth() / 2,
                                                         getStageSize().height - getComponentHeight() - 10 ) );
                }
            } );
            updateOnEvent( beforeFrameRender );
        }} );
    }

    private ImmutableVector2F getCrustOffset( ImmutableVector2F pieceOffset ) {
        ImmutableVector2D nodeOffset = crustChooserNode.position.get();
        return new ImmutableVector2F( (float) nodeOffset.getX() + pieceOffset.x,
                                      (float) nodeOffset.getY() + pieceOffset.y );
    }

    private ImmutableVector2F getContinentalOffset() {
        return getCrustOffset( crustChooserPanel.getContinentalCenter() );
    }

    private ImmutableVector2F getYoungOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getYoungOceanicCenter() );
    }

    private ImmutableVector2F getOldOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getOldOceanicCenter() );
    }

    public PlateMotionModel getPlateMotionModel() {
        return (PlateMotionModel) getModel();
    }

    @Override public void droppedCrustPiece( OrthoPiccoloNode crustPieceNode ) {
        PlateMotionModel model = getPlateMotionModel();
        CrustPiece piece = (CrustPiece) crustPieceNode.getNode();

        boolean droppedBackInPanel = isGuiUnder( crustChooserNode, Mouse.getEventX(), Mouse.getEventY() );

        if ( droppedBackInPanel ) {
            crustPieceNode.position.reset();
        }
        else {
            if ( crustPieceNode.position.get().getX() < getStageSize().getWidth() / 2 ) {
                if ( !model.hasLeftPlate() ) {
                    model.dropLeftCrust( piece.type );
                    crustPieceNode.getParent().removeChild( crustPieceNode );
                    guiNodes.remove( crustPieceNode );
                }
                else {
                    crustPieceNode.position.reset();
                }
            }
            else {
                if ( !model.hasRightPlate() ) {
                    model.dropRightCrust( piece.type );
                    crustPieceNode.getParent().removeChild( crustPieceNode );
                    guiNodes.remove( crustPieceNode );
                }
                else {
                    crustPieceNode.position.reset();
                }
            }
        }
    }
}
