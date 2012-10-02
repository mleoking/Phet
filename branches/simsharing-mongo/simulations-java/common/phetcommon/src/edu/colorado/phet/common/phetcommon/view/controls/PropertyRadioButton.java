// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.view.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * PropertyRadioButton wires up a JRadioButton to a property of type T in an enum-style Property<T>.
 *
 * @param <T> the type of object to be selected from
 * @author Sam Reid
 * @author Chris Malley
 */
public class PropertyRadioButton<T> extends JRadioButton {

    private final SettableProperty<T> property;
    private final SimpleObserver propertyObserver;

    public PropertyRadioButton( final String text, final SettableProperty<T> property, final T value ) {
        super( text );

        this.property = property;

        // update the model when the check box changes
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {

                doActionPerformed();

                property.set( value );
                propertyObserver.update();//make sure radio buttons don't toggle off, in case they're not in a button group
            }
        } );

        // update the check box when the model changes
        propertyObserver = new SimpleObserver() {
            public void update() {
                setSelected( property.get() == value );
            }
        };
        property.addObserver( propertyObserver );
    }

    protected void doActionPerformed() {
    }

    public void cleanup() {
        property.removeObserver( propertyObserver );
    }
}