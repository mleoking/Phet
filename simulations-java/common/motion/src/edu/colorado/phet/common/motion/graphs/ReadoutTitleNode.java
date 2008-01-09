package edu.colorado.phet.common.motion.graphs;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ShadowHTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 8:59:23 PM
 */
public class ReadoutTitleNode extends PNode {
    //8-13-2007: Rotation sim's performance has 50% memory allocation and 30% processor usage in HTMLNode.update
    //Therefore, we just set the HTMLNode once, and update the text in piccolo without swing 
    private ShadowHTMLNode titleNode;
    private ITextNode valueNode;
    private ShadowHTMLNode unitsNode;

    private ControlGraphSeries series;
    private DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
    private PhetPPath background;
    private double insetX = 2;
    private double insetY = 2;

    public static interface ITextNode {

        void setFont( Font titleFont );

        void setTextPaint( Color color );

        void setShadowColor( Color color );

        void setText( String valueText );
    }

    public static class OutlinePText extends PNode implements ITextNode {
        private String text;
        private Font font;
        private FontRenderContext fontRenderContext;
        private PhetPPath render;

        public OutlinePText( PhetPPath render, String text, Font font, FontRenderContext fontRenderContext ) {
            this.render = render;
            this.text = text;
            this.font = font;
            this.fontRenderContext = fontRenderContext;
            addChild( render );
            update();
        }

        void update() {
            TextLayout textLayout = new TextLayout( text, font, fontRenderContext );
            render.setPathTo( textLayout.getOutline( new AffineTransform() ) );
        }

        public void setFont( Font titleFont ) {
            this.font = titleFont;
            update();
        }

        public void setTextPaint( Color color ) {
            render.setPaint( color );
        }

        public void setShadowColor( Color color ) {
            render.setStrokePaint( color );
        }

        public void setText( String valueText ) {
            this.text=valueText;
            update();
        }
    }

    public ReadoutTitleNode( ControlGraphSeries series ) {
        this.series = series;

        titleNode = new ShadowHTMLNode();
        titleNode.setFont( getTitleFont() );
        titleNode.setColor( series.getColor() );

        valueNode = new OutlinePText( new PhetPPath( series.getColor(), new BasicStroke( 1.2f ), Color.black ), "0.00", new PhetDefaultFont(), new FontRenderContext( new AffineTransform(), true, false ) );
        valueNode.setFont( getTitleFont() );
        valueNode.setTextPaint( series.getColor() );

        unitsNode = new ShadowHTMLNode( series.getUnits() );
        unitsNode.setFont( getTitleFont() );
        unitsNode.setColor( series.getColor() );

        if ( isLowRes() ) {
            titleNode.setShadowColor( new Color( 255, 255, 255, 255 ) );
            valueNode.setShadowColor( new Color( 255, 255, 255, 255 ) );
            unitsNode.setShadowColor( new Color( 255, 255, 255, 255 ) );
        }

        background = new PhetPPath( Color.white );
        addChild( background );
        addChild( titleNode );
        addChild( (PNode)valueNode );
        addChild( unitsNode );
        background.translate( insetX, insetY );
        titleNode.translate( insetX, insetY );
        series.getTemporalVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                updateText();
            }
        } );
        series.addListener( new ControlGraphSeries.Adapter() {
            public void unitsChanged() {
                updateText();
            }
        } );

        if ( series.getCharacterName() != null ) {
            titleNode.setHtml( "<html>" + series.getAbbr() + "<sub>" + series.getCharacterName() + "</sub>= " );
        }
        else {
            titleNode.setHtml( series.getAbbr() + "= " );
        }

//        ((PNode)valueNode).setOffset( titleNode.getFullBounds().getWidth() + 3, 3 );
//        ((PNode)valueNode).setOffset( titleNode.getFullBounds().getWidth() + 3, ((PNode)valueNode).getHeight()*2 );
        System.out.println( "((PNode)valueNode).getHeight()*2  = " + ( (PNode) valueNode ).getFullBounds().getHeight() );
//        ((PNode)valueNode).setOffset( titleNode.getFullBounds().getWidth() + 3, 100);
        ((PNode)valueNode).setOffset( titleNode.getFullBounds().getWidth() + 3, ( (PNode) valueNode ).getFullBounds().getHeight()*1.5);
        updateText();
    }

    private Font getTitleFont() {
        return new Font( PhetDefaultFont.getDefaultFontName(), Font.BOLD, isLowRes() ? 12 : 14 );
    }

    private boolean isLowRes() {
        return Toolkit.getDefaultToolkit().getScreenSize().width <= 1024;
    }

    public ControlGraphSeries getSeries() {
        return series;
    }

    protected void updateText() {
        setValueText( decimalFormat.format( getValueToDisplay() ) );
    }

    private void setValueText( String valueText ) {
        valueNode.setText( valueText );
        double maxY = ((PNode)valueNode).getFullBounds().getMaxY();
        unitsNode.setOffset( ((PNode)valueNode).getFullBounds().getMaxX() + 3, maxY - unitsNode.getFullBounds().getHeight() );
        background.setPathTo( RectangleUtils.expand( titleNode.getFullBounds().createUnion( unitsNode.getFullBounds() ), insetX, insetY ) );//todo: avoid setting identical shapes here for performance considerations
    }

    public double getPreferredWidth() {
        setValueText( "MMM.MM" );
        double width = getFullBounds().getWidth();
        updateText();
        return width;
    }

    protected double getValueToDisplay() {
        return series.getTemporalVariable().getValue();
    }

}
