package chum.engine;

import chum.gl.RenderContext;
import chum.gl.RenderNode;


/**
   This special GameNode is intended to be the root node of the game graph.

   It devides the overall graph into two parts: logic, and rendering.
   The logic subtree is intended to control behavior, whereas the rendering
   subtree should do all the rendering.

   This almost maps to a MVC architecture -- the View is the rendering subtree.
   The Controller is intended to be the logic side, and the Model is likely
   split between the two for practical reasons.
 */
public class GameTree extends GameNode {

    /** The root GameNode for logic */
    public GameNode logic;

    /** The root RenderNode */
    public RenderNode rendering;



    /**
       Create a new GameTree
    */
    public GameTree() {
        super();
    }
    

    public void setLogicTree(GameNode newLogic) {
        if ( logic != null )
            removeNode(logic);
        
        logic = newLogic;
        
        if ( newLogic != null )
            addNode(newLogic);
    }
    
    
    public void setRenderTree(RenderNode newRendering) {
        if ( rendering != null )
            removeNode(rendering);
        
        rendering = newRendering;
        
        if ( newRendering != null )
            addNode(newRendering);
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
       Any GameEvents dispatched up to the root of the GameTree get reflected
       back down.  That allows events to propagate up one branch of the tree and
       back down another.
    */
    @Override
    public void dispatchEventUp(GameEvent event) {
        dispatchEventDown(event);
    }

    @Override
    public void dispatchEventDown(GameEvent event) {
        if ( !gameController.activity.onGameEvent(event) )
            super.dispatchEventDown(event);
    }

}
