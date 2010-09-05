package chum.engine;

import chum.gl.RenderContext;

import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;


/**
 * GameController is used to track shared info needed by all of the GameNodes.
 * 
 * It is typically created by a GameActivity, but GameActivity is not explicitly
 * required.
 * 
 * TODO: can't currently use GameController without GameActivity -- need to get
 * cleaner separation
 */
public class GameController {

    /** The GameActivity */
    public GameActivity activity;

    /** The GameTree representing the game logic, controllers, renderers */
    public GameTree tree;

    /** The view that handles input events */
    public View inputView;

    /** The rendering context */
    public RenderContext renderContext;

    /**
     * with and height of the viewport TODO: can these be removed? makes more
     * sense to have in RenderContext?
     **/
    public int width, height;

    /** Whether the activity is paused */
    public boolean paused;

    /**
     * Total amount of elapsed game time (wall clock - paused time)
     * (milliseconds)
     */
    public long totalElapsed;

    /** The time the current frame started */
    public long currentFrameStart;

    /** Start time of the last frame (milliseconds) */
    public long lastFrameStart;

    /** The difference between the start of the current and last frames */
    public long frameDelta;
    
    /** Frame counter */
    public long frameCounter;

    /** FPS start time */
    public long fpsStart;

    /** The last calculated frame rate */
    public int fps;

    
    /** The target update interval.
     * Default of 16 means ~60fps
     * Set this higher for lower target framerate.
     * Setting this lower doesn't make much sense, since hardware refresh typically
     * limited to 60fps
     */
    public long targetInterval = 16;
    
    
    /** Handler for sending messages to the main (UI) thread */
    public Handler uiHandler;

    /** Global randomizer instance available throughout the game */
    public static final Random random = new Random(android.os.SystemClock.uptimeMillis());

    
    final EventQueue events;
    final RenderLock renderLock;
    final PauseLock pauseLock;

    
    /**
       
    */
    public GameController(GameActivity activity) {
        this.activity = activity;
        events = new EventQueue();
        renderLock = new RenderLock();
        pauseLock = new PauseLock();
        preallocateEventPools();
    }

    
    /**
     * Create the pools of GameEvents, etc ahead of time so allocation
     *  doesn't have to happen during game play
     */
    public void preallocateEventPools() {
        // Allocate a bunch of GameEvents, put them back into the pool
        GameEvent[] gameEvents = new GameEvent[32];
        for(int i=0; i<gameEvents.length; ++i) {
            gameEvents[i] = GameEvent.obtain();
        }
        for(int i=0; i<gameEvents.length; ++i) {
            gameEvents[i].recycle();
        }
        

    }
    
    
    /**
     * Called once to start the GameThread initially, after the GameTree is created.
     */
    public void start() {
    }
    

    /**
     * Called to pause the game
     */
    public void onPause() {
        synchronized(pauseLock) {
            paused = true;
        }   
    }
    
    
    /**
     * Called to resume the game
     * 
     */
    public void onResume() {
        synchronized(pauseLock) {
            resetFrame();
            paused = false;
            pauseLock.notifyAll();
        }
    }
    
    
    
    /**
     * Called each frame, from the GLSurfaceView rendering thread.
     */
    public void update() {
        lastFrameStart = currentFrameStart;
        currentFrameStart = SystemClock.uptimeMillis();
        frameDelta = currentFrameStart - lastFrameStart;
        totalElapsed += frameDelta;
        frameCounter++;
        
        // If frames are finishing fast, sleep to save power, leave time
        // for other threads, etc
        if ( frameDelta < targetInterval ) {
            synchronized(renderLock) {
                try { renderLock.wait(targetInterval - frameDelta); }
                catch(InterruptedException e) {}
            }
            frameDelta = targetInterval;
        }

        // Process pending input

        // Process queued events
        int dispatched = events.dispatchAll();
        
        // Process the logic half of the GameTree
        if (tree.logic.update(frameDelta) ||
            dispatched > 0 ) {
            // Do the rendering part of the tree 
            tree.rendering.update(frameDelta);
        }

        // Check for pause in the game
        while (paused) {
            synchronized(pauseLock) {
                try { pauseLock.wait(); }
                catch(InterruptedException e) {}
            }
        }
    }
    
    
    /**
     * Return the calculated FPS
     */
    public int getFPS() {
        long now = SystemClock.uptimeMillis();
        long elapsed = now - fpsStart;
        if (elapsed < 3000)
            return fps;

        fps = (int) (1000 * frameCounter / elapsed);

        frameCounter = 0;
        fpsStart = now;

        return fps;
    }


    public void resetFrame() {
        lastFrameStart = currentFrameStart = SystemClock.uptimeMillis();
        
        frameCounter = 0;
        fpsStart = lastFrameStart;
    }
    

    public static class EventQueue {
        public volatile GameEvent first;
        public volatile GameEvent last;
        
        public void post(GameEvent event) {
            synchronized(this) {
                if ( last == null ) {
                    first = last = event;
                } else {    
                    last.nextQueued = event;
                    last = event;
                }
                event.nextQueued = null;
            }
        }
        
        public void dispatchEvent(GameEvent event) {
            if ( event.up ) {
                event.origin.dispatchEventUp(event);
            } else {
                event.lastUp = null;
                event.origin.dispatchEventDown(event,true);
            }
        }
        

        public int dispatchAll() {
            GameEvent dispatching;
            
            synchronized(this) {
                dispatching = first;
                first = last = null;
            }   

            int count = 0;
            while ( dispatching != null ) {
                dispatchEvent(dispatching);
                GameEvent dispatched = dispatching;
                dispatching = dispatching.nextQueued;
                dispatched.recycle();
                count++;
            }
            
            return count;
        }
    }
    

    
    public static class RenderLock {
    }
    
    public static class PauseLock {
    }

}
