/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.semiconductor.common.TransformGraphic;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 9:48:13 AM
 * Copyright (c) Mar 15, 2004 by Sam Reid
 */
public class ElectricFieldSectionGraphic extends TransformGraphic {
    private ElectricFieldGraphic bfieldG;
    private ElectricFieldGraphic ifieldG;

    public ElectricFieldSectionGraphic( ElectricFieldSection field, ModelViewTransform2D transform ) {
        super( transform );

        bfieldG = new ElectricFieldGraphic( SimStrings.get( "ElectricFieldSectionGraphic.BatteryForceLabel" ), field.getBatteryField(), transform );
        ifieldG = new ElectricFieldGraphic( SimStrings.get( "ElectricFieldSectionGraphic.InternalForceLabel" ), field.getInternalField(), transform );
    }

    public void paint( Graphics2D g ) {
        bfieldG.paint( g );
        ifieldG.paint( g );
    }

    public void update() {
    }
}
