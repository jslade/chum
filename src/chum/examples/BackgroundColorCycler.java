package chum.examples;

import chum.engine.*;
import chum.gl.GLColor;
import chum.gl.RenderNode;
import chum.gl.render.ClearNode;
import chum.gl.render.OrthographicProjection;
import chum.util.Log;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;



/**
*/
public class BackgroundColorCycler extends GameActivity
{
    private GLColor current_bg = new GLColor("#ffffff");


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setGameTree(new GameTree(this){
                protected GameNode createLogicTree() {
                    return null;
                }


                protected RenderNode createRenderTree() {
                    RenderNode rnode = new RenderNode();
                    rnode.addNode(new ClearNode(current_bg));
                    rnode.addNode(new OrthographicProjection());
                    return rnode;
                }
            });
    }


}
