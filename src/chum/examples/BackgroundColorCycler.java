package chum.examples;

import chum.engine.*;
import chum.fp.FP;
import chum.gl.GLColor;
import chum.gl.RenderNode;
import chum.input.TouchInputNode;
import chum.gl.render.ClearNode;
import chum.gl.render.OrthographicProjection;
import chum.util.Log;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;



/**
*/
public class BackgroundColorCycler extends GameActivity
{
    private GLColor bg = new GLColor("#ffffff");


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected GameTree createGameTree() {
        return (new GameTree(this) {
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
