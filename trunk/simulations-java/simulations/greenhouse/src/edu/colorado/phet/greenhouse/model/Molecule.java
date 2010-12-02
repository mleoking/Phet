/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;

/**
 * Class that represents a molecule in the model.  This, by its nature, is
 * essentially a composition of other objects, generally atoms and atomic
 * bonds.
 *
 * @author John Blanco
 */
public abstract class Molecule {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    private static final double PHOTON_EMISSION_SPEED = 2; // Picometers per second.

    private static final double PHOTON_ABSORPTION_DISTANCE = 100;

    private static final Random RAND = new Random();

    private static final double OSCILLATION_FREQUENCY = 3;  // Cycles per second of sim time.
    private static final double ABSORPTION_HYSTERESIS_TIME = 200; // Milliseconds of sim time.

    private static final double MIN_PHOTON_HOLD_TIME = 600; // Milliseconds of sim time.
    private static final double MAX_PHOTON_HOLD_TIME = 1200; // Milliseconds of sim time.

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Atoms and bonds that comprise this molecule.
    protected final ArrayList<Atom> atoms = new ArrayList<Atom>();
    protected final ArrayList<AtomicBond> atomicBonds = new ArrayList<AtomicBond>();

    // Offsets for each atom from the molecule's center of gravity.
    protected final HashMap<Atom, Vector2D> atomCogOffsets = new HashMap<Atom, Vector2D>();

    protected final ArrayList<Listener> listeners = new ArrayList<Listener>();

    // This is basically the location of the molecule, but it is specified as
    // the center of gravity since a molecule is a composite object.
    private final Point2D centerOfGravity = new Point2D.Double();

    // Velocity and acceleration for this molecule.
    private final Vector2D velocity = new Vector2D( 0, 0 );

    // Variable that tracks whether or not a photon has been absorbed and has
    // not yet been re-emitted.
    private boolean photonAbsorbed = false;

    //PHOTON RE_EMISSION
    // Variables involved in the holding and re-emitting of photons.
    private double photonHoldCountdownTime = 0;
    // Photon to be emitted when the photon hold timer expires.
    protected Photon photonToEmit = null;

    //BREAK APART
    // Variables involved in the holding and re-emitting of photons.
    private double breakApartCountdownTime = 0;

    private double absorbtionHysteresisCountdownTime = 0;

    // The "pass through photon list" keeps track of photons that were not
    // absorbed due to random probability (essentially a simulation of quantum
    // properties).  This is needed since the absorption of a given photon
    // will likely be tested at many time steps, so we need to keep track of
    // whether it gets rejected.
    private static final int PASS_THROUGH_PHOTON_LIST_SIZE = 10;
    private final ArrayList<Photon> passThroughPhotonList = new ArrayList<Photon>( PASS_THROUGH_PHOTON_LIST_SIZE );

    // List of photon wavelengths that this molecule can absorb.  This is a
    // list of frequencies, which is a grand oversimplification of the real
    // behavior, but works for the current purposes of this sim.
    private final ArrayList<Double> photonAbsorptionWavelengths = new ArrayList<Double>(2);

    // TODO: This is temp for prototyping.
    protected boolean rotateClockwise = false;

    private PhotonAbsorptionReactionStrategy strategy = new VibrationStrategy( this );

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    protected boolean isPhotonAbsorbed() {
        return photonAbsorbed;
    }

    protected void setPhotonAbsorbed( boolean photonAbsorbed ) {
        this.photonAbsorbed = photonAbsorbed;
    }

    /**
     * Get the ID used for this molecule.
     */
    public abstract MoleculeID getMoleculeID();

    /**
     * Static factory method for producing molecules of a given type.
     */
    public static Molecule createMolecule( MoleculeID moleculeID){
        Molecule newMolecule = null;
        switch ( moleculeID ){
        case CH4:
            newMolecule = new CH4();
            break;
        case CO2:
            newMolecule = new CO2();
            break;
        case H2O:
            newMolecule = new H2O();
            break;
        case N2:
            newMolecule = new N2();
            break;
        case N2O:
            newMolecule = new N2O();
            break;
        case O2:
            newMolecule = new O2();
            break;
        default:
            System.err.println("Molecule: " + " - Error: Unrecognized molecule type.");
            assert false;
        }

        return newMolecule;
    }

