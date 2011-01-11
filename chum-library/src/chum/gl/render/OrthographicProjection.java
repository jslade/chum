package chum.gl.render;


import chum.gl.render.primitive.ProjectionTransform;


/**
   Sets up an orthographic (2D) projection
*/
public class OrthographicProjection extends ProjectionNode {

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
    }


    public OrthographicProjection(boolean isDynamic) {
        super(isDynamic);
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
        setExtents(width,height);
    }


    /**
       Called during setup ({Link onSurfaceChanged}) to set the
       extents of the coordinates in current view.
       
       This determines how the OpenGL vertex coordinates map to screen
       coordinates.

       The default behavior is to put the origin (0,0) at the lower left
       of the screen, and the right and top of the screen correspond to
       the width and height (number of pixels in each direction).
    */
    protected void setExtents(int width, int height) {
        left = 0f;
        right = width;
        bottom = 0f;
        top = height;
    }


    /**
       Make this projection active.
    */
    @Override
    protected void setProjection(ProjectionTransform projection) {
        projection.setOrthographic(left,right,bottom,top,true);
    }
}
