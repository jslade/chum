package chum.gl.render;


import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.render.primitive.ProjectionTransform;


/**
   Base class for setting the projection matrix
*/
public abstract class ProjectionNode extends RenderNode {

    /** Whether the projection is static (one time setup) or updates every frame */
    public boolean isDynamic = false;

    /** Whether the projection has been changed */
    public boolean isDirty = true;

    /** The width of the viewport */
    protected int width;

    /** The height of the viewport */
    protected int height;

    /** The aspect ratio */
    protected float aspect = 0f;
    
    /** The rendering primitive for a phase */
    protected ProjectionTransform projectionA = new ProjectionTransform();
    
    /** The rendering primitive for b phase */
    protected ProjectionTransform projectionB = new ProjectionTransform();
    

    public ProjectionNode() {
        super();
        this.isDynamic = false;
        this.isDirty = true;
    }


    public ProjectionNode(boolean isDynamic) {
        super();
        this.isDynamic = isDynamic;
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
        this.aspect = 1.0f * width/height;
        
        isDirty = true;
    }


    /**
       If the projection is dynamic, then the projection is re-established
       on every frame.  Otherwise, only when it is dirty
    */
    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        if ( isDynamic || isDirty ) {
            isDirty = false;
            ProjectionTransform projection = renderContext.phase? projectionA : projectionB;
            setProjection(projection);
            renderContext.add(projection);
        }
        return true;
    }
 

    /**
       Make this projection active.

       Sets the matrix mode to GL_PROJECTION, then loads the orthographic
       projection into the current matrix, and finally sets the
       maxtrix mode back to GL_MODELVIEW.
    */
    protected abstract void setProjection(ProjectionTransform projection);
}
