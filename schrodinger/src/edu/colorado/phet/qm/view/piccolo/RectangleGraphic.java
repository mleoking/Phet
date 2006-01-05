/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.qm.model.RectangularObject;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colorgrid.ColorGrid;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class RectangleGraphic extends PNode {
    private SchrodingerPanel schrodingerPanel;

    private PPath areaGraphic;
    private PPath resizeCorner;
    private RectangularObject rectangularObject;

    public RectangleGraphic( SchrodingerPanel component, final RectangularObject rectangularObject, Color fill ) {
        this.schrodingerPanel = component;
        this.rectangularObject = rectangularObject;
        areaGraphic = new PPath();//todo transparent green.
        areaGraphic.setStrokePaint( Color.blue );
        areaGraphic.setStroke( new BasicStroke( 1.0f ) );
        areaGraphic.setPaint( fill );
        addChild( areaGraphic );
        areaGraphic.addInputEventListener( new ContinuousDrag( new LocationGetter() {
            public Point getLocation() {
                return rectangularObject.getLocation();
            }
        } ) );
        areaGraphic.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );

        GeneralPath path = new GeneralPath();
        path.moveTo( 0, 10 );
        path.lineTo( 10, 10 );
        path.lineTo( 10, 0 );
        resizeCorner = new PPath( path );
        resizeCorner.setStroke( new BasicStroke( 4 ) );
        resizeCorner.setStrokePaint( Color.green );
        resizeCorner.addInputEventListener( new CornerDrag() );
        resizeCorner.addInputEventListener( new CursorHandler( Cursor.SE_RESIZE_CURSOR ) );

        addChild( resizeCorner );
        rectangularObject.addObserver( new SimpleObserver() {
            public void update() {
                RectangleGraphic.this.update();
            }
        } );
        update();
        getColorGrid().addListener( new ColorGrid.Listener() {
            public void update() {
                System.out.println( "RectangleGraphic.update" );
                RectangleGraphic.this.update();
            }
        } );
        schrodingerPanel.getWavefunctionGraphic().addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                RectangleGraphic.this.update();
            }
        } );
    }

    public void setResizeComponentVisible( boolean visible ) {
        this.resizeCorner.setVisible( visible );
    }

    protected void update() {
        Rectangle modelRect = rectangularObject.getBounds();
        Rectangle viewRect = getViewRectangle( modelRect );
        areaGraphic.setPathTo( viewRect );
        resizeCorner.setOffset( (int)viewRect.getMaxX() - resizeCorner.getWidth() + 4, (int)viewRect.getMaxY() - resizeCorner.getHeight() + 4 );
    }

    protected ColorGrid getColorGrid() {
        return schrodingerPanel.getWavefunctionGraphic().getColorGrid();
    }

    public PPath getAreaGraphic() {
        return areaGraphic;
    }

    public Rectangle getViewRectangle( Rectangle modelRect ) {
        System.out.println( "modelRect = " + modelRect );
        if( modelRect.width == 0 ) {
            System.out.println( "RectangleGraphic.getViewRectangle" );
        }
        Rectangle gridRect = getColorGrid().getViewRectangle( modelRect );
        if( gridRect.width == 0 ) {
            gridRect.width = 1;
        }
        if( gridRect.height == 0 ) {
            gridRect.height = 1;
        }
        System.out.println( "gridRectORIG = " + gridRect );
        getSchrodingerPanel().getWavefunctionGraphic().localToGlobal( gridRect );
        globalToLocal( gridRect );
        localToParent( gridRect );
        System.out.println( "gridRect = " + gridRect );
        return gridRect;
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private static interface LocationGetter {
        Point getLocation();
    }

    public class ContinuousDrag extends PBasicInputEventHandler {
        // implements java.awt.event.MouseListener
        Point2D startLocation;
        Point origLoc;
        LocationGetter locationGetter;

        public ContinuousDrag( LocationGetter locationGetter ) {
            this.locationGetter = locationGetter;
        }

        public void mousePressed( PInputEvent e ) {
            startLocation = new Point2D.Double( e.getPosition().getX(), e.getPosition().getY() );
            origLoc = locationGetter.getLocation();
        }

        // implements java.awt.event.MouseListener
        public void mouseReleased( PInputEvent e ) {
            startLocation = null;
            origLoc = null;
        }

        // implements java.awt.event.MouseMotionListener
        public void mouseDragged( PInputEvent e ) {
            if( startLocation == null ) {
                mousePressed( e );
            }
            else {
                Point2D fin = e.getPosition();

                Point2D.Double rel = new Point2D.Double( fin.getX() - startLocation.getX(), fin.getY() - startLocation.getY() );
                double dx = rel.x;
                double dy = rel.y;
                int modelDX = (int)( dx / getColorGrid().getCellWidth() );
                int modelDY = (int)( dy / getColorGrid().getCellHeight() );
                rectangularObject.setLocation( modelDX + origLoc.x, modelDY + origLoc.y );
            }
        }
    }

    public class CornerDrag extends PBasicInputEventHandler {
        Point2D.Double startLocation;
        Dimension origDim;

        public void mousePressed( PInputEvent e ) {
            startLocation = new Point2D.Double( e.getPosition().getX(), e.getPosition().getY() );
            origDim = rectangularObject.getDimension();
        }

        public void mouseReleased( PInputEvent e ) {
            startLocation = null;
            origDim = null;
        }

        public void mouseDragged( PInputEvent e ) {
            if( startLocation == null ) {
                mousePressed( e );
            }
            else {
                Point2D fin = e.getPosition();

                Point2D.Double rel = new Point2D.Double( fin.getX() - startLocation.x, fin.getY() - startLocation.y );
                double dx = rel.x;
                double dy = rel.y;

                int modelDX = (int)( dx / getColorGrid().getCellWidth() );
                int modelDY = (int)( dy / getColorGrid().getCellHeight() );
                int newWidth = origDim.width + modelDX;
                int newHeight = origDim.height + modelDY;
                newWidth = Math.max( newWidth, 0 );
                newHeight = Math.max( newHeight, 0 );
                rectangularObject.setDimension( newWidth, newHeight );
            }

        }
    }
}
