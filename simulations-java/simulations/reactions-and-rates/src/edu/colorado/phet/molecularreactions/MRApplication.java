/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.application.PhetApplicationConfig;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.molecularreactions.modules.ComplexModule;
import edu.colorado.phet.molecularreactions.modules.RateExperimentsModule;
import edu.colorado.phet.molecularreactions.modules.SimpleModule;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * edu.colorado.phet.molecularreactions.MRApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
//public class MRApplication extends PhetApplication {
public class MRApplication extends PiccoloPhetApplication {

    public MRApplication( PhetApplicationConfig config ) {
        super( config );

        addModule( new SimpleModule() );
        addModule( new ComplexModule() );
        addModule( new RateExperimentsModule() );
    }


    public static void main( final String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {

                // Standard initializations
                PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
                phetLookAndFeel.setFont( MRConfig.CONTROL_FONT );
                phetLookAndFeel.setTitledBorderFont( MRConfig.CONTROL_FONT );
                phetLookAndFeel.initLookAndFeel();
                
                FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1000, 700 );
                PhetApplicationConfig config = new PhetApplicationConfig( args, frameSetup, MRConfig.RESOURCES );
                MRApplication application = new MRApplication( config );

                // Let 'er rip
                application.startApplication();
            }
        } );

    }
}
