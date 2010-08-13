package chum.gl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


/**
   Test wrapper for basic OpenGL ES 1.0
*/
public class MockGL10 extends MockGL implements GL10 {

    public MockGL10() {
        super();
    }
    

    public void glActiveTexture(int texture) {
        add("glActiveTexture("+texture+")");
    }

    public void glAlphaFunc(int func, float ref) {
        add("glAlphaFunc("+func+","+ref+")");
    }

    public void glAlphaFuncx(int func,int ref) {
        add("glAlphaFuncx");
    }

    public void glBindTexture(int target,int texture) {
        add("glBindTexture");
    }

    public void glBlendFunc(int sfactor,int dfactor) {
        add("glBlendFunc");
    }

    public void glClear(int mask) {
        add("glClear");
    }

    public void glClearColor(float red,float green,float blue,float alpha) {
        add("glClearColor");
    }

    public void glClearColorx(int red,int green,int blue,int alpha) {
        add("glClearColorx");
    }

    public void glClearDepthf(float depth) {
        add("glClearDepthf");
    }

    public void glClearDepthx(int depth) {
        add("glClearDepthx");
    }

    public void glClearStencil(int s) {
        add("glClearStencil");
    }

    public void glClientActiveTexture(int texture) {
        add("glClientActiveTexture");
    }

    public void glColor4f(float red,float green,float blue,float alpha) {
        add("glColor4f");
    }

    public void glColor4x(int red,int green,int blue,int alpha) {
        add("glColor4x");
    }

    public void glColorMask(boolean red,boolean green,boolean blue,boolean alpha) {
        add("glColorMask");
    }

    public void glColorPointer(int size,int type,int stride,java.nio.Buffer pointer) {
        add("glColorPointer");
    }

    public void glCompressedTexImage2D(int target,int level,int internalformat,
                                       int width,int height,int border,int imageSize,
                                       java.nio.Buffer data) {
        add("glCompressedTexImage2D");
    }

    public void glCompressedTexSubImage2D(int target,int level,int xoffset,int yoffset,
                                          int width,int height,int format,int imageSize,
                                          java.nio.Buffer data) {
        add("glCompressedTexSubImage2D");
    }

    public void glCopyTexImage2D(int target,int level,int internalformat,int x,int y,
                                 int width,int height,int border) {
        add("glCopyTexImage2D");
    }

    public void glCopyTexSubImage2D(int target,int level,int xoffset,int yoffset,int x,int y,
                                    int width,int height) {
        add("glCopyTexSubImage2D");
    }

    public void glCullFace(int mode) {
        add("glCullFace");
    }

    public void glDeleteTextures(int n,int[] textures,int offset) {
        add("glDeleteTextures");
    }

    public void glDeleteTextures(int n,java.nio.IntBuffer textures) {
        add("glDeleteTextures");
    }

    public void glDepthFunc(int func) {
        add("glDepthFunc");
    }

    public void glDepthMask(boolean flag) {
        add("glDepthMask");
    }

    public void glDepthRangef(float zNear,float zFar) {
        add("glDepthRangef");
    }

    public void glDepthRangex(int zNear,int zFar) {
        add("glDepthRangex");
    }

    public void glDisable(int cap) {
        add("glDisable");
    }

    public void glDisableClientState(int array) {
        add("glDisableClientState");
    }

    public void glDrawArrays(int mode,int first,int count) {
        add("glDrawArrays");
    }

    public void glDrawElements(int mode,int count,int type,java.nio.Buffer indices) {
        add("glDrawElements");
    }

    public void glEnable(int cap) {
        add("glEnable");
    }

    public void glEnableClientState(int array) {
        add("glEnableClientState");
    }

    public void glFinish() {
        add("glFinish");
    }

    public void glFlush() {
        add("glFlush");
    }

    public void glFogf(int pname,float param) {
        add("glFogf");
    }

    public void glFogfv(int pname,float[] params,int offset) {
        add("glFogfv");
    }

    public void glFogfv(int pname,java.nio.FloatBuffer params) {
        add("glFogfv");
    }

    public void glFogx(int pname,int param) {
        add("glFogx");
    }

    public void glFogxv(int pname,int[] params,int offset) {
        add("glFogxv");
    }

