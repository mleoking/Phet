/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.CustomDielectricMaterial;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Glass;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Paper;
import edu.colorado.phet.capacitorlab.model.DielectricMaterial.Teflon;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;


/**
 * Model for the "Dielectric" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricModel {
    
    // static properties
    private final World world;
    private final DielectricMaterial[] dielectricMaterials;
    private final CustomDielectricMaterial customDielectricMaterial;
    private final DielectricMaterial defaultDielectricMaterial;
    private final BatteryCapacitorCircuit circuit;
    private final Wire topWire, bottomWire;
    private final EFieldDetector eFieldDetector;
    private final Voltmeter voltmeter;
    
    private final WireBranch someWireBranch;
    
    public DielectricModel( CLClock clock ) {
        
        world = new World();
        
        customDielectricMaterial = new CustomDielectricMaterial( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        dielectricMaterials = new DielectricMaterial[] { customDielectricMaterial, new Teflon(), new Paper(), new Glass() };
        defaultDielectricMaterial = customDielectricMaterial;
        
        Battery battery = new Battery( CLConstants.BATTERY_LOCATION, CLConstants.BATTERY_LENGTH, CLConstants.BATTERY_DIAMETER, CLConstants.BATTERY_VOLTAGE_RANGE.getDefault() );
        final Capacitor capacitor = new Capacitor( CLConstants.CAPACITOR_LOCATION, CLConstants.PLATE_SIZE_RANGE.getDefault(), CLConstants.PLATE_SEPARATION_RANGE.getDefault(), 
                defaultDielectricMaterial, CLConstants.PLATE_SIZE_RANGE.getDefault() /* dielectricOffset */ );
        circuit = new BatteryCapacitorCircuit( clock, battery, capacitor, CLConstants.BATTERY_CONNECTED );
        
        topWire = new Wire( CLConstants.WIRE_THICKNESS, CLConstants.TOP_WIRE_EXTENT );
        bottomWire = new Wire( CLConstants.WIRE_THICKNESS, CLConstants.BOTTOM_WIRE_EXTENT );
        
        eFieldDetector = new EFieldDetector( circuit, world, CLConstants.EFIELD_DETECTOR_PROBE_LOCATION, CLConstants.EFIELD_DETECTOR_VISIBLE,
                CLConstants.EFIELD_PLATE_VECTOR_VISIBLE, CLConstants.EFIELD_DIELECTRIC_VECTOR_VISIBLE, 
                CLConstants.EFIELD_SUM_VECTOR_VISIBLE, CLConstants.EFIELD_VALUES_VISIBLE );
        
        voltmeter = new Voltmeter( circuit, world, CLConstants.VOLTMETER_VISIBLE, CLConstants.VOLTMETER_POSITIVE_PROBE_LOCATION, CLConstants.VOLTMETER_NEGATIVE_PROBE_LOCATION );
        
        final WireSegment segment1 = new WireSegment( CLConstants.WIRE_THICKNESS, new Point2D.Double(  0.005, 0.035), new Point2D.Double( 0.03, 0.035 ) );
        final WireSegment segment2 = new WireSegment( CLConstants.WIRE_THICKNESS, segment1.getEndPoint(), capacitor.getTopPlateCenter() );
        CapacitorChangeAdapter plateSeparationListener = new CapacitorChangeAdapter() {
            @Override
            public void plateSeparationChanged() {
                segment2.setEndPoint( capacitor.getTopPlateCenter() );
            }
        };
        capacitor.addCapacitorChangeListener( plateSeparationListener );
        ArrayList<WireSegment> segments = new ArrayList<WireSegment>() {{
            add( segment1 );
            add( segment2 );
        }};
        someWireBranch = new WireBranch( segments );
        plateSeparationListener.plateSeparationChanged();
        
        // default state
        reset();
    }
    
    public World getWorld() {
        return world;
    }
    
    public DielectricMaterial[] getDielectricMaterials() {
        return dielectricMaterials;
    }
    
    public BatteryCapacitorCircuit getCircuit() {
        return circuit;
    }
    
    public Battery getBattery() {
        return circuit.getBattery();
    }
    
    public Capacitor getCapacitor() {
        return circuit.getCapacitor();
    }
    
    public Wire getTopWire() {
        return topWire;
    }
    
    public Wire getBottomWire() {
        return bottomWire;
    }
    
    public EFieldDetector getEFieldDetector() {
        return eFieldDetector;
    }
    
    public Voltmeter getVoltmeter() {
        return voltmeter;
    }
    
    public WireBranch getSomeWireBranch() {
        return someWireBranch;
    }
    
    public void reset() {
        // battery
        getBattery().setVoltage( CLConstants.BATTERY_VOLTAGE_RANGE.getDefault() );
        // capacitor
        getCapacitor().setPlateSideLength( CLConstants.PLATE_SIZE_RANGE.getDefault() );
        getCapacitor().setPlateSeparation( CLConstants.PLATE_SEPARATION_RANGE.getDefault() );
        customDielectricMaterial.setDielectricConstant( CLConstants.DIELECTRIC_CONSTANT_RANGE.getDefault() );
        getCapacitor().setDielectricMaterial( defaultDielectricMaterial );
        getCapacitor().setDielectricOffset( CLConstants.DIELECTRIC_OFFSET_RANGE.getDefault() );
        // circuit
        getCircuit().setBatteryConnected( CLConstants.BATTERY_CONNECTED );
        // E-field detector
        eFieldDetector.reset();
        // voltmeter
        voltmeter.reset();
    }
}
