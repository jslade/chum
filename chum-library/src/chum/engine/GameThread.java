package chum.engine;

import chum.util.Log;


/**
 * The GameThread loops continuously to both do both logic updates and rendering.
 * It's purpose is to decouple from both the main/UI thread, and from the rendering thread.
 * 
 * UI events from the UI thread are queued up in the GameThread to be processed each iteration,
 * allowing the UI thread to continue on without blocking on the event processing.
 * 
 * This thread is interlocked with the rendering thread (from GLSurfaceView).  Most/much of
 * the rendering happens in this thread (by traversing the rendering part of the GameTree),
 * but the final flush of the OpenGL command buffer, then blocking on the OpenGL rendering,
 * happens in the rendering thread.  That allows the GameThread to start the next frame
 * immediately.
 * 
 * @author jeremy
 *
 */
public class GameThread extends Thread {

    /** The GameController */
    public GameController gameController;
    
    
    
    public GameThread(GameController gameController) {
        setName("GameThread");
        this.gameController = gameController;
    }
    

    @Override
    public void run() {
        Log.d("GameThread started");

        GameNode logic = gameController.tree.logic;

        Log.d("%s entering loop",this);
        boolean finished = false;
        
        while (!finished) {
            // Can't proceed while rendering in progress
            if ( gameController.renderLock.rendering ) {
                synchronized(gameController.renderLock) {
                    try { gameController.renderLock.wait(); }
                    catch(InterruptedException e) {}
                }
            }
            
            // Wait until an event occurs or its time to update again
            if ( gameController.events.first == null ) {
                synchronized(gameController.events) {
                    try { gameController.events.wait(gameController.targetInterval); }
                    catch(InterruptedException e) {}
                }
            }
            
            long frameDelta = gameController.frameDelta; 

            // Process pending input
            
            // Process queued events
            int dispatched = 0;
            while( gameController.events.first != null ) {
                GameEvent event = gameController.events.get();
                dispatchEvent(event);
                dispatched++;
            }
            
            // Process the logic half of the GameTree
            //if ( logic.update(frameDelta) ||
            //     dispatched > 0 ) {
            //    gameController.updated();
            //}
            logic.update(frameDelta);
            gameController.updated();
            

            // Check for pause in the game
            while ( gameController.paused ) {
                synchronized(gameController.pauseLock) {
                    try { gameController.pauseLock.wait(); }
                    catch(InterruptedException e) {}
                }
            }
        
        }
        
    }

    
    
    protected void dispatchEvent(GameEvent event) {
        if ( event.up ) {
            event.origin.dispatchEventUp(event);
        } else {
            event.origin.dispatchEventDown(event);
        }
    }
}
