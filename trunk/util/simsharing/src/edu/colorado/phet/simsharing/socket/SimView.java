// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.socket;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import org.codehaus.jackson.map.ObjectMapper;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.gravityandorbits.simsharing.GravityAndOrbitsApplicationState;
import edu.colorado.phet.simsharing.GAOHelper;
import edu.colorado.phet.simsharing.TimeControlFrame;
import edu.colorado.phet.simsharing.messages.SessionID;

/**
 * @author Sam Reid
 */
public class SimView {
    private final Thread thread;
    private final TimeControlFrame timeControl;
    private GravityAndOrbitsApplication application;
    private final String[] args;
    private final RemoteActor<GravityAndOrbitsApplicationState> sampleSource;
    private final boolean autoplay;
    private final ObjectMapper mapper = new ObjectMapper();

    public SimView( final String[] args, final SessionID sessionID, RemoteActor<GravityAndOrbitsApplicationState> sampleSource, boolean playbackMode ) {
        this.args = args;
        this.sampleSource = sampleSource;
        this.autoplay = playbackMode;
        timeControl = new TimeControlFrame( sessionID );
        timeControl.setVisible( true );
        thread = new Thread( new Runnable() {
            public void run() {
                try {
                    doRun();
                }
                catch ( ClassNotFoundException e ) {
                    e.printStackTrace();
                }
                catch ( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
        timeControl.live.set( !playbackMode );
        if ( playbackMode ) { timeControl.playing.set( true ); }
    }

    private void doRun() throws ClassNotFoundException, IOException {
        while ( running ) {
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        if ( timeControl.playing.get() ) {//TODO: may need a sleep
                            timeControl.frameToDisplay.set( Math.min( timeControl.frameToDisplay.get() + 1, timeControl.maxFrames.get() ) );
                        }
                    }
                } );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }

            final int sampleIndex = timeControl.live.get() ? -1 : timeControl.frameToDisplay.get();
            final GravityAndOrbitsApplicationState sample = sampleSource.getSample( sampleIndex );
            try {
                SwingUtilities.invokeAndWait( new Runnable() {
                    public void run() {
                        sample.apply( application );

                        //TODO: we used to pass back the sample index so that we could handle live
                        if ( sampleIndex > 0 ) {
                            timeControl.maxFrames.set( sampleIndex );
                        }
                    }
                } );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
    }

    private void alignControls() {
        timeControl.setLocation( application.getPhetFrame().getX(), application.getPhetFrame().getY() + application.getPhetFrame().getHeight() + 1 );
        timeControl.setSize( application.getPhetFrame().getWidth(), timeControl.getPreferredSize().height );
    }

    boolean running = true;

    public void start() {
        //Have to launch from non-swing-thread otherwise receive:
        //Exception in thread "AWT-EventQueue-0" java.lang.Error: Cannot call invokeAndWait from the event dispatcher thread
        application = GAOHelper.launchApplication( args, new VoidFunction0() {
            public void apply() {
                //Just let the window close but do not system.exit the whole VM
                running = false;
                timeControl.setVisible( false );//TODO: detach listeners
            }
        } );
        application.getPhetFrame().setTitle( application.getPhetFrame().getTitle() + ": Teacher Edition" );
        application.getPhetFrame().addComponentListener( new ComponentAdapter() {
            @Override
            public void componentMoved( ComponentEvent e ) {
                alignControls();
            }

            @Override
            public void componentResized( ComponentEvent e ) {
                alignControls();
            }
        } );
        alignControls();
        application.getIntro().setTeacherMode( true );
        application.getToScale().setTeacherMode( true );
        thread.start();
    }

}
