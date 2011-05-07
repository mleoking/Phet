// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property5.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Salt;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Sugar;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.deprecated.PropertyRadioButton;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser.SUGAR;

/**
 * Canvas for the introductory (first) tab in the Sugar and Salt Solutions Sim
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsCanvas extends PhetPCanvas {
    //Root node that shows the nodes in the stage coordinate frame
    private final PNode rootNode;

    //Insets to be used for padding between edge of canvas and controls, or between controls
    private final int INSET = 5;

    //Fonts
    public static Font CONTROL_FONT = new PhetFont( 16 );
    public static Font TITLE_FONT = new PhetFont( 16, true );
    private final PNode crystalLayer = new PNode();//Layer that holds the sugar and salt crystals

    private final ControlPanelNode soluteControlPanelNode;
    private final ControlPanelNode toolsControlPanelNode;

    public SugarAndSaltSolutionsCanvas( final SugarAndSaltSolutionModel model, final ObservableProperty<Boolean> removeSaltSugarButtonVisible ) {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBackground( Color.black );//Background is black so that white crystals can be seen

        //Width of the stage
        final int stageWidth = 1008;//Actual size of the canvas coming up on windows from the IDE is java.awt.Dimension[width=1008,height=676]
        final int stageHeight = (int) ( stageWidth / model.width * model.height );

        //Set the stage size according to the model aspect ratio
        final PDimension stageSize = new PDimension( stageWidth, stageHeight );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, stageSize ) );

        //Create the transform from model (SI) to view (stage) coordinates
        final double scale = stageWidth / model.width;
        final ModelViewTransform transform = createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                     new Point2D.Double( stageSize.getWidth() * 0.43, stageSize.getHeight() - 50 ),
                                                                                     scale );

        soluteControlPanelNode = new ControlPanelNode( new VBox() {{
            addChild( new PText( "Solute" ) {{setFont( TITLE_FONT );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ) );//spacer
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new PropertyRadioButton<Dispenser>( "Salt", model.dispenser, SALT ) {{setFont( CONTROL_FONT );}} );
                add( new PropertyRadioButton<Dispenser>( "Sugar", model.dispenser, Dispenser.SUGAR ) {{setFont( CONTROL_FONT );}} );
            }} ) );
        }} ) {{
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, 150 );
        }};
        addChild( soluteControlPanelNode );

        toolsControlPanelNode = new ControlPanelNode( new VBox() {{
            //Add title and a spacer below it
            addChild( new PText( "Tools" ) {{setFont( TITLE_FONT );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ) );//spacer

            //Add the controls in the control panel
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new CheckBox( "Show concentration" ) );
                add( new JPanel() {{
                    add( Box.createHorizontalStrut( 10 ) );//Indent the show values a bit since it relates to show concentration box
                    add( new CheckBox( "Show values" ) );
                }} );
                add( new CheckBox( "Measure conductivity" ) );
                add( new CheckBox( "Evaporate water" ) );
            }} ) );
        }} ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth(), soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }};
        addChild( toolsControlPanelNode );

        //Add the reset all button
        addChild( new ButtonNode( "Reset All", Color.yellow ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - INSET, stageSize.height - getFullBounds().getHeight() - INSET );
            setFont( CONTROL_FONT );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.reset();
                }
            } );
        }} );

        //Add the faucets, the first faucet should have the water stop at the base of the beaker
        addChild( new FaucetNode( transform, model.inputFlowRate, new Some<Double>( transform.modelToViewY( model.beaker.getY() ) ) ) );
        addChild( new FaucetNode( transform, model.outputFlowRate, new None<Double>() ) {{
            Point2D beakerBottomRight = model.beaker.getOutputFaucetAttachmentPoint();
            Point2D beakerBottomRightView = transform.modelToView( beakerBottomRight );
            //Move it up by the height of the faucet image, otherwise it sticks out underneath the beaker
            setOffset( beakerBottomRightView.getX() - getFullBounds().getWidth() * 0.4, //Hand tuned so it doesn't overlap the reset button in English
                       beakerBottomRightView.getY() - getFullBounds().getHeight() );
        }} );

        //Add salt crystals graphics when salt crystals are added to the model
        model.saltAdded.addListener( new CrystalMaker<Salt>( transform, crystalLayer, new Function1<Salt, PNode>() {
            public PNode apply( Salt salt ) {
                return new SaltNode( transform, salt );
            }
        } ) );

        //Add sugar crystals graphics when sugar crystals are added to the model
        model.sugarAdded.addListener( new CrystalMaker<Sugar>( transform, crystalLayer, new Function1<Sugar, PNode>() {
            public PNode apply( Sugar sugar ) {
                return new SugarNode( transform, sugar );
            }
        } ) );

        //add the salt shaker node
        addChild( new DispenserNode( transform, new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                model.addSalt( new Salt( position ) );
            }
        }, "salt-dispenser.png", model.dispenser.valueEquals( SALT ) ) );

        //add the sugar dispenser node
        addChild( new DispenserNode( transform, new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                model.addSugar( new Sugar( position ) );
            }
        }, "sugar-dispenser.png", model.dispenser.valueEquals( SUGAR ) ) );

        //Show the crystal layer behind the water and beaker so the crystals look like they go into the water instead of in front of it.
        addChild( crystalLayer );

        //Add beaker and water nodes
        final BeakerNode beakerNode = new BeakerNode( transform, model.beaker );
        addChild( beakerNode );
        addChild( new WaterNode( transform, model.water ) );

        //Add a button that allows the user to remove all solutes
        addChild( new ButtonNode( "Remove salt/sugar" ) {{
            //Button should be inside the beaker
            setOffset( beakerNode.getFullBounds().getMaxX() - getFullBounds().getWidth() - INSET,
                       beakerNode.getFullBounds().getMaxY() - getFullBounds().getHeight() - INSET );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.removeSaltAndSugar();
                }
            } );
            removeSaltSugarButtonVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );

        //Debug for showing stage
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, stageSize.getWidth(), stageSize.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    protected double getControlPanelMinX() {
        return Math.min( soluteControlPanelNode.getFullBoundsReference().getMinX(), toolsControlPanelNode.getFullBoundsReference().getMinX() );
    }
}