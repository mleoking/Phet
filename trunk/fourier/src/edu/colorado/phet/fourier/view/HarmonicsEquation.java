/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import edu.colorado.phet.common.view.phetgraphics.HTMLGraphic;
import edu.colorado.phet.fourier.FourierConstants;


/**
 * HarmonicsEquation
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsEquation extends HTMLGraphic {
    
    private static final Font DEFAULT_FONT = new Font( "Lucida Sans", Font.ITALIC, 20 );
    private static final Color DEFAULT_COLOR = Color.BLACK;
    
    private static final String TEXT_SPACE_WAVE_NUMBER = 
        "<html><i>A<sub>n</sub> = sin( k<sub>n</sub>x )</html>";
    private static final String TEXT_SPACE_WAVELENGTH = 
        "<html><i>A<sub>n</sub> = sin( 2\u03c0x / \u03BB<sub>n</sub> )</html>";
    private static final String TEXT_SPACE_MODE = 
        "<html><i>A<sub>n</sub> = sin( 2\u03c0nx / L )</html>";
    
    private static final String TEXT_TIME_ANGULAR_FREQUENCY = 
        "<html><i>A<sub>n</sub> = sin( \u03C9<sub>n</sub>t )</html>";
    private static final String TEXT_TIME_FREQUENCY = 
        "<html><i>A<sub>n</sub> = sin( 2\u03c0f<sub>n</sub>t )</html>";
    private static final String TEXT_TIME_PERIOD = 
        "<html><i>A<sub>n</sub> = sin( 2\u03c0t / T<sub>n</sub> )</html>";
    private static final String TEXT_TIME_MODE = 
        "<html><i>A<sub>n</sub> = sin( 2\u03c0nt / T )</html>";
    
    private static final String TEXT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY = 
        "<html><i>A<sub>n</sub> = sin( k<sub>n</sub>x - \u03BB<sub>n</sub>t )</html>";
    private static final String TEXT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD =
        "<html><i>A<sub>n</sub> = sin( ( 2\u03C9x / \u03BB<sub>n</sub> ) - ( 2\u03c0t / T<sub>n</sub> ) )</html>";
    private static final String TEXT_SPACE_AND_TIME_MODE = 
        "<html><i>A<sub>n</sub> = sin( ( 2\u03c0nx / L ) - ( 2\u03c0nt / T ) )</html>";
    
    /**
     * Sole constructor.
     * 
     * @param component
     * @param domain
     */
    public HarmonicsEquation( Component component ) {
        super( component, DEFAULT_FONT, "", DEFAULT_COLOR );
        setForm( FourierConstants.DOMAIN_SPACE, FourierConstants.MATH_FORM_WAVE_NUMBER );
    }
    
    public void setForm( int domain, int mathForm ) {
        
        System.out.println( "HarmonicsEquation.setForm: domain=" + domain + " mathForm=" + mathForm );//XXX
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        
        String text = null;
        
        switch ( domain ) {
            case FourierConstants.DOMAIN_SPACE:
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER:
                        text = TEXT_SPACE_WAVE_NUMBER;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH:
                        text = TEXT_SPACE_WAVELENGTH;
                         break;
                    case FourierConstants.MATH_FORM_MODE:
                        text = TEXT_SPACE_MODE;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_TIME:
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_ANGULAR_FREQUENCY:
                        text = TEXT_TIME_ANGULAR_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_FREQUENCY:
                        text = TEXT_TIME_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_PERIOD:
                        text = TEXT_TIME_PERIOD;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        text = TEXT_TIME_MODE;
                        break;
                    default:
                }
                break;
            case FourierConstants.DOMAIN_SPACE_AND_TIME:
                switch ( mathForm ) {
                    case FourierConstants.MATH_FORM_WAVE_NUMBER_AND_ANGULAR_FREQUENCY:
                        text = TEXT_SPACE_AND_TIME_WAVENUMBER_AND_ANGULAR_FREQUENCY;
                        break;
                    case FourierConstants.MATH_FORM_WAVELENGTH_AND_PERIOD:
                        text = TEXT_SPACE_AND_TIME_WAVELENGTH_AND_PERIOD;
                        break;
                    case FourierConstants.MATH_FORM_MODE:
                        text = TEXT_SPACE_AND_TIME_MODE;
                        break;
                    default:
                }
                break;
            default:
        }
        
        if ( text == null ) {  
            throw new IllegalArgumentException( 
                    "illegal combination of domain (" + domain + ") " +
                    		"and math form (" + mathForm + ")" );
        }
        else {
            setHTML( text );
        }
    }
}
