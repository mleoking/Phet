/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Particle;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.theramp.view.FloorGraphic;
import edu.colorado.phet.theramp.view.RampPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 3, 2005
 * Time: 8:11:45 AM
 * Copyright (c) Aug 3, 2005 by Sam Reid
 */

public class FireDog extends PNode {
    private PImage image;
    private RampModule module;

    public FireDog( RampModule module ) {
        this.module = module;
        try {
            BufferedImage newImage = ImageLoader.loadBufferedImage( "images/firedog.gif" );
            newImage = BufferedImageUtils.rescaleYMaintainAspectRatio( null, newImage, 100 );
            image = new PImage( newImage );
//            image = new BoidGraphic(module.getPhetPCanvas() );// ImageLoader.loadBufferedImage( "images/firedog.gif" ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        addChild( image );
        initImage();
    }

    private void initImage() {
        image.setTransform( new AffineTransform() );
        double r2 = getFloorY();
        image.setOffset( -image.getWidth(), r2 - image.getHeight() );
    }

    private double getFloorY() {
        Rectangle2D r2 = module.getRampPanel().getRampWorld().getLeftBarrierGraphic().getGlobalFullBounds();
        return globalToLocal( r2 ).getMaxY();
    }

    private double getRampX() {
        Rectangle2D r2 = module.getRampPanel().getRampWorld().getRampGraphic().getGlobalFullBounds();
        return globalToLocal( r2 ).getMaxY();
    }

    private double getFloorYOrig() {
        FloorGraphic floorGraphic = module.getRampPanel().getRampWorld().getGroundGraphic();

        double r2 = globalToLocal( floorGraphic.getGlobalFullBounds() ).getMaxY();
        return r2;
    }

    private class MoveToFire extends PActivity {
        private double randomInset;
        private double runToFireSpeed = 23;

        public MoveToFire() {
            super( -1 );
            randomInset = random.nextGaussian() * 45;
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            FireDog.this.translate( runToFireSpeed, 0 );
            if( FireDog.this.getOffset().getX() > FireDog.this.getRampX() - randomInset ) {
                terminate();
            }
        }

        protected void activityFinished() {
            super.activityFinished();
            addWaterDrop();
            getRampPanel().getRoot().addActivity( new ExpelWater() );
            getRampPanel().getRoot().addActivity( new MoveWater() );
        }
    }

    private class MoveWater extends PActivity {

        public MoveWater() {
            super( -1 );
        }

        long lastTime = -1;

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            double dt = lastTime == -1 ? getStepRate() / 1000.0 : ( elapsedTime - lastTime ) / 1000.0;
            dt *= 1.3;
            for( int i = 0; i < waterDrops.size(); i++ ) {
                WaterDrop waterDrop = (WaterDrop)waterDrops.get( i );
                waterDrop.propagate( dt );
            }
            if( waterDrops.size() == 0 ) {
                getRampPanel().getRoot().getActivityScheduler().removeActivity( this );
            }
            lastTime = elapsedTime;
        }
    }

    private void addWaterDrop() {
        WaterDrop waterDrop = new WaterDrop();
        getRampPanel().addChild( waterDrop );
        waterDrops.add( waterDrop );
    }

    private class ExpelWater extends PActivity {

        public ExpelWater() {
            super( 1000 );
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            //fire a water particle.
            addWaterDrop();
        }


        protected void activityFinished() {
            getRampPanel().getRoot().addActivity( new WalkAway() );
        }
    }

    private class WalkAway extends PActivity {

        public WalkAway() {
            super( 6000 );
            image.setImage( BufferedImageUtils.flipXMacFriendly( (BufferedImage)image.getImage() ) );
        }

        protected void activityStep( long elapsedTime ) {
            super.activityStep( elapsedTime );
            FireDog.this.translate( -6, 0 );
        }

        protected void activityFinished() {
            getRampPanel().removeChild( FireDog.this );
        }
    }

    BufferedImage dropImage;
    ArrayList waterDrops = new ArrayList();
    Random random = new Random();

    class WaterDrop extends PNode {
        Particle particle = new Particle();

        public WaterDrop() {

            if( dropImage == null ) {
                try {
//                    dropImage = ImageLoader.loadBufferedImage( "images/drop.gif" );
                    dropImage = ImageLoader.loadBufferedImage( "images/drop3.gif" );
//                    dropImage = BufferedImageUtils.rescaleYMaintainAspectRatio( null, dropImage, 14 );
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }

            PImage pimage = new PImage( dropImage );
            pimage.scale( 0.3 );
            addChild( pimage );

            Point2D point = new Point2D.Double( image.getGlobalFullBounds().getMaxX(), image.getGlobalFullBounds().getCenterY() + 20 );

            particle.setPosition( globalToLocal( point ) );
            double dx = random.nextGaussian() * 30;
            double dy = random.nextGaussian() * 20;
            particle.setVelocity( 200.0 + dx, -120 - dy );
            particle.setAcceleration( 0, 200 );
            setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        }

        public void propagate( double dt ) {
//            System.out.println( "dt = " + dt );
            particle.stepInTime( dt );
            update();
        }

        private void update() {
            setTransform( new AffineTransform() );
            setOffset( particle.getPosition() );

            Vector2D v = particle.getVelocity();
            double angle = v.getAngle();

            rotate( angle - Math.PI / 2 );
            if( particle.getPosition().getY() > getFloorY() ) {
                getRampPanel().removeChild( this );
                waterDrops.remove( this );
                module.getRampPhysicalModel().clearHeat();
            }
        }
    }

    public void putOutFire() {
        getRampPanel().addChild( this );
        getRampPanel().getRoot().addActivity( new MoveToFire() );
    }

    private RampPanel getRampPanel() {
        RampPanel canvas = module.getRampPanel();
        return canvas;
    }
}
