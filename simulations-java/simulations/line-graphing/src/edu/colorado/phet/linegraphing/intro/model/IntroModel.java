// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;

/**
 * Model for the "Intro" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class IntroModel implements Resettable {

    private static final SlopeInterceptLine DEFAULT_LINE = new SlopeInterceptLine( 5, 5, 1 );

    public final ModelViewTransform mvt;
    public final Property<SlopeInterceptLine> line;
    public final SlopeInterceptLine yEqualsXLine, yEqualsNegativeXLine;
    public final LineGraph graph;

    public IntroModel() {
        mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 28, -28 ); // y is inverted
        line = new Property<SlopeInterceptLine>( DEFAULT_LINE );
        yEqualsXLine = new SlopeInterceptLine( 1, 1, 0 );
        yEqualsNegativeXLine = new SlopeInterceptLine( 1, -1, 0 );
        graph = new LineGraph( -10, 10, -10, 10 );
    }

    public void reset() {
        line.reset();
    }
}
