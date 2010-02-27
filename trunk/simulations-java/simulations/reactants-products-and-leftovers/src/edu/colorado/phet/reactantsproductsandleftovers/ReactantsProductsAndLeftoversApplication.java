/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers;

import java.awt.Frame;

import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.reactantsproductsandleftovers.dev.DevTestReactionsMenuItem;
import edu.colorado.phet.reactantsproductsandleftovers.module.game.GameModule;
import edu.colorado.phet.reactantsproductsandleftovers.module.realreaction.RealReactionModule;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopModule;

/**
 * The main application for the "Reactants, Products and Leftovers" simulation.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ReactantsProductsAndLeftoversApplication extends PiccoloPhetApplication {

    public ReactantsProductsAndLeftoversApplication( PhetApplicationConfig config ) {
        this( config, false /* researchFlag */ );
    }
    
    public ReactantsProductsAndLeftoversApplication( PhetApplicationConfig config, boolean researchFlag ) {
        super( config );
        
        // modules
        Frame parentFrame = getPhetFrame();
        addModule( new SandwichShopModule( parentFrame ) );
        addModule( new RealReactionModule( parentFrame ) );
        addModule( new GameModule( parentFrame, researchFlag ) );
        
        // menu items
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new DevTestReactionsMenuItem() );
    }

    public static void main( final String[] args ) throws ClassNotFoundException {
        new PhetApplicationLauncher().launchSim( args, RPALConstants.PROJECT_NAME, RPALConstants.FLAVOR_RPAL, ReactantsProductsAndLeftoversApplication.class );
    }
}
