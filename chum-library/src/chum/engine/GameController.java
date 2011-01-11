package chum.engine;

import chum.gl.RenderContext;
import chum.gl.render.primitive.RenderPrimitive;
import chum.util.Log;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * GameController is used to track shared info needed by all of the GameNodes.
 * 
 * It is typically created by a GameActivity, but GameActivity is not explicitly
 * required.
 * 
 * TODO: can't currently use GameController without GameActivity -- need to get
 * cleaner separation
 */
public class GameController
    implements GLSurfaceView.Renderer
{

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

    /** Whether the surface is ready to use */
    public boolean surfaceReady;
    
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
    final GameThread gameThread;
    final RenderLock renderLock;
    final PauseLock pauseLock;

    Thread renderThread;
    
    /**
       
    */
    public GameController(GameActivity activity) {
        this.activity = activity;
        events = new EventQueue();
        gameThread = new GameThread();
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
       Start the game thread if everything is ready to go
     */
    protected void maybeStart() {
        if ( gameThread.isAlive() ) return;
        if ( !surfaceReady ) return;
        
        tree.postDown(GameEvent.obtain(GameEvent.GAME_INIT));

        Log.d("GameController.start()");
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

        maybeStart();
    }
    
    
    
    /**
     * Called each frame, from the GameThread rendering thread.
     */
    protected void update() {
        assert(Thread.currentThread() == gameThread);
        
        //Log.d("update()");
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

        // Process queued events
        int dispatched = events.dispatchAll();
        
        // Process the logic half of the GameTree
        tree.update(frameDelta);
        
        // Do the rendering part of the tree 
        tree.render(renderContext);
        renderReady();
    }
    
    
    /**
       Called when the rendering chain is completed and ready
       to be executed.
     */
    protected void renderReady() {
        RenderPrimitive renderHead = null;
        
        // If there is a built render chain ready to go, remove it
        // and pass it to the rendering thread via the renderLock.
        // That way this thread can immediately start building another one
        if ( renderContext.renderTail != null ) {
            renderContext.renderTail.nextNode = null;
            
            renderHead = renderContext.renderHead;
            renderContext.renderHead = renderContext.renderTail = null;
            
            renderContext.phase = !renderContext.phase;
        }
        
        // Signal the rendering thread a new frame is ready to go.
        // This will block if the rendering thread is still busy,
        // which will throttle the game thread if it's running ahead
        // of the rendering.
        // If the rendering thread is currently idle, this is where
        // it gets woken up.
        synchronized(renderLock) {
            renderLock.renderHead = renderHead;
            //Log.d("renderLock.notify()");
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
    

    
    /**
        Called when the rendering thread is ready to draw the next frame.
     */ 
    public void onDrawFrame(GL10 gl10) {
        //Log.d("onDrawFrame()");
        RenderPrimitive rendering;
        assert(Thread.currentThread() == renderThread);
        
        synchronized(renderLock) {
            // Wait for a frame to be available before proceeding...
            if ( renderLock.renderHead == null ) {
                //reLog.d("renderLock.wait()");
                try { renderLock.wait(targetInterval); }
                catch(InterruptedException e) {}
            }
            
            // Remove the pending rendering chain 
            rendering = renderLock.renderHead;
            renderLock.renderHead = null;
        }

        // Now render the chain completely
        while( rendering != null ) {
            rendering.render(renderContext,gl10);
            rendering = rendering.nextNode;
        }
    }
 
 
    /**
        Called when the GLSurfaceView has finished initialization
        
        // TODO: need to handle case of surface being created after
        // a pause/resume of the activity
     */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.d("onSurfaceCreated: " + width +" x " + height);
        renderContext = new RenderContext(activity,gl,config);
        renderContext.glSurface = activity.glSurface;
        renderThread = Thread.currentThread();

        activity.onSetup(this);
        tree.doSetup(this);

        activity.onSurfaceCreated(renderContext);
        tree.doSurfaceCreated(renderContext);

        resetFrame();
    }
    

    /**
       Called when the surface size changed, e.g. due to tilting
     */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.d("onSurfaceChanged: " + width +" x " + height);
        renderContext.width = width;
        renderContext.height = height;
        tree.doSurfaceChanged(width, height);

        activity.onSurfaceChanged(width,height);

        // At this point, everything is setup for the game to begin
        // TODO: this should be handled differently if this is
        // called after the initial onSurfaceChanged()
        surfaceReady = true;
        maybeStart();
    }


    protected class RenderLock {
        public RenderPrimitive renderHead;
    }
 
    protected class PauseLock {
    }


    /**
       The GameThread runs a simple loop, calling update() each iteration.
       update() does the heavy-lifting.
     */
    protected class GameThread extends Thread {
        boolean done = false;

        GameThread() {
            super();
            setName("GameThread");
        }
        
        @Override
        public void run() {
           Log.d("GameThread.run()");
            while (!done) {
                GameController.this.update();

                // Check for pause in the game
                while (paused) {
                    synchronized(pauseLock) {
                        try { pauseLock.wait(); }
                        catch(InterruptedException e) {}
                    }
                }
            }
        }
    }
    
}
