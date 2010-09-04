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
    
    
    public long targetInterval2 = targetInterval * 2;
    public long targetInterval3 = targetInterval * 3;    
    
    
    /** The GameThread for running all the logic and rendering */
    public GameThread gameThread;
    
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
        gameThread = new GameThread(this);
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
        gameThread.start();
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
     * 
     * The update is split between this thread and the GameThread.  First the GameThread is
     * woken up to do the logic updates and the main part of rendering (generating all the
     * OpenGL commands to fill the command buffer).
     * 
     * When that finishes, this thread proceeds -- by returning.  That causes the OpenGL
     * command buffer to get flushed, to finish all the rendering and swap in the
     * new display buffer.
     * 
     * In the mean time, the GameThread can actually continue on with the next frame -
     * it blocks before it starts the rendering part of the tree, however, waiting for
     * the next signal from this thread.
     * 
     * This scheme is designed to give the maximum amount of time to the GameThread to
     * do its updates, letting as much of the blocking part of the OpenGL rendering
     * as possible happen in another thread.
     */
    public void onDrawFrame() {
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
        }
            
        // Wait for GameThread to signal update has finished
        if( !renderLock.updated ) {
            synchronized(renderLock) {
                try { renderLock.wait(targetInterval); }
                catch(InterruptedException e) {}
            }
        }
        
        // Do the rendering part of the tree 
        renderLock.rendering = true;
        tree.rendering.update(frameDelta);
        renderLock.rendering = false;
        synchronized(renderLock) {
            renderLock.updated = false;
            renderLock.notifyAll();
        }
    }
    
    
    public void updated() {
        synchronized(renderLock) {
            renderLock.updated = true;
            renderLock.notifyAll();
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
        
        public GameEvent get() {
            synchronized(this) {
                if ( first == null ) return null;
                GameEvent event = first;
                first = event.nextQueued;
                if ( last == event ) last = first;
                return event;
            }
        }
    }
    

    
    public static class RenderLock {
        public volatile boolean updated = false;
        public volatile boolean rendering = false;
    }
    
    public static class PauseLock {
    }

}
