// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.multiplecapacitors;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.DielectricChargeView;
import edu.colorado.phet.capacitorlab.model.circuit.*;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorView;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterView;
import edu.colorado.phet.capacitorlab.view.multicaps.*;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Multiple Capacitors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MultipleCapacitorsCanvas extends CLCanvas {

    // global view properties, directly observable
    public final Property<Boolean> plateChargesVisibleProperty = new Property<Boolean>( CLConstants.PLATE_CHARGES_VISIBLE );
    public final Property<Boolean> eFieldVisibleProperty = new Property<Boolean>( CLConstants.EFIELD_VISIBLE );
    public final Property<DielectricChargeView> dielectricChargeViewProperty = new Property<DielectricChargeView>( CLConstants.DIELECTRIC_CHARGE_VIEW );

    private final MultipleCapacitorsModel model;
    private final CLModelViewTransform3D mvt;

    private final PNode circuitParentNode; // parent of all circuit nodes, so we don't have to mess with rendering order

    // maximums
    private final double maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField, eFieldVectorReferenceMagnitude;

    // meters
    private final CapacitanceMeterNode capacitanceMeterNode;
    private final PlateChargeMeterNode plateChargeMeterNode;
    private final StoredEnergyMeterNode storedEnergyMeterNode;
    private final VoltmeterView voltmeter;
    private final EFieldDetectorView eFieldDetector;

    public MultipleCapacitorsCanvas( MultipleCapacitorsModel model, CLModelViewTransform3D mvt, CLGlobalProperties globalProperties ) {

        this.model = model;
        this.mvt = mvt;

        //TODO maximums shouldn't be dependent on DielectricModel, and may be different for this module
        // maximums
        maxPlateCharge = DielectricModel.getMaxPlateCharge();
        maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        maxEffectiveEField = DielectricModel.getMaxEffectiveEField();
        maxDielectricEField = DielectricModel.getMaxDielectricEField();
        eFieldVectorReferenceMagnitude = DielectricModel.getMaxPlatesDielectricEFieldWithBattery();

        circuitParentNode = new PNode();
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt );
        voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, eFieldVectorReferenceMagnitude, globalProperties.dev, true /* eFieldDetectorSimplified */ );

        // rendering order
        addChild( circuitParentNode );
        addChild( capacitanceMeterNode );
        addChild( plateChargeMeterNode );
        addChild( storedEnergyMeterNode );
        addChild( eFieldDetector.getBodyNode() );
        addChild( eFieldDetector.getWireNode() );
        addChild( eFieldDetector.getProbeNode() );
        addChild( voltmeter.getBodyNode() );
        addChild( voltmeter.getPositiveProbeNode() );
        addChild( voltmeter.getPositiveWireNode() );
        addChild( voltmeter.getNegativeProbeNode() );
        addChild( voltmeter.getNegativeWireNode() );

        model.currentCircuitProperty.addObserver( new SimpleObserver() {
            public void update() {
                updateCircuit();
            }
        } );
    }

    public void reset() {
        // global properties of the view
        plateChargesVisibleProperty.reset();
        eFieldVisibleProperty.reset();
        dielectricChargeViewProperty.reset();
        // zoom level of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    private void updateCircuit() {
        circuitParentNode.removeAllChildren();
        circuitParentNode.addChild( createCircuit( model.currentCircuitProperty.get() ) );
    }

    //TODO revisit this after things are working
    // factory method for creating circuit nodes
    private PNode createCircuit( ICircuit circuit ) {
        PNode circuitNode = null;
        if ( circuit instanceof SingleCircuit ) {
            circuitNode = new SingleCircuitNode( (SingleCircuit) circuit, mvt,
                                                 plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                 maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
        }
        else if ( circuit instanceof SeriesCircuit ) {
            circuitNode = new SeriesCircuitNode( (SeriesCircuit) circuit, mvt,
                                                 plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                 maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
        }
        else if ( circuit instanceof ParallelCircuit ) {
            circuitNode = new ParallelCircuitNode( (ParallelCircuit) circuit, mvt,
                                                   plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                   maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
        }
        else if ( circuit instanceof Combination1Circuit ) {
            circuitNode = new Combination1CircuitNode( (Combination1Circuit) circuit, mvt,
                                                       plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                       maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
        }
        else if ( circuit instanceof Combination2Circuit ) {
            circuitNode = new Combination2CircuitNode( (Combination2Circuit) circuit, mvt,
                                                       plateChargesVisibleProperty, eFieldVisibleProperty, dielectricChargeViewProperty,
                                                       maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );
        }
        else {
            circuitNode = new NullCircuitNode( circuit, mvt );
        }
        return circuitNode;
    }


    @Override
    protected void updateLayout() {
        super.updateLayout();

        Dimension2D worldSize = getWorldSize();
        if ( worldSize.getWidth() <= 0 || worldSize.getHeight() <= 0 ) {
            // canvas hasn't been sized, blow off layout
            return;
        }

        // adjust the model bounds
        Point3D p = mvt.viewToModelDelta( worldSize.getWidth(), worldSize.getHeight() );
        model.getWorldBounds().setBounds( 0, 0, p.getX(), p.getY() );
    }
}
