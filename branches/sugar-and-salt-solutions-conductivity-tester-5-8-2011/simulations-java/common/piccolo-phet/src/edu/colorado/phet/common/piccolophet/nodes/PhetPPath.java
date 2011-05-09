// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * PhetPPath provides convenient constructors for setting up a PPath.
 * <p/>
 * That is, you can do PPath=new PhetPPath(myShape,fillPaint,stroke,strokePaint);
 * instead of 4 lines of code (as supported by the piccolo API).
 *
 * @author Sam Reid
 * @revision $Revision$
 */

public class PhetPPath extends PPath {

    private static final boolean IS_MAC_OS_10_4 = PhetUtilities.isMacOS_10_4();

    public PhetPPath() {
    }

    public PhetPPath( Shape shape ) {
        super( shape );
    }

    /**
     * Creates a PhetPPath with the specified fill paint and no stroke.
     *
     * @param fill the paint for fill
     */
    public PhetPPath( Paint fill ) {
        setStroke( null );
        setPaint( fill );
    }

    /**
     * Constructs a PhetPPath with the specified stroke and stroke paint, but no fill paint.
     *
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Stroke stroke, Paint strokePaint ) {
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * Constructs a PhetPPath with the specified shape and fill paint, with no stroke.
     *
     * @param shape
     * @param fill
     */
    public PhetPPath( Shape shape, Paint fill ) {
        super( shape );
        setStroke( null );
        setPaint( fill );
    }

    /**
     * Constructs a PhetPPath with the specified shape, stroke and stroke paint, but no fill paint.
     *
     * @param shape
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Shape shape, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * Constructs a PhetPPath with the specified shape, fill paint, stroke and stroke paint.
     *
     * @param shape
     * @param fill
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Shape shape, Paint fill, Stroke stroke, Paint strokePaint ) {
        super( shape );
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * Constructs a PhetPPath with the specified fill paint, stroke and stroke paint.
     *
     * @param fill
     * @param stroke
     * @param strokePaint
     */
    public PhetPPath( Paint fill, Stroke stroke, Paint strokePaint ) {
        setPaint( fill );
        setStroke( stroke );
        setStrokePaint( strokePaint );
    }

    /**
     * WORKAROUND for Gradient Paint bug on Mac OS 10.4.
     * With the default rendering value (VALUE_RENDER_QUALITY), gradient paints will crash.
     * Using VALUE_RENDER_SPEED avoids the problem.
     */
    protected void paint( PPaintContext paintContext ) {
        if ( IS_MAC_OS_10_4 ) {
            boolean usesGradient = ( ( getPaint() instanceof GradientPaint ) || ( getStrokePaint() instanceof GradientPaint ) );
            if ( usesGradient ) {
                Object saveValueRender = paintContext.getGraphics().getRenderingHint( RenderingHints.KEY_RENDERING );
                paintContext.getGraphics().setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
                super.paint( paintContext );
                if ( saveValueRender != null ) {
                    paintContext.getGraphics().setRenderingHint( RenderingHints.KEY_RENDERING, saveValueRender );
                }
            }
            else {
                super.paint( paintContext );
            }
        }
        else {
            super.paint( paintContext );
        }
    }
}
