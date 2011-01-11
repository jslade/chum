package chum.examples;

import chum.engine.GameActivity;
import chum.engine.GameNode;
import chum.gl.Color;
import chum.gl.RenderNode;
import chum.gl.render.ClearNode;
import chum.input.TouchInputNode;

import android.os.Bundle;
import android.view.MotionEvent;



/**
   Simple demonstration of changing the background clear color, based on
   where the screen is touched.  Uses the TouchInputNode to handle touch
   events, and the ClearNode to set the background color.
*/
public class BackgroundColorTouch extends GameActivity
{
    /** The background color that gets changed */
    public Color bg;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected GameNode createLogicTree() {
        return new TouchInputNode(){
            @Override
            protected boolean onTouch(MotionEvent event) {
                float px = event.getX() / renderContext.width;
                float py = event.getY() / renderContext.height;

                bg.red = px;
                bg.green = py;
                bg.blue = (px + py)/2f;
                
                return true;
            }
        };
    }

    @Override
    protected RenderNode createRenderTree(GameNode logic) {
        ClearNode clearNode = new ClearNode(Color.BLACK);
        bg = clearNode.color;        
        return clearNode;
    }

}
