package chum.gl.render;

import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.render.primitive.Matrix;


/**
   A node to set the active color
*/
public abstract class TransformNode extends RenderNode {

    /** Whether to save/restore the matrix */
    protected boolean push;

    /** The a phase render node for push */
    protected Matrix.Push ppush;
    
    /** The a phase render node for pop */
    protected Matrix.Pop ppop;
    

    public TransformNode(boolean push) {
        super();
        setPush(push);
    }


    public TransformNode() {
        this(true);
    }


    public void setPush(boolean p) {
        push = p;
        if ( push ) {
            if ( ppush == null ) {
                ppush = new Matrix.Push();
                ppop = new Matrix.Pop();
            }
        }
    }
    
    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        if ( push ) renderContext.add(ppush);
        renderTransform(renderContext);
        return true;
    }

    @Override
    public void renderPostfix(RenderContext renderContext) {
        if ( push ) renderContext.add(ppop);
    }
    
    public abstract void renderTransform(RenderContext renderContext);
}