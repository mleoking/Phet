// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.TEAPOT;

/**
 * Class that represents the steam-generating tea pot in the model.
 *
 * @author John Blanco
 */
public class TeaPot extends EnergySource {

    private static final double ENERGY_PRODUCTION_RATE = 200; // In joules/second

    private static final List<ModelElementImage> IMAGE_LIST = new ArrayList<ModelElementImage>() {{
        add( new ModelElementImage( TEAPOT, TEAPOT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR, new Vector2D( 0.02, -0.05 ) ) );
    }};

    protected TeaPot() {
        super( EnergyFormsAndChangesResources.Images.TEAPOT_ICON, IMAGE_LIST );
    }

    @Override public Energy stepInTime( double dt ) {
        return new Energy( Energy.Type.MECHANICAL, ENERGY_PRODUCTION_RATE * dt, Math.PI / 2 );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectTeapotButton;
    }
}
