package chum.gl.render;

import chum.gl.GLColor;
import chum.gl.RenderNode;
import chum.util.Log;

import javax.microedition.khronos.opengles.GL10;


/**
   RenderNode that clears the scene -- usually the first node in the render tree.
*/
public class ClearNode extends RenderNode {

    /** The color to clear to (if any) */
    public GLColor color;

    /** The clear bits */
    public int clearBits = (GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);


    public ClearNode() {
        super();
    }


    public ClearNode(GLColor color) {
        super();
        this.color = color;
    }


    public void renderPrefix(GL10 gl10) {
        if ( color != null )
            gl10.glClearColorx(color.red,color.green,color.blue,color.alpha);
        gl10.glClear(clearBits);
    }
       
}
