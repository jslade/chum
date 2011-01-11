package chum.gl.render;

import chum.gl.RenderContext;
import chum.gl.render.primitive.Scale;


/**
   A node to set the scale factor
*/
public class ScaleNode extends TransformNode {

    /** The scale factor */
    public float scale;

    /** The a phase render node for translation */
    public Scale scaleA = new Scale();

    /** The b phase render node for translation */
    public Scale scaleB = new Scale();


    public ScaleNode(float scale,boolean push) {
        super(push);
        this.scale = scale;
    }


    public ScaleNode(float scale) {
        this(scale,true);
    }
    
    
    public ScaleNode() {
        this(1f);
    }


    @Override
    public void renderTransform(RenderContext renderContext) {
        if ( scale != 1f) {
            Scale prim = renderContext.phase ? scaleA : scaleB;
            prim.scale = scale;
            renderContext.add(prim);
        }
    }

}