package chum.gl.render.primitive;

import chum.gl.RenderContext;

import javax.microedition.khronos.opengles.GL10;


public class Blend {
    
    public static class Enable extends RenderPrimitive {
        @Override
        public void render(RenderContext context, GL10 gl) {
            gl.glEnable(GL10.GL_BLEND);
        }
    }

    public static class Disable extends RenderPrimitive {
        @Override
        public void render(RenderContext context, GL10 gl) {
            gl.glDisable(GL10.GL_BLEND);
        }
    }
    
}
