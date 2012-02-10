// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.slicemodel;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;

import java.awt.geom.Area;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.fractionsintro.intro.model.Container;
import edu.colorado.phet.fractionsintro.intro.model.ContainerSetState;

import static fj.Function.curry;
import static fj.Ord.ord;

/**
 * Immutable model representing the entire state at one instant, including the number and location of slices
 *
 * @author Sam Reid
 */
public class PieSetState {
    public final int numerator;
    public final int denominator;
    public final List<Pie> pies;
    public final List<MovableSlice> slices;

    public final List<Slice> emptyCells;

    //The list of all cells
    public final List<Slice> cells;

    public PieSetState() {
        this( 0, 1, createEmptyPies(), createDefaultSlices() );
    }

    private static List<MovableSlice> createDefaultSlices() {

        final int numPies = 6;
        final int denominator = 3;
        final double pieDiameter = 120;
        final double anglePerSlice = 2 * Math.PI / denominator;

        //Slices to put in the pies
        ArrayList<MovableSlice> slices = new ArrayList<MovableSlice>() {{
            for ( int i = 0; i < numPies; i++ ) {
                for ( int k = 0; k < denominator; k++ ) {
                    if ( Math.random() < 0.5 ) {
                        add( new MovableSlice( new Slice( new ImmutableVector2D( 200, 300 ), anglePerSlice * k, anglePerSlice, pieDiameter / 2, false, -1 ), null ) );
                    }
                }
            }
        }};
        return List.iterableList( slices );
    }

    private static List<Pie> createEmptyPies() {

        final int numPies = 6;
        final int denominator = 3;
        final double pieDiameter = 120;
        final double pieSpacing = 10;
        final double anglePerSlice = 2 * Math.PI / denominator;


        //Create some cells for the empty pies
        ArrayList<Pie> pies = new ArrayList<Pie>() {{
            for ( int i = 0; i < numPies; i++ ) {

                ArrayList<Slice> cells = new ArrayList<Slice>();
                for ( int k = 0; k < denominator; k++ ) {
                    cells.add( new Slice( new ImmutableVector2D( pieDiameter * ( i + 1 ) + pieSpacing * ( i + 1 ), pieDiameter ), anglePerSlice * k, anglePerSlice, pieDiameter / 2, false, i ) );
                }

                add( new Pie( List.iterableList( cells ) ) );
            }
        }};

        return List.iterableList( pies );
    }

    public PieSetState( int numerator, int denominator, List<Pie> pies, List<MovableSlice> slices ) {
        this.numerator = numerator;
        this.denominator = denominator;
        this.pies = pies;
        this.slices = slices;

        this.emptyCells = pies.bind( new F<Pie, List<Slice>>() {
            @Override public List<Slice> f( Pie pie ) {
                return getEmptyCells( pie );
            }
        } );
        this.cells = pies.bind( new F<Pie, List<Slice>>() {
            @Override public List<Slice> f( Pie p ) {
                return p.cells;
            }
        } );
    }

    private List<Slice> getEmptyCells( Pie pie ) {
        return pie.cells.filter( new F<Slice, Boolean>() {
            @Override public Boolean f( Slice slice ) {
                return !cellFilled( slice );
            }
        } );
    }

    public PieSetState stepInTime( double simulationTimeChange ) {
        final List<MovableSlice> slices = this.slices.map( new F<MovableSlice, MovableSlice>() {
            public MovableSlice f( final MovableSlice s ) {
                if ( s.dragging ) {

                    //TODO: make this minimum function a bit cleaner please?
                    Slice closest = emptyCells.minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
                        public Ordering f( final Slice u1, final Slice u2 ) {
                            return Ord.<Comparable>comparableOrd().compare( u1.center.distance( s.center ), u2.center.distance( s.center ) );
                        }
                    } ) ) );

                    //Account for winding number
                    double closestAngle = closest.angle;
                    if ( Math.abs( closestAngle - s.angle ) > Math.PI ) {
                        if ( closestAngle > s.angle ) { closestAngle -= 2 * Math.PI; }
                        else if ( closestAngle < s.angle ) { closestAngle += 2 * Math.PI; }
                    }
                    double delta = closestAngle - s.angle;
                    final MovableSlice rotated = s.angle( s.angle + delta / 6 );//Xeno effect

                    //Keep the center in the same place
                    return rotated.translate( s.center.minus( rotated.center ) );
                }
                else {
                    return s;
                }
            }
        } );
        return slices( slices );
    }

    public PieSetState slices( List<MovableSlice> slices ) { return new PieSetState( numerator, denominator, pies, slices ); }

    public boolean cellFilled( final Slice cell ) {
        return slices.exists( new F<MovableSlice, Boolean>() {
            public Boolean f( MovableSlice m ) {
                return m.container == cell;
            }
        } );
    }

    //Find which cell a slice should get dropped into 
    public Slice getDropTarget( final MovableSlice s ) {
        final Slice closestCell = emptyCells.minimum( ord( curry( new F2<Slice, Slice, Ordering>() {
            public Ordering f( final Slice u1, final Slice u2 ) {
                return Ord.<Comparable>comparableOrd().compare( u1.center.distance( s.center ), u2.center.distance( s.center ) );
            }
        } ) ) );

        //Only allow it if the shapes actually overlapped
        return closestCell != null && !( new Area( closestCell.shape ) {{intersect( new Area( s.shape ) );}}.isEmpty() ) ? closestCell : null;
    }

    public ContainerSetState toContainerState() {
        return new ContainerSetState( denominator, new ArrayList<Container>() );
    }
}