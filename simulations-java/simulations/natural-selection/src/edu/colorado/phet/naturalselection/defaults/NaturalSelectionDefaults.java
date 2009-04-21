package edu.colorado.phet.naturalselection.defaults;

import java.awt.*;
import java.awt.geom.Point2D;

public class NaturalSelectionDefaults {
    public static final Dimension chartSize = new Dimension( 800, 600 );

    /* Not intended for instantiation */
    private NaturalSelectionDefaults() {
    }

    // Clock
    public static final boolean CLOCK_RUNNING = GlobalDefaults.CLOCK_RUNNING;
    public static final int CLOCK_FRAME_RATE = GlobalDefaults.CLOCK_FRAME_RATE;
    public static final double CLOCK_DT = GlobalDefaults.CLOCK_DT;
    public static final int CLOCK_TIME_COLUMNS = GlobalDefaults.CLOCK_TIME_COLUMNS;

    // Model-view transform
    public static final Dimension VIEW_SIZE = new Dimension( 1014, 366 );

    // ExampleModelElement
    public static final double EXAMPLE_MODEL_ELEMENT_WIDTH = 200; // meters
    public static final double EXAMPLE_MODEL_ELEMENT_HEIGHT = 100; // meters
    public static final Point2D EXAMPLE_MODEL_ELEMENT_POSITION = new Point2D.Double( 400, 400 );
    public static final double EXAMPLE_MODEL_ELEMENT_ORIENTATION = 0; // radians

}