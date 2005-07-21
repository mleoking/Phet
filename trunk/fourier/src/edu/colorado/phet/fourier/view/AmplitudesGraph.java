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

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.chart.Range2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetFlattenedGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.charts.AmplitudesChart;
import edu.colorado.phet.fourier.event.HarmonicFocusEvent;
import edu.colorado.phet.fourier.event.HarmonicFocusListener;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;


/**
 * AmplitudesGraph is the control interface for setting the amplitudes
 * of the harmonics in a Fourier series.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AmplitudesGraph extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double FLATTENED_LAYER = 1;
    private static final double SLIDERS_LAYER = 4;
    
    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 195 );
    private static final Color BACKGROUND_COLOR = new Color( 195, 195, 195 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    
    // Chart parameters
    private static final double X_MIN = FourierConfig.MIN_HARMONICS;
    private static final double X_MAX = FourierConfig.MAX_HARMONICS;
    private static final double Y_MIN = -FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAX = +FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final Range2D CHART_RANGE = new Range2D( X_MIN, Y_MIN, X_MAX, Y_MAX );
    private static final Dimension CHART_SIZE = new Dimension( 650, 130 );
    
    // Sliders parameters
    private static final int SLIDER_SPACING = 10; // space between sliders
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private AmplitudesChart _chartGraphic;
    private GraphicLayerSet _slidersGraphic;
    private ArrayList _sliders; // array of AmplitudeSlider
    private EventListenerList _listenerList;
    private EventPropagator _eventPropagator;
    private int _previousNumberOfHarmonics;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param component the parent Component
     * @param fourierSeries the model that this graphic controls
     */
    public AmplitudesGraph( Component component, FourierSeries fourierSeries ) {
        super( component );

        // Model
        _fourierSeries = fourierSeries;
        _fourierSeries.addObserver( this );
        
        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        
        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        backgroundGraphic.setStroke( BACKGROUND_STROKE );
        backgroundGraphic.setBorderColor( BACKGROUND_BORDER_COLOR );
        backgroundGraphic.setLocation( 0, 0 );
        
        // Title
        String title = SimStrings.get( "AmplitudesGraph.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( 40, 115 );
        
        // Chart
        _chartGraphic = new AmplitudesChart( component, CHART_RANGE, CHART_SIZE );
        _chartGraphic.setRegistrationPoint( 0, CHART_SIZE.height / 2 ); // at the chart's origin
        _chartGraphic.setLocation( 60, 50 + (CHART_SIZE.height / 2) );
        
        // Flatten all of the static graphics.
        PhetFlattenedGraphic flattenedGraphic = new PhetFlattenedGraphic( component );
        addGraphic( flattenedGraphic, FLATTENED_LAYER );
        flattenedGraphic.setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );
        flattenedGraphic.addGraphic( backgroundGraphic );
        flattenedGraphic.addGraphic( titleGraphic );
        flattenedGraphic.addGraphic( _chartGraphic );
        flattenedGraphic.flatten();
        flattenedGraphic.setLocation( 0, 0 );
        
        // Container for amplitude sliders -- sliders to be added in update.
        _slidersGraphic = new GraphicLayerSet( component );
        addGraphic( _slidersGraphic, SLIDERS_LAYER );
        
        // Interactivity
        {
            flattenedGraphic.setIgnoreMouse( true );
        }
        
        // Misc initialization
        {
            _sliders = new ArrayList();
            _listenerList = new EventListenerList();
            _eventPropagator = new EventPropagator();
        }
        
        reset();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
    }
    
    /**
     * Resets to the initial state.
     */
    public void reset() {
        _previousNumberOfHarmonics = 0; // force an update
        update();
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update() {

        int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();

        if ( numberOfHarmonics != _previousNumberOfHarmonics ) {
            
            _slidersGraphic.clear();

            int totalSpace = ( FourierConfig.MAX_HARMONICS + 1 ) * SLIDER_SPACING;
            int barWidth = ( CHART_SIZE.width - totalSpace ) / FourierConfig.MAX_HARMONICS;
            double deltaWavelength = ( VisibleColor.MAX_WAVELENGTH - VisibleColor.MIN_WAVELENGTH ) / ( numberOfHarmonics - 1 );

            for ( int i = 0; i < numberOfHarmonics; i++ ) {

                // Get the ith harmonic.
                Harmonic harmonic = _fourierSeries.getHarmonic( i );

                AmplitudeSlider slider = null;
                if ( i < _sliders.size() ) {
                    // Reuse an existing slider.
                    slider = (AmplitudeSlider) _sliders.get( i );
                    slider.setHarmonic( harmonic );
                }
                else {
                    // Allocate a new slider.
                    slider = new AmplitudeSlider( getComponent(), harmonic );
                    slider.addHarmonicFocusListener( _eventPropagator ); // notify when the slider gets focus
                    slider.addChangeListener( _eventPropagator ); // notify when the slider value is changed
                }
                _slidersGraphic.addGraphic( slider );

                // Slider size.
                slider.setMaxSize( barWidth, CHART_SIZE.height );

                // Slider location.
                int x = _chartGraphic.getLocation().x + ( ( i + 1 ) * SLIDER_SPACING ) + ( i * barWidth ) + ( barWidth / 2 );
                int y = _chartGraphic.getLocation().y;
                slider.setLocation( x, y );
                
                _previousNumberOfHarmonics = numberOfHarmonics;
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Adds a HarmonicFocusListener.
     * 
     * @param listener
     */
    public void addHarmonicFocusListener( HarmonicFocusListener listener ) {
        _listenerList.add( HarmonicFocusListener.class, listener );
    }
  
    /**
     * Removes a HarmonicFocusListener.
     * 
     * @param listener
     */
    public void removeHarmonicFocusListener( HarmonicFocusListener listener ) {
        _listenerList.remove( HarmonicFocusListener.class, listener );
    }
    
    /**
     * Adds a ChangeListener.
     * 
     * @param listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }
  
    /**
     * Removes a ChangeListener.
     * 
     * @param listener
     */
    public void removeChangeListenerListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }
    
    /*
     * Propogates events from AmplitudeSliders to listeners.
     */
    private class EventPropagator implements ChangeListener, HarmonicFocusListener {

        /* Invoked when an amplitude slider is moved. */
        public void stateChanged( ChangeEvent event ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == ChangeListener.class ) {
                    ( (ChangeListener) listeners[i + 1] ).stateChanged( event );
                }
            }
        }
        
        /* Invoked when an amplitude slider gains focus. */
        public void focusGained( HarmonicFocusEvent event ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == HarmonicFocusListener.class ) {
                    ( (HarmonicFocusListener) listeners[i + 1] ).focusGained( event );
                }
            }
        }

        /* Invoked when an amplitude slider loses focus. */
        public void focusLost( HarmonicFocusEvent event ) {
            Object[] listeners = _listenerList.getListenerList();
            for ( int i = 0; i < listeners.length; i += 2 ) {
                if ( listeners[i] == HarmonicFocusListener.class ) {
                    ( (HarmonicFocusListener) listeners[i + 1] ).focusLost( event );
                }
            }
        }
    }
}