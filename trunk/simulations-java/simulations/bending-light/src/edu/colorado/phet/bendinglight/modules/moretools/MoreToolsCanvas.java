// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import edu.colorado.phet.bendinglight.modules.intro.IntroCanvas;
import edu.colorado.phet.bendinglight.modules.intro.IntroModel;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class MoreToolsCanvas extends IntroCanvas {
    public MoreToolsCanvas( IntroModel model, BooleanProperty moduleActive, Resettable resetAll ) {
        super( model, moduleActive, resetAll );
        addChild( new PText( "Hello!" ) );
    }
}
