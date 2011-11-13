/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opencl;

import org.lwjgl.*;
import java.nio.*;

/**
 * The core OpenCL 1.0 OpenGL interrop functionality. 
 */
public final class CL10GL {

	/**
	 * cl_gl_object_type 
	 */
	public static final int CL_GL_OBJECT_BUFFER = 0x2000,
		CL_GL_OBJECT_TEXTURE2D = 0x2001,
		CL_GL_OBJECT_TEXTURE3D = 0x2002,
		CL_GL_OBJECT_RENDERBUFFER = 0x2003;

	/**
	 * cl_gl_texture_info 
	 */
	public static final int CL_GL_TEXTURE_TARGET = 0x2004,
		CL_GL_MIPMAP_LEVEL = 0x2005;

	private CL10GL() {}

	public static CLMem clCreateFromGLBuffer(CLContext context, long flags, int bufobj, IntBuffer errcode_ret) {
		long function_pointer = CLCapabilities.clCreateFromGLBuffer;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (errcode_ret != null)
			BufferChecks.checkBuffer(errcode_ret, 1);
		CLMem __result = new CLMem(nclCreateFromGLBuffer(context.getPointer(), flags, bufobj, errcode_ret, errcode_ret != null ? errcode_ret.position() : 0, function_pointer), context);
		return __result;
	}
	static native long nclCreateFromGLBuffer(long context, long flags, int bufobj, IntBuffer errcode_ret, int errcode_ret_position, long function_pointer);

	public static CLMem clCreateFromGLTexture2D(CLContext context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret) {
		long function_pointer = CLCapabilities.clCreateFromGLTexture2D;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (errcode_ret != null)
			BufferChecks.checkBuffer(errcode_ret, 1);
		CLMem __result = new CLMem(nclCreateFromGLTexture2D(context.getPointer(), flags, target, miplevel, texture, errcode_ret, errcode_ret != null ? errcode_ret.position() : 0, function_pointer), context);
		return __result;
	}
	static native long nclCreateFromGLTexture2D(long context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret, int errcode_ret_position, long function_pointer);

	public static CLMem clCreateFromGLTexture3D(CLContext context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret) {
		long function_pointer = CLCapabilities.clCreateFromGLTexture3D;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (errcode_ret != null)
			BufferChecks.checkBuffer(errcode_ret, 1);
		CLMem __result = new CLMem(nclCreateFromGLTexture3D(context.getPointer(), flags, target, miplevel, texture, errcode_ret, errcode_ret != null ? errcode_ret.position() : 0, function_pointer), context);
		return __result;
	}
	static native long nclCreateFromGLTexture3D(long context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret, int errcode_ret_position, long function_pointer);

	public static CLMem clCreateFromGLRenderbuffer(CLContext context, long flags, int renderbuffer, IntBuffer errcode_ret) {
		long function_pointer = CLCapabilities.clCreateFromGLRenderbuffer;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (errcode_ret != null)
			BufferChecks.checkBuffer(errcode_ret, 1);
		CLMem __result = new CLMem(nclCreateFromGLRenderbuffer(context.getPointer(), flags, renderbuffer, errcode_ret, errcode_ret != null ? errcode_ret.position() : 0, function_pointer), context);
		return __result;
	}
	static native long nclCreateFromGLRenderbuffer(long context, long flags, int renderbuffer, IntBuffer errcode_ret, int errcode_ret_position, long function_pointer);

	public static int clGetGLObjectInfo(CLMem memobj, IntBuffer gl_object_type, IntBuffer gl_object_name) {
		long function_pointer = CLCapabilities.clGetGLObjectInfo;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (gl_object_type != null)
			BufferChecks.checkBuffer(gl_object_type, 1);
		if (gl_object_name != null)
			BufferChecks.checkBuffer(gl_object_name, 1);
		int __result = nclGetGLObjectInfo(memobj.getPointer(), gl_object_type, gl_object_type != null ? gl_object_type.position() : 0, gl_object_name, gl_object_name != null ? gl_object_name.position() : 0, function_pointer);
		return __result;
	}
	static native int nclGetGLObjectInfo(long memobj, IntBuffer gl_object_type, int gl_object_type_position, IntBuffer gl_object_name, int gl_object_name_position, long function_pointer);

	public static int clGetGLTextureInfo(CLMem memobj, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
		long function_pointer = CLCapabilities.clGetGLTextureInfo;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (param_value != null)
			BufferChecks.checkDirect(param_value);
		if (param_value_size_ret != null)
			BufferChecks.checkBuffer(param_value_size_ret, 1);
		int __result = nclGetGLTextureInfo(memobj.getPointer(), param_name, (param_value == null ? 0 : param_value.remaining()), param_value, param_value != null ? param_value.position() : 0, param_value_size_ret != null ? param_value_size_ret.getBuffer() : null, param_value_size_ret != null ? param_value_size_ret.positionByte() : 0, function_pointer);
		return __result;
	}
	static native int nclGetGLTextureInfo(long memobj, int param_name, long param_value_param_value_size, ByteBuffer param_value, int param_value_position, ByteBuffer param_value_size_ret, int param_value_size_ret_position, long function_pointer);

