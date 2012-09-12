// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.common.view.X2Y2DragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeLineNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Given an equation in slope-intercept form, graph the line by manipulating 2 arbitrary points.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SI_EG_Points_ChallengeNode extends SI_ChallengeNode {

    public SI_EG_Points_ChallengeNode( final LineGameModel model, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override public ChallengeGraphNode createChallengeGraphNode( Graph graph, Property<Line> guessLine, Line answerLine, ModelViewTransform mvt ) {
        return new ThisGraphNode( graph, guessLine, answerLine, mvt );
    }

    // Graph for this challenge
    private static class ThisGraphNode extends ChallengeGraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode x1y1ManipulatorNode, x2y2ManipulatorNode;

        public ThisGraphNode( final Graph graph,
                              Property<Line> guessLine,
                              Line answerLine,
                              final ModelViewTransform mvt ) {
            super( graph, mvt );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = new SlopeInterceptLineNode( answerLine.withColor( GameConstants.CORRECT_ANSWER_COLOR ), graph, mvt );
            answerNode.setEquationVisible( false );
            answerNode.setVisible( false || PhetApplication.getInstance().isDeveloperControlsEnabled() );

            // manipulators
            final double manipulatorDiameter = mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );
            x1y1ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
            x1y1ManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                            x1y1ManipulatorNode, mvt, guessLine,
                                                                            new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                                                            new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                                                            false /* constantSlope */ ) );
            x2y2ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X2_Y2 );
            x2y2ManipulatorNode.addInputEventListener( new X2Y2DragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                            x2y2ManipulatorNode, mvt, guessLine,
                                                                            new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                                                            new Property<DoubleRange>( new DoubleRange( graph.yRange ) ) ) );

            // Rendering order
            addChild( answerNode );
            addChild( guessNodeParent );
            addChild( x1y1ManipulatorNode );
            addChild( x2y2ManipulatorNode );

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = new PointSlopeLineNode( line, graph, mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulators
                    x1y1ManipulatorNode.setOffset( mvt.modelToView( line.x1, line.y1 ) );
                    x2y2ManipulatorNode.setOffset( mvt.modelToView( line.x2, line.y2 ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
        }
    }
}
