package chum.gl.render;

import chum.fp.FP;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the scale factor
*/
public class ScaleNode extends RenderNode {

    /** The scale factor, expressed in FP */
    public int scale;

    /** Whether to save/restore the matrix */
    public boolean push;


    public ScaleNode(int scale) {
        super();
        this.scale = scale;
    }


    public ScaleNode() {
        super();
        scale = FP.ONE;
    }


    public void renderPrefix(GL10 gl) {
        if ( push ) gl.glPushMatrix();

        if ( scale != FP.ONE )
            gl.glScalex(scale,scale,scale);
    }

    public void renderPostfix(GL10 gl) {
        if ( push ) gl.glPopMatrix();
    }
}