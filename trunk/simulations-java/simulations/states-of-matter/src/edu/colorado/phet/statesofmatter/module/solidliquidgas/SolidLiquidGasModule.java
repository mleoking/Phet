// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.statesofmatter.module.solidliquidgas;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.statesofmatter.StatesOfMatterStrings;
import edu.colorado.phet.statesofmatter.defaults.SolidLiquidGasDefaults;
import edu.colorado.phet.statesofmatter.model.MultipleParticleModel;
import edu.colorado.phet.statesofmatter.view.TemperatureUnits;

/**
 * This class is where the model and view classes for the "Solid, Liquid, and
 * Gas" tab of this simulation are created and contained.
 *
 * @author John Blanco
 */
public class SolidLiquidGasModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private MultipleParticleModel m_model;
    private SolidLiquidGasCanvas m_canvas;

    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------

    public SolidLiquidGasModule( Frame parentFrame, Property<TemperatureUnits> temperatureUnits ) {

        super( StatesOfMatterStrings.TITLE_SOLID_LIQUID_GAS_MODULE,
               new ConstantDtClock( SolidLiquidGasDefaults.CLOCK_FRAME_DELAY, SolidLiquidGasDefaults.CLOCK_DT ) );

        // Model
        m_model = new MultipleParticleModel( (ConstantDtClock) getClock() );

        // Canvas
        m_canvas = new SolidLiquidGasCanvas( m_model, temperatureUnits );
        setSimulationPanel( m_canvas );

        // Control panel
        setControlPanel( new SolidLiquidGasControlPanel( this, parentFrame ) );

        // Turn off the clock control panel - a floating node is used for clock control.
        setClockControlPanel( null );

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
    public MultipleParticleModel getMultiParticleModel() {
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
        setClockRunningWhenActive( SolidLiquidGasDefaults.CLOCK_RUNNING );
    }
}
