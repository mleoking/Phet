/**
 * Class: TestModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.Kaboom;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.NucleusGraphic;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultipleNucleusFissionModule extends NuclearPhysicsModule
        implements NeutronGun, FissionListener {
    private static Random random = new Random();
    private static float neutronSpeed = 1f;

//    private InternalNeutronGun neutronGun;
    private Neutron neutronToAdd;
    private ArrayList nuclei;
    private AbstractClock clock;
    private long orgDelay;
    private double orgDt;

    public MultipleNucleusFissionModule( AbstractClock clock ) {
        super( "Chain Reaction", clock );
        this.clock = clock;
        super.addControlPanelElement( new MultipleNucleusFissionControlPanel( this ) );

        // Add a bunch of nuclei, including one in the middle that we can fire a
        // neutron at
        nuclei = new ArrayList();
        Uranium235 centralNucleus = new Uranium235( new Point2D.Double() );
        centralNucleus.addFissionListener( this );
        getPhysicalPanel().addNucleus( centralNucleus );
        getModel().addModelElement( centralNucleus );
        nuclei.add( centralNucleus );

        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                checkForFission();
                if( MultipleNucleusFissionModule.this.neutronToAdd != null ) {
                    MultipleNucleusFissionModule.this.addNeutron( neutronToAdd );
                    MultipleNucleusFissionModule.this.neutronToAdd = null;
                }
            }
        } );
    }

    public ArrayList getNuclei() {
        return nuclei;
    }

    protected void addNeucleus( Nucleus nucleus ) {
        nuclei.add( nucleus );
        nucleus.addFissionListener( this );
        getPhysicalPanel().addNucleus( nucleus );
        getModel().addModelElement( nucleus );
    }

    public void activate( PhetApplication app ) {
        orgDelay = this.clock.getDelay();
        orgDt = this.clock.getDt();
        this.clock.setDelay( 10 );
        this.clock.setDt( orgDt );
//        neutronGun = new NeutronGun();
//        Thread neutronGunThread = new Thread( neutronGun );
//        neutronGunThread.start();
    }

    public void deactivate( PhetApplication app ) {
        this.clock.setDelay( (int)( orgDelay ) );
        this.clock.setDt( orgDt );
//        neutronGun.kill();
    }

    public void fireNeutron() {
        double bounds = 600;
        double gamma = random.nextDouble() * Math.PI * 2;
        double x = bounds * Math.cos( gamma );
        double y = bounds * Math.sin( gamma );
        Neutron neutron = new Neutron( new Point2D.Double( x, y ), gamma + Math.PI );
        super.addNeutron( neutron );
    }

    Line2D.Double line = new Line2D.Double();
    Point2D.Double oldLocation = new Point2D.Double();

    private void checkForFission() {
        for( int i = 0; i < getModel().numModelElements(); i++ ) {
            ModelElement me = getModel().modelElementAt( i );
            if( me instanceof Neutron ) {
                Neutron neutron = (Neutron)me;
                oldLocation.setLocation( neutron.getLocation().getX() - neutron.getVelocity().getX() * clock.getDt(),
                                         neutron.getLocation().getY() - neutron.getVelocity().getY() * clock.getDt() );
                line.setLine( neutron.getLocation(), oldLocation );
                for( int j = 0; j < getModel().numModelElements(); j++ ) {
                    ModelElement me2 = getModel().modelElementAt( j );
                    if( me2 instanceof Uranium235 ) {
                        Nucleus nucleus = (Nucleus)me2;
//                        if( line.ptLineDistSq( nucleus.getLocation() ) < nucleus.getRadius() * nucleus.getRadius() ) {
                        if( neutron.getLocation().distanceSq( nucleus.getLocation() )
                            < nucleus.getRadius() * nucleus.getRadius() ) {
                            nucleus.fission( neutron );
                        }
                    }
                }
            }
        }
    }

    public void fission( FissionProducts products ) {

        // Remove the neutron and old nucleus
        getModel().removeModelElement( products.getInstigatingNeutron() );
        getModel().removeModelElement( products.getParent() );
        nuclei.remove( products.getParent() );
        List graphics = (List)NucleusGraphic.getGraphicForNucleus( products.getParent() );
        for( int i = 0; i < graphics.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)graphics.get( i );
            this.getPhysicalPanel().removeGraphic( ng );
        }
        NeutronGraphic ng = (NeutronGraphic)NeutronGraphic.getGraphicForNeutron( products.getInstigatingNeutron() );
        this.getPhysicalPanel().removeGraphic( ng );

        // Add fission products
        super.addNeucleus( products.getDaughter1() );
        super.addNeucleus( products.getDaughter2() );
        Neutron[] neutronProducts = products.getNeutronProducts();
        for( int i = 0; i < neutronProducts.length; i++ ) {
            NeutronGraphic npg = new NeutronGraphic( neutronProducts[i] );
            getModel().addModelElement( neutronProducts[i] );
            getPhysicalPanel().addGraphic( npg );
        }

        // Add some pizzazz
        Kaboom kaboom = new Kaboom( products.getParent().getLocation(),
                                    25, 300, getApparatusPanel() );
        getPhysicalPanel().addGraphic( kaboom );
    }

    public void removeNucleus( Nucleus nucleus ) {
        nuclei.remove( nucleus );
        ArrayList ngList = NucleusGraphic.getGraphicForNucleus( nucleus );
        for( int i = 0; i < ngList.size(); i++ ) {
            NucleusGraphic ng = (NucleusGraphic)ngList.get( i );
            super.remove( nucleus, ng );
        }
    }

    //
    // Inner classes
    //
//    private class InternalNeutronGun implements Runnable {
//        private long waitTime = 1000;
//        private boolean kill = false;
//
//        public void run() {
//            while( kill == false ) {
//                try {
//                    Thread.sleep( waitTime );
//                    this.fireNeutron();
//                }
//                catch( InterruptedException e ) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        public synchronized void kill() {
//            this.kill = true;
//        }
//
//        public void fireNeutron() {
//            double bounds = 600;
//            double gamma = random.nextDouble() * Math.PI * 2;
//            double x = bounds * Math.cos( gamma );
//            double y = bounds * Math.sin( gamma );
//            double theta = gamma
//                           + Math.PI
//                           + ( random.nextDouble() * Math.PI / 4 ) * ( random.nextBoolean() ? 1 : -1 );
//
//            Neutron neutron = new Neutron( new Point2D.Double( x, y ), theta );
//            MultipleNucleusFissionModule.this.neutronToAdd = neutron;
//        }
//
//    }
}
