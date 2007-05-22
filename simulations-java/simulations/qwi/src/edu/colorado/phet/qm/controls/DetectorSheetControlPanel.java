/*  */
package edu.colorado.phet.qm.controls;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.qm.davissongermer.QWIStrings;
import edu.colorado.phet.qm.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.DetectorSheetPNode;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.SavedScreenGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 27, 2005
 * Time: 12:54:31 PM
 *
 */

public class DetectorSheetControlPanel extends VerticalLayoutPanel {
    private JButton clearButton;
    private DetectorSheetPNode detectorSheetPNode;
    private JButton saveScreenJButton;
    private ModelSlider brightnessModelSlider;
    private JCheckBox fadeCheckbox;
    private HorizontalLayoutPanel displayPanel;

    public DetectorSheetControlPanel( final DetectorSheetPNode detectorSheetPNode ) {
//        setBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ) );
//        setBorder( BorderFactory.createCompoundBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ), BorderFactory.createBevelBorder( BevelBorder.RAISED ) ) );
        this.detectorSheetPNode = detectorSheetPNode;
        clearButton = new JButton( QWIStrings.getString( "screen.clear" ) );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                detectorSheetPNode.reset();
            }
        } );

        saveScreenJButton = new JButton( QWIStrings.getString( "screen.copy" ) );
        saveScreenJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BufferedImage image = detectorSheetPNode.copyScreen();
                SavedScreenGraphic savedScreenGraphic = new SavedScreenGraphic( getSchrodingerPanel(), image );
                savedScreenGraphic.setOffset( 130, 130 );
                getSchrodingerPanel().getSchrodingerScreenNode().addSavedScreenGraphic( savedScreenGraphic );
            }
        } );

        brightnessModelSlider = new ModelSlider( QWIStrings.getString( "screen.brightness" ), "", 0, 1.0, getBrightess(), new DecimalFormat( "0.0" ) );
        detectorSheetPNode.addListener( new DetectorSheetPNode.Listener() {
            public void brightnessChanged() {
                brightnessModelSlider.setValue( getBrightess() );
            }
        } );
        brightnessModelSlider.setTextFieldVisible( false );
//        brightnessModelSlider.setModelTicks( new double[]{0, 0.25, 0.5, 0.75, 1.0} );
        brightnessModelSlider.setModelTicks( new double[]{0, 0.5, 1.0} );
        this.brightnessModelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setBrightness();
            }
        } );
        setBrightness();

        fadeCheckbox = new JCheckBox( QWIStrings.getString( "fade" ), getSchrodingerPanel().isFadeEnabled() );
        fadeCheckbox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                getSchrodingerPanel().setFadeEnabled( fadeCheckbox.isSelected() );
            }
        } );
        getSchrodingerPanel().addListener( new QWIPanel.Adapter() {
            public void fadeStateChanged() {
                fadeCheckbox.setSelected( getSchrodingerPanel().isFadeEnabled() );
            }
        } );

        displayPanel = new HorizontalLayoutPanel();
        displayPanel.setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "screen.display" ) ) );

        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton showHits = new JRadioButton( QWIStrings.getString( "screen.hits" ), !IntensityBeamPanel.CONTINUOUS_MODE_DEFAULT );
        JRadioButton showAverage = new JRadioButton( QWIStrings.getString( "screen.intensity" ), IntensityBeamPanel.CONTINUOUS_MODE_DEFAULT );

        buttonGroup.add( showAverage );
        buttonGroup.add( showHits );

        showHits.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setContinuousDisplay( false );
            }
        } );
        showAverage.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setContinuousDisplay( true );
            }
        } );

        displayPanel.add( showAverage );
        displayPanel.add( showHits );

        HorizontalLayoutPanel screenPanel = new HorizontalLayoutPanel();
        screenPanel.setBorder( BorderFactory.createTitledBorder( QWIStrings.getString( "screen.title" ) ) );
        screenPanel.add( fadeCheckbox );
        screenPanel.add( clearButton );
        screenPanel.add( saveScreenJButton );
        add( screenPanel );
        add( brightnessModelSlider );
        add( displayPanel );
//        saveClear.setForeground( Color.blue);
    }

    private double getBrightess() {
        return detectorSheetPNode.getBrightness();
    }

    public void setBrightness() {
        detectorSheetPNode.setBrightness( brightnessModelSlider.getValue() );
        if( getIntensityPanel() != null && getIntensityPanel().getContinuousDisplay() != null ) {
            getIntensityPanel().getContinuousDisplay().setBrightness( brightnessModelSlider.getValue() );
        }
    }

    private void setContinuousDisplay( boolean b ) {
        if( getIntensityPanel() != null ) {
            getIntensityPanel().setContinuousMode( b );
        }
    }

    private IntensityBeamPanel getIntensityPanel() {
        if( detectorSheetPNode.getSchrodingerPanel() instanceof IntensityBeamPanel ) {
            return (IntensityBeamPanel)detectorSheetPNode.getSchrodingerPanel();
        }
        return null;
    }

    private QWIPanel getSchrodingerPanel() {
        return detectorSheetPNode.getSchrodingerPanel();
    }

    public void setClearButtonVisible( boolean b ) {
        clearButton.setEnabled( b );
    }

    public void setSaveButtonVisible( boolean b ) {
        saveScreenJButton.setVisible( b );
        supervalidate();
    }

    public void setBrightnessSliderVisible( boolean b ) {
        brightnessModelSlider.setVisible( b );
    }

    public void setFadeCheckBoxVisible( boolean b ) {
        fadeCheckbox.setVisible( b );
    }

    private void supervalidate() {
        validate();
    }

    public void setTypeControlVisible( boolean b ) {
        displayPanel.setVisible( b );
        supervalidate();
    }

}
