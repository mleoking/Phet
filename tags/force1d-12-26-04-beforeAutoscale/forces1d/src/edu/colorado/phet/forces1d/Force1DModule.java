/** Sam Reid*/
package edu.colorado.phet.forces1d;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.forces1d.common.PhetLookAndFeel;
import edu.colorado.phet.forces1d.model.Force1DModel;
import edu.colorado.phet.forces1d.view.Force1DPanel;
import edu.colorado.phet.forces1d.view.Force1dObject;

import javax.swing.*;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Nov 12, 2004
 * Time: 10:06:43 PM
 * Copyright (c) Nov 12, 2004 by Sam Reid
 */
public class Force1DModule extends Module {
    private Force1DModel forceModel;
    private Force1DPanel forcePanel;
    private Force1dControlPanel forceControlPanel;
    private AbstractClock clock;
    private Force1dObject[] imageElements;

    public Force1DModule( AbstractClock clock ) throws IOException {
        super( "Force1D" );
        this.clock = clock;

        forceModel = new Force1DModel( this );
        imageElements = new Force1dObject[]{
            new Force1dObject( "images/cabinet.gif", "File Cabinet", 0.8, 200, 0.3, 0.2 ),
            new Force1dObject( "images/fridge.gif", "Refrigerator", 0.35, 400, 0.7, 0.5 ),
            new Force1dObject( "images/phetbook.gif", "Textbook", 0.8, 10, 0.3, 0.25 ),
            new Force1dObject( "images/crate.gif", "Crate", 0.8, 300, 0.2, 0.2 ),
            new Force1dObject( "images/ollie.gif", "Sleepy Dog", 0.5, 25, 0.1, 0.1 ),
//            new Force1dObject( "images/ollie-poster.gif", "Sleepy Dog", 0.5 )
        };


        forcePanel = new Force1DPanel( this );
        forcePanel.addRepaintDebugGraphic( clock );
        setApparatusPanel( forcePanel );

        setModel( new BaseModel() );
        forceControlPanel = new Force1dControlPanel( this );

        setControlPanel( forceControlPanel );

        addModelElement( forceModel );

        ModelElement updateGraphics = new ModelElement() {
            public void stepInTime( double dt ) {
                forcePanel.updateGraphics();
            }
        };

        addModelElement( updateGraphics );
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
    }

    public void cursorMovedToTime( double modelX ) {
    }

    public void relayout() {
    }

//    public BufferedPhetGraphic getBackground() {
//        return forcePanel.getBufferedGraphic();
//    }

    public static void main( String[] args ) throws UnsupportedLookAndFeelException, IOException {
        UIManager.setLookAndFeel( new PhetLookAndFeel() );

        AbstractClock clock = new SwingTimerClock( 1, 30 );
        final Force1DModule module = new Force1DModule( clock );

        FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 200, 200 ) );
        ApplicationModel model = new ApplicationModel( "Forces 1D", "Force1d applet", "1.0Alpha",
                                                       frameSetup, module, clock );
        PhetApplication phetApplication = new PhetApplication( model );
        phetApplication.startApplication();
        module.getForcePanel().setSize( module.getForcePanel().getSize().width - 1, module.getForcePanel().getSize().height - 1 );
        module.getForcePanel().relayout();
        module.getForcePanel().revalidate();
//        phetApplication.getApplicationView().getPhetFrame().setExtendedState( JFrame.MAXIMIZED_BOTH );
        new FrameSetup.MaxExtent().initialize( phetApplication.getPhetFrame() );

//        UIManager.getLookAndFeel().
//        UIManager.setLookAndFeel( new PhetLookAndFeelForWindows() );
//        SwingUtilities.updateComponentTreeUI(phetApplication.getPhetFrame() );
    }

    public AbstractClock getClock() {
        return clock;
    }

    public Force1dObject imageElementAt( int i ) {
        return imageElements[i];
    }

    public Force1dObject[] getImageElements() {
        return imageElements;
    }
}
