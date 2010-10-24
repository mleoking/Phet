package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.CompleteTheSymbolProblemView;
import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * @author Sam Reid
 */
public class CompleteTheSymbolProblem extends Problem {
    public CompleteTheSymbolProblem( BuildAnAtomGameModel model, AtomValue atomValue ) {
        super( model, atomValue );
    }

    @Override
    public StateView createView( GameCanvas gameCanvas ) {
        return new CompleteTheSymbolProblemView( model, gameCanvas, this );
    }
}
