// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.teacher;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.simsharing.SimHelper;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.SessionRecord;
import edu.colorado.phet.simsharing.socketutil.Client;

/**
 * @author Sam Reid
 */
public class RecordingView extends JPanel {
    public JList recordingList;
    private SessionRecord lastShownRecording = new SessionRecord( new SessionID( -1, "hello" ), 0 );//dummy data so comparisons don't need to use null checks
    private Client client;

    public RecordingView( final Client client ) {
        super( new BorderLayout() );
        this.client = client;
        add( new JLabel( "All Sessions" ), BorderLayout.NORTH );
        recordingList = new JList() {{
            addListSelectionListener( new ListSelectionListener() {
                public void valueChanged( ListSelectionEvent e ) {
                    final SessionRecord selectedValue = (SessionRecord) recordingList.getSelectedValue();
                    System.out.println( selectedValue );
                    if ( selectedValue != null && !lastShownRecording.equals( selectedValue ) ) {
                        showRecording( selectedValue );
                        lastShownRecording = selectedValue;
                    }
                }
            } );
        }};
        add( new JScrollPane( recordingList ), BorderLayout.CENTER );
        add( new JButton( "Clear" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    try {
                        client.tell( new ClearSessions() );
                    }
                    catch ( IOException e1 ) {
                        e1.printStackTrace();
                    }
                }
            } );
        }}, BorderLayout.SOUTH );

        new Thread( new Runnable() {
            public void run() {

                //TODO: Kill this thread after view is closed
                while ( true ) {
                    try {
                        //Allow a long timeout here since it may take a long time to deliver a large list of recordings
                        Thread.sleep( 1000 );
                        final SessionList[] list = new SessionList[1];
                        list[0] = (SessionList) client.ask( new ListAllSessions() );
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                recordingList.setListData( list[0].toArray() );//TODO: remember user selection when list is refreshed
                            }
                        } );
                    }
                    catch ( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
        } ).start();
    }

    private void showRecording( SessionRecord sessionID ) {
        new SimView( sessionID.getSessionID(), SimHelper.LAUNCHER.apply() ).start();
    }
}