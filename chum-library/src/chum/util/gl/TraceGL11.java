package chum.util.gl;

import javax.microedition.khronos.opengles.GL11;


/**
   Wrapper for basic OpenGL ES 1.1 that logs every OpenGL call
*/
public class TraceGL11 extends TraceGL10 implements GL11 {

    /** The real GL instance the calls were intended for */
    public GL11 realGL11;

    /** dummy buffer tracker */
    protected int dummyBuffer = 0;


    public TraceGL11() {
        super();
    }
    

    public void glGetPointerv(int pname, java.nio.Buffer[] params) {
        trace("glGetPointerv",pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetPointerv(pname,params);
    }

    public void glBindBuffer(int target,int buffer) {
        trace("glBindBuffer",target,buffer);
        if ( pass && realGL11 != null ) realGL11.glBindBuffer(target,buffer);
    }

    public void glBufferData(int target,int size,java.nio.Buffer data,int usage) {
        trace("glBufferData",target,size,data,usage);
        if ( pass && realGL11 != null ) realGL11.glBufferData(target,size,data,usage);
    }

    public void glBufferSubData(int target,int offset,int size,java.nio.Buffer data) {
        trace("glBufferSubData",target,offset,size,data);
        if ( pass && realGL11 != null ) realGL11.glBufferSubData(target,offset,size,data);
    }

    public void glClipPlanef(int plane,float[] equation,int offset) {
        trace("glClipPlanef",plane,equation,offset);
        if ( pass && realGL11 != null ) realGL11.glClipPlanef(plane,equation,offset);
    }

    public void glClipPlanef(int plane,java.nio.FloatBuffer equation) {
        trace("glClipPlanef",plane,equation);
        if ( pass && realGL11 != null ) realGL11.glClipPlanef(plane,equation);
    }

    public void glClipPlanex(int plane,int[] equation,int offset) {
        trace("glClipPlanex",plane,equation,offset);
        if ( pass && realGL11 != null ) realGL11.glClipPlanex(plane,equation,offset);
    }

    public void glClipPlanex(int plane,java.nio.IntBuffer equation) {
        trace("glClipPlanex",plane,equation);
        if ( pass && realGL11 != null ) realGL11.glClipPlanex(plane,equation);
    }

    public void glColor4ub(byte red,byte green,byte blue,byte alpha) {
        trace("glColor4ub",red,green,blue,alpha);
        if ( pass && realGL11 != null ) realGL11.glColor4ub(red,green,blue,alpha);
    }

    public void glColorPointer(int size,int type,int stride,int offset) {
        trace("glColorPointer",size,type,stride,offset);
        if ( pass && realGL11 != null ) realGL11.glColorPointer(size,type,stride,offset);
    }

    public void glDeleteBuffers(int n,int[] buffers,int offset) {
        trace("glDeleteBuffers",n,buffers,offset);
        if ( pass && realGL11 != null ) realGL11.glDeleteBuffers(n,buffers,offset);
    }

    public void glDeleteBuffers(int n,java.nio.IntBuffer buffers) {
        trace("glDeleteBuffers",n,buffers);
        if ( pass && realGL11 != null ) realGL11.glDeleteBuffers(n,buffers);
    }

    public void glDrawElements(int mode,int count,int type,int offset) {
        trace("glDrawElements",mode,count,type,offset);
        if ( pass && realGL11 != null ) realGL11.glDrawElements(mode,count,type,offset);
    }

    public void glGenBuffers(int n,int[] buffers,int offset) {
        trace("glGenBuffers",n,buffers,offset);
        if ( pass && realGL11 != null ) realGL11.glGenBuffers(n,buffers,offset);
        else for(int i=0; i<n; ++i) buffers[offset+i] = ++dummyBuffer;
    }

    public void glGenBuffers(int n,java.nio.IntBuffer buffers) {
        trace("glGenBuffers",n,buffers);
        if ( pass && realGL11 != null ) realGL11.glGenBuffers(n,buffers);
        else for(int i=0; i<n; ++i) buffers.put(++dummyBuffer);
    }

    public void glGetBooleanv(int pname,boolean[] params,int offset) {
        trace("glGetBooleanv",pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetBooleanv(pname,params,offset);
    }
     
    public void glGetBooleanv(int pname,java.nio.IntBuffer params) {
        trace("glGetBooleanv",pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetBooleanv(pname,params);
    }

    public void glGetBufferParameteriv(int target,int pname,int[] params,int offset) {
        trace("glGetBufferParameteriv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetBufferParameteriv(target,pname,params,offset);
    }

    public void glGetBufferParameteriv(int target,int pname,java.nio.IntBuffer params) {
        trace("glGetBufferParameteriv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetBufferParameteriv(target,pname,params);
    }

    public void glGetClipPlanef(int pname,float[] eqn,int offset) {
        trace("glGetClipPlanef",pname,eqn,offset);
        if ( pass && realGL11 != null ) realGL11.glGetClipPlanef(pname,eqn,offset);
    }

    public void glGetClipPlanef(int pname,java.nio.FloatBuffer eqn) {
        trace("glGetClipPlanef",pname,eqn);
        if ( pass && realGL11 != null ) realGL11.glGetClipPlanef(pname,eqn);
    }

    public void glGetClipPlanex(int pname,int[] eqn,int offset) {
        trace("glGetClipPlanex",pname,eqn,offset);
        if ( pass && realGL11 != null ) realGL11.glGetClipPlanex(pname,eqn,offset);
    }

    public void glGetClipPlanex(int pname,java.nio.IntBuffer eqn) {
        trace("glGetClipPlanex",pname,eqn);
        if ( pass && realGL11 != null ) realGL11.glGetClipPlanex(pname,eqn);
    }

    public void glGetFixedv(int pname,int[] params,int offset) {
        trace("glGetFixedv",pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetFixedv(pname,params,offset);
    }

    public void glGetFixedv(int pname,java.nio.IntBuffer params) {
        trace("glGetFixedv",pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetFixedv(pname,params);
    }

    public void glGetFloatv(int pname,float[] params,int offset) {
        trace("glGetFloatv",pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetFloatv(pname,params,offset);
    }

    public void glGetFloatv(int pname,java.nio.FloatBuffer params) {
        trace("glGetFloatv",pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetFloatv(pname,params);
    }

    public void glGetLightfv(int light,int pname,float[] params,int offset) {
        trace("glGetLightfv",light,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetLightfv(light,pname,params,offset);
    }

    public void glGetLightfv(int light,int pname,java.nio.FloatBuffer params) {
        trace("glGetLightfv",light,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetLightfv(light,pname,params);
    }

    public void glGetLightxv(int light,int pname,int[] params,int offset) {
        trace("glGetLightxv",light,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetLightxv(light,pname,params,offset);
    }

    public void glGetLightxv(int light,int pname,java.nio.IntBuffer params) {
        trace("glGetLightxv",light,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetLightxv(light,pname,params);
    }

    public void glGetMaterialfv(int face,int pname,float[] params,int offset) {
        trace("glGetMaterialfv",face,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetMaterialfv(face,pname,params,offset);
    }

    public void glGetMaterialfv(int face,int pname,java.nio.FloatBuffer params) {
        trace("glGetMaterialfv",face,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetMaterialfv(face,pname,params);
    }

    public void glGetMaterialxv(int face,int pname,int[] params,int offset) {
        trace("glGetMaterialxv",face,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetMaterialxv(face,pname,params,offset);
    }

    public void glGetMaterialxv(int face,int pname,java.nio.IntBuffer params) {
        trace("glGetMaterialxv",face,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetMaterialxv(face,pname,params);
    }

    public void glGetTexEnviv(int env, int pname,int[] params,int offset) {
        trace("glGetTexEnviv",env,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetTexEnviv(env,pname,params,offset);
    }

    public void glGetTexEnviv(int env,int pname,java.nio.IntBuffer params) {
        trace("glGetTexEnviv",env,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetTexEnviv(env,pname,params);
    }

    public void glGetTexEnvxv(int env,int pname,int[] params,int offset) {
        trace("glGetTexEnvxv",env,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetTexEnvxv(env,pname,params,offset);
    }

    public void glGetTexEnvxv(int env,int pname,java.nio.IntBuffer params) {
        trace("glGetTexEnvxv",env,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetTexEnvxv(env,pname,params);
    }

    public void glGetTexParameterfv(int target,int pname,float[] params,int offset) {
        trace("glGetTexParameterfv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetTexParameterfv(target,pname,params,offset);
    }

    public void glGetTexParameterfv(int target,int pname,java.nio.FloatBuffer params) {
        trace("glGetTexParameterfv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetTexParameterfv(target,pname,params);
    }

    public void glGetTexParameteriv(int target,int pname,int[] params,int offset) {
        trace("glGetTexParameteriv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetTexParameteriv(target,pname,params,offset);
    }

    public void glGetTexParameteriv(int target,int pname,java.nio.IntBuffer params) {
        trace("glGetTexParameteriv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetTexParameteriv(target,pname,params);
    }

    public void glGetTexParameterxv(int target,int pname,int[] params,int offset) {
        trace("glGetTexParameterxv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glGetTexParameterxv(target,pname,params,offset);
    }

    public void glGetTexParameterxv(int target,int pname,java.nio.IntBuffer params) {
        trace("glGetTexParameterxv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glGetTexParameterxv(target,pname,params);
    }

    public boolean glIsBuffer(int buffer) {
        trace("glIsBuffer",buffer);
        if ( pass && realGL11 != null ) return realGL11.glIsBuffer(buffer);
        return false;
    }

    public boolean glIsEnabled(int cap) {
        trace("glIsEnabled",cap);
        if ( pass && realGL11 != null ) return realGL11.glIsEnabled(cap);
        return false;
    }

    public boolean glIsTexture(int texture) {
        trace("glIsTexture",texture);
        if ( pass && realGL11 != null ) realGL11.glIsTexture(texture);
        return false;
    }

    public void glNormalPointer(int type,int stride,int offset) {
        trace("glNormalPointer",type,stride,offset);
        if ( pass && realGL11 != null ) realGL11.glNormalPointer(type,stride,offset);
    }

    public void glPointParameterf(int pname,float param) {
        trace("glPointParameterf",pname,param);
        if ( pass && realGL11 != null ) realGL11.glPointParameterf(pname,param);
    }

    public void glPointParameterfv(int pname,float[] params,int offset) {
        trace("glPointParameterfv",pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glPointParameterfv(pname,params,offset);
    }

    public void glPointParameterfv(int pname,java.nio.FloatBuffer params) {
        trace("glPointParameterfv",pname,params);
        if ( pass && realGL11 != null ) realGL11.glPointParameterfv(pname,params);
    }

    public void glPointParameterx(int pname,int param) {
        trace("glPointParameterx",pname,param);
        if ( pass && realGL11 != null ) realGL11.glPointParameterx(pname,param);
    }

    public void glPointParameterxv(int pname,int[] params,int offset) {
        trace("glPointParameterxv",pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glPointParameterxv(pname,params,offset);
    }

    public void glPointParameterxv(int pname,java.nio.IntBuffer params) {
        trace("glPointParameterxv",pname,params);
        if ( pass && realGL11 != null ) realGL11.glPointParameterxv(pname,params);
    }

    public void glPointSizePointerOES(int type,int stride,java.nio.Buffer pointer) {
        trace("glPointSizePointerOES",type,stride,pointer);
        if ( pass && realGL11 != null ) realGL11.glPointSizePointerOES(type,stride,pointer);
    }

    public void glTexCoordPointer(int size,int type,int stride,int offset) {
        trace("glTexCoordPointer",size,type,stride,offset);
        if ( pass && realGL11 != null ) realGL11.glTexCoordPointer(size,type,stride,offset);
    }

    public void glTexEnvi(int target,int pname,int param) {
        trace("glTexEnvi",target,pname,param);
        if ( pass && realGL11 != null ) realGL11.glTexEnvi(target,pname,param);
    }

    public void glTexEnviv(int target,int pname,int[] params,int offset) {
        trace("glTexEnviv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glTexEnviv(target,pname,params,offset);
    }

    public void glTexEnviv(int target,int pname,java.nio.IntBuffer params) {
        trace("glTexEnviv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glTexEnviv(target,pname,params);
    }

    public void glTexParameterfv(int target,int pname,float[] params,int offset) {
        trace("glTexParameterfv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glTexParameterfv(target,pname,params,offset);
    }

    public void glTexParameterfv(int target,int pname,java.nio.FloatBuffer params) {
        trace("glTexParameterfv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glTexParameterfv(target,pname,params);
    }

    public void glTexParameteri(int target,int pname,int param) {
        trace("glTexParameteri",target,pname,param);
        if ( pass && realGL11 != null ) realGL11.glTexParameteri(target,pname,param);
    }

    public void glTexParameteriv(int target,int pname,int[] params,int offset) {
        trace("glTexParameteriv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glTexParameteriv(target,pname,params,offset);
    }

    public void glTexParameteriv(int target,int pname,java.nio.IntBuffer params) {
        trace("glTexParameteriv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glTexParameteriv(target,pname,params);
    }

    public void glTexParameterxv(int target,int pname,int[] params,int offset) {
        trace("glTexParameterxv",target,pname,params,offset);
        if ( pass && realGL11 != null ) realGL11.glTexParameterxv(target,pname,params,offset);
    }

    public void glTexParameterxv(int target,int pname,java.nio.IntBuffer params) {
        trace("glTexParameterxv",target,pname,params);
        if ( pass && realGL11 != null ) realGL11.glTexParameterxv(target,pname,params);
    }

    public void glVertexPointer(int size,int type,int stride,int offset) {
        trace("glVertexPointer",size,type,stride,offset);
        if ( pass && realGL11 != null ) realGL11.glVertexPointer(size,type,stride,offset);
    }

}
