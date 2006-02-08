/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.util.Observable;
import java.util.Observer;

import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.model.AbstractPotential;
import edu.colorado.phet.quantumtunneling.model.PlaneWave;
import edu.colorado.phet.quantumtunneling.model.TotalEnergy;
import edu.colorado.phet.quantumtunneling.model.WavePacket;


/**
 * EnergyPlot is the plot that displays total and potential energy.
 * The total energy display is different depending on whether the
 * wave is a plane wave or wave packet.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class EnergyPlot extends XYPlot implements Observer {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    // Model references
    private AbstractPotential _potentialEnergy;
    private TotalEnergy  _totalEnergy;
    private WavePacket _wavePacket;
    private PlaneWave _planeWave;
    
    // View
    private XYSeries _totalEnergySeries;
    private XYSeries _potentialEnergySeries;
    private StandardXYItemRenderer _planeWaveRenderer; // total energy renderer for plane wave
    private TotalEnergyRenderer _wavePacketRenderer; // total energy renderer for wave packet
    private int _totalEnergyIndex; // total energy dataset index
    private int _potentialEnergyIndex; // potential energy dataset index
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public EnergyPlot() {
        super();
        
        // Labels (localized)
        String energyLabel = SimStrings.get( "axis.energy" ) + " (" + SimStrings.get( "units.energy" ) + ")";
        String potentialEnergyLabel = SimStrings.get( "legend.potentialEnergy" );
        String totalEnergyLabel = SimStrings.get( "legend.totalEnergy" );
        
        int dataSetIndex = 0;
        
        // Potential Energy series
        _potentialEnergySeries = new XYSeries( potentialEnergyLabel, false /* autoSort */ );
        {
            _potentialEnergyIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _potentialEnergySeries );
            setDataset( _potentialEnergyIndex, dataset );
            // Renderer
            XYItemRenderer renderer = new StandardXYItemRenderer();
            renderer.setPaint( QTConstants.POTENTIAL_ENERGY_COLOR );
            renderer.setStroke( QTConstants.POTENTIAL_ENERGY_STROKE );
            setRenderer( _potentialEnergyIndex, renderer );
        }
        
        // Total Energy series -- switch renderers based on wave type
        _totalEnergySeries = new XYSeries( totalEnergyLabel, false /* autoSort */);
        {
            _totalEnergyIndex = dataSetIndex++;
            // Dataset
            XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries( _totalEnergySeries );
            setDataset( _totalEnergyIndex, dataset );
            // Plane Wave renderer
            _planeWaveRenderer = new StandardXYItemRenderer();
            _planeWaveRenderer.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
            _planeWaveRenderer.setStroke( QTConstants.TOTAL_ENERGY_STROKE );
            // Wave Packet renderer
            _wavePacketRenderer = new TotalEnergyRenderer();
            _wavePacketRenderer.setPaint( QTConstants.TOTAL_ENERGY_COLOR );
            _wavePacketRenderer.setStroke( QTConstants.TOTAL_ENERGY_STROKE );
            // Default renderer
            setRenderer( _totalEnergyIndex, _wavePacketRenderer );
        }
        
        // X axis 
        PositionAxis xAxis = new PositionAxis();
        
        // Y axis
        NumberAxis yAxis = new NumberAxis( energyLabel );
        yAxis.setLabelFont( QTConstants.AXIS_LABEL_FONT );
        yAxis.setRange( QTConstants.ENERGY_RANGE );
        yAxis.setTickLabelPaint( QTConstants.TICK_LABEL_COLOR );
        yAxis.setTickMarkPaint( QTConstants.TICK_MARK_COLOR );

        setRangeAxisLocation( AxisLocation.BOTTOM_OR_LEFT );
        setBackgroundPaint( QTConstants.PLOT_BACKGROUND );
        setDomainGridlinesVisible( QTConstants.SHOW_VERTICAL_GRIDLINES );
        setRangeGridlinesVisible( QTConstants.SHOW_HORIZONTAL_GRIDLINES );
        setDomainAxis( xAxis );
        setRangeAxis( yAxis ); 
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Sets the total energy model that is displayed.
     * 
     * @param totalEnergy
     */
    public void setTotalEnergy( TotalEnergy totalEnergy ) {
        if ( _totalEnergy != null ) {
            _totalEnergy.deleteObserver( this );
        }
        _totalEnergy = totalEnergy;
        _totalEnergy.addObserver( this );
        updateTotalEnergy();
    }
    
    /**
     * Sets the potential energy model that is displayed.
     * 
     * @param potentialEnergy
     */
    public void setPotentialEnergy( AbstractPotential potentialEnergy ) {
        if ( _potentialEnergy != null ) {
            _potentialEnergy.deleteObserver( this );
        }
        _potentialEnergy = potentialEnergy;
        _potentialEnergy.addObserver( this );
        _wavePacketRenderer.setPotentialEnergy( potentialEnergy );
        updatePotentialEnergy();
    }
    
    /**
     * Sets the wave packet.
     * 
     * @param wavePacket
     */
    public void setWavePacket( WavePacket wavePacket ) {
        if ( _wavePacket != null ) {
            _wavePacket.deleteObserver( this );
        }
        _wavePacket = wavePacket;
        _wavePacket.addObserver( this );
        _wavePacketRenderer.setWavePacket( wavePacket );
        updateTotalEnergy();
    }
    
    /**
     * Sets the plane wave.
     * 
     * @param planeWave
     */
    public void setPlaneWave( PlaneWave planeWave ) {
        if ( _planeWave != null ) {
            _planeWave.deleteObserver( this );
        }
        _planeWave = planeWave;
        _planeWave.addObserver( this );
        updateTotalEnergy();
    }
    
    /**
     * Displays the total energy based on the wave packet.
     */
    public void showWavePacket() {
        useWavePacketRenderer( true );
    }
    
    /**
     * Displays the total energy based on the plane wave.
     */
    public void showPlaneWave() {
        useWavePacketRenderer( false );
    }
    
    /*
     * Installs one of the two total energy renderers.
     * 
     * @param visible
     */
    private void useWavePacketRenderer( boolean b ) {
        if ( b ) {
            setRenderer( _totalEnergyIndex, _wavePacketRenderer );
        }
        else {
            setRenderer( _totalEnergyIndex, _planeWaveRenderer );
        }
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the view to match the model.
     * 
     * @param observable
     * @param arg
     */
    public void update( Observable observable, Object arg ) {
        if ( observable == _potentialEnergy ) {
            updatePotentialEnergy();
            updateTotalEnergy();
        }
        else if ( observable == _totalEnergy ) {
            updateTotalEnergy();
        }
        else if ( observable == _wavePacket ) {
            updateTotalEnergy();
        }
        else if ( observable == _planeWave ) {
            updateTotalEnergy();
        }
    }
    
    //----------------------------------------------------------------------------
    // Update handlers
    //----------------------------------------------------------------------------

    /**
     * Updates everything.
     */
    public void update() {
        updateTotalEnergy();
        updatePotentialEnergy();
    }
    
    /*
     * Updates the total energy series to match the model.
     */
    private void updateTotalEnergy() {
        if ( _totalEnergy != null ) {
            Range range = getDomainAxis().getRange();
            _totalEnergySeries.setNotify( false );
            _totalEnergySeries.clear();
            _totalEnergySeries.add( range.getLowerBound(), _totalEnergy.getEnergy() );
            _totalEnergySeries.add( range.getUpperBound(), _totalEnergy.getEnergy() );
            _totalEnergySeries.setNotify( true );
        }
    }
    
    /*
     * Updates the potential energy series to match the model.
     */
    private void updatePotentialEnergy() {
        if ( _potentialEnergy != null ) {
            _potentialEnergySeries.setNotify( false );
            _potentialEnergySeries.clear();
            final int numberOfRegions = _potentialEnergy.getNumberOfRegions();
            for ( int i = 0; i < numberOfRegions; i++ ) {
                final double start = _potentialEnergy.getStart( i );
                final double end = _potentialEnergy.getEnd( i );
                final double energy = _potentialEnergy.getEnergy( i );
                _potentialEnergySeries.add( start, energy );
                _potentialEnergySeries.add( end, energy );
            }
            _potentialEnergySeries.setNotify( true );
        }
    }
}
