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
public class GameNode {

    /** The context for this node */
    public GameController gameController;

    /** The parent node in the game graph */
    public GameNode parent;

    /** The set of child nodes */
    public GameNode[] children;

    /** The number of child nodes */
    public int num_children;
    

    /** The name of the node, for finding nodes in the tree
        to link them together */
    public String name;



    /** Create a new node, not initially in the tree */
    public GameNode() {
    }


    /** 
        Create a new node, and add it as a child of the given
        parent node
    */
    public GameNode(GameNode parent) {
        parent.addNode(this);
    }


    /**
       Set the name of this node

       @return The node
    */
    public GameNode setName(String n) {
        name = n;
        return this;
    }


    /**
       Add a node to this node's list of child nodes.

       @param n The node to add.  If it already has a parent, it is first
       removed from that parent node.

       @return The node
     */
    public GameNode addNode(GameNode n) {
        return insertNode(n,num_children);
    }
    
    
    /**
       Add a node this this node's list of child nodes, at a specific position
     
     */
    public GameNode insertNode(GameNode n,int position) {
        if ( n == null )
            throw new IllegalArgumentException("node can't be null");

        if ( n.parent != null )
            n.parent.removeNode(n);

        synchronized(this) {
            if ( position < 0 ) position = 0;
            if ( position > num_children ) position = num_children;
            
            if ( children == null ) children = new GameNode[2];
            if ( num_children == children.length ) {
                GameNode[] new_children = new GameNode[children.length*2];
                for( int i=0; i<num_children; ++i )
                    new_children[i] = children[i];
                children = new_children;
            }
        
            for ( int i=num_children; i > position; --i ) children[i] = children[i-1];
            children[position] = n;
            num_children++;

            _added(n);
        }

        return this;
    }

    
    /**
       Insert a node into the tree by making it a child of this node, and reparenting all of this
       node's children to the new node
     */
    public GameNode spliceIn(GameNode insertNode) {
        while ( num_children > 0 ) {
            insertNode.addNode(children[0]);
        }
        this.addNode(insertNode);
        return this;
    }

    
    /**
       Remove a node from the tree after moving all of its children to the parent
     */
    public GameNode spliceOut() {
        GameNode oldParent = parent;
        remove();
        while ( num_children > 0 ) {
            oldParent.addNode(children[0]);
        }
        
        return this;
    }

    
    /**
       Remove a node from this node's list of child nodes.

       @param n The node to remove
       @returns The node
    */
    public GameNode removeNode(GameNode n) {
        if ( n == null )
            throw new IllegalArgumentException("node can't be null");

        if ( n.parent == this ) {
            synchronized(this) {
                for ( int i=num_children-1; i>=0; --i ) {
                    if ( children[i].equals(n) ) {
                        for ( int j=i+1; j<num_children; ++i, ++j )
                            children[i] = children[j];
                        num_children--;
                        break;
                    }
                }
                n.parent = null;
                n.onRemoved(this);
            }
        }

        return this;
    }


    /**
       Replace a node in this node's list of child nodes with another node

       @param oldNode The node to replace
       @param newNode The node to put in its place
       @returns The node
    */
    public GameNode replaceNode(GameNode oldNode,GameNode newNode) {
        if ( oldNode == null )
            throw new IllegalArgumentException("oldNode can't be null");
        if ( newNode == null )
            throw new IllegalArgumentException("newNode can't be null");

        if ( oldNode.parent == this ) {
            synchronized(this) {
                for ( int i=num_children-1; i>=0; --i ) {
                    if ( children[i].equals(oldNode) ) {
                        _removed(oldNode);
                        children[i] = newNode;
                        _added(newNode);
                        break;
                    }
                }
            }
        }

        return this;
    }


    /**
       Remove this node from it's parent (if any)
       @returns The node
    */
    public GameNode remove() {
        if ( parent != null )
            parent.removeNode(this);
        return this;
    }


    protected void _added(GameNode n) {
        n.parent = this;
    
        // Need to also call onSetup() on the node, and
        // any of its children, if onSetup() was previously
        // called on this node
        if ( gameController != null ) {
            _addedSetupVisitor.gameController = this.gameController;
            n.visit(_addedSetupVisitor,false);
        }

        // Call onAdded() after onSetup() so gameController is in place
        n.onAdded(this);
    }


    private static class AddedSetupVisitor implements Visitor {
        GameController gameController;
        public void run(GameNode node) {
            node.onSetup(gameController);
        }
    };
    static final private AddedSetupVisitor _addedSetupVisitor = new AddedSetupVisitor();



    protected void _removed(GameNode n) {
        n.parent = null;
        //n.gameController = null;
        n.onRemoved(this);
    }


    /** Called when the node is added into the tree */
    public void onAdded(GameNode newParent) {}

    /** Called when the node is remove from the tree */
    public void onRemoved(GameNode oldParent) {}



    /**
       Find a node with a given name.

       This will search the entire tree for a matching node, starting with the
       called node -- so it can be expensive.  It should not be done frequently.

       The name can be a hierarchical name, with name parts separated by '.'.  For example,
       
         find("foo.bar");

       would search for a node named "bar" contained in a node named "foo"

       The primary intended purpose of this method is to find related nodes
       during the onSetup() call, once all the tree has been constructed, to
       associated related nodes (e.g. the visible rendering node corresponding
       to a model / logic node)
    */
    public GameNode findNode(String name) {
        return findNode(name,null);
    }


