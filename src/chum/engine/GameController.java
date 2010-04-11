package chum.engine;

import chum.gl.RenderContext;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

import java.util.Random;


/**
   GameController is used to track shared info needed by all of the GameNodes.

   It is typically created by a GameActivity, but GameActivity is not explicitly required.
*/
public class GameController {

    
    /** The GameTree representing the game logic, controllers, renderers */
    public GameTree tree;
       
    /** The view that handles input events */
    public View inputView;

    /** The rendering context */
    public RenderContext renderContext;


    /** with and height of the viewport
        todo: can these be removed?  makes more sense to have in RenderContext?
     **/
    public int width, height;
    

    /** Whether the activity is paused */
    public boolean paused;


    /** Start time of the last frame (milliseconds) */
    public long lastFrameStart;

    /** Frame counter */
    public int frameCounter;
    
    /** FPS start time */
    public long fpsStart;

    /** The last calculated frame rate */
    public int fps;


    /** Handler for sending messages to the main (UI) thread */
    public Handler uiHandler;

    /** Handler for sending messages to the game / rendering thread */
    public Handler gameHandler;


    /** Global randomizer instance available throughout the game */
    public static final Random random =
        new Random( android.os.SystemClock.uptimeMillis() );


    /**
       
    */
    public GameController() {
        

    }


    /**
       Called each frame
    */
    public void update() {
	long currentFrameStart = SystemClock.uptimeMillis();
	long frameDelta = currentFrameStart - lastFrameStart;

	tree.update(frameDelta);

	lastFrameStart = currentFrameStart;
        frameCounter++;
    }


    /**
       Return the calculated FPS
    */
    public int getFPS() {
        long now = SystemClock.uptimeMillis();
        long elapsed = now - fpsStart;
        if ( elapsed < 3000 )
            return fps;

        fps = (int)(1000 * frameCounter / elapsed);

        frameCounter = 0;
        fpsStart = now;

        return fps;
    }

}

