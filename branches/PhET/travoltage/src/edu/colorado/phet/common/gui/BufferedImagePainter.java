package edu.colorado.phet.common.gui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class BufferedImagePainter implements Painter {
    BufferedImage bi;
    AffineTransform at;

    public BufferedImagePainter( BufferedImage bi ) {
        this( bi, new AffineTransform() );
    }

    public BufferedImagePainter( BufferedImage bi, AffineTransform at ) {
        this.bi = bi;
        this.at = at;
    }

    public void paint( Graphics2D g ) {
        g.drawRenderedImage( bi, at );
    }
}


