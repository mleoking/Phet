package phet.paint;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtils {
    public static BufferedImage scaleToSizeApproximate( BufferedImage root, int x, int y ) {
        double width = root.getWidth();
        double height = root.getHeight();

        double scaleX = x / width;
        double scaleY = y / height;
        return scale( root, scaleX, scaleY );
    }

    public static BufferedImage transform( BufferedImage root, AffineTransform at ) {
        AffineTransformOp ato = new AffineTransformOp( at, AffineTransformOp.TYPE_BILINEAR );
        BufferedImage out = ato.filter( root, null );
        return out;
    }

    public static BufferedImage scale( BufferedImage root, double scale ) {
        return scale( root, scale, scale );
    }

    public static BufferedImage scale( BufferedImage root, double scaleX, double scaleY ) {
        AffineTransformOp ato = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
        BufferedImage out = ato.filter( root, null );
        return out;
    }
}

