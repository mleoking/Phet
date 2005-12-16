/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.qm.modules.intensity.IntensityModule;
import edu.colorado.phet.qm.modules.mandel.MandelModule;
import edu.colorado.phet.qm.modules.single.SingleParticleModule;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:48:21 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerApplication extends PhetApplication {
    public static String TITLE = "Quantum Wave Interference";
    public static String DESCRIPTION = "Quantum Wave Interference";
    public static String VERSION = "0.25";

    static {
        PhetLookAndFeel.setLookAndFeel();
    }

    public SchrodingerApplication( String[] args ) {
        super( args, TITLE, DESCRIPTION, VERSION, createClock(), true, createFrameSetup() );

//        SchrodingerModule singleParticleModel = new SingleParticleModule( this );
//        setModules( new Module[]{singleParticleModel} );
//
//        SchrodingerModule intensityModule = new IntensityModule( this );
//        setModules( new Module[]{intensityModule} );

        SchrodingerModule intensityModule = new IntensityModule( this );
        SchrodingerModule singleParticleModel = new SingleParticleModule( this );
        SchrodingerModule mandelModule = new MandelModule( this );
        setModules( new Module[]{intensityModule, singleParticleModel, mandelModule} );
    }

    private static IClock createClock() {
//        return new SwingTimerClock( 30, new TimeConverter.Constant( 1.0 ), 1.0 );
        return new SwingClock( 30, 1 );
    }

    private static FrameSetup createFrameSetup() {
        return new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
    }

    public static void main( String[] args ) {
        new PhetLookAndFeel().apply();
        SchrodingerApplication schrodingerApplication = new SchrodingerApplication( args );
        schrodingerApplication.startApplication();
    }

}
