// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.jmephet.CanvasTransform.CenteredStageCanvasTransform;
import edu.colorado.phet.jmephet.JMEModule;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.JMEView;
import edu.colorado.phet.jmephet.PhetCamera;
import edu.colorado.phet.jmephet.PhetCamera.CenteredStageCameraStrategy;
import edu.colorado.phet.jmephet.PhetJMEApplication;
import edu.colorado.phet.platetectonics.util.JMEModelViewTransform;
import edu.colorado.phet.platetectonics.view.PlateTectonicsJMEApplication;

import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix4f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 * General plate tectonics module that consolidates common behavior between the various tabs
 */
public abstract class PlateTectonicsModule extends JMEModule {
    protected CenteredStageCanvasTransform canvasTransform;
    protected JMEView mainView;

    private JMEModelViewTransform modelViewTransform;

    public PlateTectonicsModule( Frame parentFrame, String name ) {
        super( parentFrame, name, new ConstantDtClock( 30.0 ) );

        // TODO: better initialization for this model view transform (for each module)
        modelViewTransform = new JMEModelViewTransform( Matrix4f.IDENTITY.mult( 0.002f ) ); // 0.5 km => 1 distance in view
    }

    public PlateTectonicsJMEApplication getApp() {
        return (PlateTectonicsJMEApplication) JMEUtils.getApplication();
    }

    @Override public PhetJMEApplication createApplication( Frame parentFrame ) {
        return new PlateTectonicsJMEApplication( parentFrame );
    }

    public JMEModelViewTransform getModelViewTransform() {
        return modelViewTransform;
    }

    @Override public void initialize() {
        canvasTransform = new CenteredStageCanvasTransform( getApp() );

        /*---------------------------------------------------------------------------*
        * temporary test scene
        *----------------------------------------------------------------------------*/
        mainView = createMainView( "Main", new PhetCamera( getStageSize(), new CenteredStageCameraStrategy( 40, 1, 1000 ) ) {{
            setLocation( new Vector3f( 0, 100, 400 ) );
            lookAt( new Vector3f( 0f, 0f, 0f ), Vector3f.UNIT_Y );
        }} );

        // light it
        addLighting( mainView.getScene() );
    }

    // camera to use for debugging purposes
    public Camera getDebugCamera() {
        return null;
    }

    public static void addLighting( Node node ) {
        final DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 1, 1, 1, 1.3f ) );
        node.addLight( sun );

        final DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 1, 1, 1, 0.5f ) );
        node.addLight( moon );
    }
}
