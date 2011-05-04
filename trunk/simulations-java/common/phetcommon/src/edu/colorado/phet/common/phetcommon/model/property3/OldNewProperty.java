// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.property3;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This Property implementation signifies the old and new value on callbacks by wrapping an Observable2 property.  It is implemented by composition of a wrapped observable.
 *
 * @author Sam Reid
 */
class OldNewProperty<T> implements Observable2<T> {
    //Parent value which will be observed for change events
    private final GettableObservable0<T> parent;

    //Keep track of the old value for notifications
    private T oldValue;

    //List of listeners that will receive the value in callbacks
    private final ListenerList<ChangeObserver<T>> listenerList = new ListenerList<ChangeObserver<T>>( new VoidFunction1<ChangeObserver<T>>() {
        public void apply( ChangeObserver<T> observer ) {
            observer.update( new ChangeEvent<T>( parent.get(), oldValue ) );
        }
    } );

    //Create a NewProperty wrapped around the specified parent
    public OldNewProperty( final GettableObservable0<T> parent ) {
        this.parent = parent;
        parent.addObserver( new VoidFunction0() {
            public void apply() {
                listenerList.notifyListeners();
                oldValue = parent.get();
            }
        } );
    }

    //adds a listener that will receive the new value in its callback
    public void addObserver( ChangeObserver<T> observer ) {
        listenerList.add( observer );
    }

    //removes a listener that will receive the new value in its callback
    public void removeObserver( ChangeObserver<T> observer ) {
        listenerList.remove( observer );
    }

    //Test that demonstrates usage of NewProperty
    public static void main( String[] args ) {
        Property<Boolean> visible = new Property<Boolean>( true );
        Property<Boolean> selected = new Property<Boolean>( false );
        final And and = new And( visible, selected );
        selected.set( true );

        and.addObserver( new ChangeObserver<Boolean>() {
            public void update( ChangeEvent<Boolean> e ) {
                System.out.println( "e = " + e );
            }
        } );
        visible.set( true );
        selected.set( true );
        selected.reset();

        visible.addObserver( new ChangeObserver<Boolean>() {
            public void update( ChangeEvent<Boolean> e ) {
                System.out.println( "visible changed: " + e );
            }
        } );
        visible.set( true );
        visible.set( false );
        visible.set( false );
        visible.set( true );
    }
}