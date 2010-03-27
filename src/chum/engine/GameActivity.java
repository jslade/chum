package chum.engine;

import chum.cfg.Config;
import chum.gl.RenderContext;
import chum.util.Log;
import chum.util.DefaultExceptionHandler;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**

 */
public abstract class GameActivity extends Activity 
    implements GLSurfaceView.Renderer,
               Handler.Callback
{	
    /** Whether the activity is paused */
    protected boolean paused;

    /** GLSurfaceView **/
    protected GLSurfaceView glSurface;

    /** The RenderContext */
    public RenderContext renderContext;

    /** GameTree that implements game logic and rendering **/
    protected GameTree tree;

    /** with and height of the viewport **/
    protected int width, height;
    
    /** Start time of the last frame (milliseconds) */
    protected long lastFrameStart;

    /** Frame counter */
    protected int frameCounter;
    
    /** FPS start time */
    protected long fpsStart;

    /** The last calculated frame rate */
    protected int fps;


    /** Handler for sending messages to the main (UI) thread */
    protected Handler mainHandler;


    /**
       Called on creation of the Activity
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupExceptionHandler();

        mainHandler = new Handler(this);
        paused = false;

        glSurface = createGLSurface();
        glSurface.setRenderer(this);
        this.setContentView(glSurface);

        setGameTree(new GameTree.Dummy(this));
    }


    /**
       Create the GLSurfaceView
    */
    protected GLSurfaceView createGLSurface() {
        GLSurfaceView glsv = new GLSurfaceView(this);
        return glsv;
    }


    /**
       Set the GameTree instance to be used for this activity
    */
    public void setGameTree(GameTree tree) {
        this.tree = tree;
    }


    /**
       Called each Frame
    */
    @Override
    public void onDrawFrame(GL10 _gl) {
	long currentFrameStart = SystemClock.uptimeMillis();
	long frameDelta = currentFrameStart - lastFrameStart;

	tree.update(frameDelta);

	lastFrameStart = currentFrameStart;
        frameCounter++;
    }


    /**
       Called when the GLSurfaceView has finished initialization
    */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        renderContext = new RenderContext(this,gl,config);
        renderContext.glSurface = this.glSurface;

        tree.onSurfaceCreated(this.renderContext);

	lastFrameStart = fpsStart = SystemClock.uptimeMillis();
    }


    /**
       Called when the surface size changed, e.g. due to tilting
    */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
	this.width = width;
	this.height = height;
        renderContext.width = width;
        renderContext.height = height;

        tree.onSurfaceChanged(this,width, height);
    }


    /**
       Called when the application is paused. We need to
       also pause the GLSurfaceView.
    */
    @Override
    protected void onPause() {
	super.onPause();
	glSurface.onPause();
        paused = true;
        if ( tree != null ) tree.onPause();
    }


    /**
       Called when the application is resumed. We need to
        also resume the GLSurfaceView.
    */
    @Override
    protected void onResume() {
	super.onResume();
	glSurface.onResume();
        if ( tree != null ) tree.onResume();
        paused = false;
    }

    
    /**
       @return whether the GameActivity is currently paused
    */
    public boolean isPaused() { return paused; }



    /**
       @return the surface width in pixels
    */
    public int getWidth( ) {
	return width;
    }


    /**
       @return the surface height in pixels
    */
    public int getHeight( ) {
	return height;
    }



    /**
       @return the current average framerate, as an integer number
       of frames per second
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



    public Handler getMainHandler() {
        return mainHandler;
    }


    /**
       Handle a message sent to the main (UI) thread
    */
    public boolean handleMessage(Message msg) {
        return false;
    }



    /**
       Setup the default exception handler.

       Uses the Config setting to determine what type of exception
       handler should be used.  The handler is instantiated and
       installed as the default exception handler for all threads.

       Once the handler is installed, it is also given the chance
       to dispatch any exception reports that may have been
       generated on previous runs of the app.
    */
    protected void setupExceptionHandler() {
        // Create the new exception handler instance and register
        // it to handle all uncaught exceptions for all threads.
        DefaultExceptionHandler handler =
            Config.getConfig(this).defaultExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);

        // Dispatch any exceptions that may have been handled
        // and logged on previous runs of the app, but not yet
        // dispatched.
        handler.dispatch(this);
    }

}
