/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;
    private GravityAndOrbitsControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GravityAndOrbitsModule( Frame parentFrame ) {
        super( GravityAndOrbitsStrings.TITLE_EXAMPLE_MODULE, new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.CLOCK_DT ) );

        // Model
        GravityAndOrbitsClock clock = (GravityAndOrbitsClock) getClock();
        model = new GravityAndOrbitsModel( clock );

        // Canvas
        canvas = new GravityAndOrbitsCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new GravityAndOrbitsControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( GravityAndOrbitsStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( GravityAndOrbitsDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( clockControlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // reset the clock
        GravityAndOrbitsClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
