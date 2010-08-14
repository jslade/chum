package chum.gl.render;

import chum.engine.GameController;
import chum.f.Vec3;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the active color
*/
public class RotateNode extends RenderNode {

    /** The axis of the rotation */
    public Vec3 rotation;

    /** The angle, expressed in degrees */
    public float degrees;

    /** Whether to save/restore the matrix */
    public boolean push;


    public RotateNode(float deg, Vec3 x) {
        super();
        rotation = new Vec3(x);
        degrees = deg;
    }


    public RotateNode() {
        super();
        rotation = new Vec3();
        degrees = 0;
    }


    public void onSetup(GameController gc) {
    	super.onSetup(gc);
    	if ( num_children > 0 ) push = true;
    }
    
    
    public void renderPrefix(GL10 gl) {
        if ( push ) gl.glPushMatrix();
        
        if ( degrees != 0 )
            gl.glRotatef(degrees,
                         rotation.x,
                         rotation.y,
                         rotation.z);
    }

    public void renderPostfix(GL10 gl) {
        if ( push ) gl.glPopMatrix();
    }
}
