package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class StabilityIndicator extends PNode {

    public StabilityIndicator( final Atom atom ) {
        addChild( new PText( "Unstable" ) {{
            setFont( new PhetFont( 18, true ) );
            setTextPaint( Color.black );
        }} );
        SimpleObserver updateVisibility = new SimpleObserver() {
            public void update() {
                setVisible( !atom.isStable() );
            }
        };
        atom.addObserver( updateVisibility );
        updateVisibility.update();
    }
}
