/**
 * Class: EnergyLevelsControlPanel
 * Class: ${PACKAGE}
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 9:13:49 AM
 */
package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.lasers.view.BaseLaserModule;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnergyLevelsControlPanel extends JPanel {

    public EnergyLevelsControlPanel( final BaseLaserModule module ) {
        JPanel energyLevelsControlPanel = new JPanel();
        final JCheckBox energyLevelsCB = new JCheckBox( "Display energy levels", false );
        energyLevelsCB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.setEnergyLevelsVisible( energyLevelsCB.isSelected() );
//                new SetEnergyLevelsVisibleCmd( energyLevelsCB.isSelected() ).doIt();
            }
        } );
        energyLevelsControlPanel.add( energyLevelsCB );
        this.add( energyLevelsControlPanel );
    }


    //
    // Interfaces implemented
    //
    
    //
    // Static fields and methods
    //
    
    //
    // Inner classes
    //
}