	public static int clEnqueueAcquireGLObjects(CLCommandQueue command_queue, PointerBuffer mem_objects, PointerBuffer event_wait_list, PointerBuffer event) {
		long function_pointer = CLCapabilities.clEnqueueAcquireGLObjects;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(mem_objects, 1);
		if (event_wait_list != null)
			BufferChecks.checkDirect(event_wait_list);
		if (event != null)
			BufferChecks.checkBuffer(event, 1);
		int __result = nclEnqueueAcquireGLObjects(command_queue.getPointer(), mem_objects.remaining(), mem_objects.getBuffer(), mem_objects.positionByte(), (event_wait_list == null ? 0 : event_wait_list.remaining()), event_wait_list != null ? event_wait_list.getBuffer() : null, event_wait_list != null ? event_wait_list.positionByte() : 0, event != null ? event.getBuffer() : null, event != null ? event.positionByte() : 0, function_pointer);
		if ( __result == CL10.CL_SUCCESS ) command_queue.registerCLEvent(event);
		return __result;
	}
	static native int nclEnqueueAcquireGLObjects(long command_queue, int mem_objects_num_objects, ByteBuffer mem_objects, int mem_objects_position, int event_wait_list_num_events_in_wait_list, ByteBuffer event_wait_list, int event_wait_list_position, ByteBuffer event, int event_position, long function_pointer);

	/** Overloads clEnqueueAcquireGLObjects. */
	public static int clEnqueueAcquireGLObjects(CLCommandQueue command_queue, CLMem mem_object, PointerBuffer event_wait_list, PointerBuffer event) {
		long function_pointer = CLCapabilities.clEnqueueAcquireGLObjects;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (event_wait_list != null)
			BufferChecks.checkDirect(event_wait_list);
		if (event != null)
			BufferChecks.checkBuffer(event, 1);
		int __result = nclEnqueueAcquireGLObjects(command_queue.getPointer(), 1, APIUtil.getBufferPointer().put(0, mem_object).getBuffer(), 0, (event_wait_list == null ? 0 : event_wait_list.remaining()), event_wait_list != null ? event_wait_list.getBuffer() : null, event_wait_list != null ? event_wait_list.positionByte() : 0, event != null ? event.getBuffer() : null, event != null ? event.positionByte() : 0, function_pointer);
		if ( __result == CL10.CL_SUCCESS ) command_queue.registerCLEvent(event);
		return __result;
	}

	public static int clEnqueueReleaseGLObjects(CLCommandQueue command_queue, PointerBuffer mem_objects, PointerBuffer event_wait_list, PointerBuffer event) {
		long function_pointer = CLCapabilities.clEnqueueReleaseGLObjects;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkBuffer(mem_objects, 1);
		if (event_wait_list != null)
			BufferChecks.checkDirect(event_wait_list);
		if (event != null)
			BufferChecks.checkBuffer(event, 1);
		int __result = nclEnqueueReleaseGLObjects(command_queue.getPointer(), mem_objects.remaining(), mem_objects.getBuffer(), mem_objects.positionByte(), (event_wait_list == null ? 0 : event_wait_list.remaining()), event_wait_list != null ? event_wait_list.getBuffer() : null, event_wait_list != null ? event_wait_list.positionByte() : 0, event != null ? event.getBuffer() : null, event != null ? event.positionByte() : 0, function_pointer);
		if ( __result == CL10.CL_SUCCESS ) command_queue.registerCLEvent(event);
		return __result;
	}
	static native int nclEnqueueReleaseGLObjects(long command_queue, int mem_objects_num_objects, ByteBuffer mem_objects, int mem_objects_position, int event_wait_list_num_events_in_wait_list, ByteBuffer event_wait_list, int event_wait_list_position, ByteBuffer event, int event_position, long function_pointer);

	/** Overloads clEnqueueReleaseGLObjects. */
	public static int clEnqueueReleaseGLObjects(CLCommandQueue command_queue, CLMem mem_object, PointerBuffer event_wait_list, PointerBuffer event) {
		long function_pointer = CLCapabilities.clEnqueueReleaseGLObjects;
		BufferChecks.checkFunctionAddress(function_pointer);
		if (event_wait_list != null)
			BufferChecks.checkDirect(event_wait_list);
		if (event != null)
			BufferChecks.checkBuffer(event, 1);
		int __result = nclEnqueueReleaseGLObjects(command_queue.getPointer(), 1, APIUtil.getBufferPointer().put(0, mem_object).getBuffer(), 0, (event_wait_list == null ? 0 : event_wait_list.remaining()), event_wait_list != null ? event_wait_list.getBuffer() : null, event_wait_list != null ? event_wait_list.positionByte() : 0, event != null ? event.getBuffer() : null, event != null ? event.positionByte() : 0, function_pointer);
		if ( __result == CL10.CL_SUCCESS ) command_queue.registerCLEvent(event);
		return __result;
	}
}
