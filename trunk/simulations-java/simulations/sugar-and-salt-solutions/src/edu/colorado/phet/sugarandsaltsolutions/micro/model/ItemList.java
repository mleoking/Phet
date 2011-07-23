package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Observable list class that can be observed for items added or removed.
 *
 * @author Sam Reid
 */
public class ItemList<T> implements Iterable<T> {
    private final ArrayList<T> items = new ArrayList<T>();
    private final ListenerList<T> itemAddedListeners = new ListenerList<T>();
    private final ListenerList<T> itemRemovedListeners = new ListenerList<T>();

    public void addItemAddedListener( VoidFunction1<T> listener ) {
        itemAddedListeners.addListener( listener );
    }

    public void removeItemAddedListener( VoidFunction1<T> listener ) {
        itemAddedListeners.removeListener( listener );
    }

    public void addItemRemovedListener( VoidFunction1<T> listener ) {
        itemRemovedListeners.addListener( listener );
    }

    public void removeItemRemovedListener( VoidFunction1<T> listener ) {
        itemRemovedListeners.removeListener( listener );
    }

    public void add( T item ) {
        items.add( item );
        itemAddedListeners.notifyListeners( item );
    }

    //TODO: Make this work for subtypes of T
    public void remove( Object item ) {
        items.remove( item );
        itemRemovedListeners.notifyListeners( (T) item );
    }

    public ArrayList<T> getItems() {
        return items;
    }

    public Iterator<T> iterator() {
        return items.iterator();
    }

    public boolean contains( T item ) {
        return items.contains( item );
    }

    //Count the items in the list that match the predicate
    public int count( Function1<T, Boolean> predicate ) {
        int count = 0;
        for ( T item : items ) {
            if ( predicate.apply( item ) ) {
                count++;
            }
        }
        return count;
    }

    //Count the items in the list that are an instance of the specified class
    public int count( final Class<? extends T>... clazz ) {
        return getMatchingItems( clazz ).size();
    }

    //Remove all instances that match the specified classes
    public void clear( final Class<? extends T>... clazz ) {
        for ( T item : getMatchingItems( clazz ) ) {
            remove( item );
        }
    }

    //Determine which items are instances of the specified classes
    private ArrayList<T> getMatchingItems( final Class<? extends T>... clazz ) {
        return new ArrayList<T>() {{
            for ( T item : items ) {
                for ( Class<? extends T> aClass : clazz ) {
                    if ( aClass.isInstance( item ) ) {
                        add( item );
                        break;
                    }
                }
            }
        }};
    }

    //Remove all items from the list
    public void clear() {
        while ( size() > 0 ) {
            remove( getItems().get( 0 ) );
        }
    }

    //Get the number of elements in the list
    private int size() {
        return getItems().size();
    }
}