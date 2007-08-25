/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common_sound.model.clock;

import java.util.EventObject;


/**
 * ClockStateEvent
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ClockStateEvent extends EventObject {

    public ClockStateEvent( Object source ) {
        super( source );
    }

    public double getDt() {
        return ( (AbstractClock)getSource() ).getDt();
    }

    public int getDelay() {
        return (int)( (AbstractClock)getSource() ).getDelay();
    }

    public boolean getIsPaused() {
        return ( (AbstractClock)getSource() ).isPaused();
    }

    public boolean getIsRunning() {
        return ( (AbstractClock)getSource() ).isRunning();
    }

}