    /**
     * Advance the molecule one step in time.
     */
    public void stepInTime(double dt){

        if (isPhotonAbsorbed()){
            strategy.stepInTime(dt);
            // A photon has been captured, so we should be oscillating.

            // See if it is time to re-emit the photon.
            photonHoldCountdownTime -= dt;
            if (photonHoldCountdownTime <= 0){
                photonHoldCountdownTime = 0;
                emitPhoton();
                setPhotonAbsorbed( false );
                absorbtionHysteresisCountdownTime = ABSORPTION_HYSTERESIS_TIME;
                strategy.reset();
            }

            breakApartCountdownTime -= dt;
            if (breakApartCountdownTime<=0){
                breakApartCountdownTime = 0;
                breakApart();
            }

            // Update the atom positions.
            updateAtomPositions();
        }

        if (absorbtionHysteresisCountdownTime > 0){
            absorbtionHysteresisCountdownTime -= dt;
        }

        // Do any movement that is required.
        setCenterOfGravityPos( velocity.getDestination( centerOfGravity ) );
        setCenterOfGravityPos( centerOfGravity.getX() + velocity.getX() * dt, centerOfGravity.getY() + velocity.getY() * dt);
    }

    protected void breakApart() {
    }

    /**
     * Reset the molecule.  Any photons that have been absorbed are forgotten,
     * and any oscillation is reset.
     * @return
     */
    public void reset(){
        photonAbsorbed = false;
        photonToEmit = null;
        strategy.reset();
        photonHoldCountdownTime = 0;
        breakApartCountdownTime = 0;
        absorbtionHysteresisCountdownTime = 0;
    }

    protected double getAbsorbtionHysteresisCountdownTime() {
        return absorbtionHysteresisCountdownTime;
    }

    /**
     * Initialize the offsets from the center of gravity for each atom within
     * this molecule.  This should be in the "relaxed" (i.e. non-oscillating)
     * state.
     */
    protected abstract void initializeAtomOffsets();

    public void addListener(Listener listener){
        // Don't bother adding if already there.
        if (!listeners.contains( listener )){
            listeners.add(listener);
        }
    }

    public void removeListener(Listener listener){
        listeners.remove(listener);
    }

    public Point2D getCenterOfGravityPos(){
        return new Point2D.Double(centerOfGravity.getX(), centerOfGravity.getY());
    }

    protected Point2D getCenterOfGravityPosRef(){
        return centerOfGravity;
    }

    /**
     * Add a wavelength to the list of those absorbed by this molecule.
     *
     * @param wavelength
     */
    protected void addPhotonAbsorptionWavelength(double wavelength){
        photonAbsorptionWavelengths.add( wavelength );
    }

    /**
     * Set the location of this molecule by specifying the center of gravity.
     * This will be unique to each molecule's configuration, and it will cause
     * the individual molecules to be located such that the center of gravity
     * is in the specified location.  The relative orientation of the atoms
     * that comprise the molecules will not be changed.
     *
     * @param x the x location to set
     * @param y the y location to set
     */
    public void setCenterOfGravityPos( double x, double y ){
        this.centerOfGravity.setLocation( x, y );
        updateAtomPositions();
    }

    public void setCenterOfGravityPos(Point2D centerOfGravityPos){
        setCenterOfGravityPos( centerOfGravityPos.getX(), centerOfGravityPos.getY() );
    }

    /**
     * Update the formation of the atoms based on the specified location
     * within the oscillation sequence.  Note that this only alters the
     * offsets but does not actually move the atoms, so the method for
     * updating the positions will need to be called in order to get the atoms
     * to actually move.  This is done so that if the atom is moving and is
     * also oscillating we don't end up sending out two position updates per
     * atom per time step.
     *
     * @param oscillationRadians
     */
    protected void updateOscillationFormation(double oscillationRadians){
        return; // Does nothing by default, override for molecules that oscillate.
    }

