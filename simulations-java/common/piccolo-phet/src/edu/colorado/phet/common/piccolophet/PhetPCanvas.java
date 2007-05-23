/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14676 $
 * Date modified : $Date:2007-04-17 02:58:50 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.piccolophet;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * Piccolo canvas extension that provides support for maintenance of aspect ratio,
 * and convenience methods for usage.
 */

public class PhetPCanvas extends PSwingCanvas {
    private TransformStrategy transformStrategy;
    private ComponentAdapter resizeAdapter;
    private PhetRootPNode phetRootNode;
    private AffineTransform transform;

    public PhetPCanvas() {
        this( new ConstantTransformStrategy( new AffineTransform() ) );
    }

    public PhetPCanvas( Dimension renderingSize ) {
        this( new ConstantTransformStrategy( new AffineTransform() ) );
        setTransformStrategy( new RenderingSizeStrategy( this, renderingSize ) );
    }

    public PhetPCanvas( Rectangle2D modelViewport ) {
        this( new ConstantTransformStrategy( new AffineTransform() ) );
        setTransformStrategy( new ViewportStrategy( this, modelViewport ) );
    }

    public PhetPCanvas( TransformStrategy transformStrategy ) {
        this.transformStrategy = transformStrategy;
        this.phetRootNode = new PhetRootPNode();
        getLayer().addChild( phetRootNode );
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        resizeAdapter = new ResizeAdapter();
        addComponentListener( resizeAdapter );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        /*By default, a PhETPCanvas is opaque, that is, no components should be visible underneath this panel.
        This allows for usage of performance improving facilities, such as immediate painting.
         */
        setOpaque( true );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        requestFocus();
    }

    public void setTransformStrategy( TransformStrategy transformStrategy ) {
        this.transformStrategy = transformStrategy;
        updateScale();
    }

    protected void updateScale() {
        phetRootNode.setWorldTransform( transformStrategy.getWorldTransform() );
    }

    public void setWorldScale( double scale ) {
        phetRootNode.setWorldScale( scale );
    }

