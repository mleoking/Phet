// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Single slice for a pie.
 */
public class PieSliceNode extends PNode {
    public PieSliceNode( double startDegrees, double extentDegrees,

                         //The area which the entire pie should take up
                         Rectangle2D area, Paint color, Paint strokePaint, float stroke ) {

        final boolean fullCircle = extentDegrees >= 360 - 1E-6;

        final Arc2D.Double arc = new Arc2D.Double( area.getX(), area.getY(), area.getWidth(), area.getHeight(), startDegrees, extentDegrees, Arc2D.Double.PIE );
        final Ellipse2D.Double ellipse = new Ellipse2D.Double( area.getX(), area.getY(), area.getWidth(), area.getHeight() );
        PhetPPath path = new PhetPPath( fullCircle ? ellipse : arc, color, new BasicStroke( stroke ), strokePaint );
        addChild( path );
    }
}