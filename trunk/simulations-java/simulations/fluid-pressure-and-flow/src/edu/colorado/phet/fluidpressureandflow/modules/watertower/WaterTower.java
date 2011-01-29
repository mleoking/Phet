// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.watertower;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * @author Sam Reid
 */
public class WaterTower {
    public static final int MAX_Y = 18;
    private Property<ImmutableVector2D> tankBottomCenter = new Property<ImmutableVector2D>( new ImmutableVector2D( 0, MAX_Y ) );
    private static double TANK_RADIUS = 5;
    private static double TANK_HEIGHT = 10;
    private static final int LEG_EXTENSION = 3;
    private static double tankVolume = Math.PI * TANK_RADIUS * TANK_RADIUS * TANK_HEIGHT;
    private Property<Double> fluidVolumeProperty = new Property<Double>( tankVolume );//meters cubed

    public Rectangle2D.Double getTankShape() {
        return new Rectangle2D.Double( tankBottomCenter.getValue().getX() - TANK_RADIUS, tankBottomCenter.getValue().getY(), TANK_RADIUS * 2, TANK_HEIGHT );
    }

    public Property<ImmutableVector2D> getTankBottomCenter() {
        return tankBottomCenter;
    }

    public Shape getSupportShape() {
        final Point2D bottomCenter = tankBottomCenter.getValue().toPoint2D();
        final Point2D.Double leftLegTop = new Point2D.Double( bottomCenter.getX() - TANK_RADIUS / 2, bottomCenter.getY() );
        final Point2D.Double leftLegBottom = new Point2D.Double( bottomCenter.getX() - TANK_RADIUS / 2 - LEG_EXTENSION, 0 );
        final Point2D.Double rightLegTop = new Point2D.Double( bottomCenter.getX() + TANK_RADIUS / 2, bottomCenter.getY() );
        final Point2D.Double rightLegBottom = new Point2D.Double( bottomCenter.getX() + TANK_RADIUS / 2 + LEG_EXTENSION, 0 );

        //These functions allow us to map a fraction of the distance up one of the water tower legs to a point on the leg, for adding crossbeams
        final Function.LinearFunction yMap = new Function.LinearFunction( 0, 1, 0, bottomCenter.getY() );
        final Function.LinearFunction xMapLeft = new Function.LinearFunction( 0, 1, bottomCenter.getX() - TANK_RADIUS / 2 - LEG_EXTENSION, bottomCenter.getX() - TANK_RADIUS / 2 );
        final Function.LinearFunction xMapRight = new Function.LinearFunction( 0, 1, bottomCenter.getX() + TANK_RADIUS / 2 + LEG_EXTENSION, bottomCenter.getX() + TANK_RADIUS / 2 );

        DoubleGeneralPath path = new DoubleGeneralPath();
        addLine( path, leftLegTop, leftLegBottom );
        addLine( path, rightLegTop, rightLegBottom );
        addStruts( path, yMap, xMapLeft, xMapRight, 0.1, 0.4 );
        addStruts( path, yMap, xMapLeft, xMapRight, 0.5, 0.8 );
        return new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER ).createStrokedShape( path.getGeneralPath() );
    }

    private void addStruts( DoubleGeneralPath path, Function.LinearFunction yMap, Function.LinearFunction xMapLeft, Function.LinearFunction xMapRight, double leftFraction, double rightFraction ) {
        addStrut( path, yMap, xMapLeft, xMapRight, leftFraction, rightFraction );
        addStrut( path, yMap, xMapLeft, xMapRight, rightFraction, leftFraction );
    }

    private void addStrut( DoubleGeneralPath path, Function.LinearFunction yMap, Function.LinearFunction xMapLeft, Function.LinearFunction xMapRight, double leftFraction, double rightFraction ) {
        addLine( path, new Point2D.Double( xMapLeft.evaluate( leftFraction ), yMap.evaluate( leftFraction ) ), new Point2D.Double( xMapRight.evaluate( rightFraction ), yMap.evaluate( rightFraction ) ) );
    }

    private void addLine( DoubleGeneralPath path, Point2D.Double a, Point2D.Double b ) {
        path.moveTo( a );
        path.lineTo( b );
    }

    public Property<Double> getFluidVolumeProperty() {
        return fluidVolumeProperty;
    }

    public Shape getWaterShape() {
        return new Rectangle2D.Double( tankBottomCenter.getValue().getX() - TANK_RADIUS, tankBottomCenter.getValue().getY(), TANK_RADIUS * 2, getWaterHeight() );
    }

    private double getWaterHeight() {
        return fluidVolumeProperty.getValue() / Math.PI / TANK_RADIUS / TANK_RADIUS;
    }

    public Point2D getHoleLocation() {
        return new Point2D.Double( tankBottomCenter.getValue().getX() + TANK_RADIUS, tankBottomCenter.getValue().getY() );
    }
}
