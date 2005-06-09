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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.model.FourierSeries;
import edu.colorado.phet.fourier.model.Harmonic;
import edu.colorado.phet.fourier.model.HarmonicDataSet;
import edu.colorado.phet.fourier.util.FourierUtils;


/**
 * HarmonicsGraphic
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class HarmonicsGraphic extends GraphicLayerSet implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;

    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 210 );
    private static final Color BACKGROUND_COLOR = new Color( 255, 231, 222 ); // pastel red
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( "Lucida Sans", Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin

    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    
    // Tick Mark parameter
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new Font( "Lucida Sans", Font.BOLD, 12 );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;
    private static final Color MINOR_TICK_COLOR = MAJOR_TICK_COLOR;
    
    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 1f );
    private static final Color MINOR_GRIDLINE_COLOR = new Color( 0, 0, 0, 100 );
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.5f );
    
    // X Axis parameters
    private static final double L = 1.0;
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

    // Y Axis parameters
    private static final double Y_RANGE_START = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_MAJOR_TICK_SPACING = 0.5;
    private static final double Y_MINOR_TICK_SPACING = 0.1;

    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 600, 130 );

    // Wave parameters
    private static final Stroke WAVE_STROKE = new BasicStroke( 1f );
    private static final int NUMBER_OF_DATA_POINTS = 1000;
    private static final int MAX_FUNDAMENTAL_CYCLES = 4;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FourierSeries _fourierSeriesModel;
    private Chart _chartGraphic;
    private ArrayList _dataSets; // array of HarmonicDataSet
    private int _previousNumberOfHarmonics;
    private int _waveType;
    private ZoomControl _horizontalZoomControl;
    private int _xZoomLevel;
    private LabelTable _spaceLabels1, _spaceLabels2;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param component          the parent Component
     * @param fourierSeriesModel the Fourier series that this view displays
     */
    public HarmonicsGraphic( Component component, FourierSeries fourierSeriesModel ) {
        super( component );

        // Enable antialiasing
        setRenderingHints( new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON ) );

        // Background
        PhetShapeGraphic backgroundGraphic = new PhetShapeGraphic( component );
        backgroundGraphic.setShape( new Rectangle( 0, 0, BACKGROUND_SIZE.width, BACKGROUND_SIZE.height ) );
        backgroundGraphic.setPaint( BACKGROUND_COLOR );
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        backgroundGraphic.setLocation( -100, -125 );
        
        // Model
        _fourierSeriesModel = fourierSeriesModel;
        _fourierSeriesModel.addObserver( this );
        
        // Title
        String title = SimStrings.get( "HarmonicsGraphic.title" );
        PhetTextGraphic titleGraphic = new PhetTextGraphic( component, TITLE_FONT, title, TITLE_COLOR );
        titleGraphic.centerRegistrationPoint();
        titleGraphic.rotate( -( Math.PI / 2 ) );
        titleGraphic.setLocation( TITLE_X_OFFSET, 0 );
        addGraphic( titleGraphic, TITLE_LAYER );

        // Chart
        {
            _chartGraphic = new Chart( component, CHART_RANGE, CHART_SIZE );
            addGraphic( _chartGraphic, CHART_LAYER );

            _chartGraphic.setLocation( 0, -( CHART_SIZE.height / 2 ) );

            // Symbolic labels for the X axis
            {
                _spaceLabels1 = new LabelTable();
                _spaceLabels1.put( -1.00 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.75 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.50 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( -0.25 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put(     0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( +0.25 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( +0.50 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( +0.75 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3L/4", MAJOR_TICK_COLOR ) );
                _spaceLabels1.put( +1.00 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L", MAJOR_TICK_COLOR ) );

                _spaceLabels2 = new LabelTable();
                _spaceLabels2.put( -2.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-2L", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -1.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-3L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -1.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( -0.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "-L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put(    0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "0", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( +0.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( +1.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "L", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( +1.5 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "3L/2", MAJOR_TICK_COLOR ) );
                _spaceLabels2.put( +2.0 * L, new PhetTextGraphic( component, MAJOR_TICK_FONT, "2L", MAJOR_TICK_COLOR ) );
            }

            // X axis
            {
                _chartGraphic.getXAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getXAxis().setColor( AXIS_COLOR );

                // No ticks or labels on the axis
                _chartGraphic.getXAxis().setMajorTicksVisible( false );
                _chartGraphic.getXAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getXAxis().setMinorTicksVisible( false );
                _chartGraphic.getXAxis().setMinorTickLabelsVisible( false );

                // Major ticks with labels below the chart
                _chartGraphic.getHorizontalTicks().setMajorTicksVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getHorizontalTicks().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizontalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                _chartGraphic.getHorizontalTicks().setMajorTickFont( MAJOR_TICK_FONT );
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );

                // Vertical gridlines for major ticks.
                _chartGraphic.getVerticalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMajorTickSpacing( X_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );
                
                // Vertical gridlines for minor ticks.
                _chartGraphic.getVerticalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getVerticalGridlines().setMinorTickSpacing( X_MINOR_TICK_SPACING );
                _chartGraphic.getVerticalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                _chartGraphic.getVerticalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
            }

            // Y axis
            {
                _chartGraphic.getYAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getYAxis().setColor( AXIS_COLOR );

                // No ticks or labels on the axis
                _chartGraphic.getYAxis().setMajorTicksVisible( false );
                _chartGraphic.getYAxis().setMajorTickLabelsVisible( false );
                _chartGraphic.getYAxis().setMinorTicksVisible( false );
                _chartGraphic.getYAxis().setMinorTickLabelsVisible( false );

                // Major ticks with labels to the left of the chart
                _chartGraphic.getVerticalTicks().setMajorTicksVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickLabelsVisible( true );
                _chartGraphic.getVerticalTicks().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getVerticalTicks().setMajorTickStroke( MAJOR_TICK_STROKE );
                _chartGraphic.getVerticalTicks().setMajorTickFont( MAJOR_TICK_FONT );

                // Horizontal gridlines for major ticks
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( Y_MAJOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesColor( MAJOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMajorGridlinesStroke( MAJOR_GRIDLINE_STROKE );

                // Horizontal gridlines for minor ticks
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesVisible( true );
                _chartGraphic.getHorizonalGridlines().setMinorTickSpacing( Y_MINOR_TICK_SPACING );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesColor( MINOR_GRIDLINE_COLOR );
                _chartGraphic.getHorizonalGridlines().setMinorGridlinesStroke( MINOR_GRIDLINE_STROKE );
            }
        }

        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( CHART_SIZE.width + 10, -50 );
        }

        // Interactivity
        {
            titleGraphic.setIgnoreMouse( true );
            
            EventListener listener = new EventListener();
            _horizontalZoomControl.addActionListener( listener );
        }

        // Misc initialization
        {
            _xZoomLevel = 0;
            _dataSets = new ArrayList();
            _previousNumberOfHarmonics = -1; // force update
        }

        update();
    }

    /**
     * Finalizes an instance of this type.
     * Call this method prior to releasing all references to an object of this type.
     */
    public void finalize() {
        _fourierSeriesModel.removeObserver( this );
        _fourierSeriesModel = null;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Sets the wave type, sine or cosine.
     *
     * @param waveType FourierConstants.WAVE_TYPE_SINE or FourierConstants.WAVE_TYPE_COSINE
     */
    public void setWaveType( int waveType ) {
        if ( waveType != _waveType ) {
            _waveType = waveType;
            for ( int i = 0; i < _dataSets.size(); i++ ) {
                HarmonicDataSet dataSet = (HarmonicDataSet) _dataSets.get( i );
                dataSet.setWaveType( _waveType );
            }
            repaint();
        }
    }

    /**
     * Gets the wave type.
     *
     * @return FourierConstants.WAVE_TYPE_SINE or FourierConstants.WAVE_TYPE_COSINE
     */
    public int getWaveType() {
        return _waveType;
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------

    /**
     * Updates the view to match the model.
     */
    public void update() {

        int numberOfHarmonics = _fourierSeriesModel.getNumberOfHarmonics();

        // If the number of harmonics has changed...
        if ( _previousNumberOfHarmonics != numberOfHarmonics ) {

            // Dump the old data sets & graphics.
            _dataSets.clear();
            _chartGraphic.removeAllDataSetGraphics();

            // Create new data sets & graphics for each harmonic.
            for ( int i = 0; i < _fourierSeriesModel.getNumberOfHarmonics(); i++ ) {
                Harmonic harmonic = _fourierSeriesModel.getHarmonic( i );

                HarmonicDataSet dataSet = new HarmonicDataSet( harmonic, NUMBER_OF_DATA_POINTS, L, MAX_FUNDAMENTAL_CYCLES );
                _dataSets.add( dataSet );

                Color harmonicColor = FourierUtils.calculateHarmonicColor( i );
                DataSetGraphic dataSetGraphic = new LinePlot( getComponent(), _chartGraphic, dataSet, WAVE_STROKE, harmonicColor );
                _chartGraphic.addDataSetGraphic( dataSetGraphic );
            }

            _previousNumberOfHarmonics = numberOfHarmonics;

            repaint();
        }
    }

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /* 
     * EventListener handles events related to this view.
     */
    private class EventListener implements ActionListener {

        public EventListener() {}

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _horizontalZoomControl ) {
                handleHorizontalZoom( event.getID() );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    /*
     * Handles horizontal zooming.
     * 
     * @param actionID indicates the type of zoom
     */
    private void handleHorizontalZoom( int actionID ) {

        // Adjust the zoom level.
        if ( actionID == ZoomControl.ACTION_ID_ZOOM_IN ) {
            _xZoomLevel++;
        }
        else {
            _xZoomLevel--;
        }
        
        // Obtuse sqrt(2) zoom factor, immune to numeric precision errors 
        double zoomFactor = Math.pow( 2, Math.abs( _xZoomLevel ) / 2.0 );
        
        // Adjust the chart's horizontal range.
        Range2D range = _chartGraphic.getRange();
        double xRange;
        if ( _xZoomLevel == 0 ) {
            xRange = ( L / 2 );
        }
        else if ( _xZoomLevel > 0 ) {
            xRange = ( L / 2 ) / zoomFactor; 
        }
        else {
            xRange = ( L / 2 ) * zoomFactor;
        }
        range.setMaxX( xRange );
        range.setMinX( -xRange );
        _chartGraphic.setRange( range );

        updateTicksAndGridlines();
        updateZoomButtons();
    }
    
    /*
     * Adjusts ticks and gridlines to match the chart range.
     */
    private void updateTicksAndGridlines() {
        
        Range2D range = _chartGraphic.getRange();
        
        // X axis ticks and gridlines
        {
            if ( _xZoomLevel > -3 ) {
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels1 );
            }
            else {
                _chartGraphic.getHorizontalTicks().setMajorLabels( _spaceLabels2 );
            }
        }
    }
    
    /*
     * Enables and disabled zoom buttons based on the current
     * zoom levels and range of the chart.
     */
    private void updateZoomButtons() {
        
        Range2D range = _chartGraphic.getRange();
        
        // Horizontal buttons
        if ( range.getMaxX() >= X_RANGE_MAX ) {
            _horizontalZoomControl.setZoomOutEnabled( false );
            _horizontalZoomControl.setZoomInEnabled( true );
        }
        else if ( range.getMaxX() <= X_RANGE_MIN ) {
            _horizontalZoomControl.setZoomOutEnabled( true );
            _horizontalZoomControl.setZoomInEnabled( false );
        }
        else {
            _horizontalZoomControl.setZoomOutEnabled( true );
            _horizontalZoomControl.setZoomInEnabled( true );
        }
    }
}
