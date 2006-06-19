/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.coreadditions.CubicUtil;
import edu.colorado.phet.nuclearphysics.Config;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;

/**
 * This class represents the energy profile of a particular atom.
 * <p/>
 * Here is my assumed model for the shape of the profile:
 * <ul>
 * <li>The width of the profile is the distance from tail to tail (i.e., twice the
 * distance from the center of the well to one of the tails).
 * <li>The tails flatten out at energy = 0
 * <li>The sides of the well are vertical, and the bottom is horizontal.
 * <li>The shape of the tails is exponential
 * </ul>
 */
public class EnergyProfile extends SimpleObservable implements SimpleObserver {
    private double width;
//    private double maxPotential;
//    private double wellDepth;
    private double wellWidth;
    private double maxEnergy;
    private double minEnergy;
    private Shape[] shape = new Shape[4];
//    private Point2D.Double endPt1 = new Point2D.Double();
//    private Point2D.Double ctrlPt1 = new Point2D.Double();
//    private Point2D.Double endPt2 = new Point2D.Double();
//    private Point2D.Double ctrlPt2A = new Point2D.Double();
//    private Point2D.Double ctrlPt2B = new Point2D.Double();
//    private Point2D.Double endPt3 = new Point2D.Double();
//    private Point2D.Double ctrlPt3 = new Point2D.Double();
//    private Point2D.Double endPt4 = new Point2D.Double();
//    private Point2D.Double ctrlPt4A = new Point2D.Double();
//    private Point2D.Double ctrlPt4B = new Point2D.Double();
//    private Point2D.Double endPt5 = new Point2D.Double();
//    private Point2D.Double ctrlPt5 = new Point2D.Double();
    private double alphaDecayX;
    private GeneralPath profilePath;
    private GeneralPath profileBackgroundPath;
    private CubicUtil cubicUtil;
    private Nucleus nucleus;
    private AffineTransform profileTx = new AffineTransform();


    public EnergyProfile( Nucleus nucleus ) {
        this.nucleus = nucleus;
        this.width = Config.defaultProfileWidth;
        this.minEnergy = -( 0.5 * nucleus.getNumNeutrons() );
        this.maxEnergy = minEnergy + 2 * nucleus.getNumProtons();
//        this.wellDepth = 0.5 * nucleus.getNumNeutrons();
//        this.maxPotential = 2 * nucleus.getNumProtons();
//        this.wellDepth = 0.5 * nucleus.getNumNeutrons();
        this.generate();
    }


    public double getWidth() {
        return width;
    }

    public void setWidth( double width ) {
        this.width = width;
        generate();
    }

    public double getMinEnergy() {
        return minEnergy;
    }

    public void setMinEnergy( double minEnergy ) {
        this.minEnergy = minEnergy;
    }

    public double getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy( double maxEnergy ) {
        this.maxEnergy = maxEnergy;
    }

    /**
     * Returns the perpendicular distance from a point to the
     * side of the profile. If the point is inside the hill,
     * the value returned is < 0. If the point is outside the
     * hill, the value returned is > 0.
     *
     * @param pt
     */
    public double getDistFromHill( Point2D.Double pt ) {
        double dx = Math.abs( pt.getX() ) - Math.abs( this.getHillX( pt.getY() ) );
        return dx;
    }

