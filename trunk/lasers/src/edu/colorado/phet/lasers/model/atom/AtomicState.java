/**
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.atom;

import edu.colorado.phet.common.util.EventRegistry;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.util.EventListener;
import java.util.EventObject;

public abstract class AtomicState {

    ///////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //

    static public final double minWavelength = Photon.BLUE - 80;
    static public final double maxWavelength = Photon.GRAY;
    static public final double minEnergy = Photon.wavelengthToEnergy( maxWavelength );
    static public final double maxEnergy = Photon.wavelengthToEnergy( minWavelength );
    static protected double s_collisionLikelihood = 1;
    static protected final double wavelengthTolerance = 10;
    //        static protected double s_collisionLikelihood = 0.2;


    ///////////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private double energyLevel;
    private double wavelength;
    private int numAtomsInState;
    private EventRegistry eventRegistry = new EventRegistry();
    private double meanLifetime = Double.POSITIVE_INFINITY;


    public void addListener( EventListener listener ) {
        eventRegistry.addListener( listener );
    }

    public void removeListener( EventListener listener ) {
        eventRegistry.removeListener( listener );
    }

    void incrNumInState() {
        numAtomsInState++;

        int total = GroundState.instance().getNumAtomsInState() + MiddleEnergyState.instance().getNumAtomsInState() + HighEnergyState.instance().getNumAtomsInState();
        if( total != 6 ) {
            System.out.println( "$$$" );
        }
    }

    void decrementNumInState() {
        numAtomsInState--;
        if( numAtomsInState < 0 ) {
            System.out.println( "!!!" );
        }
        int total = GroundState.instance().getNumAtomsInState() + MiddleEnergyState.instance().getNumAtomsInState() + HighEnergyState.instance().getNumAtomsInState();
        if( total != 5 ) {
            System.out.println( "%%%" );
        }
    }

    public int getNumAtomsInState() {
        return numAtomsInState;
    }

    public void clearNumInState() {
        numAtomsInState = 0;
    }

    public double getEnergyLevel() {
        return energyLevel;
    }

    /**
     * Returns the lifetime of the state. This is based on the energy level.
     * The higher the energy, the shorter the lifetime.
     *
     * @return
     */
    public double getMeanLifeTime() {
        return meanLifetime;
    }

    public void setMeanLifetime( double lifetime ) {
        this.meanLifetime = lifetime;
        eventRegistry.fireEvent( new MeanLifetimeChangeEvent() );
    }

    public void setEnergyLevel( double energyLevel ) {
        this.energyLevel = energyLevel;
        this.wavelength = Photon.energyToWavelength( energyLevel );
        //        fireEnergyLevelChangeEvent( new EnergyLevelChangeEvent( this ) );
        eventRegistry.fireEvent( new EnergyLevelChangeEvent( this ) );
    }

    public double getWavelength() {
        return wavelength;
    }

    protected void setEmittedPhotonWavelength( double wavelength ) {
        this.wavelength = wavelength;
        this.energyLevel = Photon.wavelengthToEnergy( wavelength );
    }

    protected double getEmittedPhotonWavelength() {
        return wavelength;
    }

    protected boolean isStimulatedBy( Photon photon ) {
        return ( Math.abs( photon.getWavelength() - this.getWavelength() ) <= wavelengthTolerance && Math.random() < s_collisionLikelihood );
    }

    abstract public void collideWithPhoton( Atom atom, Photon photon );

    abstract public AtomicState getNextLowerEnergyState();

    abstract public AtomicState getNextHigherEnergyState();

    //////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //
    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    public static class MaxEnergyState extends AtomicState {
        private static MaxEnergyState instance = new MaxEnergyState();

        public static MaxEnergyState instance() {
            return instance;
        }

        private MaxEnergyState() {
            setEnergyLevel( maxEnergy );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }
    }

    /**
     * A class that represents the highest energy and shortest wavelength we will allow
     */
    public static class MinEnergyState extends AtomicState {
        private static MinEnergyState instance = new MinEnergyState();

        public static MinEnergyState instance() {
            return instance;
        }

        private MinEnergyState() {
            setEnergyLevel( minEnergy );
        }

        public void collideWithPhoton( Atom atom, Photon photon ) {
        }

        public AtomicState getNextLowerEnergyState() {
            return null;
        }

        public AtomicState getNextHigherEnergyState() {
            return null;
        }
    }

    public interface MeanLifetimeChangeListener extends EventListener {
        public void meanLifetimeChanged( MeanLifetimeChangeEvent event );
    }

    public class MeanLifetimeChangeEvent extends EventObject {
        public MeanLifetimeChangeEvent() {
            super( AtomicState.this );
        }

        public double getMeanLifetime() {
            return AtomicState.this.getMeanLifeTime();
        }
    }

    public interface EnergyLevelChangeListener extends EventListener {
        void energyLevelChangeOccurred( EnergyLevelChangeEvent event );
    }

    public class EnergyLevelChangeEvent extends EventObject {
        public EnergyLevelChangeEvent( Object source ) {
            super( source );
        }

        public double getEnergy() {
            return ( (AtomicState)getSource() ).getEnergyLevel();
        }
    }
}
