/**
 * Class: BoxDoorGraphic
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 10, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BoxDoorGraphic extends PhetGraphic implements SimpleObserver {
//public class BoxDoorGraphic extends DefaultInteractiveGraphic implements SimpleObserver {
    private int x;
    private int y;
    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private Box2D box;
    private PhetImageGraphic imageGraphic;
    private double openingMaxX;
    private boolean doorHighlighted;
    private Point2D[] opening = new Point2D[2];

    public BoxDoorGraphic( Component component,
                           int x, int y, int minX, int minY, int maxX, int maxY,
                           Box2D box ) {
        super( component );
        BufferedImage doorImg = null;
        try {
            doorImg = ImageLoader.loadBufferedImage( IdealGasConfig.DOOR_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        imageGraphic = new PhetImageGraphic( component, doorImg );
//        super.setBoundedGraphic( imageGraphic );
        this.x = x;
        this.y = y;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.box = box;
        this.openingMaxX = x + imageGraphic.getBounds().getWidth();
        box.addObserver( this );

        setCursor( Cursor.getPredefinedCursor( Cursor.E_RESIZE_CURSOR ) );
        addTranslationListener( new DoorTranslator() );
    }

    public void fireMouseEntered( MouseEvent e ) {
        super.fireMouseEntered( e );
        doorHighlighted = true;
    }

    public void fireMouseExited( MouseEvent e ) {
        super.fireMouseExited( e );
        doorHighlighted = false;
    }

    private class DoorTranslator implements TranslationListener {
        public void translationOccurred( TranslationEvent event ) {
            translateDoor( event.getDx(), event.getDy() );
            box.setOpening( opening );
        }

        public DoorTranslator() {
            translate( 0, 0 );
        }
    }


    public void translateDoor( double dx, double dy ) {

        minX = (int)( box.getMinX() - imageGraphic.getBounds().getWidth() + ( box.getMaxX() - openingMaxX ) );
        // Update the position of the image on the screen
        x = (int)Math.min( maxX, Math.max( minX, x + dx ) );
        y = (int)Math.min( maxY, Math.max( minY, y + dy ) );
        imageGraphic.setLocation( x, y - (int)imageGraphic.getBounds().getHeight() );

        // Update the box's openinng
        opening[0] = new Point2D.Double( x + imageGraphic.getBounds().getWidth(),
                                         box.getMinY() );
        opening[1] = new Point2D.Double( openingMaxX,
                                         box.getMinY() );
    }

    public void update() {
        if( minY != (int)box.getMinY() || minX != (int)box.getMinX() ) {
            translateDoor( 0, 0 );
            minX = (int)box.getMinX();
            minY = (int)box.getMinY();
            maxY = (int)box.getMinY();
            imageGraphic.setLocation( (int)imageGraphic.getBounds().getMinX(),
                                      minY - (int)imageGraphic.getBounds().getHeight() );
            imageGraphic.repaint();
        }
    }

    public void paint( Graphics2D g ) {
//        super.paint( g );
        imageGraphic.paint( g );
        if( doorHighlighted ) {
            GraphicsState gs = new GraphicsState( g );
            g.setStroke( new BasicStroke( 1 ) );
            g.setColor( Color.red );
            g.draw( this.imageGraphic.getBounds() );
            gs.restoreGraphics();
        }
    }

    protected Rectangle determineBounds() {
        return imageGraphic.getBounds();
    }
}
