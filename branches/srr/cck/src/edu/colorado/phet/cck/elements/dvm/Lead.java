/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.dvm;

import edu.colorado.phet.cck.common.SimpleObservable;

/**
 * User: Sam Reid
 * Date: Oct 26, 2003
 * Time: 1:58:33 AM
 * Copyright (c) Oct 26, 2003 by Sam Reid
 */
public class Lead extends SimpleObservable {
    double x;
    double y;

    public Lead(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void translate(double dx, double dy) {
        x += dx;
        y += dy;
        updateObservers();
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

}
