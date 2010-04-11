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
        this.isDynamic = false;
    }


    public OrthographicProjection(boolean isDynamic) {
        super();
        this.isDynamic = isDynamic;
    }


    /**
       Called when the surface changes size.  If this is a static
       projection (meaning the camera does not ever change), then the
       project is setup once at this time, instead of being done every
       frame.
    */
    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width,height);
        setExtents(width,height);
        if ( !isDynamic )
            setProjection(renderContext.gl10);
    }


    /**
       If the projection is dynamic, then the projection is re-established
       on every frame.
    */
    public void renderPrefix(GL10 gl10) {
        if ( isDynamic )
            setProjection(gl10);
    }
 

    /**
       Called during setup ({Link onSurfaceChanged}) to set the
       extents of the coorinates in current view.
       
       This determines how the OpenGL vertex coordinates map to screen
       coordinates.

       The default behavior is to put the origin (0,0) at the lower left
       of the screen, and the right and top of the screen correspond to
       the width and height (number of pixels in each direction).
    */
    protected void setExtents(int width, int height) {
        left = 0f;
        right = (float)width;
        bottom = 0f;
        top = (float)height;
    }


    /**
       Make this projection active.

       Sets the matrix mode to GL_PROJECTION, then loads the orthographic
       projection into the current matrix, and finally sets the
       maxtrix mode back to GL_MODELVIEW.
    */
    protected void setProjection(GL10 gl10) {
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();
        GLU.gluOrtho2D(gl10,left,right,bottom,top);
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
    }
}
