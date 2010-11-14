package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;

/**
 * @author Sam Reid
 */
public class SimStateMessage<T> implements Serializable {
    private long time;
    private final T simState;

    public SimStateMessage( long time, T simState ) {
        this.time = time;
        this.simState = simState;
    }
}
