// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.BCEImages;
import edu.colorado.phet.balancingchemicalequations.BCESymbols;

/**
 * Base class for atoms.
 * Inner classes for each specific atom.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Atom {

    private final String symbol;
    private final Image image;

    public Atom( String symbol, Image image ) {
        this.symbol = symbol;
        this.image = image;
    }

    public String getSymbol() {
        return symbol;
    }

    public Image getImage() {
        return image;
    }

    public static class C extends Atom {
        public C() {
           super( BCESymbols.C, BCEImages.C );
        }
    }

    public static class H extends Atom {
        public H() {
           super( BCESymbols.H, BCEImages.H );
        }
    }

    public static class N extends Atom {
        public N() {
           super( BCESymbols.N, BCEImages.N );
        }
    }

    public static class O extends Atom {
        public O() {
           super( BCESymbols.O, BCEImages.O );
        }
    }

    public static class S extends Atom {
        public S() {
           super( BCESymbols.S, BCEImages.S );
        }
    }
}
