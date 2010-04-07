package chum.engine;

import chum.gl.RenderContext;
import chum.util.Log;


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



    public GameNode() {
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

        return this;
    }


    /**
       Remove a node from this node's list of child nodes.

       @param n The node to remove
       @returns The node
    */
    public GameNode removeNode(GameNode n) {
        if ( n.parent == this ) {
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


    /**
       Find a node with a given name.

       This will search the entire tree for a matching node, starting with the
       called node -- so it can be expensive.  It should not be done frequently.

       The primary intended purpose of this method is to find related nodes
       during the onSetup() call, once all the tree has been constructed, to
       associated related nodes (e.g. the visible rendering node corresponding
       to a model / logic node)
    */
    public GameNode findNode(String name) {
        if ( name.equals(this.name) ) return this;

        if ( parent != null ) {
            GameNode found = parent.findNode(name);
            if ( found != null ) return found;
        }

        for( int i=0; i<num_children; ++i ) {
            GameNode found = children[i].findNodeDown(name);
            if ( found != null ) return found;
        }

        return null;
    }


    /**
       Special findNode() helper method that only searches down the
       hierarchy -- preventing cycles of searching up/dn/up/dn
    */
    protected GameNode findNodeDown(String name) {
        if ( name.equals(this.name) ) return this;

        for( int i=0; i<num_children; ++i ) {
            GameNode found = children[i].findNodeDown(name);
            if ( found != null ) return found;
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
       setup, so as finding specific other nodes in the tree
    */
    public void onSetup(GameController gameController) {
        this.gameController = gameController;
    }


    /**
       Called when the game surface has been created.

       This happens after the game tree is setup, and immediately before
       the first frame is rendered.
    */
    public void onSurfaceCreated(RenderContext renderContext) {
        gameController.renderContext = renderContext;
    }


    /**
       Called when the game surface is resized.  This is called at least
       once, immediately before rendering the first frame.  It may be
       called additional times, if the app is setup to auto-rotate, etc
    */
    public void onSurfaceChanged(int width, int height) {
    }




    /**
       Update this node according to a specific amount of elapsed time
       
       @param millis Number of milliseconds since last update
       @return true if something changed with the node.
    */
    public boolean update(long millis) {
        dispatchEvents();

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



    // Pending event chains
    protected GameEvent pendingUpEvent;
    protected GameEvent pendingDownEvent;
    protected GameEvent dispatchingEvent;


    /**
       Post a GameEvent that propogates up the tree from this node.

       The event is just added to a linked list of events to be dispatched
       on the next call to dispatchEvents(), which happens during update()
    */
    public void postUp(GameEvent event) {
        event.nextQueued = null;
        synchronized(this) {
            if ( pendingUpEvent == null )
                pendingUpEvent = event;
            else {
                GameEvent pending = pendingUpEvent;
                while ( pending.nextQueued != null ) pending = pending.nextQueued;
                pending.nextQueued = event;
            }
        }
    }



    /**
       Post a GameEvent that propogates down the tree from this node

       The event is just added to a linked list of events to be dispatched
       on the next call to dispatchEvents(), which happens during update()
    */
    public void postDown(GameEvent event) {
        event.nextQueued = null;
        synchronized(this) {
            if ( pendingDownEvent == null )
                pendingDownEvent = event;
            else {
                GameEvent pending = pendingDownEvent;
                while ( pending.nextQueued != null ) pending = pending.nextQueued;
                pending.nextQueued = event;
            }
        }
    }


    /**
       Post an event to propogate up after a certain delay
    */
    public void postUpDelayed(GameEvent event,long delay) {
        GameEvent.Delayed delayed = GameEvent.Delayed.obtain(this,event,true);
        gameController.uiHandler.postDelayed(delayed,delay);
    }


    /**
       Post an event to propogate down after a certain delay
    */
    public void postDownDelayed(GameEvent event,long delay) {
        GameEvent.Delayed delayed = GameEvent.Delayed.obtain(this,event,false);
        gameController.uiHandler.postDelayed(delayed,delay);
    }


    /**
       Dispatch any pending events

       First removes all pending events so new events can get queued during the dispatch.
       Then dispatches all the up events, then down events.
    */
    protected void dispatchEvents() {
        GameEvent dispatchingUpEvent;
        GameEvent dispatchingDownEvent;

        synchronized(this) {
            dispatchingUpEvent = pendingUpEvent;
            dispatchingDownEvent = pendingDownEvent;
            pendingUpEvent = null;
            pendingDownEvent = null;
        }

        while ( dispatchingUpEvent != null ) {
            dispatchingEvent = dispatchingUpEvent;
            dispatchingUpEvent = dispatchingUpEvent.nextQueued;
            dispatchEventUp(dispatchingEvent);
            dispatchingEvent.recycle();
        }

        while ( dispatchingDownEvent != null ) {
            dispatchingEvent = dispatchingDownEvent;
            dispatchingDownEvent = dispatchingDownEvent.nextQueued;
            dispatchEventDown(dispatchingEvent);
            dispatchingEvent.recycle();
        }

        dispatchingEvent = null;
    }


    /**
       Dispatch an event up the tree from this node
    */
    protected void dispatchEventUp(GameEvent event) {
        if ( onGameEvent(event) )
            return; // consumed
        
        if ( parent != null )
            parent.dispatchEventUp(event);
    }


    /**
       Dispatch an event down the tree from this node
    */
    protected void dispatchEventDown(GameEvent event) {
        if ( onGameEvent(event) )
            return; // consumed
        
        for(int i=0; i<num_children; ++i)
            children[i].dispatchEventDown(event);
    }


    /**
       Handle a GameEvent
    */
    public boolean onGameEvent(GameEvent event) {
        return false;
    }

}
