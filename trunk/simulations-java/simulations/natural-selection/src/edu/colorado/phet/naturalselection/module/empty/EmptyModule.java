/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.module.empty;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.NaturalSelectionDefaults;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;

/**
 * A quick and dirty emply module for development purposes
 *
 * @author Jonathan Olson
 */
public class EmptyModule extends PiccoloModule {

    public EmptyModule( Frame parentFrame ) {
        super( NaturalSelectionStrings.TITLE_NATURAL_SELECTION_MODULE, new NaturalSelectionClock( NaturalSelectionDefaults.CLOCK_FRAME_RATE, NaturalSelectionDefaults.CLOCK_DT ) );
    }
}