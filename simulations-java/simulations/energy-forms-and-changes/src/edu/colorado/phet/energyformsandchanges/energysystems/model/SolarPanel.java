// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.SOLAR_PANEL_BASE;

/**
 * Class that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanel extends EnergyConverter {

    private static final double CONVERSION_EFFICIENCY = 0.3;

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( SOLAR_PANEL_BASE, SOLAR_PANEL_BASE.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( 0, -0.04 ) ) );
        add( new ModelElementImage( SOLAR_PANEL, SOLAR_PANEL.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( 0, 0.04 ) ) );
    }};

    protected SolarPanel() {
        super( EnergyFormsAndChangesResources.Images.SOLAR_PANEL_ICON, IMAGE_LIST );
    }

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {
        // TODO: Implement.
        return new Energy( Energy.Type.ELECTRICAL, incomingEnergy.amount * CONVERSION_EFFICIENCY, 0 );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSolarPanelButton;
    }
}
