
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common_force1d.application.Module;
import edu.colorado.phet.common_force1d.application.PhetApplication;
import edu.colorado.phet.common_force1d.model.BaseModel;
import edu.colorado.phet.common_force1d.model.clock.AbstractClock;
import edu.colorado.phet.common_force1d.model.clock.ClockTickEvent;
import edu.colorado.phet.common_force1d.model.clock.SwingTimerClock;
import edu.colorado.phet.common_force1d.util.QuickTimer;
import edu.colorado.phet.common_force1d.view.PhetFrame;
import edu.colorado.phet.common_force1d.view.PhetLookAndFeel;
import edu.colorado.phet.common_force1d.view.util.FrameSetup;
import edu.colorado.phet.common_force1d.view.util.SimStrings;
import edu.colorado.phet.forces1d.common.ColorDialog;
import edu.colorado.phet.forces1d.common.plotdevice.DefaultPlaybackPanel;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.model.Force1dObject;
import edu.colorado.phet.forces1d.view.Force1DLookAndFeel;
import edu.colorado.phet.forces1d.view.Force1DPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DApplication extends Module {
    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/Force1DStrings";
//    public static final String LOCALIZATION_BUNDLE_BASENAME = "localization/Force1d-test";
    private PhetLookAndFeel phetLookAndFeel;
    private Force1DModel forceModel;
    protected Force1DPanel forcePanel;
    private Force1dControlPanel fullControlPanel;
    private SimpleControlPanel simpleControlPanel;
    private Force1dObject[] imageElements;
    private static boolean readyToRender = false;
    private DefaultPlaybackPanel playbackPanel;
    private PhetFrame phetFrame;
    private Force1DLookAndFeel force1DLookAndFeel = new Force1DLookAndFeel();
    private int objectIndex;
    private IForceControl currentControlPanel;
    private static final String VERSION = "1.01.15";

    public Force1DApplication( AbstractClock clock, PhetLookAndFeel phetLookAndFeel ) throws IOException {
        this( clock, SimStrings.get( "Force1DModule.moduleName" ), phetLookAndFeel );
    }

    public Force1DApplication( AbstractClock clock, String name, PhetLookAndFeel phetLookAndFeel ) throws IOException {
        super( name, clock );
        System.out.println( "Force1DModule.Force1DModule-a" );
        this.phetLookAndFeel = phetLookAndFeel;
//        this.clock = clock;

        forceModel = new Force1DModel( this );
        setModel( new BaseModel() );
        imageElements = new Force1dObject[]{
            new Force1dObject( "images/cabinet.gif", SimStrings.get( "Force1DModule.fileCabinet" ), 0.8, 200, 0.3, 0.2 ),
            new Force1dObject( "images/fridge.gif", SimStrings.get( "Force1DModule.refrigerator" ), 0.35, 400, 0.7, 0.5 ),
            new Force1dObject( "images/phetbook.gif", SimStrings.get( "Force1DModule.textbook" ), 0.8, 10, 0.3, 0.25 ),
            new Force1dObject( "images/crate.gif", SimStrings.get( "Force1DModule.crate" ), 0.8, 300, 0.2, 0.2 ),
            new Force1dObject( "images/ollie.gif", SimStrings.get( "Force1DModule.sleepyDog" ), 0.5, 25, 0.1, 0.1 ),
        };
        System.out.println( "Force1DModule.Force1DModule" );
        forcePanel = new Force1DPanel( this );
        System.out.println( "Force1DModule.Force1DModule2" );
        forcePanel.addRepaintDebugGraphic( clock );
        setApparatusPanel( forcePanel );

        fullControlPanel = new Force1dControlPanel( this );
        simpleControlPanel = new SimpleControlPanel( this );

        setControlPanel( simpleControlPanel );
        addModelElement( forceModel );

        playbackPanel = new DefaultPlaybackPanel( getForceModel().getPlotDeviceModel() );

        getForceModel().setBoundsWalled();

        CrashAudioPlayer crashAudioPlayer = new CrashAudioPlayer();
        getForceModel().addCollisionListener( crashAudioPlayer );
    }

    public void updateGraphics( ClockTickEvent event ) {
        super.updateGraphics( event );
        forcePanel.updateGraphics();
    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        forcePanel.setHelpEnabled( h );
    }

    public Force1DModel getForceModel() {
        return forceModel;
    }

    public Force1DPanel getForcePanel() {
        return forcePanel;
    }

    public void reset() {
        forceModel.reset();
        forcePanel.reset();
        fullControlPanel.reset();
        simpleControlPanel.reset();
    }

    public void cursorMovedToTime( double modelX, int index ) {
        forcePanel.cursorMovedToTime( modelX, index );
    }

    public void relayoutPlots() {
        if( forcePanel != null ) {
            forcePanel.layoutPlots();//TODO this looks wrong.
            forcePanel.invalidate();
            forcePanel.repaint();
        }
    }

    public PhetLookAndFeel getPhetLookAndFeel() {
        return phetLookAndFeel;
    }

    public static void main( String[] args ) throws IOException {
        //        Locale.setDefault( Locale.ITALIAN );//for testing
        SimStrings.init( args, LOCALIZATION_BUNDLE_BASENAME );
        PhetLookAndFeel.setLookAndFeel();
        PhetLookAndFeel lookAndFeel = new PhetLookAndFeel();
        lookAndFeel.apply();

        AbstractClock clock = new SwingTimerClock( 1, 30 );
        FrameSetup frameSetup = ( new FrameSetup.CenteredWithInsets( 200, 200 ) );

//        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha",
//                                                       frameSetup, m, clock );
//        model.setName( "force1d" );
//        String version = SimStrings.get( "Force1DModule.version" );
        String version = VERSION;
//        String version = "v1r13";
        final PhetApplication phetApplication = new PhetApplication( args, SimStrings.get( "Force1DModule.title" ) + " (" + version+")",
                                                                     SimStrings.get( "Force1DModule.description" ), version, clock, false, frameSetup );

        final Force1DApplication module = new Force1DApplication( clock, lookAndFeel );
        module.getApparatusPanel().getGraphic().setVisible( false );
        Module[] m = new Module[]{module};
        phetApplication.setModules( m );
        phetApplication.getPhetFrame().addWindowStateListener( new WindowStateListener() {
            public void windowStateChanged( WindowEvent e ) {
                int oldState = e.getOldState();
                int newState = e.getNewState();
                if( ( oldState & Frame.MAXIMIZED_BOTH ) == 0 &&
                    ( newState & Frame.MAXIMIZED_BOTH ) != 0 ) {
                    readyToRender = true;
                }
            }
        } );
        module.getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( readyToRender ) {
                    setup( module );
//                    setup( simpleModule );
                    readyToRender = false;
                }
            }
        } );

        JMenu options = new JMenu( SimStrings.get( "Force1DModule.options" ) );
        JMenuItem item = new JMenuItem( SimStrings.get( "Force1DModule.backgroundColor" ) );
        item.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.showColorDialog();
            }
        } );
        options.add( item );

        phetApplication.getPhetFrame().addMenu( options );
        phetApplication.startApplication();

        new FrameSetup.MaxExtent().initialize( phetApplication.getPhetFrame() );
        module.setPhetFrame( phetApplication.getPhetFrame() );
    }

    private void setPhetFrame( PhetFrame phetFrame ) {
        this.phetFrame = phetFrame;
        getForcePanel().setPhetFrame( phetFrame );

    }

    private void showColorDialog() {
        String title = SimStrings.get( "Force1DModule.chartcolor" );
        ColorDialog.showDialog( title, getApparatusPanel(), Color.yellow, new ColorDialog.Listener() {
            public void colorChanged( Color color ) {
                setChartBackground( color );
            }

            public void cancelled( Color orig ) {
                setChartBackground( orig );
            }

            public void ok( Color color ) {
                setChartBackground( color );
            }
        } );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

        app.getPhetFrame().getBasicPhetPanel().setAppControlPanel( playbackPanel );
    }

    private void setChartBackground( Color color ) {
        forcePanel.setChartBackground( color );
    }

    private static void setup( Force1DApplication module ) {
        final Force1DPanel p = module.getForcePanel();
        p.setReferenceSize();
        p.forceLayout( p.getWidth(), p.getHeight() );

        module.getApparatusPanel().getGraphic().setVisible( true );
        p.paintImmediately( 0, 0, p.getWidth(), p.getHeight() );
    }

    public Force1dObject imageElementAt( int i ) {
        return imageElements[i];
    }

    public Force1dObject[] getImageElements() {
        return imageElements;
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public Force1DLookAndFeel getForce1DLookAndFeel() {
        return force1DLookAndFeel;
    }

    public void setObject( Force1dObject force1dObject ) {
        objectIndex = Arrays.asList( imageElements ).indexOf( force1dObject );
        try {
            getForcePanel().getBlockGraphic().setImage( force1dObject.getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        forceModel.getBlock().setMass( force1dObject.getMass() );
        forceModel.getBlock().setStaticFriction( force1dObject.getStaticFriction() );
        forceModel.getBlock().setKineticFriction( force1dObject.getKineticFriction() );
    }

    public void clearData() {
        getForcePanel().clearData();
    }

    public void setFrictionEnabled( boolean useFriction ) {
        getForceModel().setFrictionEnabled( useFriction );
    }

    public void setImageIndex( int imageIndex ) {
        try {
            getForcePanel().getBlockGraphic().setImage( imageElements[imageIndex].getImage() );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public int getImageIndex() {
        return objectIndex;
    }

    public void restoreDefaults() {
        setObject( imageElements[objectIndex] );
        getForceModel().setGravity( 9.8 );
    }

    public void clockTicked( ClockTickEvent event ) {
        QuickTimer totalTime = new QuickTimer();

        QuickTimer userInputTime = new QuickTimer();
        handleControlUserInputs();
        getApparatusPanel().handleUserInput();
        debug( "userInputTime = " + userInputTime );

        QuickTimer modelTime = new QuickTimer();
        getModel().clockTicked( event );
        debug( "modelTime = " + modelTime );

        QuickTimer updateControlPanelTime = new QuickTimer();
        updateControlPanelGraphics();
        debug( "updateControlPanelTime = " + updateControlPanelTime );

        QuickTimer updateGraphicsTime = new QuickTimer();
        updateGraphics( event );
        debug( "updateGraphicsTime = " + updateGraphicsTime );

        QuickTimer paintTime = new QuickTimer();
        getApparatusPanel().paint();
        debug( "paintTime = " + paintTime );

        debug( "totalTime = " + totalTime );
    }

    private void handleControlUserInputs() {
        if( getActiveControlPanel() != null ) {
            getActiveControlPanel().handleUserInput();
        }
    }

    private void updateControlPanelGraphics() {
        if( getActiveControlPanel() != null ) {
            getActiveControlPanel().updateGraphics();
        }
    }

    public static void debug( String str ) {
        boolean debug = false;
        if( debug ) {
            System.out.println( "str = " + str );
        }
    }

    public IForceControl getActiveControlPanel() {
        return currentControlPanel;
    }

    public void setSimpleControlPanel() {
        setControlPanel( simpleControlPanel );
    }

    public void setControlPanel( IForceControl controlPanel ) {
        this.currentControlPanel = controlPanel;
        super.setControlPanel( controlPanel );
        if( phetFrame != null ) {
            phetFrame.getBasicPhetPanel().setControlPanel( controlPanel );
            phetFrame.getBasicPhetPanel().invalidate();
            phetFrame.getBasicPhetPanel().validate();
            phetFrame.getBasicPhetPanel().doLayout();
        }
        Window window = SwingUtilities.getWindowAncestor( controlPanel );
        if( window instanceof JFrame ) {
            JFrame frame = (JFrame)window;
            frame.invalidate();
            frame.doLayout();
        }
        fullControlPanel.getFreeBodyDiagramSuite().controlsChanged();
        simpleControlPanel.getFreeBodyDiagramSuite().controlsChanged();
    }

    public void setAdvancedControlPanel() {
        setControlPanel( fullControlPanel );
    }
}
