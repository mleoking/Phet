/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToChargeQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.SchematicToMassQuestionView;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * Problem that presents a schematic (a.k.a. a "Bohr model") and asks the user
 * a question about the charge.
 *
 * @author John Blanco
 */
public class SchematicToChargeQuestionProblem extends Problem {

    /**
     * Constructor.
     */
    public SchematicToChargeQuestionProblem( BuildAnAtomGameModel model, AtomValue atom ) {
        super( model, atom );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new SchematicToChargeQuestionView( model, gameCanvas, this );
    }
}
