/**
 * Class: SphereHollowSphereExpert
 * Package: edu.colorado.phet.collision
 * Author: Another Guy
 * Date: Sep 21, 2004
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.idealgas.model.HollowSphere;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.SphericalBody;

public class SphereHollowSphereExpert implements CollisionExpert {

    private ContactDetector detector = new SphereHollowSphereContactDetector();
    private IdealGasModel model;
    private double dt;

    public SphereHollowSphereExpert( IdealGasModel model, double dt ) {
        this.model = model;
        this.dt = dt;
    }

    public boolean detectAndDoCollision( CollidableBody bodyA, CollidableBody bodyB ) {
        boolean haveCollided = false;
        if( detector.applies( bodyA, bodyB ) && detector.areInContact( bodyA, bodyB ) ) {
            Collision collision = new SphereSphereCollision( (HollowSphere)bodyA,
                                                             (SphericalBody)bodyB );
            collision.collide();
            haveCollided = true;
        }

        // Check containment
        if( detector.applies( bodyA, bodyB ) ) {
            HollowSphere hollowSphere = null;
            SphericalBody sphere = null;
            if( bodyA instanceof HollowSphere ) {
                hollowSphere = (HollowSphere)bodyA;
                sphere = (SphericalBody)bodyB;
            }
            else {
                hollowSphere = (HollowSphere)bodyB;
                sphere = (SphericalBody)bodyA;
            }
            double dist = hollowSphere.getPosition().distance( sphere.getPosition() );
            if( hollowSphere.containsBody( sphere )) {
                if( dist + sphere.getRadius() > hollowSphere.getRadius() ) {
                    Collision collision = new SphereSphereCollision( (HollowSphere)bodyA,
                                                                     (SphericalBody)bodyB );
                    collision.collide();
                    haveCollided = true;
                }
            }
            else {
                if( dist - sphere.getRadius() < hollowSphere.getRadius() ) {
                    Collision collision = new SphereSphereCollision( (HollowSphere)bodyA,
                                                                     (SphericalBody)bodyB );
                    collision.collide();
                    haveCollided = true;
                }
            }                        
        }
        return haveCollided;
    }
}