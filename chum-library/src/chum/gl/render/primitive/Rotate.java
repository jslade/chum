package chum.gl.render.primitive;

import chum.f.Vec3;
import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


public class Rotate extends chum.gl.render.primitive.RenderPrimitive {
    
    public float degrees;
    
    public Vec3 rotation = new Vec3();
    
    @Override
    public void render(RenderContext context, GL10 gl) {
        gl.glRotatef(degrees,
                     rotation.x,
                     rotation.y,
                     rotation.z);
    }

}
