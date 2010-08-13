package chum.gl;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


/**
   Test wrapper for basic OpenGL ES 1.1
*/
public class MockGL11 extends MockGL10 implements GL11 {

    /** dummy buffer tracker */
    public int dummyBuffer = 0;


    public MockGL11() {
        super();
    }
    

    public void glGetPointerv(int pname, java.nio.Buffer[] params) {
        add("glGetPointerv");
    }

    public void glBindBuffer(int target,int buffer) {
        add("glBindBuffer");
    }

    public void glBufferData(int target,int size,java.nio.Buffer data,int usage) {
        add("glBufferData");
    }

    public void glBufferSubData(int target,int offset,int size,java.nio.Buffer data) {
        add("glBufferSubData");
    }

    public void glClipPlanef(int plane,float[] equation,int offset) {
        add("glClipPlanef");
    }

    public void glClipPlanef(int plane,java.nio.FloatBuffer equation) {
        add("glClipPlanef");
    }

    public void glClipPlanex(int plane,int[] equation,int offset) {
        add("glClipPlanex");
    }

    public void glClipPlanex(int plane,java.nio.IntBuffer equation) {
        add("glClipPlanex");
    }

    public void glColor4ub(byte red,byte green,byte blue,byte alpha) {
        add("glColor4ub");
    }

    public void glColorPointer(int size,int type,int stride,int offset) {
        add("glColorPointer");
    }

    public void glDeleteBuffers(int n,int[] buffers,int offset) {
        add("glDeleteBuffers");
    }

    public void glDeleteBuffers(int n,java.nio.IntBuffer buffers) {
        add("glDeleteBuffers");
    }

    public void glDrawElements(int mode,int count,int type,int offset) {
        add("glDrawElements");
    }

    public void glGenBuffers(int n,int[] buffers,int offset) {
        add("glGenBuffers("+n+")");
        for(int i=0; i<n; ++i)
            buffers[offset+i] = ++dummyBuffer;
    }

    public void glGenBuffers(int n,java.nio.IntBuffer buffers) {
        add("glGenBuffers("+n+")");
        for(int i=0; i<n; ++i)
            buffers.put(++dummyBuffer);
    }

    public void glGetBooleanv(int pname,boolean[] params,int offset) {
        add("glGetBooleanv");
    }
     
    public void glGetBooleanv(int pname,java.nio.IntBuffer params) {
        add("glGetBooleanv");
    }

    public void glGetBufferParameteriv(int target,int pname,int[] params,int offset) {
        add("glGetBufferParameteriv");
    }

    public void glGetBufferParameteriv(int target,int pname,java.nio.IntBuffer params) {
        add("glGetBufferParameteriv");
    }

    public void glGetClipPlanef(int pname,float[] eqn,int offset) {
        add("glGetClipPlanef");
    }

    public void glGetClipPlanef(int pname,java.nio.FloatBuffer eqn) {
        add("glGetClipPlanef");
    }

    public void glGetClipPlanex(int pname,int[] eqn,int offset) {
        add("glGetClipPlanex");
    }

    public void glGetClipPlanex(int pname,java.nio.IntBuffer eqn) {
        add("glGetClipPlanex");
    }

    public void glGetFixedv(int pname,int[] params,int offset) {
        add("glGetFixedv");
    }

    public void glGetFixedv(int pname,java.nio.IntBuffer params) {
        add("glGetFixedv");
    }

    public void glGetFloatv(int pname,float[] params,int offset) {
        add("glGetFloatv");
    }

    public void glGetFloatv(int pname,java.nio.FloatBuffer params) {
        add("glGetFloatv");
    }

    public void glGetLightfv(int light,int pname,float[] params,int offset) {
        add("glGetLightfv");
    }

    public void glGetLightfv(int light,int pname,java.nio.FloatBuffer params) {
        add("glGetLightfv");
    }

    public void glGetLightxv(int light,int pname,int[] params,int offset) {
        add("glGetLightxv");
    }

    public void glGetLightxv(int light,int pname,java.nio.IntBuffer params) {
        add("glGetLightxv");
    }

    public void glGetMaterialfv(int face,int pname,float[] params,int offset) {
        add("glGetMaterialfv");
    }

