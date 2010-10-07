package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.model.Atom;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 * @author John Blanco
 */
public class ElementNameIndicator extends PNode {
    private final Atom atom;
    private final PText elementName;

    public ElementNameIndicator( final Atom atom ) {
        this.atom = atom;
        elementName = new PText() {
            {
                setFont( new PhetFont( 18, true ) );
                setTextPaint( Color.red );
            }
        };
        addChild( elementName );

        atom.addObserver( new SimpleObserver() {

            public void update() {
                ElementNameIndicator.this.update();
            }
        } );

        update();
    }

    public void update() {
        elementName.setText( atom.getNumProtons() > 0 ? atom.getName() : " " );  // Can't set to a 0-length string or it can mess up layout in canvas.
        elementName.setOffset( -elementName.getFullBoundsReference().width / 2, -elementName.getFullBoundsReference().height / 2 );
    }
}
