/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.circuit.components.Bulb;
import edu.colorado.phet.cck3.circuit.components.SchematicResistorGraphic;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 20, 2004
 * Time: 11:45:11 PM
 * Copyright (c) Jun 20, 2004 by Sam Reid
 */
public class SchematicBulbGraphic extends SchematicResistorGraphic {
    private Bulb bulb;
    FastPaintShapeGraphic shapeGraphic;

    public SchematicBulbGraphic( Component parent, Bulb bulb, ModelViewTransform2D transform, double wireThickness ) {
        super( parent, bulb, transform, wireThickness );
        this.bulb = bulb;
        shapeGraphic = new FastPaintShapeGraphic( null, Color.black, new BasicStroke( 3 ), parent );
        changed();
    }

    protected void changed() {
        super.changed();
        if( shapeGraphic != null ) {
            //draw a circle around the resistor part.
            Point2D catPoint = super.getCatPoint();
            Point2D anoPoint = getAnoPoint();
            double dist = catPoint.distance( anoPoint );
            double radius = dist / 2;
            Vector2D vec = new Vector2D.Double( catPoint, anoPoint );
            Point2D ctr = vec.getScaledInstance( .5 ).getDestination( catPoint );
            Ellipse2D.Double ellipse = new Ellipse2D.Double();
            Point2D corner = new Vector2D.Double( radius, radius ).getDestination( ctr );
            ellipse.setFrameFromCenter( ctr, corner );
            shapeGraphic.setShape( ellipse );
        }
    }

    public void paint( Graphics2D g ) {
        super.paint( g );
        shapeGraphic.paint( g );
    }

    public boolean contains( int x, int y ) {
        return shapeGraphic.getShape().contains( x, y );
    }
}
