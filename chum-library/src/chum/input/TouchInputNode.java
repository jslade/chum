package chum.input;

import chum.engine.GameController;
import chum.engine.GameEvent;

import android.view.MotionEvent;
import android.view.View;


/**
   TouchInputNode handles touch input from the game view.  Touch input events are received
   by the game view in the UI thread, and made available to game/rendering thread.
*/
public class TouchInputNode extends InputNode
    implements View.OnTouchListener {

    /** The delay to force in the UI thread between events. */
    public int touchDelay = 8;



    public TouchInputNode() {
        super();

    }


    /**
       At setup time, registers this node as a listener for the view, so touch
       events will get sent here
    */
    @Override
    public void onSetup(GameController gameController) {
        super.onSetup(gameController);

        // todo: for now, listens to events on the glSurface, but should allow
        // for cases where another view handles events (e.g. an overlay view)
        gameController.inputView.setOnTouchListener(this);
    }


    /**
       Called when a touch event is dispatched to the view.
       
       All touch events get queued into the normal GameEvent queue,
       and will get dispatched back to this node via onGameEvent() 
    */
    public boolean onTouch(View v, MotionEvent event) {
    	// Have to queue a copy of the original event, since the
    	// original will get recycled after this method returns
    	MotionEvent copy = MotionEvent.obtain(event);
    	postUp(GameEvent.obtain(GameEvent.INPUT_TOUCH,copy));

        switch(event.getAction()) {
        case MotionEvent.ACTION_MOVE:
            throttle();
            break;
        }

        return true;
    }


    /**
       Do something with the touch event
    */
    protected boolean onTouch(MotionEvent event) {
    	return false;
    }

       
    /**
       Throttle the frequency of touch events by sleeping the UI thread
     */
    protected void throttle() {
        if ( touchDelay > 0 ) {
            try { Thread.sleep(touchDelay); }
            catch (java.lang.InterruptedException e) {}
        }
    }


    @Override
    public boolean onGameEvent(GameEvent event) {
    	if(event.type == GameEvent.INPUT_TOUCH) {
    		return onTouch((MotionEvent)event.object);
    	}
    	return super.onGameEvent(event);
    }

}
