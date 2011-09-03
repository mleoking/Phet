package edu.colorado.phet.moleculeshapes.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Random;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesProperties;
import edu.colorado.phet.moleculeshapes.control.GeometryNameNode;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel;
import edu.colorado.phet.moleculeshapes.control.MoleculeShapesPanelNode;
import edu.colorado.phet.moleculeshapes.control.RealMoleculeOverlayNode;
import edu.colorado.phet.moleculeshapes.jme.HUDNode;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.jme.PhetJMEApplication;
import edu.colorado.phet.moleculeshapes.jme.PiccoloJMENode;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.PairGroup;
import edu.colorado.phet.moleculeshapes.util.Fireable;
import edu.colorado.phet.moleculeshapes.util.VoidNotifier;
import edu.umd.cs.piccolo.util.PBounds;

import com.jme3.bounding.BoundingSphere;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.JmeCanvasContext;

/**
 * Use jme3 to show a rotating molecule
 * TODO: audit for any other synchronization issues. we have the AWT and JME threads running rampant!
 * TODO: ability to deadlock: (see below)
 * TODO: electron geometry name repaint issue - check threading and repaint()
 * TODO: slightly increase general rotation speed?
 * <p/>
 * NOTES:
 * TODO: it's weird to drag out an invisible lone pair
 * TODO: consider color-coding the molecular / electron readouts?
 * TODO: Reset: how many bonds (0 or 2) should it reset to?
 * TODO: double-check naming with double/triple bonds
 * TODO: test startup crash failure dialog
 * TODO: looks weird to not see 180-degree angles? how even to handle?!?
 * <p/>
 * Found one Java-level deadlock:
 * =============================
 * "LWJGL Renderer Thread":
 * waiting to lock monitor 0x00000000081eeef8 (object 0x000000070016cdc8, a com.sun.java.swing.plaf.windows.AnimationController),
 * which is held by "AWT-EventQueue-0"
 * "AWT-EventQueue-0":
 * waiting to lock monitor 0x0000000008071a58 (object 0x00000007000a8bb0, a edu.colorado.phet.moleculeshapes.jme.HUDNode),
 * which is held by "LWJGL Renderer Thread"
 * <p/>
 * Java stack information for the threads listed above:
 * ===================================================
 * "LWJGL Renderer Thread":
 * at com.sun.java.swing.plaf.windows.AnimationController.getState(AnimationController.java:172)
 * - waiting to lock <0x000000070016cdc8> (a com.sun.java.swing.plaf.windows.AnimationController)
 * at com.sun.java.swing.plaf.windows.AnimationController.triggerAnimation(AnimationController.java:99)
 * at com.sun.java.swing.plaf.windows.AnimationController.paintSkin(AnimationController.java:223)
 * at com.sun.java.swing.plaf.windows.XPStyle$Skin.paintSkin(XPStyle.java:585)
 * at com.sun.java.swing.plaf.windows.XPStyle$Skin.paintSkin(XPStyle.java:554)
 * at com.sun.java.swing.plaf.windows.WindowsIconFactory$RadioButtonIcon.paintIcon(WindowsIconFactory.java:445)
 * at javax.swing.plaf.basic.BasicRadioButtonUI.paint(BasicRadioButtonUI.java:162)
 * - locked <0x0000000700003320> (a com.sun.java.swing.plaf.windows.WindowsRadioButtonUI)
 * at javax.swing.plaf.ComponentUI.update(ComponentUI.java:143)
 * at javax.swing.JComponent.paintComponent(JComponent.java:752)
 * at javax.swing.JComponent.paint(JComponent.java:1029)
 * at edu.umd.cs.piccolox.pswing.PSwing.paint(PSwing.java:480)
 * at edu.umd.cs.piccolox.pswing.PSwing.paint(PSwing.java:392)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2826)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PCamera.paintCameraView(PCamera.java:374)
 * at edu.umd.cs.piccolo.PCamera.paint(PCamera.java:356)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2826)
 * at edu.umd.cs.piccolo.PCamera.fullPaint(PCamera.java:448)
 * at edu.umd.cs.piccolo.PCanvas.paintComponent(PCanvas.java:608)
 * at javax.swing.JComponent.paint(JComponent.java:1029)
 * at javax.swing.JComponent.paintChildren(JComponent.java:862)
 * - locked <0x00000007003bbce8> (a java.awt.Component$AWTTreeLock)
 * at javax.swing.JComponent.paint(JComponent.java:1038)
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode$1.paint(HUDNode.java:83)
 * at edu.colorado.phet.moleculeshapes.jme.PaintableImage.refreshImage(PaintableImage.java:29)
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode$5.update(HUDNode.java:153)
 * - locked <0x00000007000a8bb0> (a edu.colorado.phet.moleculeshapes.jme.HUDNode)
 * at com.jme3.app.state.AppStateManager.update(AppStateManager.java:153)
 * at edu.colorado.phet.moleculeshapes.jme.PhetJMEApplication.update(PhetJMEApplication.java:108)
 * - locked <0x000000070007a188> (a edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication)
 * at com.jme3.system.lwjgl.LwjglAbstractDisplay.runLoop(LwjglAbstractDisplay.java:144)
 * at com.jme3.system.lwjgl.LwjglCanvas.runLoop(LwjglCanvas.java:199)
 * at com.jme3.system.lwjgl.LwjglAbstractDisplay.run(LwjglAbstractDisplay.java:218)
 * at java.lang.Thread.run(Thread.java:662)
 * "AWT-EventQueue-0":
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode.repaint(HUDNode.java:163)
 * - waiting to lock <0x00000007000a8bb0> (a edu.colorado.phet.moleculeshapes.jme.HUDNode)
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode$2.apply(HUDNode.java:90)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:31)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:36)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:36)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:36)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.addDirtyRegion(JMERepaintManager.java:18)
 * at javax.swing.JComponent.repaint(JComponent.java:4734)
 * at java.awt.Component.repaint(Component.java:3103)
 * at com.sun.java.swing.plaf.windows.AnimationController.actionPerformed(AnimationController.java:251)
 * - locked <0x000000070016cdc8> (a com.sun.java.swing.plaf.windows.AnimationController)
 * at javax.swing.Timer.fireActionPerformed(Timer.java:291)
 * at javax.swing.Timer$DoPostEvent.run(Timer.java:221)
 * at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209)
 * at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:642)
 * at java.awt.EventQueue.access$000(EventQueue.java:85)
 * at java.awt.EventQueue$1.run(EventQueue.java:603)
 * at java.awt.EventQueue$1.run(EventQueue.java:601)
 * at java.security.AccessController.doPrivileged(Native Method)
 * at java.security.AccessControlContext$1.doIntersectionPrivilege(AccessControlContext.java:87)
 * at java.awt.EventQueue.dispatchEvent(EventQueue.java:612)
 * at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:269)
 * at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:184)
 * at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:174)
 * at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:169)
 * at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:161)
 * at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)
 * <p/>
 * Found 1 deadlock.
 * <p/>
 * <p/>
 * "LWJGL Renderer Thread":
 * at com.sun.java.swing.plaf.windows.AnimationController.getState(AnimationController.java:172)
 * - waiting to lock <0x00000007002d99f8> (a com.sun.java.swing.plaf.windows.AnimationController)
 * at com.sun.java.swing.plaf.windows.AnimationController.triggerAnimation(AnimationController.java:99)
 * at com.sun.java.swing.plaf.windows.AnimationController.paintSkin(AnimationController.java:223)
 * at com.sun.java.swing.plaf.windows.XPStyle$Skin.paintSkin(XPStyle.java:585)
 * at com.sun.java.swing.plaf.windows.XPStyle$Skin.paintSkin(XPStyle.java:554)
 * at com.sun.java.swing.plaf.windows.WindowsIconFactory$RadioButtonIcon.paintIcon(WindowsIconFactory.java:445)
 * at javax.swing.plaf.basic.BasicRadioButtonUI.paint(BasicRadioButtonUI.java:162)
 * - locked <0x00000007000d13c8> (a com.sun.java.swing.plaf.windows.WindowsRadioButtonUI)
 * at javax.swing.plaf.ComponentUI.update(ComponentUI.java:143)
 * at javax.swing.JComponent.paintComponent(JComponent.java:752)
 * at javax.swing.JComponent.paint(JComponent.java:1029)
 * at edu.umd.cs.piccolox.pswing.PSwing.paint(PSwing.java:480)
 * at edu.umd.cs.piccolox.pswing.PSwing.paint(PSwing.java:392)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2826)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2832)
 * at edu.umd.cs.piccolo.PCamera.paintCameraView(PCamera.java:374)
 * at edu.umd.cs.piccolo.PCamera.paint(PCamera.java:356)
 * at edu.umd.cs.piccolo.PNode.fullPaint(PNode.java:2826)
 * at edu.umd.cs.piccolo.PCamera.fullPaint(PCamera.java:448)
 * at edu.umd.cs.piccolo.PCanvas.paintComponent(PCanvas.java:608)
 * at javax.swing.JComponent.paint(JComponent.java:1029)
 * at javax.swing.JComponent.paintChildren(JComponent.java:862)
 * - locked <0x00000007001f87b0> (a java.awt.Component$AWTTreeLock)
 * at javax.swing.JComponent.paint(JComponent.java:1038)
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode$1.paint(HUDNode.java:83)
 * at edu.colorado.phet.moleculeshapes.jme.PaintableImage.refreshImage(PaintableImage.java:29)
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode$5.update(HUDNode.java:153)
 * - locked <0x0000000700078728> (a edu.colorado.phet.moleculeshapes.jme.HUDNode)
 * at com.jme3.app.state.AppStateManager.update(AppStateManager.java:153)
 * at edu.colorado.phet.moleculeshapes.jme.PhetJMEApplication.update(PhetJMEApplication.java:108)
 * - locked <0x0000000700076b40> (a edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication)
 * at com.jme3.system.lwjgl.LwjglAbstractDisplay.runLoop(LwjglAbstractDisplay.java:144)
 * at com.jme3.system.lwjgl.LwjglCanvas.runLoop(LwjglCanvas.java:199)
 * at com.jme3.system.lwjgl.LwjglAbstractDisplay.run(LwjglAbstractDisplay.java:218)
 * at java.lang.Thread.run(Thread.java:662)
 * "AWT-EventQueue-0":
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode.repaint(HUDNode.java:163)
 * - waiting to lock <0x0000000700078728> (a edu.colorado.phet.moleculeshapes.jme.HUDNode)
 * at edu.colorado.phet.moleculeshapes.jme.HUDNode$2.apply(HUDNode.java:90)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:31)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:36)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:36)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.notifyComponent(JMERepaintManager.java:36)
 * at edu.colorado.phet.moleculeshapes.jme.JMERepaintManager.addDirtyRegion(JMERepaintManager.java:18)
 * at javax.swing.JComponent.repaint(JComponent.java:4734)
 * at java.awt.Component.repaint(Component.java:3103)
 * at com.sun.java.swing.plaf.windows.AnimationController.actionPerformed(AnimationController.java:251)
 * - locked <0x00000007002d99f8> (a com.sun.java.swing.plaf.windows.AnimationController)
 * at javax.swing.Timer.fireActionPerformed(Timer.java:291)
 * at javax.swing.Timer$DoPostEvent.run(Timer.java:221)
 * at java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:209)
 * at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:642)
 * at java.awt.EventQueue.access$000(EventQueue.java:85)
 * at java.awt.EventQueue$1.run(EventQueue.java:603)
 * at java.awt.EventQueue$1.run(EventQueue.java:601)
 * at java.security.AccessController.doPrivileged(Native Method)
 * at java.security.AccessControlContext$1.doIntersectionPrivilege(AccessControlContext.java:87)
 * at java.awt.EventQueue.dispatchEvent(EventQueue.java:612)
 * at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:269)
 * at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:184)
 * at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:174)
 * at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:169)
 * at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:161)
 * at java.awt.EventDispatchThread.run(EventDispatchThread.java:122)
 */
