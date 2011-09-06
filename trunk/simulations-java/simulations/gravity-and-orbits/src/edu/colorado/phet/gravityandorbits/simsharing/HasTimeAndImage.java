// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.gravityandorbits.simsharing;

/**
 * @author Sam Reid
 */
public interface HasTimeAndImage {
    long getTime();

    SerializableBufferedImage getImage();
}
