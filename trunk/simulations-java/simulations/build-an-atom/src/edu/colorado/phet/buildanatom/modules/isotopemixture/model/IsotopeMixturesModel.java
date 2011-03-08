/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.isotopemixture.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import edu.colorado.phet.buildanatom.model.AtomIdentifier;
import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.IAtom;
import edu.colorado.phet.buildanatom.model.IConfigurableAtomModel;
import edu.colorado.phet.buildanatom.model.IDynamicAtom;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.model.MonoIsotopeParticleBucket;
import edu.colorado.phet.buildanatom.model.SphericalParticle;
import edu.colorado.phet.buildanatom.modules.game.model.SimpleAtom;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Model portion of Isotopes Mixture module.  This model contains a mixture
 * of isotopes and allows a user to move various different isotopes in and
 * out of the "Isotope Test Chamber", and simply keeps track of the average
 * mass within the chamber.
 *
 * @author John Blanco
 */
public class IsotopeMixturesModel implements Resettable, IConfigurableAtomModel {

    // -----------------------------------------------------------------------
    // Class Data
    // -----------------------------------------------------------------------

    // Size of the buckets that will hold the isotopes.
    private static final Dimension2D BUCKET_SIZE = new PDimension( 1000, 400 ); // In picometers.

    // Within this model, the isotopes come in two sizes, small and large, and
    // atoms are either one size or another, and all atoms that are shown at
    // a given time are all the same size.  The larger size is based somewhat
    // on reality.  The smaller size is used when we want to show a lot of
    // atoms at once.
    private static final double LARGE_ISOTOPE_RADIUS = 83; // in picometers.
    private static final double SMALL_ISOTOPE_RADIUS = 30; // in picometers.

    // Numbers of isotopes that are placed into the buckets when a new atomic
    // number is selected.
    private static final int NUM_LARGE_ISOTOPES_PER_BUCKET = 9;
    private static final int NUM_SMALL_ISOTOPES_PER_BUCKET = 75;

    // List of colors which will be used to represent the various isotopes.
    private static final Color [] ISOTOPE_COLORS = new Color [] { new Color( 180, 82, 205), Color.green,
        new Color(255, 69, 0), new Color( 139, 90, 43 ) };

    // Enum of the possible interactivity types.
    public enum InteractivityMode {
        BUCKETS_AND_LARGE_ATOMS,  // The user is dragging large isotopes between the test chamber and a set of buckets.
        SLIDERS_AND_SMALL_ATOMS   // The user is adding and removing small isotopes to/from the chamber using sliders.
    }

    // -----------------------------------------------------------------------
    // Instance Data
    // -----------------------------------------------------------------------

    private final BuildAnAtomClock clock;

    // The test chamber into and out of which the isotopes can be moved.
    private final IsotopeTestChamber testChamber = new IsotopeTestChamber();

    // This atom is the "prototype isotope", meaning that it is set in order
    // to set the atomic weight of the family of isotopes that are currently
    // in use.
    private final SimpleAtom prototypeIsotope = new SimpleAtom();

    // This property contains the list of isotopes that exist in nature as
    // variations of the current "prototype isotope".  In other words, this
    // contains a list of all stable isotopes that match the atomic weight
    // of the currently configured isotope.  There should be only one of each
    // possible isotope.
    private final Property<List<ImmutableAtom>> possibleIsotopesProperty =
            new Property<List<ImmutableAtom>>( new ArrayList<ImmutableAtom>() );

    // This property contains the list of the buckets where isotopes that are
    // not in the test chamber reside.  This needs to change whenever a new
    // element is selected, since there is one bucket for each stable isotope
    // configuration for a given element.
    private final Property<List<MonoIsotopeParticleBucket>> bucketListProperty =
            new Property<List<MonoIsotopeParticleBucket>>( new ArrayList<MonoIsotopeParticleBucket>() );

