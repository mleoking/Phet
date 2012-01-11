// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelObject;

/**
 * @author Sam Reid
 */
public class SimSharing {
    public static enum Components implements ModelObject {game}

    public static enum Parameters implements IParameterKey {score, perfectScore, time, bestTime, isNewBestTime, timerVisible, level}

    public static enum Actions implements IModelAction {
        ended
    }
}
