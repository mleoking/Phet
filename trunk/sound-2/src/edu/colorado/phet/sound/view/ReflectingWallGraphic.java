/**
 * Class: ReflectingWallGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 16, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;

import java.awt.*;
import java.awt.geom.*;

public class ReflectingWallGraphic extends PhetShapeGraphic {

    private double x;
    private double y;
    private double w;
    private double h;
    private double theta;
    private Shape xformedWall;
    private AffineTransform xform;
    private Color wallColor = new Color( 48, 0, 128 );
    private Line2D.Double wallEdge = new Line2D.Double();
    private Stroke wallEdgeStroke = new BasicStroke( 4f );
    private GeneralPath interferingWaveMask;

    private Rectangle2D.Double wall;
    private Color color;
    private boolean displayHelpOrnaments;

    /**
     * @param x
     * @param y
     * @param w
     * @param h
     * @param theta Angle of rotation, countreclockwise
     */
    public ReflectingWallGraphic( Component component, Color color,
                                  double x, double y, double w, double h, double theta ) {
        super( component, null, color );

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.theta = theta;
        wall = new Rectangle2D.Double( x, y, w, h );

        interferingWaveMask = new GeneralPath();
        setAngle( theta );
        setLocation( x );
    }

    public Point2D.Double getMidPoint() {
        double x = getShape().getBounds2D().getMinX() + getShape().getBounds2D().getWidth() / 2;
        double y = getShape().getBounds2D().getMinY() + getShape().getBounds2D().getHeight() / 2;
        return new Point2D.Double( x, y );
    }


    /**
     * @param x
     */
    public void setLocation( double x ) {
        xform = AffineTransform.getTranslateInstance( x - this.x, 0 );
        xformedWall = xform.createTransformedShape( xformedWall );
        this.x = x;
        setShape( xformedWall );
        setInterferingWaveMask();
    }

    public double getLocation() {
        return this.x;
    }

    /**
     * @param theta
     */
    public void setAngle( double theta ) {
        this.theta = theta;
        xform = AffineTransform.getRotateInstance( -Math.toRadians( theta ), x, y );
        xformedWall = xform.createTransformedShape( wall );
        setShape( xformedWall );
        setInterferingWaveMask();
    }

    public double getAngle() {
        return theta;
    }

    private void setInterferingWaveMask() {

        // Create a interferingWaveMask to block out the new wavefront behind the wall.
        double xMax = 1000;
        double yMax = 800;
        double tan = Math.tan( Math.toRadians( theta ) );
        double x0 = Double.isInfinite( tan ) ? x : x - ( yMax - y ) / tan;
        double y0 = Double.isInfinite( tan ) ? y : y - ( xMax - x ) * tan;

        synchronized( interferingWaveMask ) {
            interferingWaveMask.reset();

            if( theta > 0 && theta < 90 ) {
                // The +2 is here so the wall will show
                interferingWaveMask.moveTo( (float)x0 + 2, (float)yMax );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)y0 );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)yMax );
                interferingWaveMask.closePath();
            }
            else {
                // The +2 is here so the wall will show
                interferingWaveMask.moveTo( (float)x0 + 2, (float)yMax );
                interferingWaveMask.lineTo( (float)x0 + 2, (float)y0 );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)y0 );
                interferingWaveMask.lineTo( (float)xMax + 2, (float)yMax );
                interferingWaveMask.closePath();
            }
            wallEdge.setLine( x0, yMax, xMax, y0 );
        }
    }

    /**
     * @return
     */
    double getX() {
        return x;
    }

    /**
     * @return
     */
    double getY() {
        return y;
    }

    /**
     * @param g
     */
    public void paint( Graphics2D g ) {
        g.setColor( wallColor );
        //        g.draw( xformedWall );
        //        g.fill( xformedWall );

        // TODO: Make this a Paintable
        g.setColor( new Color( 128, 128, 128 ) );
        synchronized( interferingWaveMask ) {
            g.fill( interferingWaveMask );
            g.setColor( wallColor );
            g.setStroke( wallEdgeStroke );
            g.draw( wallEdge );
        }

        if( displayHelpOrnaments ) {
            Ellipse2D.Double midPoint = new Ellipse2D.Double( getMidPoint().getX() - h, getMidPoint().getY() - h, 6, 6 );
            g.setStroke( new BasicStroke( 2 ) );
            g.setColor( Color.black );
            g.draw( midPoint );
            g.setColor( Color.yellow );
            g.fill( midPoint );

            double radius = 100;
            pushRenderingHints( g );
            g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
            Arc2D.Double upperArc = new Arc2D.Double( getMidPoint().getX() - radius - h,
                                                      getMidPoint().getY() - radius - h,
                                                      radius * 2, radius * 2,
                                                      getAngle() - 30, 60, Arc2D.OPEN );
            Arc2D.Double lowerArc = new Arc2D.Double( getMidPoint().getX() - radius - h,
                                                      getMidPoint().getY() - radius - h,
                                                      radius * 2, radius * 2,
                                                      getAngle() + 150, 60, Arc2D.OPEN );
            g.setColor( Color.black );
            g.draw( upperArc );
            g.draw( lowerArc );
            popRenderingHints( g );
        }

        // For debugging
        //            g.setColor(Color.RED);
        //            g.fillArc((int) x, (int) y, 5, 5, 0, 360);
        //            g.setColor(Color.RED);
        //            g.fillArc((int) b.getX(), (int) b.getY(), 5, 5, 0, 360);
        //            g.fillArc((int) pp.getX(), (int) pp.getY(), 5, 5, 0, 360);
        //            g.fillArc((int) p.getX(), (int) p.getY(), 5, 5, 0, 360);
        //            g.fillArc(300, 300, 5, 5, 0, 360);
    }

    public void setDisplayHelpOrnaments( boolean enabled ) {
        displayHelpOrnaments = enabled;
    }
}
