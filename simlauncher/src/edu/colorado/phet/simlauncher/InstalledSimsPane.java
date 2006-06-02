/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher;

import edu.colorado.phet.simlauncher.menus.InstalledSimulationPopupMenu;
import edu.colorado.phet.simlauncher.util.ChangeEventChannel;
import edu.colorado.phet.simlauncher.actions.LaunchSimulationAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * InstalledSimsPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsPane extends JPanel implements Catalog.ChangeListener,
                                                         SimulationContainer {

    private SimulationTable simTable;
    private SimulationTable.SimulationComparator simTableSortType = Options.instance().getInstalledSimulationsSortType();
//    private SimulationTable.SimulationComparator simTableSortType = SimulationTable.NAME_SORT;
    private JScrollPane simTableScrollPane;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();
    private JButton launchBtn;
    private GridBagConstraints tableGbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                                  GridBagConstraints.CENTER,
                                                                  GridBagConstraints.NONE,
                                                                  new Insets( 0, 0, 0, 0 ), 0, 0 );
    private GridBagConstraints launchButtonGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                                         GridBagConstraints.CENTER,
                                                                         GridBagConstraints.NONE,
                                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );

    /**
     *
     */
    public InstalledSimsPane() {
        super( new GridBagLayout() );

        // Listen for new simulations, and for changes in the installed and uninstalled simulations lists
        Catalog.instance().addChangeListener( this );

        // Launch button
        launchBtn = new JButton( "Launch" );
        // Add an extension to the Launch action that resorts the table if the sort order is
        // most-recently-used
        launchBtn.addActionListener( new LaunchSimulationAction( this, this ) {
            public void actionPerformed( ActionEvent e ) {
                super.actionPerformed( e );
                if( Options.instance().getInstalledSimulationsSortType().equals( SimulationTable.MOST_RECENTLY_USED_SORT )) {
                    updateSimTable();
                }
            }
        } );
        launchBtn.setEnabled( false );
        add( launchBtn, launchButtonGbc );

        updateSimTable();

        // Listen for changes in Options
        Options.instance().addListener( new Options.ChangeListener() {
            public void optionsChanged( Options.ChangeEvent event ) {
                if( simTableSortType != event.getOptions().getInstalledSimulationsSortType() ) {
                    simTableSortType = event.getOptions().getInstalledSimulationsSortType();
                }
                updateSimTable();
            }
        } );
    }

    /**
     *
     */
    public void addChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    /**
     *
     */
    public void removeChangeListener( ChangeEventChannel.ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    private void updateSimTable() {
        if( simTable != null ) {
            simTableScrollPane.remove( simTable );
            remove( simTableScrollPane );
        }

        simTable = new SimulationTable( Catalog.instance().getInstalledSimulations(),
                                        Options.instance().isShowInstalledThumbnails(),
                                        simTableSortType );

        // Add mouse handler
        simTable.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                handleSimulationSelection( e );
            }

            public void mousePressed( MouseEvent e ) {
//                handleSimulationSelection( e );
            }

            public void mouseReleased( MouseEvent e ) {
//                handleSimulationSelection( e );
            }
        } );

        simTableScrollPane = new JScrollPane( simTable );
        add( simTableScrollPane, tableGbc );
        revalidate();
    }

    /**
     * @param event
     */
    private void handleSimulationSelection( MouseEvent event ) {
        Simulation sim = simTable.getSelection();
        launchBtn.setEnabled( sim != null );

        // If it's a right click and a simulation is selected, pop up the context menu
        if( event.isPopupTrigger() && sim != null ) {
            new InstalledSimulationPopupMenu( sim ).show( this, event.getX(), event.getY() );
        }

        // If a double left click, launch the simulation
        if( !event.isPopupTrigger() && event.getClickCount() == 2 ) {
            System.out.println( "InstalledSimsPane.handleSimulationSelection" );
            sim.launch();
        }

        // Notify change listeners
        changeEventChannel.notifyChangeListeners( InstalledSimsPane.this );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Catalog.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void catatlogChanged( Catalog.ChangeEvent event ) {
        updateSimTable();
        changeEventChannel.notifyChangeListeners( InstalledSimsPane.this );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SimulationContainer
    //--------------------------------------------------------------------------------------------------

    public Simulation getSimulation() {
        return simTable.getSimulation();
    }
}
