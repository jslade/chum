package chum.examples;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import chum.engine.GameActivity;


/**
   This example demonstrates the default exception handling built into
   the GameActivity class.  It does nothing until a button is pressed
   which causes a NullPointerException.
*/
public class ForceCloseExample extends GameActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.force_close_example);

        Button force_close = (Button)findViewById(R.id.force_close);
        force_close.setOnClickListener( new View.OnClickListener() {
                public void onClick(View view) {
                    doForceClose();
                }
            });
    }


    public void doForceClose() {
    	// This causes an intentional "Force Close"
    	// by generating a NullPointerException
        Button button = null;
        button.getId();
    }

}
