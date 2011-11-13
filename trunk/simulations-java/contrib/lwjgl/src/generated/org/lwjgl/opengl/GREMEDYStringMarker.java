/* MACHINE GENERATED FILE, DO NOT EDIT */

package org.lwjgl.opengl;

import org.lwjgl.*;
import java.nio.*;

public final class GREMEDYStringMarker {

	private GREMEDYStringMarker() {}

	public static void glStringMarkerGREMEDY(ByteBuffer string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glStringMarkerGREMEDY;
		BufferChecks.checkFunctionAddress(function_pointer);
		BufferChecks.checkDirect(string);
		nglStringMarkerGREMEDY(string.remaining(), string, string.position(), function_pointer);
	}
	static native void nglStringMarkerGREMEDY(int string_len, ByteBuffer string, int string_position, long function_pointer);

	/** Overloads glStringMarkerGREMEDY. */
	public static void glStringMarkerGREMEDY(CharSequence string) {
		ContextCapabilities caps = GLContext.getCapabilities();
		long function_pointer = caps.glStringMarkerGREMEDY;
		BufferChecks.checkFunctionAddress(function_pointer);
		nglStringMarkerGREMEDY(string.length(), APIUtil.getBuffer(string), 0, function_pointer);
	}
}
