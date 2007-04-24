
package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.semiconductor_semi.macro.doping.DopantType;
import edu.colorado.phet.semiconductor_semi.macro.energy.statemodels.ExciteForConduction;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 9:24:11 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public class SimpleConductRight3 extends DefaultStateDiagram {
    private ExciteForConduction leftExcite;
    private ExciteForConduction midExcite;
    private ExciteForConduction rightExcite;

    public SimpleConductRight3( EnergySection energySection, DopantType dopantType ) {
        super( energySection );
        leftExcite = excite( dopantType.getDopingBand(), 0, dopantType.getNumFilledLevels() - 1 );
        midExcite = excite( dopantType.getDopingBand(), 1, dopantType.getNumFilledLevels() - 1 );
        rightExcite = excite( dopantType.getDopingBand(), 2, dopantType.getNumFilledLevels() - 1 );
        enter( leftExcite.getLeftCell() );
        exitRight( rightExcite.getRightCell() );
        propagateRight( leftExcite.getLeftCell() );
    }

    public ExciteForConduction getLeftExcite() {
        return leftExcite;
    }

    public ExciteForConduction getRightExcite() {
        return rightExcite;
    }

    public ExciteForConduction getMidExcite() {
        return midExcite;
    }
}