public class MoleculeJMEApplication extends PhetJMEApplication {

    /*---------------------------------------------------------------------------*
    * input mapping constants
    *----------------------------------------------------------------------------*/
    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_LMB = "CameraDrag";
    public static final String MAP_MMB = "RightMouseButton";

    private static final float MOUSE_SCALE = 3.0f;

    /*---------------------------------------------------------------------------*
    * model
    *----------------------------------------------------------------------------*/

    private MoleculeModel molecule = new MoleculeModel();

    public static final Property<Boolean> showLonePairs = new Property<Boolean>( true );
    public final VoidNotifier resetNotifier = new VoidNotifier();

    public MoleculeJMEApplication( Frame parentFrame ) {
        this.parentFrame = parentFrame;
    }

    /*---------------------------------------------------------------------------*
    * dragging
    *----------------------------------------------------------------------------*/

    public static enum DragMode {
        MODEL_ROTATE, // rotate the VSEPR model molecule
        PAIR_FRESH_PLANAR, // drag an atom/lone pair on the z=0 plane
        PAIR_EXISTING_SPHERICAL, // drag an atom/lone pair across the surface of a sphere
        REAL_MOLECULE_ROTATE // rotate the "real" molecule in the display
    }

    private boolean dragging = false; // keeps track of the drag state
    private DragMode dragMode = DragMode.MODEL_ROTATE;
    private PairGroup draggedParticle = null;
    private boolean globalLeftMouseDown = false; // keep track of the LMB state, since we need to deal with a few synchronization issues

