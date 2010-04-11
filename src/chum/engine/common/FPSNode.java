package chum.engine.common;

import chum.engine.GameController;
import chum.engine.GameEvent;
import chum.engine.GameNode;
import chum.util.Log;


/**
   FPSNode is a common helper node that logs the current FPS, either to the logger,
   to a TextNode, or both
*/
public class FPSNode extends GameNode {

    /** The interval (int milliseconds) between updating the FPS */
    public long interval = 3000;

    /** The most recent FPS */
    public int fps = 0;

    /** Whether to log to the logger */
    public boolean toLogger = true;

    /** Callback to be executed on each update */
    public Callback callback;


    public FPSNode() {
        super();
    }
        
    public FPSNode(Callback callback) {
        super();
        this.callback = callback;
    }
        

    @Override
    public void onSetup(GameController gameController) {
        super.onSetup(gameController);
        showFPS(); // kick off the cycle
    }

    

    @Override
    public boolean onGameEvent(GameEvent event) {
        if ( event.object == this ) {
            showFPS();
            return true;
        }

        return super.onGameEvent(event);
    }


    public void showFPS() {
        fps = gameController.getFPS();
        if ( callback != null )
            callback.run(this, fps);

        if ( toLogger )
            Log.d("FPS = %d", fps);

        // Show it again in the future
        postUpDelayed(GameEvent.obtain(0,this),interval);
    }


    public interface Callback {
        public void run(FPSNode node, int fps);
    }

}
