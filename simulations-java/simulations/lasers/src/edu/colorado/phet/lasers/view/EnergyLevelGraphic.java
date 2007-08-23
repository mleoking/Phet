/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.phetcommon.math.ModelViewTransform1D;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.quantum.QuantumConfig;
import edu.colorado.phet.common.quantum.model.AtomicState;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * An interactive graphic that represents an energy level for a type of atom. It can be moved up and down with the
 * mouse, and it's range of movement is limited by the energy levels of the next higher and next lower energy states.
 */
public class EnergyLevelGraphic extends CompositePhetGraphic {
    private AtomicState atomicState;
    private double groundStateEnergy;
    private boolean isAdjustable;
    private double iconLocX;
    private Color color;
    private double xLoc;
    private double width;
    private EnergyLevelRep energyLevelRep;
    // This transform controls the y location of the line
    private ModelViewTransform1D energyYTx;
    private boolean arrowsEnabled = true;
    // The fewest pixels that will be allowed between energy levels;
    private int minPixelsBetweenLevels = EnergyLifetimeSlider.sliderHeight;

    // Strategy for setting to color of this energy level graphic
    //    private ColorStrategy colorStrategy = new BlackStrategy();
    private ColorStrategy colorStrategy = new VisibleColorStrategy();
    private LevelIcon levelIcon;
    private boolean match = false;
    private long lastMatchTime;

    /**
     * @param component
     * @param atomicState
     * @param groundStateEnergy
     * @param xLoc
     * @param width
     * @param isAdjustable      Determines if the graphic can be moved up and down with the mouse
     */
    public EnergyLevelGraphic( Component component, AtomicState atomicState, double groundStateEnergy, double xLoc, double width,
                               boolean isAdjustable, double iconLocX ) {
        super( null );

        this.atomicState = atomicState;
        this.groundStateEnergy = groundStateEnergy;
        this.isAdjustable = isAdjustable;
        this.iconLocX = iconLocX;

        // Add a listener that will track changes in the atomic state
        atomicState.addListener( new AtomicStateChangeListener() );

        this.xLoc = xLoc;
        this.width = width;
        energyLevelRep = new EnergyLevelRep( component );
        addGraphic( energyLevelRep );

        if( isAdjustable ) {
            setCursorHand();
            addTranslationListener( new EnergyLevelTranslator() );
        }
    }

    public void update( ModelViewTransform1D tx ) {
        this.energyYTx = tx;
        energyLevelRep.update();
    }

    public Point2D getPosition() {
        return energyLevelRep.getBounds().getLocation();
    }

    public Point2D getLinePosition() {
        return energyLevelRep.getLinePosition();
    }

    public void setArrowsEnabled( boolean arrowsEnabled ) {
        this.arrowsEnabled = arrowsEnabled;
    }

    public void setLevelIcon( LevelIcon levelIcon ) {
        this.levelIcon = levelIcon;
        levelIcon.setCursorHand();
        energyLevelRep.setLevelIcon( levelIcon );
    }

    public void setMinPixelsBetweenLevels( int minPixelsBetweenLevels ) {
        this.minPixelsBetweenLevels = minPixelsBetweenLevels;
    }

    /**
     * Sets the strategy used to pick the color for the graphic
     *
     * @param colorStrategy
     */
    public void setColorStrategy( ColorStrategy colorStrategy ) {
        this.colorStrategy = colorStrategy;
    }

    public void setMatch( boolean match ) {
        this.match = match;
        lastMatchTime = System.currentTimeMillis();
        energyLevelRep.update();
        repaint();
    }

    public static void setBlinkRenderer( Color color ) {
        for( int i = 0; i < instances.size(); i++ ) {
            EnergyLevelRep energyLevelRep = (EnergyLevelRep)instances.get( i );
            energyLevelRep.setBlinkRenderer( color );
        }
    }

