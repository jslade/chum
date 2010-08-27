package chum.engine.common;

import chum.engine.GameController;
import chum.engine.GameNode;

/**
   Special node the provides hooks before/after calling some other node.
   This is actually just a pseudo-node in the tree primarily intended for debugging
   aides, such as with TraceNode.
*/
public class HookNode extends GameNode {

    protected GameNode realNode;
    
    public HookNode(GameNode node) {
        super();
        this.realNode = node;
        attach();
    }
    
    
    public void attach() {
        // Artificially insert myself into the tree in place
        // of the real node
        if ( parent != null ) return;
        this.parent = realNode.parent;
        if ( parent != null ) {
            for(int i=0; i<parent.num_children; ++i) {
                if ( parent.children[i] == realNode ) {
                    parent.children[i] = this;
                    break;
                }
            }
        }
        //chum.util.Log.d("%s attached to %s", this, realNode);
    }

    
    public void detach() {
        // Remove myself from the tree, restoring the original node
        if ( parent == null ) return;
        for(int i=0; i<parent.num_children; ++i) {
            if (parent.children[i] == this) {
                parent.children[i] = realNode;
                break;
            }
        }
        //chum.util.Log.d("%s detached from %s", this, realNode);
    }


    @Override
    public GameNode findNode(String name, GameNode skipNode) {
        return realNode.findNode(name,skipNode); 
    }

    
    @Override
    public void onPause() {
        realNode.onPause();
    }
 
    
    @Override
    public void onResume() {
        realNode.onResume();
    }
    

    @Override
    public void onSetup(GameController gameController) {
        realNode.onSetup(gameController);
    }
    
    
    @Override
    public void onSurfaceChanged(int width, int height) {
        realNode.onSurfaceChanged(width,height);
    }
    
    
    @Override
    public boolean update(long millis) {
        return realNode.update(millis);
    }

    
    @Override
    public void visit(GameNode.Visitor visitor, boolean depthFirst) {
        super.visit(visitor,depthFirst);
        realNode.visit(visitor,depthFirst);
    }
    
}
