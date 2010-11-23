/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Creates 2D projections of shapes that are related to the 3D voltmeter model.
 * Shapes are in the global view coordinate frame, everything else is in model coordinates.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterShapeFactory {
    
    /*
     * Sizes determined by visual inspection of the associated image files.
     * To see the corresponding bounds, run the sim with -dev.
     */
    private final PDimension PROBE_TIP_SIZE = new PDimension( 0.0005, 0.0015 );
    
    private final Voltmeter voltmeter;
    private final CLModelViewTransform3D mvt;
    
    public VoltmeterShapeFactory( Voltmeter voltmeter, CLModelViewTransform3D mvt ) {
        this.voltmeter = voltmeter;
        this.mvt = mvt;
    }

    /**
     * Gets the shape of the positive probe's tip in the world coordinate frame.
     * @return
     */
    public Shape getPositiveProbeTipShape() {
        return getProbeTipShape( voltmeter.getPositiveProbeLocationReference(), -mvt.getYaw() );
    }
    
    /**
     * Gets the shape of the negative probe's tip in the world coordinate frame.
     * @return
     */
    public Shape getNegativeProbeTipShape() {
        return getProbeTipShape( voltmeter.getNegativeProbeLocationReference(), -mvt.getYaw() );
    }
    
    /*
     * Gets the shape of a probe tip relative to some specified origin.
     */
    private Shape getProbeTipShape( Point3D origin, double theta ) {
        double x = origin.getX() - ( PROBE_TIP_SIZE.getWidth() / 2 );
        double y = origin.getY();
        Rectangle2D r = new Rectangle2D.Double( x, y, PROBE_TIP_SIZE.getWidth(), PROBE_TIP_SIZE.getHeight() );
        AffineTransform t = AffineTransform.getRotateInstance( theta, origin.getX(), origin.getY() );
        Shape s = t.createTransformedShape( r );
        return mvt.modelToView( s );
    }
}
