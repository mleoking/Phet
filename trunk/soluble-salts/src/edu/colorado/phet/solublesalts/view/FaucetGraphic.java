/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.solublesalts.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.RegisterablePNode;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.piccolo.pswing.PSwingCanvas;
import edu.colorado.phet.solublesalts.SolubleSaltsConfig;
import edu.colorado.phet.solublesalts.model.Faucet;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.io.IOException;


/**
 * An interactive graphic to represent the faucet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class FaucetGraphic extends RegisterablePNode implements Faucet.ChangeListener {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Orientations
    public static final int LEFT_FACING = 1, RIGHT_FACING = 2;
    // Registrations
    public static final int WALL_ATTACHMENT = 1, SPOUT = 2;
    private static final double MAX_WATER_WIDTH = 20.0;
    private static final Color WATER_COLOR = new Color( 194, 234, 255, 180 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Rectangle waterShape;
    private PImage faucetImage;
    private Faucet faucet;
    private double streamMaxY;
    private PPath waterGraphic;

    //----------------------------------------------------------------------------
    // Constructors & finalizers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     */
    public FaucetGraphic( PSwingCanvas pSwingCanvas, int orientation, int registration, Faucet faucet, double streamMaxY ) {
        faucet.addChangeListener( this );
        this.faucet = faucet;
        this.streamMaxY = streamMaxY;

        // Faucet
        BufferedImage bImg = null;
        try {
            bImg = ImageLoader.loadBufferedImage( SolubleSaltsConfig.FAUCET_IMAGE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        Point2D waterGraphicLocation = null;
        switch( registration ) {
            case SPOUT:
                this.setRegistrationPoint( 13, 77 );
                break;
            case WALL_ATTACHMENT:
                this.setRegistrationPoint( bImg.getWidth(), 20 );
                waterGraphicLocation = new Point2D.Double( 12,
                                                           77 );
                break;
            default:
                throw new RuntimeException( "Invalid registration" );
        }

        // If the faucet is facing right, flip the image and adjust the location of the water and the
        // registration pt.
        if( orientation == RIGHT_FACING ) {
            AffineTransform atx = AffineTransform.getScaleInstance( -1, 1 );
            atx.translate( -bImg.getWidth( null ), 0 );
            AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
            waterGraphicLocation = new Point2D.Double( getRegistrationPoint().getX(),
                                                       getRegistrationPoint().getY() );
            waterGraphicLocation = atx.transform( waterGraphicLocation, null );
            bImg = atxOp.filter( bImg, null );
            setRegistrationPoint( bImg.getWidth() - getRegistrationPoint().getX(),
                                  getRegistrationPoint().getY() );
        }
        faucetImage = new PImage( bImg );
        addChild( faucetImage );

        // Water
        waterShape = new Rectangle( 0, 0, 0, 0 );
        waterGraphic = new PPath( waterShape );
        waterGraphic.setOffset( waterGraphicLocation );
        waterGraphic.setPaint( WATER_COLOR );
        waterGraphic.setStrokePaint( null );
        addChild( waterGraphic );

        // Water Flow slider
        final JSlider flowSlider = new JSlider( 0, (int)faucet.getMaxFlow(), 0 );
        flowSlider.addChangeListener( new ChangeListener() {
            public void stateChanged
                    ( ChangeEvent e ) {
                FaucetGraphic.this.faucet.setFlow( flowSlider.getValue() );
            }
        } );
        flowSlider.setPreferredSize( new Dimension( (int)faucetImage.getWidth() / 2, 15 ) );
        PSwing pSwing = new PSwing( pSwingCanvas, flowSlider );
        pSwing.setOffset( 22, 35 );
        pSwing.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseEntered
                    ( PInputEvent
                    event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( new Cursor( Cursor.W_RESIZE_CURSOR ) );
            }

            public void mouseExited
                    ( PInputEvent
                    event ) {
                PhetPCanvas ppc = (PhetPCanvas)event.getComponent();
                ppc.setCursor( Cursor.getDefaultCursor() );
            }

        } );
        addChild( pSwing );

        update();
    }

    //----------------------------------------------------------------------------
    // Faucet.ChangeListener implementation
    //----------------------------------------------------------------------------

    public void stateChanged( Faucet.ChangeEvent event ) {
        update();
    }

    /*
     * @see edu.colorado.phet.common.util.SimpleObserver#update()
     */
    public void update() {
        int waterWidth = (int)Math.abs( faucet.getFlow() * MAX_WATER_WIDTH / faucet.getMaxFlow() );
        waterShape.setBounds( -( waterWidth / 2 ), 0, waterWidth,
                              (int)( ( streamMaxY - getYOffset() ) / getScale() ) );
        waterGraphic.setPathTo( waterShape );
        repaint();
    }
}