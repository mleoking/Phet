package edu.colorado.phet.movingman.motion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * Created by: Sam
 * Dec 6, 2007 at 7:03:45 AM
 */
public class SoundCheckBox extends JCheckBox {
    public SoundCheckBox( final ISoundObject soundObject ) {
        super( SimStrings.get( "options.sound" ) );
        setSelected( soundObject.isAudioEnabled() );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                soundObject.setAudioEnabled( isSelected() );
            }
        } );
    }
}
