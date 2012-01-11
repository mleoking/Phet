// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ISystemObject;

/**
 * @author Sam Reid
 */
public class JMEPhetSimsharing {
    public enum Objects implements ISystemObject {
        jmePhetApplication
    }

    public enum Actions implements ISystemAction {
        erred,
    }
}
