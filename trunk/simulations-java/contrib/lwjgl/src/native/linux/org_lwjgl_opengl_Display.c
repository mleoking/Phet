/* 
 * Copyright (c) 2002-2008 LWJGL Project
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

/**
 * $Id: org_lwjgl_opengl_Display.c 3453 2010-10-28 21:39:55Z kappa1 $
 *
 * Linux specific display functions.
 *
 * @author elias_naur <elias_naur@users.sourceforge.net>
 * @version $Revision: 3453 $
 */

#include <X11/X.h>
#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/extensions/xf86vmode.h>
#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <jni.h>
#include <jawt_md.h>
#include "common_tools.h"
#include "extgl.h"
#include "extgl_glx.h"
#include "context.h"
#include "org_lwjgl_opengl_LinuxDisplay.h"
#include "org_lwjgl_opengl_LinuxDisplayPeerInfo.h"
#include "org_lwjgl_LinuxSysImplementation.h"

#define ERR_MSG_SIZE 1024

typedef struct {
	unsigned long flags;
	unsigned long functions;
	unsigned long decorations;
	long input_mode;
	unsigned long status;
} MotifWmHints;

#define MWM_HINTS_DECORATIONS   (1L << 1)

static GLXWindow glx_window = None;

static Colormap cmap;
static int current_depth;
static Pixmap current_icon_pixmap;	
static Pixmap current_icon_mask_pixmap;	

static Visual *current_visual;

static bool checkXError(JNIEnv *env, Display *disp) {
	XSync(disp, False);
	return (*env)->ExceptionCheck(env) == JNI_FALSE;
}

static int global_error_handler(Display *disp, XErrorEvent *error) {
	JNIEnv *env = getThreadEnv();
	if (env != NULL) {
		jclass org_lwjgl_LinuxDisplay_class = (*env)->FindClass(env, "org/lwjgl/opengl/LinuxDisplay");
		if (org_lwjgl_LinuxDisplay_class == NULL) {
			// Don't propagate error
			(*env)->ExceptionClear(env);
			return 0;
		}
		jmethodID handler_method = (*env)->GetStaticMethodID(env, org_lwjgl_LinuxDisplay_class, "globalErrorHandler", "(JJJJJJJ)I");
		if (handler_method == NULL)
			return 0;
		return (*env)->CallStaticIntMethod(env, org_lwjgl_LinuxDisplay_class, handler_method, (jlong)(intptr_t)disp, (jlong)(intptr_t)error, 
				(jlong)(intptr_t)error->display, (jlong)error->serial, (jlong)error->error_code, (jlong)error->request_code, (jlong)error->minor_code);
	} else
		return 0;
}

static jlong openDisplay(JNIEnv *env) {
	Display *display_connection = XOpenDisplay(NULL);
	if (display_connection == NULL) {
		throwException(env, "Could not open X display connection");
		return (intptr_t)NULL;
	}
	return (intptr_t)display_connection;
}

JNIEXPORT jint JNICALL Java_org_lwjgl_DefaultSysImplementation_getJNIVersion
  (JNIEnv *env, jobject ignored) {
	return org_lwjgl_LinuxSysImplementation_JNI_VERSION;
}

