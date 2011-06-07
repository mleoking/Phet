// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.module.dielectric;

import edu.colorado.phet.capacitorlab.CLGlobalProperties;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.developer.EFieldShapesDebugNode;
import edu.colorado.phet.capacitorlab.developer.VoltageShapesDebugNode;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.CLCanvas;
import edu.colorado.phet.capacitorlab.view.DielectricCircuitNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.CapacitanceMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.PlateChargeMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.BarMeterNode.StoredEnergyMeterNode;
import edu.colorado.phet.capacitorlab.view.meters.EFieldDetectorView;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterView;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Canvas for the "Dielectric" module.
 * </p>
 * This canvas has much in common with MultipleCapacitorsCanvas, but was developed added much earlier, uses a different
 * representation for circuits, and has different parameterizations of some view components.  I attempted to force some
 * of the common bits into the base class, but it because messy and less readable. So I decided that a bit of
 * duplication is preferable here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricCanvas extends CLCanvas {

    private final DielectricModel model;
    private final CLGlobalProperties globalProperties;
    private final BarMeterNode capacitanceMeterNode, plateChargeMeterNode, storedEnergyMeterNode;
    private final PNode shapesDebugParentNode;

    public DielectricCanvas( final DielectricModel model, CLModelViewTransform3D mvt, CLGlobalProperties globalProperties,
                             boolean eFieldDetectorSimplified, boolean dielectricVisible ) {
        super( model, mvt );

        this.model = model;
        this.globalProperties = globalProperties;

        // Maximums, for calibrating various view representations.
        final double maxPlateCharge = DielectricModel.getMaxPlateCharge();
        final double maxExcessDielectricPlateCharge = DielectricModel.getMaxExcessDielectricPlateCharge();
        final double maxEffectiveEField = DielectricModel.getMaxEffectiveEField();
        final double maxDielectricEField = DielectricModel.getMaxDielectricEField();
        final double eFieldReferenceMagnitude = DielectricModel.getEFieldReferenceMagnitude();

        // circuit
        final DielectricCircuitNode circuitNode = new DielectricCircuitNode( model.getCircuit(), mvt, dielectricVisible,
                                                                             getPlateChargesVisibleProperty(), getEFieldVisibleProperty(), getDielectricChargeViewProperty(),
                                                                             maxPlateCharge, maxExcessDielectricPlateCharge, maxEffectiveEField, maxDielectricEField );

        // meters
        capacitanceMeterNode = new CapacitanceMeterNode( model.getCapacitanceMeter(), mvt, CLStrings.CAPACITANCE );
        plateChargeMeterNode = new PlateChargeMeterNode( model.getPlateChargeMeter(), mvt, CLStrings.PLATE_CHARGE_TOP );
        storedEnergyMeterNode = new StoredEnergyMeterNode( model.getStoredEnergyMeter(), mvt, CLStrings.STORED_ENERGY );
        VoltmeterView voltmeter = new VoltmeterView( model.getVoltmeter(), mvt );
        EFieldDetectorView eFieldDetector = new EFieldDetectorView( model.getEFieldDetector(), mvt, eFieldReferenceMagnitude, globalProperties.dev, eFieldDetectorSimplified );

        // debug
        shapesDebugParentNode = new PComposite();

        // rendering order
        addChild( circuitNode );
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
        addChild( shapesDebugParentNode );

        // watch things whose visibility causes the dielectric to become transparent
        SimpleObserver o = new SimpleObserver() {
            public void update() {
                boolean transparent = getEFieldVisibleProperty().get() || model.getVoltmeter().isVisible() || model.getEFieldDetector().visibleProperty.get();
                circuitNode.setDielectricTransparent( transparent );
            }
        };
        getEFieldVisibleProperty().addObserver( o );
        model.getVoltmeter().visibleProperty.addObserver( o );
        model.getEFieldDetector().visibleProperty.addObserver( o );

        // change visibility of debug shapes
        SimpleObserver shapesVisibilityObserver = new SimpleObserver() {
            public void update() {
                updateShapesDebugNodes();
            }
        };
        globalProperties.eFieldShapesVisibleProperty.addObserver( shapesVisibilityObserver );
        globalProperties.voltageShapesVisibleProperty.addObserver( shapesVisibilityObserver );

        // default state
        reset();
    }

    public void reset() {
        super.reset();
        // zoom levels of bar meters
        capacitanceMeterNode.reset();
        plateChargeMeterNode.reset();
        storedEnergyMeterNode.reset();
    }

    // Updates the debugging shapes by recreating them. Quick and dirty, because this is a developer feature.
    private void updateShapesDebugNodes() {
        shapesDebugParentNode.removeAllChildren();

        PNode voltageShapesDebugNode = new VoltageShapesDebugNode( model.getCircuit(), model.getVoltmeter() );
        shapesDebugParentNode.addChild( voltageShapesDebugNode );
        voltageShapesDebugNode.setVisible( globalProperties.voltageShapesVisibleProperty.get() );

        PNode eFieldShapesDebugNode = new EFieldShapesDebugNode( model.getCircuit() );
        shapesDebugParentNode.addChild( eFieldShapesDebugNode );
        eFieldShapesDebugNode.setVisible( globalProperties.eFieldShapesVisibleProperty.get() );
    }
}
