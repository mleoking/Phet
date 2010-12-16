package edu.colorado.phet.fluidpressureandflow.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.IsSelected;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowModule;
import edu.colorado.phet.fluidpressureandflow.model.FluidPressureAndFlowModel;
import edu.colorado.phet.fluidpressureandflow.model.Units;

/**
 * @author Sam Reid
 */
public class UnitsControlPanel<T extends FluidPressureAndFlowModel> extends PhetTitledPanel {

    public UnitsControlPanel( FluidPressureAndFlowModule<T> module ) {
        super( "Units" );
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        setBackground( FluidPressureControlPanel.BACKGROUND );
        setForeground( FluidPressureControlPanel.FOREGROUND );

        final T model = module.getFluidPressureAndFlowModel();
        add( new RadioButton( "atmospheres (atm)", new IsSelected<Units.Unit>( Units.ATMOSPHERE, model.getPressureUnitProperty() ) ) );
        add( new RadioButton( "Pascals (Pa)", new IsSelected<Units.Unit>( Units.PASCAL, model.getPressureUnitProperty() ) ) );
        add( new RadioButton( "<html>pounds per<br>square inch (psi)</html>", new IsSelected<Units.Unit>( Units.PSI, model.getPressureUnitProperty() ) ) );
        add( new JSeparator() );
        add( new RadioButton( "feet (ft)", new IsSelected<Units.Unit>( Units.FEET, model.getDistanceUnitProperty() ) ) );
        add( new RadioButton( "meters (m)", new IsSelected<Units.Unit>( Units.METERS, model.getDistanceUnitProperty() ) ) );
    }
}
