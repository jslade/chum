package chum.engine;

import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.util.Log;

import javax.microedition.khronos.opengles.GL10;


/**
   This special GameNode is intended to be the root node of the game graph.
 */
public abstract class GameTree extends GameNode {

    /** The GameActivity */
    public GameActivity gameActivity;

    /** The RenderContext */
    public RenderContext renderContext;

    /** The root GameNode for logic */
    public GameNode logic;

    /** The root RenderNode */
    public RenderNode rendering;



    /**
       Create a new GameTree
    */
    public GameTree(GameActivity activity) {
        super();

        this.gameActivity = activity;

        logic = createLogicTree();
        if ( logic != null ) 
            addNode(logic);

        rendering = createRenderTree();
        if ( rendering != null )
            addNode(rendering);
    }
    

    /**
       Create the game logic, represented by a GameNode.
    */
    protected abstract GameNode createLogicTree();


    /**
       Create the game rendering tree, represented by RenderNode
    */
    protected abstract RenderNode createRenderTree();



    /**
       Called when the game drawing surface is initially created
    */
    public void onSurfaceCreated(final RenderContext renderContext) {
        this.renderContext = renderContext;
        
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    Log.d("onSurfaceCreated() visit %s",node);
                    node.onSetup(renderContext);
                }
            };
        visit(visitor,false);
    }


    /**
       Called when the surface is resized
    */
    public void onSurfaceChanged(GameActivity activity,
                                 final int width, final int height) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    Log.d("onSurfaceChanged() visit %s",node);
                    node.onResized(width,height);
                }
            };
        visit(visitor,false);
    }


    /**
       Called when starting the rendering loop
    */
    public void onResume(GameActivity activity) {
        Visitor visitor = new Visitor() {
                public void run(GameNode node) {
                    Log.d("onResume() visit %s",node);
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
                    Log.d("onPause() visit %s",node);
                    node.onPause();
                }
            };
        visit(visitor,false);
    }


    public static class Dummy extends GameTree {
        public Dummy(GameActivity activity) { super(activity); }
        protected GameNode createLogicTree() { return null; }
        protected RenderNode createRenderTree() { return null; }
    }
}
