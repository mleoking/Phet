/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.waves.view;

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.emf.model.Electron;
import edu.colorado.phet.emf.EmfConfig;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;

/**
 * WaveMdeiumGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

/**
 * This variant of WaveMediumGraphic is the one used in the non-interefernce modules of the sound simulation.
 */
public class WaveMediumGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------

    public static final int TO_LEFT = -1;
    public static final int TO_RIGHT = 1;

    // Note that larger values for the stroke slow down performance considerably
    private static double s_defaultStrokeWidth = 10;
    private static Stroke s_defaultStroke = new BasicStroke( (float)s_defaultStrokeWidth );

    private static BufferedImage createBufferedImage() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage( 800, 800 );
        //        return gc.createCompatibleImage( 300, 200 );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    // TODO: This should be set by a call to initLayout, not here.
    private Electron electron;
    private Point2D origin;
    private double height = EmfConfig.WAVEFRONT_HEIGHT;
    private double stroke = 1;
    private boolean isPlanar = true;
    private BufferedImage buffImg;
    private Graphics2D g2DBuffImg;
    private float opacity = 1.0f;
    private static final double MAX_AMPLITDUE = 100;
    private double xExtent;
    private double xOffset = 15;
    private int direction;
    private Color maxAmplitudeColor = Color.red;
    private Color[] colorForAmplitude = new Color[255];

    /**
     * todo: rename WaveMediumGraphic
     */
    public WaveMediumGraphic( Electron electron, Component component, Point2D origin, double xExtent, int direction ) {
        super( component, createBufferedImage() );

        this.origin = origin;
        this.xExtent = xExtent;
        this.direction = direction;

        // Hook up to the WaveMedium we are observing
        this.electron = electron;
        electron.addObserver( this );

        buffImg = super.getImage();
        g2DBuffImg = buffImg.createGraphics();
//        g2DBuffImg.setColor( backgroundColor );
        g2DBuffImg.setColor( new Color( 0, 0, 0, 0 ) );

        g2DBuffImg.fill( new Rectangle( buffImg.getMinX(), buffImg.getMinY(), buffImg.getWidth(), buffImg.getHeight() ) );
        setGraphicsHints( g2DBuffImg );

        setMaxAmplitudeColor( Color.red );
    }

    public void setMaxAmplitudeColor( Color color ) {
        for( int i = 0; i < colorForAmplitude.length; i++ ) {
            colorForAmplitude[i] = new Color( color.getRed(),
                                              color.getGreen(),
                                              color.getBlue(),
                                              i );
        }
    }

    /**
     * Gets the color corresponding to a particular amplitude at a particular point. The idea is to
     * match the zero pressure point in the wave medium to the background color reported by the
     * rgbReporter
     *
     * @param amplitude
     * @return
     */
    private Color getColorForAmplitude( double amplitude ) {
        double normalizedAmplitude = Math.min( 1, Math.abs( amplitude / MAX_AMPLITDUE ) );
        return colorForAmplitude[(int)( normalizedAmplitude * ( 255 - 1 ))];
    }

    private void setGraphicsHints( Graphics2D g2 ) {
        g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
        //        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g2.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE );
        g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
    }

    /**
     * @return
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * @param opacity
     */
    public void setOpacity( float opacity ) {
        this.opacity = opacity;
    }

    /**
     *
     */
    public void paint( Graphics2D g ) {

        this.setGraphicsHints( g );

        // Set opacity
        Composite incomingComposite = g.getComposite();
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) );

        g.setStroke( s_defaultStroke );

        Point2D end1 = new Point2D.Float();
        Point2D end2 = new Point2D.Float();
        Line2D line = new Line2D.Float();

        // Draw a line or arc for each value in the amplitude array of the wave front
        for( double x = 1; x * direction < xExtent; x += s_defaultStrokeWidth * direction ) {
            g.setColor( getColorForAmplitude( electron.getDynamicFieldAt( new Point2D.Double( origin.getX() + x, origin.getY() ) ).getMagnitude() ) );
            if( this.isPlanar ) {
                end1.setLocation( origin.getX() + ( xOffset * direction ) + x, origin.getY() - height / 2 );
                end2.setLocation( origin.getX() + ( xOffset * direction ) + x, origin.getY() + height / 2 );
//                end1.setLocation( origin.getX() + ( x * stroke ), origin.getY() - height / 2 );
//                end2.setLocation( origin.getX() + ( x * stroke ), origin.getY() + height / 2 );
                line.setLine( end1, end2 );
                g.draw( line );
            }
        }
        g.setComposite( incomingComposite );
    }

    /**
     *
     */
    public Point2D getOrigin() {
        return origin;
    }

    /**
     *
     */
    public void update() {
//        for( int i = 0; i < amplitudes.length; i++ ) {
//            amplitudes[i] = electron.getDynamicFieldAt( (double)i );
//        }
        this.repaint();
    }
}
