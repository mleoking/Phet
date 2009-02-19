/* Copyright 2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.updates.IAskMeLaterStrategy;
import edu.colorado.phet.common.phetcommon.updates.InstallerAskMeLaterStrategy;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Notifies the user about a recommended installer update that was automatically discovered.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class InstallerAutomaticUpdateDialog extends InstallerAbstractUpdateDialog {
    
    private final IAskMeLaterStrategy askMeLaterStrategy;
    
    public InstallerAutomaticUpdateDialog( Frame owner, IAskMeLaterStrategy askMeLaterStrategy ) {
        super( owner );
        this.askMeLaterStrategy = askMeLaterStrategy;
        initGUI();
    }
    
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( new UpdateButton( this ) );
        buttonPanel.add( new AskMeLaterButton( this, askMeLaterStrategy ) );
        buttonPanel.add( new MoreButton( this ) );
        return buttonPanel;
    }
    
    /*
     * Test, this edits the real preferences file!
     */
     public static void main( String[] args ) {
         InstallerAutomaticUpdateDialog dialog = new InstallerAutomaticUpdateDialog( null, new InstallerAskMeLaterStrategy() );
         dialog.addWindowListener( new WindowAdapter() {
             public void windowClosing( WindowEvent e ) {
                 System.exit( 0 );
             }
             public void windowClosed( WindowEvent e ) {
                 System.exit( 0 );
             }
         } );
         SwingUtils.centerWindowOnScreen( dialog );
         dialog.setVisible( true );
     }

}
