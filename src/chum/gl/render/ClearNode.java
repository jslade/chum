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
        Log.d("created ClearNode()");
    }


    public ClearNode(GLColor color) {
        super();
        this.color = color;
        Log.d("created ClearNode(%s)", color);
    }


    /**
       Called whenever the rendering surface is resized, including once
       at startup.
    */
    public void onResized(int width, int height) {
        setViewport(width,height);
    }


    /**
       Create the viewport for OpenGL rendering, using glViewport().

       Standard behavior is to use the entire size of the view surface as the
       viewport.
    */
    public void setViewport(int width, int height) {
        context.gl10.glViewport(0,0,width,height);
    }
    
       
    public void renderPrefix(GL10 gl10) {
        if ( color != null )
            gl10.glClearColorx(color.red,color.green,color.blue,color.alpha);
        gl10.glClear(clearBits);
    }
       
}
