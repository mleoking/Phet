/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.DialogUtils;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTStrings;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.util.CursorUtils;


/**
 * AbstractControlPanel is the base class for all control panels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    protected static final Font TITLE_FONT = OTConstants.CONTROL_PANEL_TITLE_FONT;
    protected static final Font CONTROL_FONT = OTConstants.CONTROL_PANEL_CONTROL_FONT;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private AbstractModule _module; // module that this control panel is associated with
    private JButton _resetButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param module
     */
    public AbstractControlPanel( AbstractModule module ) {
        super();
        setInsets( new Insets( 0, 3, 0, 3 ) );
        _module = module;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the module that this control panel is part of.
     * 
     * @return the module
     */
    public AbstractModule getModule() {
        return _module;
    }
    
    //----------------------------------------------------------------------------
    // Add things to the control panel
    //----------------------------------------------------------------------------
    
    /**
     * Gets the reset button, usually for attaching a help item to it.
     * 
     * @return the reset button
     */
    public JButton getResetButton() {
        return _resetButton;
    }
    
    /**
     * Adds a Reset button to the control panel.
     * The button handler calls the module's reset method.
     */
    public void addResetButton() {
        _resetButton = new JButton( OTStrings.RESET_ALL );
        _resetButton.addActionListener( new ActionListener() { 
            public void actionPerformed( ActionEvent e ) {
                Frame frame = PhetApplication.instance().getPhetFrame();
                int option = DialogUtils.showConfirmDialog( frame, OTStrings.CONFIRM_RESET_ALL, JOptionPane.YES_NO_OPTION );
                if ( option == JOptionPane.YES_OPTION ) {
                    CursorUtils.setWaitCursorEnabled( true );
                    _module.resetAll();
                    CursorUtils.setWaitCursorEnabled( false );
                }
            }
        } );
        addControl( _resetButton );
    }
    
    /**
     * Sets the minumum width of the control panel.
     * 
     * @param minimumWidth
     */
    public void setMinumumWidth( int minimumWidth ) {
        JPanel fillerPanel = new JPanel();
        fillerPanel.setLayout( new BoxLayout( fillerPanel, BoxLayout.X_AXIS ) );
        fillerPanel.add( Box.createHorizontalStrut( minimumWidth ) );
        addControlFullWidth( fillerPanel );
    }
}
