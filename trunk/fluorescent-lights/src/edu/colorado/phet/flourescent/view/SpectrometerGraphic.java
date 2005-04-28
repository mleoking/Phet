/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.flourescent.view;

import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetcomponents.PhetButton;
import edu.colorado.phet.flourescent.model.Spectrometer;
import edu.colorado.phet.flourescent.FluorescentLightsConfig;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * SpectrometerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrometerGraphic extends GraphicLayerSet implements Spectrometer.ChangeListener {
//public class SpectrometerGraphic extends CompositePhetGraphic implements Spectrometer.ChangeListener {

    private String spectrometerImageFileName = "images/spectrometer-panel.png";

    private PhetImageGraphic backgroundPanel;
    private Point displayOrigin = new Point( 15, 115 );
    private int displayHeight = 100;
    private int displayWidth = 400;
    private int horizontalDisplayMargin = 10;
    private ArrayList photonMarkers = new ArrayList();


    public SpectrometerGraphic( Component component, final Spectrometer spectrometer ) {
        super( component );
        spectrometer.addChangeListener( this );

        backgroundPanel = new PhetImageGraphic( component, spectrometerImageFileName );
        addGraphic( backgroundPanel );

        addButtons( component, spectrometer );

        setCursorHand();
        addTranslationListener( new DefaultTranslator( this ) );
    }

    private void addButtons( Component component, final Spectrometer spectrometer ) {
        // Add start/stop button
        final PhetButton startStopBtn = new PhetButton( component, "Start" );
        startStopBtn.setFont( FluorescentLightsConfig.defaultControlFont );
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
        resetBtn.setFont( FluorescentLightsConfig.defaultControlFont );
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

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void countChanged( Spectrometer.CountChangeEvent eventCount ) {
        // Determine the y location for the wavelength of the photon in the event
        double wavelength = eventCount.getWavelength();
        double minWavelength = Photon.DEEP_RED;
        double maxWavelength = Photon.BLUE;
        double indicatorWidth = 2.5;
        double indicatorHeight = 1.5;
        int wavelengthLoc = (int)( ( wavelength - minWavelength )
                                   / ( maxWavelength - minWavelength ) * ( displayWidth - horizontalDisplayMargin * 2 )
                                   + horizontalDisplayMargin );
        double indicatorRadius = 1.5;
        int indicatorLoc = (int)( -( eventCount.getPhotonCount() - 1 ) * indicatorRadius * 2 );

        // If we haven't filled the height of the display with indicators, add one
        if( -indicatorLoc <= displayHeight - indicatorRadius ) {
            // Create a graphic for the photon
            Ellipse2D.Double shape = new Ellipse2D.Double( wavelengthLoc + displayOrigin.getX(),
                                                           indicatorLoc + displayOrigin.getY(),
                                                           indicatorWidth * 2, indicatorHeight * 2 );
            PhetShapeGraphic psg = new PhetShapeGraphic( getComponent(),
                                                         shape,
                                                         new VisibleColor( wavelength ) );
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
}
