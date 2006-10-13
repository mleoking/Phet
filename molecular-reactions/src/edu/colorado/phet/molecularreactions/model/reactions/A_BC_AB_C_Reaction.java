/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model.reactions;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.model.collision.MoleculeMoleculeCollisionSpec;
import edu.colorado.phet.molecularreactions.model.collision.HardBodyCollision;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.math.MathUtil;

/**
 * A_AB_BC_C_Reaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class A_BC_AB_C_Reaction extends Reaction {
    private static EnergyProfile energyProfile = new EnergyProfile( MRConfig.DEFAULT_REACTION_THRESHOLD * .1,
                                                                    MRConfig.DEFAULT_REACTION_THRESHOLD,
                                                                    MRConfig.DEFAULT_REACTION_THRESHOLD * .6,
//                                                                    100 );
50 );
    private MRModel model;

    /**
     * Constructor
     *
     * @param model
     */
    public A_BC_AB_C_Reaction( MRModel model ) {
        super( energyProfile, new Criteria( energyProfile ) );
        this.model = model;
    }

    /**
     * Returns the potential energy of the reaction components
     *
     * @param m1
     * @param m2
     * @return
     */
    public double getPotentialEnergy( AbstractMolecule m1, AbstractMolecule m2 ) {
        double pe = 0;
        if( m1 instanceof MoleculeAB || m2 instanceof MoleculeAB ) {
            pe = getEnergyProfile().getRightLevel();
        }
        else if( m1 instanceof MoleculeBC || m2 instanceof MoleculeBC ) {
            pe = getEnergyProfile().getLeftLevel();
        }
        else {
            throw new RuntimeException( "internal error" );
        }
        return pe;
    }

    /**
     * Returns the energy between the floor associated with the composite molecule in
     * a potential reaction, and the reaction's threshold peak
     *
     * @param mA
     * @param mB
     * @return
     */
    public double getThresholdEnergy( AbstractMolecule mA, AbstractMolecule mB ) {
        double thresholdEnergy = 0;
        if( mA instanceof MoleculeA && mB instanceof MoleculeBC ) {
            thresholdEnergy = energyProfile.getPeakLevel() - energyProfile.getLeftLevel();
        }
        else if( mA instanceof MoleculeBC && mB instanceof MoleculeA ) {
            thresholdEnergy = energyProfile.getPeakLevel() - energyProfile.getLeftLevel();
        }
        else if( mA instanceof MoleculeC && mB instanceof MoleculeAB ) {
            thresholdEnergy = energyProfile.getPeakLevel() - energyProfile.getRightLevel();
        }
        else if( mA instanceof MoleculeAB && mB instanceof MoleculeC ) {
            thresholdEnergy = energyProfile.getPeakLevel() - energyProfile.getRightLevel();
        }
        else {
            throw new RuntimeException( "arguments of wrong type" );
        }
        return thresholdEnergy;
    }

    public void doReaction( CompositeMolecule cm, SimpleMolecule sm ) {
        if( cm instanceof MoleculeAB && sm instanceof MoleculeC ) {
            doReaction( (MoleculeAB)cm, (MoleculeC)sm );
        }
        else if( cm instanceof MoleculeBC && sm instanceof MoleculeA ) {
            doReaction( (MoleculeBC)cm, (MoleculeA)sm );
        }
        else {
            throw new RuntimeException( "internal error" );
        }
    }

    private void doReaction( MoleculeAB mAB, MoleculeC mC ) {
        // Delete the old composite molecule and make a new one with the new components
        MoleculeB mB = mAB.getMoleculeB();
        MoleculeA mA = mAB.getMoleculeA();
        MoleculeBC mBC = new MoleculeBC( new SimpleMolecule[]{mB, mC} );
        doReactionII( mAB, mBC, mC, mA );
    }

    private void doReaction( MoleculeBC mBC, MoleculeA mA ) {
        // Delete the old composite molecule and make a new one with the new components
        MoleculeB mB = mBC.getMoleculeB();
        MoleculeC mC = mBC.getMoleculeC();
        MoleculeAB mAB = new MoleculeAB( new SimpleMolecule[]{mB, mA} );
        doReactionII( mBC, mAB, mA, mC );
    }

    /**
     * Removes the old composite molecule from the model, adds the new one, and
     * sets the kinematics for the reaction products using a hard sphere collision
     *
     * @param oldComposite
     * @param newComposite
     * @param newFreeMolecule
     */
    private void doReactionII( AbstractMolecule oldComposite,
                               AbstractMolecule newComposite,
                               AbstractMolecule oldFreeMolecule,
                               AbstractMolecule newFreeMolecule ) {

        model.removeModelElement( oldComposite );
//        model.addModelElement( newComposite );
        newFreeMolecule.setParentComposite( null );

        SimpleMolecule a = (SimpleMolecule)oldFreeMolecule;
        SimpleMolecule b = oldComposite.getComponentMolecules()[0] instanceof MoleculeB
                             ? (SimpleMolecule)oldComposite.getComponentMolecules()[0]
                             : (SimpleMolecule)oldComposite.getComponentMolecules()[1];
        SimpleMolecule c = (SimpleMolecule)newFreeMolecule;

        double vabx = ( b.getMass() * b.getVelocity().getX() + a.getMass() * a.getVelocity().getX() )
                / ( b.getMass() + a.getMass() );
        double vaby = ( b.getMass() * b.getVelocity().getY() + a.getMass() * a.getVelocity().getY() )
                / ( b.getMass() + a.getMass() );
        a.setVelocity( new Vector2D.Double( vabx, vaby ));
        b.setVelocity( new Vector2D.Double( vabx, vaby ));

        SimpleMolecule[] sms = new SimpleMolecule[]{ a, b };
        if( newComposite instanceof MoleculeAB ) {
            newComposite = new MoleculeAB( sms );
        }
        if( newComposite instanceof MoleculeBC ) {
            newComposite = new MoleculeBC( sms );
        }
        model.addModelElement( newComposite );


        // assign velocities to the reaction products
//        setReactionProductVelocities( oldFreeMolecule, oldComposite, newComposite, newFreeMolecule );
//        double vCompX = ( oldFreeMolecule.getMass() * oldFreeMolecule.getVelocity().getX() + oldComposite.getMass() * oldComposite.getVelocity().getX() )
//                        / newComposite.getMass();
//        double vCompY = ( oldFreeMolecule.getMass() * oldFreeMolecule.getVelocity().getY() + oldComposite.getMass() * oldComposite.getVelocity().getY() )
//                        / newComposite.getMass();
//        newComposite.setVelocity( vCompX, vCompY );
//        newFreeMolecule.setVelocity( 0, 0 );

        // Compute the kinematics of the released molecule
        HardBodyCollision collision = new HardBodyCollision();
        collision.detectAndDoCollision( newComposite, newFreeMolecule );


        if( false ) {

            // Add kinetic energy to the molecules equivalent to the difference in potential energy
            // between the peak and the flat
            double floorPE = 0;
            if( newComposite instanceof MoleculeAB ) {
                floorPE = getEnergyProfile().getRightLevel();
            }
            else {
                floorPE = getEnergyProfile().getLeftLevel();
            }

            double dPE = getEnergyProfile().getPeakLevel() - floorPE;
            double KEi = newComposite.getKineticEnergy() + newFreeMolecule.getKineticEnergy();
            double r2 = ( dPE + KEi ) / KEi;
            newComposite.setVelocity( newComposite.getVelocity().scale( r2 ) );
            newFreeMolecule.setVelocity( newFreeMolecule.getVelocity().scale( r2 ) );

            if( true ) {
                return;
            }

//        Vector2D vKE = new Vector2D.Double( oldFreeMolecule.getPosition(), newFreeMolecule.getPosition() ).normalize();
//        double dFreeMoleculeKE = Math.sqrt( 2 * dPE / newFreeMolecule.getFullMass() ) / 2;
//        double dCompositeKE = Math.sqrt( 2 * dPE / newComposite.getFullMass() ) / 2;
//        Vector2D dVFree = new Vector2D.Double( vKE ).scale( dFreeMoleculeKE );
//        Vector2D dVComposite = new Vector2D.Double( vKE ).scale( -dCompositeKE );
//
//        newFreeMolecule.setVelocity( newFreeMolecule.getVelocity().add( dVFree ));
//        newComposite.setVelocity( newComposite.getVelocity().add( dVComposite ));
//
//        if( true) return;

//        double vC0 = newComposite.getSpeed();
//        double vC1 = Math.sqrt( 2 * ( dPE / 2 ) / newComposite.getMass() + vC0 * vC0 );
//        newComposite.setVelocity( newComposite.getVelocity().normalize().scale( -vC1 ));
//        double vF0 = newFreeMolecule.getSpeed();
//        double vF1 = Math.sqrt( 2 * ( dPE / 2 ) / newFreeMolecule.getMass() + vF0 * vF0 );
//        newFreeMolecule.setVelocity( newFreeMolecule.getVelocity().normalize().scale( vF1 ));

//        double vM0i = newFreeMolecule.getVelocity().getMagnitude();
//        double vM0f = Math.sqrt( dPE / newFreeMolecule.getMass() + vM0i * vM0i );
//        newFreeMolecule.setVelocity( newFreeMolecule.getVelocity().normalize().scale( vM0f ) );
//
//        double vM1i = newComposite.getVelocity().getMagnitude();
//        double vM1f = Math.sqrt( dPE / newComposite.getMass() + vM1i * vM1i );
//        newComposite.setVelocity( newComposite.getVelocity().normalize().scale( vM1f ) );

            double KE0 = newFreeMolecule.getKineticEnergy() + newComposite.getKineticEnergy();
            double r = ( 1 + dPE / KE0 ) / 2;

            newFreeMolecule.setVelocity( newFreeMolecule.getVelocity().getX() * r, newFreeMolecule.getVelocity().getY() * r );
            newComposite.setVelocity( newComposite.getVelocity().getX() * r, newComposite.getVelocity().getY() * r );

//        System.out.println( "vM0i = " + vM0i );
//        System.out.println( "vM0f = " + vM0f );
//        System.out.println( "vM1i = " + vM1i );
//        System.out.println( "vM1f = " + vM1f );

        }
    }

    private void setReactionProductVelocities( AbstractMolecule a, AbstractMolecule bc, AbstractMolecule ab, AbstractMolecule c ) {
        // Get initial kinetic energy
        double kei = a.getKineticEnergy() + bc.getKineticEnergy();

        // Get the initial momentum
        Vector2D Pi = new Vector2D.Double( a.getMomentum() ).add( bc.getMomentum() );
        double pix = Pi.getX();
        double piy = Pi.getY();

        double vaix = a.getVelocity().getX();
        double vbcix = bc.getVelocity().getX();
        double vaiy = a.getVelocity().getY();
        double vbciy = bc.getVelocity().getY();

        double ma = a.getMass();
        double mbc = bc.getMass();
        double mab = ab.getMass();
        double mc = c.getMass();

        double qa = mc * mc / mab + mc;
        double qbx = -( 2 * pix * mc / mab );
        double qcx = ( pix * pix / mab ) - 2 * kei;
        double[] rootsX = MathUtil.quadraticRoots( qa, qbx, qcx );
        double qby = -( 2 * piy * mc / mab );
        double qcy = ( piy * piy / mab ) - 2 * kei;
        double[] rootsY = MathUtil.quadraticRoots( qa, qby, qcy );

        Vector2D vcf = new Vector2D.Double( rootsX[1], rootsY[1] );
        c.setVelocity( vcf );

        double vabfx = ( pix - mc * vcf.getX() ) / mab;
        double vabfy = ( piy - mc * vcf.getY() ) / mab;

        Vector2D vabf = new Vector2D.Double( vabfx, vabfy );
        ab.setVelocity( vabf );

    }


    public SimpleMolecule getMoleculeToRemove( CompositeMolecule compositeMolecule, SimpleMolecule moleculeAdded ) {
        SimpleMolecule sm = null;
        if( moleculeAdded instanceof MoleculeA ) {
            if( compositeMolecule.getComponentMolecules()[0] instanceof MoleculeC ) {
                sm = compositeMolecule.getComponentMolecules()[0];
            }
            else if( compositeMolecule.getComponentMolecules()[1] instanceof MoleculeC ) {
                sm = compositeMolecule.getComponentMolecules()[1];
            }
            else {
                throw new RuntimeException( "internal error" );
            }
        }
        if( moleculeAdded instanceof MoleculeC ) {
            if( compositeMolecule.getComponentMolecules()[0] instanceof MoleculeA ) {
                sm = compositeMolecule.getComponentMolecules()[0];
            }
            else if( compositeMolecule.getComponentMolecules()[1] instanceof MoleculeA ) {
                sm = compositeMolecule.getComponentMolecules()[1];
            }
            else {
                throw new RuntimeException( "internal error" );
            }
        }
        return sm;
    }

    public SimpleMolecule getMoleculeToKeep( CompositeMolecule compositeMolecule, SimpleMolecule moleculeAdded ) {
        SimpleMolecule sm = getMoleculeToRemove( compositeMolecule, moleculeAdded );
        SimpleMolecule moleculeToKeep = null;
        if( sm == compositeMolecule.getComponentMolecules()[0] ) {
            moleculeToKeep = compositeMolecule.getComponentMolecules()[1];
        }
        else if( sm == compositeMolecule.getComponentMolecules()[1] ) {
            moleculeToKeep = compositeMolecule.getComponentMolecules()[0];
        }
        else {
            throw new RuntimeException( "internal error" );
        }
        return moleculeToKeep;
    }

    /**
     * If the molecules aren't the proper type for the reaction, returns POSITIVE_INFINITY.
     *
     * @param mA
     * @param mB
     * @return
     */
    public double getCollisionDistance( AbstractMolecule mA, AbstractMolecule mB ) {
        if( moleculesAreProperTypes( mA, mB ) ) {
            return getCollisionVector( mA, mB ).getMagnitude();
        }
        else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public Vector2D getCollisionVector( AbstractMolecule mA, AbstractMolecule mB ) {
        Vector2D v = null;
        if( moleculesAreProperTypes( mA, mB ) ) {

            // One of the molecules must be a composite, and the other a simple one. Get references to them, and
            // get a reference to the B molecule in the composite
            CompositeMolecule cm = mA instanceof CompositeMolecule
                                   ? (CompositeMolecule)mA
                                   : (CompositeMolecule)mB;
            SimpleMolecule sm = mB instanceof CompositeMolecule
                                ? (SimpleMolecule)mA
                                : (SimpleMolecule)mB;
            SimpleMolecule bm = cm.getComponentMolecules()[0] instanceof MoleculeB ?
                                cm.getComponentMolecules()[0] :
                                cm.getComponentMolecules()[1];

            double pcx = bm.getPosition().getX() - bm.getRadius() * MathUtil.getSign( bm.getPosition().getX() - sm.getCM().getX() );
            double psx = sm.getPosition().getX() - sm.getRadius() * MathUtil.getSign( sm.getPosition().getX() - bm.getPosition().getX() );
            double pcy = bm.getPosition().getY() - bm.getRadius() * MathUtil.getSign( bm.getPosition().getY() - sm.getCM().getY() );
            double psy = sm.getPosition().getY() - sm.getRadius() * MathUtil.getSign( sm.getPosition().getY() - bm.getPosition().getY() );

            int sign = ( mA == cm ) ? -1 : 1;
            v = new Vector2D.Double( sign * ( pcx - psx ), sign * ( pcy - psy ) );
        }
        return v;
    }

//    public void setCollisionDistance( Molecule mA, Molecule mB ) {
//        if( moleculesAreProperTypes( mA, mB )) {
//
//            // One of the molecules must be a composite, and the other a simple one. Get references to them, and
//            // get a reference to the B molecule in the composite
//            CompositeMolecule cm = mA instanceof CompositeMolecule
//                                   ? (CompositeMolecule)mA
//                                   : (CompositeMolecule)mB;
//            SimpleMolecule sm = mB instanceof CompositeMolecule
//                                ? (SimpleMolecule)mA
//                                : (SimpleMolecule)mB;
//            SimpleMolecule bm = cm.getComponentMolecules()[0] instanceof MoleculeB ?
//                                cm.getComponentMolecules()[0] :
//                                cm.getComponentMolecules()[1];
//
//            double pcx = bm.getPosition().getX() - bm.getRadius() * MathUtil.getSign( bm.getPosition().getX() - sm.getCM().getX() );
//            double psx = sm.getPosition().getX() - sm.getRadius() * MathUtil.getSign( sm.getPosition().getX() - bm.getPosition().getX() );
//            double pcy = bm.getPosition().getY() - bm.getRadius() * MathUtil.getSign( bm.getPosition().getY() - sm.getCM().getY() );
//            double psy = sm.getPosition().getY() - sm.getRadius() * MathUtil.getSign( sm.getPosition().getY() - bm.getPosition().getY() );
//            Vector2D v = new Vector2D.Double( pcx - psx, pcy - psy );
//            result = v.getMagnitude();
//        }
//
//    }

    /**
     * The ReactionCriteria for this Reaction class
     */
    private static class Criteria implements Reaction.ReactionCriteria {

        private EnergyProfile energyProfile;

        public Criteria( EnergyProfile energyProfile ) {
            this.energyProfile = energyProfile;
        }

        public boolean criteriaMet( AbstractMolecule m1, AbstractMolecule m2, MoleculeMoleculeCollisionSpec collisionSpec, double thresholdEnergy ) {
            boolean result = false;

            // The simple molecule must have collided with the B simple
            // molecule in the composite molecule
            boolean classificationCriterionMet = false;
            if( moleculesAreProperTypes( m1, m2 )
                && ( ( collisionSpec.getSimpleMoleculeA() instanceof MoleculeA
                       && collisionSpec.getSimpleMoleculeB() instanceof MoleculeB )
                     || ( ( collisionSpec.getSimpleMoleculeA() instanceof MoleculeB
                            && collisionSpec.getSimpleMoleculeB() instanceof MoleculeA ) ) ) ) {
                classificationCriterionMet = true;
            }
            else if( moleculesAreProperTypes( m1, m2 )
                     && ( ( collisionSpec.getSimpleMoleculeA() instanceof MoleculeC
                            && collisionSpec.getSimpleMoleculeB() instanceof MoleculeB )
                          || ( ( collisionSpec.getSimpleMoleculeA() instanceof MoleculeB
                                 && collisionSpec.getSimpleMoleculeB() instanceof MoleculeC ) ) ) ) {
                classificationCriterionMet = true;
            }

            return classificationCriterionMet;

            // The relative kinetic energy of the collision must be above the
            // energy profile threshold
//            if( classificationCriterionMet ) {
//                CompositeMolecule cm = m1 instanceof CompositeMolecule
//                                       ? (CompositeMolecule)m1
//                                       : (CompositeMolecule)m2;
//                double de = 0;
//                if( cm instanceof MoleculeBC ) {
//                    de = energyProfile.getPeakLevel() - energyProfile.getLeftLevel();
//                }
//                else if( cm instanceof MoleculeAB ) {
//                    de = energyProfile.getPeakLevel() - energyProfile.getRightLevel();
//                }
//                else {
//                    throw new IllegalArgumentException( "internal error " );
//                }
//                result = getRelKE( m1, m2 ) > de;
//            }
//            return result;
        }

        /**
         * Determines if one of the molecules is simple and the other composite, and
         * if the simple one is of the correct class to react with the composite
         *
         * @param m1
         * @param m2
         * @return true if the molecules are the proper type for the reaction
         */
        public boolean moleculesAreProperTypes( AbstractMolecule m1, AbstractMolecule m2 ) {

            // We need to have one simple molecule and one composite molecule
            boolean firstClassificationCriterionMet = false;
            CompositeMolecule cm = null;
            SimpleMolecule sm = null;
            if( m1 instanceof CompositeMolecule ) {
                cm = (CompositeMolecule)m1;
                if( m2 instanceof SimpleMolecule ) {
                    sm = (SimpleMolecule)m2;
                    firstClassificationCriterionMet = true;
                }
            }
            else {
                sm = (SimpleMolecule)m1;
                if( m2 instanceof CompositeMolecule ) {
                    cm = (CompositeMolecule)m2;
                    firstClassificationCriterionMet = true;
                }
            }

            // The simple molecule must be of a type not contained in the
            // composite molecule
            boolean secondClassificationCriterionMet = false;
            if( firstClassificationCriterionMet ) {
                if( cm instanceof MoleculeAB
                    && sm instanceof MoleculeC ) {
                    secondClassificationCriterionMet = true;
                }
                else if( cm instanceof MoleculeBC
                         && sm instanceof MoleculeA ) {
                    secondClassificationCriterionMet = true;
                }
            }

            return secondClassificationCriterionMet;
        }

        private static double getRelKE( AbstractMolecule m1, AbstractMolecule m2 ) {
            // Determine the kinetic energy in the collision. We consider this to be the
            // kinetic energy of an object whose mass is equal to the total masses of
            // the two molecules, moving at a speed equal to the magnitude of the
            // relative velocity of the two molecules
            Vector2D loa = new Vector2D.Double( m2.getPosition().getX() - m1.getPosition().getX(),
                                                m2.getPosition().getY() - m1.getPosition().getY() ).normalize();
            double sRel = Math.max( m1.getVelocity().dot( loa ) - m2.getVelocity().dot( loa ), 0 );
            double ke = 0.5 * ( m1.getMass() + m2.getMass() ) * sRel * sRel;
            return ke;
        }
    }
}
