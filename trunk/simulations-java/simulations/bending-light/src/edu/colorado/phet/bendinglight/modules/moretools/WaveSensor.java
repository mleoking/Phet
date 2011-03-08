// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * @author Sam Reid
 */
public class WaveSensor {
    public final Probe probe1 = new Probe( -4.173076923076922E-7, 9.180769230769231E-7 ) {{
//        series.setValue( new ArrayList<Option<ImmutableVector2D>>() {{
//            for ( int i = 0; i < 200; i++ ) {
//                add( new Option.Some<ImmutableVector2D>( new ImmutableVector2D( i / 100.0, Math.sin( i / 100.0 * 10 ) ) ) );
//            }
//        }} );
    }};
    public final Probe probe2 = new Probe( -1.5440384615384618E-6, -1.2936538461538458E-6 ) {{
//        series.setValue( new ArrayList<Option<ImmutableVector2D>>() {{
//            for ( int i = 0; i < 200; i++ ) {
//                add( new Option.Some<ImmutableVector2D>( new ImmutableVector2D( i / 100.0, Math.cos( i / 100.0 * 10 ) ) ) );
//            }
//        }} );
    }};
    public final Property<ImmutableVector2D> bodyPosition = new Property<ImmutableVector2D>( new ImmutableVector2D( 4.882500000000015E-6, -3.1298076923077013E-6 ) );
    public final Clock clock;

    public WaveSensor( final Clock clock, final Function1<ImmutableVector2D, Option<Double>> probe1Value, final Function1<ImmutableVector2D, Option<Double>> probe2Value ) {
        this.clock = clock;
        //This code helps to come up with a good set of defaults for the values.
//        public static String toCode( ImmutableVector2D v ) {
//        return "new ImmutableVector2D(" + v.getX() + "," + v.getY() + ")";
//    }
//        final SimpleObserver simpleObserver = new SimpleObserver() {
//            public void update() {
//                System.out.println( "bodyPosition.setValue( " + toCode( bodyPosition.getValue() ) + ");" );
//                System.out.println( "probe1.position.setValue( " + toCode( probe1.position.getValue() ) + ");" );
//                System.out.println( "probe2.position.setValue( " + toCode( probe2.position.getValue() ) + ");" );
//            }
//        };
//        probe1.position.addObserver( simpleObserver );
//        probe2.position.addObserver( simpleObserver );

        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                updateProbeSample( probe1, probe1Value, clock );
                updateProbeSample( probe2, probe2Value, clock );
            }
        } );
    }

    private void updateProbeSample( Probe probe, Function1<ImmutableVector2D, Option<Double>> probe1Value, Clock clock ) {
        final Option<Double> value = probe1Value.apply( probe.position.getValue() );
        if ( value.isSome() ) {
            probe.addSample( new Option.Some<ImmutableVector2D>( new ImmutableVector2D( clock.getSimulationTime(), value.get() ) ) );
        }
        else {
            probe.addSample( new Option.None<ImmutableVector2D>() );
        }
    }

    public void translateBody( Dimension2D dimension2D ) {
        bodyPosition.setValue( bodyPosition.getValue().plus( dimension2D ) );
    }

    /*
     * Moves the sensor body and probes until the hot spot (center of one probe) is on the specified position.
     */
    public void translateToHotSpot( Point2D position ) {
        translateAll( new ImmutableVector2D( position ).minus( probe1.position.getValue() ) );
    }

    public void translateAll( ImmutableVector2D delta ) {
        probe1.translate( delta );
        probe2.translate( delta );
        bodyPosition.setValue( bodyPosition.getValue().plus( delta ) );
    }

    public static class Probe {
        public final Property<ImmutableVector2D> position;
        public final Property<ArrayList<Option<ImmutableVector2D>>> series = new Property<ArrayList<Option<ImmutableVector2D>>>( new ArrayList<Option<ImmutableVector2D>>() );

        public Probe( double x, double y ) {
            position = new Property<ImmutableVector2D>( new ImmutableVector2D( x, y ) );
        }

        public void translate( Dimension2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }

        public void translate( ImmutableVector2D delta ) {
            position.setValue( position.getValue().plus( delta ) );
        }

        public void addSample( final Option<ImmutableVector2D> sample ) {
            series.setValue( new ArrayList<Option<ImmutableVector2D>>( series.getValue() ) {{
                add( sample );
            }} );
        }
    }
}
