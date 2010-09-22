/* Copyright 2007, University of Colorado */

package edu.colorado.phet.buildanatom;

import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.buildanatom.module.BuildAnAtomModule;
import edu.colorado.phet.buildanatom.module.GameModule;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * The main application for this simulation.
 */
public class BuildAnAtomApplication extends PiccoloPhetApplication {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param config the configuration for this application
     */
    public BuildAnAtomApplication( PhetApplicationConfig config )
    {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /*
     * Initializes the modules.
     */
    private void initModules() {
        
        Frame parentFrame = getPhetFrame();

        Module firstModule = new BuildAnAtomModule( parentFrame );
        addModule( firstModule );

        Module secondModule = new GameModule( parentFrame );
        secondModule.setName( BuildAnAtomStrings.TITLE_GAME_MODULE );
        addModule( secondModule );
    }

    /*
     * Initializes the menubar.
     */
    private void initMenubar() {

        final PhetFrame frame = getPhetFrame();

        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        // add menu items here, or in a subclass on OptionsMenu
        if ( optionsMenu.getMenuComponentCount() > 0 ) {
            frame.addMenu( optionsMenu );
        }

        // Developer menu
        JMenu developerMenu = frame.getDeveloperMenu();
        // add items to the Developer menu here...
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    public static void main( final String[] args ) throws ClassNotFoundException {
        /* 
         * If you want to customize your application (look-&-feel, window size, etc) 
         * create your own PhetApplicationConfig and use one of the other launchSim methods
         */
        new PhetApplicationLauncher().launchSim( args, BuildAnAtomConstants.PROJECT_NAME, BuildAnAtomApplication.class );
    }
}
