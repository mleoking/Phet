/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.geom.AffineTransform;

/**
 * PersistentAffineTransform
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentAffineTransform extends AffineTransform implements Persistent {

    public PersistentAffineTransform() {
    }

    public PersistentAffineTransform( AffineTransform atx ) {
        setTransform( atx );
    }

    public PersistentAffineTransform( double m00, double m10, double m01, double m11, double m02, double m12 ) {
        super( m00, m10, m01, m11, m02, m12 );
    }

    public void setTransform( double m00, double m10, double m01, double m11, double m02, double m12 ) {
        super.setTransform( m00, m10, m01, m11, m02, m12 );
    }

    public PersistentAffineTransform( float m00, float m10, float m01, float m11, float m02, float m12 ) {
        super( m00, m10, m01, m11, m02, m12 );
    }

    public PersistentAffineTransform( double[] flatmatrix ) {
        super( flatmatrix );
    }

    public PersistentAffineTransform( float[] flatmatrix ) {
        super( flatmatrix );
    }

    //////////////////////////////////////////
    // Persistence setters and getters
    //
    public StateDescriptor getState() {
        AffineTransformDescriptor descriptor = new AffineTransformDescriptor( this );
        return descriptor;
    }

    public void setState( StateDescriptor stateDescriptor ) {
        stateDescriptor.setState( this );
    }

    //////////////////////////////////////////
    // Inner classes
    //
    public static class AffineTransformDescriptor implements StateDescriptor {
        private double[] coeffs = new double[6];

        public AffineTransformDescriptor() {
        }

        AffineTransformDescriptor( AffineTransform atx ) {
            atx.getMatrix( coeffs );
        }

        /////////////////////////////////////////
        // Generator
        //
        public void setState( Persistent ptx ) {
            ((PersistentAffineTransform)ptx).setTransform( new AffineTransform( coeffs ) );
        }

        /////////////////////////////////////////
        // Persistence setters and getters
        public double[] getCoeffs() {
            return coeffs;
        }

        public void setCoeffs( double[] coeffs ) {
            this.coeffs = coeffs;
        }
    }
}
