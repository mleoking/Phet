/* Copyright 2004, University of Colorado */

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
import java.awt.Point;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.control.D2CControlPanel;


/**
 * DiscreteToContinousModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class D2CModule extends FourierModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Rendering layers
    private static final double SOME_LAYER = 1;

    // Locations
    private static final Point SOME_LOCATION = new Point( 400, 300 );
    
    // Colors
    private static final Color APPARATUS_BACKGROUND = Color.WHITE;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private D2CControlPanel _controlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock the simulation clock
     */
    public D2CModule( AbstractClock clock ) {
        
        super( SimStrings.get( "D2CModule.title" ), clock );

        //----------------------------------------------------------------------------
        // Model
        //----------------------------------------------------------------------------

        // Module model
        BaseModel model = new BaseModel();
        this.setModel( model );
        
        //----------------------------------------------------------------------------
        // View
        //----------------------------------------------------------------------------

        // Apparatus Panel
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setBackground( APPARATUS_BACKGROUND );
        setApparatusPanel( apparatusPanel );

        //----------------------------------------------------------------------------
        // Control
        //----------------------------------------------------------------------------

        // Control Panel
        _controlPanel = new D2CControlPanel( this );
        _controlPanel.addVerticalSpace( 20 );
        _controlPanel.addResetButton();
        setControlPanel( _controlPanel );

        reset();
        
        //----------------------------------------------------------------------------
        // Help
        //----------------------------------------------------------------------------
    }
    
    //----------------------------------------------------------------------------
    // FourierModule implementation
    //----------------------------------------------------------------------------
    
    /**
     * Resets everything to the initial state.
     */
    public void reset() {
        _controlPanel.reset();
    }
}
