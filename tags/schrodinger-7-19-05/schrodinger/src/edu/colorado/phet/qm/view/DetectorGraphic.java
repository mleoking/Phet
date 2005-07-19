/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.qm.SchrodingerLookAndFeel;
import edu.colorado.phet.qm.model.Detector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class DetectorGraphic extends RectangleGraphic {
    private Detector detector;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private PhetTextGraphic probDisplay;
    private PhetGraphic closeGraphic;
    private Color darkGreen;

    public DetectorGraphic( final SchrodingerPanel schrodingerPanel, final Detector detector ) {
//        super( schrodingerPanel, detector, new Color( 0, 0, 0, 0 ) );
        super( schrodingerPanel, detector, new Color( 200, 180, 150, 65 ) );
        this.detector = detector;

        darkGreen = new Color( 50, 230, 75 );
        probDisplay = new PhetTextGraphic( schrodingerPanel, new Font( "Lucida Sans", Font.BOLD, 14 ), "", darkGreen );
        probDisplay.setIgnoreMouse( true );
        addGraphic( probDisplay );
        detector.addObserver( new SimpleObserver() {
            public void update() {
                DetectorGraphic.this.update();
            }
        } );

        JButton closeButton = SchrodingerLookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                schrodingerPanel.removeDetectorGraphic( DetectorGraphic.this );
            }
        } );
        closeGraphic = PhetJComponent.newInstance( schrodingerPanel, closeButton );
        addGraphic( closeGraphic );

        update();
    }

    public void setCloseButtonVisible( boolean visible ) {
        closeGraphic.setVisible( visible );
    }

    public void setPercentDisplayVisible( boolean visible ) {
        probDisplay.setVisible( visible );
    }

    private void update() {
        Rectangle modelRect = detector.getBounds();
        Rectangle viewRect = super.getViewRectangle( modelRect );

        double probPercent = detector.getProbability() * 100.0;
//        System.out.println( "probPercent = " + probPercent );
        String formatted = format.format( probPercent );
        probDisplay.setText( formatted + " %" );
        probDisplay.setLocation( (int)viewRect.getX(), (int)viewRect.getY() );
        if( detector.isEnabled() ) {
            probDisplay.setColor( darkGreen );
        }
        else {
            probDisplay.setColor( Color.gray );
        }
        closeGraphic.setLocation( (int)( viewRect.getX() - closeGraphic.getWidth() ), (int)viewRect.getY() - closeGraphic.getHeight() );
    }

    public Detector getDetector() {
        return detector;
    }
}
