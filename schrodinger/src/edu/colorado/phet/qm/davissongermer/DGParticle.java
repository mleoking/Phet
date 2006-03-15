/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.qm.model.ParticleUnits;
import edu.colorado.phet.qm.view.gun.AbstractGunGraphic;
import edu.colorado.phet.qm.view.gun.DefaultGunParticle;

/**
 * User: Sam Reid
 * Date: Feb 5, 2006
 * Time: 2:34:12 PM
 * Copyright (c) Feb 5, 2006 by Sam Reid
 */

public class DGParticle extends DefaultGunParticle {
    private double covariance = 0.06;
    private double startYFraction = 0.75;

    public DGParticle( AbstractGunGraphic gunGraphic ) {
        super( gunGraphic, "Electrons", "images/electron-thumb.png", new ParticleUnits.ElectronUnits() );
    }

    protected double getStartDxLattice() {
        return covariance * getDiscreteModel().getGridWidth();
    }

    protected double getStartY() {
        return getDiscreteModel().getGridHeight() * startYFraction;
    }

    public double getCovariance() {
        return covariance;
    }

    public void setCovariance( double covariance ) {
        this.covariance = covariance;
    }

    public double getStartYFraction() {
        return startYFraction;
    }

    public void setStartYFraction( double startYFraction ) {
        this.startYFraction = startYFraction;
    }

}
