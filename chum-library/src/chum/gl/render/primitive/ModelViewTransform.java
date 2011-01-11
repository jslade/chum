package chum.gl.render.primitive;

import chum.f.Vec3;
import chum.gl.RenderContext;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;


public class ModelViewTransform extends RenderPrimitive {

    public Vec3 eyePos = new Vec3();
    
    public Vec3 refPos = new Vec3();
    
    public Vec3 up = new Vec3(Vec3.Y_AXIS);
    
    public boolean identity = true;
    

    public void set(Vec3 eye, Vec3 ref, Vec3 up,boolean identity) {
        eyePos.x = eye.x;
        eyePos.y = eye.y;
        eyePos.z = eye.z;
        
        refPos.x = ref.x;
        refPos.y = ref.y;
        refPos.z = ref.z;
        
        this.up.x = up.x;
        this.up.y = up.y;
        this.up.z = up.z;
        
        this.identity = identity;
    }
    
    
    /**
        Set the camera.
     */
    @Override
    public void render(RenderContext renderContext, GL10 gl10) {
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
     
        if ( identity ) gl10.glLoadIdentity();

        // TODO: can this be done w/out GLU.gluLookAt() -- avoiding
        // the conversion to float?
        GLU.gluLookAt( gl10,
                       eyePos.x, eyePos.y, eyePos.z,
                       refPos.x, refPos.y, refPos.z,
                       up.x, up.y, up.z);
    }
    
}
