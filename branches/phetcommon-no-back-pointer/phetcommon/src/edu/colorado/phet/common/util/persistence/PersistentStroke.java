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

import java.awt.*;

/**
 * PersistentStrok<p>
 * Todo: may need to implement hashCode() and equals() differently
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentStroke extends BasicStroke implements Persistent {
    private BasicStroke stroke;

    public PersistentStroke() {
        stroke = new BasicStroke();
    }

    public PersistentStroke( BasicStroke stroke ) {
        this.stroke = stroke;
    }

    /////////////////////////////////////////////
    // Persistence getters and setters
    //
    public StateDescriptor getState() {
        return new StrokeDescriptor( this );
    }

    public void setState( StateDescriptor stateDescriptor ) {
        stateDescriptor.setState( this );
    }

    private void setStroke( BasicStroke stroke ) {
        this.stroke = stroke;
    }

    ////////////////////////////
    // Wrapper methods
    //
    public float getDashPhase() {
        return this.stroke.getDashPhase();
    }

    public float getLineWidth() {
        return this.stroke.getLineWidth();
    }

    public float getMiterLimit() {
        return this.stroke.getMiterLimit();
    }

    public int getEndCap() {
        return this.stroke.getEndCap();
    }

    public int getLineJoin() {
        return this.stroke.getLineJoin();
    }

    public float[] getDashArray() {
        return this.stroke.getDashArray();
    }

    public Shape createStrokedShape( Shape s ) {
        return this.stroke.createStrokedShape( s );
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals( Object obj ) {
        return super.equals( obj );
    }

    //////////////////////////////////////////
    // Inner classes
    //
    public static class StrokeDescriptor implements StateDescriptor {
        private float dashPhase;
        private float lineWidth;
        private float miterLimit;
        private int endCap;
        private int lineJoin;
        private float[] dashArray;

        public StrokeDescriptor() {
        }

        StrokeDescriptor( BasicStroke stroke ) {
            dashPhase = stroke.getDashPhase();
            lineWidth = stroke.getLineWidth();
            miterLimit = stroke.getMiterLimit();
            endCap = stroke.getEndCap();
            lineJoin = stroke.getLineJoin();
            dashArray = stroke.getDashArray();
        }

        ///////////////////////////////////
        // Generator
        //
        public void setState( Persistent persistentObject ) {
            PersistentStroke persistentStroke = (PersistentStroke)persistentObject;
            BasicStroke stroke = new BasicStroke( lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase );
            persistentStroke.setStroke( stroke );
        }

        ////////////////////////////////////
        // Setters and getters
        //
        public float getDashPhase() {
            return dashPhase;
        }

        public void setDashPhase( float dashPhase ) {
            this.dashPhase = dashPhase;
        }

        public float getLineWidth() {
            return lineWidth;
        }

        public void setLineWidth( float lineWidth ) {
            this.lineWidth = lineWidth;
        }

        public float getMiterLimit() {
            return miterLimit;
        }

        public void setMiterLimit( float miterLimit ) {
            this.miterLimit = miterLimit;
        }

        public int getEndCap() {
            return endCap;
        }

        public void setEndCap( int endCap ) {
            this.endCap = endCap;
        }

        public int getLineJoin() {
            return lineJoin;
        }

        public void setLineJoin( int lineJoin ) {
            this.lineJoin = lineJoin;
        }

        public float[] getDashArray() {
            return dashArray;
        }

        public void setDashArray( float[] dashArray ) {
            this.dashArray = dashArray;
        }
    }
}

