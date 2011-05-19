// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.Plus;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * The fluid combination of water and dissolved solutes, sitting on top of any precipitated solid.
 *
 * @author Sam Reid
 */
public class Solution {
    //Volume of the solution (water plus dissolved solutes)
    public final Plus volume;

    //Get the shape this water takes in its containing beaker
    public final ObservableProperty<Shape> shape;

    public Solution( DoubleProperty waterVolume,
                     final Beaker beaker,
                     //The y-location of the base of the solution (0 if sitting on the base of the beaker, or >0 if sitting on a solid precipitate).
                     final ObservableProperty<Double> y,
                     ObservableProperty<Double> dissolvedSaltVolume,
                     ObservableProperty<Double> dissolvedSugarVolume ) {

        this.volume = waterVolume.plus( dissolvedSaltVolume, dissolvedSugarVolume );
        shape = new CompositeProperty<Shape>( new Function0<Shape>() {
            public Shape apply() {
                //Assumes the beaker is rectangular
                return new Rectangle2D.Double( beaker.getX(), beaker.getY() + y.get(), beaker.getWidth(), beaker.getHeightForVolume( volume.get() ) );
            }
        }, volume, y );
    }
}