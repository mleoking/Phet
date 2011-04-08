// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;

/**
 * Models the intersection between a light ray and an interface, needed so we can optionally depict normals at each intersection.
 *
 * @author Sam Reid
 */
public class Intersection {
    private ImmutableVector2D unitNormal;
    private ImmutableVector2D point;
    private ArrayList<VoidFunction0> cleanupListeners = new ArrayList<VoidFunction0>();

    public Intersection( ImmutableVector2D unitNormal, ImmutableVector2D point ) {
        this.unitNormal = unitNormal;
        this.point = point;
    }

    public ImmutableVector2D getPoint() {
        return point;
    }

    public ImmutableVector2D getUnitNormal() {
        return unitNormal;
    }

    public void addCleanupListener( VoidFunction0 voidFunction0 ) {
        cleanupListeners.add( voidFunction0 );
    }

    public void remove() {
        for ( VoidFunction0 cleanupListener : cleanupListeners ) {
            cleanupListener.apply();
        }
    }
}
