package chum.gl;


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
    public void onSurfaceCreated(RenderContext renderContext) {
        super.onSurfaceCreated(renderContext);

        // Simply for speed (one less member access for each update),
        // keep the RenderContext in its own member
        this.renderContext = renderContext;
    }


    /**
       Called when the game surface is resized.
    */
    public void onSurfaceChanged(int width, int height) {
        super.onSurfaceChanged(width,height);
    }


    /**
       RenderNodes only render if visible
    */
    public boolean update(long millis) {
        if ( visible ) {
            return super.update(millis);
        } else {
            dispatchEvents();
            return false;
        }
    }


    /**
       RenderNodes just render but do not do any updates
    */
    public boolean updatePrefix(long millis) {
        if ( visible )
            renderPrefix(renderContext.gl10);
        return false;
    }


    /**
       RenderNodes just render but do not do any updates
    */
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
       Handle a GameEvent -- default behavior is to stop all propogation
    */
    public boolean onGameEvent(GameEvent event) {
        return true;
    }
}
