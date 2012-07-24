// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.colorado.phet.common.games.GameSettings;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;

/**
 * Model for the "Line Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineGameModel {

    private static final java.util.logging.Logger LOGGER = LoggingUtils.getLogger( LineGameModel.class.getCanonicalName() );

    private static final int GRID_VIEW_UNITS = 400; // max dimension (width or height) of the grid in the view
    private static final int CHALLENGES_PER_GAME = 5;
    private static final int MAX_POINTS_PER_CHALLENGE = 2;

    // phases of a game, mutually exclusive
    public enum GamePhase {
        SETTINGS,  // user is choosing game settings
        PLAY, // user is playing the game
        RESULTS // user is receiving results of playing the game
    }

    /*
     * States during the "play" phase of a game, mutually exclusive.
     * For lack of better names, the state names correspond to the main action that
     * the user can take in that state.  For example. the FIRST_CHECK state is where the user
     * has their first opportunity to press the "Check" button to check their answer.
     */
    public static enum PlayState {
        FIRST_CHECK,
        TRY_AGAIN,
        SECOND_CHECK,
        SHOW_ANSWER,
        NEXT
    }

    public final ModelViewTransform mvt; // transform between model and view coordinate frames
    public final GameSettings settings;
    public final GameTimer timer;
    private final HashMap<Integer, Long> bestTimes; // best times, maps level to time in ms
    private boolean isNewBestTime; // is the time for this game a new best time?
    public final Property<Integer> score; // how many points the user has earned for the current game
    public final Property<GamePhase> phase;
    public final Property<PlayState> state;

    public final Graph graph; // the graph that plots the lines
    public final Property<MatchingChallenge> challenge; // the current challenge
    private MatchingChallenge[] challenges = new MatchingChallenge[CHALLENGES_PER_GAME];
    private int challengeIndex;

    // Defaults
    public LineGameModel() {
        this( 10 );
    }

    // Uses a graph with with uniform quadrant sizes.
    private LineGameModel( int quadrantSize ) {
        this( new IntegerRange( -quadrantSize, quadrantSize ), new IntegerRange( -quadrantSize, quadrantSize ) );
    }

    private LineGameModel( IntegerRange xRange, IntegerRange yRange ) {

        final double mvtScale = GRID_VIEW_UNITS / Math.max( xRange.getLength(), yRange.getLength() ); // view units / model units
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 1.2 * GRID_VIEW_UNITS / 2, 1.25 * GRID_VIEW_UNITS / 2 ), mvtScale, -mvtScale ); // y is inverted

        settings = new GameSettings( new IntegerRange( 1, 3 ), true /* soundEnabled */, true /* timerEnabled */ );

        score = new Property<Integer>( 0 );

        graph = new Graph( xRange, yRange );

        challenge = new Property<MatchingChallenge>( new MatchingChallenge( new StraightLine( 1, 1, 1, Color.BLACK ) ) ); // initial value is meaningless

        // time
        timer = new GameTimer( new ConstantDtClock( 1000 / 5, 1 ) );
        bestTimes = new HashMap<Integer, Long>();
        for ( int i = settings.level.getMin(); i <= settings.level.getMax(); i++ ) {
            bestTimes.put( i, 0L );
        }

        phase = new Property<GamePhase>( GamePhase.SETTINGS ) {

            // Update fields so that they are accurate before property observers are notified.
            @Override public void set( GamePhase phase ) {
                LOGGER.info( "game phase = " + phase );
                if ( phase == GamePhase.SETTINGS ) {
                    // do nothing
                }
                else if ( phase == GamePhase.PLAY ) {
                    initChallenges();
                    state.set( PlayState.FIRST_CHECK );
                    timer.start();
                }
                else if ( phase == GamePhase.RESULTS ) {
                    timer.stop();
                    updateBestTime();
                }
                else {
                    throw new UnsupportedOperationException( "unsupported game phase = " + phase );
                }
                super.set( phase );
            }
        };

        initChallenges();

        state = new Property<PlayState>( PlayState.NEXT ) {{  // initialized to anything except FIRST_CHECK, to force a state change
            addObserver( new VoidFunction1<PlayState>() {
                public void apply( PlayState state ) {
                    LOGGER.info( "play state = " + state );
                    if ( state == PlayState.FIRST_CHECK ) {
                        if ( challengeIndex == challenges.length ) {
                            // game has been completed
                            phase.set( GamePhase.RESULTS );
                        }
                        else {
                            // next challenge
                            challenge.set( challenges[challengeIndex] );
                            challengeIndex++;
                        }
                    }
                }
            } );
        }};
    }

    private void initChallenges() {
        //TODO create different types of challenges, randomized for level
        challengeIndex = 0;
        challenges[0] = new MatchingChallenge( new StraightLine( 4, 2, 3, GameConstants.GIVEN_COLOR ) );
        challenges[1] = new MatchingChallenge( new StraightLine( 5, 1, 1, GameConstants.GIVEN_COLOR ) );
        challenges[2] = new MatchingChallenge( new StraightLine( -3, 3, -2, GameConstants.GIVEN_COLOR ) );
        challenges[3] = new MatchingChallenge( new StraightLine( 10, 2, -6, GameConstants.GIVEN_COLOR ) );
        challenges[4] = new MatchingChallenge( new StraightLine( 0, 3, 2, GameConstants.GIVEN_COLOR ) );
    }

    public boolean isPerfectScore() {
        return score.get() == getPerfectScore();
    }

    // Gets the number of points in a perfect score (ie, correct answers for all challenges on the first try)
    public int getPerfectScore() {
       return CHALLENGES_PER_GAME * computePoints( 1 );
    }

    /**
     * Gets the best time for a specified level.
     * If this returns zero, then there is no best time for the level.
     *
     * @param level
     * @return
     */
    public long getBestTime( int level ) {
        Long bestTime = bestTimes.get( level );
        if ( bestTime == null ) {
            bestTime = 0L;
        }
        return bestTime;
    }

    public boolean isNewBestTime() {
        return isNewBestTime;
    }

    // Updates the best time for the current level, at the end of a game with a perfect score.
    private void updateBestTime() {
        assert( !timer.isRunning() );
        if ( settings.timerEnabled.get() && score.get() == getPerfectScore() ) {
            isNewBestTime = false;
            if ( bestTimes.get( settings.level.get() ) == 0 ) {
                // there was no previous time for this level
                bestTimes.put( settings.level.get(), timer.time.get() );
            }
            else if ( timer.time.get() < bestTimes.get( settings.level.get() ) ) {
                // we have a new best time for this level
                bestTimes.put( settings.level.get(), timer.time.get() );
                isNewBestTime = true;
            }
        }
    }

    // Compute points to be awarded for a correct answer.
    public int computePoints( int attempts ) {
        return Math.max( 0, MAX_POINTS_PER_CHALLENGE - attempts + 1 ) ;
    }
}