package edu.colorado.phet.common.phetcommon.preferences;

import javax.swing.*;

public class PreferencesScopePanel extends JPanel {
    public PreferencesScopePanel( IPreferences iTrackingPreferences ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( new JLabel( "Make my preferences apply to:" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton thisOnly = new JRadioButton( "this simulation only", !iTrackingPreferences.isForAllSimulations() );
        JRadioButton all = new JRadioButton( "all PhET simulations", iTrackingPreferences.isForAllSimulations() );
        buttonGroup.add( thisOnly );
        buttonGroup.add( all );
        add( thisOnly );
        add( all );
    }
}
