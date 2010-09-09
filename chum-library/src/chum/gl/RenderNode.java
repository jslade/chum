package chum.gl;


import chum.engine.GameController;
import chum.engine.GameEvent;
import chum.engine.GameNode;

import javax.microedition.khronos.opengles.GL10;


/**
   RenderNode represents a node in the game graph that does some part
   of the rendering.  RenderNodes can be nested and pipelined just like
   normal GameNodes
*/
public class RenderNode extends GameNode {

    /** The current RenderContext */
    public RenderContext renderContext;

    /** Whether the node should be rendered */
    public boolean visible = true;

       
    /**
       Called once when the rendering surface/context is created
    */
    @Override
    public void onSurfaceCreated(RenderContext renderContext) {
        super.onSurfaceCreated(renderContext);

        // Simply for speed (one less member access for each update),
        // keep the RenderContext in its own member
        this.renderContext = renderContext;
    }

    
    /**
       Called when the node gets added into a parent.  Make sure we have a
       RenderContext if not already...
     */
    @Override
	public void onSetup(GameController gc) {
    	super.onSetup(gc);
    	this.renderContext = gc.renderContext;
    }

    /**
       Called when the game surface is resized.
    */
    @Override
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width,height);
    }


    /**
       RenderNodes only render if visible
    */
    @Override
    public boolean update(long millis) {
        if ( !visible ) return false;
        return super.update(millis);
    }


    /**
       RenderNodes just render but do not do any updates
    */
    @Override
    public boolean updatePrefix(long millis) {
        if ( visible )
            renderPrefix(renderContext.gl10);
        return false;
    }


    /**
       RenderNodes just render but do not do any updates
    */
    @Override
    public boolean updatePostfix(long millis) {
        if ( visible )
            renderPostfix(renderContext.gl10);
        return false;
    }


    /**
       Render this node prior to rendering the children
    */
    public void renderPrefix(GL10 gl10) {
    }
       

    /**
       Render this node after rendering the children
    */
    public void renderPostfix(GL10 gl10) {
    }


    /**
       For events that originate in the rendering tree (postUp() on a RenderNode),
       those events don't propogate back down side branches of the rendering tree,
       they only go straight to the top of the tree, and from there possibly back
       down the logic tree.
    */
    @Override
    protected boolean dispatchEventSideways(GameEvent event) {
        return false;
    }
    
}
