// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

/**
 * Manipulation modes, for use in configuring Game challenges.
 * These indicate which properties of a line the user is able to change.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public enum ManipulationMode {
    SLOPE,
    INTERCEPT,
    SLOPE_INTERCEPT,
    POINT,
    POINT_SLOPE,
    TWO_POINTS, /* 2 points, (x1,y1) and (x2,y2) */
    THREE_POINTS /* 3 arbitrary points that may or may not form a line */
}
