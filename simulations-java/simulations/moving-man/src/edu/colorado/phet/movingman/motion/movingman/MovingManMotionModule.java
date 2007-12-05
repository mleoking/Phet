package edu.colorado.phet.movingman.motion.movingman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.common.timeseries.ui.TimeSeriesControlPanel;
import edu.colorado.phet.movingman.ArrowPanel;
import edu.colorado.phet.movingman.common.JSAudioPlayer;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:42:37 PM
 */
public class MovingManMotionModule extends Module implements ArrowPanel.IArrowPanelModule {
    private MovingManMotionModel movingManMotionModel;
    private MovingManMotionSimPanel movingManMotionSimPanel;
    private boolean audioEnabled = true;

    public MovingManMotionModule( ConstantDtClock clock ) {
        super( "Moving Man", clock );
        movingManMotionModel = new MovingManMotionModel( clock );
        movingManMotionModel.addListener( new MovingManMotionModel.Listener() {
            public void crashedMin() {
                playSound();
            }

            public void crashedMax() {
                playSound();
            }
        } );

        movingManMotionSimPanel = new MovingManMotionSimPanel( movingManMotionModel );
        setSimulationPanel( movingManMotionSimPanel );
        setClockControlPanel( new MovingManSouthControlPanel( this, movingManMotionModel.getTimeSeriesModel(), clock.getDt() / 2, clock.getDt() * 2 ) );
        setLogoPanelVisible( false );
    }

    private void playSound() {
        if ( audioEnabled ) {
            JSAudioPlayer.playNoBlock( Thread.currentThread().getContextClassLoader().getResource( "moving-man/audio/smash0.wav" ) );
        }
    }

    public void activate() {
        super.activate();
        movingManMotionModel.startRecording();
    }

    public void setShowVelocityVector( boolean selected ) {
        movingManMotionSimPanel.setShowVelocityVector( selected );
    }

    public void setShowAccelerationVector( boolean selected ) {
        movingManMotionSimPanel.setShowAccelerationVector( selected );
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    private class MovingManSouthControlPanel extends HorizontalLayoutPanel {
        public MovingManSouthControlPanel( MovingManMotionModule seriesModel, TimeSeriesModel timeSeriesModel, double min, double max ) {
            add( new TimeSeriesControlPanel( timeSeriesModel, min, max ) );
            add( new ArrowPanel( MovingManMotionModule.this ) );
            add( new SoundCheckBox( seriesModel ) );
        }

        private class SoundCheckBox extends JCheckBox {
            public SoundCheckBox( final MovingManMotionModule seriesModel ) {
                super( "Sound" );
                setSelected( seriesModel.isAudioEnabled() );
                addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        seriesModel.setAudioEnabled( isSelected() );
                    }
                } );
            }
        }
    }

    private void setAudioEnabled( boolean audio ) {
        this.audioEnabled = audio;
    }
}
