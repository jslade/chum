package chum.examples;

import chum.engine.GameActivity;
import chum.engine.GameNode;
import chum.engine.GameNode.Visitor;
import chum.engine.common.FPSNode;
import chum.gl.RenderNode;
import chum.input.GestureInputNode;
import chum.util.Log;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import java.util.ArrayList;


/**
   Test which creates a GameTree with a large number of nodes, primarily for the
   purpose of testing the raw overhead per node.
   
   Starts out with just a couple nodes, then adds more each time the screen is double-tapped.
*/
public class ManyManyNodes extends GameActivity
{
    /** The root of the rendering tree */
    private RenderNode rendering;
    
    /** Keep track of the original title string, so it can be updated (appended) */
    private CharSequence origTitle;

    private Runnable showFPS;
    
    
    @Override
    public void setViewOptions() {
    	this.hideTitlebar = false;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        origTitle = getTitle();
        showFPS = new Runnable(){
            public void run() {
                setTitle(""+origTitle + " -- "+gameController.getFPS()+"fps");
            }
        };
        
        Toast.makeText(this,"Double tap to double node count",2000).show();
    }


    @Override
    protected GameNode createLogicTree() {
        GameNode logic = new GameNode();
        logic.addNode(new FPSNode(){
            @Override
            public void showFPS() {
                super.showFPS();
                showFPSInTitle();
            }
        });
                    
        logic.addNode(new GestureInputNode(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                addNodes();
                return true;
            }
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.d("FPS = "+gameController.getFPS());
                return true;
            }
        });
        
        return logic;
    }
    
    @Override
    protected RenderNode createRenderTree(GameNode logic) {
        rendering = new RenderNode();
        for( int i=0; i<1; ++i ) {
            GameNode inode = new RenderNode();
            rendering.addNode(inode);
            for( int j=0; j<1; ++j ) {
                GameNode jnode = new RenderNode();
                inode.addNode(jnode);
                for( int k=0; k<1; ++k ) {
                    GameNode knode = new RenderNode();
                    jnode.addNode(knode);
                }
            }
        }
        return rendering;
    }

    
    protected void addNodes() {
        // First collect all existing nodes
        final ArrayList<GameNode> existing = new ArrayList<GameNode>();
        Visitor v = new Visitor() {
            public void run(GameNode node) {
                existing.add(node);
            }
        };
        rendering.visit(v,false);
        

        // Now add a new child to each one -- doubles the number of nodes
        for( GameNode node: existing) {
            node.addNode(new GameNode());
        }
        
        int count = (existing.size() * 2);
        Log.i("New node count: "+count);
        Toast.makeText(this,"New node count: "+count,1000).show();
    }
    
    
    protected void showFPSInTitle() {
        gameController.uiHandler.post(showFPS);
    }


}

