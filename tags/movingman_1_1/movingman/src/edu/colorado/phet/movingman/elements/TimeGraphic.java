/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.movingman.elements;

import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.ObservingGraphic;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Observable;

/**
 * User: Sam Reid
 * Date: Jun 30, 2003
 * Time: 12:46:15 AM
 * Copyright (c) Jun 30, 2003 by Sam Reid
 */
public class TimeGraphic implements InteractiveGraphic, ObservingGraphic {
    String timeStr;
    Timer recordingTimer;
    int x;
    int y;
    Font f = new Font( "Lucida Sans", 0, 36 );
    Color c = Color.black;
    DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );
    private FontRenderContext frc;

    public TimeGraphic( Timer recordingTimer, Timer playbackTimer, int x, int y ) {
        this.recordingTimer = recordingTimer;
        this.x = x;
        this.y = y;
        recordingTimer.addObserver( this );
        recordingTimer.updateObservers();
        playbackTimer.addObserver( this );
        update( recordingTimer, null );
    }

    public void paint( Graphics2D g ) {
        this.frc = g.getFontRenderContext();
        g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setFont( f );

        g.drawString( timeStr, x, y );
    }

    public void update( Observable o, Object arg ) {
//        System.out.println( "recordingTimer.getTime() = " + recordingTimer.getTime() );
        Timer tx = (Timer)o;
        double scalarTime = tx.getTime();
        double seconds = scalarTime;// * timerDisplayScale; //TIMING
        this.timeStr = decimalFormat.format( seconds ) + " seconds";
    }

    public boolean canHandleMousePress( MouseEvent event ) {
        return false;
    }

    public void mousePressed( MouseEvent event ) {
    }

    public void mouseDragged( MouseEvent event ) {
    }

    public void mouseReleased( MouseEvent event ) {
    }

    public void mouseEntered( MouseEvent event ) {
    }

    public void mouseExited( MouseEvent event ) {
    }

    public Area getClipArea() {
        if( frc == null ) {
            return new Area();
        }
        else {
            Rectangle2D bound = f.getStringBounds( this.timeStr, frc );
            Rectangle2D.Double out = new Rectangle2D.Double( bound.getX(), bound.getY(), bound.getWidth(), bound.getHeight() );
            out.x = x;
            out.y = y;
            return new Area( out );
        }

    }

}
