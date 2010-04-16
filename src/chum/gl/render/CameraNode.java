package chum.gl.render;

import chum.fp.FP;
import chum.fp.Vec3;
import chum.gl.RenderNode;
import chum.util.Log;

import android.opengl.GLU;

import javax.microedition.khronos.opengles.GL10;


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
    public CameraNode(Vec3 eye, Vec3 ref) {
        super();
        eyePos.set(eye);
        refPos.set(ref);
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

        if ( !isDynamic || isDirty )
            setTransform(renderContext.gl10);
    }


    /**
       If the camaeria is dynamic or changed, then the view transformation
       is done again
    */
    public void renderPrefix(GL10 gl10) {
        if ( isDynamic || isDirty )
            setTransform(gl10);
    }
 

    private float eye_x, eye_y, eye_z;
    private float ref_x, ref_y, ref_z;
    private float up_x, up_y, up_z;


    /**
       Set the camera.
    */
    protected void setTransform(GL10 gl10) {
        isDirty = false;

        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glLoadIdentity();

        eye_x = FP.toFloat(eyePos.x);
        eye_y = FP.toFloat(eyePos.y);
        eye_z = FP.toFloat(eyePos.z);
        ref_x = FP.toFloat(refPos.x);
        ref_y = FP.toFloat(refPos.y);
        ref_z = FP.toFloat(refPos.z);
        up_x = FP.toFloat(up.x);
        up_y = FP.toFloat(up.y);
        up_z = FP.toFloat(up.z);

        // todo: can this be done w/out GLU.gluLookAt() -- avoiding
        // the conversion to float?
        GLU.gluLookAt( gl10,
                       eye_x, eye_y, eye_z,
                       ref_x, ref_y, ref_z,
                       up_x, up_y, up_z);
    }

}


