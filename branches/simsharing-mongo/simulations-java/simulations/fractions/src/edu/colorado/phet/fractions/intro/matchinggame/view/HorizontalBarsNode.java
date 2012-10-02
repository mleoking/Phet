// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.intro.intro.model.Fraction;

/**
 * @author Sam Reid
 */
public class HorizontalBarsNode extends RepresentationNode {
    public HorizontalBarsNode( ModelViewTransform transform, final Fraction fraction ) {
        super( transform, fraction );

        double width = 75;
        double height = 75;

        int numFilledSlices = fraction.numerator;
        int numSlices = fraction.denominator;
        double sliceHeight = height / numSlices;
        for ( int i = 0; i < numSlices; i++ ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, i * sliceHeight, width, sliceHeight ), i < numFilledSlices ? Color.orange : Color.white, new BasicStroke( 1 ), Color.black ) );
        }
    }
}