package chum.gl;

import android.content.Context;

import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;



/**
   RenderContext defines rendering configuration, performance options, etc.
*/
public class RenderContext {

    /** The GL10 rendering context */
    public GL10 gl10;
    
    /** EGLConfig the surface is using */
    public EGLConfig glConfig;

    /** The GL11 rendering context (if any) */
    public GL11 gl11;

    /** Whether GL11 is supported */
    public boolean isGL11 = false;


    /** The GL20 rendering context (if any) */
    //public GL20 gl20;

    /** Whether GL20 is supported */
    public boolean isGL20 = false;


    /** Whether VBOs are supported */
    public boolean canUseVBO = false;


    /** GLSurfaceView **/
    public GLSurfaceView glSurface;

    
    /** The width of the surface */
    public int width;

    /** The height of the surface */
    public int height;


    /** The app context (for loading resources, etc) */
    public Context appContext;


    /**
       Create a new RenderContext
    */
    public RenderContext(Context appContext,GL10 gl10,EGLConfig glConfig) {
        this.appContext = appContext;
        this.gl10 = gl10;
        this.glConfig = glConfig;

        if ( gl10 instanceof GL11 ) {
            this.gl11 = (GL11)gl10;
            this.isGL11 = true;
            
            // Assume VBO's can be used if this is OpenGL ES 1.1
            // This will later be refined by device-dependent settings.
            // todo: need a mechanism to override by device, and
            // user settings (?)
            this.canUseVBO = true;
        }
    }

}

