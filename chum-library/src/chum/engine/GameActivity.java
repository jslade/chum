package chum.engine;

import chum.cfg.Config;
import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.input.KeyInputNode;
import chum.util.DefaultExceptionHandler;

import android.app.Activity;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

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

    /** GameTree that implements game logic and rendering */
    protected GameTree tree;

    /** View option: whether to hide the titlebar (default true) */
    protected boolean hideTitlebar = true;
    
    /** View option: whether to show fullscreen without status bar (default true) */
    protected boolean fullscreen = true;

    

    /**
       Called on creation of the Activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupExceptionHandler();

        gameController = new GameController(this);
        gameController.uiHandler = new Handler(new Handler.Callback(){
                public boolean handleMessage(Message msg) {
                    return handleUI(msg);
                }
            });
        gameController.paused = false;

        setViewOptions();
        applyViewOptions();
        glSurface = createGLSurface();
        glSurface.setRenderer(GameActivity.this);
        this.setContentView(createContentView(glSurface));

        // By default, the glSurface is the view that receives
        // the input events -- its the view the event listeners
        // should attach to:
        gameController.inputView = glSurface;

        setGameTree(createGameTree());
        
        
        // Always want to control the media volume in the game...
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }


    /**
       Create the view hierarchy for the activity.
       The hierarchy should include the given GLSurfaceView, but may contain others as well.
       Default is to just use the GLSurface view itself.
     */
    protected View createContentView(GLSurfaceView glSurface) {
    	return glSurface;
    }

    
    /**
       Set options for the view prior to setting the view content.
       Default is to set fullscreen, non-windowed.  Subclasses will generally override this method.
     */
    protected void setViewOptions() {
    }
    
    
    /**
       After view options are set, make them active
     */
    protected void applyViewOptions() {
    	if ( this.hideTitlebar ) requestWindowFeature(Window.FEATURE_NO_TITLE);
        if ( this.fullscreen ) getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    
    /**
       Create the GLSurfaceView
    */
    protected GLSurfaceView createGLSurface() {
        return new GLSurfaceView(this);
    }


    /**
       Create the GameTree instance to be used for this activity
    */
    protected GameTree createGameTree() {
        GameTree tree = new GameTree();
        tree.setLogicTree(createLogicTree());
        tree.setRenderTree(createRenderTree(tree.logic));
        return tree;
    }

    protected abstract GameNode createLogicTree();
    
    protected abstract RenderNode createRenderTree(GameNode logic);
    

    /**
       Set the GameTree instance to be used for this activity
    */
    public void setGameTree(GameTree tree) {
        this.tree = tree;
        gameController.tree = tree;
    }


    /**
       Called when the rendering thread is ready to draw the next frame.
    */
    public void onDrawFrame(GL10 gl) {
        gameController.update();
    
        // Prevent random lockups in Froyo/2.2
        // See http://groups.google.com/group/android-developers/browse_thread/thread/d5b7e87f4b42fa8f
        gl.glFinish();
    }


    /**
       Called when the GLSurfaceView has finished initialization
    */
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        renderContext = new RenderContext(this,gl,config);
        renderContext.glSurface = this.glSurface;

        this.onSetup(gameController);
        tree.doSetup(gameController);

        this.onSurfaceCreated(this.renderContext);
        tree.doSurfaceCreated(this.renderContext);

        gameController.resetFrame();
    }


    /**
       Called when the surface size changed, e.g. due to tilting
    */
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        renderContext.width = width;
        renderContext.height = height;
        this.onSurfaceChanged(width, height);
        tree.doSurfaceChanged(width, height);
        
        // At this point, everything is setup for the game to begin
        tree.postDown(GameEvent.obtain(GameEvent.GAME_INIT));
        gameController.start();
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
        gameController.onPause();
        if ( tree != null ) {
            tree.onPause();
            glSurface.onPause();
        }
    }


    /**
       Called when the application is resumed. We need to
       also resume the GLSurfaceView.
    */
    @Override
    protected void onResume() {
        super.onResume();
        if ( tree != null ) {
            tree.onResume();
            glSurface.onResume();
        }
        gameController.onResume();
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
       Handle a game event that bubbles to the top (not handled in the tree
       
     */
    public boolean onGameEvent(GameEvent event) {
        return false;
    }
    
    
    /**
       Post a game event
     */
    public void post(GameEvent event) {
        tree.postDown(event);
    }
    
    
    /**
       Post a game event after a delay
     */
    public void postDelayed(GameEvent event,long delay) {
        tree.postDownDelayed(event,delay);
    }


    public KeyInputNode keyNode = null;
    protected boolean trackingBack = false;
    protected boolean trackingMenu = false;

    /**
       Special processing for some key events.
       Sort of modeled on pattern recommended for 2.0 API, but still supporting pre-2.0

       TODO: simplify / update this when pre-2.0 API support is dropped, or figure
       out a way to use 2.0 features when running on such devices
     */
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if ( keyNode != null ) {
            switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                trackingBack = true;
                return true;

            case KeyEvent.KEYCODE_MENU:
                trackingMenu = true;
                return true;

            default:
                return keyNode.onKeyDown(keyCode,event);
            }
        }

        return super.onKeyDown(keyCode,event);
    }
    
    
    @Override
    public boolean onKeyUp(int keyCode,KeyEvent event) {
        if ( keyNode != null ) {
            switch(keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if ( trackingBack ) {
                    trackingBack = false;
                    if ( keyNode.onBackPressed(event) )
                        return true;
                    else
                        finish(); // This is the default behavior of BACK 
                }   
                break;

            case KeyEvent.KEYCODE_MENU:
                if ( trackingMenu ) {
                    trackingMenu = false;
                    if ( keyNode.onMenuPressed(event) )
                        return true;
                }
                break;

            default:
                return keyNode.onKeyUp(keyCode,event);
            }
        }

        return super.onKeyUp(keyCode,event);
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
