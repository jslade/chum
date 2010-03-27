package chum.engine;

import chum.gl.RenderContext;


/**
   GameNode represents a node in the 'game graph' -- which may or may
   not represent a visible entity.  A node is visited every iteration
   of the engine (every frame), and given the opportunity to affect
   the game state.

   GameNodes typically define their behavior by adding one or more
   child nodes.  


   The members of this class are generally public for efficient access.
   This violates pure OO encapsulation practices, but it's a standard
   trade off for higher performance.
*/
public abstract class GameNode {

    /** The parent node in the game graph */
    public GameNode parent = null;

    /** The set of child nodes */
    public GameNode[] children = null;

    /** The number of child nodes */
    public int num_children;
    

    /** The name of the node, for development / debugging purposes */
    public String name;



    public GameNode() {
    }


    /**
       Add a node to this node's list of child nodes.

       @param n The node to add.  If it already has a parent, it is first
       removed from that parent node.
     */
    public GameNode addNode(GameNode n) {
        if ( n.parent != null )
            n.parent.removeNode(n);

        if ( children == null ) children = new GameNode[2];
        if ( num_children == children.length ) {
            GameNode[] new_children = new GameNode[children.length*2];
            for( int i=0; i<num_children; ++i )
                new_children[i] = children[i];
            children = new_children;
        }

        children[num_children++] = n;
        n.parent = this;
        return n;
    }


    /**
       Remove a node from this node's list of child nodes.

       @param n The node to remove
    */
    public void removeNode(GameNode n) {
        if ( n.parent != this ) return;
        for ( int i=num_children-1; i>=0; --i ) {
            if ( children[i].equals(n) ) {
                for ( int j=i+1; j<num_children; ++i, ++j )
                    children[i] = children[j];
                num_children--;
                break;
            }
        }
        n.parent = null;
    }


    /**
       Remove this node from it's parent (if any)
    */
    public GameNode remove() {
        if ( parent != null )
            parent.removeNode(this);
        return this;
    }


    /**
       Called whenever the game loop is starting
    */
    public void onResume() {
    }


    /**
       Called whenever the game loop is stopping
    */
    public void onPause() {
    }


    /**
       Called when the game graph is being initialized.  This happens
       once the OpenGL surface is created, immediately before rendering
       the first frame.
    */
    public void onSetup(RenderContext renderContext) {
    }


    /**
       Called when the game surface is resized.  This is called at least
       once, immediately before rendering the first frame.  It may be
       called additional times, if the app is setup to auto-rotate, etc
    */
    public void onResized(int width, int height) {
    }




    /**
       Update this node according to a specific amount of elapsed time
       
       @param millis Number of milliseconds since last update
       @return true if something changed with the node.
    */
    public boolean update(long millis) {
        boolean updated = false;
        if ( updatePrefix(millis) ) updated = true;
        for(int i=0; i<num_children; ++i) {
            if ( children[i].update(millis) ) updated = true;
        }
        if ( updatePostfix(millis) ) updated = true;
        return updated;
    }
    

    /**
       Update this node prior to updating the children
    */
    public boolean updatePrefix(long millis) {
        return false;
    }


    /**
       Update this node after updating the children
    */
    public boolean updatePostfix(long millis) {
        return false;
    }











    /**
       Execute some task in the context of every node, recursively.

       @param run The task to execute
       @param depthFirst If true, will visit the children first
    */
    public void visit(Visitor visitor, boolean depthFirst) {
        if ( depthFirst ) {
            for( int i=0; i<num_children; ++i )
                children[i].visit(visitor,true);
            visitor.run(this);
        } else {
            visitor.run(this);
            for( int i=0; i<num_children; ++i )
                children[i].visit(visitor,false);
        }
    }


    /**
       A generic bit of code to execute on a node and each of its children,
       recursively.
    */
    public static interface Visitor {
        public abstract void run(GameNode node);
    }


}
