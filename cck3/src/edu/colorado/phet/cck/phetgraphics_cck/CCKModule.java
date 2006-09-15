/** Sam Reid*/
package edu.colorado.phet.cck.phetgraphics_cck;

import edu.colorado.phet.cck.*;
import edu.colorado.phet.cck.chart.AbstractFloatingChart;
import edu.colorado.phet.cck.chart.CCKTime;
import edu.colorado.phet.cck.chart.CurrentStripChart;
import edu.colorado.phet.cck.chart.VoltageStripChart;
import edu.colorado.phet.cck.model.*;
import edu.colorado.phet.cck.model.analysis.CircuitSolver;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.CircuitGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.cck.phetgraphics_cck.circuit.toolbox.Toolbox;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common_cck.application.Module;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.model.BaseModel;
import edu.colorado.phet.common_cck.model.ModelElement;
import edu.colorado.phet.common_cck.view.components.AspectRatioPanel;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:27:42 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class CCKModule extends Module implements ICCKModule {
    /**
     * General module data
     */
    private CCKModel model;
    private CCKParameters parameters;
    private CCKImageSuite imageSuite;
    private CCKControlPanel cckControlPanel;
    private DecimalFormat decimalFormat = new DecimalFormat( "0.0#" );
    private boolean electronsVisible = true;
    public static final Color backgroundColor = new Color( 200, 240, 200 );
//    public static final Color toolboxColor = new Color( 241, 241, 241 );

    /**
     * PhetGraphics-specific data
     */
    private CCKApparatusPanel cckApparatusPanel;

    /* Aspect ratio panel used in single-module setups*/
    private AspectRatioPanel aspectRatioPanel;

    /**
     * Piccolo-specific data
     */
    private PNode stopwatch;

    public CCKModule( String[] args ) throws IOException {
        super( SimStrings.get( "ModuleTitle.CCK3Module" ) );
        this.parameters = new CCKParameters( this, args );
        setModel( new BaseModel() );
        model = new CCKModel();

        imageSuite = new CCKImageSuite();

        cckApparatusPanel = new CCKApparatusPanel( this, model );
        setApparatusPanel( cckApparatusPanel );

        stripChartClock.start();
        setupStopwatch();

        // Create a BaseModel that will get clock ticks and add a model element to
        // it that will tell the CCKModel to step
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                model.stepInTime( dt );
            }
        } );


        setResistivityEnabled( true );
        setInternalResistanceOn( true );

        aspectRatioPanel = new AspectRatioPanel( getApparatusPanel(), 5, 5, 1.2 );
    }

    public void initControlPanel( edu.colorado.phet.common.application.Module module ) {
        this.cckControlPanel = new CCKControlPanel( this, module );
        setControlPanel( cckControlPanel );
    }

    private void setupStopwatch() {
        stopwatch = new MyPhetPNode( cckApparatusPanel, new PSwing( getApparatusPanel(), new StopwatchDecorator( stripChartClock, 1.0 * CCKTime.scale, "s" ) ) );
        stopwatch.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatch.addInputEventListener( new PDragEventHandler() );
        getApparatusPanel().addScreenChild( stopwatch );
        stopwatch.setOffset( 150, 150 );
        stopwatch.setVisible( false );
    }

    public RectangleRepaintApparatusPanel getCCKApparatusPanel() {
        return cckApparatusPanel;
    }

    public boolean isElectronsVisible() {
        return electronsVisible;
    }

    public void layoutElectrons( Branch[] branches ) {
        model.layoutElectrons( branches );
    }

    public Rectangle getViewBounds() {
        return new Rectangle( 0, 0, getApparatusPanel().getWidth(), getApparatusPanel().getHeight() );
    }

    public CircuitSolver getCircuitSolver() {
        return model.getCircuitSolver();
    }

    public CircuitChangeListener getCircuitChangeListener() {
        return model.getCircuitChangeListener();
    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        cckApparatusPanel.setHelpEnabled( h );
        getApparatusPanel().repaint();
    }


    public void activate( PhetApplication app ) {
        super.activate( app );
        //Uncomment this code to correct the aspect ratio in PhetGraphics code
//        this.aspectRatioPanel = new AspectRatioPanel( getApparatusPanel(), 5, 5, model.getAspectRatio() );
//        this.aspectRatioPanel.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                relayout();
//            }
//        } );
//        app.getApplicationView().getBasicPhetPanel().setApparatusPanelContainer( aspectRatioPanel );
    }

    public Circuit getCircuit() {
        return model.getCircuit();
    }

    public CCKImageSuite getImageSuite() {
        return imageSuite;
    }

    public CircuitGraphic getCircuitGraphic() {
        return cckApparatusPanel.getCircuitGraphic();
    }

    public ParticleSetGraphic getParticleSetGraphic() {
        return getCircuitGraphic().getParticleSetGraphic();
    }

    public ParticleSet getParticleSet() {
        return model.getParticleSet();
    }

    public void setZoom( double scale ) {
        cckApparatusPanel.setZoom( scale );
    }

    public void setVirtualAmmeterVisible( boolean visible ) {
        cckApparatusPanel.setVirtualAmmeterVisible( visible );
    }

    public void removeParticlesAndGraphics( Branch branch ) {
        Electron[] out = getParticleSet().removeParticles( branch );
        getCircuitGraphic().getParticleSetGraphic().removeGraphics( out );
    }

    public void removeBranch( Branch branch ) {
        getCircuit().setFireKirkhoffChanges( false );
        removeParticlesAndGraphics( branch );
        getCircuitGraphic().removeGraphic( branch );
        getCircuit().removeBranch( branch );

        //see if the adjacent junctions are free.
        testRemove( branch.getStartJunction() );
        testRemove( branch.getEndJunction() );

        //see if the adjacent junctions remain, and should be converted to component ends (for rotation)
        //if the junction remains, and it has exactly one connection, which is of type CircuitComponent.
        cckApparatusPanel.convertJunctionGraphic( branch.getStartJunction() );
        cckApparatusPanel.convertJunctionGraphic( branch.getEndJunction() );
        getCircuit().setFireKirkhoffChanges( true );
        getCircuit().fireKirkhoffChanged();
        getApparatusPanel().repaint();
    }

    private void testRemove( Junction st ) {
        Branch[] out = getCircuit().getAdjacentBranches( st );
        if( out.length == 0 ) {
            getCircuitGraphic().removeGraphic( st );
            getCircuit().removeJunction( st );
        }
    }

    public BufferedImage loadBufferedImage( String s ) throws IOException {
        return imageSuite.getImageLoader().loadImage( s );
    }

    public void setVoltmeterVisible( boolean visible ) {
        cckApparatusPanel.setVoltmeterVisible( visible );
    }

    public void clear() {
        getCircuit().clear();
    }

    public void setLifelike( boolean lifelike ) {
        cckApparatusPanel.setLifelike( lifelike );
        getCircuit().fireBranchesMoved( getCircuit().getBranches() );
        getCircuit().fireKirkhoffChanged();

    }

    public boolean isLifelike() {
        return getCircuitGraphic().isLifelike();
    }

    public void showMegaHelp() {
        cckControlPanel.showHelpImage();
    }

    public Toolbox getToolbox() {
        return cckApparatusPanel.getToolbox();
    }

    public void setCircuit( Circuit newCircuit ) {
        getCircuit().setCircuit( newCircuit );
        for( int i = 0; i < getCircuit().numBranches(); i++ ) {
            getCircuitGraphic().addGraphic( getCircuit().branchAt( i ) );
        }
        //        circuitGraphic.fixJunctionGraphics();
        layoutElectrons( getCircuit().getBranches() );
        getCircuitSolver().apply( getCircuit() );
        getCircuitGraphic().reapplySolderGraphics();
        getApparatusPanel().repaint();
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void deleteSelection() {
        Branch[] sel = getCircuit().getSelectedBranches();
        for( int i = 0; i < sel.length; i++ ) {
            Branch branch = sel[i];
            removeBranch( branch );
        }
    }

    public void desolderSelection() {
        Junction[] sel = getCircuit().getSelectedJunctions();
        for( int i = 0; i < sel.length; i++ ) {
            Junction junction = sel[i];
            int numConnections = getCircuit().getNeighbors( junction ).length;
            if( numConnections > 1 ) {
                getCircuitGraphic().split( junction );
            }
        }
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        getToolbox().setSeriesAmmeterVisible( selected );
        getApparatusPanel().repaint();
    }

    public void selectAll() {
        getCircuit().selectAll();
    }

    public void resetDynamics() {
        getCircuit().resetDynamics();
    }

    public void clockTickFinished() {
        if( cckApparatusPanel != null ) {
            cckApparatusPanel.synchronizeImmediately();
        }
    }

    public PhetShadowTextGraphic getTimescaleGraphic() {
        return cckApparatusPanel.getTimescaleGraphic();
    }

    public void regainFocus() {
        Window window = SwingUtilities.getWindowAncestor( getApparatusPanel() );
        window.requestFocus();
        getApparatusPanel().requestFocus();
    }

    public Shape getElectronClip() {
        return cckApparatusPanel.getElectronClip();
    }

    public void recomputeElectronClip() {
        cckApparatusPanel.recomputeElectronClip();
    }

    SwingClock stripChartClock = new SwingClock( 30, 1 );

    public void addCurrentChart() {
        stripChartClock.start();
        final CurrentStripChart chart = new CurrentStripChart( getApparatusPanel(), "Current (Amps)", stripChartClock, getCircuitGraphic() );
        chart.setOffset( 200, 200 );
        getApparatusPanel().addScreenChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                getApparatusPanel().removeScreenChild( chart );
            }
        } );
    }

    public void addVoltageChart() {
        stripChartClock.start();
        final VoltageStripChart chart = new VoltageStripChart( getApparatusPanel(), "Current", stripChartClock, getCircuitGraphic() );
        chart.setOffset( 250, 400 );
        getApparatusPanel().addScreenChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                getApparatusPanel().removeScreenChild( chart );
            }
        } );
    }

    public void setAllReadoutsVisible( boolean r ) {
        getCircuitGraphic().setAllReadoutsVisible( r );
    }

    public boolean isStopwatchVisible() {
        return stopwatch.getVisible();
    }

    public void setStopwatchVisible( boolean visible ) {
        stopwatch.setVisible( visible );
    }

    public void setElectronsVisible( boolean visible ) {
        this.electronsVisible = visible;
        if( model.getElectronLayout() != null && getCircuit() != null && getApparatusPanel() != null ) {
            model.getElectronLayout().branchesMoved( getCircuit().getBranches() );
            getApparatusPanel().repaint();
        }
    }

    public Rectangle2D getModelBounds() {
        return getTransform().getModelBounds();
    }

    private void add( Branch branch ) {
        getCircuit().addBranch( branch );
        getCircuitGraphic().addGraphic( branch );
    }

    public void addTestCircuit() {
        Branch b1 = getToolbox().getBatterySource().createBranch();
        add( b1 );
        Branch b2 = getToolbox().getResistorSource().createBranch();
        add( b2 );
        Branch b3 = getToolbox().getWireSource().createBranch();
        add( b3 );

        new BranchSet( getCircuit(), new Branch[]{b1} ).translate( -5, 0 );
        edu.colorado.phet.common.math.Vector2D.Double dv = new edu.colorado.phet.common.math.Vector2D.Double( b2.getStartJunction().getPosition(), b1.getEndJunction().getPosition() );
        new BranchSet( getCircuit(), new Branch[]{b2} ).translate( dv );
        b3.getStartJunction().setPosition( b2.getEndJunction().getX(), b2.getEndJunction().getY() );
        b3.getEndJunction().setPosition( b1.getStartJunction().getX(), b1.getStartJunction().getY() );
        layoutElectrons( new Branch[]{b1, b2, b3} );
        getCircuitGraphic().collapseJunctions( b1.getEndJunction(), b2.getStartJunction() );
        getCircuitGraphic().collapseJunctions( b2.getEndJunction(), b3.getStartJunction() );
        getCircuitGraphic().collapseJunctions( b3.getEndJunction(), b1.getStartJunction() );
    }

    public CCKModel getCCKModel() {
        return model;
    }

    public void setResistivityEnabled( boolean selected ) {
        model.setResistivityEnabled( selected );
    }

    public ResistivityManager getResistivityManager() {
        return model.getResistivityManager();
    }

    public boolean isInternalResistanceOn() {
        return model.isInternalResistanceOn();
    }

    public void setInternalResistanceOn( boolean selected ) {
        model.setInternalResistanceOn( selected );
        updateReadoutGraphics();
        if( !selected ) {
            //hide all the internal resistance editors
            ArrayList batteryMenus = CircuitComponentInteractiveGraphic.BatteryJMenu.instances;
            for( int i = 0; i < batteryMenus.size(); i++ ) {
                CircuitComponentInteractiveGraphic.BatteryJMenu batteryJMenu = (CircuitComponentInteractiveGraphic.BatteryJMenu)batteryMenus.get( i );
                batteryJMenu.getResistanceEditor().setVisible( false );
            }
        }
    }

    private void updateReadoutGraphics() {
        cckApparatusPanel.updateReadoutGraphics();
    }

    public CCKParameters getParameters() {
        return parameters;
    }

    public JComponent getSimulationPanel() {
        return getApparatusPanel();
    }

    public CCKControlPanel getCCKControlPanel() {
        return cckControlPanel;
    }

    public ModelViewTransform2D getTransform() {
        return cckApparatusPanel.getTransform();
    }

    public void applicationStarted() {
    }
}
