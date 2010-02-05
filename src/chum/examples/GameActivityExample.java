package chum.examples;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import chum.engine.GameActivity;
import chum.util.Log;


/**
   This example uses a basic GameActivity that just iterates as fast as
   possible (drawing nothing), and periodically displays the FPS
   to the log.

   The periodic FPS is done using a Runnable posted to the handler
   set up by the base GameActivity for just that purpose.  The
   actual FPS calculation is also handled by the base GameActivity.
*/
public class GameActivityExample extends GameActivity
{
    private Runnable runFPS;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        runFPS = new Runnable(){
                public void run() { showFPS(); }
            };
    }

    @Override
    public void onResume() {
        super.onResume();
        mainHandler.post(runFPS);
    }

    private void showFPS() {
        Log.d("GameActivityExample FPS = %d", this.getFPS());
        if ( !paused ) mainHandler.postDelayed(runFPS, 3000);
    }
    
}
