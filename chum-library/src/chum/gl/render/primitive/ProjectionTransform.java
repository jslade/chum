package chum.gl.render.primitive;

import chum.gl.RenderContext;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;


public class ProjectionTransform extends RenderPrimitive {

    /** The aspect ratio */
    public float aspect;
    
    /** The fov setting for perspective */
    public float fov;
    
    /** The near clipping plane for perspective */
    public float nearPlane;
    
    /** The far clipping plane for perspective */
    public float farPlane;
    
    /** The left edge for orthographic */
    public float left;

    /** The right edge for orthographic */
    public float right;

    /** The top edge for orthographic */
    public float top;

    /** The bottom edge for orthographic */
    public float bottom;

    
    public boolean projection = false;
    
    public boolean identity = true;
    

    public void setPerspective(float fov, float aspect, float nearPlane, float farPlane, boolean identity) {
        this.projection = true;
        this.fov = fov;
        this.aspect = aspect;
        this.nearPlane = nearPlane;
        this.farPlane = farPlane;
        this.identity = identity;
    }

    
    public void setOrthographic(float left, float right, float bottom, float top, boolean identity) {
        this.projection = false;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
        this.identity = identity;
    }
    
    
    /**
        Set the projection.
     */
    @Override
    public void render(RenderContext renderContext, GL10 gl10) {
        gl10.glMatrixMode(GL10.GL_PROJECTION);
     
        if ( identity ) gl10.glLoadIdentity();

        if ( projection ) {
            GLU.gluPerspective(gl10,fov,aspect,nearPlane,farPlane);
        } else {
            GLU.gluOrtho2D(gl10,left,right,bottom,top);
        }
    }
    
}