    // Property that determines the type of user interactivity is allowed.
    // See the enum that describes the modes for more information.
    private final Property<InteractivityMode> interactivityModeProperty =
        new Property<InteractivityMode>( InteractivityMode.BUCKETS_AND_LARGE_ATOMS );

    // Property that determines whether the user's mix or nature's mix is
    // being displayed.  When this is set to true, indicating that nature's
    // mix should be displayed, the isotope size property is ignored.
    private final BooleanProperty showingNaturesMix = new BooleanProperty( false );

    // List that contains the isotope instances that the user can be moved by
    // the user between the buckets and the test chamber.
    private final List<MovableAtom> usersMixOfIsotopes = new ArrayList<MovableAtom>();

    // List that contains the isotope instances that are shown in the test
    // chamber to represent nature's mix of isotopes.
    private final List<MovableAtom> naturesMixOfIsotopes = new ArrayList<MovableAtom>();

    // Matches isotopes to colors used to portray them as well as the buckets
    // in which they can reside, etc.
    private final Map< ImmutableAtom, Color > isotopeColorMap = new HashMap< ImmutableAtom, Color>();

    // Listener support.
    private final List<Listener> listeners = new ArrayList<Listener>();

    // -----------------------------------------------------------------------
    // Constructor(s)
    // -----------------------------------------------------------------------

    public IsotopeMixturesModel( BuildAnAtomClock clock ) {
        this.clock = clock;

        // Listen to our own interactive mode property so that things can be
        // reconfigured when this property changes.
        interactivityModeProperty.addObserver( new SimpleObserver() {
            public void update() {
                // Set the configuration again, which will create a different
                // size atom now that the size has been changed.
                setAtomConfiguration( prototypeIsotope );
            }
        }, false );

        // Listen to our own "showing nature's mix" property so that we can
        // show and hide the appropriate isotopes when the value changes.
        showingNaturesMix.addObserver( new SimpleObserver() {
            public void update() {
                if ( showingNaturesMix.getValue() == true ){
                    hideUsersMix();
                    showNaturesMix();
                }
                else{
                    showUsersMix();
                    hideNaturesMix();
                }
            }
        }, false );
    }

    // -----------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------

    public BuildAnAtomClock getClock() {
        return clock;
    }

    public IDynamicAtom getAtom(){
        return prototypeIsotope;
    }

