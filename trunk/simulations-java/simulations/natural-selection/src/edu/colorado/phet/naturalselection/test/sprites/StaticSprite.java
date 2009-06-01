package edu.colorado.phet.naturalselection.test.sprites;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.umd.cs.piccolo.nodes.PPath;

public class StaticSprite extends Sprite {

    private PPath graphics;

    public StaticSprite( Landscape landscape, Point3D position, Color color ) {
        super( landscape, position );

        graphics = new PPath();

        graphics.setStroke( new BasicStroke( 1 ) );
        graphics.setPaint( color );
        graphics.setStrokePaint( Color.BLACK );

        DoubleGeneralPath path = new DoubleGeneralPath();
        path.moveTo( -50, 0 );
        path.lineTo( 0, -50 );
        path.lineTo( 50, 0 );
        path.lineTo( -50, 0 );

        graphics.setPathTo( path.getGeneralPath() );

        addChild( graphics );
    }
}
