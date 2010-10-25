package edu.colorado.phet.buildanatom.modules.game.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.GameOverStateView;
import edu.colorado.phet.buildanatom.modules.game.view.GameSettingsStateView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.theramp.timeseries.TimeSeriesModel;

/**
 * The primary model for the Build an Atom game.  This class sequences the
 * game and sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class BuildAnAtomGameModel {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    public static final int MAX_LEVELS = 3;
    private static final int PROBLEMS_PER_SET = 3;//TODO: fix for deployment, should be 5

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();
    private final State gameSettingsState = new State( this ){
        public StateView createView( GameCanvas gameCanvas ) {
            return new GameSettingsStateView(gameCanvas, BuildAnAtomGameModel.this );
        }
    };
    private final State gameOverState = new State( this ){
        public StateView createView( GameCanvas gameCanvas ) {
            return new GameOverStateView(gameCanvas, BuildAnAtomGameModel.this );
        }
    };
    private final Property<Integer> score = new Property<Integer>( 0 );

    // Level pools from the design doc
    private final HashMap<Integer, ArrayList<AtomValue>> levels = new HashMap<Integer, ArrayList<AtomValue>>() {{
        put( 1, new ArrayList<AtomValue>() {{
            add( new AtomValue( 1, 0, 1 ) );
            add( new AtomValue( 2, 2, 2 ) );
            add( new AtomValue( 3, 4, 3 ) );
            add( new AtomValue( 4, 5, 4 ) );
            add( new AtomValue( 5, 5, 5 ) );
            add( new AtomValue( 6, 6, 6 ) );
            add( new AtomValue( 7, 7, 7 ) );
            add( new AtomValue( 8, 8, 8 ) );
            add( new AtomValue( 9, 9, 9 ) );
            add( new AtomValue( 10, 10, 10 ) );
        }} );
        put( 2, new ArrayList<AtomValue>() {{
            add( new AtomValue( 1, 0, 0 ) );
            add( new AtomValue( 1, 0, 2 ) );
            add( new AtomValue( 3, 4, 2 ) );
            add( new AtomValue( 7, 7, 10 ) );
            add( new AtomValue( 8, 8, 10 ) );
            add( new AtomValue( 9, 9, 10 ) );
        }} );
        //before these can work, sim will need to support another shell for e-
//        put( 3, new ArrayList<AtomValue>() {{
//            add( new AtomValue( 11, 12, 11 ) );
//            add( new AtomValue( 11, 12, 10 ) );
//            add( new AtomValue( 12, 12, 12 ) );
//            add( new AtomValue( 12, 12, 10 ) );
//            add( new AtomValue( 14, 14, 14 ) );
//            add( new AtomValue( 15, 16, 15 ) );
//            add( new AtomValue( 16, 16, 16 ) );
//            add( new AtomValue( 16, 16, 18 ) );
//            add( new AtomValue( 17, 18, 17 ) );
//            add( new AtomValue( 17, 18, 18 ) );
//            add( new AtomValue( 18, 22, 18 ) );
//        }} );

    }};
    private final Random random = new Random();
    private ProblemSet problemSet;
    private ConstantDtClock clock=new ConstantDtClock( 1000,1000);//simulation time is in milliseconds

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------
    public BuildAnAtomGameModel() {
        setState( gameSettingsState );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public State getGameSettingsState() {
        return gameSettingsState;
    }

    public void setState( State newState ) {
        if ( currentState != newState ) {
            State oldState = currentState;
            currentState = newState;
            for ( GameModelListener listener : listeners ) {
                listener.stateChanged( oldState, currentState );
            }
        }
    }

    public State getState() {
        return currentState;
    }

    public void startGame( int level, boolean timerOn, boolean soundOn ) {
        System.out.println( "level = " + level );
        problemSet = new ProblemSet( this, level, PROBLEMS_PER_SET, timerOn, soundOn );
        setState( problemSet.getCurrentProblem() );

        getGameClock().resetSimulationTime();//Start time at zero in case it had time from previous runs
        getGameClock().start();//time starts when the game starts
    }

    public void addListener( GameModelListener listener ) {
        listeners.add( listener );
    }

    public void newGame() {
        setState( gameSettingsState );
    }

    public State getGameOverState() {
        return gameOverState;
    }

    /**
     * Check the user's guess and update the state of the model accordingly.
     */
    public void processGuess() {
        problemSet.getCurrentProblem().processGuess();
        score.setValue( score.getValue()+problemSet.getCurrentProblem().getScore() );
    }

    public Property<Integer> getScoreProperty() {
        return score;
    }

    public ArrayList<AtomValue> getLevel( int level ) {
        return levels.get( level );
    }

    public int getProblemIndex( Problem problem ) {
        return problemSet.getProblemIndex( problem );
    }

    public int getNumberProblems() {
        return problemSet.getTotalNumProblems();
    }

    /**
     * Moves to the next problem or gameover state if no more problems
     */
    public void next() {
        if ( problemSet.isLastProblem() ) {
            setState( getGameOverState() );
        }
        else {
            setState( problemSet.nextProblem() );
        }
    }

    public int getScore() {
        return score.getValue();
    }

    public int getMaximumPossibleScore() {
        return 2*PROBLEMS_PER_SET;//todo: move the '2' elsewhere?
    }

    public ConstantDtClock getGameClock() {
        return clock;
    }

    public long getTime() {
        return (long) clock.getSimulationTime();
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public static interface GameModelListener {
        void stateChanged( State oldState, State newState );
    }

}
