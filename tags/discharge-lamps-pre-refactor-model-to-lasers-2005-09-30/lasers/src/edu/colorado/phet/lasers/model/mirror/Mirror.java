/**
 * Class: Mirror
 * Package: edu.colorado.phet.lasers.model.mirror
 * Author: Another Guy
 * Date: Apr 3, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.model.mirror;

import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * This class represents various sorts of mirrors. The mirror is conditioned
 * by ReflectionStrategies that are added to it that determine whether the
 * mirror will reflect a particular photon.
 * <p/>
 * Examples of ReflectionStrategies are
 * <ul>
 * <li>LeftReflecting
 * <li>RightReflecting
 * <li>BandPass
 * </ul>
 */
public class Mirror extends Wall {
    // Implementation of specific reflection behaviors
    protected ArrayList reflectionStrategies = new ArrayList();

    public Mirror( Point2D end1, Point2D end2 ) {
        super( new Rectangle2D.Double( end1.getX(), end1.getY(),
                                       end2.getX() - end1.getY(),
                                       end2.getY() - end1.getY() ) );
        setPosition( end1 );
    }

    public void addReflectionStrategy( ReflectionStrategy strategy ) {
        reflectionStrategies.add( strategy );
    }

    /**
     * Tells if the mirror reflects a specified photon, based on the mirror's
     * ReflectionStrategies.
     *
     * @param photon
     * @return
     */
    public boolean reflects( Photon photon ) {
        boolean result = true;
        for( int i = 0; i < reflectionStrategies.size() && result == true; i++ ) {
            ReflectionStrategy reflectionStrategy = (ReflectionStrategy)reflectionStrategies.get( i );
            result &= reflectionStrategy.reflects( photon );
        }
        return result;
    }
}
