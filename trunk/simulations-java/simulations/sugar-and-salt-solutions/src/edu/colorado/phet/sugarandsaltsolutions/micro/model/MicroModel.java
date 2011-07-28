// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.CompositeDoubleProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import edu.colorado.phet.sugarandsaltsolutions.common.model.BeakerDimension;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ISugarAndSaltModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CalciumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.CarbonIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.ChlorideIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.HydrogenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.OxygenIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.SodiumIonParticle;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.calciumchloride.CalciumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.EthanolDropper;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ethanol.EthanolMolecule;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideLattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumchloride.SodiumChlorideShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.NitrateMolecule;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.SodiumNitrateShaker;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseCrystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseLattice;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.SucroseMolecule;

import static edu.colorado.phet.common.phetcommon.math.ImmutableVector2D.parseAngleAndMagnitude;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.*;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.molesPerLiterToMolesPerMeterCubed;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.ParticleCountTable.MAX_SODIUM_CHLORIDE;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.ParticleCountTable.MAX_SUCROSE;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.RandomUtil.randomAngle;
import static edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.NEUTRAL_COLOR;
import static java.awt.Color.blue;
import static java.awt.Color.red;
import static java.lang.Math.PI;
import static java.lang.Math.random;

/**
 * Model for the micro tab, which uses code from soluble salts sim.
 *
 * @author Sam Reid
 */
public class MicroModel extends SugarAndSaltSolutionModel implements ISugarAndSaltModel {

    private static final double framesPerSecond = 30;

    //Keep track of how many times the user has tried to create macro salt, so that we can (less frequently) create corresponding micro crystals
    //TODO: should move to the micro sugar dispenser
    private final Property<Integer> stepsOfAddingSugar = new Property<Integer>( 0 );

    //List of all spherical particles
    public final ItemList<SphericalParticle> sphericalParticles = new ItemList<SphericalParticle>();

    //List of all free particles, used to keep track of which particles (includes molecules) to move about randomly
    public final ItemList<Particle> freeParticles = new ItemList<Particle>();

    //Lists of compounds
    public final ItemList<SodiumChlorideCrystal> sodiumChlorideCrystals = new ItemList<SodiumChlorideCrystal>();
    public final ItemList<SodiumNitrateCrystal> sodiumNitrateCrystals = new ItemList<SodiumNitrateCrystal>();
    public final ItemList<CalciumChlorideCrystal> calciumChlorideCrystals = new ItemList<CalciumChlorideCrystal>();
    public final ItemList<SucroseCrystal> sucroseCrystals = new ItemList<SucroseCrystal>();

    //Randomness for random walks
    private final Random random = new Random();

    //The factor by which to scale particle sizes, so they look a bit smaller in the graphics
    public static final double sizeScale = 0.35;

    //User setting for whether color should be based on charge or identity
    public final BooleanProperty showChargeColor = new BooleanProperty( false );

    //Settable property that indicates whether the clock is running or paused
    public final Property<Boolean> clockRunning = new Property<Boolean>( true );

    //The index of the kit selected by the user
    public final Property<Integer> selectedKit = new Property<Integer>( 0 ) {{

        //When the user switches kits, clear the solutes
        addObserver( new SimpleObserver() {
            public void update() {
                clearSolutes();
            }
        } );
    }};

    //Determine if there are any solutes (i.e., if moles of salt or moles of sugar is greater than zero).  This is used to show/hide the "remove solutes" button
    private ObservableProperty<Boolean> anySolutes = freeParticles.size.greaterThan( 0 );

    //Strategy rule to use for dissolving the crystals
    private IncrementalDissolve dissolveStrategy = new IncrementalDissolve();

    //Keep track of the last time a crystal was formed so that they can be created gradually instead of all at once
    private double lastNaClCrystallizationTime;

