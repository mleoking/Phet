/*Copyright, University of Colorado, 2004.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchObserver;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.Junction;
import edu.colorado.phet.cck.elements.xml.BatteryData;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.common.math.ImmutableVector2D;


/**
 * User: Sam Reid
 * Date: Sep 3, 2003
 * Time: 2:36:21 AM
 * Copyright (c) Sep 3, 2003 by Sam Reid
 */
public class Battery extends Branch {
    boolean recursing = false;
    private ImmutableVector2D.Double dirVector;
    public double DX;
    private double internalResistance = .000001;
    private double magnitude;
    private boolean enableRotation = false;

    public String toString() {
        return super.toString() + " voltage=" + getVoltageDrop() + " internal resistance=" + internalResistance;
    }

    public Branch copy() {
        return new Battery( parent, getX1(), getY1(), getX2(), getY2(), getVoltageDrop(), DX );
    }

    public BranchData toBranchData() {
        return new BatteryData( this );
    }

    public void setDirVector( ImmutableVector2D.Double dirVector ) {
        this.dirVector = dirVector;
    }

    public Battery( Circuit parent, double x1, double y1, double x2, double y2, double voltageDrop, final double DX ) {
        super( parent, x1, y1, x2, y2 );
        dirVector = new ImmutableVector2D.Double( x2 - x1, y2 - y1 );
        this.magnitude = dirVector.getMagnitude();
        this.DX = DX;
        setVoltageDrop( voltageDrop );
        addObserver( new BranchObserver() {
            public void junctionMoved( Branch branch2, Junction junction ) {
                if( DX == 0 ) {
                    return;
                }
                if( recursing ) {
                    return;
                }
                recursing = true;
                if( !enableRotation ) {
                    recursing = false;
                    return;
                }
                if( junction == getStartJunction() ) {
                    //rotate about the end junction.
//                    ImmutableVector2D.Double endLoc=getStartJunction().getVector().getAddedInstance(dirVector);
                    ImmutableVector2D dir = getStartJunction().getVector().getSubtractedInstance( getEndJunction().getVector() ).getNormalizedInstance();
                    dir = dir.getScaledInstance( magnitude );

                    ImmutableVector2D startLoc = getEndJunction().getVector().getAddedInstance( dir );
                    getStartJunction().setLocation( startLoc.getX(), startLoc.getY() );
//                    ImmutableVector2D.Double endLoc = getStartJunction().getVector().getAddedInstance(DX, 0);
//                    getEndJunction().setLocation(endLoc.getX(), endLoc.getY());
                }
                else if( junction == getEndJunction() ) {
//                    ImmutableVector2D.Double startLoc=getEndJunction().getVector().getAddedInstance(dirVector.getScaledInstance(-1.0));

                    ImmutableVector2D dir = getEndJunction().getVector().getSubtractedInstance( getStartJunction().getVector() ).getNormalizedInstance();
                    dir = dir.getScaledInstance( magnitude );

                    ImmutableVector2D endLoc = getStartJunction().getVector().getAddedInstance( dir );
                    getEndJunction().setLocation( endLoc.getX(), endLoc.getY() );

                }
                recursing = false;//What a crazy hack.
            }

            public void currentOrVoltageChanged( Branch branch2 ) {
            }
        } );
    }

    public void setVoltageDrop( double voltageDrop ) {
        super.setVoltageDrop( voltageDrop );
        parent.fireConnectivityChanged();
    }

    public void resetDirVector() {
        this.dirVector = new ImmutableVector2D.Double( getEndJunction().getVector().getSubtractedInstance( getStartJunction().getVector() ) );
//        System.out.println("dirVector = " + dirVector);
    }

    public double getInternalResistance() {
        return internalResistance;
    }

    public double getDX() {
        return DX;
    }

    public void setLength( double length ) {
        this.magnitude = length;
        ImmutableVector2D dir = getEndJunction().getVector().getSubtractedInstance( getStartJunction().getVector() ).getNormalizedInstance();
        dir = dir.getScaledInstance( magnitude );

        ImmutableVector2D endLoc = getStartJunction().getVector().getAddedInstance( dir );
        getEndJunction().setLocation( endLoc.getX(), endLoc.getY() );
    }

    public void setRotateEnabled( boolean enab ) {
        this.enableRotation = enab;
    }
}
