/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.nuclearphysics.model.FissionProducts;
import edu.colorado.phet.nuclearphysics.model.Vessel;
import edu.colorado.phet.nuclearphysics.model.ControlRod;
import edu.colorado.phet.nuclearphysics.model.Containment;
import edu.colorado.phet.nuclearphysics.view.VesselGraphic;
import edu.colorado.phet.nuclearphysics.view.ControlRodGraphic;
import edu.colorado.phet.nuclearphysics.view.ControlRodGroupGraphic;
import edu.colorado.phet.nuclearphysics.view.ContainmentGraphic;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ControlledFissionModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ControlledFissionModule extends ChainReactionModule {

    public static final int VERTICAL = 1, HORIZONTAL = 2;

    private Vessel vessel;
    private static final double VESSEL_LAYER = 100;
    private static final double CONTROL_ROD_LAYER = VESSEL_LAYER - 1;

    public ControlledFissionModule( AbstractClock clock ) {
        super( "Controlled Reaction", clock );

        // Add the chamber
        vessel = new Vessel( 100, 100, 600, 400 );
        VesselGraphic vesselGraphic = new VesselGraphic( getPhysicalPanel(), vessel );
        getPhysicalPanel().addGraphic( vesselGraphic, VESSEL_LAYER );

        // Add control rods
        ControlRod[] controlRods = createControlRods( VERTICAL, vessel );
        ControlRodGroupGraphic controlRodGroupGraphic = new ControlRodGroupGraphic( getPhysicalPanel(),
                                                                                    controlRods,
                                                                                    vessel );
        getPhysicalPanel().addGraphic( controlRodGroupGraphic, CONTROL_ROD_LAYER );

        // Add a thermometer
    }

    /**
     * Creates a control rod for each channel in the specified vessel
     *
     * @param orientation
     * @param vessel
     * @return
     */
    private ControlRod[] createControlRods( int orientation, Vessel vessel ) {
        ControlRod[] rods = new ControlRod[vessel.getNumControlRodChannels()];
        if( orientation == VERTICAL ) {
            Rectangle2D[] channels = vessel.getChannels();
            for( int i = 0; i < channels.length; i++ ) {
                Rectangle2D channel = channels[i];
                rods[i] = new ControlRod( new Point2D.Double( channel.getMinX() + channel.getWidth() / 2,
                                                              channel.getMinY() ),
                                          new Point2D.Double( channel.getMinX() + channel.getWidth() / 2,
                                                              channel.getMaxY() ), 15 );
            }
        }
        return rods;
    }

    public void start() {

    }

    protected void computeNeutronLaunchParams() {

    }

    public void fission( FissionProducts products ) {

    }

    protected Point2D.Double findLocationForNewNucleus() {
        return null;
    }
}

