/**
 * Class: AtomAtomCollision
 * Package: edu.colorado.phet.lasers.physics.collision
 * Author: Another Guy
 * Date: Mar 21, 2003
 */
package edu.colorado.phet.lasers.physics.collision;

import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.HardsphereCollision;
import edu.colorado.phet.physics.collision.CollisionFactory;
import edu.colorado.phet.physics.collision.Collision;

public class AtomAtomCollision extends HardsphereCollision {

    private Atom atom1;
    private Atom atom2;

    /**
     * Provided so class can register a prototype with the CollisionFactory
     */
    private AtomAtomCollision() {
        //NOP
    }

    public AtomAtomCollision( Atom atom1, Atom atom2 ) {
        this.atom1 = atom1;
        this.atom2 = atom2;
    }

    protected Vector2D getLoa( Particle particleA, Particle particleB ) {
        Vector2D posA = particleA.getPosition();
        Vector2D posB = particleB.getPosition();
        return new Vector2D( posA.getX() - posB.getX(),
                             posA.getY() - posB.getY() );
    }

    //
    // Abstract methods
    //
    public void collide() {
        super.collide( atom1, atom2, getLoa( atom1, atom2 ) );
    }

    /**
     *
     * @param particleA
     * @param particleB
     * @return
     */
    public Collision createIfApplicable( Particle particleA, Particle particleB ) {
        Collision result = null;
        if( particleA instanceof Atom && particleB instanceof Atom ) {
            result = new AtomAtomCollision( (Atom)particleA, (Atom)particleB );
        }
        return result;
    }

    //
    // Static fields and methods
    //
    static public void register() {
        CollisionFactory.addPrototype( new AtomAtomCollision() );
    }
}