    public GameNode findNode(String name, GameNode skipNode) {
        if ( this == skipNode ) return null;
        if ( name.equals(this.name) ) return this;

        String[] parts = name.split("\\.",2);

        // First go down the tree
        for( int i=0; i<num_children; ++i ) {
            GameNode child = children[i];
            if ( child == skipNode ) continue;
            GameNode found = child.findNodeDown(parts[0],child);
            if ( found != null ) {
                if ( parts.length > 1 ) return found.findNodeDown(parts[1],child);
                else return found;
            }
        }

        // Then go up the tree, down parallel branches
        if ( parent != null ) {
            GameNode found = parent.findNode(parts[0],this);
            if ( found != null ) {
                if ( parts.length > 1 ) return found.findNodeDown(parts[1],this);
                else return found;
            }
        }


        return null;
    }


    /**
       Special findNode() helper method that only searches down the
       hierarchy -- preventing cycles of searching up/dn/up/dn
    */
    protected GameNode findNodeDown(String name,GameNode skipNode) {
        if ( name.equals(this.name) ) return this;

        String[] parts = name.split("\\.",2);

        for( int i=0; i<num_children; ++i ) {
            GameNode found = children[i].findNodeDown(parts[0],skipNode);
            if ( found != null ) {
                if ( parts.length > 1 ) return found.findNodeDown(parts[1],skipNode);
                else return found;
            }
        }

        return null;
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
       Called when the game graph is being initialized.

       This happens after the game tree is created, so all of the
       nodes should exist.  This gives the nodes a chance to do final
       setup, so as finding specific other nodes in the tree.
       
       If a subclass overrides this method, be sure to call super.onSetup()
    */
    public void onSetup(GameController gameController) {
        this.gameController = gameController;
    }


    /**
       Called when the game surface has been created.

       This happens after the game tree is setup, and immediately before
       the first frame is rendered.

       If a subclass overrides this method, be sure to call super.onSetup()
    */
    public void onSurfaceCreated(RenderContext renderContext) {
        if ( gameController != null )
            gameController.renderContext = renderContext;
    }


    /**
       Called when the game surface is resized.  This is called at least
       once, immediately before rendering the first frame.  It may be
       called additional times, if the app is setup to auto-rotate, etc

       If a subclass overrides this method, be sure to call super.onSetup()
    */
    public void onSurfaceChanged(int width, int height) {
    }




    /**
       Update this node according to a specific amount of elapsed time
       
       @param millis Number of milliseconds since last update
       @return true if something changed with the node.
    */
    public boolean update(long millis) {
        boolean updated = false;
        if ( updatePrefix(millis) ) updated = true;

        synchronized(this) {
            for(int i=0; i<num_children; ++i) {
                GameNode child = children[i];
                if ( child.update(millis) ) updated = true;
            }
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



    /**
       Post a GameEvent that propagates up the tree from this node.

       The event is just added to a linked list of events to be dispatched
       on the next call to dispatchEvents(), which happens during update()
    */
    public void postUp(GameEvent event) {
        event.origin = this;
        event.up = true;
        gameController.events.post(event);
    }



    /**
       ost a GameEvent that propagates down the tree from this node

       The event is just added to a linked list of events to be dispatched
       on the next call to dispatchEvents(), which happens during update()
    */
    public void postDown(GameEvent event) {
        event.origin = this;        
        event.up = false;
        gameController.events.post(event);
    }


    /**
       Post an event to propagate up after a certain delay
    */
    public void postUpDelayed(GameEvent event,long delay) {
        GameEvent.Delayed delayed = GameEvent.Delayed.obtain(this,event,true);
        gameController.uiHandler.postDelayed(delayed,delay);
    }


    /**
       Post an event to propagate down after a certain delay
    */
    public void postDownDelayed(GameEvent event,long delay) {
        GameEvent.Delayed delayed = GameEvent.Delayed.obtain(this,event,false);
        gameController.uiHandler.postDelayed(delayed,delay);
    }

    
    /**
       Dispatch an event up the tree from this node
     */
    public boolean dispatchEventUp(GameEvent event) {
        if ( onGameEvent(event) )
            return true; // consumed
        
        // Propagate it back down on side branches
        if ( this != event.origin )
            if ( dispatchEventSideways(event) )
                return true; // consumed

        // Now go up the tree
        if ( parent == null ) {
            //Log.w("Can't dispatch an event (type=%d) up from a node not in the tree (%s)",
            //      event.type, this);
        } else {
            event.lastUp = this;
            return parent.dispatchEventUp(event);
        }
        
        return false;
    }

    
    /**
       This method is in here as a separate method just to enable it to be
       overridden in RenderNode.  The 'sideways' propagation really happens
       in dispatchEventDown(), where it pays attention to the event.lastUp field
     */
    protected boolean dispatchEventSideways(GameEvent event) {
        return dispatchEventDown(event,false);
    }

    
    /**
        Dispatch an event down the tree from this node
     */
    public boolean dispatchEventDown(GameEvent event,boolean doLocal) {
        if ( doLocal && onGameEvent(event) )
            return true; // consumed
        
        for(int i=0; i<num_children; ++i) {
            GameNode child = children[i];
            if ( child != event.lastUp )
                if ( child.dispatchEventDown(event,true) )
                    return true;
        }
        
        return false;
    }


    /**
       Handle a GameEvent
    */
    public boolean onGameEvent(GameEvent event) {
        return false;
    }

}
