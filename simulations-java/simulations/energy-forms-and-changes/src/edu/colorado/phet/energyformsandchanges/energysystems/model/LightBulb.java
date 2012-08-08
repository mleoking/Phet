// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.ELEMENT_BASE;
import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.WIRE_BLACK_RIGHT;

/**
 * Base class for light bulbs in the model.
 *
 * @author John Blanco
 */
public class LightBulb extends EnergyUser {

    private final double energyToFullyLight; // In joules/sec, a.k.a. watts.
    private final IUserComponent userComponent;

    public final Property<Double> litProportion = new Property<Double>( 0.0 );

    protected LightBulb( IUserComponent userComponent, Image icon, final ModelElementImage offImage, final ModelElementImage onImage, double energyToFullyLight ) {
        super( icon, assembleImageList( offImage, onImage ) );
        this.userComponent = userComponent;
        this.energyToFullyLight = energyToFullyLight;
    }

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {
        if ( incomingEnergy.type == Energy.Type.ELECTRICAL ) {
            litProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / energyToFullyLight, 1 ) );
        }
        else {
            litProportion.set( 0.0 );
        }
    }

    static private List<ModelElementImage> assembleImageList( final ModelElementImage offImage, final ModelElementImage onImage ) {
        return new ArrayList<ModelElementImage>() {{
            add( new ModelElementImage( WIRE_BLACK_RIGHT,
                                        WIRE_BLACK_RIGHT.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                        new Vector2D( -0.01, -0.04 ) ) );
            add( new ModelElementImage( ELEMENT_BASE,
                                        ELEMENT_BASE.getWidth() / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR,
                                        new Vector2D( 0, -0.022 ) ) );
            add( offImage );
            add( onImage );
        }};
    }

    @Override public IUserComponent getUserComponent() {
        return userComponent;
    }
}
