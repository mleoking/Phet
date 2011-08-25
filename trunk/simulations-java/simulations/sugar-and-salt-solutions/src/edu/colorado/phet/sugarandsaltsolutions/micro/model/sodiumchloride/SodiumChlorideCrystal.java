package edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Formula;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;

/**
 * This crystal for Sodium Chloride salt updates the positions of the molecules to ensure they move as a crystal
 *
 * @author Sam Reid
 */
public class SodiumChlorideCrystal extends Crystal<SphericalParticle> {

    public SodiumChlorideCrystal( ImmutableVector2D position, double angle ) {
        super( new Formula( Sodium.class, Chloride.class ), position, new Chloride().radius + new Sodium().radius, angle );
    }

    //Randomly choose an initial particle for the crystal lattice
    public SphericalParticle createConstituentParticle( Class<? extends Particle> type ) {
        return type == Sodium.class ? new Sodium() : new Chloride();
    }

    //Create the bonding partner for growing the crystal
    @Override public SphericalParticle createPartner( SphericalParticle original ) {
        return original instanceof Sodium ? new Chloride() : new Sodium();
    }
}