    /**
     * Generates the cubic splines that define the energy profile. Also computes
     * the x coordinate of the hill slope that is at the same y coordinat as the
     * bottom of the well.
     */
    private void generate() {

        // Draw the curve going up the left side of the potential profile
        Point2D endPt1 = new Point2D.Double( -getWidth() / 2, 0 );
//        endPt1.x = -getWidth() / 2;
//        endPt1.y = 0;
        Point2D endPt2 = new Point2D.Double( -getWidth() / 20, -maxEnergy );
//        endPt2.x = -getWidth() / 20;
//        endPt2.y = -getMaxPotential();

        Point2D ctrlPt1 = new Point2D.Double();
        ctrlPt1.setLocation( endPt1.getX() + ( ( endPt2.getX() - endPt1.getX() ) * 8 / 8 ),
                             endPt1.getY() );
        Point2D ctrlPt2A = new Point2D.Double();
        ctrlPt2A.setLocation( endPt2.getX() - ( ( endPt2.getX() - endPt1.getX() ) / 16 ),
                              endPt2.getY() );

        shape[0] = new CubicCurve2D.Double( endPt1.getX(), endPt1.getY(),
                                            ctrlPt1.getX(), ctrlPt1.getY(),
                                            ctrlPt2A.getX(), ctrlPt2A.getY(),
                                            endPt2.getX(), endPt2.getY() );

        // Draw the curve down into the left side of the potential well
        Point2D endPt3 = new Point2D.Double( 0, 0 );

        Point2D ctrlPt2B = new Point2D.Double();
        ctrlPt2B.setLocation( endPt2.getX() + ( ( endPt2.getX() - endPt1.getX() ) / 16 ),
                              endPt2.getY() );
        Point2D ctrlPt3 = new Point2D.Double();
        ctrlPt3.setLocation( endPt3.getX() - ( ( endPt3.getX() - endPt2.getX() ) * 3 / 4 ),
                             endPt3.getY() );

        shape[1] = new CubicCurve2D.Double( endPt2.getX(), endPt2.getY(),
                                            ctrlPt2B.getX(), ctrlPt2B.getY(),
                                            ctrlPt3.getX(), ctrlPt3.getY(),
                                            endPt3.getX(), endPt3.getY() );

        // draw the curve for the right side of the well
        profileTx.setToIdentity();
        profileTx.scale( -1, 1 );

        Point2D endPt4 = new Point2D.Double( -endPt2.getX(), endPt2.getY()  );
        Point2D ctrlPt4A = new Point2D.Double( -ctrlPt2B.getX(), ctrlPt2B.getY() );
        Point2D ctrlPt4B = new Point2D.Double( -ctrlPt2A.getX(), ctrlPt2A.getY() );
        Point2D endPt5 = new Point2D.Double( -endPt1.getX(), endPt1.getY() );
        Point2D ctrlPt5 = new Point2D.Double( -ctrlPt1.getX(), ctrlPt1.getY() );

        shape[2] = new CubicCurve2D.Double( endPt3.getX(), endPt3.getY(),
                                            -ctrlPt3.getX(), ctrlPt3.getY(),
                                            ctrlPt4A.getX(), ctrlPt4A.getY(),
                                            endPt4.getX(), endPt4.getY() );
        shape[3] = new CubicCurve2D.Double( endPt4.getX(), endPt4.getY(),
                                            ctrlPt4B.getX(), ctrlPt4B.getY(),
                                            ctrlPt5.getX(), ctrlPt5.getY(),
                                            endPt5.getX(), endPt5.getY());

        profilePath = new GeneralPath();
        profilePath.append( shape[0], true );
        profilePath.append( shape[1], true );
        profilePath.append( shape[2], true );
        profilePath.append( shape[3], true );

        profileBackgroundPath = new GeneralPath();
        profileBackgroundPath.append( shape[0], true );
        profileBackgroundPath.append( shape[1], true );
        profileBackgroundPath.append( shape[2], true );
        profileBackgroundPath.append( shape[3], true );

        // Instantiate a CubicUtil. We'll use it later
        cubicUtil = new CubicUtil( endPt1, ctrlPt1,
                                   endPt2, ctrlPt2A );

        // Compute the distance from the profile's center that corresponds to the alpha decay
        // threshold
        // todo: like spot for problem!!!!
        alphaDecayX = getHillX( -minEnergy );
//        alphaDecayX = getHillX( -getWellPotential() );

        // Tell everyone we've changed
        notifyObservers();
    }

    public GeneralPath getPath() {
        return profilePath;
    }

