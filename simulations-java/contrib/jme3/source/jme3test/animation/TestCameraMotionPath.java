/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package jme3test.animation;

import com.jme3.animation.LoopMode;
import com.jme3.cinematic.events.MotionTrack;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.MotionPathListener;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Spline.SplineType;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;

public class TestCameraMotionPath extends SimpleApplication {

    private Spatial teapot;
    private boolean active = true;
    private boolean playing = false;
    private MotionPath path;
    private MotionTrack cameraMotionControl;
    private ChaseCamera chaser;
    private CameraNode camNode;

    public static void main(String[] args) {
        TestCameraMotionPath app = new TestCameraMotionPath();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        createScene();
        cam.setLocation(new Vector3f(8.4399185f, 11.189463f, 14.267577f));
        camNode = new CameraNode("Motion cam", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.getControl(0).setEnabled(false);
        path = new MotionPath();
        path.setCycle(true);
        path.addWayPoint(new Vector3f(20, 3, 0));
        path.addWayPoint(new Vector3f(0, 3, 20));
        path.addWayPoint(new Vector3f(-20, 3, 0));
        path.addWayPoint(new Vector3f(0, 3, -20));
        path.setCurveTension(0.83f);
        path.enableDebugShape(assetManager, rootNode);

        cameraMotionControl = new MotionTrack(camNode, path);
        cameraMotionControl.setLoopMode(LoopMode.Loop);
        //cameraMotionControl.setDuration(15f);
        cameraMotionControl.setLookAt(teapot.getWorldTranslation(), Vector3f.UNIT_Y);
        cameraMotionControl.setDirectionType(MotionTrack.Direction.LookAt);

        rootNode.attachChild(camNode);

        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        final BitmapText wayPointsText = new BitmapText(guiFont, false);
        wayPointsText.setSize(guiFont.getCharSet().getRenderedSize());

        guiNode.attachChild(wayPointsText);

        path.addListener(new MotionPathListener() {

            public void onWayPointReach(MotionTrack control, int wayPointIndex) {
                if (path.getNbWayPoints() == wayPointIndex + 1) {
                    wayPointsText.setText(control.getSpatial().getName() + " Finish!!! ");
                } else {
                    wayPointsText.setText(control.getSpatial().getName() + " Reached way point " + wayPointIndex);
                }
                wayPointsText.setLocalTranslation((cam.getWidth() - wayPointsText.getLineWidth()) / 2, cam.getHeight(), 0);
            }
        });

        flyCam.setEnabled(false);
        chaser = new ChaseCamera(cam, teapot);
        chaser.registerWithInput(inputManager);
        chaser.setSmoothMotion(true);
        chaser.setMaxDistance(50);
        chaser.setDefaultDistance(50);
        initInputs();

    }

    private void createScene() {
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat.setFloat("Shininess", 1f);
        mat.setBoolean("UseMaterialColors", true);
        mat.setColor("Ambient", ColorRGBA.Black);
        mat.setColor("Diffuse", ColorRGBA.DarkGray);
        mat.setColor("Specular", ColorRGBA.White.mult(0.6f));
        Material matSoil = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        matSoil.setBoolean("UseMaterialColors", true);
        matSoil.setColor("Ambient", ColorRGBA.Gray);
        matSoil.setColor("Diffuse", ColorRGBA.Gray);
        matSoil.setColor("Specular", ColorRGBA.Black);
        teapot = assetManager.loadModel("Models/Teapot/Teapot.obj");
        teapot.setLocalScale(3);
        teapot.setMaterial(mat);



        rootNode.attachChild(teapot);
        Geometry soil = new Geometry("soil", new Box(new Vector3f(0, -1.0f, 0), 50, 1, 50));
        soil.setMaterial(matSoil);
        rootNode.attachChild(soil);
        DirectionalLight light = new DirectionalLight();
        light.setDirection(new Vector3f(0, -1, 0).normalizeLocal());
        light.setColor(ColorRGBA.White.mult(1.5f));
        rootNode.addLight(light);
    }

    private void initInputs() {
        inputManager.addMapping("display_hidePath", new KeyTrigger(KeyInput.KEY_P));
        inputManager.addMapping("SwitchPathInterpolation", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("tensionUp", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("tensionDown", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("play_stop", new KeyTrigger(KeyInput.KEY_SPACE));
        ActionListener acl = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                if (name.equals("display_hidePath") && keyPressed) {
                    if (active) {
                        active = false;
                        path.disableDebugShape();
                    } else {
                        active = true;
                        path.enableDebugShape(assetManager, rootNode);
                    }
                }
                if (name.equals("play_stop") && keyPressed) {
                    if (playing) {
                        playing = false;
                        cameraMotionControl.stop();
                        chaser.setEnabled(true);
                        camNode.getControl(0).setEnabled(false);
                    } else {
                        playing = true;
                        chaser.setEnabled(false);
                        camNode.getControl(0).setEnabled(true);
                        cameraMotionControl.play();
                    }
                }

                if (name.equals("SwitchPathInterpolation") && keyPressed) {
                    if (path.getPathSplineType() == SplineType.CatmullRom){
                        path.setPathSplineType(SplineType.Linear);
                    } else {
                        path.setPathSplineType(SplineType.CatmullRom);
                    }
                }

                if (name.equals("tensionUp") && keyPressed) {
                    path.setCurveTension(path.getCurveTension() + 0.1f);
                    System.err.println("Tension : " + path.getCurveTension());
                }
                if (name.equals("tensionDown") && keyPressed) {
                    path.setCurveTension(path.getCurveTension() - 0.1f);
                    System.err.println("Tension : " + path.getCurveTension());
                }


            }
        };

        inputManager.addListener(acl, "display_hidePath", "play_stop", "SwitchPathInterpolation", "tensionUp", "tensionDown");

    }
}
