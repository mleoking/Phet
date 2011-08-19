// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.Bucket;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.FloatingClockControlNode;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Constituent;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.ICanvas;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SaltIon;
import edu.colorado.phet.sugarandsaltsolutions.water.model.SodiumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterModel;
import edu.colorado.phet.sugarandsaltsolutions.water.model.WaterMolecule;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.ZERO;
import static edu.colorado.phet.common.phetcommon.model.property.Not.not;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.INSET;
import static edu.colorado.phet.sugarandsaltsolutions.micro.view.MicroCanvas.NO_READOUT;
import static java.awt.Color.blue;
import static java.awt.Color.green;

/**
 * Canvas for the Water tab
 *
 * @author Sam Reid
 */
public class WaterCanvas extends PhetPCanvas implements ICanvas {

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
    protected WaterControlPanel controlPanel;

    //Dialog in which to show the 3d JMol view of sucrose
    private Sucrose3DDialog sucrose3DDialog;

    //Model view transform from model to stage coordinates
    protected final ModelViewTransform transform;

    public WaterCanvas( final WaterModel model, final GlobalState state ) {
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
        ImmutableRectangle2D particleWindow = model.particleWindow;
        final double particleWindowWidth = canvasSize.getWidth() * 0.7;
        final double particleWindowHeight = model.particleWindow.height * particleWindowWidth / model.particleWindow.width;
        final double particleWindowX = canvasSize.getWidth() - inset - particleWindowWidth;
        final double particleWindowY = inset;
        transform = createRectangleInvertedYMapping( particleWindow.toRectangle2D(),
                                                     new Rectangle2D.Double( particleWindowX, particleWindowY, particleWindowWidth, particleWindowHeight ) );

        // Root of our scene graph
        addWorldChild( rootNode );

        //Add the region with the particles
        particleWindowNode = new ParticleWindowNode( model, transform );
        rootNode.addChild( particleWindowNode );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, canvasSize ) );

        //Create and add a small icon of the beaker to show that this tab is a zoomed in version of it
        final MiniBeakerNode miniBeakerNode = new MiniBeakerNode() {{translate( 0, 300 ); }};
        addChild( miniBeakerNode );

        //Show a graphic that shows the particle frame to be a zoomed in part of the mini beaker
        addChild( new ZoomIndicatorNode( new CompositeProperty<Color>( new Function0<Color>() {
            public Color apply() {
                return state.colorScheme.whiteBackground.get() ? blue : Color.yellow;
            }
        }, state.colorScheme.whiteBackground ), miniBeakerNode, particleWindowNode ) );

        //Control panel
        controlPanel = new WaterControlPanel( model, state, this, sucrose3DDialog ) {{
            setOffset( INSET, canvasSize.getHeight() - getFullBounds().getHeight() - INSET );
        }};
        addChild( controlPanel );

        //DEBUGGING
