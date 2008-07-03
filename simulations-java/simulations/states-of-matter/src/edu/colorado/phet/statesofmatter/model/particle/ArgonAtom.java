/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.model.particle;

/**
 * The class represents a single atom of neon in the model.
 *
 * @author John Blanco
 */
public class ArgonAtom extends StatesOfMatterAtom {

    public static final double RADIUS = 188;
    private static final double MASS = 1.0; // TODO: JPB TBD - This is not currently used, clean up eventually.
    
    public ArgonAtom(double xPos, double yPos){
        super(xPos, yPos, RADIUS, MASS);
    }
}
