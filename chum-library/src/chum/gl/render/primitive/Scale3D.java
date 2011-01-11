package chum.gl.render.primitive;

import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


/**
   Scales independently in all dimensions
 */
public class Scale3D extends chum.gl.render.primitive.RenderPrimitive {
    
    public float scaleX = 1f;
    public float scaleY = 1f;
    public float scaleZ = 1f;
    
    @Override
    public void render(RenderContext context, GL10 gl) {
        gl.glScalef(scaleX,scaleY,scaleZ);
    }

}