    /*---------------------------------------------------------------------------*
    * positioning
    *----------------------------------------------------------------------------*/

    private Dimension lastCanvasSize;
    private boolean resizeDirty = false;

    private Quaternion rotation = new Quaternion(); // The angle about which the molecule should be rotated, changes as a function of time

    /*---------------------------------------------------------------------------*
    * graphics/control
    *----------------------------------------------------------------------------*/

    private PiccoloJMENode controlPanel;
    private PiccoloJMENode resetAllNode;
    private PiccoloJMENode namePanel;

    private final Frame parentFrame;
    private Camera overlayCamera;

    private MoleculeShapesControlPanel controlPanelNode;
    private RealMoleculeOverlayNode realMoleculeOverlayNode;

    private MoleculeModelNode moleculeNode; // The molecule to display and rotate

    private static final Random random = new Random( System.currentTimeMillis() );

    /**
     * Pseudo-constructor (JME-based)
     */
    @Override public void initialize() {
        super.initialize();

        initializeResources();

        // add an offset to the left, since we have a control panel on the right
        getSceneNode().setLocalTranslation( new Vector3f( -4.5f, 1.5f, 0 ) );

        // hook up mouse-move handlers
        inputManager.addMapping( MoleculeJMEApplication.MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

        // hook up mouse-button handlers
        inputManager.addMapping( MAP_LMB, new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );
        inputManager.addMapping( MAP_MMB, new MouseButtonTrigger( MouseInput.BUTTON_MIDDLE ) );

        /*---------------------------------------------------------------------------*
        * mouse-button presses
        *----------------------------------------------------------------------------*/
        inputManager.addListener(
                new ActionListener() {
                    public void onAction( String name, boolean isMouseDown, float tpf ) {
                        // record whether the mouse button is down
                        synchronized ( MoleculeJMEApplication.this ) {

                            // on left mouse button change
                            if ( name.equals( MAP_LMB ) ) {
                                globalLeftMouseDown = isMouseDown;

                                if ( isMouseDown ) {
                                    onLeftMouseDown();
                                }
                                else {
                                    onLeftMouseUp();
                                }
                            }

                            // kill any pair groups under the middle mouse button press
                            if ( isMouseDown && name.equals( MAP_MMB ) ) {
                                PairGroup pair = getElectronPairUnderPointer();
                                if ( pair != null ) {
                                    molecule.removePair( pair );
                                }
                            }
                        }
                    }
                }, MAP_LMB, MAP_MMB );

        /*---------------------------------------------------------------------------*
        * mouse motion
        *----------------------------------------------------------------------------*/
        inputManager.addListener(
                new AnalogListener() {
                    public void onAnalog( final String name, final float value, float tpf ) {

                        //By always updating the cursor at every mouse move, we can be sure it is always correct.
                        //Whenever there is a mouse move event, make sure the cursor is in the right state.
                        updateCursor();

                        if ( dragging ) {
                            synchronized ( MoleculeJMEApplication.this ) {

                                // function that updates a quaternion in-place, by adding the necessary rotation in, multiplied by the scale
                                final VoidFunction2<Quaternion, Float> updateQuaternion = new VoidFunction2<Quaternion, Float>() {
                                    public void apply( Quaternion quaternion, Float scale ) {
                                        if ( name.equals( MoleculeJMEApplication.MAP_LEFT ) ) {
                                            quaternion.set( new Quaternion().fromAngles( 0, -value * scale, 0 ).mult( quaternion ) );
                                        }
                                        if ( name.equals( MoleculeJMEApplication.MAP_RIGHT ) ) {
                                            quaternion.set( new Quaternion().fromAngles( 0, value * scale, 0 ).mult( quaternion ) );
                                        }
                                        if ( name.equals( MoleculeJMEApplication.MAP_UP ) ) {
                                            quaternion.set( new Quaternion().fromAngles( -value * scale, 0, 0 ).mult( quaternion ) );
                                        }
                                        if ( name.equals( MoleculeJMEApplication.MAP_DOWN ) ) {
                                            quaternion.set( new Quaternion().fromAngles( value * scale, 0, 0 ).mult( quaternion ) );
                                        }
                                    }
                                };

                                switch( dragMode ) {
                                    case MODEL_ROTATE:
                                        updateQuaternion.apply( rotation, MOUSE_SCALE );
                                        break;
                                    case REAL_MOLECULE_ROTATE:
                                        realMoleculeOverlayNode.updateRotation( updateQuaternion );
                                        break;
                                    case PAIR_FRESH_PLANAR:
                                        // put the particle on the z=0 plane
                                        draggedParticle.dragToPosition( JmeUtils.convertVector( getPlanarMoleculeCursorPosition() ) );
                                        break;
                                    case PAIR_EXISTING_SPHERICAL:
                                        draggedParticle.dragToPosition( JmeUtils.convertVector( getSphericalMoleculeCursorPosition( JmeUtils.convertVector( draggedParticle.position.get() ) ) ) );
                                        break;
                                }
                            }
                        }
                    }
                }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN, MAP_LMB );

        moleculeNode = new MoleculeModelNode( molecule, this, cam );
        getSceneNode().attachChild( moleculeNode );

        /*---------------------------------------------------------------------------*
        * scene setup
        *----------------------------------------------------------------------------*/

        addLighting( getSceneNode() );
        cam.setLocation( new Vector3f( 0, 0, 40 ) );

        // start with two single bonds
        molecule.addPair( new PairGroup( new ImmutableVector3D( 10, 0, 3 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );
        molecule.addPair( new PairGroup( new ImmutableVector3D( 2, 10, -5 ).normalized().times( PairGroup.BONDED_PAIR_DISTANCE ), 1, false ) );

        /*---------------------------------------------------------------------------*
        * real molecule overlay
        *----------------------------------------------------------------------------*/

        Node overlayNode = new Node( "Overlay" );

        overlayCamera = new Camera( settings.getWidth(), settings.getHeight() );

        final ViewPort overlayViewport = renderManager.createMainView( "Overlay Viewport", overlayCamera );
        overlayViewport.attachScene( overlayNode );
        addLiveNode( overlayNode );

        realMoleculeOverlayNode = new RealMoleculeOverlayNode( this, overlayCamera );
        overlayNode.attachChild( realMoleculeOverlayNode );

        addLighting( overlayNode );

        /*---------------------------------------------------------------------------*
        * reset button
        *----------------------------------------------------------------------------*/

        // TODO: i18n (and reset behavior)
        resetAllNode = new PiccoloJMENode( new TextButtonNode( "Reset", new PhetFont( 16 ), Color.ORANGE ) {{
            addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    resetAll();
                }
            } );
        }}, this );
        getBackgroundGuiNode().attachChild( resetAllNode );

        /*---------------------------------------------------------------------------*
        * main control panel
        *----------------------------------------------------------------------------*/
        controlPanelNode = new MoleculeShapesControlPanel( this, realMoleculeOverlayNode );
        controlPanel = new PiccoloJMENode( controlPanelNode, this );
        getBackgroundGuiNode().attachChild( controlPanel );
        controlPanel.onResize.addTarget( new Fireable<Void>() {
            public void fire( Void param ) {
                resizeDirty = true;
            }
        } );

        /*---------------------------------------------------------------------------*
        * "geometry name" panel
        *----------------------------------------------------------------------------*/
        namePanel = new PiccoloJMENode( new MoleculeShapesPanelNode( new GeometryNameNode( molecule, this ), "Geometry Name" ) {{
            // TODO fix (temporary offset since PiccoloJMENode isn't checking the "origin")
            setOffset( 0, 10 );
        }}, this );
        getBackgroundGuiNode().attachChild( namePanel );
    }

