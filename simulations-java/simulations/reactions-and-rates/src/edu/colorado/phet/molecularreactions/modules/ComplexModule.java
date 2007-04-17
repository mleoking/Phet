/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.modules;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.clock.StopwatchPanel;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.model.AbstractMolecule;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.view.PumpGraphic;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsBarChartNode;
import edu.colorado.phet.molecularreactions.view.charts.MoleculePopulationsPieChartNode;
import edu.colorado.phet.molecularreactions.view.charts.StripChartNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

import java.awt.geom.Rectangle2D;

/**
 * ComplexModule
 * <p/>
 * This module has controls for putting lots of molecules in the box and
 * charting their properties is different ways.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComplexModule extends MRModule {
    //private static final double DEFAULT_TEMPERATURE = 500.0;
    
    private MRControlPanel controlPanel;
    private PumpGraphic pumpGraphic;
    private MoleculePopulationsBarChartNode barChartNode;
    private MoleculePopulationsPieChartNode pieChart;
    private boolean firstTimeStripChartVisible = true;
    private PSwing stopwatchAdapter;

    private final StripChartNode stripChartNode;


    /**
     *
     */
    public ComplexModule() {
        this( MRConfig.RESOURCES.getLocalizedString( "Module.complexModuleTitle" ) );
    }

    /**
     *
     * @param title
     */
    protected ComplexModule( String title ) {
        super( title, MRConfig.CHART_PANE_SIZE );

        addMovableStopwatch(29.0, 453.0 );

        getEnergyView().setEnergyLineLabel( "EnergyView.Legend.totalAverageEnergy" );

        // Create the strip chart
        stripChartNode = new StripChartNode( this, MRConfig.CHART_PANE_SIZE );

        // Add the pump
        MRModel model = getMRModel();

        // Average total energy
        model.setAverageTotal( true );

        model.setDefaultTemperature( MRConfig.DEFAULT_TEMPERATURE );

        pumpGraphic = new PumpGraphic( this );
        // 15 is the wall thickness of the box graphic
        pumpGraphic.setOffset( model.getBox().getMinX() + model.getBox().getWidth(),
                               model.getBox().getMinY() + model.getBox().getHeight() + 15 - pumpGraphic.getPumpBaseLocation().getY() );
        getSpatialView().addChild( pumpGraphic );

        // Create the control panel
        controlPanel = createControlPanel();
        getControlPanel().addControl( controlPanel );

        // Create an agent that will start the strip chart when the first molecule is added to the model
        getMRModel().addListener( new PublishingModel.ModelListenerAdapter(){

            public void modelElementAdded( ModelElement element ) {
                if( element instanceof AbstractMolecule ) {
                    setStripChartRecording( true );
                }
            }
        });

        getEnergyView().clearUpperPaneContent();
    }

    public boolean isTemperatureBeingAdjusted() {
        boolean adjusting = super.isTemperatureBeingAdjusted();

        if (!adjusting && controlPanel != null) {            
            adjusting = controlPanel.isTemperatureBeingAdjusted();
        }

        //if (!(this instanceof RateExperimentsModule)) {
        //    System.out.println("Is adjusting = " + adjusting);
        //}

        return adjusting;
    }

    protected MRControlPanel createControlPanel() {
        return new ComplexMRControlPanel( this );
    }

    public void activate() {
        super.activate();
    }

    public void reset() {
        super.reset();
        setInitialConditions();
        controlPanel.reset();
        pumpGraphic.reset();
        resetStripChart();
    }

    private void setInitialConditions() {
        setBarChartVisible( false );
        setPieChartVisible( false );
    }

    public void setPieChartVisible( boolean visible ) {
        if( visible ) {
            Rectangle2D.Double bounds = new Rectangle2D.Double( 0, 0,
                                                                getEnergyView().getUpperPaneSize().getWidth(),
                                                                getEnergyView().getUpperPaneSize().getHeight() );
            pieChart = new MoleculePopulationsPieChartNode( this, bounds );
            getEnergyView().setUpperPaneContent( pieChart );
        }
        else {
            getEnergyView().clearUpperPaneContent( pieChart );
        }
    }

    public void setBarChartVisible( boolean visible ) {
        if( visible ) {
            barChartNode = new MoleculePopulationsBarChartNode( this,
                                                                getEnergyView().getUpperPaneSize(),
                                                                (PhetPCanvas)getSimulationPanel() );
            barChartNode.rescale();
            getEnergyView().setUpperPaneContent( barChartNode );
        }
        else {
            getEnergyView().clearUpperPaneContent( barChartNode );
        }
    }

    /**
     * Set visibility of the strip chart
     *
     * @param visible
     */
    public void setStripChartVisible( boolean visible ) {
        if( visible ) {
            if( firstTimeStripChartVisible ) {
                stripChartNode.rescale();
                firstTimeStripChartVisible = false;
            }
            getEnergyView().setUpperPaneContent( stripChartNode );
        }
        else {
            getEnergyView().clearUpperPaneContent( stripChartNode );
        }
    }

    /**
     * Sets the flag that determines whether the strip chart will be rescaled the next time
     * it's made visible.
     *
     * @param firstTimeStripChartVisible
     */
    protected void setFirstTimeStripChartVisible( boolean firstTimeStripChartVisible ) {
        this.firstTimeStripChartVisible = firstTimeStripChartVisible;
    }

    public void resetStripChart() {
        stripChartNode.reset();
    }

    public void setStripChartRecording( boolean recording ) {
            stripChartNode.setRecording( recording);
    }

    public void setStopwatchVisible( boolean visible ) {
        stopwatchAdapter.setVisible( visible );
    }

    protected void addMovableStopwatch( double x, double y ) {
        StopwatchPanel stopwatchPanel = new StopwatchPanel(getClock());

        stopwatchAdapter = new PSwing(stopwatchPanel);

        stopwatchAdapter.setOffset(x, y);

        stopwatchAdapter.addInputEventListener( new PDragEventHandler() );

        getCanvas().addScreenChild(stopwatchAdapter);
    }
}

