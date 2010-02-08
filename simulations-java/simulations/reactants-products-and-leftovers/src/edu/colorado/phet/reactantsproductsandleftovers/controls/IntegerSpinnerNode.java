/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.controls;

import java.awt.Toolkit;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.NumberEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Spinner node, encapsulates the use of JSpinner in case we can to switch to some custom control.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntegerSpinnerNode extends PNode {
    
    private final IntegerRange range;
    private final EventListenerList listeners;
    private final JSpinner spinner;
    private final JFormattedTextField textField;
    
    public IntegerSpinnerNode( IntegerRange range ) {
        super();
        addInputEventListener( new CursorHandler() );
        
        this.range = range;
        listeners = new EventListenerList();

        spinner = new JSpinner();
        
        // number model
        spinner.setModel( new SpinnerNumberModel( range.getDefault(), range.getMin(), range.getMax(), 1 ) );
        
        // editor
        NumberEditor editor = new NumberEditor( spinner );
        spinner.setEditor( editor );
        
        // text field, commits when Enter is pressed or focus is lost
        textField = editor.getTextField();
        textField.setColumns( String.valueOf( range.getMax() ).length() );
        textField.addKeyListener( new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                    commit();
                }
            }
        });
        textField.addFocusListener( new FocusAdapter() {
            
            public void focusLost( FocusEvent e ) {
                commit();
            }
            
            /*
             * Workaround to select contents when textfield get focus.
             * See bug ID 4699955 at bugs.sun.com
             */
            public void focusGained( FocusEvent e ) {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        textField.selectAll();
                    }
                });
            }
        } );
        
        // propagate change events
        spinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireStateChange();
            }
        });
        
        // do this *after* making changes to the spinner or there will be problems, see #1824
        addChild( new PSwing( spinner ) );
    }
    
    public void setValue( int value ) {
        spinner.setValue( new Integer( value ) );
    }

    public int getValue() {
        return ( (Integer) spinner.getValue() ).intValue();
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( ChangeListener.class, listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( ChangeListener.class, listener );
    }
    
    private void fireStateChange() {
        ChangeEvent e = new ChangeEvent( this );
        for ( ChangeListener listener : listeners.getListeners( ChangeListener.class) ) {
            listener.stateChanged( e );
        }
    }
    
    /*
     * If we can't commit the value in the text field, then revert.
     */
    private void commit() {
        try {
            //TODO this converts invalid entries like "12abc" to "12", standard JSpinner behavior but not desirable for PhET
            spinner.commitEdit();
        }
        catch ( ParseException pe ) {
            textField.setValue( getValue() ); // revert, sync textfield to value
            Toolkit.getDefaultToolkit().beep();
            showInvalidValueDialog();
            textField.selectAll();
        }
    }
    
    private void showInvalidValueDialog() {
        Object[] args = { new Integer( range.getMin() ), new Integer( range.getMax() ) };
        String message = MessageFormat.format( RPALStrings.MESSAGE_VALUE_OUT_OF_RANGE, args );
        PhetOptionPane.showErrorDialog( spinner, message );
    }
}
