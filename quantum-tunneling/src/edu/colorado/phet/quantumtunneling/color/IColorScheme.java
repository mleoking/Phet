/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.color;

import java.awt.Color;


/**
 * IColorScheme is the interface for a color scheme.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public interface IColorScheme {
    
    /**
     * Gets the color used for the background of the charts.
     * @return
     */
    public Color getChartColor();
    
    /**
     * Gets the color used for tick marks and labels.
     * @return
     */
    public Color getTicksColor();
    
    /**
     * Gets the color used for horizontal and vertical gridlines.
     * @return
     */
    public Color getGridlinesColor();
    
    /**
     * Gets the color used for region boundary markers.
     * @return
     */
    public Color getRegionMarkerColor();
    
    /**
     * Gets the color used to plot total energy.
     * @return
     */
    public Color getTotalEnergyColor();
    
    /**
     * Gets the color used to plot potential energy.
     * @return
     */
    public Color getPotentialEnergyColor();
    
    /**
     * Gets the color used to plot a wave function's real part.
     * @return
     */
    public Color getRealColor();
    
    /**
     * Gets the color used to plot a wave function's imaginary part.
     * @return
     */
    public Color getImaginaryColor();
    
    /**
     * Gets the color used to plot a wave function's magnitude.
     * @return
     */
    public Color getMagnitudeColor();
    
    /**
     * Gets the color used to plot a wave function's probability density.
     * @return
     */
    public Color getProbabilityDensityColor();
    
    /**
     * Gets the color used to fill the energy drag handles.
     * @return
     */
    public Color getDragHandleFillColor();

    /**
     * Gets the color used to stroke the energy drag handles.
     * @return
     */
    public Color getDragHandleStrokeColor();
    
    /**
     * Gets the color used for the values that appear on the energy drag handles.
     * @return
     */
    public Color getDragHandleTextColor();
}
