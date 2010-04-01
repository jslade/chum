package chum.input;

import chum.engine.GameNode;
import chum.gl.RenderContext;
import chum.util.Log;

import android.view.MotionEvent;
import android.view.View;


/**
   TouchInputNode handles touch input from the game view.  Touch input events are received
   by the game view in the UI thread, and made available to game/rendering thread.
*/
public class TouchInputNode extends InputNode
    implements View.OnTouchListener {

    /** The delay to force in the UI thread between events. */
    public int touchDelay = 20; // 20ms



    public TouchInputNode() {
        super();

    }


    /**
       At setup time, registers this node as a listener for the view, so touch
       events will get sent here
    */
    public void onSetup(RenderContext renderContext) {
        // todo: for now, listens to events on the glSurface, but should allow
        // for cases where another view handles events (e.g. an overlay view)
        renderContext.glSurface.setOnTouchListener(this);
    }


    /**
       Called when a touch event is dispatched to the view.
    */
    public boolean onTouch(View v, MotionEvent event) {
        handle(v,event);
        throttle();
        return true;
    }


    /**
       Do something with the touch event
    */
    protected void handle(View v, MotionEvent event) {
        

    }

       
    /**
       Throttle the frequency of touch events by sleeping the UI thread
     */
    protected void throttle() {
        if ( touchDelay > 0 ) {
            try { Thread.currentThread().sleep(touchDelay); }
            catch (java.lang.InterruptedException e) {}
        }
    }


}
