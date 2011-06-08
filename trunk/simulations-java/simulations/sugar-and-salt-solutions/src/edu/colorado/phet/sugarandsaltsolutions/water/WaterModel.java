// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.PolygonDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;

import static edu.colorado.phet.sugarandsaltsolutions.water.S3Element.CHLORINE_RADIUS;
import static edu.colorado.phet.sugarandsaltsolutions.water.S3Element.SODIUM_RADIUS;

/**
 * Model for "water" tab for sugar and salt solutions.
 *
 * @author Sam Reid
 */
public class WaterModel extends SugarAndSaltSolutionModel {

    //List of all model objects objects
    private ArrayList<WaterMolecule> waterList = new ArrayList<WaterMolecule>();

    //List of all Sodium ions
    private ArrayList<DefaultParticle> sodiumList = new ArrayList<DefaultParticle>();

    //List of all Chlorine ions
    private ArrayList<DefaultParticle> chlorineList = new ArrayList<DefaultParticle>();

    //Listeners who are called back when the physics updates
    private ArrayList<VoidFunction0> frameListeners = new ArrayList<VoidFunction0>();

    //Box2d world which updates the physics
    private World world;

    //Listeners that are notified when something enters the model.  Removal listeners are added to the particle itself
    private ArrayList<VoidFunction1<WaterMolecule>> waterAddedListeners = new ArrayList<VoidFunction1<WaterMolecule>>();
    private ArrayList<VoidFunction1<DefaultParticle>> sodiumAddedListeners = new ArrayList<VoidFunction1<DefaultParticle>>();
    private ArrayList<VoidFunction1<DefaultParticle>> chlorineAddedListeners = new ArrayList<VoidFunction1<DefaultParticle>>();

    private Random random = new Random();

    //Barriers are commented out while we evaluate periodic boundary conditions
//    public final Barrier bottomWall;
//    public final Barrier rightWall;
//    public final Barrier leftWall;

    public final double beakerWidth = 40E-10;
    public final double beakerHeight = beakerWidth * 0.6;
    private final double box2DWidth = 20;

    //units for water molecules are in SI

    //Beaker floor should be about 40 angstroms, to accommodate about 20 water molecules side-to-side
    //But keep box2d within -10..10 (i.e. 20 boxes wide), so scale factor is about
    double scaleFactor = box2DWidth / beakerWidth;
    private final ModelViewTransform modelToBox2D = ModelViewTransform.createSinglePointScaleMapping( new Point(), new Point(), scaleFactor );

    //Shapes for boundaries, used in periodic boundary conditions
    private ImmutableRectangle2D bottomWallShape;
    private ImmutableRectangle2D rightWallShape;
    private ImmutableRectangle2D leftWallShape;
    private ImmutableRectangle2D topWallShape;
    private int DEFAULT_NUM_WATERS = 100;
    public DoubleProperty numSodiums = new DoubleProperty( 0.0 );

    public WaterModel() {
        //Set the bounds of the physics engine.  The docs say things should be mostly between 0.1 and 10 units
        AABB worldAABB = new AABB();
        worldAABB.lowerBound = new Vec2( -200, -200 );
        worldAABB.upperBound = new Vec2( 200, 200 );

        //Create the world
        //No gravity
        world = new World( worldAABB, new Vec2( 0, 0 ), true );

        //Commented out while we evaluate periodic boundary conditions
//        world = new World( worldAABB, new Vec2( 0, -9.8f ), true );

        //Create beaker floor
        double glassThickness = 1E-10;
        bottomWallShape = new ImmutableRectangle2D( -beakerWidth / 2, 0, beakerWidth, glassThickness );
        topWallShape = new ImmutableRectangle2D( -beakerWidth / 2, beakerHeight, beakerWidth, glassThickness + beakerHeight );

        //Create sides
        rightWallShape = new ImmutableRectangle2D( beakerWidth / 2, 0, glassThickness, beakerHeight );
        leftWallShape = new ImmutableRectangle2D( -beakerWidth / 2, 0, glassThickness, beakerHeight );

        //Box2d barriers are commented out while we evaluate periodic boundary conditions
        //        leftWall = createBarrier( leftWallShape );
        //        bottomWall = createBarrier( bottomWallShape );
//        bottomWall = createBarrier( bottomWallShape );
//        rightWall = createBarrier( rightWallShape );

        //Move to a stable state on startup
        //Commented out because it takes too long
//        long startTime = System.currentTimeMillis();
//        for ( int i = 0; i < 10; i++ ) {
//            world.step( (float) ( clock.getDt() * 10 ), 1 );
//        }
//        System.out.println( "stable start time: " + ( System.currentTimeMillis() - startTime ) );

        //Set up initial state, same as reset() method would do
        initModel();
    }

