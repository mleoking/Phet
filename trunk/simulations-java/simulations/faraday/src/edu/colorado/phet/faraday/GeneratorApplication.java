/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.faraday;

import java.io.IOException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.faraday.control.menu.OptionsMenu;
import edu.colorado.phet.faraday.module.*;

/**
 * GeneratorApplication is the main application for the 
 * "Generator" simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GeneratorApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     */
    public GeneratorApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    private void initModules() {
        
        BarMagnetModule barMagnetModule = new BarMagnetModule( false /* wiggleMeEnabled */ );
        barMagnetModule.setShowEarthVisible( false );
        addModule( barMagnetModule );
        
        PickupCoilModule pickupCoilModule = new PickupCoilModule();
        addModule( pickupCoilModule );
        
        ElectromagnetModule electromagnetModule = new ElectromagnetModule();
        addModule( electromagnetModule );
        
        TransformerModule transformerModule = new TransformerModule();
        addModule( transformerModule );
        
        GeneratorModule generatorModule = new GeneratorModule();
        addModule( generatorModule );
        
        setStartModule( generatorModule );
    }

    /**
     * Initializes the menubar.
     */
    private void initMenubar() {
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu( this );
        getPhetFrame().addMenu( optionsMenu );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new GeneratorApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, appConstructor, FaradayConstants.PROJECT_NAME, FaradayConstants.FLAVOR_GENERATOR );
        appConfig.launchSim();
    }
}