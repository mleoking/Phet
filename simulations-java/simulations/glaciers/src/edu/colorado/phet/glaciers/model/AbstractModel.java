/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.glaciers.model.AbstractTool.ToolAdapter;
import edu.colorado.phet.glaciers.model.AbstractTool.ToolListener;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeAdapter;
import edu.colorado.phet.glaciers.model.Borehole.BoreholeListener;
import edu.colorado.phet.glaciers.model.BoreholeDrill.BoreholeDrillListener;
import edu.colorado.phet.glaciers.model.Debris.DebrisAdapter;
import edu.colorado.phet.glaciers.model.Debris.DebrisListener;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple.IceSurfaceRippleAdapter;
import edu.colorado.phet.glaciers.model.IceSurfaceRipple.IceSurfaceRippleListener;


/**
 * AbstractModel is the base class for all models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModel implements IToolProducer, IBoreholeProducer, IDebrisProducer, IIceSurfaceRippleProducer {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final boolean ENABLE_DEBUG_OUTPUT = true;
    private static final double YEARS_PER_DEBRIS_GENERATED = 1; // debris is generated this many years apart
    private static final double YEARS_PER_RIPPLE_GENERATED = 35; // ripples are generated this many years apart

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersClock _clock;
    private final Glacier _glacier;
    
    private final ArrayList _tools; // list of AbstractTool
    private final ArrayList _toolProducerListeners; // list of ToolProducerListener
    private final ToolListener _toolSelfDeletionListener;
    private final BoreholeDrillListener _boreholeDrillListener;
    private final BoreholeListener _boreholeSelfDeletionListener;
    
    private final ArrayList _boreholes; // list of Borehole
    private final ArrayList _boreholeProducerListeners; // list of BoreholeProducerListener
    
    private final ArrayList _debris; // list of Debris
    private final ArrayList _debrisProducerListeners; // list of DebrisProducerListener
    private final DebrisListener _debrisSelfDeletionListener;
    private final DebrisGenerator _debrisGenerator;
    private final Point3D _pDebris;
    private double _timeSinceLastDebrisGenerated;
    
    private final ArrayList _ripples; // list of IceSurfaceRipple
    private final ArrayList _rippleProducerListeners; // list of IceSurfaceRippleProducerListeners
    private final IceSurfaceRippleListener _rippleSelfDeletionListener;
    private double _timeSinceLastRippleGenerated;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AbstractModel( GlaciersClock clock, Glacier glacier ) {
        super();
        
        _clock = clock;
        _glacier = glacier;
        
        _clock.addClockListener( glacier );
        
        _tools = new ArrayList();
        _toolProducerListeners = new ArrayList();
        
        _boreholes = new ArrayList();
        _boreholeProducerListeners = new ArrayList();
        
        _debris = new ArrayList();
        _debrisProducerListeners = new ArrayList();
        _debrisGenerator = new DebrisGenerator( glacier );
        _pDebris = new Point3D.Double();
        _timeSinceLastDebrisGenerated = YEARS_PER_DEBRIS_GENERATED; // generate one on first clock tick
        
        _ripples = new ArrayList();
        _rippleProducerListeners = new ArrayList();
        _timeSinceLastRippleGenerated = YEARS_PER_RIPPLE_GENERATED; // generate one on first clock tick
        
        // create a borehole when the drill is pressed
        _boreholeDrillListener = new BoreholeDrillListener() {
            public void drillAt( Point2D position ) {
                addBorehole( position );
            }
        };
        
        // boreholes delete themselves
        _boreholeSelfDeletionListener = new BoreholeAdapter() {
            public void deleteMe( Borehole borehole ) {
                removeBorehole( borehole );
            }
        };
        
        // tools deletes themselves
        _toolSelfDeletionListener = new ToolAdapter() {
            public void deleteMe( AbstractTool tool ) {
                removeTool( tool );
            }
        };
        
        // debris deletes themselves
        _debrisSelfDeletionListener = new DebrisAdapter() {
            public void deleteMe( Debris debris ) {
                removeDebris( debris );
            }
        };
        
        // ripples delete themselves
        _rippleSelfDeletionListener = new IceSurfaceRippleAdapter() {
            public void deleteMe( IceSurfaceRipple ripple ) {
                removeIceSurfaceRipple( ripple );
            }
        };
        
        _clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                generateDebris( clockEvent );
                generateRipple( clockEvent );
            }
        });
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GlaciersClock getClock() {
        return _clock;
    }
    
    public Glacier getGlacier() {
        return _glacier;
    }
    
    public Valley getValley() {
        return _glacier.getValley();
    }
    
    public Climate getClimate() {
        return _glacier.getClimate();
    }
    
    //----------------------------------------------------------------------------
    // IToolProducer
    //----------------------------------------------------------------------------
    
    public BoreholeDrill addBoreholeDrill( Point2D position ) {
        BoreholeDrill tool = new BoreholeDrill( position, getGlacier() );
        tool.addBoreholeDrillListener( _boreholeDrillListener );
        addTool( tool );
        return tool;
    }
    
    public GlacialBudgetMeter addGlacialBudgetMeter( Point2D position ) {
        GlacialBudgetMeter tool = new GlacialBudgetMeter( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public GPSReceiver createGPSReceiver( Point2D position ) {
        GPSReceiver tool = new GPSReceiver( position );
        addTool( tool );
        return tool;
    }
    
    public IceThicknessTool addIceThicknessTool( Point2D position ) {
        IceThicknessTool tool = new IceThicknessTool( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public Thermometer addThermometer( Point2D position ) {
        Thermometer tool = new Thermometer( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public TracerFlag addTracerFlag( Point2D position ) {
        TracerFlag tool = new TracerFlag( position, getGlacier() );
        addTool( tool );
        return tool;
    }
    
    public void addToolProducerListener( IToolProducerListener listener ) {
        _toolProducerListeners.add( listener );
    }
    
    public void removeToolProducerListener( IToolProducerListener listener ) {
        _toolProducerListeners.remove( listener );
    }
    
    private void addTool( AbstractTool tool ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.addTool " + tool.getClass().getName() );
        }
        if ( _tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to add tool twice: " + tool.getClass().getName() );
        }
        tool.addToolListener( _toolSelfDeletionListener );
        _tools.add( tool );
        _clock.addClockListener( tool );
        notifyToolAdded( tool );
    }
    
    public void removeTool( AbstractTool tool ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.removeTool " + tool.getClass().getName() );
        }
        if ( !_tools.contains( tool ) ) {
            throw new IllegalStateException( "attempted to remove a tool that doesn't exist: " + tool.getClass().getName() );
        }
        if ( tool instanceof BoreholeDrill ) { //XXX
            ( (BoreholeDrill) tool ).removeBoreholeDrillListener( _boreholeDrillListener );
        }
        tool.removeToolListener( _toolSelfDeletionListener );
        _tools.remove( tool );
        _clock.removeClockListener( tool );
        notifyToolRemoved( tool );
        tool.cleanup();
    }
    
    public void removeAllTools() {
        ArrayList toolsCopy = new ArrayList( _tools ); // iterate on a copy of the array
        Iterator i = toolsCopy.iterator();
        while ( i.hasNext() ) {
            removeTool( (AbstractTool) i.next() );
        }
    }
    
    private void notifyToolAdded( AbstractTool tool ) {
        Iterator i = _toolProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IToolProducerListener)i.next()).toolAdded( tool );
        }
    }
    
    private void notifyToolRemoved( AbstractTool tool ) {
        Iterator i = _toolProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IToolProducerListener)i.next()).toolRemoved( tool );
        }
    }
    
    //----------------------------------------------------------------------------
    // IBoreholeProducer
    //----------------------------------------------------------------------------
    
    public Borehole addBorehole( Point2D position ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.addBorehole" );
        }
        Borehole borehole = new Borehole( _glacier, position );
        borehole.addBoreholeListener( _boreholeSelfDeletionListener );
        _boreholes.add( borehole );
        _clock.addClockListener( borehole );
        notifyBoreholeAdded( borehole );
        return borehole;
    }
    
    public void removeBorehole( Borehole borehole ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.removeBorehole" );
        }
        if ( !_boreholes.contains( borehole ) ) {
            throw new IllegalStateException( "attempted to remove a borehole that doesn't exist" );
        }
        borehole.removeBoreholeListener( _boreholeSelfDeletionListener );
        _boreholes.remove( borehole );
        _clock.removeClockListener( borehole );
        notifyBoreholeRemoved( borehole );
        borehole.cleanup();
    }
    
    public void removeAllBoreholes() {
        ArrayList boreholesCopy = new ArrayList( _boreholes ); // iterate on a copy of the array
        Iterator i = boreholesCopy.iterator();
        while ( i.hasNext() ) {
            removeBorehole( (Borehole) i.next() );
        }
    }
    
    public void addBoreholeProducerListener( IBoreholeProducerListener listener ) {
        _boreholeProducerListeners.add( listener );
    }
    
    public void removeBoreholeProducerListener( IBoreholeProducerListener listener ) {
        _boreholeProducerListeners.remove( listener );
    }
    
    private void notifyBoreholeAdded( Borehole borehole ) {
        Iterator i = _boreholeProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IBoreholeProducerListener)i.next()).boreholeAdded( borehole );
        }
    }
    
    private void notifyBoreholeRemoved( Borehole borehole ) {
        Iterator i = _boreholeProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IBoreholeProducerListener)i.next()).boreholeRemoved( borehole );
        }
    }
    
    //----------------------------------------------------------------------------
    // IDebrisProducer
    //----------------------------------------------------------------------------
    
    public Debris addDebris( Point3D position ) {
        Debris debris = new Debris( position, _glacier );
        debris.addDebrisListener( _debrisSelfDeletionListener );
        _debris.add( debris );
        _clock.addClockListener( debris );
        notifyDebrisAdded( debris );
        return debris;
    }
    
    public void removeDebris( Debris debris ) {
        if ( !_debris.contains( debris ) ) {
            throw new IllegalStateException( "attempted to remove debris that doesn't exist" );
        }
        debris.removeDebrisListener( _debrisSelfDeletionListener );
        _debris.remove( debris );
        _clock.removeClockListener( debris );
        notifyDebrisRemoved( debris );
        debris.cleanup();
    }
    
    public void removeAllDebris() {
        ArrayList debrisCopy = new ArrayList( _debris ); // iterate on a copy of the array
        Iterator i = debrisCopy.iterator();
        while ( i.hasNext() ) {
            removeDebris( (Debris) i.next() );
        }
    }
    
    public void addDebrisProducerListener( IDebrisProducerListener listener ) {
        _debrisProducerListeners.add( listener );
    }
    
    public void removeDebrisProducerListener( IDebrisProducerListener listener ) {
        _debrisProducerListeners.remove( listener );
    }
    
    private void notifyDebrisAdded( Debris debris ) {
        Iterator i = _debrisProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IDebrisProducerListener)i.next()).debrisAdded( debris );
        }
    }
    
    private void notifyDebrisRemoved( Debris debris ) {
        Iterator i = _debrisProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IDebrisProducerListener)i.next()).debrisRemoved( debris );
        }
    }
    
    private void generateDebris( ClockEvent clockEvent ) {
        _timeSinceLastDebrisGenerated += clockEvent.getSimulationTimeChange();
        if ( _timeSinceLastDebrisGenerated >= YEARS_PER_DEBRIS_GENERATED ) {
            _debrisGenerator.generateDebrisPosition( _pDebris /* output */ );
            addDebris( _pDebris );
            _timeSinceLastDebrisGenerated = 0;
        }
    }
    
    //----------------------------------------------------------------------------
    // IIceSurfaceRippleProducer
    //----------------------------------------------------------------------------
    
    public IceSurfaceRipple addIceSurfaceRipple( double x ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.addIceSurfaceRipple " + x );
        }
        IceSurfaceRipple ripple = new IceSurfaceRipple( x, _glacier );
        ripple.addIceSurfaceRippleListener( _rippleSelfDeletionListener );
        _ripples.add( ripple );
        _clock.addClockListener( ripple );
        notifyIceSurfaceRippleAdded( ripple );
        return ripple;
    }
    
    public void removeIceSurfaceRipple( IceSurfaceRipple ripple ) {
        if ( ENABLE_DEBUG_OUTPUT ) {
            System.out.println( "AbstractModel.removeIceSurfaceRipple " );
        }
        if ( !_ripples.contains( ripple ) ) {
            throw new IllegalStateException( "attempted to remove ripple that doesn't exist" );
        }
        ripple.removeIceSurfaceRippleListener( _rippleSelfDeletionListener );
        _ripples.remove( ripple );
        _clock.removeClockListener( ripple );
        notifyIceSurfaceRippleRemoved( ripple );
        ripple.cleanup();
    }
    
    public void removeAllIceSurfaceRipples() {
        ArrayList ripplesCopy = new ArrayList( _ripples ); // iterate on a copy of the array
        Iterator i = ripplesCopy.iterator();
        while ( i.hasNext() ) {
            removeIceSurfaceRipple( (IceSurfaceRipple) i.next() );
        }
    }
    
    public void addIceSurfaceRippleProducerListener( IIceSurfaceRippleProducerListener listener ) {
        _rippleProducerListeners.add( listener );
    }
    
    public void removeIceSurfaceRippleProducerListener( IIceSurfaceRippleProducerListener listener ) {
        _rippleProducerListeners.remove( listener );
    }
    
    private void notifyIceSurfaceRippleAdded( IceSurfaceRipple ripple ) {
        Iterator i = _rippleProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IIceSurfaceRippleProducerListener)i.next()).rippleAdded( ripple );
        }
    }
    
    private void notifyIceSurfaceRippleRemoved( IceSurfaceRipple ripple ) {
        Iterator i = _rippleProducerListeners.iterator();
        while ( i.hasNext() ) {
            ((IIceSurfaceRippleProducerListener)i.next()).rippleRemoved( ripple );
        }
    }
    
    private void generateRipple( ClockEvent clockEvent ) {
        _timeSinceLastRippleGenerated += clockEvent.getSimulationTimeChange();
        if ( _timeSinceLastRippleGenerated >= YEARS_PER_RIPPLE_GENERATED ) {
            addIceSurfaceRipple( _glacier.getHeadwallX() + 1 );
            _timeSinceLastRippleGenerated = 0;
        }
    }
}