    public void startOverlayMoleculeDrag() {
        dragging = true;
        dragMode = DragMode.REAL_MOLECULE_ROTATE;
    }

    private void onLeftMouseDown() {
        // for dragging, ignore mouse presses over the HUD
        Component componentUnderPointer = getComponentUnderPointer( Mouse.getX(), Mouse.getY() );
        boolean mouseOverInterface = componentUnderPointer != null;
        if ( !mouseOverInterface ) {
            dragging = true;

            PairGroup pair = getElectronPairUnderPointer();
            if ( pair != null ) {
                // we are over a pair group, so start the drag on it
                dragMode = DragMode.PAIR_EXISTING_SPHERICAL;
                draggedParticle = pair;
                pair.userControlled.set( true );
            }
            else {
                // set up default drag mode
                dragMode = DragMode.MODEL_ROTATE;
            }
        }
    }

    private void onLeftMouseUp() {
        // not dragging anymore
        dragging = false;

        // release an electron pair if we were dragging it
        if ( dragMode == DragMode.PAIR_FRESH_PLANAR || dragMode == DragMode.PAIR_EXISTING_SPHERICAL ) {
            draggedParticle.userControlled.set( false );
        }
    }

    public synchronized void startNewInstanceDrag( int bondOrder ) {
        // sanity check
        if ( !molecule.wouldAllowBondOrder( bondOrder ) ) {
            // don't add to the molecule if it is full
            return;
        }

        Vector3f localPosition = getPlanarMoleculeCursorPosition();

        PairGroup pair = new PairGroup( JmeUtils.convertVector( localPosition ), bondOrder, true );
        molecule.addPair( pair );

        // set up dragging information
        dragging = true;
        dragMode = DragMode.PAIR_FRESH_PLANAR;
        draggedParticle = pair;

        /*
         * If the left mouse button is not down, simulate a mouse-up. This is needed due to threading issues,
         * since if you do an "instant" mouse down/up, they both get processed before this is called.
         */
        if ( !globalLeftMouseDown ) {
            onLeftMouseUp();
        }
    }

