/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.faraday.module.BarMagnetModule;
import edu.colorado.phet.faraday.module.CompassGridModule;
import edu.colorado.phet.faraday.module.TransformerModule;


/**
 * FaradayApplicationModel is the application model for the PhET
 * "Faraday's Law" simulation.  It creates the clock and modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FaradayApplicationModel extends ApplicationModel {

    /**
     * Sole constructor.
     * 
     * @param title the application title
     * @param description the application description
     * @param version the application version
     * @param frameSetup info used to setup the application's frame
     */
    public FaradayApplicationModel( String title, String description, String version, FrameSetup frameSetup ) {

        super( title, description, version, frameSetup );

        // Clock
        boolean fixedDelay = true;
        this.setClock( new SwingTimerClock( FaradayConfig.TIME_STEP, FaradayConfig.WAIT_TIME, fixedDelay ) );

        // Simulation Modules
        CompassGridModule compassGridModule = new CompassGridModule( this );
        BarMagnetModule barMagnetModule = new BarMagnetModule( this );
        TransformerModule transformerModule = new TransformerModule( this );
        this.setModules( new Module[] { compassGridModule, barMagnetModule, transformerModule } );

        // Initial module to be displayed.
        this.setInitialModule( compassGridModule );
    }
}
