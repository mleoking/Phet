package edu.colorado.phet.rotation.view;

import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.RotationControlPanel;
import edu.colorado.phet.rotation.RotationModule;

import javax.swing.*;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:13:21 AM
 */
public class RotationSimulationPanel extends AbstractRotationSimulationPanel {
    public RotationSimulationPanel( AbstractRotationModule rotationModule, JFrame parentFrame ) {
        super( rotationModule, parentFrame );
    }

    protected JComponent createControlPanel( RulerNode rulerNode, JFrame parentFrame ) {
        return new RotationControlPanel( rulerNode, getRotationGraphSet(), getGraphSetModel(), ( (RotationModule)getAbstractRotationModule() ).getVectorViewModel(), parentFrame, getAbstractRotationModule().getRotationModel().getRotationBody( 1 ) );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new RotationPlayAreaNode( getAbstractRotationModule().getRotationModel(), ( (RotationModule)getAbstractRotationModule() ).getVectorViewModel() );
    }
}
