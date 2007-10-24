package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.graphs.FullTorqueGraphSet;
import edu.colorado.phet.rotation.graphs.RotationMinimizableControlGraph;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:38:53 PM
 */
public class MomentGraphSet extends FullTorqueGraphSet {
    public MomentGraphSet( MomentOfInertiaSimulationPanel momentOfInertiaSimulationPanel, TorqueModel torqueModel, AngleUnitModel angleUnitModel ) {
        super( momentOfInertiaSimulationPanel, torqueModel, angleUnitModel );
        addGraphSuite( new RotationMinimizableControlGraph[]{createTorqueGraph(),createMomentGraph(),createAngAccelGraph() } );
    }
}