    public GeneralPath getBackgroundPath() {
        return profileBackgroundPath;
    }

    public double getAlphaDecayX() {
        return this.alphaDecayX;
    }

    public Shape[] getShape() {
        return shape;
    }

    /**
     * Gives the x coordinate of the hill-side of the profile that
     * corresponds to a particular y coordinate.
     * <p/>
     * See: http://www.moshplant.com/direct-or/bezier/math.html
     *
     * @param y
     * @return
     */
    private double getHillX( double y ) {
        double[] roots = cubicUtil.getXforY( y );
        double result = Double.NaN;
        for( int i = 0; i < roots.length; i++ ) {
            double root = roots[i];
            if( !Double.isNaN( root ) ) {
                result = root;
            }
        }
        return result;
    }

    /**
     * Gives the y coordinate of the hill-side of the profile that
     * corresponds to a particular x coordinate.
     * <p/>
     * See: http://www.moshplant.com/direct-or/bezier/math.html
     *
     * @param x
     * @return
     */
    public double getHillY( double x ) {
        double[] roots = cubicUtil.getYforX( x );
        double result = Double.NaN;
        for( int i = 0; i < roots.length; i++ ) {
            double root = roots[i];
            if( !Double.isNaN( root ) ) {
                result = root;
            }
        }
        return result;
    }

    /**
     * Returns the x distance from the center of the profile to the profile's peak
     *
     * @return
     */
//    public double getProfilePeakX() {
//        return endPt2.getX();
//    }

    // A test to see if the parametric equations are being computed properly
    //    public Point2D.Double[] genPts( int numPts ) {
    //        Point2D.Double[] result = new Point2D.Double[numPts];
    //        double cx, cy, bx, by, ax, ay;
    //        double x0 = endPt1.getX();
    //        double y0 = endPt1.getY();
    //
    //        double x3 = endPt2.getX();
    //        double y3 = endPt2.getY();
    //
    //        double x1 = ctrlPt1.getX();
    //        double y1 = ctrlPt1.getY();
    //
    //        double x2 = ctrlPt2A.getX();
    //        double y2 = ctrlPt2A.getY();
    //
    //        cx = 3 * ( x1 - x0 );
    //        bx = 3 * ( x2 - x1 ) - cx;
    //        ax = x3 - x0 - cx - bx;
    //
    //        cy = 3 * ( y1 - y0 );
    //        by = 3 * ( y2 - y1 ) - cy;
    //        ay = y3 - y0 - cy - by;
    //        double t = 0;
    //        for( int i = 0; i < numPts; i++ ) {
    //            t += 1.0 / numPts;
    //            result[i] = new Point2D.Double();
    //            result[i].x = ax * t * t * t + bx * t * t + cx * t + x0;
    //            result[i].y = ay * t * t * t + by * t * t + cy * t + y0;
    //        }
    //        return result;
    //    }

    /**
     * Shapes the profile based on the makeup of the nucleus
     */
    public void update() {
        this.width = Config.defaultProfileWidth;
        if( minEnergy !=-( 0.5 * nucleus.getNumNeutrons() )
        || maxEnergy != minEnergy + 2 * nucleus.getNumProtons() ) {
            this.minEnergy = -( 0.5 * nucleus.getNumNeutrons() );
            this.maxEnergy = minEnergy + 2 * nucleus.getNumProtons();
            generate();
        }
//        if( this.maxPotential != 2 * nucleus.getNumProtons()
//            || this.wellDepth != 0.5 * nucleus.getNumNeutrons() ) {
//            this.maxPotential = 2 * nucleus.getNumProtons();
//            this.wellDepth = 0.5 * nucleus.getNumNeutrons();
//            this.generate();
//            notifyObservers();
//        }
    }

    public double getDyDx( double v ) {
        double dyDx = cubicUtil.dyDx( v );
        return Double.isNaN( dyDx ) ? 0 : dyDx;
    }
}
