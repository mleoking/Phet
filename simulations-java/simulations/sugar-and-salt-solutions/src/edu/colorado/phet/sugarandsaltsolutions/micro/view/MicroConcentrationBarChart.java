// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.sugarandsaltsolutions.common.view.Bar;
import edu.colorado.phet.sugarandsaltsolutions.common.view.ConcentrationBarChart;
import edu.umd.cs.piccolo.PNode;

/**
 * Bar chart that shows Na+ and Cl- concentrations for table salt.
 *
 * @author Sam Reid
 */
public class MicroConcentrationBarChart extends ConcentrationBarChart {

    private final PNode barLayer = new PNode();
    private SettableProperty<Boolean> showValues;

    public MicroConcentrationBarChart( Property<Boolean> visible,
                                       SettableProperty<Boolean> showValues,
                                       BarItem... bars ) {
        super( showValues, visible );
        addChild( barLayer );
        this.showValues = showValues;
        setBars( bars );
    }

    //Clear the previous bars and display the specified bars
    public void setBars( BarItem... bars ) {
        barLayer.removeAllChildren();
        //Convert from model units (mol/L) to stage units by multiplying by this scale factor
        final double verticalAxisScale = 4;

        //Add the bar node for each of the specified bars
        double barX = -Bar.WIDTH;
        for ( BarItem bar : bars ) {
            final double finalBarX = barX;
            barLayer.addChild( new Bar( bar.color, bar.caption, bar.icon, bar.concentration, showValues, verticalAxisScale ) {{
                setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2 + finalBarX, abscissaY );
            }} );

            barX = barX + Bar.WIDTH + 25;
        }
    }
}