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

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockStateEvent;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.*;
import edu.colorado.phet.lasers.controller.LaserConfig;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.controller.module.MultipleAtomModule;
import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.PhysicsUtil;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.photon.Beam;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A panel that displays graphics for energy levels and squiggles for the energy of the photons in collimated beams.
 * A disc is drawn on the energy levels for each atom in that state.
 */
public class LaserEnergyLevelMonitorPanel extends MonitorPanel implements SimpleObserver,
                                                                          Beam.WavelengthChangeListener,
                                                                          Beam.RateChangeListener,
                                                                          ClockStateListener {
    private static final double LEVEL_GRAPHIC_LEVEL = 1E3;

    // Number of milliseconds between display updates. Energy level populations are averaged over this time
    private long averagingPeriod = 300;
    private long lastPaintTime;
    private int numUpdatesToAverage;
    // The diameter of an atom as displayed on the screen, in pixels
    private int atomDiam = 10;
    // Dimensions of the panel
    private double panelHeight = 230;
    private double panelWidth = 320;
    // Amplitude of the squiggle waves
    private int squiggleHeight = atomDiam;
    // Location and size of energy level lines
    private Point2D origin = new Point2D.Double( 25, panelHeight - 30 );
    private double levelLineOriginX = origin.getX();
    private double levelLineLength = panelWidth - levelLineOriginX - 50;

    private EnergyLevelGraphic[] levelGraphics = new EnergyLevelGraphic[3];
    private EnergyLifetimeSlider[] lifetimeSliders = new EnergyLifetimeSlider[3];
    private int[] numAtomsInLevel = new int[3];
    private int[] atomCntAccums = new int[3];

    // Number of energy levels to show
    private int numLevels;

    private LaserModel model;
    private double pumpBeamEnergy;
    private double seedBeamEnergy;
    private ModelViewTransform1D energyYTx;
    private BufferedImage stimSquiggle;
    private BufferedImage pumpSquiggle;
    private AffineTransform stimSquiggleTx;
    private AffineTransform pumpSquiggleTx;
    private BufferedImage baseSphereImg;
    private BaseLaserModule module;
    // The offset by which all the graphic elements must be placed, caused by the heading text
    private int headerOffsetY = 20;
    private int footerOffsetY = 10;
//    private AffineTransform seedLampAtx;
//    private AffineTransform pumpLampAtx;
//    private LampIcon pumpLampGraphic;
//    private LampIcon seedLampGraphic;
//    private AffineTransform pumpLampTx;

    /**
     *
     */
    public LaserEnergyLevelMonitorPanel( BaseLaserModule module, AbstractClock clock ) {

        super( clock );

        this.module = module;
        model = module.getLaserModel();
        model.addObserver( this );
        clock.addClockStateListener( this );
        model.getPumpingBeam().addWavelengthChangeListener( this );
        model.getPumpingBeam().addRateChangeListener( this );
        model.getSeedBeam().addWavelengthChangeListener( this );
        model.getSeedBeam().addRateChangeListener( this );

        // Create the graphics and controls that represent the energy levels of the atoms
        createEnergyLevelReps();

        this.setBackground( Color.white );
        JLabel dummyLabel = new JLabel( "foo" );
        Font font = dummyLabel.getFont();
        String header = null;
        if( module instanceof MultipleAtomModule ) {
            header = SimStrings.get( "EnergyMonitorPanel.header.plural" );
        }
        else {
            header = SimStrings.get( "EnergyMonitorPanel.header.singular" );
        }
        PhetTextGraphic headingText = new PhetTextGraphic( this, font,
                                                           header,
                                                           Color.black );
        headingText.setLocation( 30, 5 );
        this.addGraphic( headingText );

        // Create images for the lmps that cue the users as to what the squiggles mean
//        createLampGraphics();

        // Set up the event handlers we need
        this.addComponentListener( new PanelResizer() );
    }

    private void createLampGraphics() {
        BufferedImage gunBI = null;
        try {
            gunBI = ImageLoader.loadBufferedImage( LaserConfig.RAY_GUN_IMAGE_FILE );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        Dimension graphicSize = new Dimension( 25, 13 );
//        Dimension graphicSize = new Dimension( 20, 10 );
//        double scaleX = graphicSize.getWidth() / gunBI.getWidth();
//        double scaleY = graphicSize.getHeight() / gunBI.getHeight();

        // Seed beam lamp graphic
//        AffineTransformOp atxOp1 = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ), AffineTransformOp.TYPE_BILINEAR );
//        BufferedImage lampBI = atxOp1.filter( gunBI, null );
//        seedLampAtx = new AffineTransform();
//        seedLampGraphic = new LampIcon( model.getSeedBeam(), this, lampBI, seedLampAtx );
//        addGraphic( seedLampGraphic, LaserConfig.PHOTON_LAYER + 1 );

        // Pump beam lamp graphic
//        BufferedImage pumpLampBI = BufferedImageUtils.getRotatedImage( lampBI, Math.PI / 2 );
//        pumpLampAtx = new AffineTransform();
//        pumpLampAtx.rotate( Math.PI / 2 );
//        pumpLampGraphic = new LampIcon( model.getPumpingBeam(), this, lampBI, pumpLampAtx );
//        addGraphic( pumpLampGraphic, LaserConfig.PHOTON_LAYER + 1 );
    }

    /**
     * Creates a line for each of the energy levels, with a slider to control the lifetime
     * of the level
     */
    private void createEnergyLevelReps() {

        // Clear any existing graphics and controls
        for( int i = 0; i < levelGraphics.length; i++ ) {
            EnergyLevelGraphic levelGraphic = levelGraphics[i];
            removeGraphic( levelGraphic );
        }
        for( int i = 0; i < lifetimeSliders.length; i++ ) {
            EnergyLifetimeSlider lifetimeSlider = lifetimeSliders[i];
            if( lifetimeSlider != null ) {
                remove( lifetimeSlider );
            }
        }
        levelGraphics = new EnergyLevelGraphic[3];
        lifetimeSliders = new EnergyLifetimeSlider[3];

        AtomicState[] states = model.getStates();
        for( int i = 0; i < states.length; i++ ) {
            AtomicState state = states[i];
            double xIndent = ( states.length - i ) * 12;
            xIndent = 5;
            double xLoc = levelLineOriginX + xIndent;
            EnergyLevelGraphic elg = new EnergyLevelGraphic( this,
                                                             state,
                                                             Color.black,
                                                             xLoc,
                                                             levelLineLength - levelLineOriginX,
                                                             true,
                                                             levelLineOriginX + levelLineLength - 25 );
//                                                             levelLineOriginX );
            addGraphic( elg, LEVEL_GRAPHIC_LEVEL );
            levelGraphics[i] = elg;

            // Don't add a lifetime adjustment slider for the ground state
            if( i > 0 ) {
                EnergyLifetimeSlider slider = new EnergyLifetimeSlider( state,
                                                                        elg,
                                                                        LaserConfig.MIDDLE_ENERGY_STATE_MAX_LIFETIME,
                                                                        this );
                lifetimeSliders[i] = slider;
                this.add( slider );

                // Add a listener that will flash the line when it matches the wavelength of
                // either of the beams
                new EnergyMatchDetector( state, model.getSeedBeam(), elg );
                new EnergyMatchDetector( state, model.getPumpingBeam(), elg );
            }

            // Add an icon to the level. This requires a dummy atom in the state the icon is to represent
            Atom atom = new Atom( model, levelGraphics.length, true );
            atom.setStates( states );
            atom.setCurrState( states[i] );
            levelGraphics[i].setLevelIcon( new edu.colorado.phet.lasers.view.LevelIcon( this, atom ) );

        }
        adjustPanel();
    }

    /**
     * Adjusts the layout of the panel
     */
    public void adjustPanel() {
        // The beamArea in which the energy levels will be displayed
        Rectangle2D bounds = new Rectangle2D.Double( getBounds().getMinX(), getBounds().getMinY() + 10,
                                                     getBounds().getWidth(), getBounds().getHeight() - 30 );
        double groundStateEnergy = model.getGroundState().getEnergyLevel();
        energyYTx = new ModelViewTransform1D( groundStateEnergy + PhysicsUtil.wavelengthToEnergy( VisibleColor.MIN_WAVELENGTH ),
                                              groundStateEnergy,
                                              (int)bounds.getBounds().getMinY() + headerOffsetY,
                                              (int)bounds.getBounds().getMaxY() - footerOffsetY );
        for( int i = 0; i < levelGraphics.length; i++ ) {
            if( levelGraphics[i] != null ) {
                levelGraphics[i].update( energyYTx );
            }
        }
        updateSquiggles();
    }

    /**
     * Sets the number of energy levels shown on the panel.
     *
     * @param numLevels Valid values are 2 and 3.
     */
    public void setNumLevels( int numLevels ) {
        this.numLevels = numLevels;
        createEnergyLevelReps();
        setPreferredSize( new Dimension( (int)panelWidth, (int)panelHeight ) );
        revalidate();
        repaint();
    }

    /**
     * Handles updates from the model
     */
    public void update() {
        atomCntAccums[0] += model.getNumGroundStateAtoms();
        atomCntAccums[1] += model.getNumMiddleStateAtoms();
        atomCntAccums[2] += model.getNumHighStateAtoms();

        // todo: these two line might be able to go somewhere they aren't called as often
        for( int i = 1; i < numLevels; i++ ) {
            if( lifetimeSliders[i] != null ) {
                lifetimeSliders[i].update();
            }
        }

        numUpdatesToAverage++;
        long currTime = System.currentTimeMillis();
        if( currTime - lastPaintTime >= averagingPeriod ) {
            // Compute the average number of atoms in each state. Take care to round off rather than truncate.
            numAtomsInLevel[0] = (int)( 0.5 + (double)atomCntAccums[0] / numUpdatesToAverage );
            numAtomsInLevel[1] = (int)( 0.5 + (double)atomCntAccums[1] / numUpdatesToAverage );
            numAtomsInLevel[2] = (int)( 0.5 + (double)atomCntAccums[2] / numUpdatesToAverage );
            atomCntAccums[0] = 0;
            atomCntAccums[1] = 0;
            atomCntAccums[2] = 0;
            numUpdatesToAverage = 0;
            lastPaintTime = currTime;
            this.invalidate();
            this.repaint();
        }
    }

    /**
     * Recomputes the squiggle images for both beams
     */
    private void updateSquiggles() {
        double groundStateEnergy = model.getGroundState().getEnergyLevel();
        double y0 = energyYTx.modelToView( groundStateEnergy );
        double y1 = energyYTx.modelToView( groundStateEnergy + seedBeamEnergy );
        double y2 = energyYTx.modelToView( groundStateEnergy + pumpBeamEnergy );

        // Build the images for the squiggles that represent the energies of the stimulating and pumping beam
        if( y0 > y1 ) {
            double squiggleOffsetX = squiggleHeight;
            stimSquiggle = computeSquiggleImage( model.getSeedBeam(), 0, (int)( y0 - y1 ), squiggleHeight );
            stimSquiggleTx = AffineTransform.getTranslateInstance( levelGraphics[1].getPosition().getX() + squiggleOffsetX,
                                                                   energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() ) );
            stimSquiggleTx.rotate( -Math.PI / 2 );

            // Update the location of the lamp graphic
//            Point p = new Point( (int)(stimSquiggleTx.getTranslateX() + stimSquiggle.getHeight() / 2 - seedLampGraphic.getWidth() / 2 ),
//                                 (int)(stimSquiggleTx.getTranslateY() - stimSquiggle.getWidth() / 2));
//            seedLampGraphic.setLocation( p );

        }

        if( y0 > y2 && numLevels > 2 ) {
            pumpSquiggle = computeSquiggleImage( model.getPumpingBeam(), 0, (int)( y0 - y2 ), squiggleHeight );
            pumpSquiggleTx = AffineTransform.getTranslateInstance( levelGraphics[2].getPosition().getX(),
                                                                   energyYTx.modelToView( module.getLaserModel().getGroundState().getEnergyLevel() ) );
            pumpSquiggleTx.rotate( -Math.PI / 2 );

            // Update the location of the lamp graphic
//            Point p2 = new Point( (int)(pumpSquiggleTx.getTranslateX() + pumpSquiggle.getHeight() / 2 + pumpLampGraphic.getWidth() / 2 ),
//                                  (int)(pumpSquiggleTx.getTranslateY() - pumpSquiggle.getWidth() / 2));
//            pumpLampGraphic.setLocation( p2 );
        }

        // Force a repaint
        this.invalidate();
        this.repaint();
    }

    /**
     * Creates a buffered image for a squiggle
     */
    private BufferedImage computeSquiggleImage( Beam beam, double phaseAngle, int length, int height ) {
        double wavelength = beam.getWavelength();
        int arrowHeight = height;

        // So that the tip of the arrow will just touch an energy level line when it is supposed to match the line,
        // we need to subtract 1 from the length of the squiggle
        int actualLength = length - 1;

        // A buffered image for generating the image data
        BufferedImage img = new BufferedImage( actualLength + 2 * arrowHeight,
                                               height,
                                               BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2d = img.createGraphics();
        int kPrev = height / 2;
        int iPrev = 0;
        Color c = VisibleColor.wavelengthToColor( wavelength );
        double freqFactor = 15 * wavelength / 680;
        for( int i = 0; i < actualLength - arrowHeight * 2; i++ ) {
            int k = (int)( Math.sin( phaseAngle + i * Math.PI * 2 / freqFactor ) * height / 2 + height / 2 );
            for( int j = 0; j < height; j++ ) {
                if( j == k ) {
                    g2d.setColor( c );
                    g2d.drawLine( iPrev + arrowHeight, kPrev, i + arrowHeight, k );
                    iPrev = i;
                    kPrev = k;
                }
            }
        }
        Arrow head = new Arrow( new Point2D.Double( arrowHeight, height / 2 ),
                                new Point2D.Double( 0, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        Arrow tail = new Arrow( new Point2D.Double( actualLength - arrowHeight, height / 2 ),
                                new Point2D.Double( actualLength, height / 2 ),
                                arrowHeight, height * 1.2, 2 );
        g2d.fill( head.getShape() );
        g2d.fill( tail.getShape() );
        g2d.dispose();
        return img;
    }

    public void setAveragingPeriod( long value ) {
        averagingPeriod = value;
    }

    public long getAveragingPeriod() {
        return averagingPeriod;
    }

    //----------------------------------------------------------------
    // Rendering
    //----------------------------------------------------------------

    /**
     * @param graphics
     */
    protected void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAntiAliasingOn( g2 );

        for( int i = 0; i < numLevels; i++ ) {
            EnergyLevelGraphic levelGraphic = levelGraphics[i];
            if( levelGraphic == null ) {
                System.out.println( "asdf" );
            }
            drawAtomsInLevel( g2, Color.darkGray, levelGraphic, numAtomsInLevel[i] );
        }

        // Draw squiggles showing what energy photons the beams are putting out
        if( stimSquiggle != null && model.getSeedBeam().isEnabled() ) {
            double intensity = model.getSeedBeam().getPhotonsPerSecond() / model.getSeedBeam().getMaxPhotonsPerSecond();
            GraphicsUtil.setAlpha( g2, intensity );
            g2.drawRenderedImage( stimSquiggle, stimSquiggleTx );
//            seedLampGraphic.setAlpha( intensity );
        }
        if( pumpSquiggle != null && model.getPumpingBeam().isEnabled() ) {
            double intensity = model.getPumpingBeam().getPhotonsPerSecond() / model.getPumpingBeam().getMaxPhotonsPerSecond();
            GraphicsUtil.setAlpha( g2, intensity );
            g2.drawRenderedImage( pumpSquiggle, pumpSquiggleTx );
//            pumpLampGraphic.setAlpha( intensity );
        }

        gs.restoreGraphics();
    }

    private void drawAtomsInLevel( Graphics2D g2, Color color, EnergyLevelGraphic line, int numInLevel ) {
        BufferedImage bi = getAtomImage( color );
        double scale = (double)atomDiam / bi.getWidth();
        AffineTransform atx = new AffineTransform();
        double offsetX = squiggleHeight * 2;
        atx.translate( line.getLinePosition().getX() + offsetX - atomDiam / 2,
                       line.getLinePosition().getY() - atomDiam );
//        atx.translate( line.getLinePosition().getX() - atomDiam / 2,
//                       line.getLinePosition().getY() - atomDiam );
        atx.scale( scale, scale );
        for( int i = 0; i < numInLevel; i++ ) {
            atx.translate( atomDiam * 0.7 / scale, 0 );
            g2.drawRenderedImage( bi, atx );
        }
    }

    private Map colorToAtomImage = new HashMap();

    private BufferedImage getAtomImage( Color color ) {
        if( baseSphereImg == null ) {
            try {
                baseSphereImg = ImageLoader.loadBufferedImage( "images/particle-red-lrg.gif" );
//                baseSphereImg = ImageLoader.loadBufferedImage( "images/particle-red-med.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        // Look for the image in the cache
        BufferedImage atomImg = (BufferedImage)colorToAtomImage.get( color );
        if( atomImg == null ) {
            atomImg = new BufferedImage( baseSphereImg.getWidth(), baseSphereImg.getHeight(), BufferedImage.TYPE_INT_ARGB );
            MakeDuotoneImageOp op = new MakeDuotoneImageOp( color );
            op.filter( baseSphereImg, atomImg );
            colorToAtomImage.put( color, atomImg );
        }
        return atomImg;
    }

    //----------------------------------------------------------------
    // LeftSystemEvent handlers
    //----------------------------------------------------------------

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        Beam beam = (Beam)event.getSource();
        if( beam == model.getPumpingBeam() ) {
            double pumpBeamWavelength = beam.getWavelength();
            pumpBeamEnergy = PhysicsUtil.wavelengthToEnergy( pumpBeamWavelength );
        }
        if( beam == model.getSeedBeam() ) {
            double seedBeamWavelength = beam.getWavelength();
            seedBeamEnergy = PhysicsUtil.wavelengthToEnergy( seedBeamWavelength );
        }
        updateSquiggles();
    }

    public void rateChangeOccurred( Beam.RateChangeEvent event ) {
        updateSquiggles();
    }

    //----------------------------------------------------------------
    // ClockStateListener implementation
    //----------------------------------------------------------------

    /**
     * If the clock pauses, force the update and repaint of energy level populations. We need to do this because
     * when the clock is running, the populations shown are averages over time, and if the clock is paused, we
     * want the populations shown to agree with the actual number of atoms in each state.
     *
     * @param event
     */
    public void stateChanged( ClockStateEvent event ) {
        if( event.getIsPaused() ) {
            numGroundLevel = model.getNumGroundStateAtoms();
            numMiddleLevel = model.getNumMiddleStateAtoms();
            numHighLevel = model.getNumHighStateAtoms();
            this.invalidate();
            this.repaint();
        }
    }

    public void delayChanged( int waitTime ) {
    }

    public void dtChanged( double dt ) {
    }

    public void threadPriorityChanged( int priority ) {
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * Set the beamArea within the panel that the energy level lines can be positioned
     */
    private class PanelResizer extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            adjustPanel();
        }
    }

    /**
     * Flashes a PhetGraphic when the wavelength of a beam matches (or is acceptably close to) the
     * difference in energy between an atomic state and the model's ground state
     */
    private class EnergyMatchDetector implements AtomicState.Listener, Beam.WavelengthChangeListener {
        private AtomicState atomicState;
        private Beam beam;
        private EnergyLevelGraphic graphic;
        private boolean matched;

        public EnergyMatchDetector( AtomicState atomicState, Beam beam, EnergyLevelGraphic graphic ) {
            this.atomicState = atomicState;
            this.beam = beam;
            this.graphic = graphic;
            atomicState.addListener( this );
            beam.addWavelengthChangeListener( this );
        }

        public void energyLevelChanged( AtomicState.Event event ) {
            checkForMatch();
        }

        private void checkForMatch() {
            double requiredDE = atomicState.getEnergyLevel() - model.getGroundState().getEnergyLevel();
            if( beam.isEnabled() && Math.abs( PhysicsUtil.wavelengthToEnergy( beam.getWavelength() ) - requiredDE )
                                    <= LaserConfig.ENERGY_TOLERANCE ) {
                if( !matched ) {
                    flashGraphic();
                    matched = true;
                }
            }
            else {
                matched = false;
            }
        }

        public void meanLifetimechanged( AtomicState.Event event ) {
            // noop
        }

        public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
            checkForMatch();
        }

        boolean hidden;

        private void flashGraphic() {
            GraphicFlasher gf = new GraphicFlasher( graphic );
            gf.start();
        }
    }

    private class GraphicFlasher extends Thread {
        private int numFlashes = 5;
        private EnergyLevelGraphic graphic;

        public GraphicFlasher( EnergyLevelGraphic graphic ) {
            this.graphic = graphic;
        }

        public void run() {
            try {
                for( int i = 0; i < numFlashes; i++ ) {
                    graphic.setVisible( false );
                    Thread.sleep( 100 );
                    graphic.setVisible( true );
                    Thread.sleep( 100 );
                }
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
    }
}
