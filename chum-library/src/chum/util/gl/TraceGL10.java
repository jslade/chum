package chum.util.gl;

import javax.microedition.khronos.opengles.GL10;


/**
 * Wrapper for basic OpenGL ES 1.0 that logs every OpenGL call
 */
public class TraceGL10 extends TraceGL implements GL10 {

    /** The real GL instance the calls were intended for */
    public GL10 realGL10;


    public TraceGL10() {
        super();
    }


    public void glActiveTexture(int texture) {
        trace("glActiveTexture", texture);
        if (pass && realGL10 != null)
            realGL10.glActiveTexture(texture);
    }


    public void glAlphaFunc(int func, float ref) {
        trace("glAlphaFunc", func, ref);
        if (pass && realGL10 != null)
            realGL10.glAlphaFunc(func, ref);
    }


    public void glAlphaFuncx(int func, int ref) {
        trace("glAlphaFuncx", func, ref);
        if (pass && realGL10 != null)
            realGL10.glAlphaFuncx(func, ref);
    }


    public void glBindTexture(int target, int texture) {
        trace("glBindTexture", target, texture);
        if (pass && realGL10 != null)
            realGL10.glBindTexture(target, texture);
    }


    public void glBlendFunc(int sfactor, int dfactor) {
        trace("glBlendFunc", sfactor, dfactor);
        if (pass && realGL10 != null)
            realGL10.glBlendFunc(sfactor, dfactor);
    }


    public void glClear(int mask) {
        trace("glClear", mask);
        if (pass && realGL10 != null)
            realGL10.glClear(mask);
    }


    public void glClearColor(float red, float green, float blue, float alpha) {
        trace("glClearColor", red, green, blue, alpha);
        if (pass && realGL10 != null)
            realGL10.glClearColor(red, green, blue, alpha);
    }


    public void glClearColorx(int red, int green, int blue, int alpha) {
        trace("glClearColorx", red, green, blue, alpha);
        if (pass && realGL10 != null)
            realGL10.glClearColorx(red, green, blue, alpha);
    }


    public void glClearDepthf(float depth) {
        trace("glClearDepthf", depth);
        if (pass && realGL10 != null)
            realGL10.glClearDepthf(depth);
    }


    public void glClearDepthx(int depth) {
        trace("glClearDepthx", depth);
        if (pass && realGL10 != null)
            realGL10.glClearDepthx(depth);
    }


    public void glClearStencil(int s) {
        trace("glClearStencil", s);
        if (pass && realGL10 != null)
            realGL10.glClearStencil(s);
    }


    public void glClientActiveTexture(int texture) {
        trace("glClientActiveTexture", texture);
        if (pass && realGL10 != null)
            realGL10.glClientActiveTexture(texture);
    }


    public void glColor4f(float red, float green, float blue, float alpha) {
        trace("glColor4f", red, green, blue, alpha);
        if (pass && realGL10 != null)
            realGL10.glColor4f(red, green, blue, alpha);
    }


    public void glColor4x(int red, int green, int blue, int alpha) {
        trace("glColor4x", red, green, blue, alpha);
        if (pass && realGL10 != null)
            realGL10.glColor4x(red, green, blue, alpha);
    }


