package chum.gl.render;

import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the scale factor
*/
public class ScaleNode extends RenderNode {

    /** The scale factor */
    public float scale;

    /** Whether to save/restore the matrix */
    public boolean push;


    public ScaleNode(float scale) {
        super();
        this.scale = scale;
    }


    public ScaleNode() {
        super();
        scale = 1f;
    }


    public void renderPrefix(GL10 gl) {
        if ( push ) gl.glPushMatrix();

        if ( scale != 1f)
            gl.glScalef(scale,scale,scale);
    }

    public void renderPostfix(GL10 gl) {
        if ( push ) gl.glPopMatrix();
    }
}