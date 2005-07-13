/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.bars;

import edu.colorado.phet.theramp.model.RampModel;
import edu.colorado.phet.theramp.model.ValueAccessor;
import edu.colorado.phet.theramp.view.RampPanel;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 8:17:06 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class WorkBarGraphSet extends BarGraphSet {
    public WorkBarGraphSet( RampPanel rampPanel, RampModel rampModel ) {
        super( rampPanel, rampModel, "Work" );
        ValueAccessor[] workAccess = new ValueAccessor[]{
            new ValueAccessor.TotalWork( getLookAndFeel() ),
            new ValueAccessor.GravityWork( getLookAndFeel() ),
            new ValueAccessor.FrictiveWork( getLookAndFeel() ),
            new ValueAccessor.AppliedWork( getLookAndFeel() )
        };
        super.setAccessors( workAccess );
//        PhetShapeGraphic workBackground = new PhetShapeGraphic( getComponent(), new Rectangle(  5, topY, 5 * 2 + workWidth, 1000 ), new Color( 240, 250, 245 ), new BasicStroke(), Color.black );
//        addGraphic( workBackground, -10 );
    }
}
