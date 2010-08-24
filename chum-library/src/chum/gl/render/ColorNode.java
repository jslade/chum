package chum.gl.render;

import chum.gl.Color;
import chum.gl.RenderNode;

import javax.microedition.khronos.opengles.GL10;


/**
   A node to set the active color
*/
public class ColorNode extends RenderNode {

    /** The color to set on renderPrefix */
    public Color color;


    /** The color to set on renderPostfix */
    public Color postColor;
    
    
    public ColorNode(Color color) {
        this(color,Color.WHITE);
    }
    
    
    public ColorNode(Color pre, Color post) {
        super();
        this.color = pre;
        this.postColor = post;
    }


    @Override
    public void renderPrefix(GL10 gl) {
        if ( color != null )
            gl.glColor4f(color.red,color.green,color.blue,color.alpha);
    }


    @Override
    public void renderPostfix(GL10 gl) {
        if ( postColor != null )
            gl.glColor4f(postColor.red,postColor.green,postColor.blue,postColor.alpha);
    }

}
