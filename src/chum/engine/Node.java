package chum.engine;



/**
   Node represents a node in the 'game graph' -- which may or may not
   represent a visible entity.  A node is visited every iteration of
   the engine (every frame), and given the opportunity to affect the
   game state.  The actual behavior of the node is defined by
   one or more Behavior objects.

*/
public abstract class Node {

    /** The parent node in the game graph */
    public Node parent = null;

    /** The set of child nodes */
    public Node[] nodes = null;

    /** The number of nodes */
    public int num_nodes;
    


    protected Node() {
    }


    /**
       Add a node to this node's list of child nodes.

       @param n The node to add.  If it already has a parent, it is first
       removed from that parent node.
     */
    public Node addNode(Node n) {
        if ( n.parent != null )
            n.parent.removeNode(n);

        if ( nodes == null ) nodes = new Node[2];
        if ( num_nodes == nodes.length ) {
            Node[] new_nodes = new Node[nodes.length*2];
            for( int i=0; i<num_nodes; ++i )
                new_nodes[i] = nodes[i];
            nodes = new_nodes;
        }

        nodes[num_nodes++] = n;
        n.parent = this;
        return n;
    }


    /**
       Remove a node from this node's list of child nodes.

       @param n The node to remove
    */
    public void removeNode(Node n) {
        if ( n.parent != this ) return;
        for ( int i=num_nodes-1; i>=0; --i ) {
            if ( nodes[i].equals(n) ) {
                for ( int j=i+1; j<num_nodes; ++i, ++j )
                    nodes[i] = nodes[j];
                num_nodes--;
                break;
            }
        }
        n.parent = null;
    }


    /**
       Remove this node from it's parent (if any)
    */
    public Node remove() {
        if ( parent != null )
            parent.removeNode(this);
        return this;
    }



    /**
       Execute some task in the context of every node, recursively.

       @param run The task to execute
       @param depthFirst If true, will visit the children first
    */
    public void visit(Visitor visitor, boolean depthFirst) {
        if ( depthFirst ) {
            for( int i=0; i<num_nodes; ++i )
                nodes[i].visit(visitor,true);
            visitor.run(this);
        } else {
            visitor.run(this);
            for( int i=0; i<num_nodes; ++i )
                nodes[i].visit(visitor,false);
        }
    }



    


    /**
       A generic bit of code to execute on a node and each of its children,
       recursively.
    */
    public static interface Visitor {
        public abstract void run(Node node);
    }


}
