package chum.gl.render;

import chum.util.Log;

import javax.microedition.khronos.opengles.GL10;


/**
   A RenderNode appropriate for use as the base of the render tree that sets up a standard
   3D environment.

   * Enables depth test
   * Enables face culling, using CCW direction as the front
*/
public class Standard3DNode extends PerspectiveProjection {

    public Standard3DNode() {
        super();

    }


    public void init(int width, int height, GL10 gl) {
        super.init(width,height,gl);

        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glClearDepthf(1f);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        
        gl.glEnable(GL10.GL_CULL_FACE);
        gl.glFrontFace(GL10.GL_CCW);
        gl.glCullFace(GL10.GL_BACK);
        
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }
}

