package chum.gl.render;

import chum.gl.Color;
import chum.gl.RenderContext;
import chum.gl.RenderNode;


/**
   RenderPrimitive that clears the scene -- usually the first node in the render tree.
*/
public class ClearNode extends RenderNode {

    /** The color */
    public Color color;
    
    /** Only uses a single RenderPrimitive, assumes it doesn't change between frames */
    public chum.gl.render.primitive.Clear clearNode;
    

    public ClearNode() {
        this(Color.BLACK);
    }


    public ClearNode(Color color) {
        super();
        clearNode = new chum.gl.render.primitive.Clear();
        if ( color != null ) {
            this.color = clearNode.color = new Color(color);
        }   
    }


    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        renderContext.add(clearNode);
        return true;
    }
       
}