    public void glFogxv(int pname,java.nio.IntBuffer params) {
        add("glFogxv");
    }

    public void glFrontFace(int mode) {
        add("glFrontFace");
    }

    public void glFrustumf(float left,float right,float bottom,float top,
                           float zNear,float zFar) {
        add("glFrustumf");
    }

    public void glFrustumx(int left,int right,int bottom,int top,
                           int zNear,int zFar) {
        add("glFrustumx");
    }

    public void glGenTextures(int n,int[] textures,int offset) {
        add("glGenTextures");
    }

    public void glGenTextures(int n,java.nio.IntBuffer textures) {
        add("glGenTextures");
    }

    public int glGetError() {
        add("glGetError");
        return 0;
    }

    public void glGetIntegerv(int pname,int[] params,int offset) {
        add("glGetIntegerv");
    }

    public void glGetIntegerv(int pname,java.nio.IntBuffer params) {
        add("glGetIntegerv");
    }

    public String glGetString(int name) {
        add("getGetString("+name+")");
        return "";
    }

    public void glHint(int target,int mode) {
        add("glHint");
    }

    public void glLightModelf(int pname,float param) {
        add("glLightModelf");
    }

    public void glLightModelfv(int pname,float[] params,int offset) {
        add("glLightModelfv");
    }

    public void glLightModelfv(int pname,java.nio.FloatBuffer params) {
        add("glLightModelfv");
    }

    public void glLightModelx(int pname,int param) {
        add("glLightModelx");
    }

    public void glLightModelxv(int pname,int[] params,int offset) {
        add("glLightModelxv");
    }

    public void glLightModelxv(int pname,java.nio.IntBuffer params) {
        add("glLightModelxv");
    }

    public void glLightf(int light,int pname,float param) {
        add("glLightf");
    }

    public void glLightfv(int light,int pname,float[] params,int offset) {
        add("glLightfv");
    }

    public void glLightfv(int light,int pname,java.nio.FloatBuffer params) {
        add("glLightfv");
    }

    public void glLightx(int light,int pname,int param) {
        add("glLightx");
    }

    public void glLightxv(int light,int pname,int[] params,int offset) {
        add("glLightxv");
    }

    public void glLightxv(int light,int pname,java.nio.IntBuffer params) {
        add("glLightxv");
    }

    public void glLineWidth(float width) {
        add("glLineWidth");
    }

    public void glLineWidthx(int width) {
        add("glLineWidthx");
    }

    public void glLoadIdentity() {
        add("glLoadIdentity");
    }                    

    public void glLoadMatrixf(float[] m,int offset) {
        add("glLoadMatrixf");
    }

    public void glLoadMatrixf(java.nio.FloatBuffer m) {
        add("glLoadMatrixf");
    }

    public void glLoadMatrixx(int[] m,int offset) {
        add("glLoadMatrixx");
    }

    public void glLoadMatrixx(java.nio.IntBuffer m) {
        add("glLoadMatrixx");
    }

    public void glLogicOp(int opcode) {
        add("glLogicOp");
    }

    public void glMaterialf(int face,int pname,float param) {
        add("glMaterialf");
    }

    public void glMaterialfv(int face,int pname,float[] params,int offset) {
        add("glMaterialfv");
    }

    public void glMaterialfv(int face,int pname,java.nio.FloatBuffer params) {
        add("glMaterialfv");
    }

    public void glMaterialx(int face,int pname,int param) {
        add("glMaterialx");
    }

    public void glMaterialxv(int face,int pname,int[] params,int offset) {
        add("glMaterialxv");
    }

    public void glMaterialxv(int face,int pname,java.nio.IntBuffer params) {
        add("glMaterialxv");
    }

    public void glMatrixMode(int mode) {
        add("glMatrixMode");
    }

    public void glMultMatrixf(float[] m,int offset) {
        add("glMultMatrixf");
    }

    public void glMultMatrixf(java.nio.FloatBuffer m) {
        add("glMultMatrixf");
    }

    public void glMultMatrixx(int[] m,int offset) {
        add("glMultMatrixx");
    }

    public void glMultMatrixx(java.nio.IntBuffer m) {
        add("glMultMatrixx");
    }

    public void glMultiTexCoord4f(int target,float s,float t,float r,float q) {
        add("glMultiTexCoord4f");
    }

