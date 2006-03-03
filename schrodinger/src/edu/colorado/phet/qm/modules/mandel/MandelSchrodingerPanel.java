/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.view.colorgrid.ColorMap;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.colormaps.WaveValueAccessor;
import edu.colorado.phet.qm.view.gun.HighIntensityGunGraphic;
import edu.colorado.phet.qm.view.piccolo.SchrodingerScreenNode;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 8:03:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class MandelSchrodingerPanel extends HighIntensitySchrodingerPanel {
    private MandelModule mandelModule;

    public MandelSchrodingerPanel( MandelModule mandelModule ) {
        super( mandelModule );
        this.mandelModule = mandelModule;
    }

    protected SchrodingerScreenNode createSchrodingerScreenNode( SchrodingerModule module ) {
        return new MandelSchrodingerScreenNode( module, this );
    }

    protected void doAddGunControlPanel() {
//don't  super.doAddGunControlPanel(), please
    }

    protected MandelModule getMandelModule() {
        return mandelModule;
    }

    protected HighIntensityGunGraphic createGun() {
        return new MandelGunSet( this );
    }

    protected boolean useGunChooserGraphic() {
        return false;
    }

    public MandelGun getLeftGun() {
        return getGunSet().getLeftGun();
    }

    public MandelGun getRightGun() {
        return getGunSet().getRightGun();
    }

    private MandelGunSet getGunSet() {
        return (MandelGunSet)getGunGraphic();
    }

    public void setSplitMode( boolean splitMode ) {
        updateWavefunctionColorMap();
    }

    public void wavelengthChanged() {
        getWavefunctionGraphic().setColorMap( new MandelSplitColorMap( getMandelModule() ) );
        double avgWavelength = ( getLeftGun().getWavelength() * getLeftGun().getIntensity() + getRightGun().getWavelength() * getRightGun().getIntensity() ) / ( getLeftGun().getIntensity() + getRightGun().getIntensity() );
        getDetectorSheetPNode().setDisplayPhotonColor( new ColorData( avgWavelength ) );
    }

    public MandelGunSet getMandelGunSet() {
        return getGunSet();
    }

    protected ColorMap createColorMap() {
        if( getMandelModule() == null || getMandelModule().getMandelModel() == null ) {
            return new PhotonColorMap( this, 0, new WaveValueAccessor.Magnitude() );
        }
        if( getMandelModule().getMandelModel().isSplit() ) {
            System.out.println( "MandelSchrodingerPanel.createColorMap: using mandelSplitColorMap" );
            return new MandelSplitColorMap( mandelModule );
        }
        else {
            System.out.println( "MandelSchrodingerPanel.createColorMap: using photonColormap (average)." );
            return new PhotonColorMap( this, ( getLeftGun().getWavelength() + getRightGun().getWavelength() ) / 2, new WaveValueAccessor.Magnitude() );
        }
    }
}
