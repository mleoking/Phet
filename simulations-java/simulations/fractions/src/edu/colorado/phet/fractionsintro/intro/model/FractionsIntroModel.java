// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.model.property.integerproperty.IntegerProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

import static edu.colorado.phet.fractionsintro.intro.model.pieset.CircularSliceFactory.CircularSliceFactory;
import static edu.colorado.phet.fractionsintro.intro.model.pieset.HorizontalSliceFactory.HorizontalSliceFactory;
import static edu.colorado.phet.fractionsintro.intro.model.pieset.VerticalSliceFactory.VerticalSliceFactory;

/**
 * Model for the Fractions Intro sim.
 * <p/>
 * Property is a convenient interface for clients, but causes problems when mapping between multiple representations.
 * I should create an isolated case of this, because trivial cases seem like they should work.
 * One solution would be to have the Property.set() methods only be called from the user side, not from the model side.
 * When any of the client interface methods are called, the rest of the state should update to reflect the new state.
 * This means that all handlers in this class should call to the state itself, not to any of the derived properties.
 * <p/>
 * New style:
 * 1. No way to forget to call reset() on some representation instances
 * 2. Interface setters are all focused on "update the entire state to match the given request"
 *
 * @author Sam Reid
 */
public class FractionsIntroModel {
    private final Property<IntroState> state = new Property<IntroState>( new IntroState() );

