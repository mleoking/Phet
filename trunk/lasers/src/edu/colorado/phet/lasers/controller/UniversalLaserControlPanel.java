package edu.colorado.phet.lasers.controller;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.view.LampGraphic;
import edu.colorado.phet.lasers.view.PumpBeamViewPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class: MultipleAtomControlPanel
 * Package: edu.colorado.phet.lasers.controller
 * Author: Another Guy
 * Date: Nov 23, 2004
 * <p/>
 * CVS Info:
 * Current revision:   $Revision$
 * On branch:          $Name$
 * Latest change by:   $Author$
 * On date:            $Date$
 */
public class UniversalLaserControlPanel extends LaserControlPanel {
    private boolean threeEnergyLevels;
    private BaseLaserModule laserModule;
    private WaveViewControlPanel waveViewControlPanel;
    private BasicOptionsPanel basicBasicOptionsPanel;
    private HighLevelEmissionControlPanel highLevelEmissionControlPanel;
    private PumpBeamViewPanel pumpBeamViewPanel;

    public UniversalLaserControlPanel( final BaseLaserModule module, AbstractClock clock ) {
        super( module );
        this.laserModule = module;

        // Add the energy levels panel. Note that it must be wrapped in another JPanel, because
        // it has a null layout manager
        addControl( module.getEnergyLevelsMonitorPanel() );

        waveViewControlPanel = new WaveViewControlPanel( module );
        highLevelEmissionControlPanel = new HighLevelEmissionControlPanel( module );

        // Add the two/three level radio buttons
        basicBasicOptionsPanel = new BasicOptionsPanel( module.getThreeEnergyLevels() );
        addControl( basicBasicOptionsPanel );

        JPanel optionsPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0,
                                                         1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        gbc.anchor = GridBagConstraints.NORTHWEST;
        optionsPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.OptionsBorderTitle" ) ) );
        gbc.anchor = GridBagConstraints.CENTER;

        // Add the options for mirror on/off
        JPanel mirrorOptionPanel = createMirrorControlPanel( module );
        gbc.gridx = 0;
        optionsPanel.add( mirrorOptionPanel, gbc );

        // Add controls for the different views of beams and photons
        gbc.gridx = 1;
        gbc.gridy = 0;
        pumpBeamViewPanel = new PumpBeamViewPanel( module );
        optionsPanel.add( pumpBeamViewPanel, gbc );

        // Add the control for showing/hiding phtoton coming off high energy state
        gbc.gridx = 0;
        gbc.gridy = 1;
        optionsPanel.add( highLevelEmissionControlPanel, gbc );

        // Add controls for view of internally produced photons
        gbc.gridx = 1;
        optionsPanel.add( waveViewControlPanel, gbc );
        JPanel container = new JPanel();
        Border border = BorderFactory.createEtchedBorder();
        container.setBorder( border );
        container.add( optionsPanel );

        super.addControl( container );

        // Reset button
        gbc.fill = GridBagConstraints.NONE;
        JButton resetBtn = new JButton( "Reset" );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        JPanel resetBtnPanel = new JPanel();
        resetBtnPanel.add( resetBtn );
        super.addControl( resetBtnPanel );

        this.doLayout();
        this.setPreferredSize( new Dimension( 340, (int)this.getSize().getHeight() ) );

        // Add a listener to the pumping beam graphic that will make the pump beam view control panel
        // visible when the graphic is visible, and vice versa
        module.getPumpLampGraphic().addChangeListener( new LampGraphic.ChangeListener() {
            public void changed( LampGraphic.ChangeEvent event ) {
                pumpBeamViewPanel.setVisible( event.getLampGraphic().isVisible() );
            }
        } );
    }

    private JPanel createMirrorControlPanel( final BaseLaserModule module ) {
        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel panel2 = new JPanel( new GridLayout( 1, 2 ) );
        panel2.add( new MirrorOnOffControlPanel( module ), gbc );
        return panel2;
    }

    /**
     * @param threeEnergyLevels
     */
    public void setThreeEnergyLevels( boolean threeEnergyLevels ) {
        this.threeEnergyLevels = threeEnergyLevels;
        laserModule.setThreeEnergyLevels( threeEnergyLevels );
        waveViewControlPanel.setVisible( threeEnergyLevels );
        highLevelEmissionControlPanel.setVisible( threeEnergyLevels );
    }

    /**
     * @param viewType
     */
    public void setUpperTransitionView( int viewType ) {
        waveViewControlPanel.setUpperTransitionView( viewType );
    }

    //--------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------

    private class BasicOptionsPanel extends JPanel {
        private JRadioButton twoLevelsRB;
        private JRadioButton threeLevelsRB;

        BasicOptionsPanel( boolean threeEnergyLevels ) {
            GridBagConstraints gbc;

            twoLevelsRB = new JRadioButton( new AbstractAction( SimStrings.get( "LaserControlPanel.TwoLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( false );
                }
            } );
            threeLevelsRB = new JRadioButton( new AbstractAction( SimStrings.get( "LaserControlPanel.ThreeLevelsRadioButton" ) ) {
                public void actionPerformed( ActionEvent e ) {
                    setThreeEnergyLevels( true );
                }
            } );
            final ButtonGroup energyButtonGroup = new ButtonGroup();
            energyButtonGroup.add( twoLevelsRB );
            energyButtonGroup.add( threeLevelsRB );

            JPanel energyButtonPanel = new JPanel();
            energyButtonPanel.add( twoLevelsRB );
            energyButtonPanel.add( threeLevelsRB );

            energyButtonPanel.setBorder( new TitledBorder( SimStrings.get( "LaserControlPanel.EnergyLevelsBorderTitle" ) ) );

            this.setLayout( new GridBagLayout() );
            gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                          GridBagConstraints.CENTER,
                                          GridBagConstraints.HORIZONTAL,
                                          new Insets( 0, 0, 0, 0 ),
                                          0, 0 );
            Border border = BorderFactory.createEtchedBorder();
            this.setBorder( border );
            this.add( energyButtonPanel, gbc );

            //Set the number of energy levels we'll see
            twoLevelsRB.setSelected( !threeEnergyLevels );
            threeLevelsRB.setSelected( threeEnergyLevels );
            setThreeEnergyLevels( threeEnergyLevels );
        }
    }

}
