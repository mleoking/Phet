// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractions.intro.intro.model.CellPointer;
import edu.colorado.phet.fractions.intro.intro.model.ContainerState;

/**
 * Shows the fraction as a set of bars that lie horizontally, taking up two rows if more than 3
 *
 * @author Sam Reid
 */
public class HorizontalBarSetFractionNode extends ChosenRepresentationNode {
    private static final double NUM_BARS_PER_LINE = 3;
    private static final double DISTANCE_BETWEEN_BARS_Y = 20;

    public HorizontalBarSetFractionNode( Property<ChosenRepresentation> chosenRepresentation, final Property<ContainerState> containerState ) {
        super( chosenRepresentation, ChosenRepresentation.HORIZONTAL_BAR );

        new RichSimpleObserver() {
            public void update() {
                removeAllChildren();

                final int distanceBetweenBars = 55;

                //Find how much space we can use for 3 bars horizontally
                double spaceForBars = FractionsIntroCanvas.WIDTH_FOR_REPRESENTATION - distanceBetweenBars * 2;
                double totalBarWidth = spaceForBars / NUM_BARS_PER_LINE;
                final int denominator = containerState.get().denominator;
                final double cellWidth = totalBarWidth / denominator;

                double x = 0;
                double y = 0;
                for ( int i = 0; i < containerState.get().numContainers; i++ ) {
                    boolean containerEmpty = containerState.get().getContainer( i ).isEmpty();
                    double barHeight = 50;
                    for ( int k = 0; k < denominator; k++ ) {
                        final CellPointer cp = new CellPointer( i, k );
                        boolean filled = containerState.get().isFilled( cp );
                        Color color = filled ? FractionsIntroCanvas.FILL_COLOR : Color.white;
                        Color strokeColor = containerEmpty ? Color.lightGray : Color.black;
                        Stroke stroke = containerEmpty ? new BasicStroke( 1 ) : new BasicStroke( 2 );
                        addChild( new PhetPPath( new Rectangle2D.Double( x, y, cellWidth, barHeight ), color, stroke, strokeColor ) {{

                            //When clicking, toggle the slice
                            PieSetFractionNode.addListener( this, containerState, cp );
                        }} );
                        x = x + cellWidth;
                    }

                    //go to next bar
                    x = x + distanceBetweenBars;

                    //Go to next row
                    if ( i == 2 ) {
                        y = y + barHeight + DISTANCE_BETWEEN_BARS_Y;
                        x = 0;
                    }
                }
            }
        }.observe( containerState );
    }
}