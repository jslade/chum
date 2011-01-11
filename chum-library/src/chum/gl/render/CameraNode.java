package chum.gl.render;

import chum.f.Vec3;
import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.render.primitive.ModelViewTransform;


/**
   Transforms the current matrix (using the GL_MODELVIEW matrix) to look from a
   certain position (the eye position) towards a reference point, with a given
   'up' orientation
*/
public class CameraNode extends RenderNode {

    /** The eye position */
    public Vec3 eyePos = new Vec3(0f,0f,-10f);

    /** The reference position */
    public Vec3 refPos = new Vec3(Vec3.ORIGIN);

    /** The up vector */
    public Vec3 up = new Vec3(Vec3.Y_AXIS);

    /** Whether the camera is static (one time setup) or updates every frame */
    public boolean isDynamic = false;

    /** Whether the location has changed */
    public boolean isDirty;

    /** The render node for phase a rendering */
    protected ModelViewTransform transformA = new ModelViewTransform();
    
    /** The render node for phase b rendering */
    protected ModelViewTransform transformB = new ModelViewTransform();
    
    

    /**
       Create a CameraNode at the default position, default orientation.
    */
    public CameraNode() {
        super();
    }


    /**
       Create a CamerNode at the specified position, looking at the specified
       reference point, with the default orientation (y-axis is up)
    */
    public CameraNode(Vec3 vec3, Vec3 origin) {
        super();
        eyePos.set(vec3);
        refPos.set(origin);
    }


    /**
       Create a CamerNode at the specified position, looking at the specified
       reference point, with the given orientation
    */
    public CameraNode(Vec3 eye, Vec3 ref, Vec3 up) {
        super();
        eyePos.set(eye);
        refPos.set(ref);
        this.up.set(up);
    }


    public CameraNode(boolean isDynamic) {
        super();
        this.isDynamic = isDynamic;
    }


    /**
       Set the eye position
    */
    public void setEyePosition(Vec3 p) {
        eyePos.set(p);
        isDirty = true;
    }


    /**
       Set the referemce position
    */
    public void setRefPosition(Vec3 p) {
        refPos.set(p);
        isDirty = true;
    }


    /**
       Set the up orientation.

       @param up Should be a normalized vector
    */
    public void setUp(Vec3 up) {
        this.up.set(up);
        isDirty = true;
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
    }


    /**
       If the camaeria is dynamic or changed, then the view transformation
       is done again
    */
    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        if ( isDynamic || isDirty ) {
            isDirty = false;
            ModelViewTransform xform = renderContext.phase ? transformA : transformB;
            xform.set(eyePos,refPos,up,true);
            renderContext.add(xform);
        }   
        return true;
    }
 

}


