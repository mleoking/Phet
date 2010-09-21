/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.motors;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.opticaltweezers.control.*;
import edu.colorado.phet.opticaltweezers.control.developer.DeveloperControlPanel;
import edu.colorado.phet.opticaltweezers.defaults.MotorsDefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.module.OTAbstractControlPanel;

/**
 * MotorsControlPanel is the control panel for MotorsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsControlPanel extends OTAbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private MotorsCanvas _canvas;
    
    private SimulationSpeedControlPanel _simulationSpeedControlPanel;
    private EnzymeControlPanel _enzymeControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private ChartsControlPanel _chartsControlPanel;
    private MiscControlPanel _miscControlPanel;
    private DeveloperControlPanel _developerControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public MotorsControlPanel( MotorsModule module, Frame parentFrame ) {
        super( module );

        _canvas = module.getMotorsCanvas();
        
        // Sub-panels
        MotorsModel model = module.getMotorsModel();
        _simulationSpeedControlPanel = new SimulationSpeedControlPanel( TITLE_FONT, CONTROL_FONT, model.getClock() );
        _enzymeControlPanel = new EnzymeControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getEnzymeANode(), _canvas.getEnzymeBNode() );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, 
                model.getBead(), model.getFluid(), model.getLaser(), model.getLaserPositionController(),
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), _canvas.getDNAForceNode(),
                _canvas.getBeadNode(), _canvas.getLaserNode() );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT,
                parentFrame, MotorsDefaults.POSITION_HISTOGRAM_DIALOG_OFFSET,
                model.getClock(), model.getBead(), model.getLaser(),
                _canvas.getPotentialEnergyChartNode(), _canvas.getLaserNode() );
        _miscControlPanel = new MiscControlPanel( TITLE_FONT, CONTROL_FONT, 
                parentFrame, MotorsDefaults.FLUID_CONTROLS_DIALOG_OFFSET, 
                _canvas.getRulerNode(), model.getFluid() );
        _developerControlPanel = new DeveloperControlPanel( TITLE_FONT, CONTROL_FONT, parentFrame,
                (OTClock)module.getClock(), model.getBead(), model.getInvisibleBead(), model.getLaser(), 
                model.getDNAStrandBead(), _canvas.getDNAStrandBeadNode(),
                model.getDNAStrandFree(), _canvas.getDNAStrandFreeNode(),
                _canvas.getTrapForceNode(), _canvas.getFluidDragForceNode(), _canvas.getDNAForceNode(),
                null /* electricFieldNode */, null /* chargeDistributionNode */,
                false /* showVacuumControls */ );
        
        // Turn off some features
        _miscControlPanel.setFluidVacuumPanelVisible( false );
        
        // Layout
        {
            addControlFullWidth( _simulationSpeedControlPanel );
            addSeparator();
            addControlFullWidth( _enzymeControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _chartsControlPanel );
            addSeparator();
            addControlFullWidth( _miscControlPanel );
            addSeparator();
            if ( PiccoloPhetApplication.getInstance().isDeveloperControlsEnabled() ) {
                addControlFullWidth( _developerControlPanel );
                addSeparator();
            }
            addResetAllButton( module );
        }
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void closeAllDialogs() {
        _chartsControlPanel.setPositionHistogramSelected( false );
        _miscControlPanel.setFluidControlsSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Access to subpanels
    //----------------------------------------------------------------------------
    
    public DeveloperControlPanel getDeveloperControlPanel() {
        return _developerControlPanel;
    }
    
    public SimulationSpeedControlPanel getSimulationSpeedControlPanel() {
        return _simulationSpeedControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public ChartsControlPanel getChartsControlPanel() {
        return _chartsControlPanel;
    }
    
    public MiscControlPanel getMiscControlPanel() {
        return _miscControlPanel;
    }
    
    public EnzymeControlPanel getEnzymeControlPanel() {
        return _enzymeControlPanel;
    }
}

