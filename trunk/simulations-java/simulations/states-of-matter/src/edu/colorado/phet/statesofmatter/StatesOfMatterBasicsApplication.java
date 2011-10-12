// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.statesofmatter.developer.DeveloperControlsMenuItem;
import edu.colorado.phet.statesofmatter.module.phasechanges.PhaseChangesModule;
import edu.colorado.phet.statesofmatter.module.solidliquidgas.SolidLiquidGasModule;

/**
 * Main application class for the States of Matter - Basics simulation.  This
 * is a somewhat simplified version of the original States of Matter
 * simulation.  This version is targeted to Middle School audiences.
 *
 * @author John Blanco
 */
public class StatesOfMatterBasicsApplication extends PiccoloPhetApplication implements IProguardKeepClass {

    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    // Property that controls whether the thermometer should display the
    // temperature in degrees Kelvin or Celsius.
    public final static BooleanProperty useKelvin = new BooleanProperty( true );

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private SolidLiquidGasModule m_solidLiquidGasModule;
    private PhaseChangesModule m_phaseChangesModule;

    //----------------------------------------------------------------------------
    // Sole Constructor
    //----------------------------------------------------------------------------

    public StatesOfMatterBasicsApplication( PhetApplicationConfig config ) {
        super( config );
        initModules();
        initMenubar();
    }

    //----------------------------------------------------------------------------
    // Initialization
    //----------------------------------------------------------------------------

    /**
     * Initializes the menu bar.
     */
    protected void initMenubar() {
        // Options menu
        OptionsMenu optionsMenu = new OptionsMenu();
        final JRadioButtonMenuItem kelvinRadioButton = new JRadioButtonMenuItem( "Kelvin" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    useKelvin.set( isSelected() );
                }
            } );
        }};
        optionsMenu.add( kelvinRadioButton );

        final JRadioButtonMenuItem celsiusRadioButton = new JRadioButtonMenuItem( "Celsius" ) {{
            setSelected( !useKelvin.get() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    useKelvin.set( !isSelected() );
                }
            } );
        }};
        optionsMenu.add( celsiusRadioButton );
        useKelvin.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean useKelvin ) {
                kelvinRadioButton.setSelected( useKelvin );
                celsiusRadioButton.setSelected( !useKelvin );
            }
        } );

        getPhetFrame().addMenu( optionsMenu );

        // Developer menu
        JMenu developerMenu = getPhetFrame().getDeveloperMenu();
        developerMenu.add( new DeveloperControlsMenuItem( this ) );
    }

    /**
     * Initializes the modules.
     */
    private void initModules() {
        Frame parentFrame = getPhetFrame();

        m_solidLiquidGasModule = new SolidLiquidGasModule( parentFrame );
        addModule( m_solidLiquidGasModule );

        m_phaseChangesModule = new PhaseChangesModule( parentFrame, false );
        addModule( m_phaseChangesModule );
    }

    //----------------------------------------------------------------------------
    // main
    //----------------------------------------------------------------------------

    /**
     * Main entry point.
     *
     * @param args command line arguments
     */
    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new StatesOfMatterBasicsApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, StatesOfMatterConstants.PROJECT_NAME,
                                                                     StatesOfMatterConstants.FLAVOR_STATES_OF_MATTER_BASICS );

        PhetLookAndFeel p = new PhetLookAndFeel();
        p.setBackgroundColor( StatesOfMatterConstants.CONTROL_PANEL_COLOR );
        appConfig.setLookAndFeel( p );

        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
