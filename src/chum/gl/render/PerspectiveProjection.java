package chum.gl.render;

import chum.gl.RenderNode;
import chum.util.Log;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;


/**
   Sets up a perspective (3D) projection
*/
public class PerspectiveProjection extends RenderNode {

    /** Whether the projection is static (one time setup) or updates every frame */
    public boolean isDynamic = false;

    /** Whether the projection has been changed */
    public boolean isDirty = false;

    /** The width of the viewport */
    protected int width;

    /** The height of the viewport */
    protected int height;

    /** The fov setting */
    public float fov;

    /** The aspect ratio */
    public float aspect = 0f;
    
    /** The near clipping plane */
    public float nearPlane;

    /** The far clipping plane */
    public float farPlane;


    public PerspectiveProjection() {
        super();
        this.isDynamic = false;
        this.isDirty = true;
    }


    public PerspectiveProjection(boolean isDynamic) {
        super();
        this.isDynamic = isDynamic;
        this.isDirty = true;
    }


    public void setPerspective(float fov, float aspect, float near, float far) {
        this.fov = fov;
        this.aspect = aspect;
        this.nearPlane = near;
        this.farPlane = far;
        this.isDirty = true;
    }

        
    /**
       Called when the surface changes size.  If this is a static
       projection (meaning the camera does not ever change), then the
       projection is setup once at this time, instead of being done every
       frame.
    */
    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width,height);

        this.width = width;
        this.height = height;

        if ( !isDynamic || isDirty )
            setProjection(renderContext.gl10);
    }


    /**
       If the projection is dynamic, then the projection is re-established
       on every frame.
    */
    public void renderPrefix(GL10 gl10) {
        if ( isDynamic || isDirty )
            setProjection(gl10);
    }
 

    /**
       Make this projection active.

       Sets the matrix mode to GL_PROJECTION, then loads the orthographic
       projection into the current matrix, and finally sets the
       maxtrix mode back to GL_MODELVIEW.
    */
    protected void setProjection(GL10 gl10) {
        isDirty = false;
        if ( aspect == 0f ) {
            aspect = 1.0f * width/height;
        }

        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glLoadIdentity();

        GLU.gluPerspective(gl10,fov,aspect,nearPlane,farPlane);
    }

}
