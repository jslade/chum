package chum.gl.render;

import chum.gl.GLColor;
import chum.gl.RenderNode;
import chum.util.Log;

import javax.microedition.khronos.opengles.GL10;


/**
   RenderNode that clears the scene to the background color each frame
*/
public class BackgroundColor extends RenderNode {

    /** The color to clear to */
    public GLColor color;

       
    public BackgroundColor(GLColor color) {
        super();
        this.color = color;
        Log.d("created BeackgroundColor(%s)", color);
    }


    public void renderPrefix(GL10 gl10) {
        gl10.glClearColorx(color.red,color.green,color.blue,color.alpha);
    }
       
}
