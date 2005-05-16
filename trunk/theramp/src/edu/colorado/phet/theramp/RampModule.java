/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.timeseries.RampTimeSeriesModel;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.colorado.phet.timeseries.TimeSeriesPlaybackPanel;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 9:57:09 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampModule extends Module {
    private RampPanel rampPanel;
    private RampModel rampModel;
    private RampControlPanel rampControlPanel;
    private RampObject[] rampObjects;
    public static final double FORCE_LENGTH_SCALE = 0.15;//1.0;
    private RampTimeSeriesModel rampTimeSeriesModel;
    private TimeSeriesPlaybackPanel rampMediaPanel;

    public RampModule( AbstractClock clock ) {
        super( "The Ramp", clock );
        setModel( new BaseModel() );
        rampModel = new RampModel();
        rampTimeSeriesModel = new RampTimeSeriesModel( this );
        clock.addClockTickListener( rampTimeSeriesModel );
        rampObjects = new RampObject[]{
            new RampObject( "images/cabinet.gif", "File Cabinet", 0.8, 200, 0.3, 0.2, 0.4 ),
            new RampObject( "images/fridge.gif", "Refrigerator", 0.35, 400, 0.7, 0.5, 0.4 ),
            new RampObject( "images/crate.gif", "Crate", 0.8, 300, 0.2, 0.2, 0.3 ),
            new RampObject( "images/ollie.gif", "Sleepy Dog", 0.5, 100, 0.1, 0.1, 0.35 ),
        };
        rampPanel = new RampPanel( this );
        setApparatusPanel( rampPanel );

        rampControlPanel = new RampControlPanel( this );
        setControlPanel( rampControlPanel );
        setObject( rampObjects[0] );

        rampMediaPanel = new TimeSeriesPlaybackPanel( rampTimeSeriesModel );
    }

    public static void main( String[] args ) {
        SwingTimerClock clock = new SwingTimerClock( 1.0, 30 );
        PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();
        phetLookAndFeel.apply();
        PhetLookAndFeel.setLookAndFeel();


        FrameSetup frameSetup = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithSize( 600, 600 ) );
        RampModule module = new RampModule( clock );
        ApplicationModel applicationModel = new ApplicationModel( "The Ramp", "Ramp Application", "0", frameSetup, module, module.getClock() );
//        PhetApplication application = new PhetApplication( args, "title", "desc", "v", clock, true, frameSetup );
        PhetApplication application = new PhetApplication( applicationModel, args );
//        application.setModules( new Module[]{module, new RampModule( clock )} );

        application.getPhetFrame().getBasicPhetPanel().setAppControlPanel( module.rampMediaPanel );
        application.startApplication();
        module.record();

    }

    public RampPanel getRampPanel() {
        return rampPanel;
    }

    public RampModel getRampModel() {
        return rampModel;
    }

    public void reset() {
        rampModel.reset();
    }

    public void setObject( RampObject rampObject ) {
        rampModel.getBlock().setMass( rampObject.getMass() );
        rampModel.getBlock().setStaticFriction( rampObject.getStaticFriction() );
        rampModel.getBlock().setKineticFriction( rampObject.getKineticFriction() );
        getRampPanel().setObject( rampObject );
    }

    public RampObject[] getRampObjects() {
        return rampObjects;
    }

    public void record() {
        rampTimeSeriesModel.setRecordMode();
        rampTimeSeriesModel.setPaused( false );
    }

    public void playback() {
        rampTimeSeriesModel.setPlaybackMode();
        rampTimeSeriesModel.setPaused( false );
    }

    public void repaintBackground() {
        System.out.println( "RampModule.repaintBackground: NOOP" );
    }

    public void setCursorsVisible( boolean b ) {
        System.out.println( "RampModule.setCursorsVisible: NOOP" );
    }

    public void updateModel( double dt ) {
        getRampModel().stepInTime( dt );
    }
}
