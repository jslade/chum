package chum.gl.render.primitive;


import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


/**
   RenderPrimitive performs some part of rendering a scene, making
   calls using the GL10 instance obtained from the RenderContext.
   
   RenderNodes are managed by GameNodes, and assembled into a chain
   (linked list) during GameNode.render().  Once the full GameTree
   has been processed, the completed RenderPrimitive chain is given to the
   rendering thread to execute.  This decouples the update thread
   from the rendering thread, allowing the two to overlap as much
   as possible for maximum frame rate.
*/
public abstract class RenderPrimitive {

    /** The next node in the chain */
    public RenderPrimitive nextNode;
    
    public abstract void render(RenderContext context, GL10 gl);
}
