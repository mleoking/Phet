package edu.colorado.phet.common.phetcommon.tracking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class TrackingMessage {
    private ArrayList entries = new ArrayList();

    //versioning the tracking system will allow us to analyze data across version changes
    //for example, we may stop tracking certain things in a newer version of the tracker
    //having the version will allow us to know that those messages are gone by design
    private static final String TRACKER_VERSION = "0.00.01";

    //versioning the messages allows us to manage data after changing message content 
    public static final String MESSAGE_VERSION = "0.00.01";

    public static final class MessageType {//enum
        private String name;

        public MessageType( String name ) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static final MessageType SIM_LAUNCHED = new MessageType( "sim-launched" );
    public static final MessageType PREFERENCES_DIALOG_SHOWN = new MessageType( "preferences-dialog-opened" );
    public static final MessageType PREFERENCES_DIALOG_HIDDEN = new MessageType( "preferences-dialog-hidden" );
    public static final MessageType UNKNOWN_TYPE = new MessageType( "unknown" );

    public TrackingMessage( PhetApplicationConfig config, MessageType messageType ) {
        initTimeZone();
        TrackingEntry[] entriesArray = new TrackingEntry[]{
                new TrackingEntry( "tracker-version", TRACKER_VERSION ),
                new TrackingEntry( "message-version", MESSAGE_VERSION ),
                new TrackingEntry( "type", messageType.getName() ),

                //Sim info first
                new TrackingEntry( "project", config.getProjectName() ),
                new TrackingEntry( "sim", config.getFlavor() ),
                new TrackingEntry( "sim-version", config.getVersion().toString() ),
                new TrackingEntry( "sim-locale", PhetResources.readLocale().toString() ),
                new TrackingEntry( "dev", config.isDev() + "" ),

                //Then general to specific information about machine config
                new TrackingEntry.SystemProperty( "os.name" ),
                new TrackingEntry.SystemProperty( "os.version" ),
                new TrackingEntry.SystemProperty( "os.arch" ),

                new TrackingEntry.SystemProperty( "javawebstart.version" ),
                new TrackingEntry.SystemProperty( "java.version" ),
                new TrackingEntry.SystemProperty( "java.vendor" ),

                new TrackingEntry.SystemProperty( "user.country" ),
                new TrackingEntry.SystemProperty( "user.timezone" ),
                new TrackingEntry( "locale-default", Locale.getDefault().toString() ),
                new TrackingEntry( PhetPreferences.KEY_PREFERENCES_FILE_CREATION_TIME, PhetPreferences.getInstance().getPreferencesFileCreatedAtMillis() + "" ),
                new TrackingEntry( "sim-started-at", config.getSimStartTimeMillis() + "" ),
                new TrackingEntry( "sim-startup-time", config.getElapsedStartupTime() + "" ),

                new TrackingEntry( "time", new SimpleDateFormat( "yyyy-MM-dd_HH:mm:ss" ).format( new Date() ) )
        };
        entries.addAll( Arrays.asList( entriesArray ) );
    }

    private void initTimeZone() {
        //for some reason, user.timezone only appears if the next line is used (otherwise user.timezone is empty or null)
        new Date().toString();
    }

    public String toPHP() {
        String php = "";
        for ( int i = 0; i < getEntryCount(); i++ ) {
            if ( i > 0 ) {
                php += "&";
            }
            php += getEntry( i ).toPHP();
        }
        return php;
    }

    private TrackingEntry getEntry( int i ) {
        return (TrackingEntry) entries.get( i );
    }

    public int getEntryCount() {
        return entries.size();
    }

    public String toHumanReadable() {
        String text = "";
        for ( int i = 0; i < getEntryCount(); i++ ) {
            if ( i > 0 ) {
                text += "\n";
            }
            text += getEntry( i ).toHumanReadable();
        }
        return text;
    }
}
