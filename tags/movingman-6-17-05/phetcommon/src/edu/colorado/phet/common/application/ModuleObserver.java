/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import java.util.EventListener;
import java.util.EventObject;

/**
 * Observes additions and removals of Modules, change in the active Module.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public interface ModuleObserver extends EventListener {
    public void moduleAdded( ModuleEvent event );

    public void activeModuleChanged(  ModuleEvent event );

    public void moduleRemoved(  ModuleEvent event );
}
