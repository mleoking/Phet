/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.qm.model.DiscreteModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 5:56:06 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class CylinderWaveCheckBox extends JCheckBox {
    private CylinderWave cylinderWave;

    public CylinderWaveCheckBox( SchrodingerModule module, DiscreteModel discreteModel ) {
        super( "Cylinder wave" );
        cylinderWave = new CylinderWave( module, discreteModel, module.getSchrodingerPanel().getGunGraphic() );

        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( isSelected() ) {
                    cylinderWave.setOn();
                }
                else {
                    cylinderWave.setOff();
                }
            }
        } );
    }

}
