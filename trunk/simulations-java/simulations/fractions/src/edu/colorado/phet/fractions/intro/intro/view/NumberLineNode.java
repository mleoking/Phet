// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.ValueEquals;
import edu.colorado.phet.common.phetcommon.util.Pair;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows a number line and a dot on the number line to represent a fraction.
 * The dot is draggable to change the fraction.  The number line is truncated at 6 (the max number for fraction values) so it won't look weird at odd aspect ratios.
 *
 * @author Sam Reid
 */
public class NumberLineNode extends PNode {

    private ArrayList<Pair<Double, Integer>> tickLocations;

    //When tick marks change, clear everything except the green circle--it has to be persisted across recreations of the number line because the user interacts with it
    private final PhetPPath greenCircle;

    public NumberLineNode( final Property<Integer> numerator, final Property<Integer> denominator, ValueEquals<ChosenRepresentation> showing ) {
        scale( 5 );
        showing.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean aBoolean ) {
                setVisible( aBoolean );
            }
        } );

        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();

                //always go the same distance to whole numbers
                final double distanceBetweenTicks = 32;
                int divisionsBetweenTicks = denominator.get();

                double dx = distanceBetweenTicks / divisionsBetweenTicks;

                //The number line itself
                addChild( new PhetPPath( new Line2D.Double( 0, 0, dx * 6 * divisionsBetweenTicks, 0 ) ) );

                //For snapping
                tickLocations = new ArrayList<Pair<Double, Integer>>();

                for ( int i = 0; i <= divisionsBetweenTicks * 6; i++ ) {

                    final int finalI = i;

                    //Major ticks at each integer
                    if ( i % divisionsBetweenTicks == 0 ) {
                        int div = i / divisionsBetweenTicks;
                        final int mod = div % 2;
                        double height = mod == 0 ? 8 : 8;
                        final BasicStroke stroke = mod == 0 ? new BasicStroke( 1 ) : new BasicStroke( 0.5f );
                        final PhetPPath path = new PhetPPath( new Line2D.Double( i * dx, -height, i * dx, height ), stroke, Color.black ) {{
                            addInputEventListener( new CursorHandler() );
                            addInputEventListener( new PBasicInputEventHandler() {
                                @Override public void mousePressed( PInputEvent event ) {
                                    numerator.set( finalI );
                                }
                            } );
                        }};
                        final PhetPPath highlightPath = new PhetPPath( new Line2D.Double( i * dx, -height, i * dx, height ), new BasicStroke( 4 ), Color.yellow );

                        new RichSimpleObserver() {
                            @Override public void update() {
                                final boolean visible = numerator.get().equals( finalI );
                                highlightPath.setVisible( visible );
                                highlightPath.setPickable( visible );
                            }
                        }.observe( numerator, denominator );
                        addChild( highlightPath );
                        addChild( path );
                        if ( mod == 0 || true ) {
                            addChild( new PhetPText( div + "", new PhetFont( 8 ) ) {{
                                setOffset( path.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, path.getFullBounds().getMaxY() );

                                addInputEventListener( new CursorHandler() );
                                addInputEventListener( new PBasicInputEventHandler() {
                                    @Override public void mousePressed( PInputEvent event ) {
                                        numerator.set( finalI );
                                    }
                                } );
                            }} );
                        }

                        //make it so the green handle can snap to this site
                        tickLocations.add( new Pair<Double, Integer>( i * dx, i ) );
                    }

                    //Minor ticks
                    else {

                        final PhetPPath highlightPath = new PhetPPath( new Line2D.Double( i * dx, -4, i * dx, 4 ), new BasicStroke( 4 ), Color.yellow );

                        new RichSimpleObserver() {
                            @Override public void update() {
                                highlightPath.setVisible( numerator.get().equals( finalI ) );
                            }
                        }.observe( numerator, denominator );
                        addChild( highlightPath );

                        //minor ticks between the integers
                        addChild( new PhetPPath( new Line2D.Double( i * dx, -4, i * dx, 4 ), new BasicStroke( 0.25f ), Color.black ) {{
                            addInputEventListener( new CursorHandler() );
                            addInputEventListener( new PBasicInputEventHandler() {
                                @Override public void mousePressed( PInputEvent event ) {
                                    numerator.set( finalI );
                                }
                            } );
                        }} );

                        //make it so the green handle can snap to this site
                        tickLocations.add( new Pair<Double, Integer>( i * dx, i ) );
                    }
                }
                if ( greenCircle != null ) {
                    greenCircle.setOffset( (double) numerator.get() / denominator.get() * distanceBetweenTicks, 0 );
                    addChild( greenCircle );
                }
            }
        }.observe( numerator, denominator );

        //Green circle in the middle of it all
        final double w = 5;
        final double w2 = 0;
        greenCircle = new PhetPPath( new Area( new Ellipse2D.Double( -w / 2, -w / 2, w, w ) ) {{
            subtract( new Area( new Ellipse2D.Double( -w2 / 2, -w2 / 2, w2, w2 ) ) );
        }}, FractionsIntroCanvas.FILL_COLOR, new BasicStroke( 0.6f ), Color.black ) {{

            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseDragged( PInputEvent event ) {
                    final Point2D newPressPoint = event.getPositionRelativeTo( getParent() );

                    //whichever tick mark is closer, go to that one
                    ArrayList<Pair<Double, Integer>> sorted = new ArrayList<Pair<Double, Integer>>( tickLocations );
                    Collections.sort( sorted, new Comparator<Pair<Double, Integer>>() {
                        public int compare( Pair<Double, Integer> o1, Pair<Double, Integer> o2 ) {
                            return Double.compare( Math.abs( o1._1 - newPressPoint.getX() ), Math.abs( o2._1 - newPressPoint.getX() ) );
                        }
                    } );
                    numerator.set( sorted.get( 0 )._2 );
                }
            } );
        }};
        addChild( greenCircle );
    }
}