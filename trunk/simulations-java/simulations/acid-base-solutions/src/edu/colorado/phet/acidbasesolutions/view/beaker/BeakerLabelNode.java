package edu.colorado.phet.acidbasesolutions.view.beaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Label on the beaker that identifies what is in the beaker.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeakerLabelNode extends PNode {
    
    private static final Color BACKGROUND_COLOR = new Color( 255, 250, 219, 215 );
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 2f );
    private static final Color BACKGROUND_STROKE_COLOR = BACKGROUND_COLOR.darker();
    
    private static final Font HTML_FONT = new PhetFont( Font.BOLD, 18 );
    private static final Color HTML_COLOR = Color.BLACK;
    
    private static final double MARGIN = 10;
    
    private final PPath backgroundNode;
    private final HTMLNode htmlNode;
    
    public BeakerLabelNode( PDimension size ) {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // background
        backgroundNode = new PPath();
        backgroundNode.setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ) );
        backgroundNode.setPaint( BACKGROUND_COLOR );
        backgroundNode.setStroke( BACKGROUND_STROKE );
        backgroundNode.setStrokePaint( BACKGROUND_STROKE_COLOR );
        addChild( backgroundNode );
        
        // text
        htmlNode = new HTMLNode( "<html>default label name</html>" );
        htmlNode.setFont( HTML_FONT );
        htmlNode.setHTMLColor( HTML_COLOR );
        addChild( htmlNode );
        
        updateLayout();
    }
    
    public void setHTML( String html ) {
        htmlNode.setHTML( HTMLUtils.toHTMLString( html ) );
        updateLayout();
    }
    
    private void updateLayout() {
        // scale the html, if necessary
        PBounds bb = backgroundNode.getFullBoundsReference();
        PBounds hb = htmlNode.getFullBoundsReference();
        double scaleX = Math.min( 1.0, ( bb.getWidth() - 2 * MARGIN ) / hb.getWidth() );
        double scaleY = Math.min( 1.0, ( bb.getHeight() - 2 * MARGIN ) / hb.getHeight() );
        double scale = Math.min( scaleX, scaleY );
        htmlNode.scale( scale );
        // center html in background
        hb = htmlNode.getFullBoundsReference();
        double xOffset = ( bb.getWidth() - hb.getWidth() ) / 2;
        double yOffset = ( bb.getHeight() - hb.getHeight() ) / 2;
        htmlNode.setOffset( xOffset, yOffset );
    }

}
