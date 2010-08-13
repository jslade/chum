package chum.examples;

import chum.engine.*;
import chum.gl.Color;
import chum.gl.RenderNode;
import chum.input.TouchInputNode;
import chum.gl.render.ClearNode;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;



/**
   Simple demonstration of changing the background clear color, based on
   where the screen is touched.  Uses the TouchInputNode to handle touch
   events, and the ClearNode to set the background color.
*/
public class BackgroundColorTouch extends GameActivity
{
    private Color bg = new Color("#ffffff");


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected GameTree createGameTree() {
        return (new GameTree() {
                protected GameNode createLogicTree() {
                    return new TouchInputNode(){
                            protected void handle(View v, MotionEvent event) {
                                float px = event.getX() / v.getWidth();
                                float py = event.getY() / v.getHeight();

                                bg.red = px;
                                bg.green = py;
                                bg.blue = (px + py)/2f;
                            }
                        };
                }

                protected RenderNode createRenderTree() {
                    return new ClearNode(bg);
                }
            });
    }


}
