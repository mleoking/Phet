// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

/**
 * See http://www.ni-c.de/2008/10/serializable-bufferedimage/
 */
public class SerializableBufferedImage implements Serializable {

    private byte[] byteImage = null;

    public SerializableBufferedImage( BufferedImage bufferedImage ) {
        this.byteImage = toByteArray( bufferedImage, "JPG" );
        byte[] png = toByteArray( bufferedImage, "PNG" );
        System.out.println( "jpg.length = " + byteImage.length + ", png.length = " + png.length );
        //        ARGB: jpg.length = 10907, png.length = 16049
        //        RGB: jpg.length = 9014, png.length = 14667
    }

    public BufferedImage getBufferedImage() {
        return fromByteArray( byteImage );
    }

    private BufferedImage fromByteArray( byte[] imagebytes ) {
        try {
            if ( imagebytes != null && ( imagebytes.length > 0 ) ) {
                BufferedImage im = ImageIO.read( new ByteArrayInputStream( imagebytes ) );
                return im;
            }
            return null;
        }
        catch ( IOException e ) {
            throw new IllegalArgumentException( e.toString() );
        }
    }

    private byte[] toByteArray( BufferedImage bufferedImage, String format ) {
        if ( bufferedImage != null ) {
            BufferedImage image = bufferedImage;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write( image, format, baos );
            }
            catch ( IOException e ) {
                throw new IllegalStateException( e.toString() );
            }
            byte[] b = baos.toByteArray();
            return b;
        }
        return new byte[0];
    }
}
