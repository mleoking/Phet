/**
 * Class: SingleAtomBaseModule
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Apr 1, 2003
 */
package edu.colorado.phet.lasers.controller.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;

import java.awt.geom.Point2D;

public class SingleAtomBaseModule extends BaseLaserModule {
    private Atom atom;
    private CollimatedBeam stimulatingBeam;
    private CollimatedBeam pumpingBeam;

    public SingleAtomBaseModule( String title ) {
        super( title );

        Point2D beamOrigin = new Point2D.Double( s_origin.getX(),
                                                 s_origin.getY() + s_boxHeight / 2 - Photon.s_radius );
        stimulatingBeam = new CollimatedBeam( getLaserModel(),
                                              Photon.RED,
                                              beamOrigin,
                                              Photon.s_radius * 2,
                                              s_boxWidth + s_laserOffsetX * 2,
                                              new Vector2D.Double( 1, 0 ) );
        stimulatingBeam.addListener( this );
        stimulatingBeam.setActive( true );
        stimulatingBeam.setPhotonsPerSecond( 1 );
        getLaserModel().setStimulatingBeam( stimulatingBeam );

        pumpingBeam = ( (LaserModel)getModel() ).getPumpingBeam();
        pumpingBeam = new CollimatedBeam( getLaserModel(),
                                          Photon.BLUE,
                                          new Point2D.Double( s_origin.getX() + s_laserOffsetX + s_boxWidth / 2 - Photon.s_radius / 2,
                                                              s_origin.getY() - s_laserOffsetX ),
                                          s_boxHeight + s_laserOffsetX * 2,
                                          s_boxWidth,
                                          new Vector2D.Double( 0, 1 ) );
        pumpingBeam.addListener( this );
        pumpingBeam.setWidth( Photon.s_radius * 2 );
        pumpingBeam.setActive( true );
        getLaserModel().setPumpingBeam( pumpingBeam );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );

        atom = new Atom();
        atom.setPosition( getLaserOrigin().getX() + s_boxWidth / 2,
                          getLaserOrigin().getY() + s_boxHeight / 2 );
        atom.setVelocity( 0, 0 );
        addAtom( atom );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        getLaserModel().removeModelElement( atom );
        atom.removeFromSystem();
    }
}
