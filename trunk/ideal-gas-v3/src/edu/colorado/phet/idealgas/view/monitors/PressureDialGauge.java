/**
 * Class: PressureDialGauge
 * Class: edu.colorado.phet.idealgas.view.monitors
 * User: Ron LeMaster
 * Date: Sep 28, 2004
 * Time: 3:40:04 PM
 */
package edu.colorado.phet.idealgas.view.monitors;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.ScalarObservable;
import edu.colorado.phet.gauges.DialGauge;
import edu.colorado.phet.idealgas.PressureSlice;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class PressureDialGauge implements Graphic {
    private PressureSensingBox box;
    private PressureSlice pressureSlice;
    private double radius = 50;
    private double stemLength = 15;
    private double stemThickness = 10;
    private DialGauge pressureGauge;
    private Point2D.Double center;
    private Rectangle2D.Double stem;
    private Font font = new Font( "Lucida Sans", Font.BOLD, 10 );

    public PressureDialGauge( PressureSensingBox box, Component component, PressureSlice pressureSlice ) {
        this.box = box;
        this.pressureSlice = pressureSlice;

        center = new Point2D.Double( box.getMaxX() + radius + stemLength, pressureSlice.getY() );
        pressureGauge = new DialGauge( new ObservablePressureBox(), component,
                                       center.getX(), center.getY(),
                                       radius * 2, 0, 100, "Pressure", "Atm",
                                       font );
        stem = new Rectangle2D.Double( box.getMaxX(), center.getY() - stemThickness / 2,
                                       stemLength, stemThickness );
    }

    public void paint( Graphics2D g ) {
        g.setColor( Color.black );
        g.fill( stem );
        pressureGauge.paint( g );
    }

    private class ObservablePressureBox extends ScalarObservable implements SimpleObserver {

        public ObservablePressureBox() {
            box.addObserver( this );
            pressureSlice.addObserver( this );
        }

        public double getValue() {
            return pressureSlice.getPressure();
//            return box.getPressure();
        }

        public void update() {
            notifyObservers();
        }
    }


}
