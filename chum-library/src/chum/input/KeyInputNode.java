package chum.input;

import chum.engine.GameController;
import chum.engine.GameEvent;

import android.view.KeyEvent;
import android.view.View;


/**
   TouchInputNode handles touch input from the game view.  Touch input events are received
   by the game view in the UI thread, and made available to game/rendering thread.
*/
public class KeyInputNode extends InputNode
    implements View.OnKeyListener {

    public KeyInputNode() {
        super();

    }


    /**
       At setup time, registers this node as a listener for the view, so touch
       events will get sent here
    */
    @Override
    public void onSetup(GameController gameController) {
        super.onSetup(gameController);

        // TODO: only allows a single KeyInputNode -- is that okay?
        gameController.inputView.setOnKeyListener(this);
    }


    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int type = GameEvent.INPUT_KEY_DOWN;
        switch(event.getAction()) {
        case KeyEvent.ACTION_DOWN:
            // Some keys have to be dealt with immediately, can't be queued
            // into the game thread
            switch( handleImmediately(keyCode,event) ) {
            case HandledSpecial:   return true;
            case PropagateSpecial: return false;
            default: // Normal - queue it up like other keys
            }
            break;
        case KeyEvent.ACTION_UP:
            type = GameEvent.INPUT_KEY_UP;
            break;
        case KeyEvent.ACTION_MULTIPLE:
            type = GameEvent.INPUT_KEY_MULTI;
            break;
        }
        
        // Key events not explicitly handled immediately get queued to
        // be dispatched in the game thread
        postUp(GameEvent.obtain(type,event));

        // And these key events get swallowed:
        return true;
    }

    private static enum DownAction { Normal, HandledSpecial, PropagateSpecial};
    


    protected DownAction handleImmediately(int keyCode, KeyEvent event) {
        switch(event.getKeyCode()) {
        case KeyEvent.KEYCODE_BACK:
            return onBack(event) ? DownAction.HandledSpecial : DownAction.PropagateSpecial;
        case KeyEvent.KEYCODE_MENU:
            return onMenu(event) ? DownAction.HandledSpecial : DownAction.PropagateSpecial;
        }
        
        return DownAction.Normal; // consume this event as a normal keypress, not
    }
    

    /**
       Handle the 'back' key.
       This is called immediately when the key is pressed, so it happens in the 
     */
    protected boolean onBack(KeyEvent event) {
        return false; // Accept normal behavior
    }
    
    
    /**
       Handle the 'menu' key
     */
    protected boolean onMenu(KeyEvent event) {
        return false; // Accept normal behavior
    }
 
 
    /**
       Do something with the key event
    */
    protected boolean onKeyDown(KeyEvent event) {
    	return false;
    }

       
    /**
       Do something with the key event
     */
    protected boolean onKeyUp(KeyEvent event) {
        return false;
    }

    
    @Override
    public boolean onGameEvent(GameEvent event) {
        switch(event.type) {
        case GameEvent.INPUT_KEY_DOWN:
            return onKeyDown((KeyEvent)event.object);
        case GameEvent.INPUT_KEY_UP:
            return onKeyUp((KeyEvent)event.object);
        }
    	return super.onGameEvent(event);
    }


}