    //Clock for the model.
    //Animate the model when the clock ticks
    public final Clock clock = new ConstantDtClock() {{
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                final IntroState s = state.get();
                final double dt = clockEvent.getSimulationTimeChange();
                final PieSet newPieSet = s.pieSet.stepInTime( dt );
                final IntroState newState = s.pieSet( newPieSet ).
                        containerSet( newPieSet.toContainerSet() ).
                        horizontalBarSet( s.horizontalBarSet.stepInTime( dt ) ).
                        verticalBarSet( s.verticalBarSet.stepInTime( dt ) );
                state.set( newState );
            }
        } );
    }};

    //Observable parts of the model
    public final SettableProperty<Representation> representation =
            new ClientProperty<Representation>( state, new Function1<IntroState, Representation>() {
                public Representation apply( IntroState s ) {
                    return s.representation;
                }
            },
                                                new Function2<IntroState, Representation, IntroState>() {
                                                    public IntroState apply( IntroState s, Representation r ) {

                                                        //Workaround for a bug: when dragging number line quickly, pie set gets out of sync.  So update it when representations change
                                                        return s.representation( r ).pieSet( CircularSliceFactory.fromContainerSetState( s.containerSet ) );
                                                    }
                                                }
            );

    public final IntegerProperty numerator =
            new IntClientProperty( state, new Function1<IntroState, Integer>() {
                public Integer apply( IntroState s ) {
                    return s.numerator;
                }
            },
                                   new Function2<IntroState, Integer, IntroState>() {
                                       public IntroState apply( IntroState s, Integer numerator ) {
                                           int oldValue = s.numerator;
                                           int delta = numerator - oldValue;
                                           if ( delta > 0 ) {
                                               for ( int i = 0; i < delta; i++ ) {
                                                   final PieSet p = s.pieSet.animateBucketSliceToPie( s.containerSet.getFirstEmptyCell() );
                                                   final PieSet h = s.horizontalBarSet.animateBucketSliceToPie( s.containerSet.getFirstEmptyCell() );
                                                   final PieSet v = s.verticalBarSet.animateBucketSliceToPie( s.containerSet.getFirstEmptyCell() );
                                                   s = s.pieSet( p ).horizontalBarSet( h ).verticalBarSet( v ).containerSet( p.toContainerSet() ).numerator( numerator );
                                               }
                                           }
                                           else if ( delta < 0 ) {
                                               for ( int i = 0; i < Math.abs( delta ); i++ ) {
                                                   final PieSet p = s.pieSet.animateSliceToBucket( s.containerSet.getLastFullCell() );
                                                   final PieSet h = s.horizontalBarSet.animateSliceToBucket( s.containerSet.getLastFullCell() );
                                                   final PieSet v = s.verticalBarSet.animateSliceToBucket( s.containerSet.getLastFullCell() );
                                                   s = s.pieSet( p ).horizontalBarSet( h ).verticalBarSet( v ).containerSet( p.toContainerSet() ).numerator( numerator );
                                               }
                                           }
                                           else {
                                               //Nothing to do if delta == 0
                                           }
                                           return s;
                                       }
                                   }
            ).toIntegerProperty();

    public final IntegerProperty denominator =
            new IntClientProperty( state, new Function1<IntroState, Integer>() {
                public Integer apply( IntroState s ) {
                    return s.denominator;
                }
            },
                                   new Function2<IntroState, Integer, IntroState>() {
                                       public IntroState apply( IntroState s, Integer denominator ) {

                                           //create a new container set
                                           ContainerSet cs = s.containerSet.update( s.maximum, denominator );
                                           return s.pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).containerSet( cs ).denominator( denominator ).
                                                   horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) ).
                                                   verticalBarSet( VerticalSliceFactory.fromContainerSetState( cs ) );
                                       }
                                   }
            ).toIntegerProperty();

    public final SettableProperty<ContainerSet> containerSet = new ClientProperty<ContainerSet>(
            state, new Function1<IntroState, ContainerSet>() {
        public ContainerSet apply( IntroState s ) {
            return s.containerSet;
        }
    },
            new Function2<IntroState, ContainerSet, IntroState>() {
                public IntroState apply( IntroState s, ContainerSet containerSet ) {
                    return s.containerSet( containerSet ).
                            pieSet( CircularSliceFactory.fromContainerSetState( containerSet ) ).
                            numerator( containerSet.numerator ).
                            denominator( containerSet.denominator );
                }
            }
    );

    //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
    public final SettableProperty<PieSet> pieSet = new ClientProperty<PieSet>(
            state, new Function1<IntroState, PieSet>() {
        public PieSet apply( IntroState s ) {
            return s.pieSet;
        }
    },
            new Function2<IntroState, PieSet, IntroState>() {
                public IntroState apply( IntroState s, PieSet pieSet ) {
                    final ContainerSet cs = pieSet.toContainerSet();
                    //Update both the pie set and container state to match the user specified pie set
                    return s.pieSet( pieSet ).containerSet( cs ).numerator( cs.numerator ).horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) );//TODO: should be horizontal
                }
            }
    );

    //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
    public final SettableProperty<PieSet> horizontalBarSet = new ClientProperty<PieSet>(
            state, new Function1<IntroState, PieSet>() {
        public PieSet apply( IntroState s ) {
            return s.horizontalBarSet;
        }
    },
            new Function2<IntroState, PieSet, IntroState>() {
                public IntroState apply( IntroState s, PieSet p ) {
                    final ContainerSet cs = p.toContainerSet();
                    //Update both the pie set and container state to match the user specified pie set
                    return s.horizontalBarSet( p ).containerSet( cs ).numerator( cs.numerator ).pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).
                            verticalBarSet( VerticalSliceFactory.fromContainerSetState( cs ) );
                }
            }
    );

    //When the user drags slices, update the ContainerSet (so it will update the spinner and make it easy to switch representations)
    public final SettableProperty<PieSet> verticalBarSet = new ClientProperty<PieSet>(
            state, new Function1<IntroState, PieSet>() {
        public PieSet apply( IntroState s ) {
            return s.verticalBarSet;
        }
    },
            new Function2<IntroState, PieSet, IntroState>() {
                public IntroState apply( IntroState s, PieSet p ) {
                    final ContainerSet cs = p.toContainerSet();
                    //Update both the pie set and container state to match the user specified pie set
                    return s.verticalBarSet( p ).
                            containerSet( cs ).
                            horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) ).
                            numerator( cs.numerator ).
                            pieSet( CircularSliceFactory.fromContainerSetState( cs ) );
                }
            }
    );
    public IntegerProperty maximum = new IntClientProperty( state, new Function1<IntroState, Integer>() {
        @Override public Integer apply( IntroState s ) {
            return s.maximum;
        }
    }, new Function2<IntroState, Integer, IntroState>() {
        @Override public IntroState apply( IntroState s, Integer maximum ) {
            final ContainerSet cs = s.containerSet.maximum( maximum );
            return s.maximum( maximum ).containerSet( cs ).
                    pieSet( CircularSliceFactory.fromContainerSetState( cs ) ).
                    horizontalBarSet( HorizontalSliceFactory.fromContainerSetState( cs ) ).
                    verticalBarSet( VerticalSliceFactory.fromContainerSetState( cs ) );
        }
    }
    ).toIntegerProperty();

    public void resetAll() {
        state.set( new IntroState() );
    }

    public Clock getClock() {
        return clock;
    }
}