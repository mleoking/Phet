/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;
import edu.colorado.phet.theramp.model.Ramp;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Feb 11, 2005
 * Time: 10:17:00 AM
 * Copyright (c) Feb 11, 2005 by Sam Reid
 */

public class RampGraphic extends GraphicLayerSet {
    private RampPanel rampPanel;
    private Ramp ramp;
    private ModelViewTransform2D transform2D;
    private double viewAngle;
    private PhetShapeGraphic surfaceGraphic;
    private PhetShapeGraphic floorGraphic;
    private PhetShapeGraphic jackGraphic;
    private int surfaceStrokeWidth = 12;
    private PhetShapeGraphic filledShapeGraphic;

    public RampGraphic( RampPanel rampPanel, final Ramp ramp ) {
        super( rampPanel );
        this.rampPanel = rampPanel;
        this.ramp = ramp;
        transform2D = new ModelViewTransform2D( new Rectangle2D.Double( -10, 0, 20, 10 ), new Rectangle( 0, 200, 800, 400 ) );

        Stroke stroke = new BasicStroke( 6.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        Stroke surfaceStroke = new BasicStroke( surfaceStrokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
        surfaceGraphic = new PhetShapeGraphic( rampPanel, null, surfaceStroke, Color.black );
        floorGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.black );
        jackGraphic = new PhetShapeGraphic( getComponent(), null, stroke, Color.blue );
        filledShapeGraphic = new PhetShapeGraphic( getComponent(), null, Color.lightGray );
        addGraphic( filledShapeGraphic );
        addGraphic( floorGraphic );
        addGraphic( jackGraphic );
        addGraphic( surfaceGraphic );

        surfaceGraphic.addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseMotionListener
            public void mouseDragged( MouseEvent e ) {
                Point pt = e.getPoint();
                Vector2D.Double vec = new Vector2D.Double( getViewOrigin(), pt );
                double angle = -vec.getAngle();
                angle = MathUtil.clamp( 0, angle, Math.PI / 2.0 );
                ramp.setAngle( angle );
            }
        } );
        surfaceGraphic.setCursorHand();

        updateRamp();
        ramp.addObserver( new SimpleObserver() {
            public void update() {
                updateRamp();
            }
        } );
//
//        floorGraphic.addTranslationListener( new TranslationListener() {
//            public void translationOccurred( TranslationEvent translationEvent ) {
//                translate( translationEvent.getDx(), translationEvent.getDy() );
//            }
//        } );
    }

    private Point getViewOrigin() {
        Point2D modelOrigin = ramp.getOrigin();
        final Point viewOrigin = transform2D.modelToView( modelOrigin );
        return viewOrigin;
    }

    private void updateRamp() {
        Point viewOrigin = getViewOrigin();
        Point2D modelDst = ramp.getEndPoint();
        Point viewDst = transform2D.modelToView( modelDst );
        Line2D.Double origSurface = new Line2D.Double( viewOrigin, viewDst );
        double origLength = new Vector2D.Double( origSurface.getP1(), origSurface.getP2() ).getMagnitude();
        Line2D line = RampUtil.getInstanceForLength( origSurface, origLength * 4 );
        surfaceGraphic.setShape( line );

        Point p2 = new Point( viewDst.x, viewOrigin.y );
        Line2D.Double floor = new Line2D.Double( viewOrigin, p2 );
        floorGraphic.setShape( floor );

        GeneralPath jackShape = createJackShape( p2, viewDst, 10 );
        jackGraphic.setShape( jackShape );

        DoubleGeneralPath path = new DoubleGeneralPath( viewOrigin );
        path.lineTo( floor.getP2() );
        path.lineTo( viewDst );
        path.closePath();
        filledShapeGraphic.setShape( path.getGeneralPath() );

        viewAngle = Math.atan2( viewDst.y - viewOrigin.y, viewDst.x - viewOrigin.x );
    }

    GeneralPath createJackShape( Point src, Point dst, int wavelength ) {
        DoubleGeneralPath path = new DoubleGeneralPath( src );
        path.lineTo( dst );
        return path.getGeneralPath();
    }

    public double getViewAngle() {
        return viewAngle;
    }

    public ModelViewTransform2D getTransform2D() {
        return transform2D;
    }

    public Ramp getRamp() {
        return ramp;
    }

    public int getSurfaceStrokeWidth() {
        return surfaceStrokeWidth;
    }

}