    private static void addLighting( Node node ) {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 1f ) );
        node.addLight( sun );

        DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f ) );
        node.addLight( moon );
    }

    private void updateCursor() {
        //This solves a problem that we saw that: when there was no padding or other component on the side of the canvas, the mouse would become East-West resize cursor
        //And wouldn't change back.
        JmeCanvasContext context = (JmeCanvasContext) getContext();
        Canvas canvas = context.getCanvas();

        //If the mouse is in front of a grabbable object, show a hand, otherwise show the default cursor
        PairGroup pair = getElectronPairUnderPointer();

        Component component = getComponentUnderPointer( Mouse.getX(), Mouse.getY() );

        if ( dragging && ( dragMode == DragMode.MODEL_ROTATE || dragMode == DragMode.REAL_MOLECULE_ROTATE ) ) {
            // rotating the molecule. for now, trying out the "move" cursor
            canvas.setCursor( Cursor.getPredefinedCursor( MoleculeShapesProperties.useRotationCursor.get() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR ) );
        }
        else if ( pair != null || dragging ) {
            // over a pair group, OR dragging one
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        }
        else if ( component != null ) {
            // over a HUD node, so set the cursor to what the component would want
            canvas.setCursor( component.getCursor() );
        }
        else {
            // default to the default cursor
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
        }
    }

    public synchronized void resetAll() {
        removeAllAtoms();
        showLonePairs.reset();

        resetNotifier.fire();
    }

    public Vector3f getPlanarMoleculeCursorPosition() {
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        float t = -click3d.getZ() / dir.getZ(); // solve for below equation at z=0. assumes camera isn't z=0, which should be safe here

        Vector3f globalStartPosition = click3d.add( dir.mult( t ) );

        // transform to moleculeNode coordinates and return
        return moleculeNode.getWorldTransform().transformInverseVector( globalStartPosition, new Vector3f() );
    }

    public Vector3f getSphericalMoleculeCursorPosition( Vector3f currentLocalPosition ) {
        // decide whether to grab the closest or farthest point if possible. for now, we try to NOT move the pair at the start of the drag
        boolean returnCloseHit = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).mult( currentLocalPosition ).z >= 0;

        // override for dev option
        if ( !MoleculeShapesProperties.allowDraggingBehind.get() ) {
            returnCloseHit = true;
        }

        // set up intersection stuff
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );

        // transform our position and direction into the local coordinate frame. we will do our computations there
        Vector3f transformedPosition = moleculeNode.getWorldTransform().transformInverseVector( click3d, new Vector3f() );
        Vector3f transformedDirection = moleculeNode.getLocalToWorldMatrix( new Matrix4f() ).transpose().mult( dir ).normalize(); // transpose trick to transform a unit vector
        Ray ray = new Ray( transformedPosition, transformedDirection );

        // how far we will end up from the center atom
        float finalDistance = (float) draggedParticle.getIdealDistanceFromCenter();

        // our sphere to cast our ray against
        BoundingSphere sphere = new BoundingSphere( finalDistance, new Vector3f( 0, 0, 0 ) );

        sphere.collideWithRay( ray, results );
        if ( results.size() == 0 ) {
            /*
             * Compute the point where the closest line through the camera and tangent to our bounding sphere intersects the sphere
             * ie, think 2d. we have a unit sphere centered at the origin, and a camera at (d,0). Our tangent point satisfies two
             * important conditions:
             * - it lies on the sphere. x^2 + y^2 == 1
             * - vector to the point (x,y) is tangent to the vector from (x,y) to our camera (d,0). thus (x,y) . (d-y, -y) == 0
             * Solve, and we get x = 1/d  plug back in for y (call that height), and we have our 2d solution.
             *
             * Now, back to 3D. Since camera is (0,0,d), our z == 1/d and our x^2 + y^2 == (our 2D y := height), then rescale them out of the unit sphere
             */

            float distanceFromCamera = transformedPosition.distance( new Vector3f() );

            // first, calculate it in unit-sphere, as noted above
            float d = distanceFromCamera / finalDistance; // scaled distance to the camera (from the origin)
            float z = 1 / d; // our result z (down-scaled)
            float height = FastMath.sqrt( d * d - 1 ) / d; // our result (down-scaled) magnitude of (x,y,0), which is the radius of the circle composed of all points that could be tangent

            /*
             * Since our camera isn't actually on the z-axis, we need to calculate two vectors. One is the direction towards
             * the camera (planeNormal, easy!), and the other is the direction perpendicular to the planeNormal that points towards
             * the mouse pointer (planeHitDirection).
             */

            // intersect our camera ray against our perpendicular plane (perpendicular to our camera position from the origin) to determine the orientations
            Vector3f planeNormal = transformedPosition.normalize();
            float t = -( transformedPosition.length() ) / ( planeNormal.dot( transformedDirection ) );
            Vector3f planeHitDirection = transformedPosition.add( transformedDirection.mult( t ) ).normalize();

            // use the above plane hit direction (perpendicular to the camera) and plane normal (collinear with the camera) to calculate the result
            Vector3f downscaledResult = planeHitDirection.mult( height ).add( planeNormal.mult( z ) );

            // scale it back to our sized sphere
            return downscaledResult.mult( finalDistance );
        }
        else {
            // pick our desired hitpoint (there are only 2), and return it
            CollisionResult result = returnCloseHit ? results.getClosestCollision() : results.getFarthestCollision();
            return result.getContactPoint();
        }
    }

    /**
     * @return The closest (hit) electron pair currently under the mouse pointer, or null if there is none
     */
    public PairGroup getElectronPairUnderPointer() {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 0f ).clone();
        Vector3f dir = cam.getWorldCoordinates( new Vector2f( click2d.x, click2d.y ), 1f ).subtractLocal( click3d );
        Ray ray = new Ray( click3d, dir );
        getSceneNode().collideWith( ray, results );
        for ( CollisionResult result : results ) {
            PairGroup pair = getElectronPairForTarget( result.getGeometry() );
            if ( pair != null ) {
                return pair;
            }
        }
        return null;
    }

    public Component getComponentUnderPointer( int x, int y ) {
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        getBackgroundGuiNode().collideWith( new Ray( new Vector3f( click2d.x, click2d.y, 0f ), new Vector3f( 0, 0, 1 ) ), results );
        for ( CollisionResult result : results ) {

            Geometry geometry = result.getGeometry();
            if ( geometry instanceof HUDNode ) {
                HUDNode node = (HUDNode) geometry;
                Vector3f hitPoint = node.transformEventCoordinates( click2d.x, click2d.y );
                Component component = node.componentAt( (int) hitPoint.x, (int) hitPoint.y );
                return component;
            }
        }
        return null;
    }


    /**
     * Given a spatial target, return any associated electron pair, or null if there is none
     *
     * @param target JME Spatial
     * @return Electron pair, or null
     */
    private PairGroup getElectronPairForTarget( Spatial target ) {
        boolean isAtom = target instanceof AtomNode;
        boolean isLonePair = target instanceof LonePairNode;

        if ( isAtom ) {
            return ( (AtomNode) target ).pair;
        }
        else if ( isLonePair ) {
            if ( !target.getCullHint().equals( CullHint.Always ) ) {
                return ( (LonePairNode) target ).pair;
            }
            else {
                return null; // lone pair invisible
            }
        }
        else {
            if ( target.getParent() != null ) {
                return getElectronPairForTarget( target.getParent() );
            }
            else {
                // failure
                return null;
            }
        }
    }

    public synchronized void removePairGroup( PairGroup group ) {
        // synchronized removal, so we don't run over the display thread
        molecule.removePair( group );
    }

    public synchronized void testRemoveAtom() {
        if ( !molecule.getGroups().isEmpty() ) {
            molecule.removePair( molecule.getGroups().get( random.nextInt( molecule.getGroups().size() ) ) );
        }
    }

    @Override public synchronized void updateState( final float tpf ) {
        molecule.update( tpf );
        moleculeNode.setLocalRotation( rotation );

        if ( resizeDirty ) {
            resizeDirty = false;
            final float padding = 10;
            if ( controlPanel != null ) {
                controlPanel.setLocalTranslation( lastCanvasSize.width - controlPanel.getWidth() - padding,
                                                  lastCanvasSize.height - controlPanel.getHeight() - padding,
                                                  0 );

                namePanel.setLocalTranslation( padding, padding, 0 );

                resetAllNode.setLocalTranslation( controlPanel.getLocalTranslation().subtract( new Vector3f( -( controlPanel.getWidth() - resetAllNode.getWidth() ) / 2, 40, 0 ) ) );

                /*---------------------------------------------------------------------------*
                * calculate real molecule overlay bounds
                *----------------------------------------------------------------------------*/

                /*
                * We need to position the molecule overlay over the portion of the screen taken up by
                * the Piccolo background target
                */

                boolean showOverlay = controlPanelNode.isOverlayVisible();
                realMoleculeOverlayNode.setCullHint( showOverlay ? CullHint.Never : CullHint.Always );
                if ( showOverlay ) {
                    // get the bounds, relative to the Piccolo origin (which is 0,0 in the component as well)
                    PBounds localOverlayBounds = controlPanelNode.getOverlayBounds();

                    // get the translation of the control panel
                    float offsetX = controlPanel.getLocalTranslation().getX();
                    float offsetY = controlPanel.getLocalTranslation().getY();

                    // convert these to screen-offset from the lower-left, since the control panel itself is translated
                    double localLeft = localOverlayBounds.getMinX() + offsetX;
                    double localRight = localOverlayBounds.getMaxX() + offsetX;
                    double localTop = controlPanel.getHeight() - localOverlayBounds.getMinY() + offsetY; // remember, Y is flipped here
                    double localBottom = controlPanel.getHeight() - localOverlayBounds.getMaxY() + offsetY; // remember, Y is flipped here

                    // rescale these numbers to between 0 and 1 (for the entire JME3 canvas size)
                    float finalLeft = (float) ( localLeft / lastCanvasSize.width );
                    float finalRight = (float) ( localRight / lastCanvasSize.width );
                    float finalBottom = (float) ( localBottom / lastCanvasSize.height );
                    float finalTop = (float) ( localTop / lastCanvasSize.height );

                    // position the overlay viewport over this region
                    overlayCamera.setViewPort( finalLeft, finalRight, finalBottom, finalTop );

                    /*---------------------------------------------------------------------------*
                    * position overlay camera
                    *----------------------------------------------------------------------------*/
                    overlayCamera.setFrustumPerspective( 45f, 1, 1f, 1000f );
                    overlayCamera.setLocation( new Vector3f( 0, 0, 40 ) );
                    overlayCamera.lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );

                    overlayCamera.update();
                }
            }
        }
    }

    public void removeAllAtoms() {
        while ( !molecule.getGroups().isEmpty() ) {
            testRemoveAtom();
        }
    }

    public void onResize( Dimension canvasSize ) {
        lastCanvasSize = canvasSize;
        resizeDirty = true;
    }

    private void initializeResources() {
        // pre-load the lone pair geometry, so we don't get that delay
        LonePairNode.getGeometry( assetManager );
    }

    public MoleculeModel getMolecule() {
        return molecule;
    }

    @Override public void handleError( String errMsg, Throwable t ) {
        super.handleError( errMsg, t );
        if ( errMsg.equals( "Failed to initialize OpenGL context" ) ) {
            PhetOptionPane.showMessageDialog( parentFrame, "The simulation was unable to start.\nUpgrading your video card's drivers may fix the problem.\nError information:\n" + t.getMessage() );
        }
    }

    public boolean canAutoRotateRealMolecule() {
        return !( dragging && dragMode == DragMode.REAL_MOLECULE_ROTATE );
    }
}