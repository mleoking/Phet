// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.buildafraction.model.MixedFraction;
import edu.colorado.phet.fractions.buildafraction.view.CollectionBoxNode;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;

import static edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas.controlPanelStroke;
import static java.lang.Math.ceil;

/**
 * Node that shows a target scoring cell, where a correct fraction can be collected.
 *
 * @author Sam Reid
 */
public class ShapeCollectionBoxNode extends CollectionBoxNode {
    private final PhetPPath path;
    private boolean completed;
    private final UndoButton undoButton;
    private ContainerNode containerNode;
    private final ShapeSceneNode sceneNode;

    public ShapeCollectionBoxNode( final ShapeSceneNode sceneNode, final MixedFraction mixedFraction, final BooleanProperty userCreatedMatch ) {
        this.sceneNode = sceneNode;
        if ( sceneNode == null ) { throw new RuntimeException( "Null scene" ); }
        double numberShapes = ceil( mixedFraction.toDouble() );
        this.path = new PhetPPath( new RoundRectangle2D.Double( 0, 0,

                                                                //room for shape items
                                                                120 * numberShapes +

                                                                //spacing between them
                                                                5 * ( numberShapes - 1 ),
                                                                114, ARC, ARC ), BACKGROUND, STROKE, STROKE_PAINT ) {{

            if ( !userCreatedMatch.get() ) { setTransparency( FADED_OUT ); }
            userCreatedMatch.addObserver( new VoidFunction1<Boolean>() {
                public void apply( final Boolean aBoolean ) {
                    animateToTransparency( 1f, FADE_IN_TIME );
                }
            } );
        }};
        addChild( this.path );

        undoButton = new UndoButton( Components.collectionBoxUndoButton ) {{
            scale( 0.8 );
            setOffset( -1, -1 );
            addActionListener( new ActionListener() {
                public void actionPerformed( final ActionEvent e ) {
                    undo();
                }
            } );
        }};
        undoButton.addInputEventListener( new CursorHandler() );
        undoButton.setVisible( false );
        addChild( undoButton );
    }

    public void undo() {
        if ( completed ) {
            completed = false;
            path.setStrokePaint( Color.darkGray );
            path.setStroke( controlPanelStroke );
            undoButton.setVisible( false );
            undoButton.setPickable( false );
            undoButton.setChildrenPickable( false );

            sceneNode.addChild( containerNode );

            containerNode.setScale( 1.0 );
            containerNode.addBackUndoButton();
            containerNode.setAllPickable( true );

            //Have to start animating back before changing the "target cell" flag, because that flag is used to determine whether it is "inPlayArea" for purposes of choosing location.
            sceneNode.animateContainerNodeToAppropriateLocation( containerNode );
            containerNode.setInTargetCell( false, 0 );

            //Send the pieces home
            containerNode.undoAll();

            //The blue "break apart" control once a container has been put into the collection box, it will fly back to the floating panel and retain its divisions.  So we just need to have it reset to no divisions when it goes back to the panel.
            containerNode.selectedPieceSize.set( 0 );

            containerNode.resetNumberOfContainers();

            containerNode = null;

            sceneNode.collectionBoxUndone();
            sceneNode.syncModelFractions();
        }
    }

    public void setCompletedFraction( ContainerNode containerNode ) {
        this.containerNode = containerNode;
        path.setStrokePaint( Color.darkGray );
        this.completed = true;
        undoButton.setVisible( true );
        undoButton.setPickable( true );
        undoButton.setChildrenPickable( true );
    }

    public boolean isCompleted() { return completed; }
}