    //Add ethanol above the solution at the dropper output location
    public void addEthanol( final ImmutableVector2D location ) {
        EthanolMolecule ethanol = new EthanolMolecule( location, randomAngle() ) {{
            //Give the ethanol molecules some initial downward velocity since they are squirted out of the dropper
            velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 * 3 ).

                    //Add randomness so they look more fluid-like
                            plus( parseAngleAndMagnitude( 0.25E-9 / 4, random() * PI ) ) );
        }};
        freeParticles.add( ethanol );
        addComponents( ethanol );
    }

    //Colors for all the dissolved components
    public final ObservableProperty<Color> sodiumColor = new IonColor( this, new SodiumIonParticle() );
    public final ObservableProperty<Color> chlorideColor = new IonColor( this, new ChlorideIonParticle() );
    public final ObservableProperty<Color> calciumColor = new IonColor( this, new CalciumIonParticle() );
    public final ObservableProperty<Color> sucroseColor = new CompositeProperty<Color>( new Function0<Color>() {
        public Color apply() {
            return showChargeColor.get() ? NEUTRAL_COLOR : red;
        }
    }, showChargeColor );
    public final ObservableProperty<Color> nitrateColor = new CompositeProperty<Color>( new Function0<Color>() {
        public Color apply() {
            return showChargeColor.get() ? NEUTRAL_COLOR : blue;
        }
    }, showChargeColor );
    public final ObservableProperty<Color> ethanolColor = new CompositeProperty<Color>( new Function0<Color>() {
        public Color apply() {
            return showChargeColor.get() ? NEUTRAL_COLOR : Color.pink;
        }
    }, showChargeColor );

    //Particle concentrations for all of the dissolved components
    public final CompositeDoubleProperty sodiumConcentration = new IonConcentration( this, SodiumIonParticle.class );
    public final CompositeDoubleProperty chlorideConcentration = new IonConcentration( this, ChlorideIonParticle.class );
    public final CompositeDoubleProperty calciumConcentration = new IonConcentration( this, CalciumIonParticle.class );
    public final CompositeDoubleProperty sucroseConcentration = new IonConcentration( this, SucroseMolecule.class );
    public final CompositeDoubleProperty ethanolConcentration = new IonConcentration( this, EthanolMolecule.class );
    public final CompositeDoubleProperty nitrateConcentration = new IonConcentration( this, NitrateMolecule.class );

    public MicroModel() {
        //SolubleSalts clock runs much faster than wall time
        super( new ConstantDtClock( framesPerSecond ),

               //The volume of the micro beaker should be 2E-23L
               //In the macro tab, the dimension is BeakerDimension( width = 0.2, height = 0.1, depth = 0.1 ), each unit in meters
               //So if it is to have the same shape is as the previous tab then we use
               // width*height*depth = 2E-23
               // and
               // width = 2*height = 2*depth
               //Solving for width, we have:
               // 2E-23 = width * width/2 * width/2
               // =>
               // 8E-23 = width^3.  Therefore
               // width = cuberoot(8E-23)
               new BeakerDimension( Math.pow( 8E-23
                                              //convert L to meters cubed
                                              * 0.001, 1 / 3.0 ) ),

               //Flow rate must be slowed since the beaker is so small.  TODO: compute this factor analytically so that it will match the first tab perfectly?  Factor out numbers?
               0.0005 * 2E-23 / 2,

               //Values sampled at runtime using a debugger using this line in SugarAndSaltSolutionModel.update: System.out.println( "solution.shape.get().getBounds2D().getMaxY() = " + solution.shape.get().getBounds2D().getMaxY() );
               2.5440282964793075E-10, 5.75234062238494E-10,

               //Ratio of length scales in meters
               1.0 / Math.pow( 8E-23 * 0.001, 1 / 3.0 ) / 0.2 );

        ObservableProperty<Boolean> moreSodiumChlorideAllowed = sphericalParticles.propertyCount( SodiumIonParticle.class ).max( sphericalParticles.propertyCount( ChlorideIonParticle.class ) ).lessThan( MAX_SODIUM_CHLORIDE );
        ObservableProperty<Boolean> moreSucroseAllowed = freeParticles.propertyCount( SucroseMolecule.class ).lessThan( MAX_SUCROSE );

        //Add models for the various dispensers: sugar, salt, etc.
        dispensers.add( new SodiumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSodiumChlorideAllowed, getSaltShakerName(), distanceScale, dispenserType, SALT ) );
        dispensers.add( new SugarDispenser( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSucroseAllowed, getSugarDispenserName(), distanceScale, dispenserType, SUGAR ) );
        dispensers.add( new SodiumNitrateShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSodiumChlorideAllowed, SODIUM_NITRATE_NEW_LINE, distanceScale, dispenserType, DispenserType.SODIUM_NITRATE ) );
        dispensers.add( new CalciumChlorideShaker( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, beaker, moreSodiumChlorideAllowed, CALCIUM_CHLORIDE_NEW_LINE, distanceScale, dispenserType, DispenserType.CALCIUM_CHLORIDE ) );
        dispensers.add( new EthanolDropper( beaker.getCenterX(), beaker.getTopY() + beaker.getHeight() * 0.5, 0, beaker, moreSodiumChlorideAllowed, Strings.ETHANOL, distanceScale, dispenserType, DispenserType.ETHANOL ) );

        //When the pause button is pressed, pause the clock
        clockRunning.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean running ) {
                clock.setRunning( running );
            }
        } );

        //When the clock pauses or starts, update the property
        clock.addClockListener( new ClockAdapter() {
            @Override public void clockPaused( ClockEvent clockEvent ) {
                clockRunning.set( false );
            }

            @Override public void clockStarted( ClockEvent clockEvent ) {
                clockRunning.set( true );
            }
        } );
    }

    //When a macro salt would be shaken out of the shaker, instead add a micro salt crystal
    public void addSaltCrystal( SodiumChlorideCrystal sodiumChlorideCrystal ) {
        //Add the components of the lattice to the model so the graphics will be created
        for ( Constituent constituent : sodiumChlorideCrystal ) {
            //TODO: separate list for NaCl crystals so no cast required here?
            sphericalParticles.add( (SphericalParticle) constituent.particle );
        }
        sodiumChlorideCrystals.add( sodiumChlorideCrystal );
    }

    public void addSodiumNitrateCrystal( SodiumNitrateCrystal crystal ) {
        addComponents( crystal );
        sodiumNitrateCrystals.add( crystal );
    }

    //Add all SphericalParticles contained in the compound so the graphics will be created
    private void addComponents( Compound<? extends Particle> compound ) {
        for ( SphericalParticle sphericalParticle : compound.getAllSphericalParticles() ) {
            sphericalParticles.add( sphericalParticle );
        }
    }

    public void addCalciumChlorideCrystal( CalciumChlorideCrystal calciumChlorideCrystal ) {
        addComponents( calciumChlorideCrystal );
        calciumChlorideCrystals.add( calciumChlorideCrystal );
    }

    //When a macro sugar would be shaken out of the shaker, instead add a micro sugar crystal
    //TODO: Create a new separate method for adding sucrose
    @Override public void addMacroSugar( MacroSugar sugar ) {

        //Only add a crystal every N steps, otherwise there are too many
        stepsOfAddingSugar.set( stepsOfAddingSugar.get() + 1 );
        if ( stepsOfAddingSugar.get() % 30 == 0 ) {
            //TODO: Make getPosition abstract so the particle can query the lattice?

            //Create a random crystal
            //TODO: get rid of cast here
            final SucroseCrystal crystal = new SucroseCrystal( sugar.position.get(), (SucroseLattice) new SucroseLattice().grow( 3 ), RandomUtil.randomAngle() );
            addComponents( crystal );
            sucroseCrystals.add( crystal );
        }
    }

    //Determine saturation points
    double sodiumChlorideSaturationPoint = molesPerLiterToMolesPerMeterCubed( 6.14 );
    double calciumChlorideSaturationPoint = molesPerLiterToMolesPerMeterCubed( 6.71 );
    double sodiumNitrateSaturationPoint = molesPerLiterToMolesPerMeterCubed( 10.8 );
    double sucroseSaturationPoint = molesPerLiterToMolesPerMeterCubed( 5.84 );

    ObservableProperty<Boolean> sodiumChlorideUnsaturated = sodiumConcentration.lessThan( sodiumChlorideSaturationPoint ).and( chlorideConcentration.lessThan( sodiumChlorideSaturationPoint ) );

    //When the simulation clock ticks, move the particles
    @Override protected void updateModel( double dt ) {
        super.updateModel( dt );

        //Move the free particles randomly
        updateParticles( dt, freeParticles );


        //Dissolve the crystals if they are below the saturation points
        //In CaCl2, the factor of 2 accounts for the fact that CaCl2 needs 2 Cl- for every 1 Ca2+
        //No saturation point for ethanol, which is miscible
        updateCrystals( dt, sodiumChlorideCrystals, sodiumChlorideUnsaturated );
        updateCrystals( dt, calciumChlorideCrystals, calciumConcentration.lessThan( calciumChlorideSaturationPoint ).and( chlorideConcentration.lessThan( calciumChlorideSaturationPoint ) ) );
        updateCrystals( dt, sodiumNitrateCrystals, sodiumConcentration.lessThan( sodiumNitrateSaturationPoint ).and( nitrateConcentration.lessThan( sodiumNitrateSaturationPoint ) ) );
        updateCrystals( dt, sucroseCrystals, sucroseConcentration.lessThan( sucroseSaturationPoint ) );

        formNaClCrystals( sodiumChlorideUnsaturated );
    }

    //Check to see whether it is time to create or add to existing crystals, if the solution is over saturated
    private void formNaClCrystals( ObservableProperty<Boolean> sodiumChlorideUnsaturated ) {
        double timeSinceLast = time - lastNaClCrystallizationTime;

        //Make sure at least 1 second has passed, then convert to crystals
        if ( !sodiumChlorideUnsaturated.get() && timeSinceLast > 1 && sodiumChlorideCrystals.size() == 0 ) {

            //Create a crystal if there weren't any
            convertToCrystal( new SodiumIonParticle() );
            lastNaClCrystallizationTime = time;
        }

        //try adding on to an existing crystal
        else if ( !sodiumChlorideUnsaturated.get() ) {

            //Find existing crystal(s)
            SodiumChlorideCrystal crystal = sodiumChlorideCrystals.get( 0 );

            //Look for an open site on its lattice
            ArrayList<CrystalSite> openSites = crystal.getOpenSites();

            class Match {
                public final Particle particle;
                public final CrystalSite crystalSite;
                public final double distance;

                Match( Particle particle, CrystalSite crystalSite ) {
                    this.particle = particle;
                    this.crystalSite = crystalSite;
                    this.distance = particle.position.get().minus( crystalSite.position ).getMagnitude();
                }

                @Override public String toString() {
                    return "Match{" +
                           "particle=" + particle +
                           ", crystalSite=" + crystalSite +
                           ", distance=" + distance +
                           '}';
                }
            }

            //Enumerate all particles and distances from crystal sites
            ArrayList<Match> matches = new ArrayList<Match>();
            for ( Particle freeParticle : freeParticles ) {
                for ( CrystalSite openSite : openSites ) {
                    if ( openSite.matches( freeParticle ) ) {
                        matches.add( new Match( freeParticle, openSite ) );
                    }
                }
            }

            //Find a matching particle nearby one of the sites and join it together
            Collections.sort( matches, new Comparator<Match>() {
                public int compare( Match o1, Match o2 ) {
                    return Double.compare( o1.distance, o2.distance );
                }
            } );
            Match match = matches.get( 0 );

            //If close enough, join the lattice
            //This number was determined by reducing particle velocity and determining a good value for when the particle is close enough
            if ( match.distance < 5E-12 ) {

                //Remove the particle
                freeParticles.remove( match.particle );
                sphericalParticles.remove( (SphericalParticle) match.particle );

                //Remove the old crystal
                for ( Constituent<SphericalParticle> constituent : crystal ) {
                    sphericalParticles.remove( constituent.particle );
                }
                sodiumChlorideCrystals.remove( crystal );

                //Create a new crystal that combines the pre-existing crystal and the new particle
                addSaltCrystal( new SodiumChlorideCrystal( crystal.position.get(), (SodiumChlorideLattice) crystal.growCrystal( match.crystalSite ), crystal.angle ) );
            }

            //Otherwise, move closest particle toward the lattice
            else {
                match.particle.velocity.set( match.crystalSite.position.minus( match.particle.position.get() ).getInstanceOfMagnitude( 0.25E-9 / 10 ) );
            }
        }
    }

    //Convert a particle to a crystal, or add to existing crystals to decrease the concentration below the saturation point
    private void convertToCrystal( SphericalParticle component ) {
        ArrayList<Particle> p = freeParticles.filter( SphericalParticle.class );
        if ( p.size() > 0 ) {
            //If there is no crystal, create one with one particle
            Particle firstParticle = p.get( 0 );
            SodiumChlorideCrystal crystal = new SodiumChlorideCrystal( firstParticle.position.get(), new SodiumChlorideLattice( component ), randomAngle() );

            freeParticles.remove( firstParticle );

            //TODO: eliminate this cast
            sphericalParticles.remove( (SphericalParticle) firstParticle );

            addSaltCrystal( crystal );
        }
    }

    //Update the crystals by moving them about and possibly dissolving them
    private void updateCrystals( double dt, ItemList<? extends Crystal> crystals, ObservableProperty<Boolean> unsaturated ) {
        //Keep track of which lattices should dissolve in this time step
        ArrayList<Crystal> toDissolve = new ArrayList<Crystal>();
        for ( Crystal crystal : crystals ) {
            //Accelerate the particle due to gravity and perform an euler integration step
            //This number was obtained by guessing and checking to find a value that looked good for accelerating the particles out of the shaker
            double mass = 1E10;

            //Cache the value to improve performance by 30% when number of particles is large
            final boolean anyPartUnderwater = isAnyPartUnderwater( crystal );

            //If any part touched the water, the lattice should slow down and move at a constant speed
            if ( anyPartUnderwater ) {
                crystal.velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 ) );
            }

            //If completely underwater, lattice should prepare to dissolve
            if ( isCompletelyUnderwater( crystal ) && !crystal.isUnderwater() ) {
                crystal.setUnderwater( time );
            }
            crystal.stepInTime( getExternalForce( anyPartUnderwater ).times( 1.0 / mass ), dt );

            //Collide with the bottom of the beaker
            double minY = crystal.getShape().getBounds2D().getMinY();
            if ( minY < 0 ) {
                crystal.translate( 0, -minY );
            }

            //Determine whether it is time for the lattice to dissolve
            if ( crystal.isUnderwater() ) {
                final double timeUnderwater = time - crystal.getUnderWaterTime();

                //Make sure it has been underwater for a certain period of time (in seconds)
                if ( timeUnderwater > 0.5 ) {
                    toDissolve.add( crystal );
                }
            }
        }

        //Handle dissolving the lattices
        for ( Crystal crystal : toDissolve ) {
            dissolveStrategy.dissolve( crystals, crystal, freeParticles, unsaturated );
        }
    }

    //Get the external force acting on the particle, gravity if the particle is in free fall or zero otherwise (e.g., in solution)
    private ImmutableVector2D getExternalForce( final boolean anyPartUnderwater ) {
        return new ImmutableVector2D( 0, anyPartUnderwater ? 0 : -9.8 );
    }

    //Determine whether the object is underwater--when it touches the water it should slow down
    private boolean isAnyPartUnderwater( Particle particle ) {
        return particle.getShape().intersects( solution.shape.get().getBounds2D() );
    }

    //Determine whether the object is completely underwater--when it goes completely underwater it should dissolve soon
    private boolean isCompletelyUnderwater( Particle particle ) {
        return solution.shape.get().getBounds2D().contains( particle.getShape().getBounds2D() );
    }

    //When the simulation clock ticks, move the particles
    private void updateParticles( double dt, ItemList<? extends Particle> particles ) {
        for ( Particle particle : particles ) {

            boolean initiallyUnderwater = solution.shape.get().contains( particle.getShape().getBounds2D() );
            ImmutableVector2D initialPosition = particle.position.get();
            ImmutableVector2D initialVelocity = particle.velocity.get();

            //If the particle was stopped by the water completely evaporating, start it moving again
            //Must be done before particle.stepInTime so that the particle doesn't pick up a small velocity in that method, since this assumes particle velocity of zero implies evaporated to the bottom
            if ( particle.velocity.get().getMagnitude() == 0 ) {
                setWaterSpeed( particle );
            }

            //Make sure submerged particles are all moving at a high enough rate, instead of just relying on their previously set velocity to be correct
            if ( initiallyUnderwater ) {
                particle.velocity.set( particle.velocity.get().getInstanceOfMagnitude( 0.25E-9 ) );
            }


            //Accelerate the particle due to gravity and perform an euler integration step
            //This number was obtained by guessing and checking to find a value that looked good for accelerating the particles out of the shaker
            double mass = 1E10;
            particle.stepInTime( getExternalForce( isAnyPartUnderwater( particle ) ).times( 1.0 / mass ), dt );

            boolean underwater = solution.shape.get().contains( particle.getShape().getBounds2D() );

            //If the particle entered the water on this step, slow it down to simulate hitting the water
            if ( !initiallyUnderwater && underwater && particle.position.get().getY() > beaker.getHeightForVolume( waterVolume.get() ) / 2 ) {
                setWaterSpeed( particle );
            }

            //Random Walk, implementation taken from edu.colorado.phet.solublesalts.model.RandomWalk
            if ( underwater ) {
                double theta = random.nextDouble() * Math.toRadians( 30.0 ) * MathUtil.nextRandomSign();
                particle.velocity.set( particle.velocity.get().getRotatedInstance( theta ) );
            }

            //Prevent the particles from leaving the solution, but only if they started in the solution
            if ( initiallyUnderwater && !underwater ) {
                ImmutableVector2D delta = particle.position.get().minus( initialPosition );
                particle.position.set( initialPosition );

                //If the particle hit the wall, point its velocity in the opposite direction so it will move away from the wall
                particle.velocity.set( parseAngleAndMagnitude( initialVelocity.getMagnitude(), delta.getAngle() + PI ) );
            }

            //Stop the particle completely if there is no water to move within
            if ( waterVolume.get() <= 0 ) {
                particle.velocity.set( new ImmutableVector2D( 0, 0 ) );
            }

            //Keep the particle within the beaker solution bounds
            preventFromFallingThroughBeakerBase( particle );
            preventFromFallingThroughBeakerRight( particle );
            preventFromFallingThroughBeakerLeft( particle );
        }
    }

    private void setWaterSpeed( Particle particle ) {
        particle.velocity.set( new ImmutableVector2D( 0, -1 ).times( 0.25E-9 ) );
    }

    public void reset() {
        super.reset();

        //Clear out solutes, particles, concentration values
        clearSolutes();

        //Reset model for user settings
        showConcentrationValues.reset();
        dispenserType.reset();
        showChargeColor.reset();
        selectedKit.reset();
        clockRunning.reset();
    }

    private void clearSolutes() {
        //Clear particle lists
        sphericalParticles.clear();
        freeParticles.clear();
        sodiumChlorideCrystals.clear();
        sodiumNitrateCrystals.clear();
        calciumChlorideCrystals.clear();
        sucroseCrystals.clear();
    }

    //Determine if there is any table salt to remove
    public ObservableProperty<Boolean> isAnySaltToRemove() {
        return sodiumConcentration.greaterThan( 0.0 ).and( chlorideConcentration.greaterThan( 0.0 ) );
    }

    //Determine if there is any sugar that can be removed
    public ObservableProperty<Boolean> isAnySugarToRemove() {
        return sucroseConcentration.greaterThan( 0.0 );
    }

    public void removeSalt() {
        super.removeSalt();

        sphericalParticles.clear( SodiumIonParticle.class, ChlorideIonParticle.class );
        freeParticles.clear( SodiumIonParticle.class, ChlorideIonParticle.class );
        sodiumChlorideCrystals.clear();
    }

    public void removeSugar() {
        super.removeSugar();

        //TODO: will need to be more discriminative about which spherical particles to remove when in solution with ethanol
        sphericalParticles.clear( HydrogenIonParticle.class, CarbonIonParticle.class, OxygenIonParticle.class );
        freeParticles.clear( SucroseMolecule.class );
        sodiumChlorideCrystals.clear();
    }

    @Override public ObservableProperty<Boolean> getAnySolutes() {
        return anySolutes;
    }

    /**
     * @inheritDoc
     */
    @Override protected String getSaltShakerName() {
        return SODIUM_CHLORIDE_NEW_LINE;
    }

    /**
     * @inheritDoc
     */
    @Override protected String getSugarDispenserName() {
        return SUCROSE;
    }

    //Called when water flows out of the output faucet, so that we can move update the particles accordingly
    @Override protected void waterDrained( double outVolume, double initialSaltConcentration, double initialSugarConcentration ) {
        super.waterDrained( outVolume, initialSaltConcentration, initialSugarConcentration );
        updateParticlesDueToWaterLevelDropped( outVolume );
    }

    //Iterate over particles that take random walks so they don't move above the top of the water
    private void updateParticlesDueToWaterLevelDropped( double changeInWaterHeight ) {
        waterLevelDropped( freeParticles, changeInWaterHeight );
        waterLevelDropped( sucroseCrystals, changeInWaterHeight );
    }

    //When water level decreases, move the particles down with the water level.
    //Beaker base is at y=0.  Move particles proportionately to how close they are to the top.
    private void waterLevelDropped( ItemList<? extends Particle> particles, double volumeDropped ) {

        double changeInWaterHeight = beaker.getHeightForVolume( volumeDropped ) - beaker.getHeightForVolume( 0 );
        for ( Particle particle : particles ) {
            if ( waterVolume.get() > 0 ) {
                double yLocationInBeaker = particle.position.get().getY();
                double waterTopY = beaker.getHeightForVolume( waterVolume.get() );
                double fractionToTop = yLocationInBeaker / waterTopY;
                particle.translate( 0, -changeInWaterHeight * fractionToTop );

                //Prevent particles from leaving the top of the liquid
                double topY = particle.getShape().getBounds2D().getMaxY();
                if ( topY > waterTopY ) {
                    particle.translate( 0, ( waterTopY - topY - 1E-12 ) );
                }
            }

            //This step must be done after prevention of particles leaving the top because falling through the bottom is worse (never returns), pushing through the top, particles
            //would just fall back to the water level
            preventFromFallingThroughBeakerBase( particle );
        }
    }

    //prevent particles from falling through the bottom of the beaker
    private void preventFromFallingThroughBeakerBase( Particle particle ) {
        double bottomY = particle.getShape().getBounds2D().getMinY();
        if ( bottomY < 0 ) {
            particle.translate( 0, -bottomY + 1E-12 );
        }
    }

    //prevent particles from falling through the bottom of the beaker
    private void preventFromFallingThroughBeakerLeft( Particle particle ) {
        double left = particle.getShape().getBounds2D().getMinX();
        if ( left < beaker.getLeftWall().getX1() ) {
            particle.translate( beaker.getLeftWall().getX1() - left, 0 );
        }
    }

    //prevent particles from falling through the bottom of the beaker
    private void preventFromFallingThroughBeakerRight( Particle particle ) {
        double right = particle.getShape().getBounds2D().getMaxX();
        if ( right > beaker.getRightWall().getX1() ) {
            particle.translate( beaker.getRightWall().getX1() - right, 0 );
        }
    }

    //When water evaporates, move the particles so they move down with the water level
    @Override protected void waterEvaporated( double evaporatedWater ) {
        super.waterEvaporated( evaporatedWater );
        updateParticlesDueToWaterLevelDropped( evaporatedWater );
    }
}