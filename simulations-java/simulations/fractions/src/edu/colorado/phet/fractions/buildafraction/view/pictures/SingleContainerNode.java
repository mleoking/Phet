// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.Effect;
import fj.F;
import fj.data.List;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.common.util.FJUtils;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.fractions.buildafraction.view.pictures.PieceNode._toFraction;
import static edu.colorado.phet.fractions.common.view.FNode.getChildren;
import static edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction.sum;

/**
 * @author Sam Reid
 */
public class SingleContainerNode extends PNode {
    public final ContainerNode parent;
    private final PNode dottedLineLayer;

    SingleContainerNode( final ContainerNode parent, final ObservableProperty<Integer> number ) {
        this.parent = parent;
        dottedLineLayer = new PNode() {{
            number.addObserver( new VoidFunction1<Integer>() {
                public void apply( final Integer number ) {
                    removeAllChildren();
                    final double pieceWidth = SimpleContainerNode.width / number;
                    double x = pieceWidth;
                    for ( int i = 0; i < number - 1; i++ ) {
                        addChild( new PhetPPath( new Line2D.Double( x, 0, x, SimpleContainerNode.height ), new BasicStroke( 1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[] { 10, 10 }, 0 ), Color.lightGray ) );

                        //Lines that stick out
//                        addChild( new PhetPPath( new Line2D.Double( x, -5, x, 0 ), new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 ), Color.lightGray ) );
//                        addChild( new PhetPPath( new Line2D.Double( x, SimpleContainerNode.height, x, SimpleContainerNode.height+5 ), new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1 ), Color.lightGray ) );
                        x += pieceWidth;
                    }
                }
            } );
        }};
        SimpleContainerNode node = new SimpleContainerNode( number.get(), Color.white ) {{
            //Thicker outer stroke
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, width, height ), Color.white, new BasicStroke( 2 ), Color.black ) );

            addInputEventListener( new SimSharingDragHandler( null, true ) {
                @Override protected void startDrag( final PInputEvent event ) {
                    super.startDrag( event );
                    parent.moveToFront();
                    addActivity( new AnimateToScale( parent, 200 ) );
                    parent.notifyListeners();
                }

                @Override protected void drag( final PInputEvent event ) {
                    super.drag( event );
                    final PDimension delta = event.getDeltaRelativeTo( getParent() );
                    parent.translate( delta.width, delta.height );
                    parent.notifyListeners();
                }

                @Override protected void endDrag( final PInputEvent event ) {
                    super.endDrag( event );
                    parent.context.endDrag( parent, event );
                    parent.notifyListeners();
                }
            } );
            addInputEventListener( new CursorHandler() );
        }};
        addChild( node );

        addChild( dottedLineLayer );

    }

    public boolean isInToolbox() { return parent.isInToolbox(); }

    //Return true if the piece would overflow this container
    public boolean willOverflow( final PieceNode piece ) {
        final Fraction sum = getFractionValue().plus( piece.toFraction() );
        return sum.numerator > sum.denominator;
    }

    public Fraction getFractionValue() { return sum( getPieces().map( _toFraction ) ); }

    public static F<SingleContainerNode, Fraction> _getFractionValue = new F<SingleContainerNode, Fraction>() {
        @Override public Fraction f( final SingleContainerNode singleContainerNode ) {
            return singleContainerNode.getFractionValue();
        }
    };

    private List<PieceNode> getPieces() {return getChildren( this, PieceNode.class );}

    //How far over should a new piece be added in?
    public double getPiecesWidth() {
        List<PieceNode> children = getPieces();
        return children.length() == 0 ? 0 :
               fj.data.List.iterableList( children ).maximum( FJUtils.ord( new F<PieceNode, Double>() {
                   @Override public Double f( final PieceNode r ) {
                       return r.getFullBounds().getMaxX();
                   }
               } ) ).getFullBounds().getMaxX();
    }

    //Assumes the piece is already in the right spot.
    public void addPiece( final PieceNode piece ) {
        Point2D offset = piece.getGlobalTranslation();
        addChild( piece );
        piece.setGlobalTranslation( offset );
        parent.pieceAdded( piece );
        dottedLineLayer.moveToFront();
        piece.setAllPickable( false );
    }

    //TODO: have cards separate for a minute before animating home?  Maybe unnecessary now that we are showing individual cards in the container.
    public void splitAll() {
        int numPieces = getPieces().length();
        double separationBetweenPieces = 4;
        double totalDeltaSpacing = separationBetweenPieces * ( numPieces - 1 );
//        int index = 0;
//        LinearFunction f = new LinearFunction( 0, numPieces - 1, -totalDeltaSpacing / 2, totalDeltaSpacing / 2 );
        for ( PieceNode child : getPieces() ) {
            parent.parent.splitPieceFromContainer( child );
        }
    }

    public static final Effect<SingleContainerNode> _splitAll = new Effect<SingleContainerNode>() {
        @Override public void e( final SingleContainerNode s ) {
            s.splitAll();
        }
    };
}