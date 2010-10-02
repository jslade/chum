package chum.input;

import chum.engine.GameController;

import android.view.KeyEvent;


/**
   KeyInputNode handles key input from the game activity.  All the on*() events are
   processed in the UI thread, so they should return as quickly as possible (e.g.
   just queue up a GameEvent for processing in the game thread, then exit).
   
   The framework only supports a single KeyInputNode instance in the GameTree at
   a time.
*/
public class KeyInputNode extends InputNode {

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
        gameController.activity.keyNode = this;
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false; // Accept normal behavior
    }


    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false; // Accept normal behavior
    }


    /**
       Handle the 'back' key.
       This is called immediately when the key is pressed, so it happens in the 
     */
    public boolean onBackPressed(KeyEvent event) {
        return false; // Accept normal behavior
    }
    
    
    /**
       Handle the 'menu' key
     */
    public boolean onMenuPressed(KeyEvent event) {
        return false; // Accept normal behavior
    }
 
}
