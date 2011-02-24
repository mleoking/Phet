// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.bendinglight.view.*;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class IntroCanvas extends BendingLightCanvas<IntroModel> {
    public IntroCanvas( IntroModel model, BooleanProperty moduleActive, final Resettable resetAll ) {
        super( model, moduleActive, new Function1<Double, Double>() {
            public Double apply( Double angle ) {
                if ( angle < -Math.PI / 2 ) { angle = Math.PI; }
                if ( angle < Math.PI / 2 && angle > 0 ) { angle = Math.PI / 2; }
                return angle;
            }
        }, new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble < Math.PI;
            }
        }, new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble > Math.PI / 2;
            }
        }, true, resetAll );
        mediumNode.addChild( new MediumNode( transform, model.topMedium ) );
        mediumNode.addChild( new MediumNode( transform, model.bottomMedium ) );

        beforeLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.topMedium, model.colorMappingFunction, "Material:" ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) - 10 - getFullBounds().getHeight() );
        }} );
        beforeLightLayer.addChild( new ControlPanelNode( new MediumControlPanel( this, model.bottomMedium, model.colorMappingFunction, "Material:" ) ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) + 10 );
        }} );

        //add a line that will show the border between the mediums even when both n's are the same... Just a thin line will be fine.
        beforeLightLayer.addChild( new PhetPPath( transform.modelToView( new Line2D.Double( -1, 0, 1, 0 ) ), new BasicStroke( 0.5f ), Color.gray ) {{
            setPickable( false );
        }} );

        beforeLightLayer.addChild( new NormalLine( transform, model.getHeight() ) {{
            showNormal.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showNormal.getValue() );
                }
            } );
        }} );

        beforeLightLayer.addChild( new ControlPanelNode( new PNode() {{
            final PText title = new PText( "Laser View" ) {{setFont( labelFont );}};
            addChild( title );
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new PropertyRadioButton<LaserView>( "Ray", laserView, LaserView.RAY ) {{setFont( labelFont );}} );
                add( new PropertyRadioButton<LaserView>( "Wave", laserView, LaserView.WAVE ) {{setFont( labelFont );}} );
            }} ) {{
                setOffset( 0, title.getFullBounds().getMaxY() );
            }} );
        }} ) {{
            setOffset( 5, 5 );
        }} );

        beforeLightLayer.addChild( new ControlPanelNode( new ToolboxNode( this, transform, showProtractor, showNormal, model.getIntensityMeter() ) ) {{
            setOffset( 10, stageSize.height - getFullBounds().getHeight() - 10 );
        }} );

        beforeLightLayer.addChild( new BendingLightResetAllButtonNode( resetAll, this ) {{
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - 10, stageSize.getHeight() - getFullBounds().getHeight() - 10 );
        }} );
    }
}