    public void glMultiTexCoord4x(int target,int s,int t,int r,int q) {
        add("glMultiTexCoord4x");
    }

    public void glNormal3f(float nx,float ny,float nz) {
        add("glNormal3f");
    }

    public void glNormal3x(int nx,int ny,int nz) {
        add("glNormal3x");
    }

    public void glNormalPointer(int type,int stride,java.nio.Buffer pointer) {
        add("glNormalPointer");
    }

    public void glOrthof(float left,float right,float bottom,float top,
                         float zNear,float zFar) {
        add("glOrthof");
    }

    public void glOrthox(int left,int right,int bottom,int top,
                         int zNear,int zFar) {
        add("glOrthox");
    }

    public void glPixelStorei(int pname,int param) {
        add("glPixelStorei");
    }

    public void glPointSize(float size) {
        add("glPointSize");
    }

    public void glPointSizex(int size) {
        add("glPointSizex");
    }

    public void glPolygonOffset(float factor,float units) {
        add("glPolygonOffset");
    }

    public void glPolygonOffsetx(int factor,int units) {
        add("glPolygonOffsetx");
    }

    public void glPopMatrix() {
        add("glPopMatrix");
    }

    public void glPushMatrix() {
        add("glPushMatrix");
    }

    public void glReadPixels(int x,int y,int width,int height,int format,int type,
                             java.nio.Buffer pixels) {
        add("glReadPixels");
    }

    public void glRotatef(float angle,float x,float y,float z) {
        add("glRotatef");
    }

    public void glRotatex(int angle,int x,int y,int z) {
        add("glRotatex");
    }

    public void glSampleCoverage(float value,boolean invert) {
        add("glSampleCoverage");
    }

    public void glSampleCoveragex(int value,boolean invert) {
        add("glSampleCoveragex");
    }

    public void glScalef(float x,float y,float z) {
        add("glScalef");
    }

    public void glScalex(int x,int y,int z) {
        add("glScalex");
    }

    public void glScissor(int x,int y,int width,int height) {
        add("glScissor");
    }

    public void glShadeModel(int mode) {
        add("glShadeModel");
    }

    public void glStencilFunc(int func,int ref,int mask) {
        add("glStencilFunc");
    }

    public void glStencilMask(int mask) {
        add("glStencilMask");
    }

    public void glStencilOp(int fail,int zfail,int zpass) {
        add("glStencilOp");
    }

    public void glTexCoordPointer(int size,int type,int stride,java.nio.Buffer pointer) {
        add("glTexCoordPointer");
    }

    public void glTexEnvf(int target,int pname,float param) {
        add("glTexEnvf");
    }

    public void glTexEnvfv(int target,int pname,float[] params,int offset) {
        add("glTexEnvfv");
    }

    public void glTexEnvfv(int target,int pname,java.nio.FloatBuffer params) {
        add("glTexEnvfv");
    }

    public void glTexEnvx(int target,int pname,int param) {
        add("glTexEnvx");
    }

    public void glTexEnvxv(int target,int pname,int[] params,int offset) {
        add("glTexEnvxv");
    }

    public void glTexEnvxv(int target,int pname,java.nio.IntBuffer params) {
        add("glTexEnvxv");
    }

    public void glTexImage2D(int target,int level,int internalformat,int width,int height,
                             int border,int format,int type,java.nio.Buffer pixels) {
        add("glTexImage2D");
    }

    public void glTexParameterf(int target,int pname,float param) {
        add("glTexParameterf");
    }

    public void glTexParameterx(int target,int pname,int param) {
        add("glTexParameterx");
    }

    public void glTexSubImage2D(int target,int level,int xoffset,int yoffset,int width,int height,
                                int format,int type,java.nio.Buffer pixels) {
        add("glTexSubImage2D");
    }

    public void glTranslatef(float x,float y,float z) {
        add("glTranslatef");
    }

    public void glTranslatex(int x,int y,int z) {
        add("glTranslatex");
    }

    public void glVertexPointer(int size,int type,int stride,java.nio.Buffer pointer) {
        add("glVertexPointer");
    }

    public void glViewport(int x,int y,int width,int height) {
        add("glViewport("+x+","+y+","+width+","+height+")");
    }

}