    /**
     * Set the element that is currently in use, and for which all stable
     * isotopes will be available for movement in and out of the test chamber.
     * In case you're wondering why this is done as an atom instead of just
     * setting the atomic weight, it is so that this will play well with the
     * existing controllers (such as the PeriodicTableControlNode) that
     * already existed at the time this class was created.
     */
    public void setAtomConfiguration( IAtom atom ) {
        // This method does NOT check if the specified atom is already the
        // current configuration.  This allows it to be as a sort of reset
        // routine.  But callers should be careful not to call this when it
        // isn't needed.

        // Clear the test chamber.
        testChamber.removeAllIsotopes();

        // Remove all existing isotope instances from the user's mix.
        List<MovableAtom> usersMixOfIsotopesCopy = new ArrayList<MovableAtom>( usersMixOfIsotopes );
        usersMixOfIsotopes.clear();
        for ( MovableAtom isotope : usersMixOfIsotopesCopy ){
            // Signal the isotope that it has been removed from the model.
            isotope.removeListener( isotopeGrabbedListener );
            isotope.removeFromModel();
        }

        // Remove all existing isotopes from nature's mix.  Note that this
        // list is not repopulated until it is needed.
        List<MovableAtom> naturesMixOfIsotopesCopy = new ArrayList<MovableAtom>( naturesMixOfIsotopes );
        naturesMixOfIsotopes.clear();
        for ( MovableAtom isotope : naturesMixOfIsotopesCopy ){
            // Signal the isotope that it has been removed from the model.
            isotope.removeListener( isotopeGrabbedListener );
            isotope.removeFromModel();
        }

        // Update the prototype atom (a.k.a. isotope) configuration.
        prototypeIsotope.setNumProtons( atom.getNumProtons() );
        prototypeIsotope.setNumElectrons( atom.getNumElectrons() );
        prototypeIsotope.setNumNeutrons( atom.getNumNeutrons() );

        // Get a list of all stable isotopes for the current atomic number.
        ArrayList<ImmutableAtom> newIsotopeList = AtomIdentifier.getStableIsotopes( atom.getNumProtons() );

        // Sort from lightest to heaviest.
        Collections.sort( newIsotopeList, new Comparator<IAtom>(){
            public int compare( IAtom atom2, IAtom atom1 ) {
                return new Double(atom2.getAtomicMass()).compareTo( atom1.getAtomicMass() );
            }
        });

        // Update the structure that maps isotope to colors.
        isotopeColorMap.clear();
        for ( ImmutableAtom isotope : newIsotopeList ) {
            isotopeColorMap.put( isotope, ISOTOPE_COLORS[isotopeColorMap.size()] );
        }

        // Update the list of possible isotopes for this atomic configuration.
        possibleIsotopesProperty.setValue( newIsotopeList );

        // Make a copy of the soon-to-be removed buckets - we'll need them
        // later for removal notifications.
        ArrayList< Bucket > oldBuckets = new ArrayList< Bucket >( bucketListProperty.getValue() );

        // Figure out the atom radius to use based on the current atom size
        // setting.
        double isotopeRadius = interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ? LARGE_ISOTOPE_RADIUS : SMALL_ISOTOPE_RADIUS;

        // Create a new list of buckets based on the new list of stable
        // isotopes.
        double bucketYOffset = testChamber.getTestChamberRect().getMinY() - 400;
        double interBucketDistanceX = (testChamber.getTestChamberRect().getWidth() * 1.2) / newIsotopeList.size();
        double bucketXOffset = testChamber.getTestChamberRect().getMinX() + interBucketDistanceX / 2;
        ArrayList<MonoIsotopeParticleBucket> newBucketList = new ArrayList<MonoIsotopeParticleBucket>();
        for ( int i = 0; i < newIsotopeList.size(); i++ ) {
            ImmutableAtom isotope = newIsotopeList.get( i );
            String bucketCaption = AtomIdentifier.getName( isotope ) + "-" + isotope.getMassNumber();
            newBucketList.add( new MonoIsotopeParticleBucket( new Point2D.Double(
                    bucketXOffset + interBucketDistanceX * i, bucketYOffset ),
                    BUCKET_SIZE, isotopeColorMap.get( isotope ), bucketCaption, isotopeRadius,
                    isotope.getNumProtons(), isotope.getNumNeutrons() ) );
        }
        bucketListProperty.setValue( newBucketList );

        // Send notifications of removal for the old buckets, since they
        // were effectively removed by setting a new list of buckets.
        for ( Bucket bucket : oldBuckets ) {
            bucket.getPartOfModelProperty().setValue( false );
        }

        // Add the actual isotopes.
        if ( showingNaturesMix.getValue() ){
            showNaturesMix();
        }
        else{
            showUsersMix();
        }
    }

    /**
     * Get the bucket where the given isotope can be placed.
     *
     * @param isotope
     * @return A bucket that can hold the isotope if one exists, null if not.
     */
    private MonoIsotopeParticleBucket getBucketForIsotope( ImmutableAtom isotope ) {
        MonoIsotopeParticleBucket isotopeBucket = null;
        for ( MonoIsotopeParticleBucket bucket : bucketListProperty.getValue() ){
            if (bucket.isIsotopeAllowed( isotope )){
                // Found it.
                isotopeBucket = bucket;
                break;
            }
        }
        return isotopeBucket;
    }

    /**
     * Get a reference to the test chamber model.
     */
    public IsotopeTestChamber getIsotopeTestChamber(){
        return testChamber;
    }

