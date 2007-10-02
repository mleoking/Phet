package edu.colorado.phet.statesofmatter.model;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.statesofmatter.StatesOfMatterConfig;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;
import edu.colorado.phet.statesofmatter.model.engine.EngineConfig;
import edu.colorado.phet.statesofmatter.model.engine.ForceComputation;
import edu.colorado.phet.statesofmatter.model.engine.ForceEngine;
import edu.colorado.phet.statesofmatter.model.engine.VerletForceEngine;
import edu.colorado.phet.statesofmatter.model.engine.kinetic.KineticEnergyAdjuster;
import edu.colorado.phet.statesofmatter.model.particle.NonOverlappingParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.ParticleCreationStrategy;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultipleParticleModel extends BaseModel implements ClockListener {
    private final List particles = new ArrayList();

    public static final MultipleParticleModel TEST = new MultipleParticleModel(ConstantDtClock.TEST);

    private double particleRadius = 0.1;
    private double particleMass   = 1024;
    private ForceEngine forceEngine = new VerletForceEngine();

    public MultipleParticleModel(IClock clock) {
        clock.addClockListener(this);

        initialize();
    }

    public void initialize() {
        ParticleCreationStrategy strategy = new NonOverlappingParticleCreationStrategy(StatesOfMatterConfig.CONTAINER_BOUNDS, particleRadius);

        particles.clear();

        for (int i = 0; i < StatesOfMatterConfig.INITIAL_PARTICLE_COUNT; i++) {
            particles.add(strategy.createNewParticle(particles, particleRadius, particleMass));
        }

        KineticEnergyAdjuster adjuster = new KineticEnergyAdjuster();

        adjuster.adjust(particles, StatesOfMatterConfig.INITIAL_KINETIC_ENERGY);
    }

    public List getParticles() {
        return Collections.unmodifiableList(particles);
    }

    public int getNumParticles() {
        return particles.size();
    }

    public StatesOfMatterParticle getParticle(int i) {
        return (StatesOfMatterParticle)particles.get(i);
    }

    public void clockTicked(ClockEvent clockEvent) {
        ForceComputation computation = forceEngine.compute((StatesOfMatterParticle[])particles.toArray(new StatesOfMatterParticle[particles.size()]), EngineConfig.TEST);

        Point2D.Double[]  newPositions      = computation.getNewPositions();
        Vector2D.Double[] newVelocities    = computation.getNewVelocities();
        Vector2D.Double[] newAccelerations = computation.getNewAccelerations();

        for (int i = 0; i < getNumParticles(); i++) {
            StatesOfMatterParticle p = getParticle(i);

            p.setX(newPositions[i].getX());
            p.setY(newPositions[i].getY());

            p.setVx(newVelocities[i].getX());
            p.setVy(newVelocities[i].getY());

            p.setAx(newAccelerations[i].getX());
            p.setAy(newAccelerations[i].getY());
        }
    }

    public void clockStarted(ClockEvent clockEvent) {
    }

    public void clockPaused(ClockEvent clockEvent) {
    }

    public void simulationTimeChanged(ClockEvent clockEvent) {
    }

    public void simulationTimeReset(ClockEvent clockEvent) {
    }

    public ParticleContainer getParticleContainer() {
        return new RectangularParticleContainer(StatesOfMatterConfig.CONTAINER_BOUNDS);
    }
}
