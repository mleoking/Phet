package edu.colorado.phet.common.phetcommon.tracking;

/**
 * Tracking message sent when the state of something changes.
 * This message is analogous to Swing's StateChanged interface.
 * <p>
 * The client is responsible for choosing a name that uniquely identifies the action.
 * We try to send both the old and new state values, but the old value is optional.
 * All values are represented as Strings. In the case of a complex value (eg, Color), 
 * we will define a String representation of the datatype.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StateChangedMessage extends TrackingMessage {
    
    /* values for the constructor "name" arg */
    
    // Preference dialog is opened or closed
    public static final String PREFERENCES_DIALOG_VISIBLE = "preferences-dialog-visible";
    // updates enabled or disabled in the Updates tab of the Preferences dialog
    public static final String UPDATES_ENABLED = "updates-enabled";
    // tracking enabled or disabled in the Tracking tab of the Preferences dialog
    public static final String TRACKING_ENABLED = "tracking-enabled";
    
    public StateChangedMessage( SessionID sessionID, String name, String oldValue, String newValue ) {
        super( sessionID, "state-changed" );
        addField( new TrackingMessageField( "name", name ) );
        addField( new TrackingMessageField( "old-value", oldValue ) );
        addField( new TrackingMessageField( "new-value", newValue ) );
    }
}
