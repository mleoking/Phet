package edu.colorado.phet.testing;

import java.util.HashMap;

public class Entry {
    private HashMap map;
    private String[] keys;//for ordering

    Entry( String[] keys, String[] values ) {
        this.keys = keys;
        map = new HashMap();
        for ( int i = 0; i < keys.length; i++ ) {
            String value = i < values.length ? values[i] : "";
            map.put( keys[i], value );
        }
    }

    public String toString() {
        String s = "[";
        for ( int i = 0; i < keys.length; i++ ) {
            String key = keys[i];
            s += key + "=" + map.get( key );
            if ( i < keys.length - 1 ) {
                s += ", ";
            }
        }
        return s + "]";
    }

    public String getValue( String key ) {
        return (String) map.get( key );
    }
}
