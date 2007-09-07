package edu.colorado.phet.statesofmatter.view;

import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.statesofmatter.model.particle.StatesOfMatterParticle;
import edu.umd.cs.piccolo.PNode;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ParticleNode extends PNode implements Updatable {
    private final StatesOfMatterParticle particle;
    private final PhetPPath path;

    public ParticleNode(StatesOfMatterParticle particle) {
        this.particle = particle;
        this.path=new PhetPPath(Color.blue);
        addChild(path);
        update();
    }

    public void update() {
        setX(particle.getX());
        setY(particle.getY());

        double length =  2 * particle.getRadius();

        Ellipse2D.Double circle = new Ellipse2D.Double(-particle.getRadius(), -particle.getRadius(), length, length);

        path.setPathTo(circle);
    }
}
