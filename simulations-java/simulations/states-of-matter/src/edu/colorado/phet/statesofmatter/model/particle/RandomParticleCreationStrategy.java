package edu.colorado.phet.statesofmatter.model.particle;

public class RandomParticleCreationStrategy extends AbstractParticleCreationStrategy implements ParticleCreationStrategy {
    private final double radius;
    private final double mass;

    public RandomParticleCreationStrategy(double radius, double mass) {
        this.radius = radius;
        this.mass   = mass;
    }

    public StatesOfMatterParticle createParticle() {
        return new StatesOfMatterParticle(Math.random(), Math.random(), radius, mass);
    }
}
