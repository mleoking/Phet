/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.module.phasechanges;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.PhaseChangesDefaults;
import edu.colorado.phet.statesofmatter.model.AbstractMultipleParticleModel;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel1;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel2;


public class PhaseChangesModule extends PiccoloModule {
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private AbstractMultipleParticleModel m_model;
    private PhaseChangesCanvas  m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public PhaseChangesModule( Frame parentFrame ) {
        
        super(StatesOfMatterStrings.TITLE_PHASE_CHANGES_MODULE, 
                new ConstantDtClock(PhaseChangesDefaults.CLOCK_FRAME_DELAY, PhaseChangesDefaults.CLOCK_DT));

        // Model
        m_model = new MultipleParticleModel1( getClock() );

        // Canvas
        m_canvas = new PhaseChangesCanvas( m_model );
        setSimulationPanel( m_canvas );
        
        // Control panel
        setControlPanel( new PhaseChangesControlPanel( this, parentFrame ) );
        
        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }
    
    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    public AbstractMultipleParticleModel getMultiParticleModel(){
        return m_model;
    }
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock, which ultimately resets the model too.
        getClock().resetSimulationTime();
        setClockRunningWhenActive( PhaseChangesDefaults.CLOCK_RUNNING );
    }
}
