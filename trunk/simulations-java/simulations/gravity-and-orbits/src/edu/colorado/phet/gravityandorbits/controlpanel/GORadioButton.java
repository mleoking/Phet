package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;

/**
 * @author Sam Reid
 */
public class GORadioButton<T> extends PropertyRadioButton<T> {
    public GORadioButton( String title, final Property<T> property, final T value ) {
        super( title, property, value );
        setOpaque( false );//TODO: is this a mac problem?
        setFont( GravityAndOrbitsControlPanel.CONTROL_FONT );
        setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
        setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
    }

    @Override
    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        Object originalKey = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, originalKey );
    }
}
