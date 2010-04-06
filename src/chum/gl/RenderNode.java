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
    public RenderContext context;

    /** Whether the node should be rendered */
    public boolean visible = true;

       
    /**
       Called once when the rendering surface/context is created
    */
    public void onSetup(RenderContext context) {
        this.context = context;
    }


    /**
       RenderNodes just render but do not do any updates
    */
    public boolean updatePrefix(long millis) {
        if ( visible )
            renderPrefix(context.gl10);
        return false;
    }


    /**
       RenderNodes just render but do not do any updates
    */
    public boolean updatePostfix(long millis) {
        if ( visible )
            renderPostfix(context.gl10);
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
