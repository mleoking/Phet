/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.particle.ArgonAtom;
import edu.colorado.phet.statesofmatter.model.particle.NeonAtom;
import edu.colorado.phet.statesofmatter.model.particle.OxygenAtom;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterAtom;

/**
 * This is the model for two particles interacting with a Lennard-Jones
 * interaction potential.
 *
 * @author John Blanco
 */
public class DualParticleModel {
    
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

    private static final double TIME_STEP = Math.pow( 0.5, 6.0 );
    private static final double PARTICLE_INTERACTION_DISTANCE_THRESH_SQRD = 6.25;
    
    // Supported molecules.
    public static final int NEON = 1;
    public static final int ARGON = 2;
    public static final int MONATOMIC_OXYGEN = 3;
    public static final int DEFAULT_MOLECULE = MONATOMIC_OXYGEN;
    public static final double DEFAULT_SIGMA = OxygenAtom.getSigma();
    public static final double DEFAULT_EPSILON = OxygenAtom.getEpsilon();
    
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
    IClock m_clock;
    private ArrayList m_listeners = new ArrayList();
    private StatesOfMatterAtom m_leftParticle;
    private StatesOfMatterAtom m_rightParticle;
    private double m_leftParticleHorizForce;
    private double m_rightParticleHorizForce;
    private double m_epsilon;  // Epsilon represents the energy parameter.
    private double m_sigma;    // Sigma represents distance parameter.
    private int m_currentMoleculeID;
    private double m_moleculeDiameter;
    
    //----------------------------------------------------------------------------
    // Constructor
    //----------------------------------------------------------------------------
    
    public DualParticleModel(IClock clock) {
        
        m_clock = clock;
        m_epsilon = 100; // TODO: JPB TBD - Arbitrary initial value, should come up with something real.
        
        // Register as a clock listener.
        clock.addClockListener(new ClockAdapter(){
            
            public void clockTicked( ClockEvent clockEvent ) {
                handleClockTicked(clockEvent);
            }
            
            public void simulationTimeReset( ClockEvent clockEvent ) {
                reset();
            }
        });
        
        // Don't bother adding molecules since the model will be reset as part
        // of the initialization process.
    }

    //----------------------------------------------------------------------------
    // Accessor Methods
    //----------------------------------------------------------------------------
    
    public StatesOfMatterAtom getLeftParticleRef(){
        return m_leftParticle;
    }
    
    public StatesOfMatterAtom getRightParticleRef(){
        return m_rightParticle;
    }
    
    public double getLeftParticleHorizForce(){
        return m_leftParticleHorizForce;
    }

    public double getRightParticleHorizForce(){
        return m_rightParticleHorizForce;
    }
    
    public int getMoleculeType(){
        return m_currentMoleculeID;
    }
    
    public void setMoleculeType(int atomID){
        
        // Verify that this is a supported value.
        if ((atomID != NEON) &&
            (atomID != ARGON) &&
            (atomID != MONATOMIC_OXYGEN)){
            
            System.err.println("Error: Unsupported molecule type.");
            assert false;
            atomID = NEON;
        }
        
        // Inform any listeners of the removal of existing particles.
        if (m_leftParticle != null){
            notifyParticleRemoved( m_leftParticle );
            m_leftParticle = null;
        }
        if (m_rightParticle != null){
            notifyParticleRemoved( m_rightParticle );
            m_rightParticle = null;
        }
        
        // Set the new atoms based on the requested type..
        switch (atomID){
        case NEON:
            m_leftParticle = new NeonAtom(0, 0);
            m_rightParticle = new NeonAtom(0, 0);
            m_sigma = NeonAtom.getSigma();
            m_epsilon = NeonAtom.getEpsilon();
            m_moleculeDiameter = 2 * NeonAtom.RADIUS;
            break;
        case ARGON:
            m_leftParticle = new ArgonAtom(0, 0);
            m_rightParticle = new ArgonAtom(0, 0);
            m_sigma = ArgonAtom.getSigma();
            m_epsilon = ArgonAtom.getEpsilon();
            m_moleculeDiameter = 2 * ArgonAtom.RADIUS;
            break;
        case MONATOMIC_OXYGEN:
            m_leftParticle = new OxygenAtom(0, 0);
            m_rightParticle = new OxygenAtom(0, 0);
            m_sigma = OxygenAtom.getSigma();
            m_epsilon = OxygenAtom.getEpsilon();
            m_moleculeDiameter = 2 * OxygenAtom.RADIUS;
            break;
        }
        
        m_currentMoleculeID = atomID;
        
        // Let listeners know about the new molecules.
        notifyParticleAdded( m_leftParticle );
        notifyParticleAdded( m_rightParticle );
        
        // Let listeners know about parameter changes.
        notifyInteractionPotentialChanged();
        
        // Let listeners know about the diameter change.
        notifyParticleDiameterChanged();
        
        // Move them to be initially separated.
        double diameter = m_leftParticle.getRadius() * 2;
        m_leftParticle.setPosition( -2 * diameter, 0 );
        m_rightParticle.setPosition( 2 * diameter, 0 );
    }
    
