package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PlayPauseButton;
import edu.colorado.phet.movingman.model.MutableBoolean;
import edu.colorado.phet.recordandplayback.model.RecordAndPlaybackModel;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class GoButton extends PNode {
    public GoButton(final RecordAndPlaybackModel recordAndPlaybackModel, final MutableBoolean modeSelected) {
        this(recordAndPlaybackModel, null, modeSelected);
    }

    public GoButton(final RecordAndPlaybackModel recordAndPlaybackModel,
                    PNode parent,//To be centered in, null if none  
                    final MutableBoolean modeSelected) {
        final PlayPauseButton button = new PlayPauseButton(30);
        SimpleObserver updateButtonState = new SimpleObserver() {
            public void update() {
                button.setPlaying(!recordAndPlaybackModel.isPaused());
            }
        };
        recordAndPlaybackModel.addObserver(updateButtonState);
        updateButtonState.update();
        button.addListener(new PlayPauseButton.Listener() {
            public void playbackStateChanged() {
                recordAndPlaybackModel.setPaused(!button.isPlaying());
            }
        });
        final SimpleObserver observer = new SimpleObserver() {
            public void update() {
                button.setVisible(modeSelected.getValue());
            }
        };
        observer.update();
        modeSelected.addObserver(observer);
        recordAndPlaybackModel.addObserver(observer);
        if (parent != null) {
            button.setOffset(parent.getFullBounds().getMaxX() - button.getFullBounds().getWidth(), parent.getFullBounds().getMaxY());
        }
        addChild(button);
    }
}
