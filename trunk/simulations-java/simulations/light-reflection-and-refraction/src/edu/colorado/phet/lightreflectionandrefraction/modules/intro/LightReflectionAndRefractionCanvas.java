// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.view.LaserNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LightRayNode;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas extends PhetPCanvas {
    private PNode rootNode;
    public final BooleanProperty showNormal = new BooleanProperty( true );
    public final BooleanProperty showProtractor = new BooleanProperty( false );

    public LightReflectionAndRefractionCanvas( final LRRModel model ) {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, LRRModel.STAGE_SIZE ) );

        setBackground( Color.black );

        final ModelViewTransform transform = ModelViewTransform.createRectangleInvertedYMapping(
                new Rectangle2D.Double( -model.getWidth() / 2, -model.getHeight() / 2, model.getWidth(), model.getHeight() ),
                new Rectangle2D.Double( 0, 0, LRRModel.STAGE_SIZE.width *
                                              0.85 //Account for the control panel
                        , LRRModel.STAGE_SIZE.height ) );

        final VoidFunction1<LightRay> addLightRayNode = new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                final LightRayNode node = new LightRayNode( transform, lightRay );
                addChild( node );
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        removeChild( node );
                    }
                } );
            }
        };

        addChild( new MediumNode( transform, model.topMedium ) );
        addChild( new MediumNode( transform, model.bottomMedium ) );

        addChild( new LaserNode( transform, model.getLaser() ) );

        addChild( new ControlPanel( new VerticalLayoutPanel() {{
            add( new VerticalLayoutPanel() {{
                setBorder( new PhetTitledBorder( "Index of Refraction" ) );
                add( new IndexOfRefractionSlider( model.topMedium, model.colorMappingFunction, "n1=" ) );
                add( new IndexOfRefractionSlider( model.bottomMedium, model.colorMappingFunction, "n2=" ) );
            }} );
            add( new VerticalLayoutPanel() {{
                setBorder( new PhetTitledBorder( "View" ) );
                final Property<Boolean> ray = new Property<Boolean>( true );
                add( new PropertyRadioButton<Boolean>( "Ray", ray, true ) );
                add( new PropertyRadioButton<Boolean>( "Wave", ray, false ) {{setEnabled( false );}} );
            }} );
            add( new VerticalLayoutPanel() {{
                setBorder( new PhetTitledBorder( "Tools" ) );
                add( new PropertyCheckBox( "Show Normal", showNormal ) );
                add( new PropertyCheckBox( "Protractor", showProtractor ) );
                add( new PropertyCheckBox( "Intensity Meter", model.getIntensityMeter().enabled ) );
            }} );
        }} ) {{
            setOffset( LRRModel.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 0 );
        }} );

        //Normal Line
        double x = transform.modelToViewX( 0 );
        double y1 = transform.modelToViewY( 0 - model.getHeight() / 3 );
        double y2 = transform.modelToViewY( 0 + model.getHeight() / 3 );
        addChild( new PhetPPath( new Line2D.Double( x, y1, x, y2 ), new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 10, 10 }, 0 ), Color.yellow ) {{
            showNormal.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showNormal.getValue() );
                }
            } );
        }} );

        //Protractor
        addChild( new ProtractorNode( transform, showProtractor ) );

        addChild( new IntensityMeterNode( transform, model.getIntensityMeter() ) );

        for ( LightRay lightRay : model.getRays() ) {
            addLightRayNode.apply( lightRay );
        }
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( final LightRay lightRay ) {
                addLightRayNode.apply( lightRay );
            }
        } );

        //add a line that will show the border between the mediums even when both n's are the same... Just a thin line will be fine.
        addChild( new PhetPPath( transform.modelToView( new Line2D.Double( -1, 0, 1, 0 ) ), new BasicStroke( 0.5f ), Color.gray ) {{
            setPickable( false );
        }} );
    }

    public static class ControlPanel extends PNode {
        public ControlPanel( JComponent controlPanel ) {
            final PSwing pswing = new PSwing( controlPanel );
            addChild( pswing );
//            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, pswing.getFullBounds().getWidth(), pswing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.blue ) );
        }
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}
