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

import edu.colorado.phet.simlauncher.util.ChangeEventChannel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * InstalledSimsPane
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class InstalledSimsPane extends JPanel implements Simulation.ChangeListener,
                                                         SimulationContainer,
                                                         ChangeEventChannel.ChangeEventSource {
    private SimulationTable simTable;
    private JScrollPane simTableScrollPane;
    private ChangeEventChannel changeEventChannel = new ChangeEventChannel();
    private JButton launchBtn;


    /**
     *
     */
    public InstalledSimsPane() {
        super( new GridBagLayout() );

        // Listen for new simulations, and for changes in the installed and uninstalled simulations lists
        Simulation.addListener( this );

        // Launch button
        launchBtn = new JButton( "Launch" );
        launchBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                launchSim( (Simulation)simTable.getSelection() );
            }
        } );
        launchBtn.setEnabled( false );

        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        gbc.gridy++;
        gbc.gridy++;
        add( launchBtn, gbc );

        updateSimTable();

        Options.instance().addListener( new Options.ChangeListener() {
            public void optionsChanged( Options.ChangeEvent event ) {
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

        simTable = new SimulationTable( Simulation.getInstalledSims(), Options.instance().isShowInstalledThumbnails() );

        // Add mouse handler
        simTable.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                handleSimulationSelection( e );
            }

            public void mousePressed( MouseEvent e ) {
                handleSimulationSelection( e );
            }

            public void mouseReleased( MouseEvent e ) {
                handleSimulationSelection( e );
            }
        } );

        simTableScrollPane = new JScrollPane( simTable );
        GridBagConstraints gbc = new GridBagConstraints( 0, 1, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        add( simTableScrollPane, gbc );
        revalidate();
    }

    /**
     * Launches the simulation
     * 
     * @param sim
     */
    private void launchSim( Simulation sim ) {
        if( sim != null ) {
            sim.launch();
        }
    }

    /**
     * @param event
     */
    private void handleSimulationSelection( MouseEvent event ) {
        Simulation sim = simTable.getSelection();
        launchBtn.setEnabled( sim != null );
        if( event.isPopupTrigger() && sim != null ) {
            new InstalledSimulationPopupMenu( sim ).show( this, event.getX(), event.getY() );
        }
        changeEventChannel.notifyChangeListeners( InstalledSimsPane.this );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Simulation.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void instancesChanged() {
//        updateSims();
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
