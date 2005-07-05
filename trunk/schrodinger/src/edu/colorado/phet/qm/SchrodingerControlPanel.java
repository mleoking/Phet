/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.ModelViewTransform1D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.AdvancedPanel;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.components.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphicListener;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.potentials.SimpleGradientPotential;
import edu.colorado.phet.qm.model.propagators.*;
import edu.colorado.phet.qm.phetcommon.IntegralModelElement;
import edu.colorado.phet.qm.view.ColorMap;
import edu.colorado.phet.qm.view.SchrodingerPanel;
import edu.colorado.phet.qm.view.colormaps.ImaginaryGrayColorMap;
import edu.colorado.phet.qm.view.colormaps.MagnitudeInGrayscale;
import edu.colorado.phet.qm.view.colormaps.RealGrayColorMap;
import edu.colorado.phet.qm.view.colormaps.VisualColorMap;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:51:18 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SchrodingerControlPanel extends ControlPanel {
    private SchrodingerModule module;
    private ModelElement particleFirer;
    private CylinderWaveControl cylinderWaveBox;
    private FiniteDifferencePropagator2ndOrder classicalPropagator2ndOrder;
    private InitialConditionPanel initialConditionPanel;

    public SchrodingerControlPanel( final SchrodingerModule module ) {
        super( module );
        this.module = module;
        JButton reset = new JButton( "Reset" );
        reset.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        addControl( reset );
        initialConditionPanel = createInitialConditionPanel();
        AdvancedPanel advancedIC = new AdvancedPanel( "Show>>", "Hide<<" );
        advancedIC.addControlFullWidth( initialConditionPanel );
        advancedIC.setBorder( BorderFactory.createTitledBorder( "Initial Conditions" ) );
        advancedIC.addListener( new AdvancedPanel.Listener() {

            public void advancedPanelHidden( AdvancedPanel advancedPanel ) {
                JFrame parent = (JFrame)SwingUtilities.getWindowAncestor( SchrodingerControlPanel.this );
                parent.invalidate();
                parent.validate();
                parent.repaint();
            }

            public void advancedPanelShown( AdvancedPanel advancedPanel ) {
            }
        } );

//        addControlFullWidth( advancedIC );

        JButton fireParticle = new JButton( "Fire Particle" );
        fireParticle.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                fireParticle();
            }
        } );
//        addControl( fireParticle );

        try {
            HorizontalLayoutPanel hoPan = new HorizontalLayoutPanel();

            final JCheckBox ruler = new JCheckBox( "Ruler" );
            ImageIcon icon = new ImageIcon( ImageLoader.loadBufferedImage( "images/ruler-thumb.jpg" ) );
            hoPan.add( ruler );
            hoPan.add( new JLabel( icon ) );
            getSchrodingerPanel().getRulerGraphic().addPhetGraphicListener( new PhetGraphicListener() {
                public void phetGraphicChanged( PhetGraphic phetGraphic ) {
                }

                public void phetGraphicVisibilityChanged( PhetGraphic phetGraphic ) {
                    ruler.setSelected( getSchrodingerPanel().getRulerGraphic().isVisible() );
                }
            } );

            ruler.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    getSchrodingerPanel().setRulerVisible( ruler.isSelected() );
                }
            } );
            addControl( hoPan );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        VerticalLayoutPanel intensityScreen = new IntensityScreenPanel( this );
        addControlFullWidth( intensityScreen );

        VerticalLayoutPanel colorPanel = createVisualizationPanel();
        addControlFullWidth( colorPanel );

        VerticalLayoutPanel simulationPanel = createSimulationPanel( module );
        AdvancedPanel advSim = new AdvancedPanel( "Simulation >>", "Hide <<" );
        advSim.addControlFullWidth( simulationPanel );
        addControlFullWidth( advSim );
//        addControlFullWidth( simulationPanel );

        VerticalLayoutPanel potentialPanel = createPotentialPanel( module );
        addControlFullWidth( potentialPanel );

        VerticalLayoutPanel exp = createExpectationPanel();
        addControlFullWidth( exp );

        VerticalLayoutPanel interactionPanel = createDetectorPanel();
        addControlFullWidth( interactionPanel );

