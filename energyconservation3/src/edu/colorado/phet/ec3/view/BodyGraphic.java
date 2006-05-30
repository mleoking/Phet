/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.EC3Module;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:21 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class BodyGraphic extends PNode {
    private Body body;
    private EC3Module ec3Module;
    private PPath boundsDebugPPath;
    private PImage skater;
    private PPath centerDebugger;
    private JetPackGraphic jetPackGraphic;
//    private boolean debugCenter = true;
    private boolean debugCenter = false;

    public BodyGraphic( final EC3Module ec3Module, Body body ) {
        this.ec3Module = ec3Module;
        this.body = body;
        boundsDebugPPath = new PPath( body.getShape() );
        boundsDebugPPath.setStroke( null );
        boundsDebugPPath.setPaint( new Color( 0, 0, 255, 128 ) );
        jetPackGraphic = new JetPackGraphic( this );
        addChild( jetPackGraphic );
        try {
            BufferedImage image = ImageLoader.loadBufferedImage( "images/skater3.png" );
            skater = new PImage( image );
            addChild( skater );

            centerDebugger = new PPath();
            centerDebugger.setStroke( null );
            centerDebugger.setPaint( Color.red );
            if( debugCenter ) {
                addChild( centerDebugger );
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }


        ec3Module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( BodyGraphic.this );
                boolean okToTranslate = true;
                if( getBody().getLocatedShape().getBounds2D().getMinY() < 0 && delta.getHeight() < 0 ) {
                    okToTranslate = false;
                }
                PBounds b = getFullBounds();
                localToGlobal( b );
                ec3Module.getEnergyConservationCanvas().getLayer().globalToLocal( b );
                if( b.getMaxX() > ec3Module.getEnergyConservationCanvas().getWidth() && delta.getWidth() > 0 ) {
                    okToTranslate = false;
                }
                if( b.getMinX() < 0 && delta.getWidth() < 0 ) {
                    okToTranslate = false;
                }
                if( okToTranslate ) {
                    getBody().translate( delta.getWidth(), delta.getHeight() );
                }

            }
        } );
        addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                getBody().setUserControlled( true );
                getBody().setVelocity( 0, 0 );
            }

            public void mouseReleased( PInputEvent event ) {
                getBody().setUserControlled( false );
            }

            public void mouseDragged( PInputEvent event ) {
                getBody().setUserControlled( true );
                getBody().setVelocity( 0, 0 );
            }
        } );

        update();
    }

    public Body getBody() {
        return body;
    }

    public void setBody( Body body ) {
        this.body = body;
        update();
    }

    public PImage getSkater() {
        return skater;
    }

    public void update() {
        boundsDebugPPath.setPathTo( body.getLocatedShape() );

        skater.setTransform( createSkaterTransform() );
        centerDebugger.setPathTo( new Rectangle2D.Double( body.getAttachPoint().getX(), body.getAttachPoint().getY(), 0.1, 0.1 ) );
        if( body.getThrust().getMagnitude() != 0 ) {
            setFlamesVisible( true );
        }
        else {
            setFlamesVisible( false );
        }
        updateFlames();
    }

    public AffineTransform createTransform( double objWidth, double objHeight, int imageWidth, int imageHeight ) {
        AffineTransform t = new AffineTransform();
        t.concatenate( body.getTransform() );
        t.translate( -getBodyModelWidth() / 2, 0 );
        t.translate( 0, -AbstractSpline.SPLINE_THICKNESS / 2.0 );
        t.scale( objWidth / imageWidth, objHeight / imageHeight );

        if( body.isFacingRight() ) {
            t.concatenate( AffineTransform.getScaleInstance( -1, 1 ) );
            t.translate( -imageWidth * 3 / 2.0, 0 );
        }
        else {
//            t.concatenate( AffineTransform.getScaleInstance( -1, 1 ) );
            t.translate( imageWidth / 2, 0 );
        }
        return t;
    }

    public boolean isFacingRight() {
        return body.isFacingRight();
    }

    public AffineTransform createSkaterTransform() {
        return createTransform( getBodyModelWidth(),
                                getBodyModelHeight(),
                                skater.getImage().getWidth( null ),
                                skater.getImage().getHeight( null ) );
    }

    public double getBodyModelHeight() {
        return body.getShape().getBounds2D().getHeight();
    }

    public double getBodyModelWidth() {
        return body.getShape().getBounds2D().getWidth();
    }

    private void updateFlames() {
        jetPackGraphic.update();
    }

    private void setFlamesVisible( boolean flamesVisible ) {
        jetPackGraphic.setVisible( flamesVisible );
    }

    public boolean isBoxVisible() {
        return getChildrenReference().contains( boundsDebugPPath );
    }

    public void setBoxVisible( boolean v ) {
        if( v && !isBoxVisible() ) {
            addChild( boundsDebugPPath );
        }
        else if( !v && isBoxVisible() ) {
            removeChild( boundsDebugPPath );
        }
    }
}
