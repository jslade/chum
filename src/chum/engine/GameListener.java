package chum.engine;

import javax.microedition.khronos.opengles.GL10;


/**
   This is the main class for implementing game rendering and logic.

   This is implemented as an abstracct class instead of an interface
   simply as a (small) performance optimization: calling virtual
   methods has less overhead than calling interface methods.  The
   main loop method (step()) is called very frequently, so it should
   be as efficient to call as possible.
 */
public abstract class GameListener {

    /** The GL10 instance passed when the surface was created */
    protected GL10 gl;

    /** The GameActivity */
    protected GameActivity gameActivity;

    
    /**
       Called when the surface is initially created
    */
    public void onSurfaceCreated(GameActivity activity, GL10 gl) {
        this.gameActivity = activity;
        this.gl = gl;
        initGL();
    }


    /**
       Called when the surface is resized
    */
    public void onSurfaceChanged(GameActivity activity, GL10 gl,
                                 int width, int height) {
        this.gameActivity = activity;
        this.gl = gl;
        resized(width, height);
    }


    /**
       Set initial GL state, rendering options.
    */
    public abstract void initGL();


    /**
       Handle change in size
    */
    public abstract void resized(int width, int height);



    /**
       Called every frame

       @param frameDelta The number if milliseconds elapsed since 
       the start of the last frame.
    */
    public abstract void step(long delta);





    /**
       A dummy implementation of the GameListener
    */
    static class Dummy extends GameListener {
        public void initGL() {}
        public void resized(int width, int height) {}
        public void step(long delta) {}
    }
}
