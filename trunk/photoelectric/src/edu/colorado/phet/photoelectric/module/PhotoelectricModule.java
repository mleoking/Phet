/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.photoelectric.module;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.control.BatterySlider;
import edu.colorado.phet.dischargelamps.model.Battery;
import edu.colorado.phet.dischargelamps.view.BatteryReadout;
import edu.colorado.phet.dischargelamps.view.PlateGraphic;
import edu.colorado.phet.lasers.controller.module.BaseLaserModule;
import edu.colorado.phet.lasers.model.ResonatingCavity;
import edu.colorado.phet.lasers.model.photon.CollimatedBeam;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedEvent;
import edu.colorado.phet.lasers.model.photon.PhotonEmittedListener;
import edu.colorado.phet.lasers.view.BeamCurtainGraphic;
import edu.colorado.phet.lasers.view.LampGraphic;
import edu.colorado.phet.lasers.view.ResonatingCavityGraphic;
import edu.colorado.phet.photoelectric.PhotoelectricApplication;
import edu.colorado.phet.photoelectric.PhotoelectricConfig;
import edu.colorado.phet.photoelectric.controller.BeamControl;
import edu.colorado.phet.photoelectric.controller.PhotoelectricControlPanel;
import edu.colorado.phet.photoelectric.model.PhotoelectricModel;
import edu.colorado.phet.photoelectric.model.PhotoelectricTarget;
import edu.colorado.phet.photoelectric.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;

