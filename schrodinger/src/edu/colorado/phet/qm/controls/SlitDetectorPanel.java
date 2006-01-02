/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.intensity.IntensityPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 10:04:32 AM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class SlitDetectorPanel extends VerticalLayoutPanel {
    private IntensityModule intensityModule;
    private JCheckBox leftSlit;
    private JCheckBox rightSlit;

    public SlitDetectorPanel( final IntensityModule intensityModule ) {
        this.intensityModule = intensityModule;
        leftSlit = new JCheckBox( "Detector on Left Slit" );
        leftSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.setLeftDetectorEnabled( leftSlit.isSelected() );
            }
        } );
        add( leftSlit );

        rightSlit = new JCheckBox( "Detector on Right Slit" );
        rightSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                intensityModule.setRightDetectorEnabled( rightSlit.isSelected() );
            }
        } );
        add( rightSlit );

        intensityModule.addListener( new IntensityModule.Adapter() {
            public void detectorsChanged() {
                leftSlit.setSelected( intensityModule.isLeftDetectorEnabled() );
                rightSlit.setSelected( intensityModule.isRightDetectorEnabled() );
            }
        } );
        intensityModule.getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void doubleSlitVisibilityChanged() {
                synchronizeModelState();
            }
        } );
    }

    public IntensityPanel getIntensityPanel() {
        return intensityModule.getIntensityPanel();
    }

    public void synchronizeModelState() {
        intensityModule.setRightDetectorEnabled( rightSlit.isSelected() );
        intensityModule.setLeftDetectorEnabled( leftSlit.isSelected() );
    }

    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        leftSlit.setEnabled( enabled );
        rightSlit.setEnabled( enabled );
    }
}
