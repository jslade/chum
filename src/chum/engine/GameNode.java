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
        gameController.gameHandler.postDelayed(delayed,delay);
    }


    /**
       Post an event to propogate down after a certain delay
    */
    public void postDownDelayed(GameEvent event,long delay) {
        GameEvent.Delayed delayed = GameEvent.Delayed.obtain(this,event,false);
        gameController.gameHandler.postDelayed(delayed,delay);
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
        if ( event == dispatchingEvent ) return; // prevents cycles
        
        if ( onGameEvent(event) )
            return; // consumed
        
        if ( parent != null )
            parent.dispatchEventUp(event);
    }


    /**
       Dispatch an event down the tree from this node
    */
    protected void dispatchEventDown(GameEvent event) {
        if ( event == dispatchingEvent ) return; // prevents cycles

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
