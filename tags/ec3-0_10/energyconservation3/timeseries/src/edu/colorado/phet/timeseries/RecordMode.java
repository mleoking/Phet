/** Sam Reid*/
package edu.colorado.phet.timeseries;

import edu.colorado.phet.common.model.clock.ClockTickEvent;


/**
 * User: Sam Reid
 * Date: Aug 15, 2004
 * Time: 7:42:04 PM
 * Copyright (c) Aug 15, 2004 by Sam Reid
 */
public class RecordMode extends Mode {
    private PhetTimer timer;

    public RecordMode( final TimeSeriesModel timeSeriesModel ) {
        super( timeSeriesModel, "Record" );
        timer = new PhetTimer( "Record Timer" );
    }

    public void initialize() {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        double recTime = timeSeriesModel.getRecordTime();
        timeSeriesModel.setReplayTime( recTime );
    }

    public void reset() {
        timer.reset();
    }

    public PhetTimer getTimer() {
        return timer;
    }

    public void clockTicked( ClockTickEvent event ) {
        TimeSeriesModel timeSeriesModel = getTimeSeriesModel();
        double dt = event.getDt();
        double recorderTime = timer.getTime();
        double maxTime = timeSeriesModel.getMaxAllowedTime();
        if( !timeSeriesModel.isPaused() ) {
            double newTime = recorderTime + dt;// * timer.getTimerScale();
            if( newTime > maxTime ) {
                dt = ( maxTime - recorderTime );// / timer.getTimerScale();
            }
            timer.stepInTime( dt, maxTime );//this could go over the max.
            timeSeriesModel.updateModel( event );
            timeSeriesModel.addSeriesPoint( timeSeriesModel.getModelState(), timeSeriesModel.getRecordTime() );
        }
    }

    public void initAgain( TimeSeriesModel timeSeriesModel ) {
//        timeSeriesModel.setLastPoint();
    }
}
