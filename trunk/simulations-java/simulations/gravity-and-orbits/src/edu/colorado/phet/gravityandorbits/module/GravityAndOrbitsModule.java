/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.BodyRenderer;
import edu.colorado.phet.gravityandorbits.view.Scale;
import edu.colorado.phet.gravityandorbits.view.VectorNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    public static final double G = 6.67428E-11;

    private Property<Boolean> showGravityForceProperty = new Property<Boolean>( false );
    private Property<Boolean> showPathProperty = new Property<Boolean>( false );
    private Property<Boolean> showVelocityProperty = new Property<Boolean>( false );
    private Property<Boolean> showMassProperty = new Property<Boolean>( false );
    private Property<Boolean> clockPausedProperty = new Property<Boolean>( true );

    private Property<Scale> scaleProperty = new Property<Scale>( Scale.CARTOON );

    private static final double SUN_RADIUS = 6.955E8;
    private static final double SUN_MASS = 1.989E30;

    private static final double EARTH_RADIUS = 6.371E6;
    public static final double EARTH_MASS = 5.9736E24;
    private static final double EARTH_PERIHELION = 147098290E3;
    private static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = 30300;

    private static final double MOON_MASS = 7.3477E22;
    private static final double MOON_RADIUS = 1737.1E3;
    private static final double MOON_EARTH_SPEED = 1.01E3;
    private static final double MOON_SPEED = MOON_EARTH_SPEED;
    private static final double MOON_PERIGEE = 391370E3;
    private static final double MOON_X = EARTH_PERIHELION;
    private static final double MOON_Y = MOON_PERIGEE;

    //see http://en.wikipedia.org/wiki/International_Space_Station
    private static final double SPACE_STATION_RADIUS = 109;
    private static final double SPACE_STATION_MASS = 369914;
    private static final double SPACE_STATION_SPEED = 7706;
    private static final double SPACE_STATION_PERIGEE = 347000;

    private static final Function1<Double, String> days = new Function1<Double, String>() {
        public String apply( Double time ) {
            return (int) ( time / GravityAndOrbitsDefaults.SECONDS_PER_DAY ) + " Earth Days";
        }
    };
    private static final Function1<Double, String> minutes = new Function1<Double, String>() {
        public String apply( Double time ) {
            return (int) ( time / GravityAndOrbitsDefaults.SECONDS_PER_MINUTE ) + " Earth Minutes";
        }
    };
    private final int SEC_PER_YEAR = 365 * 24 * 60 * 60;

    public static Function2<Body, Double, BodyRenderer> getImageRenderer( final String image ) {
        return new Function2<Body, Double, BodyRenderer>() {
            public BodyRenderer apply( Body body, Double viewDiameter ) {
                return new BodyRenderer.ImageRenderer( body, viewDiameter, image );
            }
        };
    }

    private final Function2<Body, Double, BodyRenderer> SUN_RENDERER = new Function2<Body, Double, BodyRenderer>() {
        public BodyRenderer apply( Body body, Double viewDiameter ) {
            return new BodyRenderer.SphereRenderer( body, viewDiameter );
        }
    };
    private final int SEC_PER_MOON_ORBIT = 28 * 24 * 60 * 60;
    private final int SEC_PER_SPACE_STATION_ORBIT = 90 * 60;
    private final ArrayList<GravityAndOrbitsMode> modes = new ArrayList<GravityAndOrbitsMode>() {{
        Camera camera = new Camera();
        add( new GravityAndOrbitsMode( "Sun & Planet", VectorNode.FORCE_SCALE * 100 * 1.2, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT, days,
                                       createIconImage( true, true, false, false ), SEC_PER_YEAR, clockPausedProperty ) {
            {
                final Body sun = createSun( getMaxPathLength() );
                addBody( sun );
                addBody( createEarth( sun, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION, getMaxPathLength(), 650 ) );
            }

            @Override
            public double getZoomScale() {
                return 1.25;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return new ImmutableVector2D( 0, 0 );
            }
        } );
        add( new GravityAndOrbitsMode( "Sun, Planet & Moon", VectorNode.FORCE_SCALE * 100 * 1.2, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT, days,
                                       createIconImage( true, true, true, false ), SEC_PER_YEAR, clockPausedProperty ) {
            {
                final Body sun = createSun( getMaxPathLength() );
                addBody( sun );
                final Body earth = createEarth( sun, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION, getMaxPathLength(), 650 );
                addBody( earth );
                final Body moon = createMoon( earth, MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION,
                                              false,//no room for the slider
                                              getMaxPathLength(), 17, 1000, 25 );
                addBody( moon );
            }

            @Override
            public double getZoomScale() {
                return 1.25;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return new ImmutableVector2D( 0, 0 );
            }
        } );
        add( new GravityAndOrbitsMode( "Planet & Moon", VectorNode.FORCE_SCALE * 100 / 2 * 0.9, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT / 3, days,
                                       createIconImage( false, true, true, false ), SEC_PER_MOON_ORBIT, clockPausedProperty ) {
            // Add in some initial -x velocity to offset the earth-moon barycenter drift
            //This value was computed by sampling the total momentum in GravityAndOrbitsModel for this mode
            ImmutableVector2D sampledSystemMomentum = new ImmutableVector2D( 7.421397422188586E25, -1.080211713202125E22 );

            final Body earth;

            {
                ImmutableVector2D velocityOffset = sampledSystemMomentum.getScaledInstance( -1 / ( EARTH_MASS + MOON_MASS ) );
                earth = createEarth( null, velocityOffset.getX(), velocityOffset.getY(), getMaxPathLength(),
                                     650.0 / 400.0 / 1.25 * 10 );//scale so it is a similar size to other modes

                addBody( earth );
                addBody( createMoon( earth, MOON_SPEED, 0, true, getMaxPathLength(), 1, 1000 / 400 / 1.25 * 10, 1 ) );
            }

            @Override
            public double getZoomScale() {
                return 400;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return earth.getPosition();
            }
        } );
        add( new GravityAndOrbitsMode( "Planet & Space Station", VectorNode.FORCE_SCALE * 10000 * 1000 * 10000 * 100 * 3, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT / 10000 * 9, minutes,
                                       createIconImage( false, true, false, true ), SEC_PER_SPACE_STATION_ORBIT, clockPausedProperty ) {
            final Body earth = createEarth( null, 0, 0, getMaxPathLength(), 650.0 / 400.0 / 1.25 * 10 * 1.5 * 1.5 / 54 );

            {
                addBody( earth );
                addBody( createSpaceStation( earth, getMaxPathLength() ) );
            }

            @Override
            public double getZoomScale() {
                return 400 * 54;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return earth.getPosition();
            }
        } );
    }};

    private Image createIconImage( final boolean sun, final boolean earth, final boolean moon, final boolean spaceStation ) {
        return new PNode() {
            {
                int inset = 20;//distance between icons
                addChild( new PhetPPath( new Rectangle2D.Double( 20, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ) );
                addIcon( inset, createSun( 0 ).createRenderer( 30 ), sun );
                addIcon( inset, createEarth( null, 0, 0, 0, 650 ).createRenderer( 25 ), earth );
                addIcon( inset, createMoon( null, 0, 0, true, 0, 17, 1000, 1 ).createRenderer( 20 ), moon );
                addIcon( inset, createSpaceStation( null, 0 ).createRenderer( 30 ), spaceStation );
            }

            private void addIcon( int inset, PNode sunIcon, boolean sun ) {
                addChild( sunIcon );
                sunIcon.setOffset( getFullBounds().getMaxX() + inset + sunIcon.getFullBounds().getWidth() / 2, 0 );
                sunIcon.setVisible( sun );
            }
        }.toImage();
    }

    private Body createSpaceStation( Body earth, int maxPathLength ) {
        return new Body( earth, "Space Station", EARTH_PERIHELION + SPACE_STATION_PERIGEE + EARTH_RADIUS, 0, SPACE_STATION_RADIUS * 2 * 1000, 0,
                         SPACE_STATION_SPEED, SPACE_STATION_MASS, Color.gray, Color.white, 25000 / 80.0 / 54, 1000 * 1.6 / 80 * 2 / 54, getImageRenderer( "space-station.png" ), scaleProperty, -Math.PI / 4, true, maxPathLength, 1 );
    }

    private Property<GravityAndOrbitsMode> modeProperty = new Property<GravityAndOrbitsMode>( modes.get( 0 ) );

    private Body createMoon( Body earth, double vx, double vy, boolean massSettable, int maxPathLength, final double cartoonOffsetScale, final double cartoonDiameterScaleFactor, double cartoonForceVectorScale ) {
        return new Body( earth, "Moon", MOON_X, -MOON_Y, MOON_RADIUS * 2, vx, vy, MOON_MASS, Color.gray, Color.white, cartoonDiameterScaleFactor, cartoonOffsetScale,//putting this number too large makes a kink or curly-q in the moon trajectory, which should be avoided
                         getImageRenderer( "moon.png" ), scaleProperty, -3 * Math.PI / 4, massSettable, maxPathLength, cartoonForceVectorScale );
    }

    private Body createEarth( Body sun, double vx, double vy, int maxPathLength, final double cartoonDiameterScaleFactor ) {
        return new Body( sun, "Earth", EARTH_PERIHELION, 0, EARTH_RADIUS * 2, vx, vy, EARTH_MASS, Color.blue, Color.white, cartoonDiameterScaleFactor, 1, getImageRenderer( "earth.png" ), scaleProperty, -Math.PI / 4, true, maxPathLength, 1 );
    }

    private Body createSun( int maxPathLength ) {
        return new Body( null, "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, SUN_MASS, Color.yellow, Color.white, 50, 1, SUN_RENDERER, scaleProperty, -Math.PI / 4, true, maxPathLength, 1 );
    }

    public ArrayList<GravityAndOrbitsMode> getModes() {
        return new ArrayList<GravityAndOrbitsMode>( modes );
    }

    public GravityAndOrbitsModule( final PhetFrame phetFrame, String[] commandLineArgs ) {
        super( "Gravity and Orbits"
               + ": " + Arrays.asList( commandLineArgs )//For simsharing
                ,
               new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.DEFAULT_DT ) );//TODO: I don't think this clock is used since each mode has its own clock; perhaps this just runs the active tab?

        for ( GravityAndOrbitsMode mode : modes ) {
            mode.init( this );
        }

        setSimulationPanel( getMode().getCanvas() );

        // Switch the entire canvas on mode switches
        modeProperty.addObserver( new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setSimulationPanel( getMode().getCanvas() );
                        phetFrame.invalidate();
                        phetFrame.validate();
                        phetFrame.doLayout();
                    }
                } );
                updateActiveModule();
                modeProperty.getValue().startZoom();
            }
        } );
        setClockControlPanel( null );//clock panel appears in the canvas

        reset();
    }

    private GravityAndOrbitsMode getMode() {
        return modeProperty.getValue();
    }

    private void updateActiveModule() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.setActive( mode == getMode() );
        }
    }

    /**
     * Resets the module.
     */
    public void resetAll() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.reset();
        }
        showGravityForceProperty.reset();
        showPathProperty.reset();
        showVelocityProperty.reset();
        showMassProperty.reset();
        scaleProperty.reset();
        modeProperty.reset();
    }

    public Property<Boolean> getShowGravityForceProperty() {
        return showGravityForceProperty;
    }

    public Property<Boolean> getShowPathProperty() {
        return showPathProperty;
    }

    public Property<Boolean> getShowVelocityProperty() {
        return showVelocityProperty;
    }

    public Property<Boolean> getShowMassProperty() {
        return showMassProperty;
    }

    public Property<GravityAndOrbitsMode> getModeProperty() {
        return modeProperty;
    }

    public void setTeacherMode( boolean b ) {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.getModel().teacherMode = b;
        }
    }

    public void addModelSteppedListener( SimpleObserver simpleObserver ) {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.getModel().addModelSteppedListener( simpleObserver );
        }
    }

    public Property<Scale> getScaleProperty() {
        return scaleProperty;
    }

    public Property<Boolean> getClockPausedProperty() {
        return clockPausedProperty;
    }
}