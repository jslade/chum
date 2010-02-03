package chum.engine;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import chum.util.Log;


/**

 */
public class GameActivity extends Activity 
    implements GLSurfaceView.Renderer
{	
    /** GLSurfaceView **/
    private GLSurfaceView glSurface;

    /** GL context passed to onSurfaceCreated */
    private GL10 gl;

    /** EGLConfig the surface is using */
    private EGLConfig glConfig;

    /** with and height of the viewport **/
    private int width, height;
    
    /** GameListener **/
    private GameListener listener = new GameListener.Dummy();

    /** Start time of the last frame (milliseconds) */
    private long lastFrameStart;

    /** Frame counter */
    private int frameCounter;
    
    /** Frame time accumulator */
    private long frameTimeAccum;

    /** The last calculated frame rate */
    private int fps;

       
    /**
       Called on creation of the Activity
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glSurface = new GLSurfaceView(this);
        glSurface.setRenderer(this);
        this.setContentView(glSurface);
    }


    /**
       Called each Frame
    */
    @Override
    public void onDrawFrame(GL10 _gl) {
	long currentFrameStart = android.os.SystemClock.uptimeMillis();
	long frameDelta = currentFrameStart - lastFrameStart;

	listener.step(frameDelta);

	lastFrameStart = currentFrameStart;
        frameCounter++;
        frameTimeAccum += frameDelta;
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
       Called when the GLSurfaceView has finished initialization
    */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        this.gl = gl;
        this.glConfig = config;

	lastFrameStart = android.os.SystemClock.uptimeMillis();
        listener.onSurfaceCreated(this,gl);
    }

    /**
       Called when the application is paused. We need to
       also pause the GLSurfaceView.
    */
    @Override
    protected void onPause() {
	super.onPause();
	glSurface.onPause();		
    }

    /**
       Called when the application is resumed. We need to
        also resume the GLSurfaceView.
    */
    @Override
    protected void onResume() {
	super.onResume();
	glSurface.onResume();		
    }		

    
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
        if ( frameTimeAccum <= 0 )
            return 0;
        if ( frameTimeAccum < 2000 )
            return fps;

        fps = (int)(1000 * frameCounter / frameTimeAccum);
        frameTimeAccum = 1000;
        frameCounter = fps;

        return fps;
    }

}
