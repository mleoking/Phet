/**
 * Class: DialGauge
 * Class: edu.colorado.phet.sound.view
 * User: Ron LeMaster
 * Date: Sep 8, 2004
 * Time: 7:39:47 AM
 */
package edu.colorado.phet.gauges;

import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.ScalarObservable;
import edu.colorado.phet.coreadditions.ScalarObserver;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;

// todo: add min and max lines and legends on face
// todo: add min and max limits to needle
// todo: add ScalarObservable interface and ScalarObserver interface

public class DialGauge extends CompositeGraphic implements ScalarObserver {

    private ScalarObservable dataSource;
    private String title;
    private String units;
    private Component component;
    private double x;
    private double y;
    private double diam;
    private double min;
    private double max;
    private NeedleGraphic needleGraphic;
    private FaceGraphic faceGraphic;
    private static Font s_defaultFont = new Font( "Lucida Sans", Font.BOLD, 8 );
    private Font font;
    private NumberFormat formatter = new DecimalFormat( "#0.0" );

    private double needleLength = 0.5;
    private double datum;
    private Color backgroundColor = new Color( 245, 255, 250 );

    public DialGauge( ScalarObservable dataSource, Component component,
                      double x, double y, double diam, double min, double max,
                      String title, String units ) {
        this( dataSource, component, x, y, diam, min, max, title, units, s_defaultFont );
    }

    public DialGauge( ScalarObservable dataSource, Component component,
                      double x, double y, double diam, double min, double max,
                      String title, String units, Font font ) {
        this.dataSource = dataSource;
        this.title = title;
        this.units = units;
        this.font = font;
        dataSource.addObserver( this );
        this.component = component;
        this.x = x;
        this.y = y;
        this.diam = diam;
        this.min = min;
        this.max = max;
        faceGraphic = new FaceGraphic();
        this.addGraphic( faceGraphic );
        needleGraphic = new NeedleGraphic();
        this.addGraphic( needleGraphic );

        update();
    }

    public void update() {
        datum = dataSource.getValue();
        double needleDatum = Math.max( Math.min( datum, max ), min );
        double p = ( max - needleDatum ) / ( max - min );
        double theta = -( ( Math.PI * 5 / 4 ) + ( Math.PI * 3 / 2 ) * p ) - Math.PI / 2;
        needleGraphic.update( theta );
        faceGraphic.repaint();
    }

    public void setBackground( Color color ) {
        backgroundColor = color;
    }

    private class NeedleGraphic extends PhetShapeGraphic {
        private Rectangle.Double needle;
        // Ratio of needle on either side of pivot point
        private double r = 0.2;
        private double l;
        private AffineTransform needleTx;
        private Ellipse2D.Double pivot = new Ellipse2D.Double();

        NeedleGraphic() {
            super( component, null, Color.red );
            needle = new Rectangle2D.Double();
            l = diam * needleLength;
//            l = diam * .5;
            needle.setRect( x - l * r, y - 1, l, 2 );
            super.setShape( needle );
        }

        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            GraphicsUtil.setAntiAliasingOn( g );
            g.transform( needleTx );
            super.paint( g );
            g.setColor( Color.black );
            g.fill( pivot );
            restoreGraphicsState();
        }

