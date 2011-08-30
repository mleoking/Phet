// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.geneexpressionbasics.common.model.BiomoleculeShapeUtils;
import edu.colorado.phet.geneexpressionbasics.common.model.IdleState;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Class that represents messenger ribonucleic acid, or mRNA, in the model.
 *
 * @author John Blanco
 */
public class MessengerRna extends MobileBiomolecule {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Color used by this molecule.  Since mRNA is depicted as a line and not
    // as a closed shape, a transparent color is used.  This enables reuse of
    // generic biomolecule classes.
    private static final Color NOMINAL_COLOR = new Color( 0, 0, 0, 0 );

    // Minimum distance between points that define the shape.  This is done so
    // that this doesn't end up defined by so many points that the shape is
    // strange looking.
    private static final double MIN_DISTANCE_BETWEEN_POINTS = 50;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final List<Point2D> shapeDefiningPoints = new ArrayList<Point2D>();

    // This vector controls the way in which the mRNA drifts as it grows.  Each
    // time growth occurs, the scaler that represents the amount of growth is
    // multiplied by this vector, and the vector is then applied to the
    // positions of all of the shape-defining points.  At the time of this
    // writing, there is no need to have anything other than one way of
    // drifting while growing, but this could easily be made settable or into
    // a constructor param.
    private ImmutableVector2D driftWhileGrowingVector = new Vector2D( 0.2, 0.5 );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /**
     * Constructor.  This creates the mRNA as a single point, with the intention
     * of growing it.
     *
     * @param position
     */
    public MessengerRna( GeneExpressionModel model, Point2D position ) {
        super( model, new DoubleGeneralPath( position ).getGeneralPath(), NOMINAL_COLOR );
        setPosition( position );
        // Explicitly set the state to idle, so that this won't move (other
        // than growing) until it is released.
        behaviorState = new IdleState( this );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /**
     * Grow the mRNA to the provided location, which essentially means a length
     * is being added.  This is usually done in small amounts, and is likely to
     * look weird if an attempt is made to grow to a distant point.
     */
    public void growTo( Point2D p ) {
        if ( shapeDefiningPoints.size() > 0 ) {
            Point2D lastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 1 );
            double growthAmount = lastPoint.distance( p );
            // Cause all existing points to "drift".
            ImmutableVector2D driftVector = driftWhileGrowingVector.getScaledInstance( growthAmount );
            for ( Point2D point : shapeDefiningPoints ) {
                point.setLocation( point.getX() + driftVector.getX(), point.getY() + driftVector.getY() );
            }
            if ( shapeDefiningPoints.size() >= 2 ) {
                // If the current last point is less than the min distance from
                // the 2nd to last point, replace the current last point.  This
                // prevents having zillions of shape-defining points, which is
                // harder to work with.
                Point2D secondToLastPoint = shapeDefiningPoints.get( shapeDefiningPoints.size() - 2 );
                if ( lastPoint.distance( secondToLastPoint ) < MIN_DISTANCE_BETWEEN_POINTS ) {
                    shapeDefiningPoints.remove( lastPoint );
                }
            }
        }
        // Add the new point.
        shapeDefiningPoints.add( p );
        // Update the shape to reflect the newly added point.
        shapeProperty.set( BiomoleculeShapeUtils.createCurvyLineFromPoints( shapeDefiningPoints ) );
    }

    public void growTo( double x, double y ) {
        growTo( new Point2D.Double( x, y ) );
    }
}
