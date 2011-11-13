/* MACHINE GENERATED FILE, DO NOT EDIT */

#include <jni.h>
#include "extcl.h"

typedef CL_API_ENTRY cl_int (CL_API_CALL *clEnqueueMigrateMemObjectEXTPROC) (cl_command_queue command_queue, cl_uint num_mem_objects, const cl_mem * mem_objects, cl_mem_migration_flags_ext flags, cl_uint num_events_in_wait_list, const cl_event * event_wait_list, cl_event * event);

JNIEXPORT jint JNICALL Java_org_lwjgl_opencl_EXTMigrateMemobject_nclEnqueueMigrateMemObjectEXT(JNIEnv *env, jclass clazz, jlong command_queue, jint num_mem_objects, jobject mem_objects, jint mem_objects_position, jlong flags, jint num_events_in_wait_list, jobject event_wait_list, jint event_wait_list_position, jobject event, jint event_position, jlong function_pointer) {
	const cl_mem *mem_objects_address = ((const cl_mem *)(((char *)(*env)->GetDirectBufferAddress(env, mem_objects)) + mem_objects_position));
	const cl_event *event_wait_list_address = ((const cl_event *)(((char *)safeGetBufferAddress(env, event_wait_list)) + event_wait_list_position));
	cl_event *event_address = ((cl_event *)(((char *)safeGetBufferAddress(env, event)) + event_position));
	clEnqueueMigrateMemObjectEXTPROC clEnqueueMigrateMemObjectEXT = (clEnqueueMigrateMemObjectEXTPROC)((intptr_t)function_pointer);
	cl_int __result = clEnqueueMigrateMemObjectEXT((cl_command_queue)(intptr_t)command_queue, num_mem_objects, mem_objects_address, flags, num_events_in_wait_list, event_wait_list_address, event_address);
	return __result;
}

