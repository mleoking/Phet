/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.phetgraphics;

import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PhetImageGraphic
 *
 * @author ?
 * @version $Revision$
 */
public class PhetImageGraphic extends PhetGraphic {
    private BufferedImage image;
    private boolean shapeDirty = true;
    private Shape shape;
    private String imageResourceName;

    public PhetImageGraphic( Component component ) {
        this( component, null, 0, 0 );
    }

    public PhetImageGraphic( Component component, String imageResourceName ) {
        this( component, (BufferedImage)null );
        this.imageResourceName = imageResourceName;

        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Image resource not found: " + imageResourceName );
        }
        setImage( bufferedImage );
    }

    public PhetImageGraphic( Component component, BufferedImage image ) {
        this( component, image, 0, 0 );
    }

    public PhetImageGraphic( Component component, BufferedImage image, int x, int y ) {
        super( component );
        this.image = image;
        setLocation( x, y );
    }

    public Shape getShape() {
        AffineTransform transform = getNetTransform();
        if( shapeDirty ) {
            if( image == null ) {
                return null;
            }
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            this.shape = transform.createTransformedShape( rect );
            shapeDirty = false;
        }
        return shape;
    }

    public boolean contains( int x, int y ) {
        return isVisible() && getShape() != null && getShape().contains( x, y );
    }

    protected Rectangle determineBounds() {
        return getShape() == null ? null : getShape().getBounds();
    }

    public void paint( Graphics2D g2 ) {
        if( isVisible() && image != null ) {
            Shape origClip = g2.getClip();
            super.saveGraphicsState( g2 );
            super.applyClip( g2 );
            RenderingHints hints = getRenderingHints();
            if( hints != null ) {
                g2.setRenderingHints( hints );
            }
            g2.drawRenderedImage( image, getNetTransform() );
            super.restoreGraphicsState();
            super.setClip( origClip );
        }
    }

    public void setBoundsDirty() {
        super.setBoundsDirty();
        shapeDirty = true;
    }

    public void setImage( BufferedImage image ) {
        if( this.image != image ) {
            this.image = image;
            setBoundsDirty();
            autorepaint();
        }
    }

    public BufferedImage getImage() {
        return image;
    }


    ///////////////////////////////////////////////////
    // Persistence support
    //

    public PhetImageGraphic() {
        // noop
    }

    public String getImageResourceName() {
        return imageResourceName;
    }

    public void setImageResourceName( String imageResourceName ) {
        this.imageResourceName = imageResourceName;
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageLoader.loadBufferedImage( imageResourceName );
        }
        catch( IOException e ) {
            throw new RuntimeException( "Image resource not found: " + imageResourceName );
        }
        setImage( bufferedImage );
    }
}