    public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha) {
        trace("glColorMask", red, green, blue, alpha);
        if (pass && realGL10 != null)
            realGL10.glColorMask(red, green, blue, alpha);
    }


    public void glColorPointer(int size, int type, int stride, java.nio.Buffer pointer) {
        trace("glColorPointer", size, type, stride, pointer);
        if (pass && realGL10 != null)
            realGL10.glColorPointer(size, type, stride, pointer);
    }


    public void glCompressedTexImage2D(int target, int level, int internalformat,
            int width, int height, int border, int imageSize, java.nio.Buffer data) {
        trace("glCompressedTexImage2D", target, level, internalformat, width, height,
              border, imageSize, data);
        if (pass && realGL10 != null)
            realGL10.glCompressedTexImage2D(target, level, internalformat, width, height,
                                          border, imageSize, data);
    }


    public void glCompressedTexSubImage2D(int target, int level, int xoffset,
            int yoffset, int width, int height, int format, int imageSize,
            java.nio.Buffer data) {
        trace("glCompressedTexSubImage2D", target, level, xoffset, yoffset, width,
              height, format, imageSize, data);
        if (pass && realGL10 != null)
            realGL10.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width,
                                             height, format, imageSize, data);
    }


    public void glCopyTexImage2D(int target, int level, int internalformat, int x, int y,
            int width, int height, int border) {
        trace("glCopyTexImage2D", target, level, internalformat, x, y, width, height,
              border);
        if (pass && realGL10 != null)
            realGL10.glCopyTexImage2D(target, level, internalformat, x, y, width, height,
                                    border);
    }


    public void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset,
            int x, int y, int width, int height) {
        trace("glCopyTexSubImage2D", target, level, xoffset, yoffset, x, y, width, height);
        if (pass && realGL10 != null)
            realGL10.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width,
                                       height);
    }


    public void glCullFace(int mode) {
        trace("glCullFace", mode);
        if (pass && realGL10 != null)
            realGL10.glCullFace(mode);
    }


    public void glDeleteTextures(int n, int[] textures, int offset) {
        trace("glDeleteTextures", n, textures, offset);
        if (pass && realGL10 != null)
            realGL10.glDeleteTextures(n, textures, offset);
    }


    public void glDeleteTextures(int n, java.nio.IntBuffer textures) {
        trace("glDeleteTextures", n, textures);
        if (pass && realGL10 != null)
            realGL10.glDeleteTextures(n, textures);
    }


    public void glDepthFunc(int func) {
        trace("glDepthFunc", func);
        if (pass && realGL10 != null)
            realGL10.glDepthFunc(func);
    }


    public void glDepthMask(boolean flag) {
        trace("glDepthMask", flag);
        if (pass && realGL10 != null)
            realGL10.glDepthMask(flag);
    }


    public void glDepthRangef(float zNear, float zFar) {
        trace("glDepthRangef", zNear, zFar);
        if (pass && realGL10 != null)
            realGL10.glDepthRangef(zNear, zFar);
    }


    public void glDepthRangex(int zNear, int zFar) {
        trace("glDepthRangex", zNear, zFar);
        if (pass && realGL10 != null)
            realGL10.glDepthRangex(zNear, zFar);
    }


    public void glDisable(int cap) {
        trace("glDisable", cap);
        if (pass && realGL10 != null)
            realGL10.glDisable(cap);
    }


    public void glDisableClientState(int array) {
        trace("glDisableClientState", array);
        if (pass && realGL10 != null)
            realGL10.glDisableClientState(array);
    }


    public void glDrawArrays(int mode, int first, int count) {
        trace("glDrawArrays", mode, first, count);
        if (pass && realGL10 != null)
            realGL10.glDrawArrays(mode, first, count);
    }


    public void glDrawElements(int mode, int count, int type, java.nio.Buffer indices) {
        trace("glDrawElements", mode, count, type, indices);
        if (pass && realGL10 != null)
            realGL10.glDrawElements(mode, count, type, indices);
    }


    public void glEnable(int cap) {
        trace("glEnable",cap);
        if (pass && realGL10 != null)
            realGL10.glEnable(cap);
    }


    public void glEnableClientState(int array) {
        trace("glEnableClientState", array);
        if (pass && realGL10 != null)
            realGL10.glEnableClientState(array);
    }


    public void glFinish() {
        trace("glFinish");
        if (pass && realGL10 != null)
            realGL10.glFinish();
    }


    public void glFlush() {
        trace("glFlush");
        if (pass && realGL10 != null)
            realGL10.glFlush();
    }


    public void glFogf(int pname, float param) {
        trace("glFogf", pname, param);
        if (pass && realGL10 != null)
            realGL10.glFogf(pname, param);
    }


    public void glFogfv(int pname, float[] params, int offset) {
        trace("glFogfv", pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glFogfv(pname, params, offset);
    }


    public void glFogfv(int pname, java.nio.FloatBuffer params) {
        trace("glFogfv", pname, params);
        if (pass && realGL10 != null)
            realGL10.glFogfv(pname, params);
    }


    public void glFogx(int pname, int param) {
        trace("glFogx", pname, param);
        if (pass && realGL10 != null)
            realGL10.glFogx(pname, param);
    }


    public void glFogxv(int pname, int[] params, int offset) {
        trace("glFogxv", pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glFogxv(pname, params, offset);
    }


    public void glFogxv(int pname, java.nio.IntBuffer params) {
        trace("glFogxv", pname, params);
        if (pass && realGL10 != null)
            realGL10.glFogxv(pname, params);
    }


    public void glFrontFace(int mode) {
        trace("glFrontFace", mode);
        if (pass && realGL10 != null)
            realGL10.glFrontFace(mode);
    }


    public void glFrustumf(float left, float right, float bottom, float top, float zNear,
            float zFar) {
        trace("glFrustumf", left, right, bottom, top, zNear, zFar);
        if (pass && realGL10 != null)
            realGL10.glFrustumf(left, right, bottom, top, zNear, zFar);
    }


    public void glFrustumx(int left, int right, int bottom, int top, int zNear, int zFar) {
        trace("glFrustumx", left, right, bottom, top, zNear, zFar);
        if (pass && realGL10 != null)
            realGL10.glFrustumx(left, right, bottom, top, zNear, zFar);
    }


    public void glGenTextures(int n, int[] textures, int offset) {
        trace("glGenTextures", n, textures, offset);
        if (pass && realGL10 != null)
            realGL10.glGenTextures(n, textures, offset);
    }


    public void glGenTextures(int n, java.nio.IntBuffer textures) {
        trace("glGenTextures", n, textures);
        if (pass && realGL10 != null)
            realGL10.glGenTextures(n, textures);
    }


    public int glGetError() {
        trace("glGetError");
        if (pass && realGL10 != null)
            return realGL10.glGetError();
        else
            return 0;
    }


    public void glGetIntegerv(int pname, int[] params, int offset) {
        trace("glGetIntegerv", pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glGetIntegerv(pname, params, offset);
    }


    public void glGetIntegerv(int pname, java.nio.IntBuffer params) {
        trace("glGetIntegerv", pname, params);
        if (pass && realGL10 != null)
            realGL10.glGetIntegerv(pname, params);
    }


    public String glGetString(int name) {
        trace("glGetString", name);
        if (pass && realGL10 != null)
            return realGL10.glGetString(name);
        else
            return "";
    }


    public void glHint(int target, int mode) {
        trace("glHint", target, mode);
        if (pass && realGL10 != null)
            realGL10.glHint(target, mode);
    }


    public void glLightModelf(int pname, float param) {
        trace("glLightModelf", pname, param);
        if (pass && realGL10 != null)
            realGL10.glLightModelf(pname, param);
    }


    public void glLightModelfv(int pname, float[] params, int offset) {
        trace("glLightModelfv", pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glLightModelfv(pname, params, offset);
    }


    public void glLightModelfv(int pname, java.nio.FloatBuffer params) {
        trace("glLightModelfv", pname, params);
        if (pass && realGL10 != null)
            realGL10.glLightModelfv(pname, params);
    }


    public void glLightModelx(int pname, int param) {
        trace("glLightModelx", pname, param);
        if (pass && realGL10 != null)
            realGL10.glLightModelx(pname, param);
    }


    public void glLightModelxv(int pname, int[] params, int offset) {
        trace("glLightModelxv", pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glLightModelxv(pname, params, offset);
    }


    public void glLightModelxv(int pname, java.nio.IntBuffer params) {
        trace("glLightModelxv", pname, params);
        if (pass && realGL10 != null)
            realGL10.glLightModelxv(pname, params);
    }


    public void glLightf(int light, int pname, float param) {
        trace("glLightf", light, pname, param);
        if (pass && realGL10 != null)
            realGL10.glLightf(light, pname, param);
    }


    public void glLightfv(int light, int pname, float[] params, int offset) {
        trace("glLightfv", light, pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glLightfv(light, pname, params, offset);
    }


    public void glLightfv(int light, int pname, java.nio.FloatBuffer params) {
        trace("glLightfv", light, pname, params);
        if (pass && realGL10 != null)
            realGL10.glLightfv(light, pname, params);
    }


    public void glLightx(int light, int pname, int param) {
        trace("glLightx", light, pname, param);
        if (pass && realGL10 != null)
            realGL10.glLightx(light, pname, param);
    }


    public void glLightxv(int light, int pname, int[] params, int offset) {
        trace("glLightxv", light, pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glLightxv(light, pname, params, offset);
    }


    public void glLightxv(int light, int pname, java.nio.IntBuffer params) {
        trace("glLightxv", light, pname, params);
        if (pass && realGL10 != null)
            realGL10.glLightxv(light, pname, params);
    }


    public void glLineWidth(float width) {
        trace("glLineWidth", width);
        if (pass && realGL10 != null)
            realGL10.glLineWidth(width);
    }


    public void glLineWidthx(int width) {
        trace("glLineWidthx", width);
        if (pass && realGL10 != null)
            realGL10.glLineWidthx(width);
    }


    public void glLoadIdentity() {
        trace("glLoadIdentity");
        if (pass && realGL10 != null)
            realGL10.glLoadIdentity();
    }


    public void glLoadMatrixf(float[] m, int offset) {
        trace("glLoadMatrixf", m, offset);
        if (pass && realGL10 != null)
            realGL10.glLoadMatrixf(m, offset);
    }


    public void glLoadMatrixf(java.nio.FloatBuffer m) {
        trace("glLoadMatrixf", m);
        if (pass && realGL10 != null)
            realGL10.glLoadMatrixf(m);
    }


    public void glLoadMatrixx(int[] m, int offset) {
        trace("glLoadMatrixx", m, offset);
        if (pass && realGL10 != null)
            realGL10.glLoadMatrixx(m, offset);
    }


    public void glLoadMatrixx(java.nio.IntBuffer m) {
        trace("glLoadMatrixx", m);
        if (pass && realGL10 != null)
            realGL10.glLoadMatrixx(m);
    }


    public void glLogicOp(int opcode) {
        trace("glLogicOp", opcode);
        if (pass && realGL10 != null)
            realGL10.glLogicOp(opcode);
    }


    public void glMaterialf(int face, int pname, float param) {
        trace("glMaterialf", face, pname, param);
        if (pass && realGL10 != null)
            realGL10.glMaterialf(face, pname, param);
    }


    public void glMaterialfv(int face, int pname, float[] params, int offset) {
        trace("glMaterialfv", face, pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glMaterialfv(face, pname, params, offset);
    }


    public void glMaterialfv(int face, int pname, java.nio.FloatBuffer params) {
        trace("glMaterialfv", face, pname, params);
        if (pass && realGL10 != null)
            realGL10.glMaterialfv(face, pname, params);
    }


    public void glMaterialx(int face, int pname, int param) {
        trace("glMaterialx", face, pname, param);
        if (pass && realGL10 != null)
            realGL10.glMaterialx(face, pname, param);
    }


    public void glMaterialxv(int face, int pname, int[] params, int offset) {
        trace("glMaterialxv", face, pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glMaterialxv(face, pname, params, offset);
    }


    public void glMaterialxv(int face, int pname, java.nio.IntBuffer params) {
        trace("glMaterialxv", face, pname, params);
        if (pass && realGL10 != null)
            realGL10.glMaterialxv(face, pname, params);
    }


    public void glMatrixMode(int mode) {
        trace("glMatrixMode", mode);
        if (pass && realGL10 != null)
            realGL10.glMatrixMode(mode);
    }


    public void glMultMatrixf(float[] m, int offset) {
        trace("glMultMatrixf", m, offset);
        if (pass && realGL10 != null)
            realGL10.glMultMatrixf(m, offset);
    }


    public void glMultMatrixf(java.nio.FloatBuffer m) {
        trace("glMultMatrixf", m);
        if (pass && realGL10 != null)
            realGL10.glMultMatrixf(m);
    }


    public void glMultMatrixx(int[] m, int offset) {
        trace("glMultMatrixx", m, offset);
        if (pass && realGL10 != null)
            realGL10.glMultMatrixx(m, offset);
    }


    public void glMultMatrixx(java.nio.IntBuffer m) {
        trace("glMultMatrixx", m);
        if (pass && realGL10 != null)
            realGL10.glMultMatrixx(m);
    }


    public void glMultiTexCoord4f(int target, float s, float t, float r, float q) {
        trace("glMultiTexCoord4f", target, s, t, r, q);
        if (pass && realGL10 != null)
            realGL10.glMultiTexCoord4f(target, s, t, r, q);
    }


    public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
        trace("glMultiTexCoord4x", target, s, t, r, q);
        if (pass && realGL10 != null)
            realGL10.glMultiTexCoord4x(target, s, t, r, q);
    }


    public void glNormal3f(float nx, float ny, float nz) {
        trace("glNormal3f", nx, ny, nz);
        if (pass && realGL10 != null)
            realGL10.glNormal3f(nx, ny, nz);
    }


    public void glNormal3x(int nx, int ny, int nz) {
        trace("glNormal3x", nx, ny, nz);
        if (pass && realGL10 != null)
            realGL10.glNormal3x(nx, ny, nz);
    }


    public void glNormalPointer(int type, int stride, java.nio.Buffer pointer) {
        trace("glNormalPointer", type, stride, pointer);
        if (pass && realGL10 != null)
            realGL10.glNormalPointer(type, stride, pointer);
    }


    public void glOrthof(float left, float right, float bottom, float top, float zNear,
            float zFar) {
        trace("glOrthof", left, right, bottom, top, zNear, zFar);
        if (pass && realGL10 != null)
            realGL10.glOrthof(left, right, bottom, top, zNear, zFar);
    }


    public void glOrthox(int left, int right, int bottom, int top, int zNear, int zFar) {
        trace("glOrthox", left, right, bottom, top, zNear, zFar);
        if (pass && realGL10 != null)
            realGL10.glOrthox(left, right, bottom, top, zNear, zFar);
    }


    public void glPixelStorei(int pname, int param) {
        trace("glPixelStorei", pname, param);
        if (pass && realGL10 != null)
            realGL10.glPixelStorei(pname, param);
    }


    public void glPointSize(float size) {
        trace("glPointSize", size);
        if (pass && realGL10 != null)
            realGL10.glPointSize(size);
    }


    public void glPointSizex(int size) {
        trace("glPointSizex", size);
        if (pass && realGL10 != null)
            realGL10.glPointSizex(size);
    }


    public void glPolygonOffset(float factor, float units) {
        trace("glPolygonOffset", factor, units);
        if (pass && realGL10 != null)
            realGL10.glPolygonOffset(factor, units);
    }


    public void glPolygonOffsetx(int factor, int units) {
        trace("glPolygonOffsetx", factor, units);
        if (pass && realGL10 != null)
            realGL10.glPolygonOffsetx(factor, units);
    }


    public void glPopMatrix() {
        trace("glPopMatrix");
        if (pass && realGL10 != null)
            realGL10.glPopMatrix();
    }


    public void glPushMatrix() {
        trace("glPushMatrix");
        if (pass && realGL10 != null)
            realGL10.glPushMatrix();
    }


    public void glReadPixels(int x, int y, int width, int height, int format, int type,
            java.nio.Buffer pixels) {
        trace("glReadPixels", x, y, width, height, format, type, pixels);
        if (pass && realGL10 != null)
            realGL10.glReadPixels(x, y, width, height, format, type, pixels);
    }


    public void glRotatef(float angle, float x, float y, float z) {
        trace("glRotatef", angle, x, y, z);
        if (pass && realGL10 != null)
            realGL10.glRotatef(angle, x, y, z);
    }


    public void glRotatex(int angle, int x, int y, int z) {
        trace("glRotatex", angle, x, y, z);
        if (pass && realGL10 != null)
            realGL10.glRotatex(angle, x, y, z);
    }


    public void glSampleCoverage(float value, boolean invert) {
        trace("glSampleCoverage", value, invert);
        if (pass && realGL10 != null)
            realGL10.glSampleCoverage(value, invert);
    }


    public void glSampleCoveragex(int value, boolean invert) {
        trace("glSampleCoveragex", value, invert);
        if (pass && realGL10 != null)
            realGL10.glSampleCoveragex(value, invert);
    }


    public void glScalef(float x, float y, float z) {
        trace("glScalef", x, y, z);
        if (pass && realGL10 != null)
            realGL10.glScalef(x, y, z);
    }


    public void glScalex(int x, int y, int z) {
        trace("glScalex", x, y, z);
        if (pass && realGL10 != null)
            realGL10.glScalex(x, y, z);
    }


    public void glScissor(int x, int y, int width, int height) {
        trace("glScissor", x, y, width, height);
        if (pass && realGL10 != null)
            realGL10.glScissor(x, y, width, height);
    }


    public void glShadeModel(int mode) {
        trace("glShadeModel", mode);
        if (pass && realGL10 != null)
            realGL10.glShadeModel(mode);
    }


    public void glStencilFunc(int func, int ref, int mask) {
        trace("glStencilFunc", func, ref, mask);
        if (pass && realGL10 != null)
            realGL10.glStencilFunc(func, ref, mask);
    }


    public void glStencilMask(int mask) {
        trace("glStencilMask", mask);
        if (pass && realGL10 != null)
            realGL10.glStencilMask(mask);
    }


    public void glStencilOp(int fail, int zfail, int zpass) {
        trace("glStencilOp", fail, zfail, zpass);
        if (pass && realGL10 != null)
            realGL10.glStencilOp(fail, zfail, zpass);
    }


    public void glTexCoordPointer(int size, int type, int stride, java.nio.Buffer pointer) {
        trace("glTexCoordPointer", size, type, stride, pointer);
        if (pass && realGL10 != null)
            realGL10.glTexCoordPointer(size, type, stride, pointer);
    }


    public void glTexEnvf(int target, int pname, float param) {
        trace("glTexEnvf", target, pname, param);
        if (pass && realGL10 != null)
            realGL10.glTexEnvf(target, pname, param);
    }


    public void glTexEnvfv(int target, int pname, float[] params, int offset) {
        trace("glTexEnvfv", target, pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glTexEnvfv(target, pname, params, offset);
    }


    public void glTexEnvfv(int target, int pname, java.nio.FloatBuffer params) {
        trace("glTexEnvfv", target, pname, params);
        if (pass && realGL10 != null)
            realGL10.glTexEnvfv(target, pname, params);
    }


    public void glTexEnvx(int target, int pname, int param) {
        trace("glTexEnvx", target, pname, param);
        if (pass && realGL10 != null)
            realGL10.glTexEnvx(target, pname, param);
    }


    public void glTexEnvxv(int target, int pname, int[] params, int offset) {
        trace("glTexEnvxv", target, pname, params, offset);
        if (pass && realGL10 != null)
            realGL10.glTexEnvxv(target, pname, params, offset);
    }


    public void glTexEnvxv(int target, int pname, java.nio.IntBuffer params) {
        trace("glTexEnvxv", target, pname, params);
        if (pass && realGL10 != null)
            realGL10.glTexEnvxv(target, pname, params);
    }


    public void glTexImage2D(int target, int level, int internalformat, int width,
            int height, int border, int format, int type, java.nio.Buffer pixels) {
        trace("glTexImage2D", target, level, internalformat, width, height, border,
              format, type, pixels);
        if (pass && realGL10 != null)
            realGL10.glTexImage2D(target, level, internalformat, width, height, border,
                                format, type, pixels);
    }


    public void glTexParameterf(int target, int pname, float param) {
        trace("glTexParameterf", target, pname, param);
        if (pass && realGL10 != null)
            realGL10.glTexParameterf(target, pname, param);
    }


    public void glTexParameterx(int target, int pname, int param) {
        trace("glTexParameterx", target, pname, param);
        if (pass && realGL10 != null)
            realGL10.glTexParameterx(target, pname, param);
    }


    public void glTexSubImage2D(int target, int level, int xoffset, int yoffset,
            int width, int height, int format, int type, java.nio.Buffer pixels) {
        trace("glTexSubImage2D", target, level, xoffset, yoffset, width, height, format,
              type, pixels);
        if (pass && realGL10 != null)
            realGL10.glTexSubImage2D(target, level, xoffset, yoffset, width, height,
                                   format, type, pixels);
    }


    public void glTranslatef(float x, float y, float z) {
        trace("glTranslatef", x, y, z);
        if (pass && realGL10 != null)
            realGL10.glTranslatef(x, y, z);
    }


    public void glTranslatex(int x, int y, int z) {
        trace("glTranslatex", x, y, z);
        if (pass && realGL10 != null)
            realGL10.glTranslatex(x, y, z);
    }


    public void glVertexPointer(int size, int type, int stride, java.nio.Buffer pointer) {
        trace("glVertexPointer", size, type, stride, pointer);
        if (pass && realGL10 != null)
            realGL10.glVertexPointer(size, type, stride, pointer);
    }


    public void glViewport(int x, int y, int width, int height) {
        trace("glViewport", x, y, width, height);
        if (pass && realGL10 != null)
            realGL10.glViewport(x, y, width, height);
    }
    
}