    protected void markPhotonForPassThrough(Photon photon){
        if (passThroughPhotonList.size() >= PASS_THROUGH_PHOTON_LIST_SIZE){
            // Make room for this photon be deleting the oldest one.
            passThroughPhotonList.remove( 0 );
        }
        passThroughPhotonList.add( photon );
    }

    protected boolean isPhotonMarkedForPassThrough(Photon photon){
        return (passThroughPhotonList.contains( photon ));
    }

    public ArrayList<Atom> getAtoms() {
        return new ArrayList<Atom>(atoms);
    }

    public ArrayList<AtomicBond> getAtomicBonds() {
        return new ArrayList<AtomicBond>(atomicBonds);
    }

    /**
     * Decide whether or not to absorb the offered photon.
     *
     * @param photon - The photon offered for absorption.
     * @return
     */
    public boolean queryAbsorbPhoton( Photon photon ){
        boolean absorbPhoton = false;
        if (!isPhotonAbsorbed() &&
            getAbsorbtionHysteresisCountdownTime() <= 0 &&
            photonAbsorptionWavelengths.contains( photon.getWavelength() ) &&
            photon.getLocation().distance( getCenterOfGravityPos() ) < PHOTON_ABSORPTION_DISTANCE &&
            !isPhotonMarkedForPassThrough( photon ))
        {
            // All circumstances are correct for photon absorption, so now
            // we decide probabalistically whether or not to actually do
            // it.  This essentially simulates the quantum nature of the
            // absorption.

            if (RAND.nextDouble()< SingleMoleculePhotonAbsorptionProbability.getInstance().getAbsorptionsProbability()){
                absorbPhoton = true;
                setPhotonAbsorbed( true );
                startBreakApartTimer( photon );
                startPhotonEmissionTimer( photon );
                rotateClockwise = RAND.nextBoolean();
            }
            else{
                // Do NOT absorb it - mark it for pass through instead.
                absorbPhoton = false;
                markPhotonForPassThrough( photon );
            }
        }
        else{
            absorbPhoton = false;
        }

        return absorbPhoton;
    }

    protected void addAtom( Atom atom ){
        atoms.add( atom );
    }

    protected void addAtomicBond( AtomicBond atomicBond ){
        atomicBonds.add( atomicBond );
    }

    /**
     * This method is used by subclasses to let the base class know that it
     * should start the remission timer.
     *
     * @param photonToEmit
     */
    protected void startPhotonEmissionTimer( Photon photonToEmit ){
        photonHoldCountdownTime = MIN_PHOTON_HOLD_TIME + RAND.nextDouble() * (MAX_PHOTON_HOLD_TIME - MIN_PHOTON_HOLD_TIME);
        this.photonToEmit = photonToEmit;
    }

    protected void startBreakApartTimer( Photon photonToEmit ){
        breakApartCountdownTime = MIN_PHOTON_HOLD_TIME;
    }

    protected void emitPhoton( double wavelength ){
        Photon emittedPhoton = new Photon( wavelength, null );
        double emissionAngle = RAND.nextDouble() * Math.PI * 2;
        emittedPhoton.setVelocity( (float)(PHOTON_EMISSION_SPEED * Math.cos( emissionAngle )),
                (float)(PHOTON_EMISSION_SPEED * Math.sin( emissionAngle )));
        emittedPhoton.setLocation( getCenterOfGravityPosRef() );
        notifyPhotonEmitted( emittedPhoton );
    }

    protected void emitPhoton(){
        double emissionAngle = RAND.nextDouble() * Math.PI * 2;
        photonToEmit.setVelocity( (float)(PHOTON_EMISSION_SPEED * Math.cos( emissionAngle )),
                (float)(PHOTON_EMISSION_SPEED * Math.sin( emissionAngle )));
        photonToEmit.setLocation( getCenterOfGravityPosRef() );
        // Sending the notification will cause the primary model class to add
        // this photon to the model.
        notifyPhotonEmitted( photonToEmit );
    }

