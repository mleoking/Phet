/**
 * Class: RigidHollowSphereModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 18, 2004
 * Time: 12:35:56 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereHollowSphereExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;
import edu.colorado.phet.instrumentation.Thermometer;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.Random;

public class RigidHollowSphereModule extends IdealGasModule implements GasSource {

    private static final float initialVelocity = 35;

    private HollowSphere sphere;
    private Class gasSpecies = HeavySpecies.class;
    private LinkedList moleculesInSphere = new LinkedList();
    private Random random = new Random();

    /**
     * Constructor
     *
     * @param clock
     */
    public RigidHollowSphereModule( AbstractClock clock ) {
        super( clock, SimStrings.get( "ModuleTitle.RigidHollowSphere" ) );
        double xOrigin = 200;
        double yOrigin = 250;
        double xDiag = 434;
        double yDiag = 397;

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereHollowSphereExpert( getIdealGasModel(), clock.getDt() ) );

        // Set the size of the box
        final Box2D box = getIdealGasModel().getBox();
        //        box.setRegion( 300, 100, box.getMaxX(), box.getMaxY() );
        sphere = new HollowSphere( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
                                                       box.getMinY() + box.getHeight() / 2 ),
                                   new Vector2D.Double( 0, 0 ),
                                   new Vector2D.Double( 0, 0 ),
                                   100,
                                   50 );
        box.setMinimumWidth( sphere.getRadius() * 3 );

        int thermometerHeight = 100;
        Thermometer thermometer = getThermomenter();
        thermometer.setLocation( new Point2D.Double( box.getMaxX() - 30, box.getMinY() - thermometerHeight ) );

        new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
        getIdealGasModel().getBox().addContainedBody( sphere );
        addGraphic( new HollowSphereGraphic( getApparatusPanel(), sphere ), 20 );

        // Put some intial gas inside and outside sphere
        addGas( xDiag, xOrigin, yDiag, yOrigin );

        // Turn on gravity
        setGravity( IdealGasConfig.MAX_GRAVITY / 10 );

        // Add controls to the control panel that are specific to this module
        JPanel controlPanel = new JPanel( new GridBagLayout() );
        controlPanel.setBorder( new TitledBorder( SimStrings.get( "RigidHollowSphereControlPanel.controlsTitle" ) ) );
        GridBagConstraints gbc = null;
        Insets insets = new Insets( 0, 0, 0, 0 );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, 0, 0 );
        HollowSphereControlPanel hollowSphereControlPanel = new HollowSphereControlPanel( this, RigidHollowSphereModule.this, sphere );
        controlPanel.add( hollowSphereControlPanel, gbc );
        this.addResetListener( hollowSphereControlPanel );
        getIdealGasControlPanel().addParticleControl( controlPanel );
    }

    private void addGas( double xDiag, double xOrigin, double yDiag, double yOrigin ) {
        // Put some heavy gas outside the sphere
        for( int i = 0; i < 0; i++ ) {
            //        for( int i = 0; i < 100; i++ ) {
            double x = Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            double y = Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            double theta = Math.random() * Math.PI * 2;
            double vx = (double)Math.cos( theta ) * initialVelocity;
            double vy = (double)Math.sin( theta ) * initialVelocity;
            GasMolecule p1 = new HeavySpecies( new Point2D.Double( x, y ),
                                               new Vector2D.Double( vx, vy ),
                                               new Vector2D.Double( 0, 0 ) );
            new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
        }

        // Put some heavy gas in the sphere
        GasMolecule p1 = null;
        int num = 0;
        //        int num = 6;
        //        int num = 4;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                double v = initialVelocity;
                double theta = Math.random() * Math.PI * 2;
                double vx = Math.cos( theta ) * v;
                double vy = Math.sin( theta ) * v;
                if( HeavySpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new HeavySpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ) );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                if( LightSpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new LightSpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ) );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                sphere.addContainedBody( p1 );
            }
        }
    }

    protected Pump.PumpingEnergyStrategy getPumpingEnergyStrategy() {
        return new Pump.FixedEnergyStrategy();
    }

    public void setCurrentGasSpecies( Class gasSpecies ) {
        this.gasSpecies = gasSpecies;
    }

    public Class getCurrentGasSpecies() {
        return this.gasSpecies;
    }

    public void removeGasMoleculeFromSphere( Class gasSpecies ) {
        // Find a molecule of the right species, and remove it
        boolean found = false;
        GasMolecule gasMolecule = null;
        for( int i = 0; !found && i < moleculesInSphere.size(); i++ ) {
            gasMolecule = (GasMolecule)moleculesInSphere.get( i );
            if( gasSpecies.isInstance( gasMolecule ) ) {
                found = true;
                moleculesInSphere.remove( gasMolecule );
                this.sphere.removeContainedBody( gasMolecule );
            }
        }

        // TODO: this next thing looks really wrong. Why remove something if nothing was found?
//        if( !found ) {
//            gasMolecule = (GasMolecule)moleculesInSphere.removeFirst();
//        }
        getIdealGasModel().removeModelElement( gasMolecule );
    }

    public void addMoleculeToSphere( Class species ) {
        Point2D location = sphere.getNewMoleculeLocation();
        Vector2D velocity = sphere.getNewMoleculeVelocity( species, getIdealGasModel() );
        GasMolecule gm = null;
        if( species == HeavySpecies.class ) {
            gm = new HeavySpecies( location, velocity, new Vector2D.Double() );
        }
        if( species == LightSpecies.class ) {
            gm = new LightSpecies( location, velocity, new Vector2D.Double() );
        }
        moleculesInSphere.add( gm );
        PumpMoleculeCmd cmd = new PumpMoleculeCmd( this.getIdealGasModel(), gm,
                                                   this );
        cmd.doIt();
        this.sphere.addContainedBody( gm );
    }
}
