package edu.colorado.phet.buildtools.test.gui;

import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class TestLocaleList extends JList {

    private DefaultListModel model;

    public TestLocaleList( Locale[] locales ) {
        model = new DefaultListModel();

        setModel( model );

        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );

        for ( Locale locale : sortedLocales( locales ) ) {
            model.addElement( new LocaleListElement( locale ) );
        }

        setSelectedIndex( 0 );

        addListSelectionListener( new ListSelectionListener() {
            public void valueChanged( ListSelectionEvent listSelectionEvent ) {
                notifyChanged();
            }
        } );
    }

    public Locale getSelectedLocale() {
        int index = getSelectedIndex();
        if ( index < 0 ) {
            return null;
        }
        return ( (LocaleListElement) ( model.get( index ) ) ).getLocale();
    }

    private List<Locale> sortedLocales( Locale[] locales ) {
        List<Locale> ret = new LinkedList<Locale>();
        Locale en = null;
        for ( Locale locale : locales ) {
            if ( LocaleUtils.localeToString( locale ).equals( "en" ) ) {
                en = locale;
            }
            else {
                ret.add( locale );
            }
        }
        Collections.sort( ret, new Comparator<Locale>() {
            public int compare( Locale a, Locale b ) {
                return LocaleUtils.localeToString( a ).compareTo( LocaleUtils.localeToString( b ) );
            }
        } );
        if ( en != null ) {
            ret.add( 0, en );
        }
        return ret;
    }

    private static class LocaleListElement {
        private Locale locale;

        private LocaleListElement( Locale locale ) {
            this.locale = locale;
        }

        public String toString() {
            return LocaleUtils.localeToString( locale );
        }

        public Locale getLocale() {
            return locale;
        }

        public boolean equals( Object obj ) {
            if ( obj instanceof LocaleListElement ) {
                LocaleListElement element = (LocaleListElement) obj;
                return element.toString().equals( toString() );
            }
            else {
                return false;
            }
        }

        public int hashCode() {
            return LocaleUtils.localeToString( locale ).hashCode();
        }
    }


    private List<Listener> listeners = new LinkedList<Listener>();

    public static interface Listener {
        public void notifyChanged();
    }

    public void notifyChanged() {
        for ( Listener listener : listeners ) {
            listener.notifyChanged();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }
}