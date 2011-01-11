package chum.gl.render.primitive;

import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


public class Matrix {
    
    public static class Push extends RenderPrimitive {
        @Override
        public void render(RenderContext context, GL10 gl) {
            gl.glPushMatrix();            
        }
    }

    public static class Pop extends RenderPrimitive {
        @Override
        public void render(RenderContext context, GL10 gl) {
            gl.glPopMatrix();            
        }
    }

}
