/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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

package jmetest.renderer;

import com.jme.app.SimpleGame;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.SharedMesh;
import com.jme.scene.SharedNode;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.shape.Sphere;

/**
 * <code>TestSharedNodeOffset</code>
 * 
 * @author Mark Powell
 * @version $Id: TestSharedNodeOffset.java 4130 2009-03-19 20:04:51Z blaine.dev $
 */
public class TestSharedNodeOffset extends SimpleGame {
	Node file = null;
	
	Sphere s;
	
	SharedMesh shared;
	/**
	 * Entry point for the test,
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TestSharedNodeOffset app = new TestSharedNodeOffset();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	@Override
	protected void simpleUpdate() {
		s.updateGeometricState(tpf, true);
	}

	/**
	 * builds the trimesh.
	 * 
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	@Override
	protected void simpleInitGame() {
		display.setTitle("jME - Sphere");
		display.getRenderer().setBackgroundColor(ColorRGBA.white.clone());
		display.setMinSamples(4);
		
		file = new Node("main");
		
		s = new Sphere("s", 20,20,5);
		
		Pyramid p = new Pyramid("p", 10,5);
		
		SharedMesh ss = new SharedMesh("sharedSphere", s);
		ss.setLocalTranslation(new Vector3f(5,5,0));
		SharedMesh sp = new SharedMesh("sharedPyr", p);
		
		file.attachChild(ss);
		file.attachChild(sp);

		Node n1 = new Node("n1");

		for (int i = 0; i < 100; i++) {
			
			SharedNode sm = new SharedNode("Share" + i, file);
			sm.setLocalTranslation(new Vector3f(
					(float) Math.random() * 200 - 100,
					(float) Math.random() * 200 - 100,
					(float) Math.random() * 200 - 100));
			n1.attachChild(sm);
			
			if(i == 0) {
				shared = (SharedMesh)sm.getChild(0);
			}
		}

		n1.setLocalTranslation(new Vector3f(500, 0, 0));
		rootNode.attachChild(n1);

		SharedNode sm1 = new SharedNode("Share1", n1);
		sm1.setLocalTranslation(new Vector3f(200, 100, 200));
		sm1.setLocalScale(new Vector3f(0.5f, 0.5f, 0.5f));
		Matrix3f m = new Matrix3f();
		m.fromAngleAxis(2f, new Vector3f(1, 1, 0));
		sm1.setLocalRotation(m);
		rootNode.attachChild(sm1);

		SharedNode sm2 = new SharedNode("Share1", n1);
		sm2.setLocalTranslation(new Vector3f(-200, 100, 200));
		rootNode.attachChild(sm2);

		SharedNode sm3 = new SharedNode("Share1", n1);
		sm3.setLocalTranslation(new Vector3f(200, 100, -200));
		rootNode.attachChild(sm3);

		SharedNode sm4 = new SharedNode("Share1", n1);
		sm4.setLocalTranslation(new Vector3f(-200, -100, -200));
		rootNode.attachChild(sm4);

	}
}
