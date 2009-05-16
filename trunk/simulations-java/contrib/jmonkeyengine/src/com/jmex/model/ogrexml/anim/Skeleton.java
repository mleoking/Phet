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

package com.jmex.model.ogrexml.anim;

import java.io.IOException;

import com.jme.math.Matrix4f;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;

/**
 * A skeleton is a hierarchy of bones.
 * Skeleton updates the world transforms to reflect the current local
 * animated matrixes.
 */
public final class Skeleton implements Savable {

    private Bone rootBone;
    private Bone[] boneList;

    /**
     * Contains the skinning matrices, multiplying it by a vertex effected by a bone
     * will cause it to go to the animated position.
     */
    private transient Matrix4f[] skinningMatrixes;

    /**
     * Creates a skeleton from a bone list. The root bone is found automatically.
     * @param boneList
     */
    public Skeleton(Bone[] boneList){
        this.boneList = boneList;
        for (Bone b : boneList){
            if (b.parent == null){
                if (rootBone != null){
                    System.err.println("CURRENT ROOT "+rootBone.name);
                    System.err.println("NEW ROOT "+b.name);
                    throw new IllegalStateException("Cannot have more than one root bone in skeleton");
                }

                rootBone = b;
            }
        }

        createSkinningMatrices();

        rootBone.update();
        rootBone.setBindingPose();
    }

    /**
     * Copy constructor.
     * Most of the skeleton data is deeply-copied except the bone bind and inverseBind transforms.
     * @param source
     */
    public Skeleton(Skeleton source){
        Bone[] sourceList = source.boneList;
        boneList = new Bone[sourceList.length];
        for (int i = 0; i < sourceList.length; i++)
            boneList[i] = new Bone(sourceList[i]);

        rootBone = recreateBoneStructure(source.rootBone);

        createSkinningMatrices();

        rootBone.update();
    }

    private void createSkinningMatrices(){
        skinningMatrixes = new Matrix4f[boneList.length];
        for (int i = 0; i < skinningMatrixes.length; i++)
            skinningMatrixes[i] = new Matrix4f();
    }

    private Bone recreateBoneStructure(Bone sourceRoot){
        Bone targetRoot = getBone(sourceRoot.name);

        for (Bone sourceChild : sourceRoot.children){
            // find my version of the child
            Bone targetChild = getBone(sourceChild.name);
            targetRoot.addChild(targetChild);
            recreateBoneStructure(sourceChild);
        }

        return targetRoot;
    }

    public Bone getRoot(){
        return rootBone;
    }

    public Bone getBone(int index){
        return boneList[index];
    }

    public Bone getBone(String name){
        for (int i = 0; i < boneList.length; i++)
            if (boneList[i].name.equals(name))
                return boneList[i];

        return null;
    }

    public int getBoneIndex(Bone bone){
        for (int i = 0; i < boneList.length; i++)
            if (boneList[i] == bone)
                return i;

        return -1;
    }

    public Matrix4f[] computeSkinningMatrices(){
        for (int i = 0; i < boneList.length; i++){
            boneList[i].getOffsetTransform(skinningMatrixes[i]);
        }
        return skinningMatrixes;
    }

    public int getBoneCount() {
        return boneList.length;
    }

    public void sendToShader(GLSLShaderObjectsState shader){
        Matrix4f[] skinningMats = computeSkinningMatrices();
        // NOTE: Not supported by jME2 without a patch.
        //shader.setUniform("boneMatrices", skinningMats, true);
        throw new UnsupportedOperationException("Hardware skinning cannot be used without a jME2 patch");
    }

    /**
     * Used for binary loading as a Savable; the object must be constructed,
     * then the parameters usually present in the constructor for this class are
     * restored from the file the object was saved to.
     */
    public Skeleton() {

    }

    public Class getClassTag() {
        return this.getClass();
    }

    public void read(JMEImporter im) throws IOException {
        InputCapsule input = im.getCapsule(this);
        rootBone = (Bone) input.readSavable("rootBone", null);
        Savable[] boneListAsSavable = input.readSavableArray("boneList", null);

        boneList = new Bone[boneListAsSavable.length];
        for (int i = 0; i < boneListAsSavable.length; i++)
            boneList[i] = (Bone) boneListAsSavable[i];

        createSkinningMatrices();
        rootBone.update();
        rootBone.setBindingPose();
    }

    public void write(JMEExporter ex) throws IOException {
        OutputCapsule output = ex.getCapsule(this);
        output.write(rootBone, "rootBone", null);
        output.write(boneList, "boneList", null);
    }
}