    protected class ResizeAdapter extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            updateScale();
        }

        public void componentShown( ComponentEvent e ) {
            updateScale();
        }
    }

    /*
    Methods for accessing screen/world in default layer.
    */

    public PhetRootPNode getPhetRootNode() {
        return phetRootNode;
    }

    public void setPhetRootNode( PhetRootPNode phetRootNode ) {
        if( this.phetRootNode != null ) {
            getLayer().removeChild( this.phetRootNode );
        }
        this.phetRootNode = phetRootNode;
        getLayer().addChild( this.phetRootNode );
    }

    public void addScreenChild( PNode node ) {
        phetRootNode.addScreenChild( node );
    }

    /**
     * Adds the child as a screen child at the specified index.
     *
     * @param index
     * @param node
     */
    public void addScreenChild( int index, PNode node ) {
        phetRootNode.addScreenChild( index, node );
    }

    public void removeScreenChild( PNode node ) {
        phetRootNode.removeChild( node );
    }

    public void addWorldChild( PNode graphic ) {
        phetRootNode.addWorldChild( graphic );
    }

    public void addWorldChild( int index, PNode graphic ) {
        phetRootNode.addWorldChild( index, graphic );
    }

    /**
     * This may become deprecated (just use phetRootNode.removeChild)
     *
     * @param graphic
     */
    public void removeWorldChild( PNode graphic ) {
        try {
            phetRootNode.removeWorldChild( graphic );
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // Hack because Piccolo can't be modified
            // It doesn't expose world children so we can't
            // safely check for their presence
        }
    }

    /*
    Piccolo convenience methods.
    */
    public void setDebugRegionManagement( boolean debugRegionManagement ) {
        PDebug.debugRegionManagement = debugRegionManagement;
    }

    public void setDebugFrameRateToConsole( boolean frameRateToConsole ) {
        PDebug.debugPrintFrameRate = frameRateToConsole;
    }

    public void setDebugFullBounds( boolean debugFullBounds ) {
        PDebug.debugFullBounds = debugFullBounds;
    }
    
    /**
     * Gets the transform that was used for the most recent paintComponent call.
     * 
     * @return AffineTransform, null if paintComponent hasn't been called yet
     */
    public AffineTransform getTransform() {
        return transform;
    }
    
    /**
     * Gets the size of the canvas is screen coordinates.
     * 
     * @return Dimension2D
     */
    public Dimension2D getScreenSize() {
        return new PDimension( getWidth(), getHeight() );
    }
    
    /**
     * Gets the size of the canvas is world coordinates.
     * 
     * @return Dimension2D
     */
    public Dimension2D getWorldSize() {
        Dimension2D dim = getScreenSize();
        getPhetRootNode().screenToWorld( dim ); // modifies dim!
        return dim;
    }
    
    /**
     * Remembers the AffineTransform that was used to paint the canvas.
     * 
     * @param g
     */
    public void paintComponent( Graphics g ) {
        transform = ( (Graphics2D) g ).getTransform();
        super.paintComponent( g );
    }

    public void addActivity( PActivity activity ) {
        getRoot().addActivity( activity );
    }

    public void removeActivity( PActivity activity ) {
        getRoot().getActivityScheduler().removeActivity( activity );
    }

    public static interface TransformStrategy {
        AffineTransform getWorldTransform();
    }

    public static class ConstantTransformStrategy implements TransformStrategy {
        private AffineTransform affineTransform;

        public ConstantTransformStrategy( AffineTransform affineTransform ) {
            this.affineTransform = affineTransform;
        }

        public AffineTransform getWorldTransform() {
            return new AffineTransform( affineTransform );
        }
    }

    public static class RenderingSizeStrategy implements TransformStrategy {
        private PhetPCanvas phetPCanvas;
        private Dimension renderingSize;

        public RenderingSizeStrategy( PhetPCanvas phetPCanvas, Dimension renderingSize ) {
            this.phetPCanvas = phetPCanvas;
            this.renderingSize = renderingSize;
            phetPCanvas.addComponentListener( new ComponentAdapter() {
                public void componentShown( ComponentEvent e ) {
                    if( RenderingSizeStrategy.this.renderingSize == null ) {
                        setRenderingSize();
                    }
                }
            } );
        }

        public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
            this.phetPCanvas = phetPCanvas;
        }

        public AffineTransform getWorldTransform() {
            if( renderingSize == null && phetPCanvas.isVisible() ) {
                setRenderingSize();
            }
            double sx = getScaleX();
            double sy = getScaleY();

            //use the smaller
            double scale = sx < sy ? sx : sy;
            scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

            return AffineTransform.getScaleInstance( scale, scale );
        }

        private void setRenderingSize() {
            setRenderingSize( phetPCanvas.getSize() );
        }

        public void setRenderingSize( Dimension dim ) {
            this.renderingSize = new Dimension( dim );
        }

        public void setRenderingSize( int width, int height ) {
            setRenderingSize( new Dimension( width, height ) );
        }

        private double getScaleY() {
            return ( (double)phetPCanvas.getHeight() ) / renderingSize.height;
        }

        private double getScaleX() {
            return ( (double)phetPCanvas.getWidth() ) / renderingSize.width;
        }
    }

    public static class ViewportStrategy implements TransformStrategy {
        private Rectangle2D modelViewport;
        private PhetPCanvas phetPCanvas;

        public ViewportStrategy( PhetPCanvas phetPCanvas, Rectangle2D modelViewport ) {
            this.phetPCanvas = phetPCanvas;
            this.modelViewport = modelViewport;
        }

        protected double getScaleY() {
            return phetPCanvas.getHeight() / modelViewport.getHeight();
        }

        protected double getScaleX() {
            return phetPCanvas.getWidth() / modelViewport.getWidth();
        }

        public void componentShown( ComponentEvent e ) {
        }

        public AffineTransform getWorldTransform() {
            double sx = getScaleX();
            double sy = getScaleY();

            //use the smaller
            double scale = sx < sy ? sx : sy;
            if( scale < 0 ) {
                System.err.println( this.getClass().getName() + ": Warning: Sometimes in 1.5, sometimes getWidth() and getHeight() return negative values, causing troubles for this layout code." );
            }
            if( scale != 0.0 ) {
                AffineTransform worldTransform = new AffineTransform();
                worldTransform.translate( 0, phetPCanvas.getHeight() );
                worldTransform.scale( scale, -scale );
                worldTransform.translate( modelViewport.getX(), -modelViewport.getY() );
                return worldTransform;
            }
            else {
//                System.err.println( "Scale evaluated to zero!" );//removed debugging statements
            }
            return new AffineTransform();
        }
    }
}