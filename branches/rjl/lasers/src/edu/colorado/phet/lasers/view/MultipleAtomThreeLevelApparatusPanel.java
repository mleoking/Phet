/**
 * Class: MultipleAtomThreeLevelApparatusPanel
 * Class: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 28, 2003
 * Time: 1:36:10 PM
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.controller.command.AddAtomCmd;
import edu.colorado.phet.lasers.controller.ThreeLevelControlPanel;
import edu.colorado.phet.lasers.controller.ApparatusConfiguration;
import edu.colorado.phet.lasers.physics.atom.Atom;
import edu.colorado.phet.lasers.physics.ResonatingCavity;
import edu.colorado.phet.physics.collision.ContactDetector;
import edu.colorado.phet.physics.Vector2D;
import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.PhetControlPanel;
import edu.colorado.phet.graphics.MonitorPanel;

import java.util.ArrayList;

public class MultipleAtomThreeLevelApparatusPanel extends BaseLaserApparatusPanel {

    private ArrayList atoms;
    private MonitorPanel monitorPanel = new ThreeEnergyLevelMonitorPanel();
    private PhetControlPanel controlPanel = new ThreeLevelControlPanel();

    /**
     *
     */
    public MultipleAtomThreeLevelApparatusPanel() {
        super( "Multiple Atoms / Three Levels" );
    }

    /**
     *
     */
    public void activate() {
        super.activate();

        PhetApplication.instance().getPhetMainPanel().setMonitorPanel( monitorPanel );
        PhetApplication.instance().getPhetMainPanel().setControlPanel( controlPanel );

        float newHeight = 100;
        ResonatingCavity cavity = this.getCavity();
        float cavityHeight =  cavity.getHeight();
        Vector2D cavityPos = cavity.getPosition();
        float yNew = cavityPos.getY() + cavityHeight / 2 - newHeight / 2;
        cavity.setPosition( cavityPos.getX(), yNew );
        cavity.setHeight( newHeight );

        Atom atom = null;
        atoms = new ArrayList();
        for( int i = 0; i < 20; i++ ) {
            atom = new Atom();
            boolean placed = false;

            // Place atoms so they don't overlap
            do {
                placed = true;
                atom.setPosition( (float)( getLaserOrigin().getX() + ( Math.random() ) * ( s_boxWidth - atom.getRadius() * 2 ) + atom.getRadius() ),
                                  (float)( getLaserOrigin().getY() + ( Math.random() ) * ( newHeight - atom.getRadius() * 2 ) ) + atom.getRadius() );
                atom.setVelocity( (float)( Math.random() - 0.5 ) * s_maxSpeed,
                                  (float)( Math.random() - 0.5 ) * s_maxSpeed );
                for( int j = 0; j < atoms.size(); j++ ) {
                    Atom atom2 = (Atom)atoms.get( j );
                    if( ContactDetector.areContacting( atom, atom2 )) {
                        placed = false;
                        break;
                    }
                }
            } while( !placed );
            atoms.add( atom );
            new AddAtomCmd( atom ).doIt();
        }

        ApparatusConfiguration config = new ApparatusConfiguration();
        config.setStimulatedPhotonRate( 2.0f );
        config.setMiddleEnergySpontaneousEmissionTime( 0.500f );
        config.setPumpingPhotonRate( 100f );
        config.setHighEnergySpontaneousEmissionTime( 0.05f );
        config.setReflectivity( 0.7f );
        config.configureSystem();

    }

    /**
     *
     */
    public void deactivate() {
        super.deactivate();
        atoms.clear();
    }

    //
    // Static fields and methods
    //
    private float s_maxSpeed = 200;
}
