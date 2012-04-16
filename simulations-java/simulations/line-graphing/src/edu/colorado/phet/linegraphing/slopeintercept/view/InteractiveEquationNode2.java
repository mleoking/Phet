// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptLine;
import edu.colorado.phet.linegraphing.slopeintercept.view.NumberPickerNode.InterceptPickerNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.NumberPickerNode.SlopePickerNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Interface for manipulating a source-intercept equation.
 * This version uses Ariel Paul's "number picker" user-interface component instead of spinner buttons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class InteractiveEquationNode2 extends PhetPNode {

    private static final NumberFormat FORMAT = new DefaultDecimalFormat( "0" );

    private final Property<Double> rise, run, intercept;

    public InteractiveEquationNode2( final Property<SlopeInterceptLine> interactiveLine,
                                     Property<DoubleRange> riseRange,
                                     Property<DoubleRange> runRange,
                                     Property<DoubleRange> interceptRange,
                                     PhetFont font ) {

        this.rise = new Property<Double>( interactiveLine.get().rise );
        this.run = new Property<Double>( interactiveLine.get().run );
        this.intercept = new Property<Double>( interactiveLine.get().intercept );

        // determine the max width of the rise and run spinners, based on the extents of their range
        double maxSlopeWidth;
        {
            PNode maxRiseNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMax() ), riseRange, font, FORMAT );
            PNode minRiseNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( riseRange.get().getMin() ), riseRange, font, FORMAT );
            double maxRiseWidth = Math.max( maxRiseNode.getFullBoundsReference().getWidth(), minRiseNode.getFullBoundsReference().getWidth() );
            PNode maxRunNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMax() ), runRange, font, FORMAT );
            PNode minRunNode = new SlopePickerNode( UserComponents.riseSpinner, new Property<Double>( runRange.get().getMin() ), runRange, font, FORMAT );
            double maxRunWidth = Math.max( maxRunNode.getFullBoundsReference().getWidth(), minRunNode.getFullBoundsReference().getWidth() );
            maxSlopeWidth = Math.max( maxRiseWidth, maxRunWidth );
        }

        // y = mx + b
        PText yNode = new PhetPText( "y", font );
        PText equalsNode = new PhetPText( "=", font );
        PNode riseNode = new ZeroOffsetNode( new SlopePickerNode( UserComponents.riseSpinner, this.rise, riseRange, font, FORMAT ) );
        PNode runNode = new ZeroOffsetNode( new SlopePickerNode( UserComponents.runSpinner, this.run, runRange, font, FORMAT ) );
        final PPath lineNode = new PPath( new Line2D.Double( 0, 0, maxSlopeWidth, 0 ) ) {{
            setStroke( new BasicStroke( 2f ) );
        }};
        PText xNode = new PhetPText( "x", font );
        final PText interceptSignNode = new PhetPText( "+", font );
        PNode interceptNode = new ZeroOffsetNode( new InterceptPickerNode( UserComponents.interceptSpinner, this.intercept, interceptRange, font, FORMAT ) );

        // rendering order
        {
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            addChild( interceptSignNode );
            addChild( interceptNode );
        }

        // layout
        {
            // NOTE: x spacing varies and was tweaked to look good
            final double ySpacing = 6;
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + 10,
                                  yNode.getYOffset() );
            lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + 10,
                                equalsNode.getFullBoundsReference().getCenterY() );
            riseNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - riseNode.getFullBoundsReference().getWidth(),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getMaxX() - runNode.getFullBoundsReference().getWidth(),
                               lineNode.getFullBoundsReference().getMinY() + ySpacing );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + 15,
                             yNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + 10,
                                         xNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + 2,
                                     interceptSignNode.getFullBoundsReference().getCenterY() - ( interceptNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // sync the model with the controls
        RichSimpleObserver lineUpdater = new RichSimpleObserver() {
            @Override public void update() {
                interactiveLine.set( new SlopeInterceptLine( rise.get(), run.get(), intercept.get(), LGColors.INTERACTIVE_LINE ) );
                interceptSignNode.setText( intercept.get() >= 0 ? "+" : "-" );
            }
        };
        lineUpdater.observe( rise, run, intercept );

        // sync the controls with the model
        interactiveLine.addObserver( new VoidFunction1<SlopeInterceptLine>() {
            public void apply( SlopeInterceptLine line ) {
                rise.set( line.rise );
                run.set( line.run );
                intercept.set( line.intercept );
            }
        } );
    }
}
