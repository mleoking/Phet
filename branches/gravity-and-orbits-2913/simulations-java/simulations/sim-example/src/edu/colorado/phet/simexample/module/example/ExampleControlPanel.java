// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simexample.module.example;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.simexample.SimExampleResources;
import edu.colorado.phet.simexample.control.ExampleSubPanel;

/**
 * The control panel for the "Example" module.
 */
public class ExampleControlPanel extends ControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private ExampleSubPanel _exampleSubPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public ExampleControlPanel( ExampleModule module, Frame parentFrame, ExampleModel model ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = SimExampleResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        _exampleSubPanel = new ExampleSubPanel( model.getExampleModelElement() );
        
        // Layout
        {
            addControlFullWidth( _exampleSubPanel );
            addSeparator();
            addResetAllButton( module );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        //XXX close any dialogs created via the control panel
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
    public ExampleSubPanel getExampleSubPanel() {
        return _exampleSubPanel;
    }

}