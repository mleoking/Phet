/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo.event;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.geom.Point2D;

/**
 * Drag behavior that ensures the dragged object stays inside the specified bounds.
 * The bounds must be rectangular, and this fails with rotated bounds.
 */

public class BoundedDragHandler extends PBasicInputEventHandler {
    private PNode dragNode;
    private PNode boundingNode;//work in global coordinate frame for generality.
    private Point2D relativeClickPoint;

    public BoundedDragHandler( PNode dragNode, PNode boundingNode ) {
        this.dragNode = dragNode;
        this.boundingNode = boundingNode;
    }

    public void mousePressed( PInputEvent event ) {
        setRelativeClickPoint( event );
    }

    private void setRelativeClickPoint( PInputEvent event ) {
        Point2D nodeLoc = dragNode.getGlobalTranslation();
        Point2D clickLoc = getGlobalClickPoint( event );
        relativeClickPoint = new Point2D.Double( clickLoc.getX() - nodeLoc.getX(), clickLoc.getY() - nodeLoc.getY() );
    }

    private Point2D getGlobalClickPoint( PInputEvent event ) {
        return event.getCanvasPosition();
//
//        Point2D pt = event.getPositionRelativeTo( dragNode ); //TODO lose the assumption that global=canvas coordinate frame.
//        pt = dragNode.parentToLocal( pt );  //todo this failed for a ruler node in SchrodingerApp
//        return dragNode.localToGlobal( pt );
    }

    public void mouseDragged( PInputEvent event ) {
        if( relativeClickPoint == null ) {
            setRelativeClickPoint( event );
        }
        else {
            PNode pickedNode = dragNode;

            Point2D pt = getGlobalClickPoint( event );
            Point2D newPoint = new Point2D.Double( pt.getX() - relativeClickPoint.getX(), pt.getY() - relativeClickPoint.getY() );
//            System.out.println( System.currentTimeMillis() + ", relativeClickPoint=" + relativeClickPoint + ", pt=" + pt + ", newPoint = " + newPoint );
            pickedNode.setGlobalTranslation( newPoint );

//            System.out.println( "pickedNode.getGlobalFullBounds().getMaxX() = " + pickedNode.getGlobalFullBounds().getMaxX() );
//            System.out.println( "boundingNode.getGlobalFullBounds().getMaxX() = " + boundingNode.getGlobalFullBounds().getMaxX() );

            if( !boundingNode.getGlobalFullBounds().contains( dragNode.getGlobalFullBounds() ) ) {
                double newX = pickedNode.getGlobalTranslation().getX();
                double newY = pickedNode.getGlobalTranslation().getY();
                if( pickedNode.getGlobalFullBounds().getX() < boundingNode.getFullBounds().getX() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getX();
                    double y0 = pickedNode.getGlobalFullBounds().getMinX();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX() - 1, pickedNode.getGlobalTranslation().getY() ) );
                    double x1 = pickedNode.getGlobalTranslation().getX();
                    double y1 = pickedNode.getGlobalFullBounds().getMinX();

                    newX = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMinX() );
                }
                if( pickedNode.getGlobalFullBounds().getY() < boundingNode.getFullBounds().getY() ) {

                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getY();
                    double y0 = pickedNode.getGlobalFullBounds().getMinY();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX(), pickedNode.getGlobalTranslation().getY() - 1 ) );
                    double x1 = pickedNode.getGlobalTranslation().getY();
                    double y1 = pickedNode.getGlobalFullBounds().getMinY();

                    newY = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMinY() );
                }
                if( pickedNode.getGlobalFullBounds().getMaxX() > boundingNode.getGlobalFullBounds().getMaxX() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getX();
                    double y0 = pickedNode.getGlobalFullBounds().getMaxX();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX() - 1, pickedNode.getGlobalTranslation().getY() ) );
                    double x1 = pickedNode.getGlobalTranslation().getX();
                    double y1 = pickedNode.getGlobalFullBounds().getMaxX();
                    newX = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMaxX() );
                }
                if( pickedNode.getGlobalFullBounds().getMaxY() > boundingNode.getGlobalFullBounds().getMaxY() ) {
                    //let's take data and fit (to account for scale, rotation & shear)
                    double x0 = pickedNode.getGlobalTranslation().getY();
                    double y0 = pickedNode.getGlobalFullBounds().getMaxY();

                    pickedNode.setGlobalTranslation( new Point2D.Double( pickedNode.getGlobalTranslation().getX(), pickedNode.getGlobalTranslation().getY() - 1 ) );
                    double x1 = pickedNode.getGlobalTranslation().getY();
                    double y1 = pickedNode.getGlobalFullBounds().getMaxY();
                    newY = fitLinear( x0, y0, x1, y1, boundingNode.getGlobalFullBounds().getMaxY() );
                }
                Point2D offset = new Point2D.Double( newX, newY );
//                System.out.println( System.currentTimeMillis() + ", offset = " + offset );
//                Point2D fullRollbackPoint = new Point2D.Double( newX - offset.getX(), newY - offset.getY() );
//                System.out.println( "rollbackPoint = " + offset );
                dragNode.setGlobalTranslation( offset );
//                event.getPickedNode().setOffset( rollbackPoint );
            }
        }
    }

    /* There is probably a more readable way to do this.*/
    private double fitLinear( double x0, double y0, double x1, double y1, double minX ) {
        double slope = ( y0 - y1 ) / ( x0 - x1 );
        double intercept = y0 - slope * x0;

        double desiredY = minX;
        double requiredGlobalOffset = ( desiredY - intercept ) / slope;

        return requiredGlobalOffset;
    }

    public void mouseReleased( PInputEvent event ) {
        super.mouseReleased( event );
        relativeClickPoint = null;
    }

    public PNode getBoundingNode() {
        return boundingNode;
    }

    public PNode getDragNode() {
        return dragNode;
    }
}