    public Property<List<ImmutableAtom>> getPossibleIsotopesProperty() {
        return possibleIsotopesProperty;
    }

    public Property<InteractivityMode> getInteractivityModeProperty() {
        return interactivityModeProperty;
    }

    public Property<List<MonoIsotopeParticleBucket>> getBucketListProperty() {
        return bucketListProperty;
    }

    public BooleanProperty getShowingNaturesMixProperty() {
        return showingNaturesMix;
    }

    public Color getColorForIsotope( ImmutableAtom isotope ) {
        return isotopeColorMap.containsKey( isotope ) ? isotopeColorMap.get( isotope ) : Color.BLACK;
    }

    public void reset() {
        // Set the default element to be hydrogen.
        setAtomConfiguration( new ImmutableAtom( 1, 0, 1 ) );
    }

    public void addListener( Listener listener ){
        listeners.add( listener );
    }

    public void removeListener( Listener listener ){
        listeners.remove( listener );
    }

    private void notifyIsotopeInstanceAdded( MovableAtom atom ){
        for ( Listener listener : listeners ) {
            listener.isotopeInstanceAdded( atom );
        }
    }

    /**
     * Hide the user's mix of isotopes.  This is generally done when
     * switching to show nature's mix.  It is accomplished by sending
     * notifications of removal for each isotope but keeping them around.
     */
    private void hideUsersMix(){
        for ( MovableAtom isotope : usersMixOfIsotopes ){
            if ( testChamber.getTestChamberRect().contains( isotope.getPosition() ) ){
                testChamber.removeIsotope( isotope );
            }
            isotope.getPartOfModelProperty().setValue( false );
        }
    }

    /**
     * Show the user's mix.  This is intended to be called after the user's
     * mix of isotopes was previously hidden.
     */
    private void showUsersMix(){
        if ( usersMixOfIsotopes.size() == 0 ){
            // This is the first time that the user's mix has been shown since
            // the current element was selected.  Create the individual
            // isotope instances and add them to the bucket.
            double isotopeRadius = interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ? LARGE_ISOTOPE_RADIUS : SMALL_ISOTOPE_RADIUS;
            for ( ImmutableAtom isotope : possibleIsotopesProperty.getValue() ) {
                MonoIsotopeParticleBucket isotopeBucket = getBucketForIsotope( isotope );
                assert isotopeBucket != null; // If there is no bucket for this isotope, there is a bug.

                // Create each isotope instance and add to appropriate bucket.
                int numIsotopesPerBucket = interactivityModeProperty.getValue() == InteractivityMode.BUCKETS_AND_LARGE_ATOMS ? NUM_LARGE_ISOTOPES_PER_BUCKET : NUM_SMALL_ISOTOPES_PER_BUCKET;
                for ( int i = 0; i < numIsotopesPerBucket; i++){
                    MovableAtom movableIsotope = new MovableAtom( isotope.getNumProtons(), isotope.getNumNeutrons(),
                            isotopeRadius, new Point2D.Double(0, 0), clock );
                    movableIsotope.addListener( isotopeGrabbedListener );
                    isotopeBucket.addIsotopeInstance( movableIsotope );
                    usersMixOfIsotopes.add( movableIsotope );
                }
            }
        }
        // Set each isotope to be in the model.
        for ( MovableAtom isotope : usersMixOfIsotopes ){
            if ( testChamber.getTestChamberRect().contains( isotope.getPosition() )){
                testChamber.addIsotope( isotope );
            }
            isotope.getPartOfModelProperty().setValue( true );
            notifyIsotopeInstanceAdded( isotope );
        }
    }

