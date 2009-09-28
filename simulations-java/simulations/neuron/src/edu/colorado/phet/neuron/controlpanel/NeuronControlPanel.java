/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Frame;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.neuron.NeuronResources;
import edu.colorado.phet.neuron.model.AbstractLeakChannel;
import edu.colorado.phet.neuron.model.AbstractMembraneChannel;
import edu.colorado.phet.neuron.model.AtomType;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannelTypes;
import edu.colorado.phet.neuron.model.PotassiumIon;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.colorado.phet.neuron.model.SodiumIon;
import edu.colorado.phet.neuron.model.SodiumLeakageChannel;
import edu.colorado.phet.neuron.module.MembraneDiffusionModule;
import edu.colorado.phet.neuron.view.AtomNode;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Control panel for the neuron sim.
 *
 * @author John Blanco
 */
public class NeuronControlPanel extends ControlPanel {

	//----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	private static final Dimension2D OVERALL_SIZE_OF_LEAK_CHANNEL_ICON = new PDimension(38, 50);
	private static final Dimension2D CHANNEL_SIZE_OF_LEAK_CHANNEL_ICON = new PDimension(15, 30);
	
	// The model-view transform below is used to make nodes that typically
	// reside on the canvas be of an appropriate size for inclusion on the
	// control panel.
	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-8, -8, 16, 16));

	//----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	private AxonModel axonModel;
	private LeakChannelSlider sodiumLeakChannelControl;
	private LeakChannelSlider potassiumLeakChannelControl;
	private ConcentrationSlider2 sodiumConcentrationControl;
	private ConcentrationSlider2 potassiumConcentrationControl;
    
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
        
        // Listen to the model for changes that affect this control panel.
        model.addListener(new AxonModel.Adapter(){
    		public void channelAdded(AbstractMembraneChannel channel) {
    			updateChannelControlSliders();
    		}
    		public void concentrationRatioChanged(AtomType atomType) {
    			updateConcentrationControlSliders();
    		}
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = NeuronResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // TODO: Internationalize.
        // Add the control for the number of sodium leakage channels.
        sodiumLeakChannelControl = new LeakChannelSlider("Sodium Leak Channels", axonModel, AtomType.SODIUM); 
        addControlFullWidth(sodiumLeakChannelControl);
        
        // Add the control for the number of potassium leakage channels.
        potassiumLeakChannelControl = new LeakChannelSlider("Potassium Leak Channels", axonModel, 
        		AtomType.POTASSIUM); 
        addControlFullWidth(potassiumLeakChannelControl);
        
        // Add the control for sodium concentration.
        sodiumConcentrationControl = new ConcentrationSlider2("Sodium Concentration", axonModel, AtomType.SODIUM);
        addControlFullWidth(sodiumConcentrationControl);
        
        // Add the control for potassium concentration.
        potassiumConcentrationControl = new ConcentrationSlider2("Potassium Concentration", axonModel, 
        		AtomType.POTASSIUM);
        addControlFullWidth(potassiumConcentrationControl);
        
        addResetAllButton( module );
        
        updateChannelControlSliders();
        updateConcentrationControlSliders();
    }
    
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
    
    private void updateChannelControlSliders(){
    	
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
    
    private void updateConcentrationControlSliders(){
    	
    	if (sodiumConcentrationControl.getValue() != axonModel.getProportionOfAtomsInside(AtomType.SODIUM)){
    		sodiumConcentrationControl.setValue( axonModel.getProportionOfAtomsInside(AtomType.SODIUM));
    	}
    	if (potassiumConcentrationControl.getValue() != axonModel.getProportionOfAtomsInside(AtomType.POTASSIUM)){
    		potassiumConcentrationControl.setValue( axonModel.getProportionOfAtomsInside(AtomType.POTASSIUM));
    	}
    }
    
    //----------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //----------------------------------------------------------------------------
    
    private static class LeakChannelSlider extends LinearValueControl{
    	
        public LeakChannelSlider(String title, final AxonModel axonModel, AtomType atomType) {
            super( 0, 5, title, "0", "");
            setUpDownArrowDelta( 1 );
            setTextFieldVisible(false);
            setTickPattern( "0" );
            setMajorTickSpacing( 1 );
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(true);
            
            // Set up the variables that will differ based on the type.
            AbstractLeakChannel leakChannel;
            final MembraneChannelTypes channelType;
            switch (atomType){
            case SODIUM:
            	leakChannel = new SodiumLeakageChannel();
            	channelType = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
            	break;
            case POTASSIUM:
            	leakChannel = new PotassiumLeakageChannel();
            	channelType = MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
            	break;
            	
            default:
            	System.err.println(getClass().getName() + " - Error: Unknown leak channel type.");
            	assert false;
            	leakChannel = new SodiumLeakageChannel();  // Just in case.
            	channelType = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL; // Just in case.
            }
            leakChannel.setDimensions(OVERALL_SIZE_OF_LEAK_CHANNEL_ICON, CHANNEL_SIZE_OF_LEAK_CHANNEL_ICON);
            leakChannel.setRotationalAngle(-Math.PI / 2);
            
            // Create and set the icon image.
            PNode iconNode = new MembraneChannelNode(leakChannel, MVT);
            JLabel _valueLabel = getValueLabel();
            _valueLabel.setIcon( new ImageIcon(iconNode.toImage(40, 40, new Color(0,0,0,0))) );
            _valueLabel.setVerticalTextPosition( JLabel.CENTER );
            _valueLabel.setHorizontalTextPosition( JLabel.LEFT );

            // Register a listener to handle changes.
            addChangeListener(new ChangeListener() {
    			public void stateChanged(ChangeEvent e) {
    				int value = (int)Math.round(getValue());
    				if ( value != axonModel.getNumMembraneChannels(channelType) ){
    					axonModel.setNumMembraneChannels(channelType, value);
    				}
    			}
    		});
		}
    }

    private static class ConcentrationSlider extends LinearValueControl{
    	
        public ConcentrationSlider(String title, PNode icon) {
            super( 0, 1, title, "0", "");
            setUpDownArrowDelta( 0.01 );
            setTextFieldVisible(false);
            setTickPattern( "0.00" );
            setMajorTickSpacing( 0.25 );
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(false);
            
            // Set the icon and the text alignment in a way that works well
            // for this particular control.
            JLabel _valueLabel = getValueLabel();
            _valueLabel.setIcon( new ImageIcon(icon.toImage(20, 20, new Color(0,0,0,0))) );
            _valueLabel.setVerticalTextPosition( JLabel.CENTER );
            _valueLabel.setHorizontalTextPosition( JLabel.LEFT );
		}
    }
    
    private static class ConcentrationSlider2 extends LinearValueControl{
    	
        public ConcentrationSlider2(String title, final AxonModel axonModel, final AtomType atomType) {
            super( 0, 1, title, "0", "");
            setUpDownArrowDelta( 0.01 );
            setTextFieldVisible(false);
            setTickPattern( "0.00" );
            setMajorTickSpacing( 0.25 );
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(false);

            // Set up the variables that will differ based on the atom type.
            AtomNode atomNode;
            switch (atomType){
            case SODIUM:
            	atomNode = new AtomNode(new SodiumIon(), MVT);
            	break;
            case POTASSIUM:
            	atomNode = new AtomNode(new PotassiumIon(), MVT);
            	break;
            	
            default:
            	System.err.println(getClass().getName() + " - Error: Unknown atom type.");
            	assert false;
            	atomNode = new AtomNode(new SodiumIon(), MVT);  // Just in case.
            }
            
            // Create and add the icon.
            JLabel _valueLabel = getValueLabel();
            _valueLabel.setIcon( new ImageIcon(atomNode.toImage(20, 20, new Color(0,0,0,0))) );
            _valueLabel.setVerticalTextPosition( JLabel.CENTER );
            _valueLabel.setHorizontalTextPosition( JLabel.LEFT );
            
            // Set up the change listener for this control.
            addChangeListener(new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
            		double value = getValue();
            		if ( value != axonModel.getProportionOfAtomsInside(atomType) ){
            			axonModel.setConcentration(atomType, value);
            		}
            	}
            });
		}
    }
}
