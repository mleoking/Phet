/**
 * Class: NuclearPhysicsApplication
 * Package: edu.colorado.phet.nuclearphysics
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.controller.FissionModule;
import edu.colorado.phet.nuclearphysics.view.AlphaDecayModule;

public class NuclearPhysicsApplication extends PhetApplication {

    public NuclearPhysicsApplication( ApplicationDescriptor descriptor, Module m, AbstractClock clock ) {
        super( descriptor, m, clock );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public NuclearPhysicsApplication( ApplicationDescriptor descriptor, Module[] modules, AbstractClock clock ) {
        super( descriptor, modules, clock );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public static void main( String[] args ) {
        String desc = GraphicsUtil.formatMessage( "An investigation of\nnuclear fision and fusion" );
        ApplicationDescriptor appDesc = new ApplicationDescriptor( "Nuclear Physics",
                                                                   desc,
                                                                   "0.1" );
        // Note: a ThreadedClock here ends up looking balky
        AbstractClock clock = new SwingTimerClock( 10, 50, true );
//        Module testModule = new TestModule( clock );
        Module alphaModule = new AlphaDecayModule( clock );
        Module fissionModule = new FissionModule( clock );
        Module[] modules = new Module[]{alphaModule, fissionModule};//, testModule};
        NuclearPhysicsApplication app = new NuclearPhysicsApplication( appDesc, modules, clock );
//        app.startApplication( fissionModule );
        app.startApplication( alphaModule );
    }
}
