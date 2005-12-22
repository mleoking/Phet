/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.ui.TextAnchor;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.quantumtunneling.QTConstants;
import edu.colorado.phet.quantumtunneling.enum.WaveType;
import edu.colorado.phet.quantumtunneling.model.*;
import edu.colorado.phet.quantumtunneling.module.QTModule;
import edu.colorado.phet.quantumtunneling.view.EnergyPlot;


/**
 * ConfigureEnergyDialog is the "Configure Energy" dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ConfigureEnergyDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final Dimension CHART_SIZE = new Dimension( 450, 150 );
    
    private static final String POSITION_FORMAT = "0.0";
    private static final String ENERGY_FORMAT = "0.0";
    private static final double POSITION_STEP = 0.1;
    private static final double ENERGY_STEP = 0.1;
    private static final double MIN_ENERGY = QTConstants.ENERGY_RANGE.getLowerBound();
    private static final double MAX_ENERGY = QTConstants.ENERGY_RANGE.getUpperBound();
    private static final double MIN_POSITION = QTConstants.POSITION_RANGE.getLowerBound();
    private static final double MAX_POSITION = QTConstants.POSITION_RANGE.getUpperBound();
    
    /*
     * All SpinnerNumberModels are given a range that is well outside the range
     * of valid values. We do this so that we can control the validation ourselves,
     * warn the user about invalid values, and reset the values accordingly.
     */
    private static final double SPINNER_MAX = Double.MAX_VALUE;
    
    private static final Font AXES_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Font ANNOTATION_FONT = new Font( QTConstants.FONT_NAME, Font.PLAIN, 12 );
    private static final Color BARRIER_PROPERTIES_COLOR = Color.RED;
    private static final Dimension SPINNER_SIZE = new Dimension( 65, 25 );
    
    /* How close the annotations are to the top and bottom of the chart */
    private static final double ANNOTATION_MARGIN = 0.25;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Model
    private TotalEnergy _totalEnergy;
    private AbstractPotential _potentialEnergy;
    
    // Chart area
    private EnergyPlot _energyPlot;
    
    // Input area
    private JPanel _inputPanel;
    private JComboBox _potentialComboBox;
    private Object _constantItem, _stepItem, _singleBarrierItem, _doubleBarrierItem; // potential choices
    private JSpinner _teSpinner;
    private ArrayList _peSpinners; // array of JSpinner
    private JSpinner _stepSpinner;
    private ArrayList _widthSpinners; // array of JSpinner
    private ArrayList _positionSpinners; // array of JSpinner
    
    // Action area
    private JButton _applyButton, _closeButton;
    
    // Misc
    private Frame _parent;
    private QTModule _module;
    private EventListener _listener;
    private boolean _peChanged, _teChanged;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param parent
     * @param totalEnergy
     * @param potentialEnergy
     * @param waveType
     */
    public ConfigureEnergyDialog( Frame parent, QTModule module, TotalEnergy totalEnergy, AbstractPotential potentialEnergy, WaveType waveType ) {
        super( parent );

        setTitle( SimStrings.get( "title.configureEnergy" ) );
        setModal( true );
        setResizable( false );

        _parent = parent;
        _module = module;
        _listener = new EventListener();
        
        // Make copies of the model
        _totalEnergy = new TotalEnergy( totalEnergy );
        _potentialEnergy = clonePotentialEnergy( potentialEnergy );

        createUI( parent, waveType );
        populateValues();
        
        setLocationRelativeTo( parent );
        
        // do this after creating the UI!
        {
            _teChanged = _peChanged = false;
            _applyButton.setEnabled( false ); // disabled until something is changed
        }
    }

    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        // Nothing to do
    }

    //----------------------------------------------------------------------------
    // Private initializers
    //----------------------------------------------------------------------------

    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     * @param waveType the wave type
     */
    private void createUI( Frame parent, WaveType waveType ) {
        
        JPanel chartPanel = createChartPanel( waveType );
        _inputPanel = new JPanel();
        _inputPanel.add( createInputPanel() );
        JPanel actionsPanel = createActionsPanel();

        JPanel p1 = new JPanel( new BorderLayout() );
        p1.add( chartPanel, BorderLayout.NORTH );
        p1.add( new JSeparator(), BorderLayout.CENTER );
        
        JPanel p2 = new JPanel( new BorderLayout() );
        p2.add( p1, BorderLayout.NORTH );
        p2.add( _inputPanel, BorderLayout.SOUTH );
        
        JPanel p3 = new JPanel( new BorderLayout() );
        p3.add( p2, BorderLayout.NORTH );
        p3.add( new JSeparator(), BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( p3, BorderLayout.NORTH );
        mainPanel.add( actionsPanel, BorderLayout.SOUTH );

        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        chartPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );
        _inputPanel.setBorder( new EmptyBorder( 0, 0, 5, 0 ) );

        getContentPane().add( mainPanel );
        pack();
    }

    /*
     * Creates the dialog's chart panel.
     * 
     * @param waveType
     * @return the chart panel
     */
    private JPanel createChartPanel( WaveType waveType ) {

        // Plot
        _energyPlot = new EnergyPlot();
        _energyPlot.setAxesFont( AXES_FONT );
        _energyPlot.setWaveType( waveType );
        
        // Chart
        JFreeChart chart = new JFreeChart( null /*title*/, null /*font*/, _energyPlot, false /* createLegend */);
        
        // Chart panel
        ChartPanel chartPanel = new ChartPanel( chart );
        chartPanel.setPopupMenu( null ); // disable popup menu, on by default
        chartPanel.setMouseZoomable( false ); // disable zooming, on by default
        chartPanel.setMinimumDrawWidth( (int) CHART_SIZE.getWidth() - 1 );
        chartPanel.setMinimumDrawHeight( (int) CHART_SIZE.getHeight() - 1 );
        chartPanel.setPreferredSize( CHART_SIZE );

        return chartPanel;
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        // Menu panel...
        JPanel menuPanel = new JPanel();
        {
            // Potential choices...
            JLabel potentialLabel = new JLabel( SimStrings.get( "label.potential" ) );
            _constantItem = SimStrings.get( "choice.potential.constant" );
            _stepItem = SimStrings.get( "choice.potential.step" );
            _singleBarrierItem = SimStrings.get( "choice.potential.barrier" );
            _doubleBarrierItem = SimStrings.get( "choice.potential.double" );
            
            // Potential menu...
            Object[] items = { _constantItem, _stepItem, _singleBarrierItem, _doubleBarrierItem };
            _potentialComboBox = new JComboBox( items );
            _potentialComboBox.addItemListener( _listener );

            // Layout
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.addAnchoredComponent( potentialLabel, 0, 1, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _potentialComboBox, 0, 2, GridBagConstraints.WEST );
            menuPanel.setLayout( new BorderLayout() );
            menuPanel.add( innerPanel, BorderLayout.WEST );
        }

        // Spinner panel...
        JPanel spinnerPanel = new JPanel();
        {
            EasyGridBagLayout inputPanelLayout = new EasyGridBagLayout( spinnerPanel );
            inputPanelLayout.setMinimumWidth( 0, 25 );
            inputPanelLayout.setMinimumWidth( 4, 60 );
            inputPanelLayout.setMinimumWidth( 5, 25 );

            spinnerPanel.setLayout( inputPanelLayout );
            int row = 0;

            // Total Energy
            {
                JLabel teLabel = new JLabel( SimStrings.get( "label.totalEnergy" ) );
                teLabel.setForeground( new Color( 16, 159, 33 ) ); // dark green
                _teSpinner = new CommonSpinner( 0, -SPINNER_MAX, SPINNER_MAX, ENERGY_STEP, ENERGY_FORMAT );
                _teSpinner.addChangeListener( _listener );
                JLabel teUnits = new JLabel( SimStrings.get( "units.energy" ) );
                inputPanelLayout.addAnchoredComponent( teLabel, row, 0, 2, 1, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( _teSpinner, row, 2 );
                inputPanelLayout.addComponent( teUnits, row, 3 );
                row++;
            }

            // Potential Energy for each region...
            {
                JLabel peTitle = new JLabel( SimStrings.get( "label.potentialEnergy" ) );
                peTitle.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                inputPanelLayout.addAnchoredComponent( peTitle, row, 0, 4, 1, GridBagConstraints.WEST );
                row++;
                int numberOfRegions = _potentialEnergy.getNumberOfRegions();
                _peSpinners = new ArrayList();
                for ( int i = 0; i < numberOfRegions; i++ ) {
                    JLabel peLabel = new JLabel( "R" + ( i + 1 ) + ":" );
                    peLabel.setForeground( QTConstants.POTENTIAL_ENERGY_COLOR );
                    JSpinner peSpinner = new CommonSpinner( 0, -SPINNER_MAX, SPINNER_MAX, ENERGY_STEP, ENERGY_FORMAT );
                    peSpinner.addChangeListener( _listener );
                    _peSpinners.add( peSpinner );
                    JLabel peUnits = new JLabel( SimStrings.get( "units.energy" ) );
                    inputPanelLayout.addAnchoredComponent( peLabel, row, 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( peSpinner, row, 2, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( peUnits, row, 3, GridBagConstraints.WEST );
                    row++;
                }
            }

            // Step...
            _stepSpinner = null;
            if ( _potentialEnergy instanceof StepPotential ) {
                JLabel stepLabel = new JLabel( SimStrings.get( "label.stepPosition" ) );
                stepLabel.setForeground( Color.BLACK );
                _stepSpinner = new CommonSpinner( 0, -SPINNER_MAX, SPINNER_MAX, POSITION_STEP, POSITION_FORMAT );
                _stepSpinner.addChangeListener( _listener );
                JLabel stepUnits = new JLabel( SimStrings.get( "units.position" ) );
                inputPanelLayout.addAnchoredComponent( stepLabel, row, 0, 2, 1, GridBagConstraints.EAST );
                inputPanelLayout.addComponent( _stepSpinner, row, 2 );
                inputPanelLayout.addComponent( stepUnits, row, 3 );
                row++;
            }
            
            // Barriers...
            _widthSpinners = null;
            _positionSpinners = null;
            if ( _potentialEnergy instanceof BarrierPotential ) {

                row = 1;
                int column = 5;

                int numberOfBarriers = ( (BarrierPotential) _potentialEnergy ).getNumberOfBarriers();

                // Barrier Positions...
                _positionSpinners = new ArrayList();
                JLabel positionTitle = new JLabel( SimStrings.get( "label.barrierPosition" ) );
                positionTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( positionTitle, row, column, 4, 1, GridBagConstraints.WEST );
                row++;
                column++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    JLabel positionLabel = new JLabel( "B" + ( i + 1 ) + ":" );
                    positionLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                    JSpinner positionSpinner = new CommonSpinner( 0, -SPINNER_MAX, SPINNER_MAX, POSITION_STEP, POSITION_FORMAT );
                    positionSpinner.addChangeListener( _listener );
                    _positionSpinners.add( positionSpinner );
                    JLabel positionUnits = new JLabel( SimStrings.get( "units.position" ) );
                    inputPanelLayout.addAnchoredComponent( positionLabel, row, column, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( positionSpinner, row, column + 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( positionUnits, row, column + 2, GridBagConstraints.WEST );
                    row++;
                }
                column--;
                
                // Barrier Widths...
                _widthSpinners = new ArrayList();
                JLabel widthTitle = new JLabel( SimStrings.get( "label.barrierWidth" ) );
                widthTitle.setForeground( BARRIER_PROPERTIES_COLOR );
                inputPanelLayout.addAnchoredComponent( widthTitle, row, column, 4, 1, GridBagConstraints.WEST );
                row++;
                column++;
                for ( int i = 0; i < numberOfBarriers; i++ ) {
                    JLabel widthLabel = new JLabel( "B" + ( i + 1 ) + ":" );
                    widthLabel.setForeground( BARRIER_PROPERTIES_COLOR );
                    JSpinner widthSpinner = new CommonSpinner( 0, -SPINNER_MAX, SPINNER_MAX, POSITION_STEP, POSITION_FORMAT );
                    widthSpinner.addChangeListener( _listener );
                    _widthSpinners.add( widthSpinner );
                    JLabel widthUnits = new JLabel( SimStrings.get( "units.position" ) );
                    inputPanelLayout.addAnchoredComponent( widthLabel, row, column, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( widthSpinner, row, column + 1, GridBagConstraints.EAST );
                    inputPanelLayout.addAnchoredComponent( widthUnits, row, column + 2, GridBagConstraints.WEST );
                    row++;
                }
            }
        }

        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.add( menuPanel, BorderLayout.NORTH );
        inputPanel.add( spinnerPanel, BorderLayout.CENTER );
        
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of Apply and Close buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _applyButton = new JButton( SimStrings.get( "button.apply" ) );
        _applyButton.addActionListener( _listener );

        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _listener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 2, 10, 0 ) );
        buttonPanel.add( _applyButton );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    /*
     * Populates the user interface with values from the model.
     */
    private void populateValues() {
        
        double minX = _energyPlot.getDomainAxis().getRange().getLowerBound();
        double maxX = _energyPlot.getDomainAxis().getRange().getUpperBound();
        
        // Energy plot
        _energyPlot.setTotalEnergy( _totalEnergy );
        _energyPlot.setPotentialEnergy( _potentialEnergy );
        
        // Potential type
        _potentialComboBox.removeItemListener( _listener );
        if ( _potentialEnergy instanceof ConstantPotential ) {
            _potentialComboBox.setSelectedItem( _constantItem );
        }
        else if ( _potentialEnergy instanceof StepPotential ) {
            _potentialComboBox.setSelectedItem( _stepItem );
        }
        else if ( _potentialEnergy instanceof SingleBarrierPotential ) {
            _potentialComboBox.setSelectedItem( _singleBarrierItem );
        }
        else if ( _potentialEnergy instanceof DoubleBarrierPotential ) {
            _potentialComboBox.setSelectedItem( _doubleBarrierItem );
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + _potentialEnergy.getClass().getName() );
        }
        _potentialComboBox.addItemListener( _listener );
        
        // Total Energy
        double te = _totalEnergy.getEnergy();
        _teSpinner.setValue( new Double( te ) );
        
        // Potential Energy per region
        for ( int i = 0; i < _peSpinners.size(); i++ ) {
            double pe = _potentialEnergy.getEnergy( i );
            JSpinner peSpinner = (JSpinner) _peSpinners.get( i );
            peSpinner.setValue( new Double( pe ) );
        }
        
        // Step 
        if ( _stepSpinner != null ) {
            double position = _potentialEnergy.getStart( 1 );
            _stepSpinner.setValue( new Double( position ) );
        }
        
        // Barrier Width
        if ( _widthSpinners != null ) {
            for ( int i = 0; i < _widthSpinners.size(); i++ ) {
                JSpinner widthSpinner = (JSpinner) _widthSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double width = ( (BarrierPotential) _potentialEnergy ).getWidth( regionIndex );
                widthSpinner.setValue( new Double( width ) );
            }
        }
        
        // Barrier Positions
        if ( _positionSpinners != null ) {
            for ( int i = 0; i < _positionSpinners.size(); i++ ) {
                JSpinner positionSpinner = (JSpinner) _positionSpinners.get( i );
                int regionIndex = BarrierPotential.toRegionIndex( i );
                double position = ( (BarrierPotential) _potentialEnergy ).getStart( regionIndex );
                positionSpinner.setValue( new Double( position ) );
            }
        }
    }
    
    /*
     * Rebuilds the input panel when the type of potential changes.
     */
    private void rebuildInputPanel() {
        boolean visible = isVisible();
        if ( visible ) {
            setVisible( false );
        }
        _inputPanel.removeAll();
        _inputPanel.add( createInputPanel() );
        populateValues();
        pack();
        if ( visible ) {
            setVisible( true );
        }
    }
    
    //----------------------------------------------------------------------------
    // Markers & Annotations
    //----------------------------------------------------------------------------
    
    /*
     * Updates the region/barrier markers and annotations to match the the model.
     */
    private void updateMarkersAndAnnotations() {

        boolean hasBarriers = ( _potentialEnergy instanceof BarrierPotential );
        
        double minY = _energyPlot.getRangeAxis().getRange().getLowerBound();
        double maxY = _energyPlot.getRangeAxis().getRange().getUpperBound();
        
        XYItemRenderer renderer = _energyPlot.getRenderer();
        renderer.removeAnnotations();
        _energyPlot.clearDomainMarkers();
        
        int numberOfRegions = _potentialEnergy.getNumberOfRegions();
        for ( int i = 0; i < numberOfRegions; i++ ) {
            
            // Marker
            if ( i != 0 ) {
                double x = _potentialEnergy.getStart( i );
                Marker marker = new ValueMarker( x );
                marker.setPaint( QTConstants.REGION_MARKER_COLOR );
                marker.setStroke( QTConstants.REGION_MARKER_STROKE );
                _energyPlot.addDomainMarker( marker );
            }
            
            // Annotation
            {
                // Region annotation
                String text = "R" + ( i + 1 );
                double x = _potentialEnergy.getMiddle( i );
                double y = maxY - ANNOTATION_MARGIN;
                XYTextAnnotation annotation = new XYTextAnnotation( text, x, y );
                annotation.setFont( ANNOTATION_FONT );
                annotation.setPaint( QTConstants.POTENTIAL_ENERGY_COLOR );
                annotation.setTextAnchor( TextAnchor.TOP_CENTER );
                renderer.addAnnotation( annotation );
                
                // Barrier annotation
                if ( hasBarriers && BarrierPotential.isaBarrier( i ) ) {
                    int barrierIndex = BarrierPotential.toBarrierIndex( i );
                    text = "B" + ( barrierIndex + 1 );
                    y = minY + ANNOTATION_MARGIN;
                    annotation = new XYTextAnnotation( text, x, y );
                    annotation.setFont( ANNOTATION_FONT );
                    annotation.setPaint( BARRIER_PROPERTIES_COLOR );
                    annotation.setTextAnchor( TextAnchor.BOTTOM_CENTER );
                    renderer.addAnnotation( annotation );
                }
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Dispatches events to the appropriate handler method.
     */
    private class EventListener implements ActionListener, ChangeListener, ItemListener {

        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _applyButton ) {
                handleApply();
            }
            else if ( event.getSource() == _closeButton ) {
                handleClose();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _teSpinner ) {
                handleTotalEnergyChange();
            }
            else if ( _peSpinners.contains( event.getSource() ) ) { /* inefficient! */
                handlePotentialEnergyChange( _peSpinners.indexOf( event.getSource() ) );
            }
            else if ( event.getSource() == _stepSpinner ) {
                handleStepPositionChange();
            }
            else if ( _widthSpinners.contains( event.getSource() ) ) { /* inefficient! */
                handleBarrierWidthChange( _widthSpinners.indexOf( event.getSource() ) );
            }
            else if ( _positionSpinners.contains( event.getSource() ) ) { /* inefficient! */
                handleBarrierPositionChange( _positionSpinners.indexOf( event.getSource() ) );
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void itemStateChanged( ItemEvent event ) {
            if ( event.getStateChange() == ItemEvent.SELECTED ) {
                if ( event.getSource() == _potentialComboBox ) {
                    handlePotentialTypeChange();
                }
                else {
                    throw new IllegalArgumentException( "unexpected event: " + event );
                }
            }
        }
    }

    /*
     * Handles the "Apply" button.
     */
    private void handleApply() {
        if ( _teChanged ) {
            _module.setTotalEnergy( new TotalEnergy( _totalEnergy ) );
            _teChanged = false;
        }
        if ( _peChanged ) {
            _module.setPotentialEnergy( clonePotentialEnergy( _potentialEnergy ) ); 
            _peChanged = false;
        }
        _applyButton.setEnabled( false );
    }

    /*
     * Handles the "Close" button, checks for unsaved changes.
     */
    private void handleClose() {
        if ( _teChanged || _peChanged ) {
            String message = SimStrings.get( "message.unsavedChanges" );
            String title = SimStrings.get( "title.confirm" );
            int reply = JOptionPane.showConfirmDialog( this, message, "Confirm", JOptionPane.YES_NO_CANCEL_OPTION );
            if ( reply == JOptionPane.YES_OPTION) {
                handleApply();
                dispose();
            }
            if ( reply == JOptionPane.NO_OPTION) {
                dispose();
            }
            else {
                // Do nothing if canceled.
            }
        }
        else {
            dispose();
        }
    }
    
    /*
     * Handles selection in the "Potential" combo box.
     */
    private void handlePotentialTypeChange() {
        AbstractPotential potentialEnergy = null;
        
        Object o = _potentialComboBox.getSelectedItem();
        if ( o == _constantItem ) {
            potentialEnergy = new ConstantPotential();
        }
        else if ( o == _stepItem ) {
            potentialEnergy = new StepPotential();
        }
        else if ( o == _singleBarrierItem ) {
            potentialEnergy = new SingleBarrierPotential();
        }
        else if ( o == _doubleBarrierItem ) {
            potentialEnergy = new DoubleBarrierPotential();
        }
        else {
            throw new IllegalStateException( "unsupported potential selection: " + o );
        }
        
        if ( potentialEnergy != _potentialEnergy ) {
            _potentialEnergy = potentialEnergy;
            _peChanged = true;
            _applyButton.setEnabled( true );
            rebuildInputPanel();
        }
    }
    
    /*
     * Handles a change in total energy.
     */
    private void handleTotalEnergyChange() {
        Double value = (Double) _teSpinner.getValue();
        double energy = value.doubleValue();
        if ( energy >= MIN_ENERGY && energy <= MAX_ENERGY ) {
            _totalEnergy.setEnergy( energy );
            _teChanged = true;
            _applyButton.setEnabled( true );
        }
        else {
            warnInvalidInput();
            energy = _totalEnergy.getEnergy();
            _teSpinner.setValue( new Double( energy ) );
        }
    }
    
    /*
     * Handles a change in the potential energy of a region.
     */
    private void handlePotentialEnergyChange( int regionIndex ) {
        JSpinner peSpinner = (JSpinner) _peSpinners.get( regionIndex );
        Double value = (Double) peSpinner.getValue();
        double energy = value.doubleValue();
        if ( energy >= MIN_ENERGY && energy <= MAX_ENERGY ) {
            _potentialEnergy.setEnergy( regionIndex, value.doubleValue() );
            updateMarkersAndAnnotations();
            _peChanged = true;
            _applyButton.setEnabled( true );
        }
        else {
            warnInvalidInput();
            energy = _potentialEnergy.getEnergy( regionIndex );
            peSpinner.setValue( new Double( energy ) );
        }
    }
    
    /*
     * Handles a change in the position of a step.
     */
    private void handleStepPositionChange() {
        if ( _potentialEnergy instanceof StepPotential ) {
            StepPotential step = (StepPotential) _potentialEnergy;
            Double value = (Double) _stepSpinner.getValue();
            double position = value.doubleValue();
            boolean success = step.setStepPosition( position );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
                _applyButton.setEnabled( true );
            }
            else {
                warnInvalidInput();
                position = step.getStepPosition();
                _stepSpinner.setValue( new Double( position ) );
            }
        }
    }
    
    /*
     * Handles a change in the width of a barrier.
     */
    private void handleBarrierWidthChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            BarrierPotential barrier = (BarrierPotential) _potentialEnergy;
            JSpinner widthSpinner = (JSpinner) _widthSpinners.get( barrierIndex );
            Double value = (Double) widthSpinner.getValue();
            double width = value.doubleValue();
            boolean success = barrier.setBarrierWidth( barrierIndex, width );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
                _applyButton.setEnabled( true );
            }
            else {
                warnInvalidInput();
                width = barrier.getBarrierWidth( barrierIndex );
                widthSpinner.setValue( new Double( width ) );
            }
        }
    }
    
    /*
     * Handles a change in the position of a barrier.
     */
    private void handleBarrierPositionChange( int barrierIndex ) {
        if ( _potentialEnergy instanceof BarrierPotential ) {
            BarrierPotential bp = (BarrierPotential) _potentialEnergy;
            JSpinner positionSpinner = (JSpinner) _positionSpinners.get( barrierIndex );
            Double value = (Double) positionSpinner.getValue();
            double position = value.doubleValue();
            boolean success = bp.setBarrierPosition( barrierIndex, position );
            if ( success ) {
                updateMarkersAndAnnotations();
                _peChanged = true;
                _applyButton.setEnabled( true );
            }
            else {
                warnInvalidInput();
                position = bp.getBarrierPosition( barrierIndex );
                positionSpinner.setValue( new Double( position ) );
            }
        }
    }
    
    /*
     * Clones a potential energy object. 
     */
    private AbstractPotential clonePotentialEnergy( AbstractPotential pe ) {
        AbstractPotential peNew = null;
        if ( pe instanceof ConstantPotential ) {
            peNew = new ConstantPotential( (ConstantPotential) pe );
        }
        else if ( pe instanceof StepPotential ) {
            peNew = new StepPotential( (StepPotential) pe );
        }
        else if ( pe instanceof SingleBarrierPotential ) {
            peNew = new SingleBarrierPotential( (SingleBarrierPotential) pe );
        }
        else if ( pe instanceof BarrierPotential ) {
            peNew = new DoubleBarrierPotential( (DoubleBarrierPotential) pe );
        }
        else {
            throw new IllegalStateException( "unsupported potential type: " + pe.getClass().getName() );
        }
        return peNew;
    }
    
    /*
     * Warns the user about invalid input.
     */
    private void warnInvalidInput() {
        Toolkit.getDefaultToolkit().beep();
    }
    
    /*
     * Common spinner used in this dialog.
     */
    private static class CommonSpinner extends JSpinner {
        public CommonSpinner( double value, double min, double max, double step, String format ) {
            super( );
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            setModel( model );
            setEditor( new JSpinner.NumberEditor( this, format ) );
            setPreferredSize( SPINNER_SIZE );
            setMinimumSize( SPINNER_SIZE );
        }
    }
}
