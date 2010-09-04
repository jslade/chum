package chum.examples;

import chum.engine.GameActivity;
import chum.engine.GameNode;
import chum.gl.RenderNode;

import android.os.Bundle;


/**
   This example uses a basic GameActivity that just iterates as fast as
   possible (drawing nothing), and periodically displays the FPS in the
   title bar.
   

   The periodic FPS is done using a Runnable posted to the handler
   set up by the base GameActivity for just that purpose.  The
   actual FPS calculation is also handled by the base GameActivity.

   Note that the FPS usually has an upper bound of 60Hz - the screen
   refresh rate.
*/
public class FastestPossibleFPS extends GameActivity
{
    private Runnable runFPS;

    /** Keep track of the original title string, so it can be updated (appended) */
    private CharSequence origTitle;

    @Override
    public void setViewOptions() {
    	this.hideTitlebar = false;
    	this.fullscreen = false;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        runFPS = new Runnable(){
                public void run() { showFPS(); }
            };

        origTitle = getTitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        gameController.uiHandler.post(runFPS);
    }

    private void showFPS() {
        setTitle(""+origTitle + " -- "+this.getFPS()+"fps");
        
        if ( !gameController.paused )
            gameController.uiHandler.postDelayed(runFPS, 3000);
    }
    
    
    protected GameNode createLogicTree() { return new GameNode(); }

    protected RenderNode createRenderTree(GameNode logic) { return new RenderNode(); }
}