    private void showNaturesMix(){
        if ( naturesMixOfIsotopes.size() == 0 ){
            // This is the first time that nature's mix has been shown since
            // the user selected the current element, so the isotope instances
            // need to be created.
            int totalNumIsotopes = 1000;  // TODO: Make this a constant if actually used.

            // Get the list of possible isotopes and then sort it by abundance
            // so that the least abundant are added last, thus assuring that
            // they will be visible.
            ArrayList<ImmutableAtom> possibleIsotopesCopy = new ArrayList<ImmutableAtom>(getPossibleIsotopesProperty().getValue());
            Collections.sort( possibleIsotopesCopy,  new Comparator<IAtom>(){
                    public int compare( IAtom atom2, IAtom atom1 ) {
                        return new Double(AtomIdentifier.getNaturalAbundance( atom1 )).compareTo( AtomIdentifier.getNaturalAbundance( atom2 ) );
                    }
                } );

            for ( ImmutableAtom isotopeConfig : possibleIsotopesCopy ){
                int numToCreate = (int)Math.round( totalNumIsotopes * AtomIdentifier.getNaturalAbundance( isotopeConfig ) );
                if ( numToCreate == 0 ){
                    System.err.println("Warning, quantity at zero for " + AtomIdentifier.getName( isotopeConfig ) + "-" + isotopeConfig.getMassNumber());
                    numToCreate = 1;
                }
                for ( int i = 0; i < numToCreate; i++){
                    MovableAtom newIsotope = new MovableAtom(
                            isotopeConfig.getNumProtons(),
                            isotopeConfig.getNumNeutrons(),
                            SMALL_ISOTOPE_RADIUS,
                            testChamber.generateRandomLocation(),
                            clock );
                    naturesMixOfIsotopes.add( newIsotope );
                }
            }
        }
        // Send out notifications of the isotopes being added to the model.
        for ( MovableAtom isotope : naturesMixOfIsotopes ){
            testChamber.addIsotope( isotope );
            isotope.getPartOfModelProperty().setValue( true );
            notifyIsotopeInstanceAdded( isotope );
        }
    }

    private void hideNaturesMix(){
        for ( MovableAtom isotope : naturesMixOfIsotopes ){
            testChamber.removeIsotope( isotope );
            isotope.getPartOfModelProperty().setValue( false );
        }
    }

