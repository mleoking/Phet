/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view.arrows;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.ShadowHTMLGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.view.BlockGraphic;
import edu.colorado.phet.theramp.view.RampUtil;
import edu.colorado.phet.theramp.view.RampWorld;
import edu.colorado.phet.theramp.view.SurfaceGraphic;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Feb 13, 2005
 * Time: 8:22:35 PM
 * Copyright (c) Feb 13, 2005 by Sam Reid
 */
public class ForceArrowGraphic extends CompositePhetGraphic {
    private double arrowTailWidth = 30;
    private double arrowHeadHeight = 55;

    private String name;
    private Color color;
    private int dy;
    private AbstractArrowSet.ForceComponent forceComponent;
    private ShadowHTMLGraphic textGraphic;
    private PhetShapeGraphic shapeGraphic;
    private final Font font = new Font( "Lucida Sans", Font.BOLD, 14 );
    private Arrow lastArrow;
    private BlockGraphic blockGraphic;
    private boolean userVisible = true;
    private boolean nonZero = true;
    private String sub;
    private static final double THRESHOLD = 10E-8;
    private Color baseColor;

    public ForceArrowGraphic( Component component, String name, Color color,
                              int dy, AbstractArrowSet.ForceComponent forceComponent,
                              BlockGraphic blockGraphic ) {
        this( component, name, color, dy, forceComponent, blockGraphic, null );
    }

    public ForceArrowGraphic( Component component, String name, Color color,
                              int dy, AbstractArrowSet.ForceComponent forceComponent,
                              BlockGraphic blockGraphic, String sub ) {
        super( component );
        this.blockGraphic = blockGraphic;
        this.name = name;
        this.baseColor = color;
        this.sub = sub;
        if( sub != null && !sub.trim().equals( "" ) ) {
            name = "<html>" + name + "<sub>" + sub + "</sub></html>";
        }
//        color = RampUtil.transparify( color, 128 );
        color = RampUtil.transparify( color, 175 );
        this.color = color;
        this.dy = dy;
        this.forceComponent = forceComponent;
        textGraphic = new ShadowHTMLGraphic( component, name, font, Color.black, 1, 1, Color.yellow );
        shapeGraphic = new PhetShapeGraphic( component, null, color, new BasicStroke( 1 ), Color.black );
        addGraphic( shapeGraphic );
        addGraphic( textGraphic );
        setIgnoreMouse( true );
        update();
    }

    public AbstractArrowSet.ForceComponent getForceComponent() {
        return forceComponent;
    }

    public void update() {
        AbstractVector2D force = new ImmutableVector2D.Double( forceComponent.getForce() );
        force = force.getScaledInstance( RampModule.FORCE_LENGTH_SCALE );
//        if( name.equals( AbstractArrowSet.TOTAL ) ) {
//            System.out.println( "force = " + force );
//        }
        if( force.getMagnitude() <= THRESHOLD ) {
            setVisible( false );
            nonZero = false;
            return;
        }
        else {
            nonZero = true;
            setVisible( true && userVisible );
        }
        RampWorld rampWorld = getRampWorld();
        if( rampWorld == null ) {
            System.out.println( "rampWorld = " + rampWorld );
            return;
        }
        Point viewCtr = RectangleUtils.getCenter( blockGraphic.getObjectGraphic().getBoundsInAncestor( rampWorld ) );
        viewCtr = translate( viewCtr );
        Point2D.Double tail = new Point2D.Double( viewCtr.x, viewCtr.y );
        Point2D tip = new Vector2D.Double( force.getX(), force.getY() ).getDestination( tail );
        Arrow forceArrow = new Arrow( tail, tip, arrowHeadHeight, arrowHeadHeight, arrowTailWidth, 0.5, false );

        Shape forceArrowShape = forceArrow.getShape();
        if( this.lastArrow == null || !this.lastArrow.equals( forceArrow ) ) {
            shapeGraphic.setShape( forceArrowShape );

            Shape forceArrowBody = forceArrow.getTailShape();
            double tgHeight = textGraphic.getHeight();
            double arrowHeight = forceArrowBody.getBounds().getHeight();
            double y = forceArrowBody.getBounds().getY() + arrowHeight / 2 - tgHeight / 2;
            textGraphic.setLocation( forceArrowBody.getBounds().x, (int)y );
        }
        this.lastArrow = forceArrow;
    }

    private RampWorld getRampWorld() {
        PhetGraphic parent = getParent();
        while( parent != null ) {
            if( parent instanceof RampWorld ) {
                return (RampWorld)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private Point translate( Point viewCtr ) {
        SurfaceGraphic surfaceGraphic = blockGraphic.getCurrentSurfaceGraphic();
        double viewAngle = surfaceGraphic.getViewAngle();
//        System.out.println( "viewAngle = " + viewAngle );
        Point offset = new Point( (int)( Math.sin( viewAngle ) * dy ), (int)( Math.cos( viewAngle ) * dy ) );
        return new Point( viewCtr.x + offset.x, viewCtr.y - offset.y );
    }

    public String getName() {
        return name;
    }

    public void setUserVisible( boolean userVisible ) {
        this.userVisible = userVisible;
        setVisible( userVisible && nonZero );
    }

    public void setPaint( Paint paint ) {
        shapeGraphic.setPaint( paint );
    }

    public Color getBaseColor() {
        return baseColor;
    }
}
