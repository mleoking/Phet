// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.InjectorNode;
import edu.colorado.phet.common.piccolophet.nodes.ResetAllButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.background.OutsideBackgroundNode;
import edu.colorado.phet.fluidpressureandflow.common.view.EnglishRuler;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowCanvas;
import edu.colorado.phet.fluidpressureandflow.common.view.FluidPressureAndFlowControlPanelNode;
import edu.colorado.phet.fluidpressureandflow.common.view.MeterStick;
import edu.colorado.phet.fluidpressureandflow.flow.FluidFlowModule;
import edu.colorado.phet.fluidpressureandflow.flow.model.FluidFlowModel;
import edu.colorado.phet.fluidpressureandflow.flow.model.Particle;
import edu.colorado.phet.fluidpressureandflow.pressure.model.Pool;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Strings.DOTS;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.ENGLISH;
import static edu.colorado.phet.fluidpressureandflow.common.model.units.UnitSet.METRIC;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.BACKGROUND;
import static edu.colorado.phet.fluidpressureandflow.pressure.view.FluidPressureControlPanel.FOREGROUND;

/**
 * Canvas for the "Flow" tab.
 *
 * @author Sam Reid
 */
public class FluidFlowCanvas extends FluidPressureAndFlowCanvas<FluidFlowModel> {
    private final PNode particleLayer;
    private final PNode foodColoringLayer;

    private static final double MODEL_HEIGHT = Pool.DEFAULT_HEIGHT * 3.2;
    private static final double PIPE_CENTER_Y = -2;
    private static final double MODEL_WIDTH = MODEL_HEIGHT / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();

    public FluidFlowCanvas( final FluidFlowModule module ) {
        super( ModelViewTransform.createRectangleInvertedYMapping( new Rectangle2D.Double( -MODEL_WIDTH / 2, -MODEL_HEIGHT / 2 + PIPE_CENTER_Y + 0.75, MODEL_WIDTH, MODEL_HEIGHT ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ) ) );

        addChild( new OutsideBackgroundNode( transform, 3, 1 ) );

        addChild( new PipeBackNode( transform, module.model.pipe, module.model.liquidDensity ) );

        //Add the back layer for the flux meter, so the particles will look like they go through the hoop
        addChild( new FluxMeterHoopNode( transform, module.model.fluxMeter, false ) );

        particleLayer = new PNode();
        foodColoringLayer = new PNode();
        addChild( foodColoringLayer );
        addChild( particleLayer );

        final FluidFlowModel model = module.model;

        //When particle droplets are added, show graphics for them
        module.model.addParticleAddedObserver( new VoidFunction1<Particle>() {
            public void apply( Particle particle ) {
                addParticleNode( particle );
            }
        } );

        final InjectorNode dropperNode = new GridInjectorNode( transform, 3 * Math.PI / 2, new SimpleObserver() {
            public void update() {
                model.pourFoodColoring();
            }
        }, model.pipe );
        addChild( dropperNode );

        //Show a checkbox that enabled/disables adding dots to the fluid
        final PropertyCheckBox dotsCheckBox = new PropertyCheckBox( DOTS, model.dropperEnabled ) {{
            setFont( CONTROL_FONT );
            setBackground( new Color( 0, 0, 0, 0 ) );
        }};
        addChild( new PSwing( dotsCheckBox ) {{
            setOffset( dropperNode.getFullBounds().getMaxX(), dropperNode.getFullBounds().getCenterY() - getFullBounds().getHeight() / 2 );
        }} );

        //Ruler nodes, one for each unit set
        final Point2D.Double rulerModelOrigin = new Point2D.Double( 0, 0 );
        final MeterStick meterStick = new MeterStick( transform, module.meterStickVisible, module.rulerVisible, rulerModelOrigin, model, false );
        final EnglishRuler englishRuler = new EnglishRuler( transform, module.yardStickVisible, module.rulerVisible, rulerModelOrigin, model, false );
        synchronizeRulerLocations( meterStick, englishRuler );
        addChild( meterStick );
        addChild( englishRuler );

        //Add pipe front after other controls so the drag handles can't get lost behind clock control panel and other control panels
        addChild( new PipeFrontNode( transform, module.model.pipe ) );
        for ( final Particle p : module.model.getParticles() ) {
            addParticleNode( p );
        }

        //Add the front layer for the flux meter, so the particles will look like they go through the hoop
        addChild( new FluxMeterHoopNode( transform, module.model.fluxMeter, true ) );

        //Show the draggable panel with readouts attached to the flux hoop
        //Add one for each unit set, to be shown only when that unit set is selected by the user
        addChild( new FluxMeterPanelNode( transform, module.model.fluxMeter, model.units, METRIC ) );
        addChild( new FluxMeterPanelNode( transform, module.model.fluxMeter, model.units, ENGLISH ) );

        //Create and show the fluid density controls
        addFluidDensityControl( module );

        //Add the floating clock controls and sim speed slider at the bottom of the screen
        addClockControls( module );

        //Add a control for viewing and changing the fluid flow rate
        addChild( new FluidPressureAndFlowControlPanelNode( new FluidFlowRateControl( module ) ) {{
            setOffset( 10, 10 );
        }} );

        // Control Panel
        final FluidPressureAndFlowControlPanelNode controlPanelNode = new FluidPressureAndFlowControlPanelNode( new FluidFlowControlPanel( module ) ) {{
            setOffset( STAGE_SIZE.getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }};
        addChild( controlPanelNode );
        addChild( new ResetAllButtonNode( module, this, (int) ( CONTROL_FONT.getSize() * 1.3 ), FOREGROUND, BACKGROUND ) {{
            setConfirmationEnabled( false );
            setOffset( controlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, controlPanelNode.getFullBounds().getMaxY() + INSET * 2 );
        }} );

        //Add the sensor toolbox node, which also adds the velocity and pressure sensors
        //Doing this last ensures that the draggable sensors will appear in front of everything else
        addSensorToolboxNode( model, controlPanelNode );
    }

    private void addParticleNode( final Particle p ) {
        final ParticleNode node = new ParticleNode( transform, p );
        particleLayer.addChild( node );
        p.addRemovalListener( new SimpleObserver() {
            public void update() {
                particleLayer.removeChild( node );
                p.removeRemovalListener( this );
            }
        } );
    }
}