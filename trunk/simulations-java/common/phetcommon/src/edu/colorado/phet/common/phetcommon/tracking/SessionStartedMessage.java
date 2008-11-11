package edu.colorado.phet.common.phetcommon.tracking;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;

/**
 * Tracking message sent when the simulation starts, indicating the start of the session.
 * This message sends lots of general information about the simulation and the user's 
 * runtime environment.
 *
 * @author Sam Reid
 * @author Chris Malley
 */
public class SessionStartedMessage extends TrackingMessage {
    public SessionStartedMessage( PhetApplicationConfig config ) {
        super( new SessionID( config ), "session-started" );
        initTimeZone();
        TrackingMessageField[] entriesArray = new TrackingMessageField[]{
                new TrackingMessageField( "tracker-version", TRACKER_VERSION ),
                new TrackingMessageField( "message-version", MESSAGE_VERSION ),

                //Sim info first
                new TrackingMessageField( "project", config.getProjectName() ),
                new TrackingMessageField( "sim", config.getFlavor() ),
                new TrackingMessageField( "sim-type", "java" ), // to easily distinguish between Java and Flash sims
                new TrackingMessageField( "sim-version", config.getVersion().formatMajorMinorDev() ),
                new TrackingMessageField( "svn-revision", config.getVersion().getRevision() ),
                new TrackingMessageField( "locale-language", PhetResources.readLocale().getLanguage() ),
                new TrackingMessageField( "locale-country", PhetResources.readLocale().getCountry() ),
                new TrackingMessageField( "dev", config.isDev() + "" ),
                new TrackingMessageField( "phet-installation", Boolean.toString( PhetUtilities.isPhetInstallation() ) ),
                new TrackingMessageField( "session-count", toString( config.getSessionCount() ) ),

                //Then general to specific information about machine config
                new TrackingMessageField.SystemProperty( "os.name" ),
                new TrackingMessageField.SystemProperty( "os.version" ),
                new TrackingMessageField.SystemProperty( "os.arch" ),

                new TrackingMessageField.SystemProperty( "javawebstart.version" ),
                new TrackingMessageField.SystemProperty( "java.version" ),
                new TrackingMessageField.SystemProperty( "java.vendor" ),

                new TrackingMessageField.SystemProperty( "user.country" ),
                new TrackingMessageField.SystemProperty( "user.timezone" ),
                new TrackingMessageField( "locale-default", Locale.getDefault().toString() ),
                new TrackingMessageField( PhetPreferences.KEY_PREFERENCES_FILE_CREATION_TIME, PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis() + "" ),
                new TrackingMessageField( "sim-started-at", config.getSimStartTimeMillis() + "" ),
                new TrackingMessageField( "sim-startup-time", config.getElapsedStartupTime() + "" ),

                new TrackingMessageField( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
        };
        super.addFields( entriesArray );
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }
    
    private String toString( Integer i ) {
        return ( i == null ) ? "null" : i.toString();
    }
}
