/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.fissiononenucleus;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Resources;


public class FissionOneNucleusControlPanel extends ControlPanel {
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param alphaRadiationModule
     * @param parentFrame parent frame, for creating dialogs
     */
    public FissionOneNucleusControlPanel( FissionOneNucleusModule alphaRadiationModule, Frame parentFrame ) {
        super();
        
        // Set the control panel's minimum width.
        int minimumWidth = NuclearPhysics2Resources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create sub-panels
        // TODO: Add the pannels.
        
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
}
