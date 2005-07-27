// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.semiconductor.common.ArrowShape;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class EnergyTextGraphic extends TransformGraphic {

    public EnergyTextGraphic( ModelViewTransform2D modelviewtransform2d, PhetVector phetvector ) {
        super( modelviewtransform2d );
        loc = phetvector;
        text = SimStrings.get( "EnergyTextGraphic.EnergyText" );
        font = new Font( "Lucida Sans", 0, 36 );
        smallFont = new Font( "Dialog", 0, 18 );
    }

    public void paint( Graphics2D graphics2d ) {
        graphics2D = graphics2d;
        if( trfShape == null ) {
            recompute();
        }
        graphics2d.setColor( Color.blue );
        graphics2d.fill( trfShape );
        graphics2d.fill( arrowShape );
        graphics2d.setColor( Color.black );
        graphics2d.fill( lowShape );
        graphics2d.fill( highShape );
    }

    private PhetVector getTopCenter( Rectangle2D rectangle2d ) {
        return new PhetVector( rectangle2d.getX() + rectangle2d.getWidth() / 2D, rectangle2d.getY() );
    }

    public void recompute() {
        GlyphVector glyphvector = font.createGlyphVector( graphics2D.getFontRenderContext(), text );
        Shape shape = glyphvector.getOutline();
        Point point = getTransform().modelToView( loc );
        AffineTransform affinetransform = new AffineTransform();
        affinetransform.translate( point.x - 15, point.y );
        affinetransform.rotate( -1.5707963267948966D );
        trfShape = affinetransform.createTransformedShape( shape );
        PhetVector phetvector = getTopCenter( trfShape.getBounds2D() );
        phetvector = phetvector.getSubtractedInstance( 0.0D, 40D );
        PhetVector phetvector1 = phetvector.getAddedInstance( 0.0D, -200D );
        arrowShape = ( new ArrowShape( phetvector, phetvector1, 50D, 50D, 20D ) ).getArrowPath();
        highShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(),
                        SimStrings.get( "EnergyTextGraphic.HighText" ) ).getOutline( (float)phetvector1.getX() - 20F,
                        (float)phetvector1.getY() - 20F );
        lowShape = smallFont.createGlyphVector( graphics2D.getFontRenderContext(),
                        SimStrings.get( "EnergyTextGraphic.LowText" ) ).getOutline( (float)phetvector.getX() - 20F,
                        (float)phetvector.getY() + 20F );
    }

    public void update() {
        trfShape = null;
    }

    private Font font;
    private String text;
    private PhetVector loc;
    private Shape trfShape;
    private Shape arrowShape;
    private Graphics2D graphics2D;
    private Font smallFont;
    private Shape highShape;
    private Shape lowShape;
}
