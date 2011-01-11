package chum.gl.render.primitive;

import chum.gl.Color;
import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


public class SetColor extends RenderPrimitive {

    public Color color = new Color();
    
    @Override
    public void render(RenderContext context, GL10 gl) {
        gl.glColor4f(color.red,color.green,color.blue,color.alpha);
    }

}
