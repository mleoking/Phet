/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Area;

/**
 * User: Sam Reid
 * Date: Jul 17, 2004
 * Time: 7:47:12 PM
 * Copyright (c) Jul 17, 2004 by Sam Reid
 */
public class PhetTooltipGraphic extends CompositePhetGraphic {
    PhetShapeGraphic backgroundGraphic;
    PhetTextGraphic textGraphic;

    public PhetTooltipGraphic( Component component, String text ) {
        super( component );
        Color background = new Color( 160, 160, 250 );
        backgroundGraphic = new PhetShapeGraphic( component, new Area(), background, new BasicStroke( 1 ), Color.black );
        textGraphic = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), text, Color.black, 0, 0 );
        addGraphic( backgroundGraphic );
        addGraphic( textGraphic );
    }

    public void setPosition( int x, int y ) {
        textGraphic.setPosition( x, y );
        backgroundGraphic.setShape( RectangleUtils.expand( textGraphic.getBounds(), 4, 4 ) );
    }
}
