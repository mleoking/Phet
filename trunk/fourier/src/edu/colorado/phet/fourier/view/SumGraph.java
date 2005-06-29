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
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JCheckBox;

import edu.colorado.phet.chart.*;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.fourier.FourierConfig;
import edu.colorado.phet.fourier.FourierConstants;
import edu.colorado.phet.fourier.MathStrings;
import edu.colorado.phet.fourier.charts.FourierSumPlot;
import edu.colorado.phet.fourier.charts.StringLabelTable;
import edu.colorado.phet.fourier.control.ZoomControl;
import edu.colorado.phet.fourier.event.ZoomEvent;
import edu.colorado.phet.fourier.event.ZoomListener;
import edu.colorado.phet.fourier.model.FourierSeries;


/**
 * SumGraph
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class SumGraph extends GraphicLayerSet implements SimpleObserver, ZoomListener, ModelElement {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // Layers
    private static final double BACKGROUND_LAYER = 1;
    private static final double TITLE_LAYER = 2;
    private static final double CHART_LAYER = 3;
    private static final double CONTROLS_LAYER = 4;
    private static final double MATH_LAYER = 5;

    // Background parameters
    private static final Dimension BACKGROUND_SIZE = new Dimension( 800, 200 );
    private static final Color BACKGROUND_COLOR = new Color( 215, 215, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color BACKGROUND_BORDER_COLOR = Color.BLACK;
    
    // Title parameters
    private static final Font TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 20 );
    private static final Color TITLE_COLOR = Color.BLUE;
    private static final int TITLE_X_OFFSET = -15; // from origin
    
    // Axis parameter
    private static final Color AXIS_COLOR = Color.BLACK;
    private static final Stroke AXIS_STROKE = new BasicStroke( 2f );
    private static final Font AXIS_TITLE_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 16 );
    private static final Color AXIS_TITLE_COLOR = Color.BLACK;
    
    // Range labels
    private static final boolean RANGE_LABELS_VISIBLE = false;
    private static final NumberFormat RANGE_LABELS_FORMAT = new DecimalFormat( "0.00" );
    
    // Tick Mark parameter
    private static final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private static final Font MAJOR_TICK_FONT = new Font( FourierConfig.FONT_NAME, Font.BOLD, 12 );
    private static final Color MAJOR_TICK_COLOR = Color.BLACK;
    private static final Stroke MINOR_TICK_STROKE = MAJOR_TICK_STROKE;
    private static final Font MINOR_TICK_FONT = MAJOR_TICK_FONT;
    private static final Color MINOR_TICK_COLOR = MAJOR_TICK_COLOR;
    
    // Gridline parameters
    private static final Color MAJOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MAJOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRIDLINE_COLOR = Color.BLACK;
    private static final Stroke MINOR_GRIDLINE_STROKE = new BasicStroke( 0.25f );
    
    // X axis
    private static final double L = FourierConstants.L; // do not change!
    private static final double X_RANGE_START = ( L / 2 );
    private static final double X_RANGE_MIN = ( L / 4 );
    private static final double X_RANGE_MAX = ( 2 * L );
    private static final double X_MAJOR_TICK_SPACING = ( L / 4 );
    private static final double X_MINOR_TICK_SPACING = ( L / 8 );

    // Y axis
    private static final double Y_RANGE_START = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MIN = FourierConfig.MAX_HARMONIC_AMPLITUDE;
    private static final double Y_RANGE_MAX = 12.0;
    private static final double Y_MAJOR_TICK_SPACING = 5.0;
    private static final double Y_MINOR_TICK_SPACING = 1.0;
    private static final int Y_ZOOM_STEP = 2;
    
    // Chart parameters
    private static final Range2D CHART_RANGE = new Range2D( -X_RANGE_START, -Y_RANGE_START, X_RANGE_START, Y_RANGE_START );
    private static final Dimension CHART_SIZE = new Dimension( 580, 130 );
    
    // Wave parameters
    private static final int NUMBER_OF_DATA_POINTS = 1000;
    private static final int MAX_FUNDAMENTAL_CYCLES = 4;
    private static final Stroke SUM_STROKE = new BasicStroke( 1f );
    private static final Color SUM_COLOR = Color.BLACK;
    private static final double SUM_PIXELS_PER_POINT = 2;
    private static final Stroke PRESET_STROKE = new BasicStroke( 4f );
    private static final Color PRESET_COLOR = Color.LIGHT_GRAY;
    
    // Math parameters
    private static final Font MATH_FONT = new Font( FourierConfig.FONT_NAME, Font.PLAIN, 18 );
    private static final Color MATH_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private FourierSeries _fourierSeries;
    private Chart _chartGraphic;
    private SumEquation _mathGraphic;
    private PhetTextGraphic _xAxisTitleGraphic;
    private String _xAxisTitleTime, _xAxisTitleSpace;
    private FourierSumPlot _sumPlot;
    private LinePlot _presetPlot;
    private ZoomControl _horizontalZoomControl, _verticalZoomControl;
    private JCheckBox _autoScaleCheckBox;
    private int _xZoomLevel;
    private int _domain;
    private int _mathForm;
    private StringLabelTable _spaceLabels1, _spaceLabels2;
    private StringLabelTable _timeLabels1, _timeLabels2;
    private boolean _autoScaleEnabled;
    private Point2D[] _points;
    private int _previousNumberOfHarmonics;
    private int _previousPreset;
    private int _previousWaveType;
    
    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    public SumGraph( Component component, FourierSeries fourierSeries ) {
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
        addGraphic( backgroundGraphic, BACKGROUND_LAYER );
        backgroundGraphic.setLocation( -100, -115 );
        
        // Title
        String title = SimStrings.get( "SumGraphic.title" );
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

            // X axis
            {
                _chartGraphic.getXAxis().setStroke( AXIS_STROKE );
                _chartGraphic.getXAxis().setColor( AXIS_COLOR );

                // Title
                _xAxisTitleTime = "" + MathStrings.C_TIME;
                _xAxisTitleSpace = "" + MathStrings.C_SPACE;
                _xAxisTitleGraphic = new PhetTextGraphic( component, AXIS_TITLE_FONT, _xAxisTitleSpace, AXIS_TITLE_COLOR );
                _chartGraphic.setXAxisTitle( _xAxisTitleGraphic );
                
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

                // Range labels
                _chartGraphic.getVerticalTicks().setRangeLabelsVisible( RANGE_LABELS_VISIBLE );
                _chartGraphic.getVerticalTicks().setRangeLabelsNumberFormat( RANGE_LABELS_FORMAT );
                
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
        
        // Math
        {
            _mathGraphic = new SumEquation( component );
            addGraphic( _mathGraphic, MATH_LAYER );
            _mathGraphic.centerRegistrationPoint();
            _mathGraphic.setLocation( CHART_SIZE.width / 2, -(CHART_SIZE.height / 2) - 20 );
        }
        
        // Zoom controls
        {
            _horizontalZoomControl = new ZoomControl( component, ZoomControl.HORIZONTAL );
            addGraphic( _horizontalZoomControl, CONTROLS_LAYER );
            _horizontalZoomControl.setLocation( CHART_SIZE.width + 20, -50 );
            
            _verticalZoomControl = new ZoomControl( component, ZoomControl.VERTICAL );
            addGraphic( _verticalZoomControl, CONTROLS_LAYER );
            _verticalZoomControl.setLocation( _horizontalZoomControl.getX(), 
                    _horizontalZoomControl.getY() + _horizontalZoomControl.getHeight() + 5 );
        }
        
        // Auto Scale control
        {
            _autoScaleCheckBox = new JCheckBox( SimStrings.get( "SumGraphic.autoScale" ) );
            _autoScaleCheckBox.setBackground( new Color( 255, 255, 255, 0 ) );
            PhetGraphic autoScaleGraphic = PhetJComponent.newInstance( component, _autoScaleCheckBox );
            addGraphic( autoScaleGraphic, CONTROLS_LAYER );
            autoScaleGraphic.setLocation( _verticalZoomControl.getX(), 
                    _verticalZoomControl.getY() + _verticalZoomControl.getHeight() + 5 );
        }
        
        // Preset plot
        _presetPlot = new LinePlot( getComponent(), _chartGraphic, new DataSet(), PRESET_STROKE, PRESET_COLOR );
        _chartGraphic.addDataSetGraphic( _presetPlot );
        
        // Sum plot
        _sumPlot = new FourierSumPlot( getComponent(), _chartGraphic, _fourierSeries );
        _sumPlot.setPeriod( L );
        _sumPlot.setPixelsPerPoint( SUM_PIXELS_PER_POINT );
        _sumPlot.setStroke( SUM_STROKE );
        _sumPlot.setBorderColor( SUM_COLOR );
        _chartGraphic.addDataSetGraphic( _sumPlot );
        
        // Interactivity
        {
            backgroundGraphic.setIgnoreMouse( true );
            titleGraphic.setIgnoreMouse( true );
            _chartGraphic.setIgnoreMouse( true );
            _mathGraphic.setIgnoreMouse( true );
            
            _horizontalZoomControl.addZoomListener( this );
            _verticalZoomControl.addZoomListener( this );
            
            EventListener listener = new EventListener();
            _autoScaleCheckBox.addActionListener( listener );
        }
        
        // Misc initialization
        {
            _points = new Point2D[ NUMBER_OF_DATA_POINTS + 1 ];
            for ( int i = 0; i < _points.length; i++ ) {
                _points[ i ] = new Point2D.Double();
            }
        }
        
        reset();
    }
    
    /**
     * Call this method prior to releasing all references to an object of this type.
     */
    public void cleanup() {
        _fourierSeries.removeObserver( this );
        _fourierSeries = null;
        _horizontalZoomControl.removeAllZoomListeners();
        _verticalZoomControl.removeAllZoomListeners();
    }

    /**
     * Resets to the initial state.
     */
    public void reset() {

        // Chart
        {
            _xZoomLevel = 0;
            _chartGraphic.setRange( CHART_RANGE );
            _autoScaleEnabled = false;
            _autoScaleCheckBox.setSelected( _autoScaleEnabled );
            updateLabelsAndLines();
            updateZoomButtons();
            _presetPlot.setVisible( false );
        }
        
        _domain = FourierConstants.DOMAIN_SPACE;
        
        // Math Mode
        _mathForm = FourierConstants.MATH_FORM_WAVE_NUMBER;
        _mathGraphic.setVisible( false );
        updateMath();
        
        // Synchronize with model
        _previousNumberOfHarmonics = 0; // force an update
        _previousPreset = -1;
        _previousWaveType = -1;
        update();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the horizontal zoom control.
     * 
     * @return the horizontal zoom control
     */
    public ZoomControl getHorizontalZoomControl() {
        return _horizontalZoomControl;
    }
    
    /**
     * Enables things that are related to "math mode".
     * 
     * @param enabled true or false
     */
    public void setMathEnabled( boolean enabled ) {
        _mathGraphic.setVisible( enabled );
    }
    
    /**
     * Sets the domain and math form.
     * Together, these values determines how the chart is 
     * labeled, and the format of the equation shown above the chart.
     * 
     * @param domain
     * @param mathForm
     */
    public void setDomainAndMathForm( int domain, int mathForm ) {
        assert( FourierConstants.isValidDomain( domain ) );
        assert( FourierConstants.isValidMathForm( mathForm ) );
        _domain = domain;
        _mathForm = mathForm;
        updateLabelsAndLines();
        updateMath();
        _previousPreset = -1;
        update();
    }
    
    /**
     * Enables or disables auto scaling of the Y axis.
     * 
     * @param autoRescaleEnabled true or false
     */
    private void setAutoScaleEnabled( boolean autoRescaleEnabled ) {
        if ( autoRescaleEnabled != _autoScaleEnabled ) {
            _autoScaleEnabled = autoRescaleEnabled;
            updateZoomButtons();
            update();
        }
    }
    
    public void setPresetEnabled( boolean enabled ) {
            _presetPlot.setVisible( enabled );
    }

    //----------------------------------------------------------------------------
    // SimpleObserver implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     */
    public void update() {

        //FourierLog.trace( "SumGraph.update" );
        
        _sumPlot.updateDataSet();
        
        // If auto scaling is enabled, adjust the vertical scale to fit the curve.
        if ( _autoScaleEnabled ) {
            Range2D range = _chartGraphic.getRange();
            double maxAmplitude = _sumPlot.getMaxAmplitude();
            if ( maxAmplitude != range.getMaxY() ) {
                range.setMinY( -maxAmplitude );
                range.setMaxY( +maxAmplitude );
                _chartGraphic.setRange( range );
                updateLabelsAndLines();
                updateZoomButtons();
            }
        }

        // If the preset has changed, update the preset waveform.
        int preset = _fourierSeries.getPreset();
        int waveType = _fourierSeries.getWaveType();
        if ( preset != _previousPreset || waveType != _previousWaveType ) {

            _sumPlot.setStartX( 0 );
            _presetPlot.getDataSet().clear();

            Point2D[] points = null;
            if ( preset == FourierConstants.PRESET_SINE_COSINE ) {
                points = _sumPlot.getDataSet().getPoints();
            }
            else {
                points = FourierConstants.getPresetPoints( preset, waveType );
            }

            if ( points != null ) {
                Point2D[] copyPoints = new Point2D[points.length];
                for ( int i = 0; i < points.length; i++ ) {
                    copyPoints[i] = new Point2D.Double( points[i].getX(), points[i].getY() );
                }
                _presetPlot.getDataSet().addAllPoints( copyPoints );
            }

            _previousPreset = preset;
            _previousWaveType = waveType;
        }

        // If the number of harmonics has changed, update the equation.
        int numberOfHarmonics = _fourierSeries.getNumberOfHarmonics();
        if ( _previousNumberOfHarmonics != numberOfHarmonics ) {
            updateMath();
            _previousNumberOfHarmonics = numberOfHarmonics;
        }

        repaint();
    }     
    
    //----------------------------------------------------------------------------
    // ZoomListener implementation
    //----------------------------------------------------------------------------
    
    public void zoomPerformed( ZoomEvent event ) {
        int zoomType = event.getZoomType();
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN || zoomType == ZoomEvent.HORIZONTAL_ZOOM_OUT ) {
            handleHorizontalZoom( zoomType );
        }
        else if ( zoomType == ZoomEvent.VERTICAL_ZOOM_IN || zoomType == ZoomEvent.VERTICAL_ZOOM_OUT ) {
            handleVerticalZoom( zoomType );
        }
        else {
            throw new IllegalArgumentException( "unexpected event: " + event );
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
            if ( event.getSource() == _autoScaleCheckBox ) {
                setAutoScaleEnabled( _autoScaleCheckBox.isSelected() );
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
    private void handleHorizontalZoom( int zoomType ) {

        // Adjust the zoom level.
        if ( zoomType == ZoomEvent.HORIZONTAL_ZOOM_IN ) {
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

        updateLabelsAndLines();
        updateZoomButtons();
    }
    
    /*
     * Handles vertical zooming.
     * 
     * @param actionID indicates the type of zoom
     */
    private void handleVerticalZoom( int zoomType ) {

        // Get the chart's vertical range.
        Range2D range = _chartGraphic.getRange();
        double yRange = range.getMaxY();

        // Round to an integral multiple of Y_ZOOM_STEP.
        if ( yRange % Y_ZOOM_STEP > 0 ) {
            yRange = ( (int) ( yRange / Y_ZOOM_STEP ) ) * Y_ZOOM_STEP;
        }
        
        // Adjust the scale.
        if ( zoomType == ZoomEvent.VERTICAL_ZOOM_IN ) {
            yRange -= Y_ZOOM_STEP;
        }
        else {
            yRange += Y_ZOOM_STEP;
        }

        // Constrain the scale's range.
        if ( yRange < Y_RANGE_MIN ) {
            yRange = Y_RANGE_MIN;
        }
        else if ( yRange > Y_RANGE_MAX ) {
            yRange = Y_RANGE_MAX;
        }

        // Change the chart's vertical range.
        range.setMaxY( yRange );
        range.setMinY( -yRange );
        _chartGraphic.setRange( range );
        
        updateLabelsAndLines();
        updateZoomButtons();
    }
    
    /*
     * Adjusts labels, ticks and gridlines to match the chart range.
     */
    private void updateLabelsAndLines() {

        // X axis
        if ( _domain == FourierConstants.DOMAIN_TIME ) {
            _xAxisTitleGraphic.setText( _xAxisTitleTime );
            if ( _xZoomLevel > -3 ) {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getTimeLabels1() );
            }
            else {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getTimeLabels2() );
            }   
        }
        else { /* DOMAIN_SPACE or DOMAIN_SPACE_AND_TIME */
            _xAxisTitleGraphic.setText( _xAxisTitleSpace );
            if ( _xZoomLevel > -3 ) {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getSpaceLabels1() );
            }
            else {
                _chartGraphic.getHorizontalTicks().setMajorLabels( getSpaceLabels2() );
            }
        }
        
        // Y axis
        {
            Range2D range = _chartGraphic.getRange();
            double tickSpacing;
            if ( range.getMaxY() < 2 ) {
                tickSpacing = 0.5;
            }
            else if ( range.getMaxY() < 5 ) {
                tickSpacing = 1.0;
            }
            else {
                tickSpacing = 5.0;
            }
            _chartGraphic.getVerticalTicks().setMajorTickSpacing( tickSpacing );
            _chartGraphic.getHorizonalGridlines().setMajorTickSpacing( tickSpacing );
        }
    }
    
    /*
     * Enables and disables zoom buttons based on the current
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
        
        // Vertical buttons
        if ( _autoScaleEnabled ) {
            _verticalZoomControl.setZoomOutEnabled( false );
            _verticalZoomControl.setZoomInEnabled( false );
        }
        else if ( range.getMaxY() >= Y_RANGE_MAX ) {
            _verticalZoomControl.setZoomOutEnabled( false );
            _verticalZoomControl.setZoomInEnabled( true );
        }
        else if ( range.getMaxY() <= Y_RANGE_MIN ) {
            _verticalZoomControl.setZoomOutEnabled( true );
            _verticalZoomControl.setZoomInEnabled( false );
        }
        else {
            _verticalZoomControl.setZoomOutEnabled( true );
            _verticalZoomControl.setZoomInEnabled( true );
        }
    }
    
    private void updateMath() {
        _mathGraphic.setForm( _domain, _mathForm, _fourierSeries.getNumberOfHarmonics() );
    }
    
    //----------------------------------------------------------------------------
    // Chart Labels
    //----------------------------------------------------------------------------
    
    /*
     * Lazy initialization of the X axis "space" labels.
     */
    private StringLabelTable getSpaceLabels1() {
        if ( _spaceLabels1 == null ) {
            _spaceLabels1 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _spaceLabels1.put( -1.00 * L, "-L" );
            _spaceLabels1.put( -0.75 * L, "-3L/4" );
            _spaceLabels1.put( -0.50 * L, "-L/2" );
            _spaceLabels1.put( -0.25 * L, "-L/4" );
            _spaceLabels1.put(     0 * L, "0" );
            _spaceLabels1.put( +0.25 * L, "L/4" );
            _spaceLabels1.put( +0.50 * L, "L/2" );
            _spaceLabels1.put( +0.75 * L, "3L/4" );
            _spaceLabels1.put( +1.00 * L, "L" );
        }
        return _spaceLabels1;
    }
    
    /*
     * Lazy initialization of the X axis "space" labels.
     */
    private StringLabelTable getSpaceLabels2() {
        if ( _spaceLabels2 == null ) {
            _spaceLabels2 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _spaceLabels2.put( -2.0 * L, "-2L" );
            _spaceLabels2.put( -1.5 * L, "-3L/2" );
            _spaceLabels2.put( -1.0 * L, "-L" );
            _spaceLabels2.put( -0.5 * L, "-L/2" );
            _spaceLabels2.put(    0 * L, "0" );
            _spaceLabels2.put( +0.5 * L, "L/2" );
            _spaceLabels2.put( +1.0 * L, "L" );
            _spaceLabels2.put( +1.5 * L, "3L/2" );
            _spaceLabels2.put( +2.0 * L, "2L" );
        }
        return _spaceLabels2;
    }
    
    /*
     * Lazy initialization of the X axis "time" labels.
     */
    private StringLabelTable getTimeLabels1() {
        if ( _timeLabels1 == null ) {
            double T = L; // use the same quantity for wavelength and period
            _timeLabels1 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _timeLabels1.put( -1.00 * T, "-T" );
            _timeLabels1.put( -0.75 * T, "-3T/4" );
            _timeLabels1.put( -0.50 * T, "-T/2" );
            _timeLabels1.put( -0.25 * T, "-T/4" );
            _timeLabels1.put(     0 * T, "0" );
            _timeLabels1.put( +0.25 * T, "T/4" );
            _timeLabels1.put( +0.50 * T, "T/2" );
            _timeLabels1.put( +0.75 * T, "3T/4" );
            _timeLabels1.put( +1.00 * T, "T" );
        }
        return _timeLabels1;
    }
    
    /*
     * Lazy initialization of the X axis "time" labels.
     */
    private StringLabelTable getTimeLabels2() {   
        if ( _timeLabels2 == null ) {
            double T = L; // use the same quantity for wavelength and period
            _timeLabels2 = new StringLabelTable( getComponent(), MAJOR_TICK_FONT, MAJOR_TICK_COLOR );
            _timeLabels2.put( -2.0 * T, "-2T" );
            _timeLabels2.put( -1.5 * T, "-3T/2" );
            _timeLabels2.put( -1.0 * T, "-T" );
            _timeLabels2.put( -0.5 * T, "-T/2" );
            _timeLabels2.put(    0 * T, "0" );
            _timeLabels2.put( +0.5 * T, "T/2" );
            _timeLabels2.put( +1.0 * T, "T" );
            _timeLabels2.put( +1.5 * T, "3T/2" );
            _timeLabels2.put( +2.0 * T, "2T" );
        }
        return _timeLabels2;
    }
    
    //----------------------------------------------------------------------------
    // ModelElement implementation
    //----------------------------------------------------------------------------
    
    /**
     * Moves the waveform in space by shifting its start location.
     * 
     * @param dt
     */
    public void stepInTime( double dt ) {
        if ( isVisible() && _domain == FourierConstants.DOMAIN_SPACE_AND_TIME && FourierConfig.ANIMATION_ENABLED ) {
            
            double dx = dt * L / FourierConfig.ANIMATION_STEPS_PER_CYCLE;
            
            // Shift the start location of the sum waveform.
            _sumPlot.setStartX( _sumPlot.getStartX() + dx );
            
            // Shift the preset.
            Point2D[] points = _presetPlot.getDataSet().getPoints();
            if ( points != null ) {
                _presetPlot.getDataSet().clear();
                for ( int i = 0; i < points.length; i++ ) {
                    points[i].setLocation( points[i].getX() + dx, points[i].getY() );
                }
                _presetPlot.getDataSet().addAllPoints( points );
            }
        }
    }
}
