/** Sam Reid*/
package edu.colorado.phet.cck3.grabbag;

import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.common.math.AbstractVector2D;

import java.awt.geom.Point2D;

/**
 * Marker class.
 */
public class GrabBagResistor extends Resistor {
    private GrabBagItem itemInfo;

    public GrabBagResistor( Point2D start, AbstractVector2D dir, double length, double height, KirkhoffListener kl, GrabBagItem itemInfo ) {
        super( start, dir, length, height, kl );
        this.itemInfo = itemInfo;
    }

    public GrabBagItem getItemInfo() {
        return itemInfo;
    }
}