//        JButton addParticle = new JButton( "Add Particle" );
//        addParticle.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                Wavefunction newParticle = new Wavefunction( module.getDiscreteModel().getWavefunction().getWidth(), module.getDiscreteModel().getWavefunction().getHeight() );
//                Wave wave = new GaussianWave( new Point2D.Double( getDiscreteModel().getGridWidth() / 2,
//                                                                  getDiscreteModel().getGridHeight() * 0.8 ),
//                                              new Vector2D.Double( 0, -0.25 ), 4 );
//                new WaveSetup( wave ).initialize( newParticle );
//                module.getDiscreteModel().getWavefunction().add( newParticle );
//            }
//        } );
//        addControl( addParticle );

        VerticalLayoutPanel boundaryPanel = createBoundaryPanel();
        addControlFullWidth( boundaryPanel );

        VerticalLayoutPanel propagatorPanel = createPropagatorPanel();
        AdvancedPanel advancedPropagatorPanel = new AdvancedPanel( "Propagators>>", "Hide Propagators<<" );
        advancedPropagatorPanel.addControlFullWidth( propagatorPanel );
        addControlFullWidth( advancedPropagatorPanel );

//        VerticalLayoutPanel intensityPanel = createIntensityPanel();
//        addControlFullWidth( intensityPanel );

        ModelElement ap = new AddParticle( module, getWaveSetup() );

        particleFirer = new IntegralModelElement( ap, 32 );

//        JButton printWaveform = new JButton( "Print Waveform" );
//        printWaveform.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//
//            }
//        } );

        final JSlider speed = new JSlider( JSlider.HORIZONTAL, 0, 1000, (int)( 1000 * 0.1 ) );
        speed.setBorder( BorderFactory.createTitledBorder( "Classical Wave speed" ) );
        speed.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double x = new ModelViewTransform1D( 0, 0.5, speed.getMinimum(), speed.getMaximum() ).viewToModel( speed.getValue() );
                System.out.println( "x = " + x );
                classicalPropagator2ndOrder.setSpeed( x );
            }
        } );
//        addControlFullWidth( speed );
    }

    private WaveSetup getWaveSetup() {
        return initialConditionPanel.getWaveSetup();
    }

