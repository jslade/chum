package chum.engine;

import chum.gl.RenderContext;


/**
   This special GameNode is intended to be the root node of the game graph.

   Each frame, the GameTree is traversed node by node.  In that process, GameNodes
   that are involved in the rendering get added to the chain for the rendering pass.
   
   In the rendering pass, RenderNodes get added to the actual rendering chain that
   will be used to render the scene.  The RenderPrimitive chain is built separate from
   the processing of the GameNodes so that the actual rendering can happen in
   a separate thread.
 */
public class GameTree extends GameNode {
    
    /**
       Create a new GameTree
    */
    public GameTree() {
        super();
    }
    

    /**
       Called when the game tree is initially created
    */
    public void doSetup(final GameController gameController) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    node.onSetup(gameController);
                }
            };
        visit(visitor,false);
    }


    /**
       Called when the game drawing surface is initially created
    */
    public void doSurfaceCreated(final RenderContext renderContext) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    //Log.d("onSurfaceCreated() visit %s",node);
                    node.onSurfaceCreated(renderContext);
                }
            };
        visit(visitor,false);
    }


    /**
       Called when the surface is resized
    */
    public void doSurfaceChanged(final int width, final int height) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    //Log.d("onSurfaceChanged() visit %s",node);
                    node.onSurfaceChanged(width,height);
                }
            };
        visit(visitor,false);
    }


    /**
       Called whenever the rendering surface is resized, including once
       at startup.
    */
    public void onResized(int width, int height) {
        setViewport(width,height);
    }


    /**
       Create the viewport for OpenGL rendering, using glViewport().

       Standard behavior is to use the entire size of the view surface as the
       viewport.
       
       TODO: move this to a ViewportNode class that does this in onSurfaceChanged(),
       and have it be a standard part of the hierarchy (base class for Standard[2|3]DNode) 
    */
    public void setViewport(int width, int height) {
        gameController.renderContext.gl10.glViewport(0,0,width,height);
    }
    
       
    /**
       Called when starting the rendering loop
    */
    public void onResume(GameActivity activity) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    //Log.d("onResume() visit %s",node);
                    node.onResume();
                }
            };
        visit(visitor,false);
    }


    /**
       Called when stopping the rendering loop
    */
    public void onPause(GameActivity activity) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    //Log.d("onPause() visit %s",node);
                    node.onPause();
                }
            };
        visit(visitor,false);
    }


    /**
       Any GameEvents dispatched up to the root of the GameTree get passed to
       the GameActivity.  If it's not handled there, it gets reflected
       back down.  That allows events to propagate up one branch of the tree and
       back down another.
    */
    @Override
    public boolean dispatchEventUp(GameEvent event) {
        if (gameController.activity.onGameEvent(event))
            return true;
        return dispatchEventDown(event,false);
    }
    

    /**
       In the render phase, all nodes in the tree will have render() called
     */
    @Override
    public void render(RenderContext renderContext) {
        for(int i=0; i<num_children; ++i) {
            GameNode child = children[i];
            child.render(renderContext);
        }
    }
}
