/**
 * Class: Thermometer
 * Package: edu.colorado.phet.instrumentation
 * Author: Another Guy
 * Date: Sep 29, 2004
 */
package edu.colorado.phet.instrumentation;

import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Thermometer extends PhetGraphic /*extends AbstractGauge*/ {
    private static Color color = Color.red;
    private BarGauge gauge;
    private Ellipse2D.Double bulb;
    private NumberFormat formatter = new DecimalFormat( "#0.00" );
    private Point2D location;
    private double scale;
    private double maxScreenLevel;
    private double thickness;
    private double min;
    private double max;
    double value;
    private double numMaj;
    private double numMin;
    private Rectangle2D boundingRect;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 10 );
    private FontMetrics fontMetrics;
    private int rectBorderThickness = 3;

    public void setMin( double min ) {
        this.min = min;
    }

    public void setMax( double max ) {
        this.max = max;
    }


    public void setNumMaj( double numMaj ) {
        this.numMaj = numMaj;
    }

    public void setNumMin( double numMin ) {
        this.numMin = numMin;
    }


    public Thermometer( Component component, Point2D.Double location, double maxScreenLevel, double thickness,
                        boolean isVertical, double minLevel, double maxLevel ) {

        super( component );
        gauge = new BarGauge( location, maxScreenLevel, color, thickness, isVertical,
                              minLevel, maxLevel );
        bulb = new Ellipse2D.Double( location.x - thickness / 2, location.y + maxScreenLevel - thickness * 0.1,
                                     thickness * 2, thickness * 2 );
        this.location = location;
        this.thickness = thickness;
        scale = maxScreenLevel / maxLevel;
        this.maxScreenLevel = maxScreenLevel;
        fontMetrics = component.getFontMetrics( font );
        int readoutWidth = fontMetrics.stringWidth( "XXXXXXX" );
        boundingRect = new Rectangle2D.Double( location.getX(), location.getY(),
                                               readoutWidth + rectBorderThickness,
                                               maxScreenLevel + bulb.getHeight() );
    }

    public void setValue( double value ) {
        gauge.setLevel( Double.isNaN( value ) ? 0 : value );
        this.value = value;
    }

    public void paint( Graphics2D g ) {
        GraphicsState gs = new GraphicsState( g );
        GraphicsUtil.setAntiAliasingOn( g );
        g.setFont( font );

        int readoutHeight = fontMetrics.getHeight() + fontMetrics.getMaxDescent();
        int readoutWidth = fontMetrics.stringWidth( "XXXXXXX" );
        int yLoc = (int)( location.getY() + maxScreenLevel - readoutHeight - value * scale );

        RoundRectangle2D.Double rect = new RoundRectangle2D.Double( location.getX() + thickness,
                                                                    yLoc - rectBorderThickness,
                                                                    readoutWidth + rectBorderThickness * 2,
                                                                    readoutHeight + rectBorderThickness * 2,
                                                                    4, 4 );
        RoundRectangle2D.Double innerRect = new RoundRectangle2D.Double( location.getX() + thickness + 3,
                                                                         yLoc,
                                                                         readoutWidth, readoutHeight,
                                                                         4, 4 );
        g.setColor( Color.yellow );
        g.setStroke( new BasicStroke( 2f ) );
        g.draw( rect );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 0.3f ) );
        g.fill( rect );
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
        g.setColor( Color.white );
        g.fill( innerRect );

        double v = Double.isNaN( value ) ? 0 : value / 1000;
        String temperatureStr = formatter.format( v );
        g.setColor( Color.black );
        int strLocY = (int)innerRect.getMinY() + fontMetrics.getHeight();
        g.drawString( temperatureStr, (int)innerRect.getMaxX() - 5 - fontMetrics.stringWidth( temperatureStr ), strLocY );


        g.setStroke( new BasicStroke( 0.5f ) );
        gauge.paint( g );
        g.setColor( color );
        g.fill( bulb );
        g.setColor( Color.black );
        g.draw( bulb );
        gs.restoreGraphics();
    }

    protected Rectangle determineBounds() {
        return RectangleUtils.toRectangle( boundingRect );
    }
}
