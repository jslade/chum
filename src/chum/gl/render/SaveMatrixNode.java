package chum.gl.render;

import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node that just pushes the current matrix going in, restores it coming out.

   This is intended to be used as the parent node for adding child nodes
   that do multiple rendering operations with a modified matrix.
*/
public class SaveMatrixNode extends RenderNode {

    public SaveMatrixNode() {
        super();
    }


    public void renderPrefix(GL10 gl) {
        gl.glPushMatrix();
    }

    public void renderPostfix(GL10 gl) {
        gl.glPopMatrix();
    }
}