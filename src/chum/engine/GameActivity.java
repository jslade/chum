package chum.engine;

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

import chum.cfg.Config;
import chum.util.Log;
import chum.util.DefaultExceptionHandler;


/**

 */
public class GameActivity extends Activity 
    implements GLSurfaceView.Renderer,
               Handler.Callback
{	
    /** Whether the activity is paused */
    protected boolean paused;

    /** GLSurfaceView **/
    protected GLSurfaceView glSurface;

    /** GL context passed to onSurfaceCreated */
    protected GL10 gl;

    /** EGLConfig the surface is using */
    protected EGLConfig glConfig;

    /** with and height of the viewport **/
    protected int width, height;
    
    /** GameListener **/
    protected GameListener listener = new GameListener.Dummy();

    /** Start time of the last frame (milliseconds) */
    private long lastFrameStart;

    /** Frame counter */
    private int frameCounter;
    
    /** FPS start time */
    private long fpsStart;

    /** The last calculated frame rate */
    private int fps;


    /** Handler for sending messages to the main (UI) thread */
    protected Handler mainHandler;


    /**
       Called on creation of the Activity
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        paused = false;

        glSurface = new GLSurfaceView(this);
        glSurface.setRenderer(this);
        this.setContentView(glSurface);

        mainHandler = new Handler(this);

        setupExceptionHandler();
    }


    /**
       Called each Frame
    */
    @Override
    public void onDrawFrame(GL10 _gl) {
	long currentFrameStart = SystemClock.uptimeMillis();
	long frameDelta = currentFrameStart - lastFrameStart;

	listener.step(frameDelta);

	lastFrameStart = currentFrameStart;
        frameCounter++;
    }


    /**
       Called when the GLSurfaceView has finished initialization
    */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.gl = gl;
        this.glConfig = config;

	lastFrameStart = fpsStart = SystemClock.uptimeMillis();
        listener.onSurfaceCreated(this,gl);
    }


    /**
       Called when the surface size changed, e.g. due to tilting
    */
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.gl = gl;
	this.width = width;
	this.height = height;
        listener.onSurfaceChanged(this, gl, width, height);
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
    }


    /**
       Called when the application is resumed. We need to
        also resume the GLSurfaceView.
    */
    @Override
    protected void onResume() {
	super.onResume();
	glSurface.onResume();
        paused = false;
    }

    
    /**
       @return whether the GameActivity is currently paused
    */
    public boolean isPaused() { return paused; }


    /**
       Sets the {@link GameListener}
       @param listener the GameListener
    */
    public void setGameListener(GameListener listener) {
	this.listener = listener;		
    }


    /**
       @return the GL10 instance
    */
    public GL10 getGL() {
        return gl;
    }


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
