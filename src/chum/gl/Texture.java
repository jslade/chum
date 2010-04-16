package chum.gl;

import chum.util.Log;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.nio.IntBuffer;
import javax.microedition.khronos.opengles.GL10;


/**
   Manages a given texture (or multiple textures).

   Supports loading textures from resources, or from arbitrary bitmaps.
   Dimensions of Texture images should generally be powers of 2 in both
   directions, though that is not strictly required by all hardware.
*/
public class Texture {

    private boolean initialized;
    private int[] tex_ids;
    private int[] res_ids;
    private int tex_dim;


    /** The minimization filter */
    public int minFilter = GL10.GL_LINEAR;

    /** The magnification filter */
    public int magFilter = GL10.GL_LINEAR;



    /**
       Create a Texture for managing a single standard 2D texture image
    */
    public Texture() {
        init(1,GL10.GL_TEXTURE_2D);
    }


    /**
       Create a Texture for managing a number of standard 2D texture images.
    */
    public Texture(int num_tex) {
        init(num_tex,GL10.GL_TEXTURE_2D);
    }

        
    /**
       Create a Texture for managing a number of texture images
    */
    public Texture(int tex_dim,int num_tex) {
        init(num_tex,tex_dim);
    }


    protected void init(int num_tex,int tex_dim) {
        this.tex_dim = tex_dim;

        tex_ids = new int[num_tex];
        res_ids = new int[num_tex];
    }


    /**
       Set the Resource id to be used for the Texture.  When this is done beforehand,
       the init() method will automatically load the texture and prepare it to
       be bound.
    */
    public void setResource(int res_id) {
        setResource(0,res_id);
    }


    /**
       Set the Resource id to be used for the Texture.  When this is done beforehand,
       the init() method will automatically load the texture and prepare it to
       be bound.
    */
    public void setResource(int num,int res_id) {
        res_ids[num] = res_id;
    }



    /**
       Initialize the texture prior to using it for rendering.  This allocates
       a texture handle for the texture on the GPU.

       This will typically be called from a TextureNode or MeshNode.
    */
    public void onSurfaceCreated(RenderContext renderContext) {
        IntBuffer tex_ids_buf = IntBuffer.wrap(tex_ids);
        renderContext.gl10.glGenTextures(tex_ids.length,tex_ids_buf);

        for(int num=0; num<res_ids.length; ++num) {
            int res_id = res_ids[num];
            if ( res_id > 0 )
                load(renderContext.gl10,num, res_id,renderContext.appContext);
        }
    }
        

    /**
       Load the texture from a specific Resource
    */
    public void load(GL10 gl, int res_id, Context context) {
        load(gl, 0, res_id, context);
    }


    /**
       Load the texture from a specific resource.

       The resource is loaded as a Bitmap, then loaded into the
       texture object on the GPU, then the bitmap is recycled, as
       it is no longer needed.
    */
    public void load(GL10 gl, int num, int res_id, Context context) {
        Resources res = context.getResources();
        Bitmap bmp = BitmapFactory.decodeResource(res,res_id);
        load(gl, num, bmp);
        bmp.recycle();
    }

    
    /**
       Load the texture from the given Bitmap
    */
    public void load(GL10 gl, Bitmap bmp) {
        load(gl, 0, bmp);
    }


    /**
       Load the texture image data and filters onto the GPU
    */
    void load(GL10 gl, int num, Bitmap bmp) {
        gl.glBindTexture(tex_dim, tex_ids[num]);

        gl.glTexParameterx(tex_dim, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameterx(tex_dim, GL10.GL_TEXTURE_MAG_FILTER, magFilter);

        GLUtils.texImage2D(tex_dim, 0, bmp, 0);
    }


    /**
       Bind the texture, preparing it to be applied to following rendering
    */
    public void bind(GL10 gl) {
        bind(gl,0);
    }


    /**
       Bind the texture, preparing it to be applied to following rendering
    */
    public void bind(GL10 gl, int num) {
        gl.glBindTexture(tex_dim, tex_ids[num]);
    }


    /**
       Unbind the texture
    */
    public void unbind(GL10 gl) {
        unbind(gl,0);
    }


    /**
       Unbind the texture, so it will no longer be applied
    */
    public void unbind(GL10 gl, int num) {
        gl.glBindTexture(tex_dim, 0);
    }

}   