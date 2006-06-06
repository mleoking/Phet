/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.menus;

import edu.colorado.phet.simlauncher.Simulation;
import edu.colorado.phet.simlauncher.actions.InstallSimAction;

import javax.swing.*;

/**
 * UninstalledSimPopupMenu
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class UninstalledSimPopupMenu extends JPopupMenu {

    /**
     * 
     * @param simulation
     */
    public UninstalledSimPopupMenu( Simulation simulation ) {

        // Install menu item
        JMenuItem launchMI = new JMenuItem( "Install");
        launchMI.addActionListener( new InstallSimAction( simulation, this ) );
        add( launchMI );
    }
}
