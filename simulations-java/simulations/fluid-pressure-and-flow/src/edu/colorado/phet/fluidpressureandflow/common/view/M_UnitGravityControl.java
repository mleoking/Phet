// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.fluidpressureandflow.common.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.common.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.common.model.units.Unit;

/**
 * Gravity control that works with a certain unit
 *
 * @author Sam Reid
 */
public class M_UnitGravityControl<T extends FluidPressureAndFlowModel> extends MinimizableControl {
    public M_UnitGravityControl( final FluidPressureAndFlowModule<T> module, Unit gravityUnits ) {
        super( module.gravityControlVisible, new M_GravitySlider<T>( module, gravityUnits, module.gravityControlVisible ), "Gravity" );
    }
}
