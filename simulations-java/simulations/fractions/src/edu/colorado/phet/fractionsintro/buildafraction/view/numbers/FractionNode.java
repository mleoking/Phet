package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Node for the fraction that starts empty and gets numbers added to numerator and denominator, and is moved to the scoring cells.
 *
 * @author Sam Reid
 */
public class FractionNode extends PNode {

    public final PhetPPath topBox;
    public final PhetPPath bottomBox;
    public final PhetPPath divisorLine;
    public final PImage splitButton;
    private NumberCardNode topTarget;
    private NumberCardNode bottomTarget;
    private ArrayList<VoidFunction1<Option<Fraction>>> splitListeners = new ArrayList<VoidFunction1<Option<Fraction>>>();
    public double toolboxPositionX;
    public double toolboxPositionY;

    public FractionNode( final FractionDraggingContext context ) {
        topBox = box( true );
        bottomBox = box( true );
        divisorLine = new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black );

        splitButton = new PImage( Images.SPLIT_BLUE );
        final VBox box = new VBox( topBox, divisorLine, bottomBox );

        //Show a background behind it to make the entire shape draggable
        addChild( new RichPNode( new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), BuildAFractionCanvas.TRANSPARENT ), box ) );

        Rectangle2D bounds = divisorLine.getFullBounds();
        bounds = box.localToParent( bounds );
        splitButton.setOffset( bounds.getMaxX() + 2, bounds.getCenterY() - splitButton.getFullBounds().getHeight() / 2 );
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                Option<Fraction> value = isComplete() ? Option.some( getValue() ) : Option.<Fraction>none();
                //TODO simsharing message
                if ( topTarget != null ) {
                    topTarget.animateHome();
                    topTarget.setCardShapeVisible( true );
                    topTarget.setAllPickable( true );
                    topTarget = null;
                    topBox.setVisible( true );
                }
                if ( bottomTarget != null ) {
                    bottomTarget.animateHome();
                    bottomTarget.setCardShapeVisible( true );
                    bottomTarget.setAllPickable( true );
                    bottomTarget = null;
                    bottomBox.setVisible( true );
                }
                splitButton.setVisible( false );
                for ( VoidFunction1<Option<Fraction>> splitListener : splitListeners ) {
                    splitListener.apply( value );
                }
            }
        } );
        splitButton.setVisible( false );
        addChild( splitButton );

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                translateAll( event.getDeltaRelativeTo( event.getPickedNode().getParent() ) );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( FractionNode.this, event );
            }
        } );
    }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : BuildAFractionCanvas.TRANSPARENT );
    }

    public void attachNumber( final PhetPPath box, final NumberCardNode numberCardNode ) {
        if ( box == topBox ) {
            topTarget = numberCardNode;
        }
        else if ( box == bottomBox ) {
            bottomTarget = numberCardNode;
        }
        else {
            throw new RuntimeException( "No such box!" );
        }

        //Move number node to our coordinate frame so it will translate, scale, animate and render with this node
        final NumberNode numberNode = numberCardNode.numberNode;
        Point2D location = numberNode.getGlobalTranslation();
        location = globalToLocal( location );

        numberNode.removeFromParent();
        addChild( numberNode );

        numberNode.setOffset( location );
        numberCardNode.setCardShapeVisible( false );
    }

    public boolean isComplete() { return topTarget != null && bottomTarget != null; }

    public Fraction getValue() { return new Fraction( topTarget.number, bottomTarget.number ); }

    public PNode getTopNumber() {return topTarget;}

    public PNode getBottomNumber() {return bottomTarget;}

    public void animateAllToPosition( double x, double y, long time ) {
        animateToPositionScaleRotation( x, y, 1.0, 0, time );
        if ( topTarget != null ) {
            double dx = topTarget.getXOffset() - getXOffset();
            double dy = topTarget.getYOffset() - getYOffset();
            topTarget.animateToPositionScaleRotation( x + dx, y + dy, 1.0, 0, time );
        }
        if ( bottomTarget != null ) {
            double dx = bottomTarget.getXOffset() - getXOffset();
            double dy = bottomTarget.getYOffset() - getYOffset();
            bottomTarget.animateToPositionScaleRotation( x + dx, y + dy, 1.0, 0, time );
        }
    }

    public void translateAll( final PDimension delta ) {
        translate( delta.getWidth(), delta.getHeight() );
        if ( topTarget != null ) { topTarget.translate( delta.getWidth(), delta.getHeight() ); }
        if ( bottomTarget != null ) { bottomTarget.translate( delta.getWidth(), delta.getHeight() ); }
    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
        if ( topTarget != null ) { topTarget.setAllPickable( b );}
        if ( bottomTarget != null ) { bottomTarget.setAllPickable( b );}
    }

    public void addSplitListener( final VoidFunction1<Option<Fraction>> listener ) { splitListeners.add( listener ); }

    public boolean isInToolboxPosition() { return getXOffset() == toolboxPositionX && getYOffset() == toolboxPositionY; }

    public void setToolboxPosition( final double x, final double y ) {
        this.toolboxPositionX = x;
        this.toolboxPositionY = y;
    }

    public double getToolboxPositionX() { return toolboxPositionX; }

    public double getToolboxPositionY() { return toolboxPositionY; }
}