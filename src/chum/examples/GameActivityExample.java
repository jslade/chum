package chum.examples;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import chum.engine.GameActivity;
import chum.util.Log;



public class GameActivityExample extends GameActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        run_showFPS = new Runnable(){
                public void run() { showFPS(); }
            };
        showFPS();
    }

    private Runnable run_showFPS;


    private void showFPS() {
        Log.d("GameActivityExample FPS = %d", this.getFPS());
        mainHandler.postDelayed(run_showFPS, 3000);
    }
    
}
