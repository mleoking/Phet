// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CobaltChlorideSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CobaltIINitrateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.CopperSulfateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.DrinkMixSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.NickelIIChlorideSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumChromateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumDichromateSolution;
import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawSolution.PotassiumPermanganateSolution;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Model for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawModel implements Resettable {

    private final ArrayList<BeersLawSolution> solutions; // the supported set of solutions
    public final Property<BeersLawSolution> solution; // the selected solution
    public final Light light;
    public final SolidBeam solidBeam;
    public final PhotonBeam photonBeam;
    public final ModelViewTransform mvt;
    public final Cuvette cuvette;
    public final ATDetector detector;
    public final Ruler ruler;
    public final Absorbance absorbance;

    public BeersLawModel( IClock clock ) {

        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getWallTimeChange() / 1000d );
            }
        } );

        // No offset, scale 125x when going from model to view (1cm == 125 pixels)
        this.mvt = ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 125 );

        // Solutions, in rainbow (ROYGBIV) order.
        this.solutions = new ArrayList<BeersLawSolution>() {{
            add( new DrinkMixSolution() );
            add( new CobaltIINitrateSolution() );
            add( new CobaltChlorideSolution() );
            add( new PotassiumDichromateSolution() );
            add( new PotassiumChromateSolution() );
            add( new NickelIIChlorideSolution() );
            add( new CopperSulfateSolution() );
            add( new PotassiumPermanganateSolution() );
        }};
        this.solution = new Property<BeersLawSolution>( solutions.get( 0 ) );

        this.light = new Light( new ImmutableVector2D( 1.5, 2.2 ), false, 0.45, solution );

        this.cuvette = new Cuvette( new ImmutableVector2D( light.location.getX() + 1.5, 1.25 ), 1.0, 2.75, new DoubleRange( 0.5, 2.0 ) );

        //TODO compute drag bounds to match the stage size
        final int rulerWidth = 2; // cm
        this.ruler = new Ruler( rulerWidth, 0.1, 0.35,
                                new ImmutableVector2D( cuvette.location.getX() + ( cuvette.width.get() / 2 ) - ( rulerWidth / 2.0 ), 4.9 ), // centered under cuvette
                                new PBounds( 0, 1, 8, 4.5 ) );

        this.absorbance = new Absorbance( solution, cuvette );

        //TODO compute drag bounds to match the stage size
        this.detector = new ATDetector( new ImmutableVector2D( 6, 3.70 ), new PBounds( 0, 0, 7.9, 5.25 ),
                                        new ImmutableVector2D( 6, light.location.getY() ), new PBounds( 0, 0, 7.9, 5.25 ),
                                        light, cuvette, absorbance );

        this.solidBeam = new SolidBeam( light, cuvette, detector, absorbance, mvt );
        this.photonBeam = new PhotonBeam( light );
    }

    public void reset() {
        for ( BeersLawSolution solution : solutions ) {
            solution.reset();
        }
        solution.reset();
        light.reset();
        cuvette.reset();
        detector.reset();
        ruler.reset();
        photonBeam.reset();
    }

    public ArrayList<BeersLawSolution> getSolutions() {
        return new ArrayList<BeersLawSolution>( solutions );
    }

    private void stepInTime( double deltaSeconds ) {
        photonBeam.stepInTime( deltaSeconds );
    }
}