/**
 * Class: InteractiveSpeakerGraphic
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 31, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationListener;
import edu.colorado.phet.common.view.graphics.mousecontrols.TranslationEvent;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.sound.SoundConfig;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class InteractiveSpeakerGraphic extends CompositePhetGraphic {
    private SpeakerGraphic speakerGraphic;
    private BufferedWaveMediumGraphic waveMediumGraphic;
    private ArrayList mouseReleaseListeners = new ArrayList();
    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public InteractiveSpeakerGraphic( SpeakerGraphic speakerGraphic,
                                      BufferedWaveMediumGraphic waveMediumGraphic ) {
        this.speakerGraphic = speakerGraphic;
        addGraphic( speakerGraphic );
        this.waveMediumGraphic = waveMediumGraphic;
        this.setCursorHand();
        this.addTranslationListener( new SpeakerTranslator() );
    }

    public Point2D.Double getAudioSourceLocation() {
        return new Point2D.Double( waveMediumGraphic.getOrigin().getX(), waveMediumGraphic.getOrigin().getY() );
    }

    private class SpeakerTranslator implements TranslationListener {
        public void translationOccurred( TranslationEvent event ) {
            Point2D p = speakerGraphic.getLocation();
//            speakerGraphic.setLocation( (int)p.getX(), (int)( p.getY() + event.getDy() ) );
            speakerGraphic.setLocation( (int)p.getX(), (int)( p.getY() + 2 * MathUtil.getSign( event.getDy() ) ) );
            waveMediumGraphic.setOrigin( new Point2D.Double( SoundConfig.s_wavefrontBaseX, speakerGraphic.getLocation().y ) );
            changeListenerProxy.stateChanged( new ChangeEvent( InteractiveSpeakerGraphic.this ) );
        }
    }

    public void addChangeListener( ChangeListener changeListener ) {
        changeEventChannel.addListener( changeListener );
    }

}
