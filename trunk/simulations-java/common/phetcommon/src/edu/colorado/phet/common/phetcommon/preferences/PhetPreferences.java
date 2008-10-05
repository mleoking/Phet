package edu.colorado.phet.common.phetcommon.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PhetPreferences {
    private Properties properties = new Properties();

    private static PhetPreferences instance;
    private static final String ALL_SIMS = "all-sims";
    private static final String TRACKING = "tracking";
    private static final String APPLY_ALL = "applyToAll";
    private static final String DOT = ".";
    private static final String UPDATES = "updates";
    private static final String SEPARATOR = System.getProperty( "file.separator" );
    private static final File PREFERENCES_FILE = new File( System.getProperty( "user.home" ) + SEPARATOR + ".phet" + SEPARATOR + "preferences.properties" );

    private PhetPreferences() {
        if ( !PREFERENCES_FILE.exists() ) {
            PREFERENCES_FILE.getParentFile().mkdirs();

            setUpdatesEnabledForAll( true );
            setApplyUpdatesToAll( true );

            setTrackingEnabledForAll( true );
            setApplyTrackingToAll( true );


            storePreferences();
        }
        try {
            properties.load( new FileInputStream( PREFERENCES_FILE ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private void storePreferences() {
        try {
            properties.store( new FileOutputStream( PREFERENCES_FILE ), "Preferences for PhET, see http://phet.colorado.edu" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public static PhetPreferences getInstance() {
        if ( instance == null ) {
            instance = new PhetPreferences();
        }
        return instance;
    }

    public void setApplyTrackingToAll( boolean applyAll ) {
        properties.setProperty( ALL_SIMS + DOT + TRACKING + DOT + APPLY_ALL, Boolean.toString( applyAll ) );
        storePreferences();
    }

    public void setApplyUpdatesToAll( boolean applyAll ) {
        properties.setProperty( ALL_SIMS + DOT + UPDATES + DOT + APPLY_ALL, Boolean.toString( applyAll ) );
        storePreferences();
    }

    public void setTrackingEnabledForAll( boolean b ) {
        setBooleanEnabled( ALL_SIMS, TRACKING, b );
    }

    public void setUpdatesEnabledForAll( boolean b ) {
        setBooleanEnabled( ALL_SIMS, UPDATES, b );
    }

    public boolean isTrackingEnabled( String project, String sim ) {
        return isTrackingEnabledForAll() || getBoolean( project, sim, TRACKING, true );
    }

    public boolean isUpdatesEnabled( String project, String sim ) {
        return isUpdatesEnabledForAll() || getBoolean( project, sim, UPDATES, true );
    }

    public boolean isTrackingEnabledForAll() {
        return getBoolean( ALL_SIMS, TRACKING, true );
    }

    public boolean isUpdatesEnabledForAll() {
        return getBoolean( ALL_SIMS, UPDATES, true );
    }

    public void setTrackingEnabled( String project, String sim, boolean trackingEnabled ) {
        setBoolean( project, sim, TRACKING, trackingEnabled );
    }

    public void setUpdatesEnabled( String project, String sim, boolean updatesEnabled ) {
        setBoolean( project, sim, UPDATES, updatesEnabled );
    }

    private boolean getBoolean( String project, String sim, String type, boolean defaultValue ) {
        return getBoolean( project + DOT + sim, type, defaultValue );
    }

    private boolean getBoolean( String project, String type, boolean defaultValue ) {
        return Boolean.getBoolean( properties.getProperty( project + DOT + type, "" + defaultValue ) );
    }

    private void setBoolean( String project, String sim, String type, boolean value ) {
        setBooleanEnabled( project + DOT + sim, type, value );
    }

    private void setBooleanEnabled( String project, String type, boolean value ) {
        properties.setProperty( project + DOT + type + ".enabled", "" + value );
        storePreferences();
    }

    public String toString() {
        return properties.toString();
    }

    public static void main( String[] args ) throws IOException {
        PhetPreferences phetPreferences = new PhetPreferences();
        System.out.println( "phetPreferences = " + phetPreferences );
    }
}
