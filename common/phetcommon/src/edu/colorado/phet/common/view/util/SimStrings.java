/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * SimStrings
 * <p/>
 * Manages strings for simulations so that they can be localized. All methods are static.
 * <p/>
 * Call setStrings() for each resource bundle of strings that a simulation needs to access.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimStrings {

    private Vector localizedStrings;
    private Vector stringsPaths;
    private Locale localizedLocale;

    private static SimStrings INSTANCE = new SimStrings();


    static {
        INSTANCE.setStrings( "localization/CommonStrings" );
    }

    public static SimStrings getInstance() {
        return INSTANCE;
    }

    /**
     * Initialize application localization.
     *
     * @param args       the commandline arguments that were passed to main
     * @param bundleName the base name of the resource bundle containing localized strings
     */
    public void init( String[] args, String bundleName ) {
        // Get the default locale from property javaws.locale.
        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            getInstance().setLocale( new Locale( applicationLocale ) );
        }

        // Override default locale using "user.language=" command line argument.
        String argsKey = "user.language=";
        for( int i = 0; i < args.length; i++ ) {
            if( args[i].startsWith( argsKey ) ) {
                String locale = args[i].substring( argsKey.length(), args[i].length() );
                getInstance().setLocale( new Locale( locale ) );
                break;
            }
        }

        // Initialize simulation strings using resource bundle for the locale.
        SimStrings.setStrings( bundleName );
    }

    // TODO: make this private after all simulation use init
    public void setLocale( Locale locale ) {
        INSTANCE.localizedLocale = locale;
        // Reload all existing string resources with the new locale
        Vector priorPaths = INSTANCE.stringsPaths;
        INSTANCE.stringsPaths = null;
        INSTANCE.localizedStrings = null;
        if( priorPaths != null ) {
            for( Iterator i = priorPaths.iterator(); i.hasNext(); ) {
                String path = (String)i.next();
                setStrings( path );
            }
        }
    }

    // TODO: make this private after all simulation use init
    public static void setStrings( String stringsPath ) {
        if( INSTANCE.localizedStrings == null ) {
            INSTANCE.localizedStrings = new Vector();
            INSTANCE.stringsPaths = new Vector();
        }
        if( INSTANCE.stringsPaths.contains( stringsPath ) ) {
            return;
        }
        try {
            if( INSTANCE.localizedLocale == null ) {
                INSTANCE.localizedLocale = Locale.getDefault();
            }
            ResourceBundle rb = ResourceBundle.getBundle( stringsPath, INSTANCE.localizedLocale );
            if( rb != null ) {
                INSTANCE.localizedStrings.add( rb );
                INSTANCE.stringsPaths.add( stringsPath );
            }
        }
        catch( Exception x ) {
            System.out.println( "SimStrings.setStrings: " + x );
        }
    }

    public static Locale getLocalizedLocale() {
        return INSTANCE.localizedLocale;
    }

    /**
     * Gets a string value from the localization resource file.
     * If key's value is null, then key is returned.
     * @param key
     * @return String
     */
    public static String get( String key ) {
        if( INSTANCE.localizedStrings == null ) {

            throw new RuntimeException( "Strings not initialized" );
        }

        String value = null;

        for( Iterator i = INSTANCE.localizedStrings.iterator(); value == null && i.hasNext(); ) {
            try {
                ResourceBundle rb = (ResourceBundle)i.next();

                value = rb.getString( key );
            }
            catch( Exception x ) {
                value = null;
            }
        }

        if( value == null ) {
            System.err.println( "SimStrings.get: key not found, key = \"" + key + "\"" );
            value = key;
        }

        return value;
    }
    
    /**
     * Gets an integer value from the localization resource file.
     * If key's value is not an integer, the specified default value is returned.
     * @param key
     * @param defaultValue
     * @return int
     */
    public static int getInt( String key, int defaultValue ) {
        String s = get( key );
        int value = 0;
        try {
            value = Integer.parseInt( s );
        }
        catch( NumberFormatException nfe ) {
            System.err.println( "SimStrings: " + key + " is not an int: " + s );
            value = defaultValue;
        }
        return value;
    }
    
    /**
     * Gets a double value from the localization resource file.
     * If key's value is not a double, the specified default value is returned.
     * @param key
     * @param defaultValue
     * @return double
     */
    public static double getDouble( String key, double defaultValue ) {
        String s = get( key );
        double value = 0;
        try {
            value = Double.parseDouble( s );
        }
        catch( NumberFormatException nfe ) {
            System.err.println( "SimStrings: " + key + " is not a double: " + s );
            value = defaultValue;
        }
        return value;
    }
    
    /**
     * Gets a char value from the localization file.
     * 
     * @param key
     * @return char
     */
    public static char getChar( String key ) {
        return get( key ).charAt( 0 );
    }
}
