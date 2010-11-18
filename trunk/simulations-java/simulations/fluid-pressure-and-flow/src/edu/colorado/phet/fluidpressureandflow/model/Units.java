package edu.colorado.phet.fluidpressureandflow.model;

import java.text.DecimalFormat;
import java.text.FieldPosition;

import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * @author Sam Reid
 */
public class Units {
    public static Unit ATMOSPHERE = new LinearUnit( "Atmospheres", "atm", 9.8692E-6, new DecimalFormat( "0.0000" ) {
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
    public static Unit PASCAL = new LinearUnit( "Pascal", "Pa", 1, new DecimalFormat( "0" ) );
    public static Unit PSI = new LinearUnit( "Pounds per square inch", "psi", 145.04E-6, new DecimalFormat( "0.00" ) );

    public static Unit METERS = new LinearUnit( "Meters", "m", 1, new DecimalFormat( "0.0" ) );
    public static Unit FEET = new LinearUnit( "Feet", "ft", 3.2808399, new DecimalFormat( "0.0" ) );

    public double feetToMeters( double feet ) {
        return feet * 0.3048;
    }

    public static interface Unit {
        double siToUnit( double value );

        double toSI( double value );

        String getName();

        String getAbbreviation();

        public DecimalFormat getDecimalFormat();
    }

    public static class LinearUnit implements Unit {
        private final Function.LinearFunction linearFunction;
        private final String name;
        private final String abbreviation;
        private final DecimalFormat decimalFormat;

        public LinearUnit( String name, String abbreviation, double siToUnitScale, DecimalFormat decimalFormat ) {
            this.name = name;
            this.abbreviation = abbreviation;
            this.decimalFormat = decimalFormat;
            linearFunction = new Function.LinearFunction( 0, 1, 0, siToUnitScale );
        }

        public String getName() {
            return name;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public DecimalFormat getDecimalFormat() {
            return decimalFormat;
        }

        public double siToUnit( double value ) {
            return linearFunction.evaluate( value );
        }

        public double toSI( double value ) {
            return linearFunction.createInverse().evaluate( value );
        }
    }

}
