/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.model;

import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.opticaltweezers.defaults.DNADefaults;
import edu.colorado.phet.opticaltweezers.defaults.GlobalDefaults;

/**
 * DNAModel is the model for DNAModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DNAModel extends ClockAdapter {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _modelElements; // array of ModelElement
    
    private final OTClock _clock;

    private final Fluid _fluid;
    private final MicroscopeSlide _microscopeSlide;
    private final Laser _laser;
    private final Bead _bead;
    private final DNAStrand _dnaStrand;
    
    private ModelViewTransform _modelViewTransform;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DNAModel( OTClock clock ) {
        super();
        
        _clock = clock;
        _clock.addClockListener( this );
        
        _modelElements = new ArrayList();
        
        _fluid = new Fluid( GlobalDefaults.FLUID_SPEED_RANGE,
                GlobalDefaults.FLUID_DIRECTION,
                GlobalDefaults.FLUID_VISCOSITY_RANGE, 
                GlobalDefaults.FLUID_TEMPERATURE_RANGE );
        _modelElements.add( _fluid );
        
        _microscopeSlide = new MicroscopeSlide( DNADefaults.MICROSCOPE_SLIDE_POSITION,
                DNADefaults.MICROSCOPE_SLIDE_ORIENTATION,
                DNADefaults.MICROSCOPE_SLIDE_CENTER_HEIGHT,
                DNADefaults.MICROSCOPE_SLIDE_EDGE_HEIGHT );
        _modelElements.add( _microscopeSlide );
        
        _laser = new Laser( DNADefaults.LASER_POSITION, 
                GlobalDefaults.LASER_ORIENTATION, 
                DNADefaults.LASER_DIAMETER_AT_OBJECTIVE, 
                DNADefaults.LASER_DIAMETER_AT_WAIST,
                DNADefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_WAIST,
                DNADefaults.LASER_DISTANCE_FROM_OBJECTIVE_TO_CONTROL_PANEL,
                GlobalDefaults.LASER_WAVELENGTH,
                GlobalDefaults.LASER_VISIBLE_WAVELENGTH,
                GlobalDefaults.LASER_POWER_RANGE,
                GlobalDefaults.LASER_TRAP_FORCE_RATIO,
                GlobalDefaults.LASER_ELECTRIC_FIELD_SCALE_RANGE,
                clock );
        _modelElements.add( _laser );
        
        _bead = new Bead( DNADefaults.BEAD_POSITION, 
                GlobalDefaults.BEAD_ORIENTATION, 
                GlobalDefaults.BEAD_DIAMETER,
                GlobalDefaults.BEAD_DENSITY,
                GlobalDefaults.BEAD_DT_SUBDIVISION_THRESHOLD_RANGE,
                GlobalDefaults.BEAD_NUMBER_OF_DT_SUBDIVISIONS_RANGE,
                GlobalDefaults.BEAD_BROWNIAN_MOTION_SCALE_RANGE,
                GlobalDefaults.BEAD_VERLET_ACCELERATION_SCALE_RANGE,
                _fluid,
                _microscopeSlide,
                _laser );
         _modelElements.add( _bead );
         
         _dnaStrand = new DNAStrand( DNADefaults.DNA_CONTOUR_LENGTH, 
                 DNADefaults.DNA_PERSISTENCE_LENGTH, 
                 DNADefaults.DNA_NUMBER_OF_SPRINGS, 
                 DNADefaults.DNA_SPRING_CONSTANT_RANGE, 
                 DNADefaults.DNA_DRAG_COEFFICIENT_RANGE, 
                 DNADefaults.DNA_KICK_CONSTANT_RANGE, 
                 DNADefaults.DNA_NUMBER_OF_EVOLUTIONS_PER_CLOCK_STEP_RANGE,
                 DNADefaults.DNA_EVOLUTION_DT_RANGE,
                 DNADefaults.DNA_FLUID_DRAG_COEFFICIENT_RANGE,
                 _clock.getFastRange().getMax(),
                 _bead,
                 _fluid );
         _modelElements.add( _dnaStrand );
         _bead.attachTo( _dnaStrand ); // attach bead to DNA strand

         _modelViewTransform = new ModelViewTransform( DNADefaults.MODEL_TO_VIEW_SCALE );
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public OTClock getClock() {
        return _clock;
    }
    
    public Fluid getFluid() {
        return _fluid;
    }
    
    public MicroscopeSlide getMicroscopeSlide() {
        return _microscopeSlide;
    }
    
    public Laser getLaser() {
        return _laser;
    }
    
    public Bead getBead() {
        return _bead;
    }
    
    public DNAStrand getDNAStrand() {
        return _dnaStrand;
    }
    
    public ModelViewTransform getModelViewTransform() {
        return _modelViewTransform;
    }
    
    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * When the clock ticks, call stepInTime for each model element.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        double dt = event.getSimulationTimeChange();
        Iterator i = _modelElements.iterator();
        while ( i.hasNext() ) {
            ModelElement modelElement = (ModelElement) i.next();
            modelElement.stepInTime( dt );
        }
    }
}
