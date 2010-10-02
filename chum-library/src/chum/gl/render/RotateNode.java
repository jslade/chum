package chum.gl.render;

import chum.engine.GameController;
import chum.engine.common.Rotatable;
import chum.f.Vec3;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the active color
*/
public class RotateNode extends RenderNode
    implements Rotatable
{

    /** The axis of the rotation */
    public Vec3 rotation;

    /** The relative location of the rotation (optional) */
    public Vec3 position;
    
    /** The angle, expressed in degrees */
    public float degrees;

    /** Whether to save/restore the matrix */
    public boolean push;


    public RotateNode(float deg, Vec3 x) {
        super();
        rotation = new Vec3(Vec3.Z_AXIS);
        degrees = deg;
    }


    public RotateNode() {
        super();
        rotation = new Vec3();
        degrees = 0;
    }


    @Override
    public void onSetup(GameController gc) {
    	super.onSetup(gc);
    	if ( num_children > 0 ) push = true;
    }
    
    
    @Override
    public void renderPrefix(GL10 gl) {
        if ( push ) gl.glPushMatrix();

        if ( degrees == 0 ) return;

        if ( position != null ) {
            gl.glTranslatef(position.x,
                            position.y,
                            position.z);
        }
        
        gl.glRotatef(degrees,
                     rotation.x,
                     rotation.y,
                     rotation.z);

        if ( position != null ) {
            gl.glTranslatef(-position.x,
                            -position.y,
                            -position.z);
        }
    }

    
    @Override
    public void renderPostfix(GL10 gl) {
        if ( push ) gl.glPopMatrix();
        
        if ( degrees == 0 ) return;
    }


    @Override
    public float getAngle() {
        return degrees;
    }


    @Override
    public Vec3 getAxis() {
        return rotation;
    }


    @Override
    public void setAngle(float angle) {
        degrees = angle;
    }


    @Override
    public void setAxis(Vec3 axis) {
        rotation.set(axis);        
    }
}
