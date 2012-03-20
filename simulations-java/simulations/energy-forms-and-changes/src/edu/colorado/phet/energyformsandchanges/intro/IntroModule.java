// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.intro.model.IntroModel;
import edu.colorado.phet.energyformsandchanges.intro.view.IntroCanvas;

/**
 * The "Intro" module for the Energy Forms and Changes simulation.  This is
 * where the model and canvas are hooked together.
 *
 * @author John Blanco
 */
public class IntroModule extends SimSharingPiccoloModule {

    private IntroModel model;

    public IntroModule() {
        this( new IntroModel() );
    }

    private IntroModule( IntroModel model ) {
        super( EnergyFormsAndChangesSimSharing.UserComponents.introTab, EnergyFormsAndChangesResources.Strings.INTRO, model.getClock() );
        this.model = model;
        setLogoPanel( null ); // Turn off the "logo panel", which is the control panel on right side.
        setClockControlPanel( null );
        setSimulationPanel( new IntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
