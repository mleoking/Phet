/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:23:25 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class IntensityControlPanel extends SchrodingerControlPanel {
    public IntensityControlPanel( final IntensityModule intensityModule ) {
        super( intensityModule );
        SlitControlPanel slitControlPanel = new SlitControlPanel( intensityModule );
        addControl( slitControlPanel );

        final JCheckBox smoothCCD = new JCheckBox( "Smooth Screen Display", intensityModule.getIntensityPanel().isSmoothScreen() );
        smoothCCD.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.getIntensityPanel().setSmoothScreen( smoothCCD.isSelected() );
            }
        } );
        addControl( smoothCCD );

        final JCheckBox fading = new JCheckBox( "Fade", intensityModule.getIntensityPanel().getSmoothIntensityDisplay().isFadeEnabled() );
        fading.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.getIntensityPanel().getSmoothIntensityDisplay().setFadeEnabled( fading.isSelected() );
            }
        } );
        addControl( fading );
    }
}
