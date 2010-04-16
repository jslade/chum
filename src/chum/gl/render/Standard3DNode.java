package chum.gl.render;

import chum.gl.RenderContext;
import chum.util.Log;

import javax.microedition.khronos.opengles.GL10;


/**
   A RenderNode appropriate for use as the base of the render tree that sets up a standard
   3D environment.

   * Enables depth test
   * Enables face culling, using CCW direction as the front
*/
public class Standard3DNode extends PerspectiveProjection {

    public boolean enableTexture2D = false;


    public Standard3DNode() {
        super();

    }


    /**
       When the surface is being setup, initialize the standard
       3D settings to control the depth buffer, culling, etc
    */
    @Override
    public void onSurfaceCreated(RenderContext renderContext) {
        super.onSurfaceCreated(renderContext);

        GL10 gl = renderContext.gl10;

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glClearDepthf(1f);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glCullFace(GL10.GL_BACK);
        
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        
        if ( enableTexture2D )
            gl.glEnable(GL10.GL_TEXTURE_2D);
    }
}

