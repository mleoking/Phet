/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

/**
 * LinearValueControl is for controlling values that have a linear scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LinearValueControl extends AbstractValueControl {

    /**
     * Constructor that provides a default layout for the control.
     *
     * @param min
     * @param max
     * @param label
     * @param textFieldPattern
     * @param units
     */
    public LinearValueControl( double min, double max, String label, String textFieldPattern, String units ) {
        this( min, max, label, textFieldPattern, units, new DefaultLayoutStrategy() );
    }


    /**
     * Constructor that allows you to specify a layout for the control.
     *
     * @param min
     * @param max
     * @param label
     * @param textFieldPattern
     * @param units
     * @param layoutStrategy
     */
    public LinearValueControl( double min, double max, String label, String textFieldPattern, String units, ILayoutStrategy layoutStrategy ) {
        super( new LinearSlider( min, max ), label, textFieldPattern, units, layoutStrategy );
    }

        /**
     * Constructor that provides a default layout for the control as well as the default slider value
     *
     * @param min
     * @param max
     * @param label
     * @param textFieldPattern
     * @param units
     */
    public LinearValueControl( double min, double max, double value, String label, String textFieldPattern, String units ) {
        this( min, max, value,label, textFieldPattern, units, new DefaultLayoutStrategy() );
    }

    /**
     * Constructor that accepts a layout for the control as well as the default slider value
     *
     * @param min
     * @param max
     * @param label
     * @param textFieldPattern
     * @param units
     */
    public LinearValueControl( double min, double max, double value, String label, String textFieldPattern, String units,ILayoutStrategy layoutStrategy ) {
        this( min, max, label, textFieldPattern, units, layoutStrategy );
        setValue( value );
    }
}