//        waterModel.k.trace( "k" );
//        waterModel.pow.trace( "pow" );
//        waterModel.randomness.trace( "randomness" );

        //Create the salt and sugar buckets so salt and sugar can be dragged into the play area
        //The transform must have inverted Y so the bucket is upside-up.
        final Rectangle referenceRect = new Rectangle( 0, 0, 1, 1 );
        ModelViewTransform bucketTransform = createRectangleInvertedYMapping( referenceRect, referenceRect );
        Dimension2DDouble bucketSize = new Dimension2DDouble( 140, 70 );
        sugarBucket = new BucketView( new Bucket( canvasSize.getWidth() / 2 + 210, -canvasSize.getHeight() + bucketSize.getHeight(), bucketSize, green, SUGAR ), bucketTransform );
        saltBucket = new BucketView( new Bucket( canvasSize.getWidth() / 2, -canvasSize.getHeight() + bucketSize.getHeight(), bucketSize, blue, SALT ), bucketTransform );

        //Add the buckets to the view
        addChild( sugarBucket.getHoleNode() );
        addChild( saltBucket.getHoleNode() );

        //Create layers for the sugar and salt particles
        saltBucketParticleLayer = new PNode();
        addChild( saltBucketParticleLayer );
        addChild( saltBucket.getFrontNode() );

        sugarBucketParticleLayer = new PNode();
        addChild( sugarBucketParticleLayer );
        addChild( sugarBucket.getFrontNode() );

        //Add clock controls for pause/play/step
        addChild( new FloatingClockControlNode( model.playButtonPressed, NO_READOUT, model.clock, "", new Property<Color>( Color.white ) ) {{
            setOffset( controlPanel.getFullBounds().getMaxX() + INSET, controlPanel.getFullBounds().getMaxY() - getFullBounds().getHeight() );
        }} );

        //When a water molecule is added in the model, add graphics for each atom in the view
        model.waterList.addElementAddedObserver( new VoidFunction1<WaterMolecule>() {
            public void apply( final WaterMolecule waterMolecule ) {
                for ( SphericalParticle waterAtom : waterMolecule ) {
                    final SphericalParticleNodeWithText node = new SphericalParticleNodeWithText( transform, waterAtom, model.showChargeColor, model.showWaterCharges );
                    particleWindowNode.particleLayer.addChild( node );
                    model.waterList.addElementRemovedObserver( waterMolecule, new VoidFunction0() {
                        public void apply() {
                            model.waterList.removeElementRemovedObserver( waterMolecule, this );
                            particleWindowNode.particleLayer.removeChild( node );
                        }
                    } );
                }
            }
        } );

        //When a sucrose molecule is added in the model, add graphics for each atom in the view
        model.sucroseList.addElementAddedObserver( new VoidFunction1<Sucrose>() {
            public void apply( final Sucrose sucrose ) {
                final SucroseCrystal newCrystal = new SucroseCrystal( sucrose.getPosition(), 0 ) {{
                    addConstituent( new Constituent<Sucrose>( sucrose, ZERO ) );
                }};
                final CrystalNode crystalNode = new CrystalNode<Sucrose, SucroseCrystal>( transform, model, sugarBucket, sugarBucketParticleLayer, WaterCanvas.this, newCrystal, model.addSucrose, model.removeSucrose, not( model.showSugarAtoms ) );
                crystalNode.setIcon( false );
                crystalNode.setInBucket( false );
                particleWindowNode.particleLayer.addChild( crystalNode );

                model.sucroseList.addElementRemovedObserver( sucrose, new VoidFunction0() {
                    public void apply() {
                        model.sucroseList.removeElementRemovedObserver( sucrose, this );
                        particleWindowNode.particleLayer.removeChild( crystalNode );
                    }
                } );
            }
        } );

        //When a salt ion is added in the model, add graphics for each atom in the view
        model.saltIonList.addElementAddedObserver( new VoidFunction1<SaltIon>() {
            public void apply( final SaltIon ion ) {
                SodiumChlorideCrystal newCrystal = new SodiumChlorideCrystal( ion.getPosition(), 0 ) {{
                    addConstituent( new Constituent<SaltIon>( ion, ZERO ) );
                }};
                final CrystalNode crystalNode = new CrystalNode<SaltIon, SodiumChlorideCrystal>( transform, model, saltBucket, saltBucketParticleLayer, WaterCanvas.this, newCrystal, model.addSaltIon, model.removeSaltIon, model.showChargeColor );
                crystalNode.setIcon( false );
                crystalNode.setInBucket( false );
                particleWindowNode.particleLayer.addChild( crystalNode );

                model.saltIonList.addElementRemovedObserver( ion, new VoidFunction0() {
                    public void apply() {
                        model.saltIonList.removeElementRemovedObserver( ion, this );
                        particleWindowNode.particleLayer.removeChild( crystalNode );
                    }
                } );
            }
        } );

        //Start out the buckets with salt and sugar
        addSaltToBucket( model, transform );
        addSugarToBucket( model, transform );

        //When the sim resets, put the sugar and salt back in the buckets
        model.addResetListener( new VoidFunction0() {
            public void apply() {
                addSaltToBucket( model, transform );
                addSugarToBucket( model, transform );
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

    //Puts a single salt crystal in the sugar bucket for the user to drag out
    public void addSaltToBucket( final WaterModel model, final ModelViewTransform transform ) {
        saltBucketParticleLayer.removeAllChildren();

        //Create a model element for the sucrose crystal that the user will drag
        SodiumChlorideCrystal crystal = new SodiumChlorideCrystal( ZERO, 0 ) {{
            addConstituent( new Constituent<SaltIon>( new SaltIon.ChlorideIon(), ZERO ) );

            //Add in the experimentally determined order so it will form a small square crystal
            addConstituent( getOpenSites().get( 1 ).toConstituent() );
            addConstituent( getOpenSites().get( 2 ).toConstituent() );
            addConstituent( getOpenSites().get( 4 ).toConstituent() );
        }};
        //TODO: why is this call necessary?
        crystal.updateConstituentLocations();

        //Create the node for sugar that will be shown in the bucket that the user can grab
        CrystalNode<SaltIon, SodiumChlorideCrystal> crystalNode = new CrystalNode<SaltIon, SodiumChlorideCrystal>( transform, model, saltBucket, saltBucketParticleLayer, this, crystal, model.addSaltIon, model.removeSaltIon, model.showChargeColor );

        //Initially put the crystal node in between the front and back of the bucket layers, it changes layers when grabbed so it will be in front of the bucket
        saltBucketParticleLayer.addChild( crystalNode );

        //Center it on the bucket hole after it has been added to the layer
        crystalNode.centerInBucket();
    }

    //Puts a single sugar crystal in the salt bucket for the user to grab
    public void addSugarToBucket( final WaterModel model, final ModelViewTransform transform ) {
        sugarBucketParticleLayer.removeAllChildren();

        //Create a model element for the sucrose crystal that the user will drag
        SucroseCrystal crystal = new SucroseCrystal( ZERO, 0 ) {{
            addConstituent( new Constituent<Sucrose>( new Sucrose( ZERO, Math.PI / 2 ), ZERO ) );

            //Add at the 2nd open site instead of relying on random so that it will be horizontally latticed, so it will fit in the bucket
            addConstituent( new Constituent<Sucrose>( new Sucrose( ZERO, Math.PI / 2 ), getOpenSites().get( 2 ).relativePosition ) );
        }};
        //TODO: why is this call necessary?
        crystal.updateConstituentLocations();

        //Create the node for sugar that will be shown in the bucket that the user can grab
        CrystalNode<Sucrose, SucroseCrystal> crystalNode = new CrystalNode<Sucrose, SucroseCrystal>( transform, model, sugarBucket, sugarBucketParticleLayer, this, crystal, model.addSucrose, model.removeSucrose, not( model.showSugarAtoms ) );

        //Initially put the crystal node in between the front and back of the bucket layers, it changes layers when grabbed so it will be in front of the bucket
        sugarBucketParticleLayer.addChild( crystalNode );

        //Center it on the bucket hole after it has been added to the layer
        crystalNode.centerInBucket();
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    public ModelViewTransform getModelViewTransform() {
        return transform;
    }

    //TODO: add label on front of sugar
//    new PImage( new HTMLNode( "C<sub>12</sub>H<sub>22</sub>O<sub>11</sub>" ) {{
//            setFont( new PhetFont( 20, true ) );
//        }}.toImage() ) {{
//            childLayer.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
//                public void propertyChange( PropertyChangeEvent evt ) {
//                    setOffset( childLayer.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, childLayer.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
//                }
//            } );
//        }}

}