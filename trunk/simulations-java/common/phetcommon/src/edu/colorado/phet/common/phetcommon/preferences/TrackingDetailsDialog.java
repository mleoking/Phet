package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.GrayRectWorkaroundDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that appears when you press the "Details" button in the Tracking preferences panel.
 */
public class TrackingDetailsDialog extends GrayRectWorkaroundDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.tracking.details.title" );
    private static final String DESCRIPTION = PhetCommonResources.getString( "Common.tracking.details.description" );
    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );

    public TrackingDetailsDialog( Frame owner, ITrackingInfo trackingInfo ) {
        super( owner );
        init( trackingInfo );
    }
    
    public TrackingDetailsDialog( Dialog owner, ITrackingInfo trackingInfo ) {
        super( owner );
        init( trackingInfo );
    }
    
    private void init( ITrackingInfo trackingInfo ) {
        
        setTitle( TITLE );
        setModal( true );
        setResizable( false ); //TODO layout doesn't adjust properly when resized

        JComponent description = createDescription();
        JComponent report = createReport( trackingInfo );
        JComponent buttonPanel = createButtonPanel();

        JPanel panel = new JPanel();

        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 5, 5, 5, 5 ) ); // top, left, bottom, right
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( description, row++, column );
        layout.addComponent( report, row++, column );
        layout.addFilledComponent( buttonPanel, row++, column, GridBagConstraints.HORIZONTAL );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    protected JComponent createDescription() {
        return new JLabel( DESCRIPTION );
    }
    
    protected JComponent createReport( ITrackingInfo trackingInfo ) {
        
        final JTextArea textArea = new JTextArea( "" );
        final String text = trackingInfo.getHumanReadableTrackingInformation();
        if ( text != null ) {
            textArea.setText( text );
        }
        textArea.setEditable( false );
        
        JScrollPane scrollPane = new JScrollPane( textArea );
        scrollPane.setPreferredSize( new Dimension( scrollPane.getPreferredSize().width + 30, 200 ) );
        
        // this ensures that the first line of text is at the top of the scrollpane
        textArea.setCaretPosition( 0 );
        
        return scrollPane;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton closeButton = new JButton( CLOSE_BUTTON );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });
        panel.add( closeButton );
        return panel;
    }
}
