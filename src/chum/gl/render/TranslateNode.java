package chum.gl.render;

import chum.fp.Vec3;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the active color
*/
public class TranslateNode extends RenderNode {

    /** The position, which will be relative to the current matrix */
    public Vec3 position;

    /** Whether to save/restore the matrix */
    public boolean push;


    public TranslateNode(Vec3 v,boolean push) {
        super();
        position = new Vec3(v);
        this.push = push;
    }


    public TranslateNode(Vec3 v) {
        this(v,false);
    }


    public TranslateNode() {
        this(Vec3.ORIGIN,false);
    }


    public void renderPrefix(GL10 gl) {
        if ( push ) gl.glPushMatrix();
        gl.glTranslatex(position.x,
                        position.y,
                        position.z);
    }

    public void renderPostfix(GL10 gl) {
        if ( push ) gl.glPopMatrix();
    }
}