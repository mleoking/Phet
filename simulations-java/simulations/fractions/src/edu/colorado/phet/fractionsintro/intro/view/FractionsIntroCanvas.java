// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import fj.F;
import fj.data.List;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.intro.model.FractionsIntroModel;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Pie;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.colorado.phet.fractionsintro.intro.view.pieset.PieSetNode;
import edu.colorado.phet.fractionsintro.intro.view.pieset.WaterGlassNodeFactory;
import edu.colorado.phet.fractionsintro.intro.view.representationcontrolpanel.RepresentationControlPanel;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fractionsintro.intro.view.Representation.*;
import static fj.Ord.doubleOrd;

/**
 * Canvas for "Fractions Intro" sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends AbstractFractionsCanvas {

    public FractionsIntroCanvas( final FractionsIntroModel model ) {

        final RepresentationControlPanel representationControlPanel = new RepresentationControlPanel( model.representation ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullWidth() / 2, INSET );
        }};
        addChild( representationControlPanel );

        //Cake set with click-to-toggle (not draggable) cakes
        addChild( new CakeSetFractionNode( model.containerSet, model.representation.valueEquals( Representation.CAKE ) ) {{
            setOffset( INSET + -10, representationControlPanel.getFullBounds().getMaxY() + 100 - 40 );
        }} );

        //Number line
        addChild( new NumberLineNode( model.numerator, model.denominator, model.representation.valueEquals( NUMBER_LINE ) ) {{
            setOffset( INSET + 10, representationControlPanel.getFullBounds().getMaxY() + 100 + 15 );
        }} );

        //Show the pie set node when pies are selected
        addChild( new RepresentationNode( model.representation, PIE, new PieSetNode( model.pieSet, rootNode ) ) );

        //For horizontal bars
        addChild( new RepresentationNode( model.representation, HORIZONTAL_BAR, new PieSetNode( model.horizontalBarSet, rootNode ) ) );

        //For vertical bars
        addChild( new RepresentationNode( model.representation, VERTICAL_BAR, new PieSetNode( model.verticalBarSet, rootNode ) ) );

        //For draggable cakes
//        addChild( new RepresentationNode( model.representation, CAKE, new PieSetNode( model.cakeSet, rootNode, new F<SliceNodeArgs, PNode>() {
//            @Override public PNode f( final SliceNodeArgs a ) {
//                return new PImage( CakeNode.cropSides( FractionsResources.RESOURCES.getImage( "cake/cake_" + a.denominator + "_" + 1 + ".png" ) ) ) {{
//                    setOffset( a.slice.shape().getBounds2D().getCenterX() - getFullBounds().getWidth() / 2, a.slice.shape().getBounds2D().getCenterY() - getFullBounds().getHeight() / 2 );
//                }};
//            }
//        } ) ) );

        //For debugging water glasses region management
//        addChild( new RepresentationNode( model.representation, WATER_GLASSES ) {{
//            addChild( new PieSetNode( model.waterGlassSet, rootNode ) );
//        }} );

        //For water glasses
        addChild( new RepresentationNode( model.representation, WATER_GLASSES, new PieSetNode( model.waterGlassSet, rootNode, new WaterGlassNodeFactory() ) {
            @Override protected PNode createEmptyCellsNode( PieSet state ) {
                PNode node = new PNode();
                //Show the beakers
                for ( final Pie pie : state.pies ) {
                    final List<Double> centers = pie.cells.map( new F<Slice, Double>() {
                        @Override public Double f( Slice s ) {
                            return s.shape().getBounds2D().getMinY();
                        }
                    } );

                    //TODO: Could read from cache like WaterGlassNodeFactory instead of creating new each time to improve performance
                    node.addChild( new WaterGlassNode( state.countFilledCells( pie ), state.denominator ) {{
                        setOffset( pie.cells.index( 0 ).shape().getBounds2D().getX(), centers.minimum( doubleOrd ) );
                    }} );
                }
                return node;
            }
        } ) );

        ZeroOffsetNode fractionEqualityPanel = new ZeroOffsetNode( new FractionControlNode( model.numerator, model.denominator, model.maximum ) ) {{
            setOffset( 35, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( fractionEqualityPanel );

        final ResetAllButtonNode resetAllButtonNode = new ResetAllButtonNode( new Resettable() {
            public void reset() {
                model.resetAll();
            }
        }, this, CONTROL_FONT, Color.black, Color.orange ) {{
            setConfirmationEnabled( false );
        }};
        addChild( resetAllButtonNode );

        resetAllButtonNode.setOffset( STAGE_SIZE.width - resetAllButtonNode.getFullBounds().getWidth() - INSET, STAGE_SIZE.height - resetAllButtonNode.getFullBounds().getHeight() - INSET );

        //Spinner to change the maximum allowed value
        MaxSpinner maxSpinner = new MaxSpinner( model.maximum ) {{

            //Center above reset all button
            setOffset( resetAllButtonNode.getFullBounds().getCenterX() - getFullWidth() / 2, resetAllButtonNode.getFullBounds().getY() - getFullHeight() - INSET );
        }};
        addChild( maxSpinner );
    }
}