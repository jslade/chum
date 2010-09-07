package chum.examples.skater;

import chum.engine.*;
import chum.input.TouchInputNode;

import android.view.MotionEvent;
import android.view.View;


public class TouchController extends TouchInputNode {

    // The last steering location
    public int lastY;

    private  static final int threshold = 3;


    @Override
    protected boolean onTouch(MotionEvent event) {
        int newY = (int)event.getY();
        if ( Math.abs(newY - lastY) >= threshold ) {
            lastY = newY;
            postUp(GameEvent.obtain(StateNode.Steered,newY));
        }
        return true;
    }

}