    private void notifyPhotonEmitted(Photon photon){
        for (Listener listener : listeners){
            listener.photonEmitted( photon );
        }
    }

    /**
     * Update the positions of all atoms that comprise this molecule based on
     * the current center of gravity and the offset for each atom.
     */
    protected void updateAtomPositions(){
        for (Atom atom : atoms){
            Vector2D offset = atomCogOffsets.get( atom );
            if (offset != null){
                atom.setPosition( centerOfGravity.getX() + offset.getX(), centerOfGravity.getY() + offset.getY() );
            }
            else{
                // This shouldn't happen, and needs to be debugged if it does.
                assert false;
                System.err.println(getClass().getName() + " - Error: No offset found for atom.");
            }
        }
    }

    public void setVelocity( double vx, double vy ) {
        setVelocity( new ImmutableVector2D( vx, vy ) );
    }

    public void setVelocity( ImmutableVector2D newVelocity) {
        this.velocity.setValue( newVelocity );
    }

    public ImmutableVector2D getVelocity() {
        return velocity;
    }

    /**
     * Get an enclosing rectangle for this molecule.  This was created to
     * support searching for open locations for new molecules.
     *
     * @return
     */
    public Rectangle2D getBoundingRect(){
        Rectangle2D [] atomRects = new Rectangle2D[atoms.size()];
        for (int i = 0; i < atoms.size(); i++){
            atomRects[i] = atoms.get( i ).getBoundingRect();
        }

        return RectangleUtils.union( atomRects );
    }

    public ArrayList<Molecule> getBreakApartConstituents() {
        return new ArrayList<Molecule>(  );
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    public interface Listener {
        void photonEmitted(Photon photon);
        void brokeApart(Molecule molecule);
    }

    public static class Adapter implements Listener {
        public void photonEmitted(Photon photon) {}

        public void brokeApart(Molecule molecule) {
        }
    }

    //This strategy determines what happens to the molecule if and when it absorbs a photon
    public static class PhotonAbsorptionReactionStrategy {
        Molecule molecule;

        public PhotonAbsorptionReactionStrategy( Molecule molecule ) {
            this.molecule = molecule;
        }

        public void stepInTime( double dt ) {
        }

        public void reset() {
        }
    }

    public static class VibrationStrategy extends PhotonAbsorptionReactionStrategy {
        // Variable that tracks where in the oscillation sequence this molecule
        // is.  The oscillation sequence is periodic over 0 to 2*pi.
        private double oscillationRadians = 0;

        public VibrationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            super.stepInTime( dt );
            oscillationRadians += dt * OSCILLATION_FREQUENCY / 1000 * 2 * Math.PI;
            if ( oscillationRadians >= 2 * Math.PI ) {
                oscillationRadians -= 2 * Math.PI;
            }
            // Update the offset of the atoms based on the current oscillation
            // index.
            molecule.updateOscillationFormation( oscillationRadians );
        }

        @Override
        public void reset() {
            super.reset();
            oscillationRadians = 0;
        }
    }

    public static class RotationStrategy extends PhotonAbsorptionReactionStrategy {
        // Variable that tracks where in the oscillation sequence this molecule
        // is.  The oscillation sequence is periodic over 0 to 2*pi.
        private double oscillationRadians = 0;

        public RotationStrategy( Molecule molecule ) {
            super( molecule );
        }

        @Override
        public void stepInTime( double dt ) {
            super.stepInTime( dt );
            oscillationRadians += dt * OSCILLATION_FREQUENCY / 1000 * 2 * Math.PI;
            if ( oscillationRadians >= 2 * Math.PI ) {
                oscillationRadians -= 2 * Math.PI;
            }
            // Update the offset of the atoms based on the current oscillation
            // index.
            molecule.updateOscillationFormation( oscillationRadians );
        }

        @Override
        public void reset() {
            super.reset();
            oscillationRadians = 0;
        }
    }

    public static class NullStrategy extends PhotonAbsorptionReactionStrategy {
        public NullStrategy( Molecule molecule ) {
            super( molecule );
        }
    }
}
