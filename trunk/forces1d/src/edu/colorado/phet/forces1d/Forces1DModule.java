/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.forces1d.common.PhetLookAndFeel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Forces1DModule extends Module {

    public Forces1DModule() {
        super( "Forces1D" );
        Force1DPanel apparatusPanel = new Force1DPanel( this );
        setApparatusPanel( apparatusPanel );
        setModel( new BaseModel() );
    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel( new PhetLookAndFeel() );
        Forces1DModule module = new Forces1DModule();
        AbstractClock clock = new SwingTimerClock( 1, 30 );
        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha", new FrameSetup.CenteredWithSize( 800, 800 ), module, clock );
        PhetApplication phetApplication = new PhetApplication( model );
        phetApplication.startApplication();
    }
}