    public void setMoleculeDiameter(double diameter){
        m_moleculeDiameter = diameter;
    }
    
    public double getCurrentMoleculeDiameter(){
        return m_moleculeDiameter;
    }
    
    /**
     * Set both of the parameter that control the Lennard-Jones potential.
     * This is done all at once to avoid having to reset twice and to make
     * sure the values are reasonable with respect to one another.
     * 
     * @param sigma - distance parameter
     * @param epsilon - energy parameter
     */
    public void setLennardJonesParameters( double sigma, double epsilon ){
        m_sigma = sigma;
        m_epsilon = epsilon;
        notifyInteractionPotentialChanged();
    }
    
    /**
     * Get the sigma value, a.k.a. the Distance Paramter, which is one of the
     * two parameters that describes the Lennard-Jones potential.
     * 
     * @return
     */
    public double getSigma(){

        return m_sigma;
    }
    
    /**
     * Get the epsilon value, a.k.a. the Energy Parameter, which is one of the
     * two parameters that describes the Lennard-Jones potential.
     * 
     * @return
     */
    public double getEpsilon(){
        return m_epsilon;
    }
    
    //----------------------------------------------------------------------------
    // Other Public Methods
    //----------------------------------------------------------------------------
    
    /**
     * Reset the model.
     */
    public void reset() {
        
        // Initialize the system parameters.
        setMoleculeType( DEFAULT_MOLECULE );
    }
    
    public void addListener(Listener listener){
        
        if (m_listeners.contains( listener ))
        {
            // Don't bother re-adding.
            return;
        }
        
        m_listeners.add( listener );
    }
    
    public boolean removeListener(Listener listener){
        return m_listeners.remove( listener );
    }

    //----------------------------------------------------------------------------
    // Private Methods
    //----------------------------------------------------------------------------
    
    private void handleClockTicked(ClockEvent clockEvent) {
        
        // Execute the force calculation.
        updateForce();
        
        // Update the particle positions.
        updatePosition();
    }
    
    private void updateForce(){
        
        // TODO: JPB TBD - I think the radius is supposed to factor into this
        // somehow, but I'm not sure how, so I'll need to work it out with
        // one of the physicists eventually.
        double sigma = m_moleculeDiameter / 2 * m_sigma;
        double distance = m_rightParticle.getPositionReference().distance( m_leftParticle.getPositionReference() );
        m_rightParticleHorizForce = ((48 * m_epsilon * Math.pow( sigma, 12 ) / Math.pow( distance, 13 )) +
                (24 * m_epsilon * Math.pow(  sigma, 6 ) / Math.pow( distance, 7 )));
    }
    
    private void updatePosition(){
        
        // JPB TBD - This is all very preliminary.  Not sure what to use for mass.
        double mass = 1;
        m_rightParticle.setVx( m_rightParticle.getVx() + m_rightParticleHorizForce / mass );
        double xPos = m_rightParticle.getPositionReference().getX() + m_rightParticle.getVx();
        m_rightParticle.setPosition( xPos, 0 );
    }
    
    private void notifyParticleAdded(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleAdded( particle );
        }        
    }
    
    private void notifyParticleRemoved(StatesOfMatterAtom particle){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleRemoved( particle );
        }        
    }
    
    private void notifyInteractionPotentialChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).interactionPotentialChanged();
        }        
    }
    
    private void notifyParticleDiameterChanged(){
        for (int i = 0; i < m_listeners.size(); i++){
            ((Listener)m_listeners.get( i )).particleDiameterChanged();
        }        
    }
    
    /**
     * Runs one iteration of the Verlet implementation of the Lennard-Jones
     * force calculation on a set of particles.
     */
    private void verletMonotomic(){
        
    }

    //------------------------------------------------------------------------
    // Inner Interfaces and Classes
    //------------------------------------------------------------------------
    
    public static interface Listener {
        
        /**
         * Inform listeners that the model has been reset.
         */
        public void particleAdded(StatesOfMatterAtom particle);
        public void particleRemoved(StatesOfMatterAtom particle);
        public void interactionPotentialChanged();
        public void particleDiameterChanged();
    }
    
    public static class Adapter implements Listener {
        public void particleAdded(StatesOfMatterAtom particle){}
        public void particleRemoved(StatesOfMatterAtom particle){}
        public void interactionPotentialChanged(){};
        public void particleDiameterChanged(){};
    }
}
