/**
 * Class: IdealGasApplication
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.view.IdealGasLandF;
import edu.colorado.phet.idealgas.view.WiggleMeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public class IdealGasApplication extends PhetApplication {

//    static class IdealGasApplicationModel extends ApplicationModel {
//        public IdealGasApplicationModel() {
//            super( SimStrings.get( "IdealGasApplication.title" ),
//                   SimStrings.get( "IdealGasApplication.description" ),
//                   IdealGasConfig.VERSION,
//                   IdealGasConfig.FRAME_SETUP );
//
//            // Create the clock
//            SwingTimerClock clock = new SwingTimerClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true );
//            setClock( clock );
//
//            // Create the modules
//            Module idealGasModule = new IdealGasModule( getClock() );
//            Module[] modules = new Module[]{
//                idealGasModule,
//            };
//            setModules( modules );
//            setInitialModule( idealGasModule );
//
//            // Set the initial size
//            setFrameCenteredSize( 920, 700 );
//        }
//    }

    public IdealGasApplication( String[] args ) {
        super( args,
               SimStrings.get( "IdealGasApplication.title" ),
               SimStrings.get( "IdealGasApplication.description" ),
               IdealGasConfig.VERSION,
               new SwingTimerClock( IdealGasConfig.TIME_STEP, IdealGasConfig.WAIT_TIME, true ),
               true,
               IdealGasConfig.FRAME_SETUP );


        final IdealGasModule idealGasModule = new IdealGasModule( getClock() );
        Module[] modules = new Module[]{
            idealGasModule,
        };
        setModules( modules );

        final WiggleMeGraphic wiggleMeGraphic;
        wiggleMeGraphic = new WiggleMeGraphic( idealGasModule.getApparatusPanel(),
                                               new Point2D.Double( IdealGasConfig.X_BASE_OFFSET + 480, IdealGasConfig.Y_BASE_OFFSET + 170 ),
                                               idealGasModule.getModel() );
        wiggleMeGraphic.start();
        idealGasModule.addGraphic( wiggleMeGraphic, 40 );
        idealGasModule.getPump().addObserver( new SimpleObserver() {
            public void update() {
                if( wiggleMeGraphic != null ) {
                    wiggleMeGraphic.kill();
                    idealGasModule.getApparatusPanel().removeGraphic( wiggleMeGraphic );
                    idealGasModule.getPump().removeObserver( this );
                }
            }
        } );

        super.startApplication();
    }


    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-B" ) ) {
                ModuleManager mm = this.getModuleManager();
                for( int j = 0; j < mm.numModules(); j++ ) {
                    ApparatusPanel ap = mm.moduleAt( j ).getApparatusPanel();
                    ap.setBackground( Color.black );
                    ap.paintImmediately( ap.getBounds() );
                }
            }
        }
    }

    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( new IdealGasLandF() );
        }
        catch( UnsupportedLookAndFeelException e ) {
            e.printStackTrace();
        }
        SimStrings.init( args, IdealGasConfig.localizedStringsPath );
        new IdealGasApplication( args );
    }
}
