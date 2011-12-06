// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingActions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJMenu;
import edu.colorado.phet.moleculeshapes.MoleculeShapesColor;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.Objects;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.Parameters;
import edu.colorado.phet.moleculeshapes.MoleculeShapesSimSharing.Values;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * Displays a "Teacher" menu that allows the user to select between normal colors and a "White Background" mode
 */
public class TeachersMenu extends SimSharingJMenu {
    public TeachersMenu() {
        super( PhetCommonResources.getString( "Common.TeacherMenu" ) );
        setMnemonic( PhetCommonResources.getChar( "Common.TeacherMenu.mnemonic", 'T' ) );
        add( new JCheckBoxMenuItem( PhetCommonResources.getString( "Common.WhiteBackground" ) ) {{
            setSelected( false );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {

                    SimSharingEvents.sendEvent( Objects.BACKGROUND_COLOR, SimSharingActions.CHANGED, param( Parameters.COLOR, isSelected() ? Values.WHITE : Values.BLACK ) );

                    if ( isSelected() ) {
                        MoleculeShapesColor.PROJECTOR.apply( MoleculeShapesColor.handler );
                    }
                    else {
                        MoleculeShapesColor.DEFAULT.apply( MoleculeShapesColor.handler );
                    }
                }
            } );
            setMnemonic( PhetCommonResources.getChar( "Common.WhiteBackground.mnemonic", 'W' ) );
        }} );
    }
}
