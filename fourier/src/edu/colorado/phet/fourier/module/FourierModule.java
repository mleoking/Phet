/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.Color;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.fourier.view.DebuggerGraphic;


/**
 * FaradayModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class FourierModule extends Module {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    protected static final double DEBUG_LAYER = Double.MAX_VALUE - 1;
    protected static final double HELP_LAYER = Double.MAX_VALUE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DebuggerGraphic _debuggerGraphic;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public FourierModule( String title, AbstractClock clock ) {
        super( title, clock );
    }

    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    /**
     * Causes the graphic's location and bounds to be rendered.
     * 
     * @param graphic the graphic
     */
    protected void drawBounds( PhetGraphic graphic ) {
        ApparatusPanel apparatusPanel = getApparatusPanel();
        if ( _debuggerGraphic == null ) {
            _debuggerGraphic = new DebuggerGraphic( apparatusPanel );
            _debuggerGraphic.setLocationColor( Color.GREEN );
            _debuggerGraphic.setBoundsColor( Color.YELLOW );
        }
        _debuggerGraphic.add( graphic );
        apparatusPanel.addGraphic( _debuggerGraphic, DEBUG_LAYER );
    }
}
