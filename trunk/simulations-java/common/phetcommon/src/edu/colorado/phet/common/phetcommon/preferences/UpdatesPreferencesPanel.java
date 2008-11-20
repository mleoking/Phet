package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.IManualUpdateChecker;

/**
 * Panel for displaying preferences for the updates feature.
 */
public class UpdatesPreferencesPanel extends JPanel {
    
    private static final String UPDATES_ENABLED = PhetCommonResources.getString( "Common.updates.automaticallyCheck" );
    private static final String CHECK_FOR_UPDATES = PhetCommonResources.getString( "Common.HelpMenu.CheckForUpdates" );
    
    private final JCheckBox updatesEnabledCheckBox;
    
    public UpdatesPreferencesPanel( final IManualUpdateChecker iCheckForUpdates, boolean updatesEnabled ) {

        // enable
        updatesEnabledCheckBox = new JCheckBox( UPDATES_ENABLED, updatesEnabled );
        
        // check
        JButton checkForUpdatesButton = new JButton( CHECK_FOR_UPDATES );
        checkForUpdatesButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.CHECK_FOR_UPDATE_PRESSED );
                iCheckForUpdates.checkForUpdates();
            }
        } );
        
        // layout
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        add( Box.createRigidArea( new Dimension( 50, 20 ) ), constraints );
        add( updatesEnabledCheckBox, constraints );
        add( Box.createRigidArea( new Dimension( 50, 10 ) ), constraints );
        add( checkForUpdatesButton, constraints );
    }
    
    public boolean isUpdatesEnabled() {
        return updatesEnabledCheckBox.isSelected();
    }
}
