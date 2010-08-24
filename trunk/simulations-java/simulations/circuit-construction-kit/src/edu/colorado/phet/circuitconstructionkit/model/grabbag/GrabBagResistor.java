package edu.colorado.phet.circuitconstructionkit.model.grabbag;

import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.components.Resistor;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2DInterface;

import java.awt.geom.Point2D;

/**
 * Marker class.
 */
public class GrabBagResistor extends Resistor {
    private GrabBagItem itemInfo;

    public GrabBagResistor(Point2D start, AbstractVector2DInterface dir, double length, double height, CircuitChangeListener kl, GrabBagItem itemInfo) {
        super(start, dir, length, height, kl);
        this.itemInfo = itemInfo;
    }

    public GrabBagItem getItemInfo() {
        return itemInfo;
    }
}