JNIEXPORT jstring JNICALL Java_org_lwjgl_opengl_LinuxDisplay_getErrorText(JNIEnv *env, jclass unused, jlong display_ptr, jlong error_code) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	char err_msg_buffer[ERR_MSG_SIZE];
	XGetErrorText(disp, error_code, err_msg_buffer, ERR_MSG_SIZE);
	err_msg_buffer[ERR_MSG_SIZE - 1] = '\0';
	return NewStringNativeWithLength(env, err_msg_buffer, strlen(err_msg_buffer));
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_LinuxDisplay_callErrorHandler(JNIEnv *env, jclass unused, jlong handler_ptr, jlong display_ptr, jlong event_ptr) {
	XErrorHandler handler = (XErrorHandler)(intptr_t)handler_ptr;
	Display *disp = (Display *)(intptr_t)display_ptr;
	XErrorEvent *event = (XErrorEvent *)(intptr_t)event_ptr;
	return (jint)handler(disp, event);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_setErrorHandler(JNIEnv *env, jclass unused) {
	return (intptr_t)XSetErrorHandler(global_error_handler);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_resetErrorHandler(JNIEnv *env, jclass unused, jlong handler_ptr) {
	XErrorHandler handler = (XErrorHandler)(intptr_t)handler_ptr;
	return (intptr_t)XSetErrorHandler(handler);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_sync(JNIEnv *env, jclass unused, jlong display_ptr, jboolean throw_away_events) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	XSync(disp, throw_away_events ? True : False);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nGetDefaultScreen(JNIEnv *env, jclass unused, jlong display_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	return XDefaultScreen(disp);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nInternAtom(JNIEnv *env, jclass unused, jlong display_ptr, jstring atom_name_obj, jboolean only_if_exists) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	char *atom_name = GetStringNativeChars(env, atom_name_obj);
	if (atom_name == NULL)
		return 0;
	Atom atom = XInternAtom(disp, atom_name, only_if_exists ? True : False);
	free(atom_name);
	return atom;
}

static void setDecorations(Display *disp, Window window, int dec) {
	Atom motif_hints_atom = XInternAtom(disp, "_MOTIF_WM_HINTS", False);
	MotifWmHints motif_hints;
	motif_hints.flags = MWM_HINTS_DECORATIONS;
	motif_hints.decorations = dec;
	XChangeProperty(disp, window, motif_hints_atom, motif_hints_atom, 32, PropModeReplace, (unsigned char *)&motif_hints, sizeof(MotifWmHints)/sizeof(long));
}

static bool isLegacyFullscreen(jint window_mode) {
	return window_mode == org_lwjgl_opengl_LinuxDisplay_FULLSCREEN_LEGACY;
}

static void setWindowTitle(Display *disp, Window window, const char *title) {
	XStoreName(disp, window, title);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_openDisplay(JNIEnv *env, jclass clazz) {
	return openDisplay(env);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_closeDisplay(JNIEnv *env, jclass clazz, jlong display) {
	Display *disp = (Display *)(intptr_t)display;
	XCloseDisplay(disp);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplayPeerInfo_initDrawable(JNIEnv *env, jclass clazz, jlong window, jobject peer_info_handle) {
	X11PeerInfo *peer_info = (*env)->GetDirectBufferAddress(env, peer_info_handle);
	if (peer_info->glx13)
		peer_info->drawable = glx_window;
	else
		peer_info->drawable = window;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplayPeerInfo_initDefaultPeerInfo(JNIEnv *env, jclass clazz, jlong display, jint screen, jobject peer_info_handle, jobject pixel_format) {
	Display *disp = (Display *)(intptr_t)display;
	initPeerInfo(env, peer_info_handle, disp, screen, pixel_format, true, GLX_WINDOW_BIT, true, false);
}
  
JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nSetTitle(JNIEnv * env, jclass clazz, jlong display, jlong window_ptr, jstring title_obj) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	char * title = GetStringNativeChars(env, title_obj);
	setWindowTitle(disp, window, title);
	free(title);
}

static void freeIconPixmap(Display *disp) {
	if (current_icon_mask_pixmap != 0) {
		XFreePixmap(disp, current_icon_mask_pixmap);
		current_icon_mask_pixmap = 0;
	}
	if (current_icon_pixmap != 0) {
		XFreePixmap(disp, current_icon_pixmap);
		current_icon_pixmap = 0;
	}
}

static void destroyWindow(JNIEnv *env, Display *disp, Window window) {
	if (glx_window != None) {
		lwjgl_glXDestroyWindow(disp, glx_window);
		glx_window = None;
	}
	XDestroyWindow(disp, window);
	XFreeColormap(disp, cmap);
	freeIconPixmap(disp);
}

static bool isNetWMFullscreenSupported(JNIEnv *env, Display *disp, int screen) {
	unsigned long nitems;
	Atom actual_type;
	int actual_format;
	unsigned long bytes_after;
	Atom *supported_list;
	Atom netwm_supported_atom = XInternAtom(disp, "_NET_SUPPORTED", False);
	int result = XGetWindowProperty(disp, RootWindow(disp, screen), netwm_supported_atom, 0, 10000, False, AnyPropertyType, &actual_type, &actual_format, &nitems, &bytes_after, (void *)&supported_list);
	if (result != Success) {
		throwException(env, "Unable to query _NET_SUPPORTED window property");
		return false;
	}
	Atom fullscreen_atom = XInternAtom(disp, "_NET_WM_STATE_FULLSCREEN", False);
	bool supported = false;
	unsigned long i;
	for (i = 0; i < nitems; i++) {
		if (fullscreen_atom == supported_list[i]) {
			supported = true;
			break;
		}
	}
	XFree(supported_list);
	return supported;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nIsNetWMFullscreenSupported(JNIEnv *env, jclass unused, jlong display, jint screen) {
	Display *disp = (Display *)(intptr_t)display;
	return isNetWMFullscreenSupported(env, disp, screen) ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nReshape(JNIEnv *env, jclass clazz, jlong display, jlong window_ptr, jint x, jint y, jint width, jint height) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	XMoveWindow(disp, window, x, y);
	XResizeWindow(disp, window, width, height);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_synchronize(JNIEnv *env, jclass clazz, jlong display, jboolean synchronize) {
	Display *disp = (Display *)(intptr_t)display;
	XSynchronize(disp, synchronize ? True : False);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_getRootWindow(JNIEnv *env, jclass clazz, jlong display, jint screen) {
	Display *disp = (Display *)(intptr_t)display;
	return RootWindow(disp, screen);
}

static void updateWindowHints(JNIEnv *env, Display *disp, Window window) {
	XWMHints* win_hints = XAllocWMHints();
	if (win_hints == NULL) {
		throwException(env, "XAllocWMHints failed");
		return;
	}
	
	win_hints->flags = InputHint;
	win_hints->input = True;
	if (current_icon_pixmap != 0) {
		win_hints->flags |= IconPixmapHint;
		win_hints->icon_pixmap = current_icon_pixmap;
	}
	if (current_icon_mask_pixmap != 0) {
		win_hints->flags |= IconMaskHint;
		win_hints->icon_mask = current_icon_mask_pixmap;
	}

	XSetWMHints(disp, window, win_hints);
	XFree(win_hints);
	XFlush(disp);
}

static Window createWindow(JNIEnv* env, Display *disp, int screen, jint window_mode, X11PeerInfo *peer_info, int x, int y, int width, int height, jboolean undecorated, long parent_handle) {
	Window parent = (Window)parent_handle;
	Window win;
	XSetWindowAttributes attribs;
	int attribmask;

	XVisualInfo *vis_info = getVisualInfoFromPeerInfo(env, peer_info);
	if (vis_info == NULL)
		return false;
	cmap = XCreateColormap(disp, parent, vis_info->visual, AllocNone);
	attribs.colormap = cmap;
	attribs.border_pixel = 0;
	attribs.event_mask = ExposureMask | FocusChangeMask | VisibilityChangeMask | StructureNotifyMask | KeyPressMask | KeyReleaseMask | ButtonPressMask | ButtonReleaseMask | PointerMotionMask| EnterWindowMask | LeaveWindowMask;
	attribmask = CWColormap | CWEventMask | CWBorderPixel;
	if (isLegacyFullscreen(window_mode)) {
		attribmask |= CWOverrideRedirect;
		attribs.override_redirect = True;
	}
	win = XCreateWindow(disp, parent, x, y, width, height, 0, vis_info->depth, InputOutput, vis_info->visual, attribmask, &attribs);
	
	current_depth = vis_info->depth;
	current_visual = vis_info->visual;
	
	XFree(vis_info);
	if (!checkXError(env, disp)) {
		XFreeColormap(disp, cmap);
		return false;
	}
//	printfDebugJava(env, "Created window");
	if (undecorated) {
		// Use Motif decoration hint property and hope the window manager respects them
		setDecorations(disp, win, 0);
	}
	XSizeHints * size_hints = XAllocSizeHints();
	size_hints->flags = PMinSize | PMaxSize;
	size_hints->min_width = width;
	size_hints->max_width = width;
	size_hints->min_height = height;
	size_hints->max_height = height;
	XSetWMNormalHints(disp, win, size_hints);
	updateWindowHints(env, disp, win);
	XFree(size_hints);
#define NUM_ATOMS 1
	Atom protocol_atoms[NUM_ATOMS] = {XInternAtom(disp, "WM_DELETE_WINDOW", False)/*, XInternAtom(disp, "WM_TAKE_FOCUS", False)*/};
	XSetWMProtocols(disp, win, protocol_atoms, NUM_ATOMS);
	if (window_mode == org_lwjgl_opengl_LinuxDisplay_FULLSCREEN_NETWM) {
		Atom fullscreen_atom = XInternAtom(disp, "_NET_WM_STATE_FULLSCREEN", False);
		XChangeProperty(disp, win, XInternAtom(disp, "_NET_WM_STATE", False),
						XInternAtom(disp, "ATOM", False), 32, PropModeReplace, (const unsigned char*)&fullscreen_atom, 1);
	}
	if (!checkXError(env, disp)) {
		destroyWindow(env, disp, win);
		return 0;
	}
	return win;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_reparentWindow(JNIEnv *env, jclass unused, jlong display, jlong window_ptr, jlong parent_ptr, jint x, jint y) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	Window parent = (Window)parent_ptr;
	XReparentWindow(disp, window, parent, x, y);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_mapRaised(JNIEnv *env, jclass unused, jlong display, jlong window_ptr) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	XMapRaised(disp, window);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_getParentWindow(JNIEnv *env, jclass unused, jlong display, jlong window_ptr) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	Window root, parent;
	Window *children;
	unsigned int nchildren;
	if (XQueryTree(disp, window, &root, &parent, &children, &nchildren) == 0) {
		throwException(env, "XQueryTree failed");
		return None;
	}
	if (children != NULL)
		XFree(children);
	return parent;
}

JNIEXPORT jboolean JNICALL Java_org_lwjgl_opengl_LinuxDisplay_hasProperty(JNIEnv *env, jclass unusued, jlong display, jlong window_ptr, jlong property_ptr) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	Atom property = (Atom)property_ptr;
	int num_props;
	Atom *properties = XListProperties(disp, window, &num_props);
	if (properties == NULL)
		return JNI_FALSE;
	jboolean result = JNI_FALSE;
	for (int i = 0; i < num_props; i++) {
		if (properties[i] == property) {
			result = JNI_TRUE;
			break;
		}
	}
	XFree(properties);
	return result;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_setInputFocus(JNIEnv *env, jclass clazz, jlong display, jlong window_ptr, jlong time) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	XSetInputFocus(disp, window, RevertToParent, time);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nCreateWindow(JNIEnv *env, jclass clazz, jlong display, jint screen, jobject peer_info_handle, jobject mode, jint window_mode, jint x, jint y, jboolean undecorated, jlong parent_handle) {
	Display *disp = (Display *)(intptr_t)display;
	X11PeerInfo *peer_info = (*env)->GetDirectBufferAddress(env, peer_info_handle);
	GLXFBConfig *fb_config = NULL;
	if (peer_info->glx13) {
		fb_config = getFBConfigFromPeerInfo(env, peer_info);
		if (fb_config == NULL)
			return 0;
	}
	jclass cls_displayMode = (*env)->GetObjectClass(env, mode);
	jfieldID fid_width = (*env)->GetFieldID(env, cls_displayMode, "width", "I");
	jfieldID fid_height = (*env)->GetFieldID(env, cls_displayMode, "height", "I");
	int width = (*env)->GetIntField(env, mode, fid_width);
	int height = (*env)->GetIntField(env, mode, fid_height);
	Window win = createWindow(env, disp, screen, window_mode, peer_info, x, y, width, height, undecorated, parent_handle);
	if ((*env)->ExceptionOccurred(env)) {
		return 0;
	}
	if (peer_info->glx13) {
		glx_window = lwjgl_glXCreateWindow(disp, *fb_config, win, NULL);
		XFree(fb_config);
	}
	if (!checkXError(env, disp)) {
		lwjgl_glXDestroyWindow(disp, glx_window);
		destroyWindow(env, disp, win);
	}
	return win;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nDestroyWindow(JNIEnv *env, jclass clazz, jlong display, jlong window_ptr) {
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	destroyWindow(env, disp, window);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nLockAWT(JNIEnv *env, jclass clazz) {
	JAWT jawt;
	jawt.version = JAWT_VERSION_1_4;
	if (JAWT_GetAWT(env, &jawt) != JNI_TRUE) {
		throwException(env, "GetAWT failed");
		return;
	}
	jawt.Lock(env);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nUnlockAWT(JNIEnv *env, jclass clazz) {
	JAWT jawt;
	jawt.version = JAWT_VERSION_1_4;
	if (JAWT_GetAWT(env, &jawt) != JNI_TRUE) {
		throwException(env, "GetAWT failed");
		return;
	}
	jawt.Unlock(env);
}

static Pixmap createPixmapFromBuffer(JNIEnv *env, Display *disp, Window window, char *data, int data_size, int width, int height, int format, int depth) {
	Pixmap pixmap = XCreatePixmap(disp, window, width, height, depth);
	/* We need to copy the image data since XDestroyImage will also free its data buffer, which can't be allowed
	 * since the data buffer is managed by the jvm (it's the storage for the direct ByteBuffer)
	 */
	char *icon_copy = (char *)malloc(sizeof(*icon_copy)*data_size);

	if (icon_copy == NULL) {
		XFreePixmap(disp, pixmap);
		throwException(env, "malloc failed");
		return None;
	}
	memcpy(icon_copy, data, data_size);
	XImage *image = XCreateImage(disp, current_visual, depth, format, 0, icon_copy, width, height, 32, 0);
	if (image == NULL) {
		XFreePixmap(disp, pixmap);
		free(icon_copy);
		throwException(env, "XCreateImage failed");
		return None;
	}
	
	GC gc = XCreateGC(disp, pixmap, 0, NULL);
	XPutImage(disp, pixmap, gc, image, 0, 0, 0, 0, width, height);
	XFreeGC(disp, gc);
	XDestroyImage(image);
	// We won't free icon_copy because it is freed by XDestroyImage
	return pixmap;
}

static void setIcon(JNIEnv *env, Display *disp, Window window, char *rgb_data, int rgb_size, char *mask_data, int mask_size, int width, int height) {
	freeIconPixmap(disp);
	current_icon_pixmap = createPixmapFromBuffer(env, disp, window, rgb_data, rgb_size, width, height, ZPixmap, current_depth);
	if ((*env)->ExceptionCheck(env))
		return;
	current_icon_mask_pixmap = createPixmapFromBuffer(env, disp, window, mask_data, mask_size, width, height, XYPixmap, 1);
	if ((*env)->ExceptionCheck(env)) {
		freeIconPixmap(disp);
		return;
	}
	
	updateWindowHints(env, disp, window);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nSetWindowIcon
  (JNIEnv *env, jclass clazz, jlong display, jlong window_ptr, jobject icon_rgb_buffer, jint rgb_size, jobject icon_mask_buffer, jint mask_size, jint width, jint height)
{
	Display *disp = (Display *)(intptr_t)display;
	Window window = (Window)window_ptr;
	char *rgb_data= (char *)(*env)->GetDirectBufferAddress(env, icon_rgb_buffer);
	char *mask_data= (char *)(*env)->GetDirectBufferAddress(env, icon_mask_buffer);

	setIcon(env, disp, window, rgb_data, rgb_size, mask_data, mask_size, width, height);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nUngrabKeyboard(JNIEnv *env, jclass unused, jlong display_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	return XUngrabKeyboard(disp, CurrentTime);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nGrabKeyboard(JNIEnv *env, jclass unused, jlong display_ptr, jlong window_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	Window win = (Window)window_ptr;
	return XGrabKeyboard(disp, win, False, GrabModeAsync, GrabModeAsync, CurrentTime);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nGrabPointer(JNIEnv *env, jclass unused, jlong display_ptr, jlong window_ptr, jlong cursor_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	Window win = (Window)window_ptr;
	Cursor cursor = (Cursor)cursor_ptr;
	int grab_mask = PointerMotionMask | ButtonPressMask | ButtonReleaseMask;
	return XGrabPointer(disp, win, False, grab_mask, GrabModeAsync, GrabModeAsync, win, cursor, CurrentTime);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nSetViewPort(JNIEnv *env, jclass unused, jlong display_ptr, jlong window_ptr, jint screen) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	Window win = (Window)window_ptr;
	XWindowAttributes win_attribs;

	XGetWindowAttributes(disp, win, &win_attribs);
	XF86VidModeSetViewPort(disp, screen, win_attribs.x, win_attribs.y);
}

JNIEXPORT jint JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nUngrabPointer(JNIEnv *env, jclass unused, jlong display_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	return XUngrabPointer(disp, CurrentTime);
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nDefineCursor(JNIEnv *env, jclass unused, jlong display_ptr, jlong window_ptr, jlong cursor_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	Window win = (Window)window_ptr;
	Cursor cursor = (Cursor)cursor_ptr;
	XDefineCursor(disp, win, cursor);
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nCreateBlankCursor(JNIEnv *env, jclass unused, jlong display_ptr, jlong window_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	Window win = (Window)window_ptr;
	unsigned int best_width, best_height;
	if (XQueryBestCursor(disp, win, 1, 1, &best_width, &best_height) == 0) {
		throwException(env, "Could not query best cursor size");
		return false;
	}
	Pixmap mask = XCreatePixmap(disp, win, best_width, best_height, 1);
	XGCValues gc_values;
	gc_values.foreground = 0;
	GC gc = XCreateGC(disp, mask, GCForeground, &gc_values);
	XFillRectangle(disp, mask, gc, 0, 0, best_width, best_height);
	XFreeGC(disp, gc);
	XColor dummy_color;
	Cursor cursor = XCreatePixmapCursor(disp, mask, mask, &dummy_color, &dummy_color, 0, 0);
	XFreePixmap(disp, mask);
	return cursor;
}

JNIEXPORT jlong JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nGetInputFocus(JNIEnv *env, jclass unused, jlong display_ptr) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	int revert_mode;
	Window win;
	XGetInputFocus(disp, &win, &revert_mode);
	return win;
}

JNIEXPORT void JNICALL Java_org_lwjgl_opengl_LinuxDisplay_nIconifyWindow(JNIEnv *env, jclass unused, jlong display_ptr, jlong window_ptr, jint screen) {
	Display *disp = (Display *)(intptr_t)display_ptr;
	Window win = (Window)window_ptr;
	XIconifyWindow(disp, win, screen);
}
