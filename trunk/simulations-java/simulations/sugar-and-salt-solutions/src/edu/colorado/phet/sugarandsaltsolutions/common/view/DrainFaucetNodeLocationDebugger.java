// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Debugging utility to show the location of the faucet where the particles will drain out
 *
 * @author Sam Reid
 */
public class DrainFaucetNodeLocationDebugger extends PNode {
    public DrainFaucetNodeLocationDebugger( final ModelViewTransform transform, final SugarAndSaltSolutionModel model ) {
        double length = 4;

        //Show the location where particles enter the drain faucet
        addChild( new PhetPPath( new Rectangle2D.Double( -length, -length, length * 2, length * 2 ), Color.red ) {{
            setOffset( transform.modelToView( model.getDrainFaucetMetrics().inputPoint.toPoint2D() ) );
        }} );

        //Show the location where particles leave through the drain faucet
        addChild( new PhetPPath( new Rectangle2D.Double( -length, -length, length * 2, length * 2 ), Color.red ) {{
            setOffset( transform.modelToView( model.getDrainFaucetMetrics().outputPoint.toPoint2D() ) );
        }} );
    }
}
