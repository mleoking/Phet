/*
 * Copyright (c) 2002-2010 LWJGL Project
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
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
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
package org.lwjgl.opencl;

import org.lwjgl.PointerWrapperAbstract;

/**
 * Instances of this class can be used to receive OpenCL program build notifications.
 * A single CLBuildProgramCallback instance should only be used with programs created
 * in the same CLContext.
 *
 * @author Spasi
 */
public abstract class CLBuildProgramCallback extends PointerWrapperAbstract {

	private CLContext context;

	protected CLBuildProgramCallback() {
		super(CallbackUtil.getBuildProgramCallback());
	}

	/**
	 * Sets the context that contains the CLPrograms to which we're registered.
	 *
	 * @param context the CLContext object
	 */
	void setContext(final CLContext context) {
		this.context = context;
	}

	/**
	 * Called from native code.
	 *
	 * @param program_address the CLProgram object pointer
	 */
	private void handleMessage(long program_address) {
		handleMessage(context.getCLProgram(program_address));
	}

	/**
	 * The callback method.
	 *
	 * @param program the CLProgram object that was built
	 */
	protected abstract void handleMessage(CLProgram program);

}