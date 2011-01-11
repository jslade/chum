package chum.gl.render.primitive;

import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


/**
   Scales uniformly in all dimensions 
 */
public class Scale extends chum.gl.render.primitive.RenderPrimitive {
    
    public float scale;
    
    @Override
    public void render(RenderContext context, GL10 gl) {
        gl.glScalef(scale,scale,scale);
    }

}
