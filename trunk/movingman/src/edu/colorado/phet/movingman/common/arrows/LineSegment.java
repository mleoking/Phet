/*PhET, 2004.*/
package edu.colorado.phet.movingman.common.arrows;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 24, 2003
 * Time: 3:22:27 PM
 * Copyright (c) Jun 24, 2003 by Sam Reid
 */
public class LineSegment {
    Stroke stroke;
    Color c;

    public LineSegment( Stroke stroke, Color c ) {
        this.stroke = stroke;
        this.c = c;
    }

    public void paint( Graphics2D g, int x, int y, int x2, int y2 ) {
        Stroke origStroke = g.getStroke();
        g.setColor( c );
        g.setStroke( stroke );
        g.drawLine( x, y, x2, y2 );
        g.setStroke( origStroke );
    }
}