    public void glGetMaterialfv(int face,int pname,java.nio.FloatBuffer params) {
        add("glGetMaterialfv");
    }

    public void glGetMaterialxv(int face,int pname,int[] params,int offset) {
        add("glGetMaterialxv");
    }

    public void glGetMaterialxv(int face,int pname,java.nio.IntBuffer params) {
        add("glGetMaterialxv");
    }

    public void glGetTexEnviv(int env, int pname,int[] params,int offset) {
        add("glGetTexEnviv");
    }

    public void glGetTexEnviv(int env,int pname,java.nio.IntBuffer params) {
        add("glGetTexEnviv");
    }

    public void glGetTexEnvxv(int env,int pname,int[] params,int offset) {
        add("glGetTexEnvxv");
    }

    public void glGetTexEnvxv(int env,int pname,java.nio.IntBuffer params) {
        add("glGetTexEnvxv");
    }

    public void glGetTexParameterfv(int target,int pname,float[] params,int offset) {
        add("glGetTexParameterfv");
    }

    public void glGetTexParameterfv(int target,int pname,java.nio.FloatBuffer params) {
        add("glGetTexParameterfv");
    }

    public void glGetTexParameteriv(int target,int pname,int[] params,int offset) {
        add("glGetTexParameteriv");
    }

    public void glGetTexParameteriv(int target,int pname,java.nio.IntBuffer params) {
        add("glGetTexParameteriv");
    }

    public void glGetTexParameterxv(int target,int pname,int[] params,int offset) {
        add("glGetTexParameterxv");
    }

    public void glGetTexParameterxv(int target,int pname,java.nio.IntBuffer params) {
        add("glGetTexParameterxv");
    }

    public boolean glIsBuffer(int buffer) {
        add("glIsBuffer("+buffer+")");
        return false;
    }

    public boolean glIsEnabled(int cap) {
        add("glIsEnabled("+cap+")");
        return false;
    }

    public boolean glIsTexture(int texture) {
        add("glIsTexture("+texture+")");
        return false;
    }

    public void glNormalPointer(int type,int stride,int offset) {
        add("glNormalPointer");
    }

    public void glPointParameterf(int pname,float param) {
        add("glPointParameterf");
    }

    public void glPointParameterfv(int pname,float[] params,int offset) {
        add("glPointParameterfv");
    }

    public void glPointParameterfv(int pname,java.nio.FloatBuffer params) {
        add("glPointParameterfv");
    }

    public void glPointParameterx(int pname,int param) {
        add("glPointParameterx");
    }

    public void glPointParameterxv(int pname,int[] params,int offset) {
        add("glPointParameterxv");
    }

    public void glPointParameterxv(int pname,java.nio.IntBuffer params) {
        add("glPointParameterxv");
    }

    public void glPointSizePointerOES(int type,int stride,java.nio.Buffer pointer) {
        add("glPointSizePointerOES");
    }

    public void glTexCoordPointer(int size,int type,int stride,int offset) {
        add("glTexCoordPointer");
    }

    public void glTexEnvi(int target,int pname,int param) {
        add("glTexEnvi");

    }

    public void glTexEnviv(int target,int pname,int[] params,int offset) {
        add("glTexEnviv");
    }

    public void glTexEnviv(int target,int pname,java.nio.IntBuffer params) {
        add("glTexEnviv");
    }

    public void glTexParameterfv(int target,int pname,float[] params,int offset) {
        add("glTexParameterfv");
    }

    public void glTexParameterfv(int target,int pname,java.nio.FloatBuffer params) {
        add("glTexParameterfv");
    }

    public void glTexParameteri(int target,int pname,int param) {
        add("glTexParameteri");
    }

    public void glTexParameteriv(int target,int pname,int[] params,int offset) {
        add("glTexParameteriv");
    }

    public void glTexParameteriv(int target,int pname,java.nio.IntBuffer params) {
        add("glTexParameteriv");
    }

    public void glTexParameterxv(int target,int pname,int[] params,int offset) {
        add("glTexParameterxv");
    }

    public void glTexParameterxv(int target,int pname,java.nio.IntBuffer params) {
        add("glTexParameterxv");
    }

    public void glVertexPointer(int size,int type,int stride,int offset) {
        add("glVertexPointer");
    }

}
