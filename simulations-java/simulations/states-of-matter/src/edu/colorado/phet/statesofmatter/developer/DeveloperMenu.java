/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.statesofmatter.StatesOfMatterApplication;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author John Blanco
 */
public class DeveloperMenu extends JMenu implements ActionListener {

    private StatesOfMatterApplication _app;
    private JCheckBoxMenuItem _developerControlsItem;
    private JDialog _developerControlsDialog;

    public DeveloperMenu( StatesOfMatterApplication app ) {
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