/**
 * PhotoelectricModule
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhotoelectricModule extends BaseLaserModule {

    //----------------------------------------------------------------
    // Class data 
    //----------------------------------------------------------------

    static private final int BEAM_VIEW = 1;
    static private final int PHOTON_VIEW = 2;

    static private HashMap TARGET_COLORS = new HashMap();

    static {
        TARGET_COLORS.put( PhotoelectricTarget.COPPER, new Color( 210, 130, 30 ) );
        TARGET_COLORS.put( PhotoelectricTarget.MAGNESIUM, new Color( 130, 150, 170 ) );
        TARGET_COLORS.put( PhotoelectricTarget.SODIUM, new Color( 160, 180, 160 ) );
        TARGET_COLORS.put( PhotoelectricTarget.ZINC, new Color( 200, 200, 200 ) );
        TARGET_COLORS.put( PhotoelectricTarget.PLATINUM, new Color( 203, 230, 230 ) );
    }

//    public static boolean DEBUG = true;
    public static boolean DEBUG = false;

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private PhotoelectricTarget targetPlate;
    // The scale to apply to graphics created in external applications so they appear properly
    // on the screen
    private double externalGraphicsScale;
    // AffineTransformOp that will scale graphics created in external applications so they appear
    // properly on the screen
    private AffineTransformOp externalGraphicScaleOp;
    // An ElectronSink that absorbs electrons if they come back and hit the target
    private BeamCurtainGraphic beamGraphic;
    // Flag for type of beam view: either photon or solid beam
    private int viewType = BEAM_VIEW;
    // We have two circuit images, one for each orientation of the battery
    private PhetImageGraphic circuitGraphic;
    private BufferedImage circuitImageA;
    private BufferedImage circuitImageB;


    /**
     * Constructor
     *
     * @param application
     */
    public PhotoelectricModule( PhotoelectricApplication application ) {
        super( "Photoelectric Effect", application.getClock() );

        // Set up the basic stuff
        AbstractClock clock = application.getClock();
        ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
        apparatusPanel.setPaintStrategy( ApparatusPanel2.OFFSCREEN_BUFFER_STRATEGY );
        apparatusPanel.setBackground( Color.white );
        setApparatusPanel( apparatusPanel );

        //----------------------------------------------------------------
        // Model
        //----------------------------------------------------------------

        // Set up the model
        PhotoelectricModel model = new PhotoelectricModel( clock );
        setModel( model );
        setControlPanel( new ControlPanel( this ) );

        // Set the default work function for the target
        model.getTarget().setWorkFunction( PhotoelectricTarget.WORK_FUNCTIONS.get( PhotoelectricTarget.SODIUM ) );

        //----------------------------------------------------------------
        // View
        //----------------------------------------------------------------
        CollimatedBeam beam = model.getBeam();

        // Add a graphic for the tube
        addTubeGraphic( model, getApparatusPanel() );

        // Add a graphic for the beam
        addBeamGraphic( beam );

        // Add a listener that will place photons right next to the plate when we are in beam view mode
        beam.addPhotonEmittedListener( new PhotonPlacementManager() );

        // Add a listener that will produce photon graphics for the beam and take them away when the photons
        // leave the system
        beam.addPhotonEmittedListener( new PhotonGraphicManager( this ) );

        // Add a listener to the target plate that will create electron graphics when electrons
        // are produced, and remove them when they the electrons leave the system.
        PhotoelectricTarget target = model.getTarget();
        target.addListener( new ElectronGraphicManager( this ) );

        // Add the battery and wire graphic
        addCircuitGraphic( apparatusPanel );

        // Add a graphic for the target plate
        addTargetGraphic( model, apparatusPanel );

        // Add a graphic for the anode
        addAnodeGraphic( model, apparatusPanel );

        // Put a mask over the part of the light beam that is to the left of the target
        Rectangle mask = new Rectangle( 0, 0, DischargeLampsConfig.CATHODE_LOCATION.x, 2000 );
        PhetShapeGraphic maskGraphic = new PhetShapeGraphic( getApparatusPanel(),
                                                             mask,
                                                             getApparatusPanel().getBackground() );
        getApparatusPanel().addGraphic( maskGraphic, PhotoelectricConfig.BEAM_LAYER + 1 );

        // Create current vs voltage graph
        GraphWindow graphWindow = new GraphWindow( application.getPhetFrame(),
                                                   clock,
                                                   getPhotoelectricModel() );
        graphWindow.setVisible( false );

        // Add a listener to the model that will flip the battery image when the voltage
        // changes sign
        model.addChangeListener( new BatteryImageFlipper() );

        //----------------------------------------------------------------
        // Controls
        //----------------------------------------------------------------

        // Set up the control panel
        new PhotoelectricControlPanel( this, graphWindow );

        // Add a slider for the battery
        addGraphicBatteryControls();
        BeamControl beamControl = new BeamControl( getApparatusPanel(),
                                                   new Point( 375, 30 ),
                                                   model.getBeam(),
                                                   model.getBeam().getMaxPhotonsPerSecond() );
        getApparatusPanel().addGraphic( beamControl, 1E9 );

        //----------------------------------------------------------------
        // Total hack to get beam to look right when things come up. This should
        // be removed when BeamCurtainGraphic.update() is fixed
        //----------------------------------------------------------------
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                CollimatedBeam beam = getPhotoelectricModel().getBeam();
                beam.setPhotonsPerSecond( beam.getPhotonsPerSecond() );
            }
        } );

        //----------------------------------------------------------------
        // Debug
        //----------------------------------------------------------------

        // Add options menu item that will show current
        JMenu optionsMenu = application.getOptionsMenu();
        final JCheckBoxMenuItem currentDisplayMI = new JCheckBoxMenuItem( "Show meters" );
        optionsMenu.add( currentDisplayMI );

        final JDialog meterDlg = new JDialog( PhetApplication.instance().getPhetFrame(), false );

        final AmmeterView ammeterView = new AmmeterView( getPhotoelectricModel().getAmmeter() );
        final IntensityView intensityView = new IntensityView( getPhotoelectricModel().getBeamIntensityMeter() );
        JPanel meterPanel = new JPanel( new GridLayout( 2, 1 ) );
        meterDlg.setContentPane( meterPanel );
        meterPanel.add( ammeterView );
        meterPanel.add( intensityView );
        meterDlg.pack();
        currentDisplayMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                meterDlg.setVisible( currentDisplayMI.isSelected() );
            }
        } );

        // Slap an ammeter on the circuit, near the anode
        AmmeterViewGraphic avg = new AmmeterViewGraphic( getApparatusPanel(),
                                                         getPhotoelectricModel().getAmmeter(),
                                                         getPhotoelectricModel() );
        avg.setLocation( DischargeLampsConfig.ANODE_LOCATION.x - 100, DischargeLampsConfig.ANODE_LOCATION.y + 188 );
        getApparatusPanel().addGraphic( avg, PhotoelectricConfig.CIRCUIT_LAYER + 1 );



        // Add an option to randomize the electron velocities
        final JRadioButtonMenuItem uniformSpeedOption = new JRadioButtonMenuItem( "Uniform electron speeds" );
        optionsMenu.addSeparator();
        optionsMenu.add( uniformSpeedOption );
        uniformSpeedOption.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( uniformSpeedOption.isSelected() ) {
                    getPhotoelectricModel().getTarget().setUniformInitialElectronSpeedStrategy();
                }
            }
        } );
        final JRadioButtonMenuItem randomizedSpeedOption = new JRadioButtonMenuItem( "Randomized electron speeds" );
        optionsMenu.add( randomizedSpeedOption );
        randomizedSpeedOption.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( randomizedSpeedOption.isSelected() ) {
                    getPhotoelectricModel().getTarget().setRandomizedInitialElectronSpeedStrategy();
                }
            }
        } );
        optionsMenu.addSeparator();
        ButtonGroup speedOptionBtnGrp = new ButtonGroup();
        speedOptionBtnGrp.add( uniformSpeedOption );
        speedOptionBtnGrp.add( randomizedSpeedOption );
        uniformSpeedOption.setSelected( true );



        // Draw red dots on the beam source location and the middle of the target plate
        if( DEBUG ) {
            PhetShapeGraphic beamIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                                   new Ellipse2D.Double( beam.getPosition().getX(),
                                                                                         beam.getPosition().getY(),
                                                                                         10, 10 ),
                                                                   Color.red );
            getApparatusPanel().addGraphic( beamIndicator, 10000 );
            PhetShapeGraphic cathodIndicator = new PhetShapeGraphic( getApparatusPanel(),
                                                                     new Ellipse2D.Double( targetPlate.getPosition().getX(),
                                                                                           targetPlate.getPosition().getY(),
                                                                                           10, 10 ),
                                                                     Color.red );
            getApparatusPanel().addGraphic( cathodIndicator, 10000 );
        }
    }

    /**
     * @param beam
     */
    private void addBeamGraphic( CollimatedBeam beam ) {
        beamGraphic = new BeamCurtainGraphic( getApparatusPanel(), beam );
        getApparatusPanel().addGraphic( beamGraphic, PhotoelectricConfig.BEAM_LAYER );
        try {
            BufferedImage lampImg = ImageLoader.loadBufferedImage( PhotoelectricConfig.LAMP_IMAGE_FILE );
            // Make the lens on the lamp the same size as the beam
            AffineTransform scaleTx = AffineTransform.getScaleInstance( 100.0 / lampImg.getWidth(),
                                                                        beam.getWidth() / lampImg.getHeight() );
            AffineTransformOp scaleTxOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
            lampImg = scaleTxOp.filter( lampImg, null );

            Point2D rp = new Point2D.Double( lampImg.getWidth(), lampImg.getHeight() / 2 );
            AffineTransform atx = AffineTransform.getRotateInstance( beam.getAngle(), rp.getX(), rp.getY() );

            LampGraphic lampGraphic = new LampGraphic( beam, getApparatusPanel(), lampImg, atx );
            // todo: this is positioned with hard numbers. Fix it
            lampGraphic.setLocation( (int)beam.getPosition().getX() - 90, (int)beam.getPosition().getY() );
            getApparatusPanel().addGraphic( lampGraphic, PhotoelectricConfig.LAMP_LAYER );

            // Put a mask behind the lamp graphic to hide the beam or photons that start behind it
            Rectangle mask = new Rectangle( 0, 0, lampImg.getWidth(), lampImg.getHeight() );
            PhetShapeGraphic maskGraphic = new PhetShapeGraphic( getApparatusPanel(),
                                                                 mask,
                                                                 getApparatusPanel().getBackground() );
            maskGraphic.setTransform( atx );
            maskGraphic.setLocation( lampGraphic.getLocation() );
            getApparatusPanel().addGraphic( maskGraphic, PhotoelectricConfig.LAMP_LAYER - 1 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    /**
     * Places a slider and digital readout on the battery graphic
     */
    private void addGraphicBatteryControls() {
        Battery battery = getPhotoelectricModel().getBattery();
        final BatterySlider bSl = new BatterySlider( getApparatusPanel(), 80 /* track length */, battery );
        bSl.setMinimum( (int)-( battery.getMaxVoltage() ) );
        bSl.setMaximum( (int)( battery.getMaxVoltage() ) );
        bSl.setValue( (int)( 0 ) );
        bSl.addTick( bSl.getMinimum() );
        bSl.addTick( bSl.getMaximum() );
        bSl.addTick( 0 );
        int yBase = 490;
        bSl.setLocation( (int)DischargeLampsConfig.CATHODE_LOCATION.getX() + 174, yBase );
        getApparatusPanel().addGraphic( bSl, DischargeLampsConfig.CIRCUIT_LAYER + 10000 );

        final PhetGraphic batteryReadout = new BatteryReadout( getApparatusPanel(),
                                                               battery,
                                                               new Point( (int)DischargeLampsConfig.CATHODE_LOCATION.getX() + 194,
                                                                          yBase + 15 ),
                                                               35 );
        addGraphic( batteryReadout, DischargeLampsConfig.CIRCUIT_LAYER + 10000 );
    }

    /**
     * @param apparatusPanel
     */
    private void addCircuitGraphic( ApparatusPanel apparatusPanel ) {
        try {
            circuitImageA = ImageLoader.loadBufferedImage( "images/circuit-A.png" );
            circuitImageB = ImageLoader.loadBufferedImage( "images/circuit-B.png" );
            circuitImageA = scaleImage( circuitImageA );
            circuitImageB = scaleImage( circuitImageB );

            circuitGraphic = new PhetImageGraphic( getApparatusPanel() );
            circuitGraphic.setImage( circuitImageA );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        circuitGraphic.setRegistrationPoint( (int)( 124 * externalGraphicsScale ),
                                             (int)( 110 * externalGraphicsScale ) );
        circuitGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( circuitGraphic, PhotoelectricConfig.CIRCUIT_LAYER );
    }

    /**
     * Creates the tube, adds it to the model and creates a graphic for it
     *
     * @param model
     * @param apparatusPanel
     */
    private void addTubeGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        ResonatingCavity tube = model.getTube();
        ResonatingCavityGraphic tubeGraphic = new ResonatingCavityGraphic( getApparatusPanel(), tube );
        apparatusPanel.addGraphic( tubeGraphic, PhotoelectricConfig.TUBE_LAYER );
    }

    /**
     * @param model
     * @param apparatusPanel
     */
    private void addAnodeGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        PhetGraphic anodeGraphic = new PhotoelectricPlateGraphic( getApparatusPanel(),
                                                                  DischargeLampsConfig.CATHODE_LENGTH,
                                                                  model,
                                                                  PhotoelectricPlateGraphic.POSITIVE );
        anodeGraphic.setRegistrationPoint( (int)anodeGraphic.getBounds().getWidth(),
                                           (int)anodeGraphic.getBounds().getHeight() / 2 );

        anodeGraphic.setRegistrationPoint( 0, (int)anodeGraphic.getBounds().getHeight() / 2 );
        anodeGraphic.setLocation( DischargeLampsConfig.ANODE_LOCATION );
        apparatusPanel.addGraphic( anodeGraphic, PhotoelectricConfig.CIRCUIT_LAYER );
    }

    /**
     * @param model
     * @param apparatusPanel
     */
    private void addTargetGraphic( PhotoelectricModel model, ApparatusPanel apparatusPanel ) {
        targetPlate = model.getTarget();
        PlateGraphic targetGraphic = new PhotoelectricPlateGraphic( getApparatusPanel(),
                                                                    DischargeLampsConfig.CATHODE_LENGTH,
                                                                    model,
                                                                    PhotoelectricPlateGraphic.NEGATIVE );
        targetGraphic.setRegistrationPoint( (int)targetGraphic.getBounds().getWidth(),
                                            (int)targetGraphic.getBounds().getHeight() / 2 );

        targetGraphic.setLocation( DischargeLampsConfig.CATHODE_LOCATION );
        apparatusPanel.addGraphic( targetGraphic, PhotoelectricConfig.CIRCUIT_LAYER );

        // Add a layer on top of the electrode to represent the target material
        double materialOffsetY = 5;
        double materialThickness = 7;
        Rectangle2D material = new Rectangle2D.Double( targetGraphic.getBounds().getMaxX(),
                                                       targetGraphic.getBounds().getMinY() + materialOffsetY,
                                                       materialThickness,
                                                       targetGraphic.getBounds().getHeight() - 2 * materialOffsetY );
        Color color = (Color)TARGET_COLORS.get( targetPlate.getMaterial() );
        final PhetShapeGraphic targetMaterialGraphic = new PhetShapeGraphic( getApparatusPanel(), material, color );
        getApparatusPanel().addGraphic( targetMaterialGraphic, PhotoelectricConfig.CIRCUIT_LAYER );

        // Add a listener to the target that will set the proper color if the material changes
        model.addChangeListener( new PhotoelectricModel.ChangeListenerAdapter() {
            public void targetMaterialChanged( PhotoelectricModel.ChangeEvent event ) {
                targetMaterialGraphic.setPaint( (Paint)TARGET_COLORS.get( targetPlate.getMaterial() ) );
            }
        } );
    }

    //----------------------------------------------------------------
    // Utility methods
    //----------------------------------------------------------------

    /**
     * Returns a typed reference to the model
     */
    private PhotoelectricModel getPhotoelectricModel() {
        return (PhotoelectricModel)getModel();
    }

    /**
     * Scales an image graphic so it appears properly on the screen. This method depends on the image used by the
     * graphic to have been created at the same scale as the battery-wires graphic. The scale is based on the
     * distance between the electrodes in that image and the screen distance between the electrodes specified
     * in the configuration file.
     *
     * @param imageGraphic
     */
    private void scaleImageGraphic( PhetImageGraphic imageGraphic ) {
        if( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( DischargeLampsConfig.ANODE_LOCATION,
                                           DischargeLampsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        imageGraphic.setImage( externalGraphicScaleOp.filter( imageGraphic.getImage(), null ) );
    }

    /**
     * Scales a BufferedImage so it appears properly on the screen. This method depends on the image used by the
     * graphic to have been created at the same scale as the battery-wires graphic. The scale is based on the
     * distance between the electrodes in that image and the screen distance between the electrodes specified
     * in the configuration file.
     *
     * @param image
     */
    private BufferedImage scaleImage( BufferedImage image ) {
        if( externalGraphicScaleOp == null ) {
            int cathodeAnodeScreenDistance = 550;
            determineExternalGraphicScale( DischargeLampsConfig.ANODE_LOCATION,
                                           DischargeLampsConfig.CATHODE_LOCATION,
                                           cathodeAnodeScreenDistance );
            AffineTransform scaleTx = AffineTransform.getScaleInstance( externalGraphicsScale, externalGraphicsScale );
            externalGraphicScaleOp = new AffineTransformOp( scaleTx, AffineTransformOp.TYPE_BILINEAR );
        }
        return externalGraphicScaleOp.filter( image, null );
    }

    /**
     * Computes the scale to be applied to externally created graphics.
     * <p/>
     * Scale is determined by specifying a distance in the external graphics that should
     * be the same as the distance between two point on the screen.
     *
     * @param p1
     * @param p2
     * @param externalGraphicDist
     */
    private void determineExternalGraphicScale( Point p1, Point p2, int externalGraphicDist ) {
        externalGraphicsScale = p1.distance( p2 ) / externalGraphicDist;
    }

    //----------------------------------------------------------------
    // State/mode setters and getters
    //----------------------------------------------------------------

    /**
     * Toggles the view of the light between beam view and photon view
     *
     * @param isEnabled
     */
    public void setPhotonViewEnabled( boolean isEnabled ) {
        viewType = isEnabled ? PHOTON_VIEW : BEAM_VIEW;
        beamGraphic.setVisible( !isEnabled );
    }

    public boolean getPhotonViewEnabled() {
        return viewType == PHOTON_VIEW;
    }

    //----------------------------------------------------------------
    // Inner classes for event handling
    //----------------------------------------------------------------

    private class BatteryImageFlipper extends PhotoelectricModel.ChangeListenerAdapter {
        public void voltageChanged( PhotoelectricModel.ChangeEvent event ) {
            PhotoelectricModel model = event.getPhotoelectricModel();
            if( model.getVoltage() > 0 && circuitGraphic.getImage() != circuitImageA ) {
                circuitGraphic.setImage( circuitImageA );
            }
            else if( model.getVoltage() < 0 && circuitGraphic.getImage() != circuitImageB ) {
                circuitGraphic.setImage( circuitImageB );
            }
        }
    }

    /**
     * Modifies the initial placement of photons to be very near the target when we're in
     * beam view. This prevents the delay in response of the target when the wavelength or
     * intensity of the beam is changed.
     */
    private class PhotonPlacementManager implements PhotonEmittedListener {
        public void photonEmittedEventOccurred( PhotonEmittedEvent event ) {
            if( viewType == BEAM_VIEW ) {
                Photon photon = event.getPhoton();
                Line2D photonPath = new Line2D.Double( photon.getPosition().getX(),
                                                       photon.getPosition().getY(),
                                                       photon.getPosition().getX() + photon.getVelocity().getX(),
                                                       photon.getPosition().getY() + photon.getVelocity().getY() );
                Point2D p = MathUtil.getLinesIntersection( photonPath.getP1(), photonPath.getP2(),
                                                           targetPlate.getEndpoints()[0], targetPlate.getEndpoints()[1] );
                photon.setPosition( p.getX() - photon.getVelocity().getX(),
                                    p.getY() - photon.getVelocity().getY() );
            }
        }
    }
}
