package chum.engine;

import chum.cfg.Config;
import chum.gl.RenderContext;
import chum.util.DefaultExceptionHandler;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
   GameActivity is intended to provide the basic setup for using the rest of the engine
   components (GameContext, GameTree, etc) along with a GLSurfaceView.
 */
public abstract class GameActivity extends Activity 
    implements GLSurfaceView.Renderer
{
    /** GLSurfaceView **/
    public GLSurfaceView glSurface;

    /** The GameController */
    public GameController gameController;

    /** The RenderContext */
    public RenderContext renderContext;

    /** GameTree that implements game logic and rendering **/
    protected GameTree tree;


    /**
       Called on creation of the Activity
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupExceptionHandler();

        gameController = new GameController();
        gameController.uiHandler = new Handler(new Handler.Callback(){
                public boolean handleMessage(Message msg) {
                    return handleUI(msg);
                }
            });
        gameController.paused = false;

        glSurface = createGLSurface();
        glSurface.setRenderer(this);
        this.setContentView(glSurface);

        // By default, the glSurface is the view that recieves
        // the input events -- its the view the event listeners
        // should attach to:
        gameController.inputView = glSurface;

        setGameTree(createGameTree());
    }


    /**
       Create the GLSurfaceView
    */
    protected GLSurfaceView createGLSurface() {
        GLSurfaceView glsv = new GLSurfaceView(this);
        return glsv;
    }


    /**
       Create the GameTree instance to be used for this activity
    */
    protected GameTree createGameTree() {
        return new GameTree.Dummy();
    }


    /**
       Set the GameTree instance to be used for this activity
    */
    public void setGameTree(GameTree tree) {
        this.tree = tree;
        gameController.tree = tree;
    }


    /**
       Called each Frame
    */
    public void onDrawFrame(GL10 _gl) {
        gameController.update();
    }


    /**
       Called when the GLSurfaceView has finished initialization
    */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Looper.prepare();
        gameController.gameHandler = new Handler(new Handler.Callback(){
                public boolean handleMessage(Message msg) {
                    return handleGame(msg);
                }
            });

        renderContext = new RenderContext(this,gl,config);
        renderContext.glSurface = this.glSurface;

        this.onSetup(gameController);
        tree.doSetup(gameController);

        this.onSurfaceCreated(this.renderContext);
        tree.doSurfaceCreated(this.renderContext);

	gameController.lastFrameStart = 
            gameController.fpsStart = SystemClock.uptimeMillis();
    }


    /**
       Called when the surface size changed, e.g. due to tilting
    */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        renderContext.width = width;
        renderContext.height = height;
        this.onSurfaceChanged(width, height);
        tree.doSurfaceChanged(width, height);
    }


    /**
       Called to perform initial setup
    */
    protected void onSetup(GameController gameController) {

    }


    /**
       Called when the GLSurfaceView has finished initialization.
       This method is meant to be overridden in a subclass.
    */
    protected void onSurfaceCreated(RenderContext renderContext) {

    }


    /**
       Called when the GLSurface view has changed size (or is created
       for the first time).
       This method is meant to be overridden in a subclass.
    */
    protected void onSurfaceChanged(int width, int height) {

    }


    /**
       Called when the application is paused. We need to
       also pause the GLSurfaceView.
    */
    @Override
    protected void onPause() {
	super.onPause();
	glSurface.onPause();
        gameController.paused = true;
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
        gameController.paused = false;
    }

    
    /**
       @return the current average framerate, as an integer number
       of frames per second
    */
    public int getFPS() {
        return gameController.getFPS();
    }


    /**
       Handle a message sent to the main (UI) thread
    */
    public boolean handleUI(Message msg) {
        return false;
    }


    /**
       Handle a message sent to the game thread
    */
    public boolean handleGame(Message msg) {
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
