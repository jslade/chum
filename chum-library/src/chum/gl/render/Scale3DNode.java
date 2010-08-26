package chum.gl.render;

import chum.engine.GameController;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the scale factor
*/
public class Scale3DNode extends RenderNode {

    /** The x-axis scale factor */
    public float x;

    /** The y-axis scale factor */
    public float y;

    /** The z-axis scale factor */
    public float z;

    /** Whether to save/restore the matrix */
    public boolean push;


    public Scale3DNode() {
        this(1f,1f,1f);
    }
    
    public Scale3DNode(float x,float y,float z) {
        super();
        this.x = x;
        this.y = y;
        this.z = z;
    }


    @Override
    public void onSetup(GameController gc) {
    	super.onSetup(gc);
    	if ( num_children > 0 ) push = true;
    }
    
    
    @Override
    public void renderPrefix(GL10 gl) {
        if ( push ) gl.glPushMatrix();
        gl.glScalef(x,y,z);
    }

    @Override
    public void renderPostfix(GL10 gl) {
        if ( push ) gl.glPopMatrix();
    }
}