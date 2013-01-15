// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.model;

import java.awt.Color;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.linegraphing.common.model.Fraction;
import edu.colorado.phet.linegraphing.common.model.Line;

/**
 * Creates game challenges for Level=5, as specified in the design document.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ChallengeFactory5 extends ChallengeFactory {

    public ChallengeFactory5() {
        super();
    }

    /**
     * Creates challenges for this game level.
     *
     * @param xRange range of the graph's x axis
     * @param yRange range of the graph's y axis
     * @return list of challenges
     */
    public ArrayList<Challenge> createChallenges( IntegerRange xRange, IntegerRange yRange ) {

        ArrayList<Challenge> challenges = new ArrayList<Challenge>();

        // for y-intercept manipulation challenges, one must be positive, one negative
        ArrayList<Integer> yIntercepts = rangeToList( new IntegerRange( -10, 10 ) );

        // random choosers
        final RandomChooser<Fraction> fractionChooser = new RandomChooser<Fraction>();
        final RandomChooser<Integer> integerChooser = new RandomChooser<Integer>();
        final RandomChooser<EquationForm> equationFormChooser = new RandomChooser<EquationForm>();

        // MTE, SI, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            Line line = Line.createSlopeIntercept( 0, 1, yIntercept );
            challenges.add( new MTE_Challenge( "slope=0",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // GTL, SI, slope=0
        {
            final int yIntercept = integerChooser.choose( yIntercepts );
            Line line = Line.createSlopeIntercept( 0, 1, yIntercept );
            challenges.add( new GTL_Challenge( "slope=0",
                                               line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
        }

        // GTL, SI or PS, slope-intercept or point-slope
        {
            // randomly choose equation form
            ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
                add( EquationForm.SLOPE_INTERCEPT );
                add( EquationForm.POINT_SLOPE );
            }};
            final EquationForm equationForm = equationFormChooser.choose( equationForms );

            // random points
            final ArrayList<Integer> xList = rangeToList( new IntegerRange( -5, 5 ) );
            final ArrayList<Integer> yList = rangeToList( new IntegerRange( -5, 5 ) );
            final int x1 = ( equationForm == EquationForm.SLOPE_INTERCEPT ) ? 0 : integerChooser.choose( xList );
            final int y1 = integerChooser.choose( yList );
            final int x2 = integerChooser.choose( xList );
            final int y2 = integerChooser.choose( yList );

            // challenge
            final Line line = new Line( x1, y1, x2, y2, Color.BLACK );
            if ( equationForm == EquationForm.SLOPE_INTERCEPT ) {
                challenges.add( new GTL_Challenge( "random choice of slope-intercept, points in [-5,5]",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                challenges.add( new GTL_Challenge( "random choice of point-slope, points in [-5,5]",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // MTE, SI or PS, random slope with exclusions
        {
            // randomly choose equation form
            final ArrayList<EquationForm> equationForms = new ArrayList<EquationForm>() {{
                add( EquationForm.SLOPE_INTERCEPT );
                add( EquationForm.POINT_SLOPE );
            }};
            final EquationForm equationForm = equationFormChooser.choose( equationForms );

            // exclude slopes whose simplified absolute value matches these
            ArrayList<Fraction> excludedSlopes = new ArrayList<Fraction>() {{
                add( new Fraction( 1, 1 ) );
                add( new Fraction( 2, 1 ) );
                add( new Fraction( 1, 2 ) );
                add( new Fraction( 1, 3 ) );
                add( new Fraction( 1, 4 ) );
                add( new Fraction( 2, 3 ) );
            }};

            // choose rise and run such that they don't make an excluded slope.
            final ArrayList<Integer> riseList = rangeToList( yRange );
            final ArrayList<Integer> runList = rangeToList( xRange );
            int rise = integerChooser.choose( riseList );
            final int run = integerChooser.choose( runList );
            boolean excluded = true;
            while ( excluded && riseList.size() > 0 ) {
                excluded = false;
                // is this an excluded slope?
                for ( Fraction slope : excludedSlopes ) {
                    if ( slope.toDecimal() == new Fraction( rise, run ).toDecimal() ) {
                        excluded = true;
                        rise = integerChooser.choose( riseList ); // choose a new rise, and remove it from riseList
                        break;
                    }
                }
            }
            if ( excluded ) {
                rise = 5; // a run that's not in excludedSlopes
            }

            // points
            final int x1 = ( equationForm == EquationForm.SLOPE_INTERCEPT ) ? 0 : integerChooser.choose( rangeToList( yRange ) );
            final int y1 = integerChooser.choose( rangeToList( yRange ) );
            final int x2 = x1 + run;
            final int y2 = y1 + rise;

            // challenge
            final Line line = new Line( x1, y1, x2, y2, Color.BLACK );
            if ( equationForm == EquationForm.SLOPE_INTERCEPT ) {
                challenges.add( new GTL_Challenge( "random choice of slope-intercept, some excluded slopes",
                                                   line, EquationForm.SLOPE_INTERCEPT, ManipulationMode.SLOPE_INTERCEPT, xRange, yRange ) );
            }
            else {
                challenges.add( new GTL_Challenge( "random choice of point-slope, some excluded slopes",
                                                   line, EquationForm.POINT_SLOPE, ManipulationMode.POINT_SLOPE, xRange, yRange ) );
            }
        }

        // shuffle and return
        return shuffle( challenges );
    }
}
