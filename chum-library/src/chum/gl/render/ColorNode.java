package chum.gl.render;

import chum.gl.Color;
import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.render.primitive.SetColor;


/**
   A node to set the active color
*/
public class ColorNode extends RenderNode {

    /** The color to set on renderPrefix */
    public Color color;

    /** The render node for phase a */
    protected SetColor colorA = new SetColor();
    
    /** The render node for phase b */
    protected SetColor colorB = new SetColor();
    
    
    public ColorNode(Color color) {
        super();
        this.color = color;
    }


    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        if ( color != null ) {
            SetColor set = renderContext.phase ? colorA : colorB;
            set.color.set(color);
            renderContext.add(set);
        }
        return true;
    }

}
