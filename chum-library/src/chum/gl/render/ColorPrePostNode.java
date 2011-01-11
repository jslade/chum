package chum.gl.render;

import chum.gl.Color;
import chum.gl.RenderContext;
import chum.gl.render.primitive.SetColor;


/**
   A node to set the active color
*/
public class ColorPrePostNode extends ColorNode {

    /** The color to set on renderPostfix */
    public Color postColor;

    /** The render node for phase a */
    public SetColor postColorA;
    
    /** The render node for phase b */
    public SetColor postColorB;
    
    
    public ColorPrePostNode(Color pre,Color post) {
        super(pre);
        this.postColor = post;
    }


    @Override
    public void renderPostfix(RenderContext renderContext) {
        if ( postColor != null ) {
            SetColor set = renderContext.phase ? postColorA : postColorB;
            set.color.set(postColor);
            renderContext.add(set);
        }
    }

}
