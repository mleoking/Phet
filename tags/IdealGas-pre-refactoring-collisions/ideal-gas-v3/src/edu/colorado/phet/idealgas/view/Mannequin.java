/**
 * Class: Mannequin
 * Package: edu.colorado.phet.idealgas.view
 * Author: Another Guy
 * Date: Sep 14, 2004
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.Animation;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.PressureSensingBox;

import java.awt.*;
import java.io.IOException;

public class Mannequin extends PhetGraphic implements SimpleObserver {
    private static float s_leaningManStateChangeScaleFactor = 1.75F;

    private Animation pusher;
    private Animation leaner;
    private Image currPusherFrame;
    private Image currLeanerFrame;
    private IdealGasModel model;
    private PressureSensingBox box;
    private Point location = new Point();
    private double lastPressure;

    public Mannequin( Component component, IdealGasModel model, PressureSensingBox box ) {
        super( component );
        this.model = model;
        this.box = box;
        box.addObserver( this );
        try {
            pusher = new Animation( IdealGasConfig.PUSHER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_PUSHER_ANIMATION_FRAMES );
            leaner = new Animation( IdealGasConfig.LEANER_ANIMATION_IMAGE_FILE_PREFIX, IdealGasConfig.NUM_LEANER_ANIMATION_FRAMES );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        currPusherFrame = pusher.getCurrFrame();
        update();
    }

    protected Rectangle determineBounds() {
        return new Rectangle( location.x, location.y,
                              currPusherFrame.getWidth( null ),
                              currPusherFrame.getHeight( null ) );
    }

    public void paint( Graphics2D g ) {
        g.drawImage( currPusherFrame, location.x, location.y, null );
    }

    public void update() {
        int offsetX = -2 * (int)Box2DGraphic.s_thickness;
        int offsetY = 3 * (int)Box2DGraphic.s_thickness;

        // Update the pusher
        int nextLocationX = (int)box.getMinX() - currPusherFrame.getHeight( null ) + offsetX;
        if( nextLocationX != location.x ) {
            location.setLocation( nextLocationX, box.getMaxY() - currPusherFrame.getWidth( null ) + offsetY );

            // Update the leaner
            int dir = nextLocationX - location.x;
            currPusherFrame = dir > 0 ? pusher.getNextFrame() : pusher.getPrevFrame();
            double newPressure = box.getPressure();
            dir = newPressure == lastPressure ? 0 :
                  ( newPressure > lastPressure * s_leaningManStateChangeScaleFactor ? 1 : -1 );
            lastPressure = newPressure;
            if( dir > 0 && leaner.getCurrFrameNum() + 1 < leaner.getNumFrames() ) {
                currLeanerFrame = leaner.getNextFrame();
            }
            else if( dir < 0 && leaner.getCurrFrameNum() > 0 ) {
                currLeanerFrame = leaner.getPrevFrame();
            }

            this.setBoundsDirty();
            this.repaint();
        }
    }

}
