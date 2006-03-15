/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 29, 2005
 * Time: 9:34:53 AM
 * Copyright (c) Jul 29, 2005 by Sam Reid
 */

public class ResolutionControl extends AdvancedPanel {
    public static final int DEFAULT_WAVE_SIZE = 60;
    private SchrodingerModule schrodingerModule;
    private final int WAVE_GRAPHIC_SIZE_1024x768 = 360;

    public ResolutionControl( final SchrodingerModule schrodingerModule ) {
        super( "Resolution>>", "Resolution<<" );
        this.schrodingerModule = schrodingerModule;

        JLabel screenSizeLabel = new JLabel( "Grid Resolution" );
        addControl( screenSizeLabel );

        final JSpinner screenSize = new JSpinner( new SpinnerNumberModel( DEFAULT_WAVE_SIZE, 10, 1024, 5 ) );
        getSchrodingerModule().setWaveSize( DEFAULT_WAVE_SIZE );
        screenSize.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer)screenSize.getValue();
                getSchrodingerModule().setWaveSize( value.intValue() );
            }
        } );
//        addControl( screenSize );

//        int[]configFor1280x760 = new int[]{3, 4, 5, 6, 8, 10, 12, 16, 32};
//        int[]values = new int[]{3, 4, 5, 6, 8, 10, 12, 16,32};
//        int[]configFor1024x768 = new int[]{2, 3, 4, 5, 6, 8, 9, 10, 12, 15, 18};

        int[]configFor1024x768 = new int[]{2, 4, 8};
        Integer[]v = new Integer[configFor1024x768.length];
        for( int i = 0; i < v.length; i++ ) {
            v[i] = new Integer( configFor1024x768[i] );
        }
        final JComboBox jComboBox = new JComboBox( v );
        jComboBox.setSelectedItem( new Integer( schrodingerModule.getSchrodingerPanel().getSchrodingerScreenNode().getCellSize() ) );
        addControl( new JLabel( "Pixels per lattice cell." ) );
        addControl( jComboBox );
        jComboBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                Integer value = (Integer)jComboBox.getSelectedItem();
                schrodingerModule.setCellSize( value.intValue() );
                int waveSize = WAVE_GRAPHIC_SIZE_1024x768 / value.intValue();
                getSchrodingerModule().setWaveSize( waveSize );
            }
        } );
        getSchrodingerModule().setWaveSize( WAVE_GRAPHIC_SIZE_1024x768 / schrodingerModule.getSchrodingerPanel().getSchrodingerScreenNode().getCellSize() );

        JLabel numSkip = new JLabel( "Time Step" );
        addControl( numSkip );
        final JSpinner frameSkip = new JSpinner( new SpinnerNumberModel( SchrodingerScreenNode.numIterationsBetwenScreenUpdate, 1, 20, 1 ) );
        frameSkip.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer val = (Integer)frameSkip.getValue();
                SchrodingerScreenNode.numIterationsBetwenScreenUpdate = val.intValue();
            }
        } );
        addControl( frameSkip );
    }

    private SchrodingerModule getSchrodingerModule() {
        return schrodingerModule;
    }
}
