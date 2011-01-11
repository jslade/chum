package chum.gl.render.primitive;

import chum.f.Vec3;
import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


public class Translate extends chum.gl.render.primitive.RenderPrimitive {
    
    public Vec3 position = new Vec3();
    
    @Override
    public void render(RenderContext context, GL10 gl) {
        gl.glTranslatef(position.x,
                        position.y,
                        position.z);
    }

}
