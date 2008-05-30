package edu.colorado.phet.statesofmatter.model.engine;

import edu.colorado.phet.common.phetcommon.patterns.PubliclyCloneable;
import edu.colorado.phet.statesofmatter.StatesOfMatterConstants;
import edu.colorado.phet.statesofmatter.model.container.ParticleContainer;
import edu.colorado.phet.statesofmatter.model.container.RectangularParticleContainer;

public class EngineConfig implements PubliclyCloneable {
    public static final EngineConfig TEST = new EngineConfig(
        StatesOfMatterConstants.GRAVITY,
        new RectangularParticleContainer(StatesOfMatterConstants.CONTAINER_BOUNDS),
        StatesOfMatterConstants.EPSILON,
        StatesOfMatterConstants.RMIN,
        StatesOfMatterConstants.DELTA_T
    );

    public double gravity;
    public ParticleContainer container;
    public double epsilon;
    public double rMin;
    public double deltaT;

    public EngineConfig(double gravity, ParticleContainer container, double epsilon, double rmin, double deltaT) {
        this.gravity   = gravity;
        this.container = container;
        this.epsilon   = epsilon;
        this.rMin      = rmin;
        this.deltaT    = deltaT;
    }


    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }
}
