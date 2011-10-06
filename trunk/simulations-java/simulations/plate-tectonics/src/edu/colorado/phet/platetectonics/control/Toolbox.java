// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.LiquidExpansionThermometerNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.hud.PiccoloJMENode;
import edu.colorado.phet.platetectonics.control.RulerNode3D.RulerNode2D;
import edu.colorado.phet.platetectonics.model.ToolboxState;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Displays a toolbox that contains meters (ruler, thermometer, density meter, composition meter), and display options
 */
public class Toolbox extends PiccoloJMENode {
    private static final double INSET = 5;

    public Toolbox( PlateTectonicsModule module, final ToolboxState toolboxState ) {
        super( new ControlPanelNode( new PNode() {{
            final ZeroOffsetNode rulerNode2D = new ZeroOffsetNode( new RulerNode2D( 0.75f ) {{
                // make the ruler visible when it is in the toolbox
                toolboxState.rulerInToolbox.addObserver( JMEUtils.swingObserver( new Runnable() {
                    public void run() {
                        setVisible( toolboxState.rulerInToolbox.get() );
                    }
                } ) );

                // remove it from the toolbox when pressed
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        JMEUtils.invoke( new Runnable() {
                            public void run() {
                                toolboxState.rulerInToolbox.set( false );
                            }
                        } );
                    }
                } );
            }} ); // wrap it in a zero-offset node, since we are rotating and scaling it (bad origin)

            addChild( rulerNode2D ); // approximate scaling to get the size right

            double scale = 0.8;
            final PNode thermometer = new ZeroOffsetNode( new LiquidExpansionThermometerNode( new PDimension( 50 * scale, 150 * scale ) ) ) {{

                //Move it to the right of the ruler
                setOffset( rulerNode2D.getFullBounds().getWidth() + INSET, rulerNode2D.getFullBounds().getMaxY() - getFullBounds().getHeight() );

                // make it visible when it is in the toolbox
                toolboxState.thermometerInToolbox.addObserver( JMEUtils.swingObserver( new Runnable() {
                    public void run() {
                        setVisible( toolboxState.thermometerInToolbox.get() );
                    }
                } ) );

                // remove it from the toolbox when pressed
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override public void mousePressed( PInputEvent event ) {
                        JMEUtils.invoke( new Runnable() {
                            public void run() {
                                toolboxState.thermometerInToolbox.set( false );
                            }
                        } );
                    }
                } );
            }};
            addChild( thermometer );

            addChild( new PText( "Toolbox" ) {{
                setFont( new PhetFont( 16, true ) );
                setOffset( rulerNode2D.getFullBounds().getWidth() + 10, 0 ); // TODO: change positioning once we have added other toolbox elements
            }} );
        }} ), module.getInputHandler(), module, module.getCanvasTransform() );
    }
}