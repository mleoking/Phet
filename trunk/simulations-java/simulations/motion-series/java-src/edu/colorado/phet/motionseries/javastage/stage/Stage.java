package edu.colorado.phet.motionseries.javastage.stage;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;

/**
 * This model element of the MVC pattern represents the area in a StageCanvas that scales up and down automatically.  It is characterized by a width and height only.
 * This represents a coordinate frame analogous to what was called the "intermediate" coordinate frame in previous incarnations.
 * <p/>
 * Normally the client code will interact with the Stage through interacting with the StageNode, but this class is left public in case custom behavior is required.
 */
public class Stage extends SimpleObservable {
    private double width;
    private double height;

    public Stage(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        notifyObservers();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
