/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.waveinterference.model.Lattice2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Mar 23, 2006
 * Time: 12:25:21 AM
 * Copyright (c) Mar 23, 2006 by Sam Reid
 */

public class PressureWaveGraphic extends PNode {
    private Lattice2D lattice;
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private BufferedImage blueImageORIG;
    private int spacingBetweenCells = 10;
    private ArrayList particles = new ArrayList();
    private BufferedImage pinkImageORIG;
    private BufferedImage blueImage;
    private BufferedImage pinkImage;
    private double acceleration = 0.3;
    private double maxVelocity = 3;
    private double friction = 0.97;

    public PressureWaveGraphic( Lattice2D lattice, LatticeScreenCoordinates latticeScreenCoordinates ) {
        this.lattice = lattice;
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        try {
            blueImageORIG = ImageLoader.loadBufferedImage( "images/particle-pink.gif" );
            pinkImageORIG = ImageLoader.loadBufferedImage( "images/particle-blue.gif" );
            blueImage = BufferedImageUtils.rescaleYMaintainAspectRatio( blueImageORIG, 20 );
            pinkImage = BufferedImageUtils.rescaleYMaintainAspectRatio( pinkImageORIG, 20 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        Random random = new Random();
        int MOD = 3;
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                if( i % MOD == 0 && j % MOD == 0 ) {

//                    Particle particle = new Particle( blueImage, i, j );
                    Particle particle = new Particle( random.nextBoolean() ? pinkImage : blueImage, i, j );
                    addParticle( particle );
                }
            }
        }
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateBounds();
            }
        } );
        updateBounds();
        reorderChildren();
    }

    private void updateBounds() {
        setOffset( latticeScreenCoordinates.toScreenCoordinates( 0, 0 ) );
        int spacingBetweenCells = (int)latticeScreenCoordinates.getCellWidth();
        setSpaceBetweenCells( spacingBetweenCells );
    }

    private void reorderChildren() {
        ArrayList all = new ArrayList( particles );
        super.removeAllChildren();
        Collections.shuffle( all );
        for( int i = 0; i < all.size(); i++ ) {
            Particle particle = (Particle)all.get( i );
            addChild( particle );
        }
    }

    public void setParticleImageSize( int height ) {
        BufferedImage newBlue = BufferedImageUtils.rescaleYMaintainAspectRatio( blueImageORIG, height );
        BufferedImage newPink = BufferedImageUtils.rescaleYMaintainAspectRatio( pinkImageORIG, height );
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            if( particle.getImage() == blueImage ) {
                particle.setImage( newBlue );
            }
            else {
                particle.setImage( newPink );
            }
        }
        pinkImage = newPink;
        blueImage = newBlue;
    }

    public double getImageSize() {
        return pinkImage.getHeight();
    }

    private void addParticle( Particle particle ) {
        particles.add( particle );
        addChild( particle );
    }

    public void setSpaceBetweenCells( int spacingBetweenCells ) {
        this.spacingBetweenCells = spacingBetweenCells;
        update();
    }

    public double getDistBetweenCells() {
        return spacingBetweenCells;
    }

    public void setParticleAcceleration( double acceleration ) {
        this.acceleration = acceleration;
    }

    public double getParticleAcceleration() {
        return acceleration;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity( double value ) {
        this.maxVelocity = value;
    }

    public double getFriction() {
        return friction;
    }

    public void setFriction( double friction ) {
        this.friction = friction;
    }

    private Random random = new Random();

    class Particle extends PImage {
        private int homeX;
        private int homeY;
        private double a;
        private double b;
        private double speed = 4.0;//pixels per time step
        private Vector2D velocity = new Vector2D.Double();
        private BufferedImage newImage;


        public Particle( BufferedImage newImage, int i, int j ) {
            super( newImage );
            this.newImage = newImage;
            this.homeX = i;
            this.homeY = j;

            this.a = i * spacingBetweenCells;
            this.b = j * spacingBetweenCells;
            update();
        }

        public void update() {
            //look near to x,y (but don't stray from homeX and homeY)
            double bestValue = Double.NEGATIVE_INFINITY;
            int windowSize = 8;
            Point bestPoint = null;
            for( int i = -windowSize / 2; i <= windowSize / 2; i++ ) {
                for( int j = -windowSize / 2; j <= windowSize / 2; j++ ) {
                    if( inBounds( homeX + i, homeY + j ) ) {
//                        if( bestPoint == null || (( lattice.getValue( homeX + i, homeY + j ) > bestValue ) && new Point( homeX + i, homeY + j ).distance( homeX, homeY ) < 5 ))
                        if( bestPoint == null || ( lattice.getValue( homeX + i, homeY + j ) > bestValue && new Point( homeX + i, homeY + j ).distance( homeX, homeY ) <= windowSize / 2 ) )
                        {
                            bestPoint = new Point( homeX + i, homeY + j );
                            bestValue = lattice.getValue( homeX + i, homeY + j );
                        }
                    }
                }
            }
            if( bestPoint != null ) {
                //step towards the peak
                double prefX = bestPoint.x * spacingBetweenCells;
                double prefY = bestPoint.y * spacingBetweenCells;
                Vector2D.Double vec = new Vector2D.Double( new Point2D.Double( a, b ), new Point2D.Double( prefX, prefY ) );
                double accelScale = 1.0;
                double frictionScale = 1.0;
                if( Math.abs( bestValue ) < 0.01 ) {
                    prefX = homeX * spacingBetweenCells;
                    prefY = homeY * spacingBetweenCells;
                    vec.rotate( random.nextDouble() * Math.PI * 2 );
                    accelScale = 0.5;
                    frictionScale = 1.0 / friction * 0.99;
                    vec = new Vector2D.Double( new Point2D.Double( a, b ), new Point2D.Double( prefX, prefY ) );
                }
//            stepToTarget( vec );
                accelerateToTarget( vec, accelScale, frictionScale );
//            accelerateFromNeighbors();//very expensive
            }

        }

//        private void accelerateFromNeighbors() {
//            double netForceX=0;
//            double netForceY=0;
////            Vector2D netForce=new Vector2D.Double( );
//            for( int i = 0; i < particles.size(); i++ ) {
//                Particle particle = (Particle)particles.get( i );
////                netForce.add( getForce(particle));
//                netForceX+=particle.getX();
//                netForceX-=particle.getX();
//            }
//            velocity=velocity.add(new Vector2D.Double( netForceX,netForceY ));
//        }

//        private AbstractVector2D getForce( Particle particle ) {
//            return new Vector2D.Double();
//        }

        private void accelerateToTarget( Vector2D.Double vec, double accelScale, double frictionScale ) {
//            double acceleration = 0.4;
            if( vec.getMagnitude() >= 1.2 ) {
                vec.normalize();
//                double finalAcceleration = acceleration * ( bestValue / 2.0 + 0.5 );
//                double finalAcceleration = acceleration;
                double finalAcceleration = acceleration * accelScale;
//                if( Math.abs( bestValue ) < 0.1 ) {
//                    finalAcceleration = 0;
//                }
                vec.scale( finalAcceleration );
//                System.out.println( "vec = " + vec );

                velocity = velocity.add( vec );
//                double maxVelocity = 15;
                if( velocity.getX() > maxVelocity ) {
                    velocity.setX( maxVelocity );
                }
                if( velocity.getY() > maxVelocity ) {
                    velocity.setY( maxVelocity );
                }
                if( velocity.getX() < -maxVelocity ) {
                    velocity.setX( -maxVelocity );
                }
                if( velocity.getY() < -maxVelocity ) {
                    velocity.setY( -maxVelocity );
                }
                velocity.scale( friction * frictionScale );//friction
                Point2D dest = velocity.getDestination( new Point2D.Double( a, b ) );
                a = dest.getX();
                b = dest.getY();
                setOffset( a, b );

            }
        }

        private void stepToTarget( Vector2D.Double vec ) {
            if( vec.getMagnitude() >= 1.2 ) {
                vec.normalize();
                vec.scale( speed );
//                System.out.println( "vec = " + vec );
                Point2D dest = vec.getDestination( new Point2D.Double( a, b ) );
                a = dest.getX();
                b = dest.getY();
                setOffset( a, b );

            }
        }

        private boolean inBounds( int i, int j ) {
            return i > 0 && j > 0 && i < lattice.getWidth() && j < lattice.getHeight();
        }
    }

    public void removeAllChildren() {
        super.removeAllChildren();
        particles.clear();
    }

    public void update() {
        //each particle moves toward high wave values near it's neighborhood.
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            particle.update();
        }
    }

    public void updateORIG() {
        removeAllChildren();
        for( int i = 0; i < lattice.getWidth(); i++ ) {
            for( int j = 0; j < lattice.getHeight(); j++ ) {
                Particle particle = new Particle( blueImageORIG, i, j );
                if( lattice.getValue( i, j ) > 0.25 ) {
                    addParticle( particle );
                }
            }
        }
    }

}
