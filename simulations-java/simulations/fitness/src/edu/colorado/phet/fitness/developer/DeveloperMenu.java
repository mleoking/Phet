/* Copyright 2007, University of Colorado */

package edu.colorado.phet.fitness.developer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.fitness.FitnessApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu implements ActionListener {

    private FitnessApplication _app;
    private JCheckBoxMenuItem _developerControlsItem;
    private JDialog _developerControlsDialog;

    public DeveloperMenu( FitnessApplication app ) {
        super( "Developer" );

        _app = app;

        _developerControlsItem = new JCheckBoxMenuItem( "Developer Controls..." );
        add( _developerControlsItem );
        _developerControlsItem.addActionListener( this );
    }

    public void actionPerformed( ActionEvent event ) {
        if ( event.getSource() == _developerControlsItem ) {
            if ( _developerControlsItem.isSelected() ) {
                Frame owner = PhetApplication.instance().getPhetFrame();
                _developerControlsDialog = new DeveloperControlsDialog( owner, _app );
                _developerControlsDialog.show();
                _developerControlsDialog.addWindowListener( new WindowAdapter() {
                    public void windowClosed( WindowEvent e ) {
                        cleanup();
                    }

                    public void windowClosing( WindowEvent e ) {
                        cleanup();
                    }

                    private void cleanup() {
                        _developerControlsItem.setSelected( false );
                        _developerControlsDialog = null;
                    }
                } );
            }
            else {
                _developerControlsDialog.dispose();
            }
        }
    }
}
