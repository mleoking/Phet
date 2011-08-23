// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model.units;

import java.text.DecimalFormat;
import java.text.FieldPosition;

import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.*;

/**
 * The units for the Fluid Pressure and Flow model are SI, and Units converts them to and from different units systems.
 *
 * @author Sam Reid
 */
public class Units {
    public static Unit ATMOSPHERE = new LinearUnit( ATMOSPHERES, ATM, 9.8692E-6, new DecimalFormat( "0.0000" ) {
        @Override
        public StringBuffer format( double number, StringBuffer result, FieldPosition fieldPosition ) {
            final StringBuffer answer = super.format( number, result, fieldPosition );
            if ( answer.toString().equals( "1.0000" ) || number >= 1 ) {
                return new StringBuffer( new DecimalFormat( "0.00" ).format( number ) );//Show 0.9999 atm when lifted into the atmosphere so students don't think pressure doesn't decrease vs altitude
            }
            else {
                return answer;
            }
        }
    } );//http://en.wikipedia.org/wiki/Atmosphere_%28unit%29
    public static Unit PASCAL = new LinearUnit( PASCALS, FluidPressureAndFlowResources.Strings.PA, 1, new DecimalFormat( "0" ) );
    public static Unit PSI = new LinearUnit( POUNDS_PER_SQUARE_INCH, FluidPressureAndFlowResources.Strings.PSI, 145.04E-6, new DecimalFormat( "0.00" ) );

    public static Unit METERS = new LinearUnit( FluidPressureAndFlowResources.Strings.METERS, FluidPressureAndFlowResources.Strings.M, 1, new DecimalFormat( "0.0" ) );
    public static final double FEET_PER_METER = 3.2808399;
    public static Unit FEET = new LinearUnit( FluidPressureAndFlowResources.Strings.FEET, FT, FEET_PER_METER, new DecimalFormat( "0.0" ) );

    public static Unit METERS_PER_SECOND = new LinearUnit( FluidPressureAndFlowResources.Strings.METERS_PER_SECOND, M_PER_S, 1, new DecimalFormat( "0.0" ) );
    public static Unit FEET_PER_SECOND = new LinearUnit( "feet-per-second", FT_PER_S, FEET_PER_METER, new DecimalFormat( "0.0" ) );

    public double feetToMeters( double feet ) {
        return feet * 0.3048;
    }
}