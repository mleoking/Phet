/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereWallExpert;
import edu.colorado.phet.collision.Wall;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.Pump;
import edu.colorado.phet.idealgas.view.WallGraphic;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * MovableWallsModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MovableWallsModule extends IdealGasModule {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static Font readoutFont = new Font( "Lucida sans", Font.BOLD, 12 );


    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private Wall lowerWall;
    private Wall leftFloor;
    private Wall rightFloor;
    private int wallThickness = 12;
    private ParticleCounter leftRegionParticleCounter;
    private ParticleCounter rightRegionParticleCounter;

    public MovableWallsModule( AbstractClock clock ) {
        super( clock, "<html><center>Potential Energy<br>Surface</center></html>" );

        getIdealGasModel().addCollisionExpert( new SphereWallExpert( getIdealGasModel() ) );

        Box2D box = super.getBox();

        // Create the lower vertical wall
        lowerWall = new Wall( new Rectangle2D.Double( box.getCorner1X() + box.getWidth() / 2 - wallThickness / 2,
                                                      box.getCorner1Y() + box.getHeight() / 3,
                                                      wallThickness, box.getHeight() * 2 / 3 ),
                              box.getBoundsInternal() );
        WallGraphic lowerWallGraphic = new WallGraphic( lowerWall, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.EAST_WEST );
        lowerWallGraphic.setIsResizable( true );
        getModel().addModelElement( lowerWall );
        addGraphic( lowerWallGraphic, 1000 );
        lowerWall.addChangeListener( new LowerWallChangeListener() );

        // Create the left movable floor
        leftFloor = new Wall( new Rectangle2D.Double( box.getCorner1X(), box.getCorner2Y() - 60,
                                                      lowerWall.getBounds().getMinX() - box.getCorner1X(), wallThickness ),
                              box.getBoundsInternal() );
        WallGraphic leftFloorGraphic = new WallGraphic( leftFloor, getApparatusPanel(),
                                                        Color.gray, Color.black,
                                                        WallGraphic.NORTH_SOUTH );
        getModel().addModelElement( leftFloor );
        addGraphic( leftFloorGraphic, 1000 );

        // Create the right movable floor
        rightFloor = new Wall( new Rectangle2D.Double( lowerWall.getBounds().getMaxX(), box.getCorner2Y() - 40,
                                                       box.getCorner2X() - lowerWall.getBounds().getMaxX(), wallThickness ),
                               box.getBoundsInternal() );
        WallGraphic rightFloorGraphic = new WallGraphic( rightFloor, getApparatusPanel(),
                                                         Color.gray, Color.black,
                                                         WallGraphic.NORTH_SOUTH );
        getModel().addModelElement( rightFloor );
        addGraphic( rightFloorGraphic, 1000 );

        // Set the region for the walls
        setWallBounds();

        // Add counters for the number of particles on either side of the vertical wall
        addParticleCounters();
    }

    /**
     * Sets the region of the various walls and the region of their movement based on
     * the region of the lower vertical wall
     */
    private void setWallBounds() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D lowerWallBounds = lowerWall.getBounds();
        leftFloor.setBounds( new Rectangle2D.Double( boxBounds.getMinX(),
                                                     leftFloor.getBounds().getMinY(),
                                                     lowerWallBounds.getMinX() - boxBounds.getMinX(),
                                                     leftFloor.getBounds().getHeight() ) );
        rightFloor.setBounds( new Rectangle2D.Double( lowerWallBounds.getMaxX(),
                                                      rightFloor.getBounds().getMinY(),
                                                      boxBounds.getMaxX() - lowerWallBounds.getMaxX(),
                                                      rightFloor.getBounds().getHeight() ) );
        leftFloor.setMovementBounds( new Rectangle2D.Double( boxBounds.getMinX(),
                                                             lowerWallBounds.getMinY(),
                                                             lowerWallBounds.getMinX() - boxBounds.getMinX(),
                                                             boxBounds.getMaxY() - lowerWallBounds.getMinY() ) );
        // Right floor can't go higher than the intake port on the box
        rightFloor.setMovementBounds( new Rectangle2D.Double( lowerWallBounds.getMaxX(),
                                                              Math.max( lowerWallBounds.getMinY(), Pump.s_intakePortY + 10 ),
                                                              boxBounds.getMaxX() - lowerWallBounds.getMaxX(),
                                                              boxBounds.getMaxY() - lowerWallBounds.getMinY() ) );
    }

    /**
     * Add elements that keep count of the number of particles on either side of the vertical wall
     */
    private void addParticleCounters() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D lowerWallBounds = lowerWall.getBounds();

        // Create the particle counters
        leftRegionParticleCounter = new ParticleCounter();
        getModel().addModelElement( leftRegionParticleCounter );

        rightRegionParticleCounter = new ParticleCounter();
        getModel().addModelElement( rightRegionParticleCounter );

        // Set the bounds of the regions the particle counters cound
        setParticleCounterRegions();

        // Put readouts on the apparatus panel
        PhetGraphic leftCounterReadout = new ReadoutGraphic( leftRegionParticleCounter );
        leftCounterReadout.setLocation( (int)boxBounds.getMinX(), 25 );
        addGraphic( leftCounterReadout, IdealGasConfig.readoutLayer );

        PhetGraphic rightCounterReadout = new ReadoutGraphic( rightRegionParticleCounter );
        rightCounterReadout.setLocation( (int)boxBounds.getMaxX() - 20, 25 );
        addGraphic( rightCounterReadout, IdealGasConfig.readoutLayer );
    }

    private void setParticleCounterRegions() {
        Rectangle2D boxBounds = getBox().getBoundsInternal();
        Rectangle2D lowerWallBounds = lowerWall.getBounds();

        leftRegionParticleCounter.setRegion( new Rectangle2D.Double( boxBounds.getMinX(),
                                                                     boxBounds.getMinY(),
                                                                     lowerWallBounds.getMinX() + lowerWallBounds.getWidth() / 2 - boxBounds.getMinX(),
                                                                     boxBounds.getHeight() ) );
        rightRegionParticleCounter.setRegion( new Rectangle2D.Double( lowerWallBounds.getMinX() + lowerWallBounds.getWidth() / 2,
                                                                      boxBounds.getMinY(),
                                                                      boxBounds.getMaxX() - lowerWallBounds.getMaxX() + lowerWallBounds.getWidth() / 2,
                                                                      boxBounds.getHeight() ) );
    }

    //-----------------------------------------------------------------
    // Event handling
    //-----------------------------------------------------------------

    private class LowerWallChangeListener implements Wall.ChangeListener {
        public void wallChanged( Wall.ChangeEvent event ) {
            setWallBounds();
            setParticleCounterRegions();
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * A model element that counts the number of particles in a region of the model.
     */
    private class ParticleCounter extends SimpleObservable implements ModelElement {
        // The region within which to count particles
        private Rectangle2D region;
        private int cnt;

        public ParticleCounter(){

        }

        public ParticleCounter( Rectangle2D bounds ) {
            this.region = bounds;
        }

        public void stepInTime( double dt ) {
            cnt = 0;
            List bodies = getIdealGasModel().getBodies();
            for( int i = 0; i < bodies.size(); i++ ) {
                Object o = bodies.get( i );
                if( o instanceof GasMolecule ) {
                    GasMolecule molecule = (GasMolecule)o;
                    if( region.contains( molecule.getPosition() ) ) {
                        cnt++;
                    }
                }
            }
            notifyObservers();
        }

        public void setRegion( Rectangle2D region ) {
            this.region = region;
        }

        public int getCnt() {
            return cnt;
        }
    }

    /**
     * A text graphic for the counter readouts
     */
    private class ReadoutGraphic extends CompositePhetGraphic implements SimpleObserver {
        private ParticleCounter counter;
        private PhetTextGraphic readout;
        private PhetShapeGraphic border;

        public ReadoutGraphic( ParticleCounter counter ) {
            super( getApparatusPanel() );
            readout = new PhetTextGraphic( getApparatusPanel(), readoutFont, "", Color.black );
            this.addGraphic( readout, 10 );
            border = new PhetShapeGraphic( getApparatusPanel(), new Rectangle(40, 15 ), new BasicStroke( 1f ), Color.black );
            this.addGraphic( border, 5 );
            counter.addObserver( this );
            this.counter = counter;
            update();
        }

        public void update() {
            readout.setText( Integer.toString( counter.getCnt() ) );
            setBoundsDirty();
            repaint();
        }
    }
}
