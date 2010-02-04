package chum.examples;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import chum.engine.GameActivity;
import chum.util.Log;



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
