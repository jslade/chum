package chum.gl;

import chum.engine.GameNode;


/**
   This is intended as the base class for GameNodes which are visible in the scene, or
   otherwise contribute to the rendering of the scene.
   
   RenderNodes define the render() method to 
 */

public class RenderNode extends GameNode {

    /** Whether the node is visible */
    public boolean visible = true;
    
    
    @Override
    public void render(RenderContext renderContext) {
        if ( !visible ) return;
        
        if ( !renderPrefix(renderContext) ) return;
    
        for(int i=0; i<num_children; ++i) {
            GameNode child = children[i];
            child.render(renderContext);
        }
    
        renderPostfix(renderContext);
    }


    /**
       Do rendering before any of the child nodes get rendered.  This rendering
       just adds RenderNodes to the current rendering chain, to be processed
       later in the rendering thread.
       @return false if this node, and it's children, should be skipped
     */
    public boolean renderPrefix(RenderContext renderContext) {
        return false;
    }


    /**
       Do rendering after the child nodes.  This rendering just adds
       RenderNodes to the current rendering chain, to be processed
       later in the rendering thread.
     */
    public void renderPostfix(RenderContext renderContext) {

    }

}
