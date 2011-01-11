package chum.gl.render.primitive;

import chum.gl.Color;
import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


public class Clear extends RenderPrimitive {

    /** The optional clear color */
    public Color color;
    
    /** The clear bits */
    public int clearBits = (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


    @Override
    public void render(RenderContext renderContext,GL10 gl10) {
        if ( color != null )
            gl10.glClearColor(color.red,color.green,color.blue,color.alpha);
        gl10.glClear(clearBits);
    }
}