    // -----------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    protected final SphericalParticle.Adapter isotopeGrabbedListener = new SphericalParticle.Adapter() {
        @Override
        public void grabbedByUser( SphericalParticle particle ) {
            assert particle instanceof MovableAtom;
            MovableAtom isotope = (MovableAtom)particle;
            if ( testChamber.isIsotopeContained( isotope ) ){
                // The particle is considered removed from the test chamber as
                // soon as it is grabbed.
                testChamber.removeIsotope( isotope );
            }
            isotope.addListener( isotopeDroppedListener );
        }
    };

    protected final SphericalParticle.Adapter isotopeDroppedListener = new SphericalParticle.Adapter() {
        @Override
        public void droppedByUser( SphericalParticle particle ) {
            assert particle instanceof MovableAtom;
            MovableAtom isotope = (MovableAtom)particle;
            if ( testChamber.isIsotopePositionedOverChamber( isotope ) ){
                // Dropped inside the test chamber, so add it to the chamber.
                testChamber.addIsotope( isotope );
            }
            else{
                // Particle was dropped outside of the test chamber, so return
                // it to the bucket.
                MonoIsotopeParticleBucket bucket = getBucketForIsotope( isotope.getAtomConfiguration() );
                assert bucket != null; // Should never have an isotope without a home.
                bucket.addIsotopeInstance( isotope );
            }
            particle.removeListener( isotopeDroppedListener );
        }
    };

    public interface Listener {
        void isotopeInstanceAdded( MovableAtom atom );
    }

    public class Adapter implements Listener {
        public void isotopeInstanceAdded( MovableAtom atom ) {}
    }

    /**
     * Class that represents a "test chamber" where multiple isotopes can be
     * placed.  The test chamber calculates the average atomic mass and the
     * proportions of the various isotopes.
     */
    public static class IsotopeTestChamber {

        // ------------------------------------------------------------------------
        // Class Data
        // ------------------------------------------------------------------------

        // Size of the "test chamber", which is the area in model space into which
        // the isotopes can be dragged in order to contribute to the current
        // average atomic weight.
        private static final Dimension2D SIZE = new PDimension( 3500, 3000 ); // In picometers.

        // Rectangle that defines the location of the test chamber.  This is
        // set up so that the center of the test chamber is at (0, 0) in model
        // space.
        private static final Rectangle2D TEST_CHAMBER_RECT =
            new Rectangle2D.Double(
                    -SIZE.getWidth() / 2,
                    -SIZE.getHeight() / 2,
                    SIZE.getWidth(),
                    SIZE.getHeight() );

        // Random number generator for generating positions.
        private static final Random RAND = new Random();

        // ------------------------------------------------------------------------
        // Instance Data
        // ------------------------------------------------------------------------

        // List of isotopes that are inside the chamber.  This is updated as
        // isotopes come and go.
        private final List<MovableAtom> containedIsotopes = new ArrayList<MovableAtom>();

        // Property that tracks the number of isotopes in the chamber.  This
        // can be monitored in order to update the view.
        private final Property<Integer> isotopeCountProperty = new Property<Integer>( 0 );

        // Property that tracks the average atomic mass of all isotopes in
        // the chamber.
        private final Property<Double> averageAtomicMassProperty = new Property<Double>( 0.0 );

        // ------------------------------------------------------------------------
        // Constructor(s)
        // ------------------------------------------------------------------------

        // ------------------------------------------------------------------------
        // Methods
        // ------------------------------------------------------------------------

        public Dimension2D getTestChamberSize() {
            return SIZE;
        }

        public Rectangle2D getTestChamberRect() {
            return TEST_CHAMBER_RECT;
        }

        /**
         * Get the position of the chamber.  The position is defined as the
         * center of the chamber, not as the upper left corner or any other
         * point.
         *
         * @return
         */
        public Point2D getPosition() {
            return new Point2D.Double( TEST_CHAMBER_RECT.getCenterX(), TEST_CHAMBER_RECT.getCenterY() );
        }

        /**
         * Test whether an isotope is within the chamber.  This is strictly
         * a 2D test that looks as the isotopes center position and determines
         * if it is within the bounds of the chamber rectangle.
         * @param isotop
         * @return
         */
        public boolean isIsotopePositionedOverChamber( MovableAtom isotope ){
            return TEST_CHAMBER_RECT.contains( isotope.getPosition() );
        }

        /**
         * Returns true if the specified isotope instance was previously added
         * to the chamber.
         *
         * @param isotope
         * @return
         */
        public boolean isIsotopeContained( MovableAtom isotope ){
            return containedIsotopes.contains( isotope );
        }

        /**
         * Add the specified isotope to the chamber.  This method requires
         * the the position of the isotope be within the chamber rectangle,
         * or the isotope will not be added.
         *
         * In cases where an isotope is in a position where the center is
         * within the chamber but the edges are not, the isotope will be moved
         * so that it is fully contained within the chamber.
         *
         * @param isotope
         */
        public void addIsotope( MovableAtom isotope ){
            if ( isIsotopePositionedOverChamber( isotope ) ){
                containedIsotopes.add( isotope );
                updateCountProperty();
                // Update the average atomic mass.
                averageAtomicMassProperty.setValue( ( averageAtomicMassProperty.getValue() *
                        ( isotopeCountProperty.getValue() - 1 ) + isotope.getAtomConfiguration().getAtomicMass() ) /
                        isotopeCountProperty.getValue() );
                // If the edges of the isotope are outside of the container,
                // move it to be fully inside.
                double protrusion = isotope.getPosition().getX() + isotope.getRadius() - TEST_CHAMBER_RECT.getMaxX();
                if (protrusion >= 0){
                    isotope.setPositionAndDestination( isotope.getPosition().getX() - protrusion,
                            isotope.getPosition().getY() );
                }
                else{
                    protrusion =  TEST_CHAMBER_RECT.getMinX() - (isotope.getPosition().getX() - isotope.getRadius());
                    if (protrusion >= 0){
                        isotope.setPositionAndDestination( isotope.getPosition().getX() + protrusion,
                                isotope.getPosition().getY() );
                    }
                }
                protrusion = isotope.getPosition().getY() + isotope.getRadius() - TEST_CHAMBER_RECT.getMaxY();
                if (protrusion >= 0){
                    isotope.setPositionAndDestination( isotope.getPosition().getX(),
                            isotope.getPosition().getY() - protrusion );
                }
                else{
                    protrusion =  TEST_CHAMBER_RECT.getMinY() - (isotope.getPosition().getY() - isotope.getRadius());
                    if (protrusion >= 0){
                        isotope.setPositionAndDestination( isotope.getPosition().getX(),
                                isotope.getPosition().getY() + protrusion );
                    }
                }
            }
            else{
                // This isotope is not positioned correctly.
                System.err.println(getClass().getName() + " - Warning: Ignoring attempt to add incorrectly located isotope to test chamber.");
            }
        }

        public void removeIsotope( MovableAtom isotope ){
            containedIsotopes.remove( isotope );
            updateCountProperty();
            // Update the average atomic mass.
            if ( isotopeCountProperty.getValue() > 0 ){
                averageAtomicMassProperty.setValue( ( averageAtomicMassProperty.getValue() * ( isotopeCountProperty.getValue() + 1 )
                        - isotope.getAtomConfiguration().getAtomicMass() ) / isotopeCountProperty.getValue() );
            }
            else{
                averageAtomicMassProperty.setValue( 0.0 );
            }
        }

        public void removeAllIsotopes(){
            containedIsotopes.clear();
            updateCountProperty();
            averageAtomicMassProperty.setValue( 0.0 );
        }

        /**
         * Get the property that tracks the number of isotopes in the chamber.
         * This can be monitored to trigger updates to view elements.
         */
        public Property<Integer> getIsotopeCountProperty(){
            return isotopeCountProperty;
        }

        /**
         * Get the property that tracks the average atomic mass of the
         * isotopes in the chamber.  This can be monitored to trigger update
         * events.
         */
        public Property<Double> getAverageAtomicMassProperty(){
            return averageAtomicMassProperty;
        }

        private void updateCountProperty(){
            isotopeCountProperty.setValue( containedIsotopes.size() );
        }

        /**
         * Get the proportion of isotopes currently within the chamber that
         * match the specified configuration.  Note that electrons are
         * ignored.
         *
         * @param isotopeConfig - Atom representing the configuration in
         * question, MUST BE NEUTRAL.
         * @return
         */
        public double getIsotopeProportion( ImmutableAtom isotopeConfig ){
            assert isotopeConfig.getCharge() == 0;
            // TODO: This could be done by a map that is updated each time an
            // atom is added if better performance is needed.  This should be
            // decided before publishing this sim.
            int isotopeCount = 0;
            for ( MovableAtom isotope : containedIsotopes ){
                if ( isotopeConfig.equals( isotope.getAtomConfiguration() )){
                    isotopeCount++;
                }
            }
            return (double)isotopeCount / (double)containedIsotopes.size();
        }

        /**
         * Generate a random location within the test chamber.
         * @return
         */
        public Point2D generateRandomLocation(){
            return new Point2D.Double(
                    TEST_CHAMBER_RECT.getMinX() + RAND.nextDouble() * TEST_CHAMBER_RECT.getWidth(),
                    TEST_CHAMBER_RECT.getMinY() + RAND.nextDouble() * TEST_CHAMBER_RECT.getHeight() );
        }
    }
}
