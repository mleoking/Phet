// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.view.RelativeDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class WaterTowerNode extends PNode {
    public WaterTowerNode( final ModelViewTransform transform, final WaterTower waterTower ) {

        //Handle
        addChild( new PImage( RESOURCES.getImage( "handle.png" ) ) {{
            addInputEventListener( new RelativeDragHandler( this, transform, waterTower.getTankBottomCenter(), new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D modelLocation ) {
                    return new Point2D.Double( waterTower.getTankBottomCenter().getValue().getX(), MathUtil.clamp( 0, modelLocation.getY(), WaterTower.MAX_Y ) );
                }
            } ) );
            scale( 1.75 );
            addInputEventListener( new CursorHandler() );
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D tankTopCenter = waterTower.getTankTopCenter();
                    final Point2D view = transform.modelToView( tankTopCenter );
                    setOffset( view.getX(), view.getY() - getFullBounds().getHeight() );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.gray, new BasicStroke( 5 ), Color.darkGray ) {{ // tank
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getTankShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.black ) {{
            waterTower.getTankBottomCenter().addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getSupportShape() ) );
                }
            } );
        }} );

        addChild( new PhetPPath( Color.blue ) {{
            final SimpleObserver updateWaterLocation = new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( waterTower.getWaterShape() ) );
                }
            };
            waterTower.getTankBottomCenter().addObserver( updateWaterLocation );
            waterTower.getFluidVolumeProperty().addObserver( updateWaterLocation );
            setPickable( false );
        }} );

        //Panel covering the hole
        addChild( new PImage( BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "panel.png" ), 50 ) ) {{
            final Property<ImmutableVector2D> modelLocationRelativeToWaterTower = new Property<ImmutableVector2D>( new ImmutableVector2D( waterTower.getTankShape().getWidth() / 2, 0 ) );
            final SimpleObserver updatePanelLocation = new SimpleObserver() {
                public void update() {
                    ImmutableVector2D viewPoint = transform.modelToView( modelLocationRelativeToWaterTower.getValue().getAddedInstance( waterTower.getTankBottomCenter().getValue() ) );
                    setOffset( viewPoint.getX(), viewPoint.getY() - getFullBounds().getHeight() );
                }
            };
            modelLocationRelativeToWaterTower.addObserver( updatePanelLocation );
            waterTower.getTankBottomCenter().addObserver( updatePanelLocation );
            addInputEventListener( new RelativeDragHandler( this, transform, modelLocationRelativeToWaterTower, new Function1<Point2D, Point2D>() {
                public Point2D apply( Point2D point2D ) {
                    return new Point2D.Double( waterTower.getTankShape().getWidth() / 2, MathUtil.clamp( 0, point2D.getY(), waterTower.getTankShape().getHeight() ) );
                }
            } ) );
            addInputEventListener( new CursorHandler() );
        }} );
    }
}
