package edu.colorado.phet.movingman.application.motionsuites;

import edu.colorado.phet.movingman.application.MovingManModule;
import edu.colorado.phet.movingman.elements.Man;
import edu.colorado.phet.movingman.elements.PlotsSetup;
import edu.colorado.phet.movingman.elements.stepmotions.StepMotion;
import edu.colorado.phet.movingman.elements.stepmotions.WalkMotion;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 4, 2003
 * Time: 10:06:07 PM
 */
public class VariablePosition extends MotionSuite {
    private WalkMotion motion;
    private MovingManModule module;
    private PlotsSetup plotsSetup;

    public VariablePosition( final MovingManModule module ) throws IOException {
        super( module, "Position" );
        this.motion = new WalkMotion( module );
        this.module = module;
        this.plotsSetup = new PlotsSetup( 12, 10, 25, 20, 55, 50 );
        module.getPositionGraphic().getSlider().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double x = module.getPositionGraphic().getSlider().getModelValue();
                module.getMan().setX( x * .9 );
                module.setPaused( false );
            }
        } );
    }

    public void initialize( Man man ) {
        plotsSetup.setup( module );
        module.setNumSmoothingPoints( MovingManModule.numSmoothingPoints );
    }

    public void showControls() {
        module.getPositionGraphic().setSliderVisible( true );
        module.getVelocityGraphic().setSliderVisible( false );
        module.getAccelerationGraphic().setSliderVisible( false );
    }

    public void collidedWithWall() {
    }

    public StepMotion getStepMotion() {
        return motion;
    }
}
