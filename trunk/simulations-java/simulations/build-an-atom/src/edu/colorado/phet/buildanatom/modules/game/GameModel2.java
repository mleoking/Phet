package edu.colorado.phet.buildanatom.modules.game;

import java.util.ArrayList;

import edu.colorado.phet.common.games.GameSettingsPanel;
import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * The primary model for the Build an Atom game.  This sequences the game and
 * sends out events when the game state changes.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class GameModel2 {
    public static final int MAX_LEVELS = 3;
    public static final int MAX_SCORE = 5;

    private State currentState;
    private final ArrayList<GameModelListener> listeners = new ArrayList<GameModelListener>();

    public GameModel2() {
        setState( new GameSettingsState( this ) );
    }

    void setState( State newState ) {
        if ( currentState != newState ) {
            currentState = newState;
            notifyStateChanged();
        }
    }

    public State getState() {
        return currentState;
    }

    private void notifyStateChanged() {
        for ( GameModelListener listener : listeners ) {
            listener.stateChanged();
        }
    }

    public void startGame() {
        setState( new PlayingGame( this ) );
    }

    public void addListener( GameModelListener listener ) {
        listeners.add( listener );
    }

    public static interface GameModelListener {
        void stateChanged();
    }

    public abstract static class State {
        protected final GameModel2 model;

        public State( GameModel2 model ) {
            this.model = model;
        }
    }

    public static class GameSettingsState extends State {
        public GameSettingsState( GameModel2 model ) {
            super( model );

        }
    }

    public static class PlayingGame extends State {
        public PlayingGame( GameModel2 model ) {
            super( model );
        }
    }

    /**
     *
     */
    public void newGame() {
        setState( new GameSettingsState( this ) );
    }

    //    public static class GameSettingsStateView extends GameSettingsState {
    //        public GameSettingsStateView( GameModel2 model ) {
    //            super( model );
    //        }
    //
    //        public PNode getNode() {
    //            final GameSettingsPanel panel = new GameSettingsPanel( new IntegerRange( 1, 3 ) );
    //            final PNode gameSettingsNode = new PSwing( panel );
    //            panel.addGameSettingsPanelListener( new GameSettingsPanel.GameSettingsPanelAdapater() {
    //                @Override
    //                public void startButtonPressed() {
    //                    model.setState(new FirstChallengeState());
    //                }
    //            } );
    //        }
    //    }
}
