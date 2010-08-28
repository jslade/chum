package chum.input;

import chum.engine.GameController;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


/**
   TouchInputNode handles touch input from the game view.  Touch input events are received
   by the game view in the UI thread, and made available to game/rendering thread.
*/
public class GestureInputNode extends TouchInputNode
    implements GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener {
    
    /** The GestureDetector to handle MotionEvents */
    public GestureDetector gd;
    
    /** Whether to enable detection of double taps */
    protected boolean detectDoubleTap;
    
    /** Whether to enable detection of long-presses */
    protected boolean detectLongPress;
    
    
    public GestureInputNode() {
        this(true,false);
    }
    
    public GestureInputNode(boolean detectDoubleTap,boolean detectLongPress) {
        super();
        this.detectDoubleTap = detectDoubleTap;
        this.detectLongPress = detectLongPress;
    }

    
    @Override
    public void onSetup(GameController gameController) {
        super.onSetup(gameController);
        final GameController gc = gameController;
        gameController.uiHandler.post(new Runnable(){
            public void run() {
                gd = new GestureDetector(gc.activity,GestureInputNode.this);
                gd.setIsLongpressEnabled(detectLongPress);
                if ( detectDoubleTap ) 
                    gd.setOnDoubleTapListener(GestureInputNode.this);
            }
        });
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ( gd.onTouchEvent(event) )
            return true; 
        return super.onTouch(v,event);
    }

    public boolean onDoubleTap(MotionEvent e) {
        return false;
    }
    
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
    
    public boolean onSingleTapConfirmed(MotionEvent e){
        return false;
    }
    
    
    public boolean onDown(MotionEvent e) {
        return false;
    }
    
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float veloX, float veloY) {
        return false;
    }
    
    public void onLongPress(MotionEvent e) {
    }
    
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distX, float distY) {
        return false;
    }
    
    public void onShowPress(MotionEvent e) {
    }
    
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }
}
