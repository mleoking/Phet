/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;


/**
 * User: Sam Reid
 * Date: Mar 17, 2004
 * Time: 11:48:44 AM
 */
public class ExitLeftState extends StateChain {

    public ExitLeftState( Vector2D dest, Speed speed, EnergySection section ) {
        super();

        addState( new MoveToPosition( dest, speed ) );
        addState( new Remove( section ) );
    }

}
