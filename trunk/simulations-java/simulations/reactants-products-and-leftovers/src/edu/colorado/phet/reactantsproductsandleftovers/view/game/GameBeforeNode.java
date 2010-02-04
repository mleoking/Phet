/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.view.game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.controls.QuantityValueNode;
import edu.colorado.phet.reactantsproductsandleftovers.model.ChemicalReaction;
import edu.colorado.phet.reactantsproductsandleftovers.model.Reactant;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameGuess;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameChallenge.ChallengeType;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameAdapter;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModel.GameListener;
import edu.colorado.phet.reactantsproductsandleftovers.view.BracketedLabelNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.IDynamicNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.SubstanceImageNode;
import edu.colorado.phet.reactantsproductsandleftovers.view.ImageLayoutNode.GridLayoutNode;
import edu.umd.cs.piccolo.PNode;


public class GameBeforeNode extends GameBoxNode implements IDynamicNode {
    
    private static final String TITLE = RPALStrings.LABEL_BEFORE_REACTION;
    private static final double TITLE_Y_SPACING = 10;
    
    private static final double CONTROLS_Y_SPACING = 15;
    
    private static final double BRACKET_Y_SPACING = 3;
    private static final PhetFont BRACKET_FONT = new PhetFont( 16 );
    private static final Color BRACKET_TEXT_COLOR = Color.BLACK;
    private static final Color BRACKET_COLOR = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    private static final Stroke BRACKET_STROKE = new BasicStroke( 0.75f );
    
    private final GameModel model;
    private final GameListener gameListener;
    private final ImageLayoutNode answerImagesNode, guessImagesNode;
    private final ArrayList<QuantityValueNode> reactantValueNodes;
    private final ArrayList<ArrayList<SubstanceImageNode>> reactantImageNodeLists; // one list of images per reactant
    private final MoleculesHiddenNode moleculesHiddenNode;
    
    public GameBeforeNode( GameModel model ) {
        super( TITLE );
        
        GameChallenge challenge = model.getChallenge();
        ChemicalReaction reaction = challenge.getReaction();
        
        // image node lists
        reactantImageNodeLists = new ArrayList<ArrayList<SubstanceImageNode>>();
        
        // reactant images and value displays
        reactantValueNodes = new ArrayList<QuantityValueNode>();
        Reactant[] reactants = reaction.getReactants();
        for ( Reactant reactant : reactants ) {
            
            // one list of image nodes for each reactant 
            reactantImageNodeLists.add( new ArrayList<SubstanceImageNode>() );
            
            // one value display for each reactant
            QuantityValueNode quantityNode = new QuantityValueNode( reactant, GameModel.getQuantityRange(), RPALConstants.HISTOGRAM_IMAGE_SCALE, true /* showName */ );
            quantityNode.setEditable( false );
            addChild( quantityNode );
            reactantValueNodes.add( quantityNode );
        }
        
        // layout, origin at upper-left corner of box
        double x = 0;
        double y = 0;
        PNode boxNode = getBoxNode();
        boxNode.setOffset( x, y );
        // title centered above box
        PNode titleNode = getTitleNode();
        x = boxNode.getFullBoundsReference().getCenterX() - ( titleNode.getFullBoundsReference().getWidth() / 2 );
        y = boxNode.getFullBoundsReference().getMinY() - titleNode.getFullBoundsReference().getHeight() - TITLE_Y_SPACING;
        titleNode.setOffset( x, y );
        // reactant quantity controls, horizontally centered in "cells"
        double margin = ( reactants.length > 2 ) ? 0 : ( 0.15 * getBoxWidth() ); // make 2 reactants case look nice
        final double deltaX = ( boxNode.getFullBoundsReference().getWidth() - ( 2 * margin ) ) / ( reactants.length );
        x = boxNode.getFullBoundsReference().getMinX() + margin + ( deltaX / 2 );
        y = boxNode.getFullBoundsReference().getMaxY() + CONTROLS_Y_SPACING;
        for ( int i = 0; i < reactants.length; i++ ) {
            reactantValueNodes.get( i ).setOffset( x, y );
            x += deltaX;
        }
        
        // reactants bracket, after doing layout of leftover quantity displays
        double startX = reactantValueNodes.get( 0 ).getFullBoundsReference().getMinX();
        double endX = reactantValueNodes.get( reactantValueNodes.size() - 1 ).getFullBoundsReference().getMaxX();
        double width = endX - startX;
        PNode reactantsLabelNode = new BracketedLabelNode( RPALStrings.LABEL_REACTANTS, width, BRACKET_FONT, BRACKET_TEXT_COLOR, BRACKET_COLOR, BRACKET_STROKE );
        addChild( reactantsLabelNode );
        x = startX;
        y = 0;
        for ( QuantityValueNode node : reactantValueNodes ) {
            y = Math.max( y, node.getFullBoundsReference().getMaxY() + BRACKET_Y_SPACING );
        }
        reactantsLabelNode.setOffset( x, y );
        
        // sync with model
        this.model = model;
        gameListener = new GameAdapter() {
            @Override 
            public void guessChanged() {
                updateGuessImages();
            }
        };
        model.addGameListener( gameListener );
        
        // images
        answerImagesNode = new GridLayoutNode( getBoxSize() );
        createAnswerImages();
        addChild( answerImagesNode );
        guessImagesNode = new GridLayoutNode( getBoxSize() );
        updateGuessImages();
        addChild( guessImagesNode );
        
        // "images hidden" message node
        moleculesHiddenNode = new MoleculesHiddenNode();
        addChild( moleculesHiddenNode );
        x = ( getBoxWidth() - moleculesHiddenNode.getFullBoundsReference().getWidth() ) / 2;
        y = ( getBoxHeight() - moleculesHiddenNode.getFullBoundsReference().getHeight() ) / 2;
        moleculesHiddenNode.setOffset( x, y );
        
        // default state
        if ( challenge.getChallengeType() == ChallengeType.BEFORE ) {
            showGuess( true /* editable */, challenge.isImagesVisible() );
        }
        else {
            showAnswer( challenge.isImagesVisible() );
        }
    }
    
