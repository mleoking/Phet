/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.dischargelamps.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Shape;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Battery;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;


/**
 * FaradaySlider is the graphic slider used throughout Faraday.
 * It has a knob, a knob hightlight, and a track, with no background.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BatterySlider extends GraphicSlider {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int DEFAULT_TRACK_WIDTH = 2;
    private static final Color DEFAULT_TRACK_COLOR = Color.BLACK;
    private Battery model;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Creates a slider with a specified track length.
     * Defaults are used for the track width and color.
     *
     * @param component   the parent component
     * @param trackLength the track length, in pixels
     */
    public BatterySlider( Component component, int trackLength, Battery model ) {
        this( component, trackLength, DEFAULT_TRACK_WIDTH, DEFAULT_TRACK_COLOR, model );
    }

    /**
     * Creates a slider with a specified track length, width and color.
     *
     * @param component   the parent component
     * @param trackLength the track length, in pixels
     * @param trackWidth  the track width, in pixels
     * @param trackColor  the track color
     */
    public BatterySlider( Component component, int trackLength, int trackWidth, Color trackColor,
                          Battery model ) {
        super( component );

        this.model = model;
        this.addChangeListener( new SliderListener() );

        // Background - none
        
        // Track
        Shape shape = new Rectangle( 0, 0, trackLength, trackWidth );
        PhetGraphic track = new PhetShapeGraphic( component, shape, trackColor );
        setTrack( track );
        
        // Knob
        PhetGraphic knob = new PhetImageGraphic( component, DischargeLampsConfig.SLIDER_KNOB_IMAGE );
        knob.centerRegistrationPoint();
        setKnob( knob );
        
        // Knob Highlight
        PhetGraphic knobHighlight = new PhetImageGraphic( component, DischargeLampsConfig.SLIDER_KNOB_HIGHLIGHT_IMAGE );
        knobHighlight.centerRegistrationPoint();
        setKnobHighlight( knobHighlight );
    }

    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * SliderListener handles changes to the amplitude slider.
     */
    private class SliderListener implements ChangeListener {

        /**
         * Sole constructor
         */
        public SliderListener() {
            super();
        }

        /**
         * Handles amplitude slider changes.
         *
         * @param event the event
         */
        public void stateChanged( ChangeEvent event ) {
            if( event.getSource() == BatterySlider.this ) {
                // Read the value.
                double voltage = getValue();
                // Update the model.
                model.setVoltage( voltage / model.getMaxVoltage() * DischargeLampsConfig.VOLTAGE_CALIBRATION_FACTOR );
            }
        }
    }
}
