/**
 * Class: BaseLaserModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Mar 21, 2003
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.controller.BeamControl;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.view.BlueBeamGraphic;
import edu.colorado.phet.lasers.view.LampGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public class MultipleAtomModule extends BaseLaserModule {

    private double s_maxSpeed = .1;
    private ArrayList atoms;

    /**
     *
     */
    public MultipleAtomModule( AbstractClock clock ) {
        super( SimStrings.get( "ModuleTitle.MultipleAtomModule" ), clock );

        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() );
        CollimatedBeam stimulatingBeam = ( (LaserModel)getModel() ).getStimulatingBeam();

        Rectangle2D.Double stimulatingBeamBounds = new Rectangle2D.Double( beamOrigin.getX(), beamOrigin.getY(),
                                                                           s_boxWidth + s_laserOffsetX * 2, s_boxHeight );
        stimulatingBeam.setBounds( stimulatingBeamBounds );
        stimulatingBeam.setDirection( new Vector2D.Double( 1, 0 ) );
        stimulatingBeam.addListener( this );
        stimulatingBeam.setActive( true );
        stimulatingBeam.setPhotonsPerSecond( 1 );

        CollimatedBeam pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        Point2D pumpingBeamOrigin = new Point2D.Double( s_origin.getX() + s_laserOffsetX + s_boxWidth / 2 - Photon.s_radius / 2,
                                                        s_origin.getY() - 140 );
        Rectangle2D.Double pumpingBeamBounds = new Rectangle2D.Double( pumpingBeamOrigin.getX(), pumpingBeamOrigin.getY(),
                                                                       s_boxWidth, s_boxHeight + s_laserOffsetX * 2 );
        pumpingBeam.setBounds( pumpingBeamBounds );
        pumpingBeam.setDirection( new Vector2D.Double( 0, 1 ) );
        pumpingBeam.addListener( this );
        pumpingBeam.setActive( true );
        BlueBeamGraphic beamGraphic = new BlueBeamGraphic( getApparatusPanel(), pumpingBeam, getCavity() );
        addGraphic( beamGraphic, 1 );

        // Add the ray gun for firing photons
        try {
            Rectangle2D allocatedBounds = new Rectangle2D.Double( (int)stimulatingBeamBounds.getX() - 100,
                                                                  (int)( stimulatingBeamBounds.getY() ),
                                                                  100, (int)stimulatingBeamBounds.getHeight() );
            BufferedImage gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
            double scaleX = allocatedBounds.getWidth() / gunBI.getWidth();
            double scaleY = allocatedBounds.getHeight() / gunBI.getHeight();

            AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage beamImage = atxOp1.filter( gunBI, null );
            AffineTransform atx = new AffineTransform();
            atx.translate( allocatedBounds.getX(), allocatedBounds.getY() );
            PhetImageGraphic stimulatingBeamGraphic = new LampGraphic( stimulatingBeam, getApparatusPanel(), beamImage, atx );
            //            addGraphic( stimulatingBeamGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the intensity control
            JPanel sbmPanel = new JPanel();
            BeamControl sbm = new BeamControl( stimulatingBeam );
            Dimension sbmDim = sbm.getPreferredSize();
            sbmPanel.setBounds( (int)allocatedBounds.getX(), (int)( allocatedBounds.getY() + allocatedBounds.getHeight() ),
                                (int)sbmDim.getWidth() + 10, (int)sbmDim.getHeight() + 10 );
            sbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            sbmPanel.add( sbm );
            //            sbmPanel.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            sbmPanel.setOpaque( false );
            //            getApparatusPanel().add( sbmPanel );

            // Pumping beam lamp
            double pumpScaleX = scaleX;
            double pumpScaleY = s_boxWidth / gunBI.getHeight();
            AffineTransformOp atxOp2 = new AffineTransformOp( AffineTransform.getScaleInstance( pumpScaleX, pumpScaleY ), AffineTransformOp.TYPE_BILINEAR );
            BufferedImage pumpBeamImage = atxOp2.filter( gunBI, null );
            AffineTransform pumpingBeamTx = new AffineTransform();
            pumpingBeamTx.translate( getLaserOrigin().getX() + pumpBeamImage.getHeight() + s_boxWidth / 2 - pumpBeamImage.getHeight() / 2, 10 );
            pumpingBeamTx.rotate( Math.PI / 2 );
            BufferedImage pumpingBeamLamp = new AffineTransformOp( new AffineTransform(), AffineTransformOp.TYPE_BILINEAR ).filter( pumpBeamImage, null );
            PhetImageGraphic pumpingLampGraphic = new LampGraphic( pumpingBeam, getApparatusPanel(), pumpingBeamLamp, pumpingBeamTx );
            addGraphic( pumpingLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

            // Add the beam control
            JPanel pbmPanel = new JPanel();
            BeamControl pbm = new BeamControl( pumpingBeam );
            Dimension pbmDim = pbm.getPreferredSize();
            pbmPanel.setBounds( (int)( pumpingBeamTx.getTranslateX() - ( pumpingLampGraphic.getHeight() * pumpScaleY ) - pbmDim.getWidth() ), 10,
                                (int)pbmDim.getWidth() + 10, (int)pbmDim.getHeight() + 10 );
            pbmPanel.add( pbm );
            //            pbmPanell.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbm.setBorder( new BevelBorder( BevelBorder.RAISED ) );
            pbmPanel.setOpaque( false );
            getApparatusPanel().add( pbmPanel );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // ONly the pumping beam is enabled for this module
        pumpingBeam.setIsEnabled( true );
        stimulatingBeam.setIsEnabled( false );

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 1 );
        config.setMiddleEnergySpontaneousEmissionTime( LaserConfig.DEFAULT_SPONTANEOUS_EMISSION_TIME );
        config.setPumpingPhotonRate( 0 );
        config.setReflectivity( 0.7 );
        config.configureSystem( getLaserModel() );

    }

    /**
     *
     */
    public void activate( PhetApplication app ) {
        super.activate( app );

        Atom atom = null;
        atoms = new ArrayList();
        for( int i = 0; i < 20; i++ ) {
            atom = new Atom();
            boolean placed = false;

            // Place atoms so they don't overlap
            do {
                placed = true;
                atom.setPosition( ( getLaserOrigin().getX() + ( Math.random() ) * ( s_boxWidth - atom.getRadius() * 2 ) + atom.getRadius() ),
                                  ( getLaserOrigin().getY() + ( Math.random() ) * ( s_boxHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );
                //                atom.setVelocity( ( Math.random() - 0.5 ) * s_maxSpeed,
                //                                  ( Math.random() - 0.5 ) * s_maxSpeed );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    double d = atom.getPosition().distance( atom2.getPosition() );
                    if( d <= atom.getRadius() + atom2.getRadius() ) {
                        placed = false;
                        break;
                    }
                }
            } while( !placed );
            atoms.add( atom );
            addAtom( atom );
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 0f );
        config.setMiddleEnergySpontaneousEmissionTime( 2000f );
        config.setPumpingPhotonRate( 0 );
        //        config.setPumpingPhotonRate( 100f );
        config.setHighEnergySpontaneousEmissionTime( 2000f );
        config.setReflectivity( 0.7f );
        config.configureSystem( (LaserModel)getModel() );
    }

    /**
     *
     */
    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        for( int i = 0; i < atoms.size(); i++ ) {
            Atom atom = (Atom)atoms.get( i );
            getLaserModel().removeModelElement( atom );
            atom.removeFromSystem();
        }
        atoms.clear();
    }
}
