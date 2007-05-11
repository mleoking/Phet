package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.view.PlatformNode;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 */

public class RotationPlayAreaNode extends PNode {
    private PlatformNode platformNode;
    private PNode rotationBodyLayer = new PNode();
    private RotationModel rotationModel;

    public RotationPlayAreaNode( final RotationModel rotationModel ) {
        this.rotationModel=rotationModel;
        platformNode = new PlatformNode( rotationModel, rotationModel.getRotationPlatform() );
        platformNode.setOffset( 5, 5 );

        addChild( platformNode );
        addChild( rotationBodyLayer );

        for( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            RotationBody rotationBody = rotationModel.getRotationBody( i );
            addRotationBodyNode( rotationBody );
        }
    }

    private void addRotationBodyNode( RotationBody rotationBody ) {
        RotationBodyNode rotationBodyNode = new RotationBodyNode( rotationModel, rotationBody );
        rotationBodyLayer.addChild( rotationBodyNode );
    }

}
