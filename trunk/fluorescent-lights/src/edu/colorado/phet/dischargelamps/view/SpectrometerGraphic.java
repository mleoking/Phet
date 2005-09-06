/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.Spectrometer;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * SpectrometerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrometerGraphic extends GraphicLayerSet implements Spectrometer.ChangeListener {
//public class SpectrometerGraphic extends CompositePhetGraphic implements Spectrometer.ChangeListener {

    private static Color UV_COLOR = new Color( 180, 180, 180 );

    // Width, in pixels, of the display area in the image;
    private int imageDisplayWidth = 410;

    private PhetImageGraphic backgroundPanel;
    private Point displayOrigin = new Point( 15, 115 );
    private int displayHeight = 100;
    private int displayWidth = 600;
    private int horizontalDisplayMargin = 30;
    private ArrayList photonMarkers = new ArrayList();
    private double minWavelength = 300; // nm
    private double maxWavelength = 800; // nm
//    private double minWavelength = Photon.MIN_VISIBLE_WAVELENGTH;
//    private double maxWavelength = Photon.MAX_VISIBLE_WAVELENGTH;

    //----------------------------------------------------------------
    // Constructor and initialization
    //----------------------------------------------------------------

    public SpectrometerGraphic( Component component, final Spectrometer spectrometer ) {
        super( component );
        spectrometer.addChangeListener( this );

        BufferedImage spectrometerImage = null;
        try {
            spectrometerImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.SPECTROMETER_IMAGE_FILE_NAME );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Scale the bezel image so it is the width we want it to be on the screen
        double scaleX = (double)displayWidth / imageDisplayWidth;
        AffineTransformOp op = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, 1 ),
                                                      new RenderingHints( RenderingHints.KEY_INTERPOLATION,
                                                                          RenderingHints.VALUE_INTERPOLATION_BICUBIC ) );
        spectrometerImage = op.filter( spectrometerImage, null );

        backgroundPanel = new PhetImageGraphic( component, spectrometerImage );
        addGraphic( backgroundPanel );

        addButtons( component, spectrometer );
        addUvIRIndicators();

        setCursorHand();
        addTranslationListener( new DefaultTranslator( this ) );
    }

    private void addUvIRIndicators() {
        double xLocUv = xLocForWavelength( minWavelength ) + displayOrigin.getX() + PhotonMarker.indicatorWidth;
        Line2D uvLine = new Line2D.Double( xLocUv, displayOrigin.getY(), xLocUv, displayOrigin.getY() - displayHeight );
        PhetShapeGraphic uvLineGraphic = new PhetShapeGraphic( getComponent(), uvLine, UV_COLOR, new BasicStroke( 1 ), Color.white );
        addGraphic( uvLineGraphic );

        PhetTextGraphic uvText = new PhetTextGraphic( getComponent(),
                                                      DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                      "<- UV",
                                                      UV_COLOR,
                                                      (int)( xLocUv - 30 ),
                                                      (int)( displayOrigin.getY() + 12 ) );
        addGraphic( uvText );

        double xLocIr = xLocForWavelength( maxWavelength ) + displayOrigin.getX();
        Line2D irLine = new Line2D.Double( xLocIr, displayOrigin.getY(), xLocIr, displayOrigin.getY() - displayHeight );
        PhetShapeGraphic irLineGraphic = new PhetShapeGraphic( getComponent(), irLine, UV_COLOR, new BasicStroke( 1 ), Color.white );
        addGraphic( irLineGraphic, 1000 );

        PhetTextGraphic irText = new PhetTextGraphic( getComponent(),
                                                      DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                      "IR ->",
                                                      UV_COLOR,
                                                      (int)( xLocIr ),
                                                      (int)( displayOrigin.getY() + 12 ) );
        addGraphic( irText );

    }

    private void addButtons( Component component, final Spectrometer spectrometer ) {
        // Add start/stop button
        final PhetButton startStopBtn = new PhetButton( component, "Start" );
        startStopBtn.setFont( DischargeLampsConfig.DEFAULT_CONTROL_FONT );
        startStopBtn.addActionListener( new ActionListener() {
            private boolean start = true;

            public void actionPerformed( ActionEvent e ) {
                if( start ) {
                    spectrometer.start();
                    startStopBtn.setText( "Stop " );
                    start = false;
                }
                else {
                    spectrometer.stop();
                    startStopBtn.setText( "Start " );
                    start = true;
                }
            }
        } );
        addGraphic( startStopBtn );
        startStopBtn.setLocation( 40, (int)( backgroundPanel.getSize().height - 3 ) );
        startStopBtn.setRegistrationPoint( 0, startStopBtn.getHeight() );

        // Add reset button
        PhetButton resetBtn = new PhetButton( component, "Reset" );
        resetBtn.setFont( DischargeLampsConfig.DEFAULT_CONTROL_FONT );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                spectrometer.reset();
            }
        } );
        addGraphic( resetBtn );
        resetBtn.setLocation( 40 + startStopBtn.getWidth() + 10, (int)( backgroundPanel.getSize().height - 3 ) );
        resetBtn.setRegistrationPoint( 0, resetBtn.getHeight() );
    }

    public void reset() {
        while( !photonMarkers.isEmpty() ) {
            PhetGraphic graphic = (PhetGraphic)photonMarkers.get( 0 );
            removeGraphic( graphic );
            photonMarkers.remove( 0 );
        }
    }

    private int xLocForWavelength( double wavelength ) {
        wavelength = Math.max( Math.min( wavelength, maxWavelength ), minWavelength );
        int wavelengthLoc = (int)( ( wavelength - minWavelength )
                                   / ( maxWavelength - minWavelength ) * ( displayWidth - horizontalDisplayMargin * 2 )
                                   + horizontalDisplayMargin );
        return wavelengthLoc;
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void countChanged( Spectrometer.CountChangeEvent eventCount ) {
        // Determine the y location for the wavelength of the photon in the event
        double wavelength = eventCount.getWavelength();

        // Min wavelength displays on the left
        int wavelengthLoc = xLocForWavelength( wavelength );
        double indicatorRadius = 1.5;
        int indicatorLoc = (int)( -( eventCount.getPhotonCount() - 1 ) * indicatorRadius * 2 );

        // If we haven't filled the height of the display with indicators, add one
        if( -indicatorLoc <= displayHeight - indicatorRadius ) {
            // Create a graphic for the photon
            Ellipse2D.Double shape = new PhotonMarker( wavelengthLoc + displayOrigin.getX(),
                                                       indicatorLoc + displayOrigin.getY() );
            Color color = null;
            if( wavelength >= minWavelength && wavelength <= maxWavelength ) {
                color = new VisibleColor( wavelength );
            }
            else {
                color = UV_COLOR;
            }
            PhetShapeGraphic psg = new PhetShapeGraphic( getComponent(), shape, color );
            psg.setRegistrationPoint( 0, 3 );
            photonMarkers.add( psg );
            addGraphic( psg );
            setBoundsDirty();
            repaint();
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class DefaultTranslator implements TranslationListener {
        private PhetGraphic graphic;

        public DefaultTranslator( PhetGraphic graphic ) {
            this.graphic = graphic;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            int dx = translationEvent.getDx();
            int dy = translationEvent.getDy();
            graphic.translate( dx, dy );
        }
    }

    private static class PhotonMarker extends Ellipse2D.Double {
        static double indicatorWidth = 5;
        static double indicatorHeight = 3;

        public PhotonMarker( double x, double y ) {
            super( x, y, indicatorWidth, indicatorHeight );
        }
    }
}
