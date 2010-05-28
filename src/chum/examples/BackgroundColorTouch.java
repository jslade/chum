package chum.examples;

import chum.engine.*;
import chum.fp.FP;
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

                                bg.red = (int)(FP.ONE * px);
                                bg.green = (int)(FP.ONE * py);
                                bg.blue = (int)(FP.ONE * (px + py)/2.0);
                            }
                        };
                }

                protected RenderNode createRenderTree() {
                    return new ClearNode(bg);
                }
            });
    }


}
