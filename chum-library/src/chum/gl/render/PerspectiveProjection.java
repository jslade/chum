package chum.gl.render;


import chum.gl.render.primitive.ProjectionTransform;


/**
   Sets up a perspective (3D) projection
*/
public class PerspectiveProjection extends ProjectionNode {

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
    }


    public PerspectiveProjection(boolean isDynamic) {
        super(isDynamic);
    }


    public void setPerspective(float fov, float aspect, float near, float far) {
        this.fov = fov;
        this.aspect = aspect;
        this.nearPlane = near;
        this.farPlane = far;
        this.isDirty = true;
    }

        
    /**
       Make this projection active.
    */
    @Override
    protected void setProjection(ProjectionTransform projection) {
        if ( aspect == 0f ) aspect = 1.0f * width/height;
        projection.setPerspective(fov,aspect,nearPlane,farPlane,true);
    }

}
