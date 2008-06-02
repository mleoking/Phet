/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
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
 * ClimateControlPanel contains climate controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClimateControlPanel extends AbstractSubPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.SUBPANEL_BACKGROUND_COLOR;
    private static final String TITLE_STRING = GlaciersStrings.TITLE_CLIMATE_CONTROLS;
    private static final Color TITLE_COLOR = GlaciersConstants.SUBPANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.SUBPANEL_TITLE_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.SUBPANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.SUBPANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final LinearValueControl _temperatureControl;
    private final LinearValueControl _snowfallControl;
    
    private final ArrayList _listeners; // list of ClimateControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ClimateControlPanel( DoubleRange temperatureRange, DoubleRange snowfallRange ) {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );
        
        _listeners = new ArrayList();
        
        // temperature
        JLabel temperatureLabel = new JLabel( GlaciersStrings.SLIDER_TEMPERATURE );
        {
            double min = temperatureRange.getMin();
            double max = temperatureRange.getMax();
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_CELSIUS;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _temperatureControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _temperatureControl.setUpDownArrowDelta( 0.01 );
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
        
        // snowfall
        JLabel snowfallLabel = new JLabel( GlaciersStrings.SLIDER_SNOWFALL );        
        {
            double min = snowfallRange.getMin();
            double max = snowfallRange.getMax();
            String label = "";
            String textfieldPattern = "#0.00";
            String units = GlaciersStrings.UNITS_ACCUMULATION;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _snowfallControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _snowfallControl.setUpDownArrowDelta( 0.01 );
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
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 0, 2, 0, 2 ) ); // top, left, bottom, right
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( temperatureLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _temperatureControl, row++, column, GridBagConstraints.WEST );
        column = 0;
        layout.addAnchoredComponent( snowfallLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _snowfallControl, row++, column, GridBagConstraints.WEST );
        
        Class[] excludedClasses = { JTextField.class };
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, excludedClasses, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setSnowfall( double snowfall ) {
        if ( snowfall != getSnowfall() ) {
        _snowfallControl.setValue( snowfall );
            notifySnowfallChanged();
        }
    }
    
    public double getSnowfall() {
        return _snowfallControl.getValue();
    }
    
    public void setTemperature( double temperature ) {
        if ( temperature != getTemperature() ) {
            _temperatureControl.setValue( temperature );
            notifyTemperatureChanged();
        }
    }
    
    public double getTemperature() {
        return _temperatureControl.getValue();
    }
    
    //----------------------------------------------------------------------------
    // Listeners
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface ClimateControlPanelListener {
        public void temperatureChanged( double temperature );
        public void snowfallChanged( double snowfall );
    }
    
    public static class ClimateControlPanelAdapter implements ClimateControlPanelListener {
        public void temperatureChanged( double temperature ) {}
        public void snowfallChanged( double snowfall ) {}
    }
    
    public void addClimateControlPanelListener( ClimateControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeClimateControlPanelListener( ClimateControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifySnowfallChanged() {
        double value = getSnowfall();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ClimateControlPanelListener) i.next() ).snowfallChanged( value );
        }
    }
    
    private void notifyTemperatureChanged() {
        double value = getTemperature();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ClimateControlPanelListener) i.next() ).temperatureChanged( value );
        }
    }
}
