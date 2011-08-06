// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.INSET;

/**
 * Canvas for the Water tab
 *
 * @author Sam Reid
 */
public class WaterCanvas extends PhetPCanvas {

    //Default size of the canvas.  Sampled at runtime on a large res screen from a sim with multiple tabs
    public static final Dimension canvasSize = new Dimension( 1008, 676 );

    //Where the content is shown
    private PNode rootNode = new PNode();

    //Separate layer for the particles so they are always behind the control panel
    private ParticleWindowNode particleWindowNode;

    //Views for the salt and sugar bucket
    private BucketView saltBucket;
    private BucketView sugarBucket;

    //Layers for salt and sugar buckets
    private PNode saltBucketParticleLayer;
    private PNode sugarBucketParticleLayer;

    //Control panel with user options
    protected WaterControlPanel waterControlPanel;

    //Dialog in which to show the 3d JMol view of sucrose
    private Sucrose3DDialog sucrose3DDialog;

    //Model view transform from model to stage coordinates
    protected final ModelViewTransform transform;

    public WaterCanvas( final WaterModel waterModel, final GlobalState state ) {
        sucrose3DDialog = new Sucrose3DDialog( state.frame );
        //Use the background color specified in the backgroundColor, since it is changeable in the developer menu
        state.colorScheme.backgroundColorSet.color.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                setBackground( color );
            }
        } );

        //Set the stage size according to the same aspect ratio as used in the model

        //Gets the ModelViewTransform used to go between model coordinates (SI) and stage coordinates (roughly pixels)
        //Create the transform from model (SI) to view (stage) coordinates
        double inset = 40;
        transform = createRectangleInvertedYMapping( new Rectangle2D.Double( -waterModel.beakerWidth / 2, 0, waterModel.beakerWidth, waterModel.beakerHeight ),
                                                     new Rectangle2D.Double( -inset, -inset, canvasSize.getWidth() + inset * 2, canvasSize.getHeight() + inset * 2 ) );

        // Root of our scene graph
        addWorldChild( rootNode );

        //Add the region with the particles
        particleWindowNode = new ParticleWindowNode( waterModel, transform ) {{
            setOffset( canvasSize.getWidth() - getFullBounds().getWidth() - 50, 0 );
        }};
        rootNode.addChild( particleWindowNode );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, canvasSize ) );

        final MiniBeakerNode miniBeakerNode = new MiniBeakerNode() {{
            translate( 0, 300 );
        }};
        addChild( miniBeakerNode );

        //Show a graphic that shows the particle frame to be a zoomed in part of the mini beaker
        addChild( new ZoomIndicatorNode( new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return state.colorScheme.whiteBackground.get() ? Color.blue : Color.yellow;
            }
        }, state.colorScheme.whiteBackground ), miniBeakerNode, particleWindowNode ) );

        //Control panel
        waterControlPanel = new WaterControlPanel( waterModel, state, this, sucrose3DDialog ) {{
            setOffset( INSET, canvasSize.getHeight() - getFullBounds().getHeight() - INSET * 10 );
        }};
        addChild( waterControlPanel );

        //DEBUGGING
//        waterModel.k.trace( "k" );
//        waterModel.pow.trace( "pow" );
//        waterModel.randomness.trace( "randomness" );

        //Add a bucket with salt that can be dragged into the play area
        //The transform must have inverted Y so the bucket is upside-up.
        final Rectangle referenceRect = new Rectangle( 0, 0, 1, 1 );

        saltBucket = new BucketView( new Bucket( new Point2D.Double( canvasSize.getWidth() / 2, -canvasSize.getHeight() + 115 ), new Dimension2DDouble( 200, 130 ), Color.blue, SugarAndSaltSolutionsResources.Strings.SALT ), ModelViewTransform.createRectangleInvertedYMapping( referenceRect, referenceRect ) );
        addChild( saltBucket.getHoleNode() );

        saltBucketParticleLayer = new PNode();
        addChild( saltBucketParticleLayer );
        addChild( saltBucket.getFrontNode() );

        addSaltToBucket( waterModel, transform );

        sugarBucket = new BucketView( new Bucket( new Point2D.Double( canvasSize.getWidth() / 2 + 210, -canvasSize.getHeight() + 115 ), new Dimension2DDouble( 200, 130 ), Color.green, SugarAndSaltSolutionsResources.Strings.SUGAR ), ModelViewTransform.createRectangleInvertedYMapping( referenceRect, referenceRect ) );
        addChild( sugarBucket.getHoleNode() );

        sugarBucketParticleLayer = new PNode();
        addChild( sugarBucketParticleLayer );
        addChild( sugarBucket.getFrontNode() );

        addSugarToBucket( waterModel, transform );

        waterModel.addResetListener( new VoidFunction0() {
            public void apply() {
                addSaltToBucket( waterModel, transform );
                addSugarToBucket( waterModel, transform );
            }
        } );
    }

    //Called when the user switches to the water tab from another tab.  Remembers if the JMolDialog was showing and restores it if so
    public void moduleActivated() {
        sucrose3DDialog.moduleActivated();
    }

    //Called when the user switches to another tab.  Stores the state of the jmol dialog so that it can be restored when the user comes back to this tab
    public void moduleDeactivated() {
        sucrose3DDialog.moduleDeactivated();
    }

    public void addSaltToBucket( final WaterModel waterModel, final ModelViewTransform transform ) {
        saltBucketParticleLayer.removeAllChildren();
        saltBucketParticleLayer.addChild( new DraggableSaltCrystalNode( waterModel, transform, particleWindowNode ) {{
            centerFullBoundsOnPoint( saltBucket.getHoleNode().getFullBounds().getCenterX(), saltBucket.getHoleNode().getFullBounds().getCenterY() );
        }} );
    }

    public void addSugarToBucket( final WaterModel waterModel, final ModelViewTransform transform ) {
        sugarBucketParticleLayer.removeAllChildren();
        sugarBucketParticleLayer.addChild( new DraggableSugarCrystalNode( waterModel, transform, particleWindowNode, waterModel.showSugarAtoms ) {{
            centerFullBoundsOnPoint( sugarBucket.getHoleNode().getFullBounds().getCenterX(), sugarBucket.getHoleNode().getFullBounds().getCenterY() );
        }} );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public ModelViewTransform getModelViewTransform() {
        return transform;
    }
}