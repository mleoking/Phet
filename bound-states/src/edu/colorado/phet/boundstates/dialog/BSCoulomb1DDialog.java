/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.control.SliderControl;
import edu.colorado.phet.boundstates.model.BSCoulomb1DPotential;
import edu.colorado.phet.boundstates.module.BSWellSpec;
import edu.colorado.phet.boundstates.util.DoubleRange;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSCoulomb1DDialog is the dialog for configuring a potential composed of 1-D Coulomb wells.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSCoulomb1DDialog extends BSAbstractConfigureDialog implements Observer, ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private SliderControl _offsetSlider;
    private SliderControl _spacingSlider;
    private JSeparator _spacingSeparator;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSCoulomb1DDialog( Frame parent, BSCoulomb1DPotential potential, BSWellSpec wellSpec, boolean offsetControlSupported ) {
        super( parent, SimStrings.get( "BSCoulomb1DDialog.title" ), potential );
        JPanel inputPanel = createInputPanel( wellSpec, offsetControlSupported );
        createUI( inputPanel );
        updateControls();
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    protected JPanel createInputPanel( BSWellSpec wellSpec, boolean offsetControlSupported ) {
        
        String positionUnits = SimStrings.get( "units.position" );
        String energyUnits = SimStrings.get( "units.energy" );

        // Offset
        {
            DoubleRange offsetRange = wellSpec.getOffsetRange();
            double value = offsetRange.getDefault();
            double min = offsetRange.getMin();
            double max = offsetRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = offsetRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String offsetLabel = SimStrings.get( "label.wellOffset" );
            _offsetSlider = new SliderControl( value, min, max, 
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces, 
                    offsetLabel, energyUnits, columns, SLIDER_INSETS );
            _offsetSlider.setTextEditable( true );
            _offsetSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );   
        }

        // Spacing
        {
            DoubleRange spacingRange = wellSpec.getSpacingRange();
            double value = spacingRange.getDefault();
            double min = spacingRange.getMin();
            double max = spacingRange.getMax();
            double tickSpacing = Math.abs( max - min );
            int tickDecimalPlaces = spacingRange.getSignificantDecimalPlaces();
            int labelDecimalPlaces = tickDecimalPlaces;
            int columns = 4;
            String spacingLabel = SimStrings.get( "label.wellSpacing" );
            _spacingSlider = new SliderControl( value, min, max, 
                    tickSpacing, tickDecimalPlaces, labelDecimalPlaces, 
                    spacingLabel, positionUnits, columns, SLIDER_INSETS );
            _spacingSlider.setTextEditable( true );
            _spacingSlider.setNotifyWhileDragging( NOTIFY_WHILE_DRAGGING );
            
            _spacingSeparator = new JSeparator();
        };
        
        // Events
        {
            _offsetSlider.addChangeListener( this );
            _spacingSlider.addChangeListener( this );
        }
        
        // Layout
        JPanel inputPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
            inputPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            int row = 0;
            int col = 0;
            if ( offsetControlSupported ) {
                layout.addComponent( _offsetSlider, row, col );
                row++;
                layout.addFilledComponent( _spacingSeparator, row, col, GridBagConstraints.HORIZONTAL );
                row++;
            }
            layout.addComponent( _spacingSlider, row, col );
            row++;
        }
        
        return inputPanel;
    }

    //----------------------------------------------------------------------------
    // BSAbstractConfigureDialog implementation
    //----------------------------------------------------------------------------

    protected void updateControls() {
        
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential) getPotential();
        
        // Sync values
        _offsetSlider.setValue( potential.getOffset() );
        _spacingSlider.setValue( potential.getSpacing() );
        
        // Visiblility
        _spacingSlider.setVisible( potential.getNumberOfWells() > 1 );
        _spacingSeparator.setVisible( _spacingSlider.isVisible() );
        pack();
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------

    /**
     * Removes change listeners before disposing of the dialog.
     * If we don't do this, then we'll get events that are caused by
     * the sliders losing focus.
     */
    public void dispose() {
        _offsetSlider.removeChangeListener( this );
        _spacingSlider.removeChangeListener( this );
        super.dispose();
    }
    
    //----------------------------------------------------------------------------
    // ChangeListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Dispatches a ChangeEvent to the proper handler method.
     */
    public void stateChanged( ChangeEvent e ) {
        if ( e.getSource() == _offsetSlider ) {
            handleOffsetChange();
        }
        else if ( e.getSource() == _spacingSlider ) {
            handleSpacingChange();
        }
        else {
            throw new IllegalArgumentException( "unsupported event source: " + e.getSource() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        getPotential().setOffset( offset );
    }
    
    private void handleSpacingChange() {
        BSCoulomb1DPotential potential = (BSCoulomb1DPotential) getPotential();
        final double spacing = _spacingSlider.getValue();
        potential.setSpacing( spacing );
    }

}