    public void cleanup() {
        model.removeGameListener( gameListener );
    }
    
    public void showAnswer( boolean showImages ) {
        
        ChemicalReaction reaction = model.getChallenge().getReaction();
        
        // reactants
        for ( int i = 0; i < reactantValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = reactantValueNodes.get( i );
            // attach to reactant of reaction
            valueNode.setSubstance( reaction.getReactant( i ) );
            // set to read-only
            valueNode.setEditable( false );
        }
        
        // show images for reaction
        showGuessImages( false );
        showAnswerImages( showImages );
        showImagesHiddenMessage( !showImages );
    }
    
    public void showGuess( boolean editable, boolean showImages ) {
        
        GameGuess guess = model.getChallenge().getGuess();
        
        // reactants
        for ( int i = 0; i < reactantValueNodes.size(); i++ ) {
            QuantityValueNode valueNode = reactantValueNodes.get( i );
            // attach to reactant of user's answer
            valueNode.setSubstance( guess.getReactant( i ) );
            // set editability
            valueNode.setEditable( editable );
        }
        
        // show images for user's answer
        showAnswerImages( false );
        showGuessImages( showImages );
        showImagesHiddenMessage( false );
    }
    
    public void showAnswerImages( boolean b ) {
        answerImagesNode.setVisible( b );
        moleculesHiddenNode.setVisible( !b );
    }
    
    public void showGuessImages( boolean b ) {
        guessImagesNode.setVisible( b );
    }
    
    public void showImagesHiddenMessage( boolean b ) {
        moleculesHiddenNode.setVisible( b );
    }
    
    /*
     * Sets images for the reactants of the correct answer.
     */
    private void createAnswerImages() {
        ChemicalReaction reaction = model.getChallenge().getReaction();
        Reactant[] reactants = reaction.getReactants();
        PNode previousNode = null;
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            for ( int j = 0; j < reactant.getQuantity(); j++ ) {
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                answerImagesNode.addNode( imageNode, previousNode, null );
                previousNode = imageNode;
            }
        }
    }

    /*
     * Updates images for reactants to match the user's guess.
     * The last image added is the first to be removed. 
     */
    private void updateGuessImages() {
        
        GameGuess guess = model.getChallenge().getGuess();

        /*
         * Do all removal first, so that we free up space in the box.
         */

        // remove reactants
        Reactant[] reactants = guess.getReactants();
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = reactantImageNodeLists.get( i );
            while ( reactant.getQuantity() < imageNodes.size() ) {
                SubstanceImageNode imageNode = imageNodes.get( imageNodes.size() - 1 );
                imageNode.cleanup();
                guessImagesNode.removeNode( imageNode );
                imageNodes.remove( imageNode );
            }
        }

        /*
         * Do all additions after removals, so that we have free space in the box.
         */

        // add reactants
        for ( int i = 0; i < reactants.length; i++ ) {
            Reactant reactant = reactants[i];
            ArrayList<SubstanceImageNode> imageNodes = reactantImageNodeLists.get( i );
            while ( reactant.getQuantity() > imageNodes.size() ) {
                PNode lastNodeAdded = null;
                if ( imageNodes.size() > 0 ) {
                    lastNodeAdded = imageNodes.get( imageNodes.size() - 1 );
                }
                SubstanceImageNode imageNode = new SubstanceImageNode( reactant );
                imageNode.scale( RPALConstants.BEFORE_AFTER_BOX_IMAGE_SCALE );
                imageNodes.add( imageNode );
                guessImagesNode.addNode( imageNode, lastNodeAdded, reactantValueNodes.get( i ) );
                lastNodeAdded = imageNode;
            }
        }
    }
    
}
