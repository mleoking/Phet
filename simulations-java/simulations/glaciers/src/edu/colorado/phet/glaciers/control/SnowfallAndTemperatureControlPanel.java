/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * SnowfallAndTemperatureControlPanel is the control panel for controlling climate
 * using a simple "snowfall & temperature" model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SnowfallAndTemperatureControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _snowfallControl;
    private LinearValueControl _snowfallReferenceElevationControl;
    private LinearValueControl _temperatureControl;
    
    private ArrayList _listeners; // list of SnowfallAndTemperatureControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public SnowfallAndTemperatureControlPanel( DoubleRange snowfallRange, DoubleRange snowfallReferenceElevationRange, DoubleRange temperatureRange ) {
        super();
        
        _listeners = new ArrayList();
        
        // snowfall
        JLabel snowfallLabel = new JLabel( GlaciersStrings.SLIDER_SNOWFALL );        
        {
            double min = snowfallRange.getMin();
            double max = snowfallRange.getMax();
            String label = "";
            String textfieldPattern = "#0.0";
            String units = GlaciersStrings.UNITS_ACCUMULATION;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _snowfallControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _snowfallControl.setUpDownArrowDelta( 0.1 );
            _snowfallControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallControl.isAdjusting() ) {
                        notifySnowfallChanged();
                    }
                }
            } );
            
            // fonts & colors
            snowfallLabel.setForeground( CONTROL_COLOR );
            snowfallLabel.setFont( CONTROL_FONT );
            _snowfallControl.setFont( CONTROL_FONT );
            _snowfallControl.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _snowfallControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // snowfall reference elevation
        JLabel snowfallReferenceElevationLabel = new JLabel( GlaciersStrings.SLIDER_SNOWFALL_REFERENCE_ELEVATION );        
        {
            double min = snowfallReferenceElevationRange.getMin();
            double max = snowfallReferenceElevationRange.getMax();
            String label = "";
            String textfieldPattern = "###0";
            String units = GlaciersStrings.UNITS_ELEVATION;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _snowfallReferenceElevationControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );

            _snowfallReferenceElevationControl.setUpDownArrowDelta( 1 );
            _snowfallReferenceElevationControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_snowfallReferenceElevationControl.isAdjusting() ) {
                        notifySnowfallReferenceElevationChanged();
                    }
                }
            } );
            
            // fonts & colors
            snowfallReferenceElevationLabel.setForeground( CONTROL_COLOR );
            snowfallReferenceElevationLabel.setFont( CONTROL_FONT );
            _snowfallReferenceElevationControl.setFont( CONTROL_FONT );
            _snowfallReferenceElevationControl.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _snowfallReferenceElevationControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // temperature
        JLabel temperatureLabel = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
        {
            double min = temperatureRange.getMin();
            double max = temperatureRange.getMax();
            String label = "";
            String textfieldPattern = "#0.0";
            String units = GlaciersStrings.UNITS_TEMPERATURE;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _temperatureControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _temperatureControl.setUpDownArrowDelta( 0.1 );
            _temperatureControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_temperatureControl.isAdjusting() ) {
                        notifyTemperatureChanged();
                    }
                }
            } );
            
            // fonts & colors
            temperatureLabel.setForeground( CONTROL_COLOR );
            temperatureLabel.setFont( CONTROL_FONT );
            _temperatureControl.setFont( CONTROL_FONT );
            _temperatureControl.getUnitsLabel().setForeground( CONTROL_COLOR );
            Dictionary d = _temperatureControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( snowfallLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _snowfallControl, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( snowfallReferenceElevationLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _snowfallReferenceElevationControl, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( temperatureLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _temperatureControl, row++, column, GridBagConstraints.WEST );
        
        Class[] excludedClasses = { JTextField.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public double getSnowfall() {
        return _snowfallControl.getValue();
    }
    
    public void setSnowfall( double snowfall ) {
        if ( snowfall != getSnowfall() ) {
            _snowfallControl.setValue( snowfall );
        }
    }
    
    public double getSnowfallReferenceElevation() {
        return _snowfallReferenceElevationControl.getValue();
    }
    
    public void setSnowfallReferenceElevation( double snowfallReferenceElevation ) {
        if ( snowfallReferenceElevation != getSnowfallReferenceElevation() ) {
            _snowfallReferenceElevationControl.setValue( snowfallReferenceElevation );
        }
    }
    
    public double getTemperature() {
        return _temperatureControl.getValue();
    }
    
    public void setTemperature( double temperature ) {
        if ( temperature != getTemperature() ) {
            _temperatureControl.setValue( temperature );
        }
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface SnowfallAndTemperatureControlPanelListener {
        public void snowfallChanged( double snowfall );
        public void snowfallReferenceElevationChanged( double snowfallReferenceElevation );
        public void temperatureChanged( double temperature );
    }
    
    public static class SnowfallAndTemperatureControlPanelAdapter implements SnowfallAndTemperatureControlPanelListener {
        public void snowfallChanged( double snowfall ) {};
        public void snowfallReferenceElevationChanged( double snowfallReferenceElevation ) {};
        public void temperatureChanged( double temperature ) {};
    }
    
    public void addSnowfallAndTemperatureControlPanelListener( SnowfallAndTemperatureControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeSnowfallAndTemperatureControlPanelListener( SnowfallAndTemperatureControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifySnowfallChanged() {
        double value = getSnowfall();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (SnowfallAndTemperatureControlPanelListener) i.next() ).snowfallChanged( value );
        }
    }
    
    private void notifySnowfallReferenceElevationChanged() {
        double value = getSnowfallReferenceElevation();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (SnowfallAndTemperatureControlPanelListener) i.next() ).snowfallReferenceElevationChanged( value );
        }
    }
    
    private void notifyTemperatureChanged() {
        double value = getTemperature();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (SnowfallAndTemperatureControlPanelListener) i.next() ).temperatureChanged( value );
        }
    }
}
