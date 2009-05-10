package edu.colorado.phet.unfuddletool.data;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

public class DateTime {
    public String rawString;

    public java.util.Date date;

    public DateTime( String raw ) {
        rawString = raw;


        if ( raw.endsWith( "Z" ) ) {
            SimpleDateFormat format = new SimpleDateFormat( "z yyyy-MM-dd-hh:mm:ss" );
            date = format.parse( "GMT " + raw.replace( 'T', '-' ), new ParsePosition( 0 ) );
        }
        else {
            SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd-hh:mm:ss-z" );
            String tmp = raw.replace( 'T', '-' );
            int lastIdx = tmp.lastIndexOf( '-' );
            tmp = tmp.substring(0, lastIdx) + "-GMT" + tmp.substring( lastIdx ); 
            date = format.parse( tmp, new ParsePosition( 0 ) );
        }
    }

    public String toString() {
        return date.toString();
    }

    public java.util.Date getDate() {
        return date;
    }

    public boolean equals( DateTime other ) {
        return getDate().equals( other.getDate() );
    }

    public static void main( String[] args ) {
        String[] tests = new String[]{"2009-05-10T11:43:15Z", "2009-05-10T11:43:15Z", "2009-05-10T04:43:15-07:00"};

        for ( int i = 0; i < tests.length; i++ ) {
            String test = tests[i];

            System.out.println( test + " => " + ( new DateTime( test ) ) );
        }
    }
}
