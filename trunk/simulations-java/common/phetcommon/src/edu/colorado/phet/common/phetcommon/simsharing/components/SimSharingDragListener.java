// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEventArgs;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Base class for drag listeners that perform sim-sharing data collection.
 * <p/>
 * If a client is not interested in sim-sharing, use the zero-arg constructor;
 * otherwise use one of the other constructors, or setSimSharingEventArgs, to
 * provide information that will be sent with sim-sharing events.
 * <p/>
 * Overrides should take care to called super first, so that events are sent first.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingDragListener extends MouseAdapter {

    private SimSharingEventArgs eventArgs;

    public SimSharingDragListener() {
    }

    public SimSharingDragListener( String object ) {
        this( new SimSharingEventArgs( object ) );
    }

    public SimSharingDragListener( String object, Function0<Parameter[]> parameters ) {
        this( new SimSharingEventArgs( object, parameters ) );
    }

    public SimSharingDragListener( SimSharingEventArgs eventArgs ) {
        this.eventArgs = eventArgs;
    }

    public void setSimSharingEventArgs( SimSharingEventArgs eventArgs ) {
        this.eventArgs = eventArgs;
    }

    @Override public void mousePressed( MouseEvent event ) {
        if ( eventArgs != null ) {
            SimSharingEvents.sendEvent( eventArgs.object, Actions.START_DRAG, addPosition( eventArgs.parameters.apply(), event ) );
        }
        super.mousePressed( event );
    }

    @Override public void mouseReleased( MouseEvent event ) {
        if ( eventArgs != null ) {
            SimSharingEvents.sendEvent( eventArgs.object, Actions.END_DRAG, addPosition( eventArgs.parameters.apply(), event ) );
        }
        super.mouseReleased( event );
    }

    //Adds the mouse position (relative to the source component) to an array of message parameters
    private Parameter[] addPosition( Parameter[] parameters, final MouseEvent event ) {
        return new ArrayList<Parameter>( Arrays.asList( parameters ) ) {{
            add( new Parameter( "x", event.getX() ) );
            add( new Parameter( "y", event.getY() ) );
        }}.toArray( new Parameter[0] );
    }
}
