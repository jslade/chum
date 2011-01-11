package chum.gl.render;


import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.render.primitive.Matrix;


/**
   A node that just pushes the current matrix going in, restores it coming out.

   This is intended to be used as the parent node for adding child nodes
   that do multiple rendering operations with a modified matrix.
*/
public class SaveMatrixNode extends RenderNode {

    protected Matrix.Push push = new Matrix.Push();
    protected Matrix.Pop pop = new Matrix.Pop();
    

    public SaveMatrixNode() {
        super();
    }


    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        renderContext.add(push);
        return true;
    }

    
    @Override
    public void renderPostfix(RenderContext renderContext) {
        renderContext.add(pop);
    }
}