/**
 * Class: Rubidium
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import java.awt.geom.Point2D;

public class Rubidium extends Nucleus {
    private static final int numNeutrons = 48;
    private static final int numProtons = 37;

    public Rubidium( Point2D.Double position ) {
        super( position, numNeutrons, numProtons );
    }
}
