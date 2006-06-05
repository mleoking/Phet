/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.complexcolormaps.ComplexColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.GrayscaleColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.MagnitudeColorMap;
import edu.colorado.phet.qm.view.complexcolormaps.VisualColorMap3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Dec 17, 2005
 * Time: 7:40:43 PM
 * Copyright (c) Dec 17, 2005 by Sam Reid
 */

public class ParticleVisualizationPanel extends VerticalLayoutPanel implements IVisualizationPanel {
    private QWIPanel QWIPanel;
    private VisButton phaseColorRadioButton;
    private ButtonGroup buttonGroup;
    private VisButton grayMag;
    private VisButton realGray;
    private VisButton complexGray;
    private VisButton[] v;

    public ParticleVisualizationPanel( QWIPanel QWIPanel ) {
        this.QWIPanel = QWIPanel;

        setBorder( BorderFactory.createTitledBorder( "Wave Function Display" ) );
        buttonGroup = new ButtonGroup();

        grayMag = createVisualizationButton( "Magnitude", new MagnitudeColorMap(), new WaveValueAccessor.Magnitude(), true, buttonGroup );
        addFullWidth( grayMag );

        realGray = createVisualizationButton( "Real Part", new GrayscaleColorMap.Real(), new WaveValueAccessor.Real(), false, buttonGroup );
        addFullWidth( realGray );

        complexGray = createVisualizationButton( "Imaginary Part        ", new GrayscaleColorMap.Imaginary(), new WaveValueAccessor.Imag(), false, buttonGroup );
        addFullWidth( complexGray );

        phaseColorRadioButton = createVisualizationButton( "Phase Color", new VisualColorMap3(), new WaveValueAccessor.Magnitude(), false, buttonGroup );
        addFullWidth( phaseColorRadioButton );
        v = new VisButton[]{grayMag, realGray, complexGray, phaseColorRadioButton};
    }

    private VisButton createVisualizationButton( String s, final ComplexColorMap colorMap, final WaveValueAccessor waveValueAccessor, boolean b, ButtonGroup buttonGroup ) {
        final VisButton radioButton = new VisButton( s, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                QWIPanel.setVisualizationStyle( colorMap, waveValueAccessor );
            }
        } );
        buttonGroup.add( radioButton );
        radioButton.setSelected( b );
        return radioButton;
    }

    public void setPhaseColorEnabled( boolean b ) {
        phaseColorRadioButton.setEnabled( b );
    }

    public Component getPanel() {
        return this;
    }

    public void applyChanges() {
        for( int i = 0; i < v.length; i++ ) {
            VisButton visButton = v[i];
            if( visButton.isSelected() ) {
                visButton.fireEvent();
            }
        }
    }
}
