/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class org_lwjgl_WindowsSysImplementation */

#ifndef _Included_org_lwjgl_WindowsSysImplementation
#define _Included_org_lwjgl_WindowsSysImplementation
#ifdef __cplusplus
extern "C" {
#endif
#undef org_lwjgl_WindowsSysImplementation_JNI_VERSION
#define org_lwjgl_WindowsSysImplementation_JNI_VERSION 23L
/*
 * Class:     org_lwjgl_WindowsSysImplementation
 * Method:    nGetTime
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_org_lwjgl_WindowsSysImplementation_nGetTime
  (JNIEnv *, jclass);

/*
 * Class:     org_lwjgl_WindowsSysImplementation
 * Method:    nAlert
 * Signature: (JLjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_lwjgl_WindowsSysImplementation_nAlert
  (JNIEnv *, jclass, jlong, jstring, jstring);

/*
 * Class:     org_lwjgl_WindowsSysImplementation
 * Method:    initCommonControls
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_lwjgl_WindowsSysImplementation_initCommonControls
  (JNIEnv *, jclass);

/*
 * Class:     org_lwjgl_WindowsSysImplementation
 * Method:    nGetClipboard
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_lwjgl_WindowsSysImplementation_nGetClipboard
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
