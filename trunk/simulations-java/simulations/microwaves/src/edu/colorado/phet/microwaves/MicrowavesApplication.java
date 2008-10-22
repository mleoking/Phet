/**
 * Class: MicrowaveApplication
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwaves;


import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common_microwaves.application.Module;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.common_microwaves.view.ApplicationDescriptor;
import edu.colorado.phet.coreadditions_microwaves.clock.DynamicClockModel;
import edu.colorado.phet.coreadditions_microwaves.clock.SwingTimerClock;
import edu.colorado.phet.coreadditions_microwaves.components.PhetFrame;
import edu.colorado.phet.microwaves.coreadditions.MessageFormatter;

public class MicrowavesApplication {


    //
    // Static fields and methods
    //
    public static double s_speedOfLight = 10;

    public static PhetApplication s_application;

    // Localization
    public static final String localizedStringsPath = "microwaves/localization/microwaves-strings";
    //todo: convert to proper use of PhetApplicationConfig for getting version
    private static final String VERSION = new PhetResources( "microwaves" ).getVersion().formatForTitleBar();

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                runApplication( args );
            }
        } );
    }

    private static void runApplication( String[] args ) {
        new PhetLookAndFeel().initLookAndFeel();
//        MicrowavesResources.getStringInstance().init( args, localizedStringsPath );

        Module oneMoleculesModule = new OneMoleculeModule();
        Module singleLineOfMoleculesModule2 = new SingleLineOfMoleculesModule2();
        Module manyMoleculesModule = new ManyMoleculesModule();
        Module coffeeModule = new CoffeeModule();
        Module[] modules = new Module[]{
                oneMoleculesModule,
                singleLineOfMoleculesModule2,
                manyMoleculesModule,
                coffeeModule
        };
        ApplicationDescriptor appDescriptor = new ApplicationDescriptor(
                MicrowavesResources.getString( "MicrowavesApplication.title" )
                + " ("
                + VERSION
                + ")",
                MessageFormatter.format( MicrowavesResources.getString( "MicrowavesApplication.description" ) ),
                VERSION, new FrameSetup.CenteredWithSize( 1024, 768 ) );
        s_application = new PhetApplication( appDescriptor, modules,
                                             new SwingTimerClock( new DynamicClockModel( 20, 50 ) ) );
        PhetFrame frame = s_application.getApplicationView().getPhetFrame();
//        frame.addMenu( new ViewMenu() );
        frame.addMenu( new MicrowaveModule.ControlMenu() );
        frame.setDefaultLookAndFeelDecorated( true );
//        frame.setIconImage( lookAndFeel.getSmallIconImage() );

//        s_application.startApplication( manyMoleculesModule );
        s_application.startApplication( singleLineOfMoleculesModule2 );
    }

}