    public static void setFlowRenderer() {
        for( int i = 0; i < instances.size(); i++ ) {
            EnergyLevelRep energyLevelRep = (EnergyLevelRep)instances.get( i );
            energyLevelRep.setFlowRenderer();
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Updates the graphic when the energy level of the atomic state changes
     */
    private class AtomicStateChangeListener extends AtomicState.ChangeListenerAdapter {
        public void energyLevelChanged( AtomicState.Event event ) {
            energyLevelRep.update();
            levelIcon.updateEnergy( event.getEnergy() );
        }
    }

    public static boolean laserApplicationRunning = false;//todo: fix this awkward workaround for problem in EnergyLevelGraphic

    /**
     * Inner class that handles translation of the graphic
     */
    private class EnergyLevelTranslator implements TranslationListener {

        public void translationOccurred( TranslationEvent translationEvent ) {
            int dy = translationEvent.getDy();
            double energyChange = energyYTx.viewToModelDifferential( dy );
            // Don't let one level get closer than a certain number of pixels to the one above or below
            double minEnergyDifference = energyYTx.viewToModelDifferential( -minPixelsBetweenLevels );
            double newEnergy = Math.max( Math.min( atomicState.getNextHigherEnergyState().getEnergyLevel() - minEnergyDifference,
                                                   atomicState.getEnergyLevel() + energyChange ),
                                         atomicState.getNextLowerEnergyState().getEnergyLevel() + minEnergyDifference );

            // The following line was screwing things up for the configurable atom in Discharge Lamps when I
            // rebuilton 9/11/06
            if( laserApplicationRunning ) {//todo: fix this awkward workaround for problem in EnergyLevelGraphic
                newEnergy = Math.min( newEnergy, PhysicsUtil.wavelengthToEnergy( QuantumConfig.MIN_WAVELENGTH ) + groundStateEnergy );
            }

            atomicState.setEnergyLevel( newEnergy );
            atomicState.determineEmittedPhotonWavelength();

            // Update the atom icon
            levelIcon.updateEnergy( newEnergy );
        }
    }

    public static final ArrayList instances = new ArrayList();

    public static void setRenderer( RenderStrategy renderStrategy ) {
        for( int i = 0; i < instances.size(); i++ ) {
            EnergyLevelRep levelRep = (EnergyLevelRep)instances.get( i );
            levelRep.setRenderer( renderStrategy );
        }
    }

    /**
     * The graphic class itself
     */
    public class EnergyLevelRep extends CompositePhetGraphic {

        private Rectangle2D levelLine = new Rectangle2D.Double();
        private double thickness = 2;
        private Arrow arrow1;
        private Arrow arrow2;
        private Rectangle boundingRect;
        private LevelIcon levelIcon;
        private PhetTextGraphic2 textGraphic;

        protected EnergyLevelRep( Component component ) {
            super( component );
            PhetShapeGraphic lineGraphic = new PhetShapeGraphic( component, levelLine, color );
            lineGraphic.setVisible( true );
            addGraphic( lineGraphic );
            instances.add( this );

        }

        public void setRenderer( RenderStrategy renderStrategy ) {
            this.strategy = renderStrategy;
            update();
        }

        private void update() {
            color = colorStrategy.getColor( atomicState, groundStateEnergy );

            // We need to create a new color that can't be transparent. VisibleColor will return
            // an "invisible" color with RGB = 0,0,0 if the wavelenght is not visible. And since our
            // ground state has a wavelength that is below visible, and we want a black line, this
            // is the best hack to use.
            color = new Color( color.getRed(), color.getGreen(), color.getBlue() );
            int y = energyYTx.modelToView( atomicState.getEnergyLevel() );
            levelLine.setRect( xLoc, y - thickness / 2, width, thickness );

            if( levelIcon != null ) {
                levelIcon.setLocation( (int)( iconLocX ), (int)( y - thickness ) );
            }

            if( isAdjustable ) {
                double xOffset = width - 30;
                int arrowHt = 16;
                int arrowHeadWd = 10;
                int tailWd = 3;
                arrow1 = new Arrow( new Point2D.Double( xLoc + xOffset, y ),
                                    new Point2D.Double( xLoc + xOffset, y + arrowHt ),
                                    arrowHeadWd, arrowHeadWd, tailWd );
                arrow2 = new Arrow( new Point2D.Double( xLoc + xOffset, y ),
                                    new Point2D.Double( xLoc + xOffset, y - arrowHt ),
                                    arrowHeadWd, arrowHeadWd, tailWd );
            }
            if( textGraphic != null ) {
                textGraphic.setLocation( (int)( iconLocX + levelIcon.getWidth() / 2 + 6 ), (int)levelLine.getY()  -textGraphic.getHeight()/2-EnergyLifetimeSlider.sliderHeight);
            }
//            textGraphic.setLocation( (int)( iconLocX ), (int)levelLine.getY() );
            boundingRect = determineBoundsInternal();
            setBoundsDirty();
            repaint();
        }

        protected Rectangle determineBounds() {
            return boundingRect;
        }

        private Rectangle determineBoundsInternal() {
            if( arrow1 != null && arrowsEnabled ) {
                Area a = new Area( arrow1.getShape() );
                a.add( new Area( arrow2.getShape() ) );
                a.add( new Area( levelLine ) );
                return a.getBounds();
            }
            else {
                return levelLine.getBounds();
            }
        }

        void setLevelIcon( LevelIcon levelIcon ) {
            this.levelIcon = levelIcon;
            addGraphic( levelIcon );
            if( atomicState.getEnergyLevel() > -13.5 ) {
//                textGraphic = new PhetTextGraphic2( getComponent(), new Font( "Lucida Sans", Font.PLAIN, 10 ), "Lifetime", Color.black );
                textGraphic = new PhetTextGraphic2( getComponent(), new JLabel().getFont(), "Lifetime", Color.black );
                addGraphic( textGraphic );
            }
        }

        public boolean contains( int x, int y ) {
            return boundingRect.contains( x, y ) || levelIcon.contains( x, y );
        }

        //        RenderStrategy strategy = new FlowLine();
        //        RenderStrategy strategy = new Blink( Color.gray );
        RenderStrategy strategy = new Blink( Color.lightGray );

        //----------------------------------------------------------------
        // Rendering
        //----------------------------------------------------------------
        public void paint( Graphics2D g ) {
            saveGraphicsState( g );
            strategy.render( g );
            super.paint( g );
            restoreGraphicsState();
        }

        public Point2D getLinePosition() {
            return levelLine.getBounds().getLocation();
        }

        public void setBlinkRenderer( Color color ) {
            setRenderer( new Blink( color ) );
        }

        public void setFlowRenderer() {
            setRenderer( new FlowLine() );
        }

        public class Blink implements RenderStrategy {
            Color targetColor;

            public Blink( Color targetColor ) {
                this.targetColor = targetColor;
            }

            public void render( Graphics2D g ) {
                if( isAdjustable && arrowsEnabled ) {
                    g.setColor( Color.DARK_GRAY );
                    g.draw( arrow1.getShape() );
                    g.draw( arrow2.getShape() );
                }
                boolean timeOn = ( System.currentTimeMillis() / 400 ) % 2 == 0;
                if( System.currentTimeMillis() - lastMatchTime > 1500 ) {
                    timeOn = false;
                }
                g.setColor( timeOn && match ? targetColor : color );
                if( match ) {
                    levelIcon.setVisible( !timeOn );
                }
                else {
                    levelIcon.setVisible( true );
                }
                g.fill( levelLine );
            }

        }

        public class FlowLine implements RenderStrategy {

            float phase = 0f;

            public void render( Graphics2D g ) {

                if( isAdjustable && arrowsEnabled ) {
                    g.setColor( Color.DARK_GRAY );
                    g.draw( arrow1.getShape() );
                    g.draw( arrow2.getShape() );
                }
                g.setColor( color );
                if( match ) {
                    g.setStroke( new BasicStroke( 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, new float[]{5, 3}, phase ) );
                    g.draw( new Line2D.Double( levelLine.getMinX(), levelLine.getCenterY(), levelLine.getMaxX(), levelLine.getCenterY() ) );
                    phase += 8 - 1;
                }
                else {
                    g.setColor( color );
                    g.fill( levelLine );
                }

            }
        }
    }

    public interface RenderStrategy {
        public void render( Graphics2D g2 );
    }


    /**
     * Determines the color in which to render lines and atoms for a specified state
     */
    static public interface ColorStrategy {
        Color getColor( AtomicState state, double groundStateEnergy );
    }

    static public class VisibleColorStrategy implements ColorStrategy {
        public Color getColor( AtomicState state, double groundStateEnergy ) {
            double de = state.getEnergyLevel() - groundStateEnergy;
            return VisibleColor.wavelengthToColor( PhysicsUtil.energyToWavelength( de ) );
        }
    }

    static public class BlackStrategy implements ColorStrategy {
        public Color getColor( AtomicState state, double groundStateEnergy ) {
            return Color.black;
        }
    }
}
