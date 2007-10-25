package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.piccolophet.nodes.HandleNode;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:52:02 AM
 */

public class RotationPlayAreaNode extends PNode {

    private PNode rotationPlatformNode;
    private PNode rotationBodyLayer = new PNode();
    private RotationModel rotationModel;
    private PNode vectorLayer = new PNode();
    private RotationOriginNode originNode;
    private RotationRulerNode rulerNode;

    public static final double SCALE = 3.0 / 200.0;
    private CircleNode circularMotionNode;

    class RotationPlatformNodeWithHandle extends RotationPlatformNode {

        
        private double handleHeight = 10 * RotationPlayAreaNode.SCALE;

        //    private PhetPPath handleNode;
        public RotationPlatformNodeWithHandle( final RotationPlatform rotationPlatform ) {
            super( rotationPlatform );
            HandleNode handleNode2 = new HandleNode( handleHeight / 2 * 7, handleHeight * 7, Color.gray );
            handleNode2.setStroke( new BasicStroke( 1.0f / 50.0f ) );
            handleNode2.setOffset( rotationPlatform.getRadius() + handleNode2.getFullBounds().getWidth() * 0.9, handleNode2.getFullBounds().getHeight() / 2 );
            handleNode2.rotate( Math.PI );
            super.addContentNode( handleNode2 );
        }
    }

    public RotationPlayAreaNode( final RotationModel rotationModel, VectorViewModel vectiorViewModel, AngleUnitModel angleUnitModel ) {
        this.rotationModel = rotationModel;
        rotationPlatformNode = createRotationPlatformNode(rotationModel.getRotationPlatform());
//        rotationPlatformNode = new BufferedRotationPlatformNode( rotationModel.getRotationPlatform() );
        originNode = new RotationOriginNode( rotationModel.getRotationPlatform(), angleUnitModel );
        rulerNode = new RotationRulerNode( rotationModel.getRotationPlatform().getRadius() * 2, 50 * SCALE, new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8"}, "m", 4, 14 );
        rulerNode.setTransform( AffineTransform.getScaleInstance( 1, -1 ) );
        rulerNode.setVisible( false );

        addChild( rotationPlatformNode );
        addChild( rotationBodyLayer );
        addChild( vectorLayer );
        addChild( originNode );
        addChild( rulerNode );

        for ( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addRotationBodyNode( rotationModel.getRotationBody( i ) );
        }
        for ( int i = 0; i < rotationModel.getNumRotationBodies(); i++ ) {
            addVectorNode( rotationModel.getRotationBody( i ), vectiorViewModel );
        }
        circularMotionNode = new CircleNode( rotationModel );
        circularMotionNode.setVisible( false );
        addChild( circularMotionNode );

        setTransform( AffineTransform.getScaleInstance( 1, -1 ) );

    }

    protected PNode createRotationPlatformNode( RotationPlatform rotationPlatform ) {
        return new RotationPlatformNodeWithHandle( rotationPlatform );
    }

    public CircleNode getCircularMotionNode() {
        return circularMotionNode;
    }

    private void addVectorNode( RotationBody rotationBody, VectorViewModel vectorViewModel ) {
        vectorLayer.addChild( new BodyVectorLayer( rotationModel, rotationBody, vectorViewModel ) );
    }

    public PNode getPlatformNode() {
        return rotationPlatformNode;
    }

    private void addRotationBodyNode( RotationBody rotationBody ) {
        rotationBodyLayer.addChild( new RotationBodyNode( rotationModel, rotationBody ) );
    }

    public RulerNode getRulerNode() {
        return rulerNode;
    }

    public PNode getOriginNode() {
        return originNode;
    }

    public void resetAll() {
        rulerNode.setVisible( false );
    }
}
