package edu.colorado.phet.movingman.motion.force1d;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.movingman.MovingManApplication;
import edu.colorado.phet.movingman.motion.MotionProjectLookAndFeel;

/**
 * Created by: Sam
 * Dec 4, 2007 at 1:23:32 PM
 */
public class Force1DMotionApplication extends PhetApplication {
    public Force1DMotionApplication( String[] args ) {
        super( new Forece1DMotionConfig( args, new FrameSetup.TopCenter( 1024, 768 ), PhetResources.forProject( "moving-man" ), "mm-force1d" ) );
        addModule( new Force1DMotionModule() );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                MotionProjectLookAndFeel.init();
                SimStrings.getInstance().addStrings( MovingManApplication.localizedStringsPath );
                new Force1DMotionApplication( args ).startApplication();
            }
        } );

    }

    private static class Forece1DMotionConfig extends PhetApplicationConfig {
        public Forece1DMotionConfig( String[] commandLineArgs, FrameSetup frameSetup, PhetResources resourceLoader, String flavor ) {
            super( commandLineArgs, frameSetup, resourceLoader, flavor );
        }
    }
}
