/**
 * Class: StarshipCoordsGraphic
 * Class: edu.colorado.phet.distanceladder.view
 * User: Ron LeMaster
 * Date: Apr 12, 2004
 * Time: 2:52:50 PM
 */
package edu.colorado.phet.distanceladder.view;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.distanceladder.model.Starship;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class StarshipCoordsGraphic implements Graphic {
    private Starship starship;
    private Container container;
    private Stroke ringStroke = new BasicStroke( 1f );
    private Color graphicColor = Color.white;
    private Ellipse2D.Double ring = new Ellipse2D.Double();
    private Line2D.Double radialLine = new Line2D.Double();
    private double coordRingSpace = 100;
    private double radialLineSpace = Math.PI / 4;


    public StarshipCoordsGraphic( Starship starship, Container container ) {
        this.starship = starship;
        this.container = container;
    }

    public void paint( Graphics2D g ) {
        GraphicsUtil.setAntiAliasingOn( g );
        g.setColor( graphicColor );
        g.setStroke( ringStroke );

        double maxR = Math.max( container.getWidth(), container.getHeight());
        // Draw concentric rings
        for( double r = coordRingSpace;
             r < maxR;
             r += coordRingSpace ) {
            ring.setFrameFromCenter( starship.getLocation().getX(), starship.getLocation().getY(),
                                     starship.getLocation().getX() + r,
                                     starship.getLocation().getY() + r);
            g.draw( ring );
        }

        // Draw radial lines
        for( double theta = 0; theta < Math.PI * 2; theta += radialLineSpace ) {
            radialLine.setLine( starship.getLocation().getX(), starship.getLocation().getY(),
                                starship.getLocation().getX() + maxR * Math.cos( theta ),
                                starship.getLocation().getY() + maxR * Math.sin( theta ));
            g.draw( radialLine );
        }
    }
}
