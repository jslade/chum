package chum.examples;

import chum.engine.*;
import chum.gl.GLColor;
import chum.gl.RenderNode;
import chum.gl.render.BackgroundColor;
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
                    return new BackgroundColor(current_bg);
                }
            });
    }

    /**
       Create the GLSurfaceView
    */
    protected GLSurfaceView createGLSurface() {
        GLSurfaceView glsv = super.createGLSurface();
        glsv.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR |
                           GLSurfaceView.DEBUG_LOG_GL_CALLS);
        return glsv;
    }



}