//    private VerticalLayoutPanel createIntensityPanel() {
//
//        VerticalLayoutPanel verticalLayoutPanel = new VerticalLayoutPanel();
//        final JCheckBox gun = new JCheckBox( "Gun" );
//        gun.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                setGunActive( gun.isSelected() );
//            }
//        } );
//        verticalLayoutPanel.add( gun );
//        return verticalLayoutPanel;
//    }
//
//
//    private void setGunActive( boolean selected ) {
//        if( selected ) {
//            module.getModel().addModelElement( particleFirer );
//        }
//        else {
//            module.getModel().removeModelElement( particleFirer );
//        }
//        module.setGunActive( selected );
//    }

    private VerticalLayoutPanel createPropagatorPanel() {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Propagator" ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton richardson = createPropagatorButton( buttonGroup, "Richardson", new RichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        layoutPanel.add( richardson );

        JRadioButton modified = createPropagatorButton( buttonGroup, "Modified Richardson", new ModifiedRichardsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        layoutPanel.add( modified );

        JRadioButton crank = createPropagatorButton( buttonGroup, "Crank-Nicholson?", new CrankNicholsonPropagator( getDiscreteModel().getDeltaTime(), getDiscreteModel().getBoundaryCondition(), getDiscreteModel().getPotential() ) );
        layoutPanel.add( crank );

        JRadioButton light = createPropagatorButton( buttonGroup, "Avg", new AveragePropagator() );
        layoutPanel.add( light );

        classicalPropagator2ndOrder = new FiniteDifferencePropagator2ndOrder( getDiscreteModel().getPotential() );
        JRadioButton lap = createPropagatorButton( buttonGroup, "finite difference", classicalPropagator2ndOrder );
        layoutPanel.add( lap );
        lap.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                initClassicalWave( classicalPropagator2ndOrder );
            }
        } );
        return layoutPanel;
    }

    private void initClassicalWave( FiniteDifferencePropagator2ndOrder propagator2ndOrder ) {
        initialConditionPanel.initClassicalWave( propagator2ndOrder );
    }

    private JRadioButton createPropagatorButton( ButtonGroup buttonGroup, String s, final Propagator propagator ) {

        JRadioButton radioButton = new JRadioButton( s );
        buttonGroup.add( radioButton );
        if( getDiscreteModel().getPropagator().getClass().equals( propagator.getClass() ) ) {
            radioButton.setSelected( true );
        }
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getDiscreteModel().setPropagator( propagator );
            }
        } );
        return radioButton;
    }

    private VerticalLayoutPanel createBoundaryPanel() {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Boundary Condition" ) );

        layoutPanel.add( createPlaneWaveBox() );
        cylinderWaveBox = createCylinderWaveBox();
        layoutPanel.add( cylinderWaveBox );

        return layoutPanel;
    }

    private JCheckBox createPlaneWaveBox() {
        final JCheckBox planeWaveCheckbox = new JCheckBox( "Plane Wave" );
        double scale = 1.0;
        double k = 1.0 / 10.0 * Math.PI * scale;
        final PlaneWave planeWave = new PlaneWave( k, getDiscreteModel().getGridWidth() );

        planeWave.setMagnitude( 0.015 );
        int damping = getDiscreteModel().getDamping().getDepth();
        int tubSize = 5;
        final Rectangle rectangle = new Rectangle( damping, getWavefunction().getHeight() - damping - tubSize,
                                                   getWavefunction().getWidth() - 2 * damping, tubSize );
        final WaveSource waveSource = new WaveSource( rectangle, planeWave );

        planeWaveCheckbox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( planeWaveCheckbox.isSelected() ) {
                    getDiscreteModel().addListener( waveSource );
                }
                else {
                    getDiscreteModel().removeListener( waveSource );
                }
            }
        } );
        return planeWaveCheckbox;
    }

    private CylinderWaveControl createCylinderWaveBox() {
        return new CylinderWaveControl( module, getDiscreteModel() );
    }

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    private VerticalLayoutPanel createDetectorPanel() {
        return new DetectorPanel( module );
    }

    private VerticalLayoutPanel createExpectationPanel() {
        VerticalLayoutPanel lay = new VerticalLayoutPanel();
        lay.setBorder( BorderFactory.createTitledBorder( "Observables" ) );
        final JCheckBox x = new JCheckBox( "<X>" );
        x.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayXExpectation( x.isSelected() );
            }
        } );
        lay.add( x );

        final JCheckBox y = new JCheckBox( "<Y>" );
        y.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayYExpectation( y.isSelected() );
            }
        } );
        lay.add( y );

        final JCheckBox c = new JCheckBox( "collapse-to" );
        c.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setDisplayCollapsePoint( c.isSelected() );
            }
        } );
        lay.add( c );

        return lay;
    }

    private InitialConditionPanel createInitialConditionPanel() {
        return new InitialConditionPanel( this );
    }

    private VerticalLayoutPanel createPotentialPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel layoutPanel = new VerticalLayoutPanel();
        layoutPanel.setBorder( BorderFactory.createTitledBorder( "Potential" ) );

        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearPotential();
            }
        } );
        layoutPanel.add( clear );

        final HorizontalDoubleSlit doubleSlitPotential = createDoubleSlit();
        final JCheckBox doubleSlit = new JCheckBox( "Double Slit", false );
        doubleSlit.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( doubleSlit.isSelected() ) {
                    addPotential( doubleSlitPotential );
                }
                else {
                    removePotential( doubleSlitPotential );
                }
            }
        } );
        layoutPanel.add( doubleSlit );
        VerticalLayoutPanel configureDoubleSlit = new ConfigureHorizontalSlitPanel( doubleSlitPotential );
        layoutPanel.add( configureDoubleSlit );

        JButton slopingLeft = new JButton( "Add Slope" );
        slopingLeft.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                addPotential( createSlopingPotential() );
            }
        } );
        layoutPanel.add( slopingLeft );

        JButton newBarrier = new JButton( "Add Barrier" );
        newBarrier.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.addPotential();
            }
        } );
        layoutPanel.add( newBarrier );

        return layoutPanel;
    }

    private void removePotential( Potential potential ) {
        getSchrodingerPanel().getDiscreteModel().removePotential( potential );
    }

    private void clearPotential() {
        module.getDiscreteModel().clearPotential();
        getSchrodingerPanel().clearPotential();
    }

    private Potential createSlopingPotential() {
        return new SimpleGradientPotential( 0.01 );
    }

    private HorizontalDoubleSlit createDoubleSlit() {

        double potentialValue = 200E12;
        HorizontalDoubleSlit doubleSlit = new HorizontalDoubleSlit( getDiscreteModel().getGridWidth(),
                                                                    getDiscreteModel().getGridHeight(),
                                                                    (int)( getDiscreteModel().getGridWidth() * 0.4 ), 10, 5, 10, potentialValue );
        return doubleSlit;
    }

    private void addPotential( Potential potential ) {
        getSchrodingerPanel().getDiscreteModel().addPotential( potential );
    }

    private VerticalLayoutPanel createSimulationPanel( final SchrodingerModule module ) {
        VerticalLayoutPanel simulationPanel = new VerticalLayoutPanel();
        simulationPanel.setBorder( BorderFactory.createTitledBorder( "Simulation" ) );

        final JSpinner gridWidth = new JSpinner( new SpinnerNumberModel( getDiscreteModel().getGridWidth(), 1, 1000, 10 ) );
        gridWidth.setBorder( BorderFactory.createTitledBorder( "Resolution" ) );
        gridWidth.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int val = ( (Integer)gridWidth.getValue() ).intValue();
                module.setGridSpacing( val, val );
//                addPotential( new ConstantPotential( 0.0 ) );
            }
        } );
        simulationPanel.addFullWidth( gridWidth );

        final double origDT = getDiscreteModel().getDeltaTime();
        System.out.println( "origDT = " + origDT );
        final JSpinner timeStep = new JSpinner( new SpinnerNumberModel( 0.8, 0, 2, 0.1 ) );
        timeStep.setBorder( BorderFactory.createTitledBorder( "DT" ) );

        timeStep.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double t = ( (Number)timeStep.getValue() ).doubleValue();
                getDiscreteModel().setDeltaTime( t );
            }
        } );
        simulationPanel.addFullWidth( timeStep );
        return simulationPanel;
    }

    private VerticalLayoutPanel createVisualizationPanel() {
        VerticalLayoutPanel colorPanel = new VerticalLayoutPanel();
        colorPanel.setBorder( BorderFactory.createTitledBorder( "Color Scheme" ) );
        ButtonGroup buttonGroup = new ButtonGroup();

        JRadioButton visualTM = createVisualizationButton( "Rainbow", new VisualColorMap( getSchrodingerPanel() ), true, buttonGroup );
        colorPanel.addFullWidth( visualTM );

        JRadioButton grayMag = createVisualizationButton( "Magnitude-Gray", new MagnitudeInGrayscale( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( grayMag );

        JRadioButton realGray = createVisualizationButton( "Real-Gray", new RealGrayColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( realGray );

        JRadioButton complexGray = createVisualizationButton( "Imaginary-Gray", new ImaginaryGrayColorMap( getSchrodingerPanel() ), false, buttonGroup );
        colorPanel.addFullWidth( complexGray );

//        JRadioButton blackBackground = createVisualizationButton( "HSB on Black", new DefaultColorMap( getSchrodingerPanel() ), false, buttonGroup );
//        colorPanel.addFullWidth( blackBackground );
//
//        JRadioButton whiteBackground = createVisualizationButton( "HSB on White", new DefaultWhiteColorMap( getSchrodingerPanel() ), false, buttonGroup );
//        colorPanel.addFullWidth( whiteBackground );
        return colorPanel;
    }

    private JRadioButton createVisualizationButton( String s, final ColorMap colorMap, boolean b, ButtonGroup buttonGroup ) {
        JRadioButton radioButton = new JRadioButton( s );
        radioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                getSchrodingerPanel().getWavefunctionGraphic().setWavefunctionColorMap( colorMap );
            }
        } );
        buttonGroup.add( radioButton );
        radioButton.setSelected( b );
        return radioButton;
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return module.getSchrodingerPanel();
    }

    public void fireParticle() {
        WaveSetup waveSetup = getWaveSetup();
        module.fireParticle( waveSetup );
    }

    public DiscreteModel getDiscreteModel() {
        return module.getDiscreteModel();
    }

    public SchrodingerModule getModule() {
        return module;
    }
}
