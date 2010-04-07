package chum.gl.render;

import chum.gl.RenderNode;
import chum.util.Log;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;


/**
   Sets up an orthographic (2D) projection
*/
public class OrthographicProjection extends RenderNode {

    /** Whether the viewport is static (one time setup) or updates every frame */
    public boolean isDynamic = false;

    /** The left edge of the projection */
    protected float left;
    
    /** The right edge of the projection */
    protected float right;

    /** The top edge of the projection */
    protected float top;

    /** The bottom edge of the projection */
    protected float bottom;

       
       
    public OrthographicProjection() {
        super();
        Log.d("created OrthographicProjection()");
        this.isDynamic = false;
    }


    public OrthographicProjection(boolean isDynamic) {
        super();
        Log.d("created OrthographicProjection(%s)",isDynamic);
        this.isDynamic = isDynamic;
    }


    /** Called when the surface changes size */
    @Override
    public void onSurfaceChanged(int width, int height) {
        // Default setup is for the origin to be lower left of the screen
        left = 0f;
        right = (float)width;
        bottom = 0f;
        top = (float)height;

        if ( !isDynamic ) {
            set(renderContext.gl10);
        }
    }


    public void renderPrefix(GL10 gl10) {
        if ( isDynamic ) {
            set(gl10);
        }
    }
 

    protected void set(GL10 gl10) {
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        GLU.gluOrtho2D(gl10,left,right,bottom,top);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
    }
}
