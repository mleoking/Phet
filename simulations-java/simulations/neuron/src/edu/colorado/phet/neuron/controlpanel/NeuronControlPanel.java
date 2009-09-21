/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannelTypes;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.colorado.phet.neuron.model.SodiumLeakageChannel;
import edu.colorado.phet.neuron.module.MembraneDiffusionModule;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for the neuron sim.
 *
 * @author John Blanco
 */
public class NeuronControlPanel extends ControlPanel {


	//----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
	private AxonModel axonModel;
	private LeakChannelSlider sodiumLeakChannelControl;
	private LeakChannelSlider potassiumLeakChannelControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     * @param parentFrame parent frame, for creating dialogs
     */
    public NeuronControlPanel( MembraneDiffusionModule module, Frame parentFrame, AxonModel model ) {
        super();
        
        this.axonModel = model;
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // TODO: Internationalize.
        sodiumLeakChannelControl = new LeakChannelSlider("Sodium Leak Channels", 
        		new MembraneChannelNode(new SodiumLeakageChannel(), new ModelViewTransform2D()));
        sodiumLeakChannelControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = (int)Math.round(sodiumLeakChannelControl.getValue());
				if ( value != axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL) ){
					axonModel.setNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL, value);
				}
			}
		});
        addControlFullWidth(sodiumLeakChannelControl);
        
        potassiumLeakChannelControl = new LeakChannelSlider("Potassium Leak Channels", 
        		new MembraneChannelNode(new PotassiumLeakageChannel(), new ModelViewTransform2D()));
        potassiumLeakChannelControl.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = (int)Math.round(potassiumLeakChannelControl.getValue());
				if ( value != axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL) ){
					axonModel.setNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL, value);
				}
			}
		});
        addControlFullWidth(potassiumLeakChannelControl);
        
        // Layout
        {
            addResetAllButton( module );
        }
        
        updateSliders();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private void updateSliders(){
    	
    	if (sodiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL)){
    		
    		sodiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL));
    	}
    	if (potassiumLeakChannelControl.getValue() != 
    		axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL)){
    		
    		potassiumLeakChannelControl.setValue(
    				axonModel.getNumMembraneChannels(MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL));
    	}
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    private static class LeakChannelSlider extends LinearValueControl{
    	
        public LeakChannelSlider(String title, PNode icon) {
            super( 0, 5, title, "0", "");
            setUpDownArrowDelta( 1 );
            setTextFieldEditable( false );
            setTickPattern( "0" );
            setMajorTickSpacing( 1 );
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setValueLabelIcon(new ImageIcon(icon.toImage(50, 50, new Color(0,0,0,0))));
            setSnapToTicks(true);
		}
    }
}
