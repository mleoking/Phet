// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.util;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * MouseTracker for Piccolo-based simulations
 *
 * @author Ron LeMaster
 */
public class PMouseTracker extends PNode {

    private PText coords;
    private PNode crosshairs = new PNode();

    public PMouseTracker( PhetPCanvas canvas ) {
        PPath horizontalLine = new PPath( new Line2D.Double( -1000, 0, 1000, 0 ) );
        PPath verticalLine = new PPath( new Line2D.Double( 0, -1000, 0, 1000 ) );
        horizontalLine.setStrokePaint( Color.red );
        verticalLine.setStrokePaint( Color.red );
        crosshairs.addChild( horizontalLine );
        crosshairs.addChild( verticalLine );
        addChild( crosshairs );

        coords = new PText();
        coords.setOffset( 10, -20 );
        addChild( coords );

        canvas.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseMoved( PInputEvent event ) {
                setOffset( event.getPosition() );
                coords.setText( "[" + event.getPosition().getX() + ":" + event.getPosition().getY() + "]" );
                repaint();
            }
        } );
    }


}