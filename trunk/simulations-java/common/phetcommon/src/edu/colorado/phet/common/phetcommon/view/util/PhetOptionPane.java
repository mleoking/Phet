
package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * PhetOptionPane provides basic JOptionPane functionality, but uses a PaintImmediateDialog
 * to address issues with AWT thread priority (see Unfuddle #89).
 * <p>
 * All dialogs are modal and non-resizable.
 * Return values are identical to (and obtained from) JOptionPane.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhetOptionPane {
    
    public static int showMessageDialog( Component parent, String message ) {
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.message" );
        return showJOptionPaneDialog( parent, message, title, JOptionPane.INFORMATION_MESSAGE );
    }
    
    public static int showMessageDialog( Component parent, String message, String title ) {
        return showJOptionPaneDialog( parent, message, title, JOptionPane.INFORMATION_MESSAGE );
    }
    
    public static int showMessageDialog( Component parent, String message, String title, int messageType ) {
        return showJOptionPaneDialog( parent, message, title, messageType );
    }
    
    public static int showOKCancelDialog( Component parent, String message ) {
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
        return showJOptionPaneDialog( parent, message, title, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );
    }
    
    public static int showOKCancelDialog( Component parent, String message, String title ) {
        return showJOptionPaneDialog( parent, message, title, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION );
    }
    
    public static int showYesNoDialog( Component parent, String message ) {
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.confirm" );
        return showJOptionPaneDialog( parent, message, title, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION );
    }
    
    public static int showYesNoDialog( Component parent, String message, String title ) {
        return showJOptionPaneDialog( parent, message, title, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION );
    }
    
    public static int showWarningDialog( Component parent, String message ) {
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.warning" );
        return showJOptionPaneDialog( parent, message, title, JOptionPane.WARNING_MESSAGE );
    }
    
    public static int showErrorDialog( Component parent, String message ) {
        String title = PhetCommonResources.getInstance().getLocalizedString( "Common.title.error" );
        return showJOptionPaneDialog( parent, message, title, JOptionPane.ERROR_MESSAGE );
    }
    
    private static int showJOptionPaneDialog( Component parent, String message, String title, int messageType ) {
        return showJOptionPaneDialog( parent, message, title, messageType, JOptionPane.DEFAULT_OPTION );
    }
   
    /*
     * Shows a dialog using a JOptionPane.
     * Return values are obtained from JOptionPane, see its javadoc.
     */
    private static int showJOptionPaneDialog( Component parent, String message, String title, int messageType, int optionType ) {

        // Use a JOptionPane to get the right dialog look and layout
        JOptionPane pane = new JOptionPane( message, messageType, optionType );
        pane.selectInitialValue();
        
        // Create our own dialog to solve issue #89
        final JDialog dialog = createDialog( parent, title );
        dialog.getContentPane().add( pane );

        // Close the dialog when the user makes a selection
        pane.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent event ) {
                if ( event.getPropertyName().equals( JOptionPane.VALUE_PROPERTY ) ) {
                    dialog.setVisible( false );
                }
            }
        } );

        // pack the dialog first so it will be centered correctly
        dialog.pack();
        SwingUtils.centerDialog( dialog, parent );
        dialog.setVisible( true );
        
        // blocks here until user makes a choice
        dialog.dispose();

        // any not-int selection assumes the user closed the dialog via the window decoration
        int returnValue = JOptionPane.CLOSED_OPTION;
        Object paneValue = pane.getValue();
        if ( paneValue instanceof Integer ) {
            returnValue = ((Integer)paneValue).intValue(); 
        }
        return returnValue;
    }

    /*
     * Creates a PaintImmediateDialog.
     */
    private static JDialog createDialog( Component parent, String title ) {
        JDialog dialog = null;
        Window window = getWindowForComponent( parent );
        if ( window instanceof Frame ) {
            dialog = new PaintImmediateDialog( (Frame) window, title );
        }
        else {
            dialog = new PaintImmediateDialog( (Dialog) window, title );
        }
        dialog.setModal( true );
        dialog.setResizable( false );
        return dialog;
    }
    
    /*
     * JOptionPane.getWindowForComponent isn't public, reproduced here
     */
    private static Window getWindowForComponent( Component parentComponent ) throws HeadlessException {
        if ( parentComponent == null ) {
            return JOptionPane.getRootFrame();
        }
        if ( parentComponent instanceof Frame || parentComponent instanceof Dialog ) {
            return (Window) parentComponent;
        }
        return getWindowForComponent( parentComponent.getParent() );
    }
    
    /* tests */
    public static void main( String[] args ) {
        int value = PhetOptionPane.showMessageDialog( null, "message", "title" );
        System.out.println( "value=" + value );
        value = PhetOptionPane.showOKCancelDialog( null, "ok question", "title" );
        System.out.println( "value=" + value );
        value = PhetOptionPane.showYesNoDialog( null, "yes/no question", "title" );
        System.out.println( "value=" + value );
        value = PhetOptionPane.showWarningDialog( null, "warning" );
        System.out.println( "value=" + value );
        value = PhetOptionPane.showErrorDialog( null, "error" );
        System.out.println( "value=" + value );
        System.exit( 0 );
    }
}