        void update( double theta ) {
            needleTx = AffineTransform.getRotateInstance( theta, x, y );
            pivot.setFrameFromCenter( x, y, x + 2, y + 2 );
            repaint();
        }
    }

    private class FaceGraphic extends PhetShapeGraphic {
        private Rectangle2D.Double tickMark;

        FaceGraphic() {
            super( component, null, backgroundColor, new BasicStroke( 5 ), new Color( 80, 80, 40 ) );
            Shape face = new Ellipse2D.Double( x - diam / 2, y - diam / 2, diam, diam );
            super.setShape( face );
            tickMark = new Rectangle2D.Double( x + diam * 3 / 8, y - 1, diam / 16, 2 );
//            tickMark = new Rectangle2D.Double( x + diam / 8, y - 1, diam / 8, 2 );
//            tickMark = new Rectangle2D.Double( x + diam / 6, y - 1, diam / 8, 2 );
        }


        public void paint( Graphics2D g ) {
            setBackground( backgroundColor );
            saveGraphicsState( g );
            GraphicsUtil.setAntiAliasingOn( g );
            super.paint( g );

            // Paint tick marks
            int numTickMarks = 19;
//            int numTickMarks = 7;
            g.setColor( Color.black );
            double tickSpace = ( Math.PI * 6 / 4 ) / ( numTickMarks - 1 );
            for( double theta = Math.PI * 3 / 4; theta <= Math.PI * 9 / 4 + tickSpace / 2; theta += tickSpace ) {
                AffineTransform orgTx = g.getTransform();
//                double theta = minTickTheta + i * Math.PI / 4;
                g.transform( AffineTransform.getRotateInstance( theta, x, y ) );
                g.fill( tickMark );
                g.setTransform( orgTx );
            }
//            double minTickTheta = Math.PI * 3 / 4;
//            double maxTickTheta = Math.PI / 4;
//            for( int i = 0; i < numTickMarks; i++ ) {
//                AffineTransform orgTx = g.getTransform();
//                double theta = minTickTheta + i * Math.PI / 4;
//                g.transform( AffineTransform.getRotateInstance( theta, x, y ) );
//                g.fill( tickMark );
//                g.setTransform( orgTx );
//            }

            // Paint values on min and max tick marks
            FontRenderContext frc = g.getFontRenderContext();
            String minStr = Double.toString( min );
            Rectangle2D bounds = font.getStringBounds( minStr, frc );
            g.setFont( font );
            double radRatio = .6;
//            g.drawString( minStr,
//                          (float)( x + Math.cos( minTickTheta ) * ( diam / 2 ) * radRatio ) - (float)bounds.getWidth() / 2,
//                          (float)( y + Math.sin( minTickTheta ) * ( diam / 2 ) * radRatio ) + (float)bounds.getHeight() );
//            String maxStr = Double.toString( max );
//            bounds = font.getStringBounds( maxStr, frc );
//            g.drawString( Double.toString( max ),
//                          (float)( x + Math.cos( maxTickTheta ) * ( diam / 2 ) * radRatio ) - (float)bounds.getWidth() / 2,
//                          (float)( y + Math.sin( maxTickTheta ) * ( diam / 2 ) * radRatio ) + (float)bounds.getHeight() );

            // Paint value, and units label
            RoundRectangle2D rect = new RoundRectangle2D.Double( 0, 0, 0, 0, 3, 3 );
            rect.setFrameFromCenter( x, y + 10, x + 30, y + 17 );
            g.setColor( Color.white );
            g.fill( rect );
            g.setColor( Color.yellow );
            g.setStroke( new BasicStroke( 3f ) );
            g.draw( rect );
            g.setColor( Color.black );
            g.setStroke( new BasicStroke( 0.5f ) );
            g.draw( rect );
            String datumString = formatter.format( datum ) + " " + units;
            bounds = font.getStringBounds( datumString, frc );
            g.setColor( Color.black );
            g.drawString( datumString,
                          (float)x - (float)bounds.getWidth() / 2,
                          (float)( y + ( ( diam / 4 ) * radRatio ) ) );
//            rect.setFrameFromCenter( x, y + 10, x + 30, y + 17 );
//            g.setColor( Color.yellow );
//            g.setStroke( new BasicStroke( 3f ) );
//            g.draw( rect );

//            double dy = bounds.getHeight();
//            bounds = font.getStringBounds( units, frc );
//            g.drawString( units,
//                          (float)x - (float)bounds.getWidth() / 2,
//                          (float)( y + ( ( diam / 2 ) * .6 ) + dy ) );

            // Paint the title
            bounds = font.getStringBounds( title, frc );
            g.setColor( Color.black );
            g.drawString( title,
                          (float)x - (float)bounds.getWidth() / 2,
                          (float)( y - ( ( diam / 4 ) * radRatio ) ) );

            restoreGraphicsState();
        }
    }
}
