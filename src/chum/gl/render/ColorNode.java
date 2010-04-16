package chum.gl.render;

import chum.gl.Color;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the active color
*/
public class ColorNode extends RenderNode {

    /** The color to set */
    public Color color;


    public ColorNode(Color color) {
        super();
        this.color = color;
    }


    public void renderPrefix(GL10 gl) {
        gl.glColor4x(color.red,color.green,color.blue,color.alpha);
    }

}
