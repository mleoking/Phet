package edu.colorado.phet.forces1d.common.plotdevice;

import edu.colorado.phet.common.view.util.ImageLoader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 31, 2004
 * Time: 12:55:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class DefaultPlaybackPanel extends JPanel {
    private JButton playback;
    private JButton pause;
    private JButton rewind;

    public DefaultPlaybackPanel( final PlotDeviceModel plotDeviceModel ) throws IOException {
        ImageLoader cil = new ImageLoader();

        String root = "images/icons/java/media/";
        BufferedImage playU = cil.loadImage( root + "Play24.gif" );
        BufferedImage pauseU = cil.loadImage( root + "Pause24.gif" );
        BufferedImage rewU = cil.loadImage( root + "Rewind24.gif" );
        ImageIcon playIcon = new ImageIcon( playU );
        ImageIcon pauseIcon = new ImageIcon( pauseU );
        ImageIcon rewIcon = new ImageIcon( rewU );

        playback = new JButton( "Playback", playIcon );
        playback.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                plotDeviceModel.setPlaybackMode();
                plotDeviceModel.setPaused( false );
            }
        } );


        pause = new JButton( "Pause", pauseIcon );
        pause.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                plotDeviceModel.setPaused( true );
            }
        } );

        rewind = new JButton( "Rewind", rewIcon );
        rewind.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                plotDeviceModel.setPaused( true );
                plotDeviceModel.rewind();
            }
        } );

        add( playback );
        add( pause );
        add( rewind );

        plotDeviceModel.addListener( new PlotDeviceModel.Listener() {
            public void recordingStarted() {
                setButtons( false, true, false );
            }

            public void recordingPaused() {
                setButtons( true, false, true );
            }

            public void recordingFinished() {
                setButtons( true, false, true );
            }

            public void playbackStarted() {
                setButtons( false, true, true );
            }

            public void playbackPaused() {
                setButtons( true, false, true );
            }

            public void playbackFinished() {
                setButtons( false, false, true );
            }

            public void reset() {
                setButtons( false, false, false );
            }

            public void rewind() {
                setButtons( true, false, false );
            }
        } );
    }

    private void setButtons( boolean play, boolean paused, boolean rew ) {

        playback.setEnabled( play );
        pause.setEnabled( paused );
        rewind.setEnabled( rew );
    }
}
