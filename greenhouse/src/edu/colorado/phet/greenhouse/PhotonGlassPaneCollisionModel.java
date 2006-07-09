/**
 * Class: PhotonGlassPaneCollisionModel
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 30, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.coreadditions.Vector3D;
import edu.colorado.phet.filter.BandpassFilter;
import edu.colorado.phet.filter.Filter1D;

public class PhotonGlassPaneCollisionModel {
    private static Vector2D n = new Vector2D();
    private static Vector2D vRel = new Vector2D();
    private static Vector2D r1 = new Vector2D();
    private static Vector3D nj = new Vector3D();
    private static Vector2D result = new Vector2D();
    private static Filter1D irFilter = new BandpassFilter( 800E-9, 1500E-9 );

    public static void handle( Photon photon, GlassPane glassPane ) {

        // Do bounding box check
        boolean boundingBoxesOverlap = glassPane.getBounds().contains( photon.getLocation() );
        if( boundingBoxesOverlap ) {
            if( irFilter.passes( photon.getWavelength() ) ) {
                doScatter( photon, glassPane );
            }
        }
    }

    static int up = 0;
    static int down = 0;

    private static void doScatter( Photon photon, GlassPane glassPane ) {
        // Scatter the photon in a random direction
        double dispersionAngle = Math.PI / 2;
        double theta = Math.random() * dispersionAngle + ( Math.PI * 3 / 2 ) - ( dispersionAngle / 2 );
        theta += Math.random() < 0.5 ? 0 : Math.PI;
        float vBar = photon.getVelocity().getMagnitude();
        Photon newPhoton = new Photon( photon.getWavelength(), glassPane );
        newPhoton.setVelocity( vBar * (float)Math.cos( theta ),
                               vBar * (float)Math.sin( theta ) );
        double y = glassPane.getBounds().getY();
        if( theta % ( 2 * Math.PI ) < Math.PI ) {
            y = glassPane.getBounds().getMaxY() + 0.5;
        }
        else {
            y = glassPane.getBounds().getMinY() - 1;
        }
        newPhoton.setLocation( Math.random() * glassPane.getWidth() + glassPane.getBounds().getX(), y );

        glassPane.absorbPhoton( photon );
        glassPane.emitPhoton( newPhoton );
    }
}