    //Adds some NaCl molecules by adding nearby sodium and chlorine pairs, electrostatic forces are responsible for keeping them together until they are pulled apart by water
    public void addSalt() {
        final double separation = CHLORINE_RADIUS + SODIUM_RADIUS;

        addSodiumIon( 0, beakerHeight / 2 );
        addChlorineIon( separation, beakerHeight / 2 );

        addChlorineIon( 0, beakerHeight / 2 + separation );
        addSodiumIon( separation, beakerHeight / 2 + separation );
    }

    //Adds some random sodium particles
    private void addSodiumParticles( long seed ) {
        Random random = new Random( seed );
        for ( int i = 0; i < 10; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            final double x = float1 * beakerWidth / 2;
            final double y = random.nextFloat() * beakerHeight;
            addSodiumIon( x, y );
        }
    }

    //Adds a chlorine ion
    public void addChlorineIon( double x, double y ) {
        DefaultParticle chlorineIon = new DefaultParticle( world, modelToBox2D, x, y, 0, 0, 0, new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 chlorineMolecule ) {
                addFrameListener( chlorineMolecule );
            }
        }, -1, CHLORINE_RADIUS );
        chlorineList.add( chlorineIon );
        for ( VoidFunction1<DefaultParticle> chlorineAddedListener : chlorineAddedListeners ) {
            chlorineAddedListener.apply( chlorineIon );
        }
    }

    public void addSodiumIon( double x, double y ) {
        DefaultParticle sodiumIon = new DefaultParticle( world, modelToBox2D, x, y, 0, 0, 0, new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 sodiumMolecule ) {
                addFrameListener( sodiumMolecule );
            }
        }, +1, SODIUM_RADIUS );
        sodiumList.add( sodiumIon );
        updateNumSodiums();
        for ( VoidFunction1<DefaultParticle> sodiumAddedListener : sodiumAddedListeners ) {
            sodiumAddedListener.apply( sodiumIon );
        }
    }

    private void updateNumSodiums() {
        numSodiums.set( sodiumList.size() + 0.0 );
    }

    //Adds default water particles
    private void addWaterParticles( long seed, int numParticles ) {
        Random random = new Random( seed );
        for ( int i = 0; i < numParticles; i++ ) {
            float float1 = (float) ( ( random.nextFloat() - 0.5 ) * 2 );
            final double x = float1 * beakerWidth / 2;
            final double y = random.nextFloat() * beakerHeight;
            final float angle = (float) ( random.nextFloat() * Math.PI * 2 );
            addWater( x, y, angle );
        }
    }

    //Adds a single water molecule
    public void addWater( double x, double y, float angle ) {
        WaterMolecule water = new WaterMolecule( world, modelToBox2D, x, y, 0, 0, angle, new VoidFunction1<VoidFunction0>() {
            public void apply( VoidFunction0 waterMolecule ) {
                addFrameListener( waterMolecule );
            }
        } );
        waterList.add( water );
        for ( VoidFunction1<WaterMolecule> waterAddedListener : waterAddedListeners ) {
            waterAddedListener.apply( water );
        }
    }

    //Creates a rectangular barrier
    private Barrier createBarrier( ImmutableRectangle2D modelRect ) {
        final ImmutableRectangle2D box2DRect = modelToBox2D.modelToView( modelRect );
        PolygonDef shape = new PolygonDef() {{
            restitution = 0.2f;
            setAsBox( (float) box2DRect.width, (float) box2DRect.height );
        }};
        BodyDef bd = new BodyDef() {{
            position = new Vec2( (float) box2DRect.x, (float) box2DRect.y );
        }};
        Body body = world.createBody( bd );
        body.createShape( shape );
        body.setMassFromShapes();

        return new Barrier( body, modelRect );
    }

    public void addWaterAddedListener( VoidFunction1<WaterMolecule> waterAddedListener ) {
        waterAddedListeners.add( waterAddedListener );
    }

    @Override protected void updateModel( double dt ) {
        //Ignore super update for now
//        super.updateModel( dt );

        for ( WaterMolecule waterMolecule : waterList ) {
            //Apply a random force so the system doesn't settle down
            float rand1 = ( random.nextFloat() - 0.5f ) * 2;
            float rand2 = ( random.nextFloat() - 0.5f ) * 2;
            waterMolecule.body.applyForce( new Vec2( rand1 * randomness.get(), rand2 * randomness.get() ), waterMolecule.body.getPosition() );

            //Setting random velocity looks funny
//            double randomAngle = random.nextDouble() * Math.PI * 2;
//            ImmutableVector2D v = ImmutableVector2D.parseAngleAndMagnitude( rand1 * 1, randomAngle );
//            Vec2 linearVelocity = waterMolecule.body.getLinearVelocity();
//            waterMolecule.body.setLinearVelocity( new Vec2( linearVelocity.x + (float) v.getX(), linearVelocity.y + (float) v.getY() ) );
        }

        //Apply coulomb forces between all pairs of particles
        for ( WaterMolecule waterMolecule : waterList ) {
            waterMolecule.body.applyForce( getCoulombForce( waterMolecule.getOxygenParticle() ), waterMolecule.body.getPosition() );
            waterMolecule.body.applyForce( getCoulombForce( waterMolecule.getH1Particle() ), waterMolecule.getH1Particle().getBox2DPosition() );
            waterMolecule.body.applyForce( getCoulombForce( waterMolecule.getH2Particle() ), waterMolecule.getH2Particle().getBox2DPosition() );
        }
        for ( DefaultParticle sodiumIon : sodiumList ) {
            sodiumIon.body.applyForce( getCoulombForce( sodiumIon ), sodiumIon.body.getPosition() );
        }
        for ( DefaultParticle chlorineIon : chlorineList ) {
            chlorineIon.body.applyForce( getCoulombForce( chlorineIon ), chlorineIon.body.getPosition() );
        }
        world.step( (float) dt / 2, 50 );

        //Apply periodic boundary conditions
        applyPeriodicBoundaryConditions( sodiumList );
        applyPeriodicBoundaryConditions( chlorineList );
        applyPeriodicBoundaryConditions( waterList );

        //Notify listeners that the model changed
        for ( VoidFunction0 frameListener : frameListeners ) {
            frameListener.apply();
        }
    }

    //Move particles from one side of the screen to the other if they went out of bounds
    private void applyPeriodicBoundaryConditions( ArrayList<? extends Particle> ionList ) {
        for ( Particle sodium : ionList ) {
            if ( sodium.getModelPosition().getX() > rightWallShape.x ) {
                sodium.setModelPosition( new ImmutableVector2D( leftWallShape.getMaxX(), sodium.getModelPosition().getY() ) );
            }
            if ( sodium.getModelPosition().getX() < leftWallShape.getMaxX() ) {
                sodium.setModelPosition( new ImmutableVector2D( rightWallShape.x, sodium.getModelPosition().getY() ) );
            }
            if ( sodium.getModelPosition().getY() < bottomWallShape.getMaxY() ) {
                sodium.setModelPosition( new ImmutableVector2D( sodium.getModelPosition().getX(), topWallShape.y ) );
            }
            if ( sodium.getModelPosition().getY() > topWallShape.y ) {
                sodium.setModelPosition( new ImmutableVector2D( sodium.getModelPosition().getX(), bottomWallShape.getMaxY() ) );
            }
        }
    }

    //Gets the force on a single particle
    private Vec2 getCoulombForce( Particle target ) {
        Vec2 sumForces = new Vec2();
        for ( DefaultParticle source : sodiumList ) {
            sumForces = sumForces.add( getCoulombForce( source, target ) );
        }
        for ( DefaultParticle source : chlorineList ) {
            sumForces = sumForces.add( getCoulombForce( source, target ) );
        }
        for ( WaterMolecule water : waterList ) {
            sumForces = sumForces.add( getCoulombForce( water.getOxygenParticle(), target ) );
            sumForces = sumForces.add( getCoulombForce( water.getH1Particle(), target ) );
            sumForces = sumForces.add( getCoulombForce( water.getH2Particle(), target ) );
        }
        return sumForces;
    }

    //So we don't have to reallocate zeros all the time
    private final Vec2 zero = new Vec2();

    //Properties for developer controls
    public final Property<Integer> k = new Property<Integer>( 500 );
    public final Property<Integer> pow = new Property<Integer>( 2 );
    public final Property<Integer> randomness = new Property<Integer>( 10 );

    //Get the contribution to the total coulomb force from a single source
    private Vec2 getCoulombForce( Particle source, Particle target ) {
        if ( source == target ||
             ( source.getBox2DPosition().x == target.getBox2DPosition().x && source.getBox2DPosition().y == target.getBox2DPosition().y ) ) {
            return zero;
        }
        Vec2 r = source.getBox2DPosition().sub( target.getBox2DPosition() );
        double distance = r.length();
//        System.out.println( "distance = " + distance );

        //Optimize forces for the distance between a sodium and chlorine so it is the strongest bond.
        //Units are box2d units
        if ( distance < 1.2 ) { distance = 1.2; }

        double q1 = source.getCharge();
        double q2 = target.getCharge();

        //Use a gaussian so that NaCl has a strong affinity
//        double x0 = 1.29499;
//        double distanceFunction = Math.exp( -Math.pow( distance - x0, 2 ) );

        double distanceFunction = 1 / Math.pow( distance, pow.get() );
        double magnitude = -k.get() * q1 * q2 * distanceFunction;

//        System.out.println( "distance = " + distance + ", mag = " + magnitude );
//        System.out.println( distance + "\t" + magnitude );

//        double MAX = 1000;
//        double MIN = 1E-3;
//        if ( Math.abs( mag ) > MAX && q1 * q2 > 0 ) {
//            magnitude = MAX;
//        }
//        else if ( magnitude < MIN ) {
//            return zero;
//        }
//        System.out.println( magnitude );
        r.normalize();
        return r.mul( (float) magnitude );
    }

    //Get all bodies in the model
    public ArrayList<WaterMolecule> getWaterList() {
        return waterList;
    }

    //Register for a callback when the model steps
    public void addFrameListener( VoidFunction0 listener ) {
        frameListeners.add( listener );
    }

    //Resets the model, clearing water molecules and starting over
    @Override public void reset() {
        initModel();
    }

    //Set up the initial model state, used on init and after reset
    private void initModel() {
        clearWater();
        clearSodium();
        clearChlorine();

        //Add water particles
        addWaterParticles( System.currentTimeMillis(), DEFAULT_NUM_WATERS );
    }

    //TODO: factor out code from clear methods
    //Removes all water from the model
    private void clearWater() {
        for ( WaterMolecule waterMolecule : waterList ) {
            world.destroyBody( waterMolecule.body );
            waterMolecule.notifyRemoved();
        }
        waterList.clear();
    }

    //Removes all sodium from the model
    private void clearSodium() {
        for ( DefaultParticle sodiumMolecule : sodiumList ) {
            world.destroyBody( sodiumMolecule.body );
            sodiumMolecule.notifyRemoved();
        }
        sodiumList.clear();
        updateNumSodiums();
    }

    //Removes all Chlorine from the model
    private void clearChlorine() {
        for ( DefaultParticle chlorineParticle : chlorineList ) {
            world.destroyBody( chlorineParticle.body );
            chlorineParticle.notifyRemoved();
        }
        chlorineList.clear();
    }

    public ArrayList<DefaultParticle> getSodiumIonList() {
        return sodiumList;
    }

    public void addSodiumIonAddedListener( VoidFunction1<DefaultParticle> listener ) {
        sodiumAddedListeners.add( listener );
    }

    public ArrayList<DefaultParticle> getChlorineIonList() {
        return chlorineList;
    }

    public void addChlorineIonAddedListener( VoidFunction1<DefaultParticle> createNode ) {
        chlorineAddedListeners.add( createNode );
    }

    //Gets a random number within the horizontal range of the beaker
    public double getRandomX() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * beakerWidth - beakerWidth / 2 );
    }

    //Gets a random number within the vertical range of the beaker
    public double getRandomY() {
        return (float) ( SugarAndSaltSolutionsApplication.random.nextFloat() * beakerHeight );
    }

    //Model object representing a barrier, such as the beaker floor or wall which particles shouldn't pass through
    public static class Barrier {
        public final Body body;
        public final ImmutableRectangle2D shape;

        public Barrier( Body body, ImmutableRectangle2D shape ) {
            this.body = body;
            this.shape = shape;
        }
    }
}