package chum.gl;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;


/**
   Manages a given texture (or multiple textures).

   Supports loading textures from resources, or from arbitrary bitmaps.
   Dimensions of Texture images should generally be powers of 2 in both
   directions, though that is not strictly required by all hardware.
*/
public class Texture {

    /** The RenderContext */
    public RenderContext renderContext;
    
    /** The ImageProvider to (re)load the image as needed */
    public ImageProvider[] provider;
    
    private final int[] tex_ids;
    private final int tex_dim;

    /** The minimization filter */
    public int minFilter = GL10.GL_LINEAR;

    /** The magnification filter */
    public int magFilter = GL10.GL_LINEAR;

    /** The texture environment */
    public int texEnv = GL10.GL_MODULATE;

    
    /**
       Create a Texture for managing a single standard 2D texture image
    */
    public Texture(RenderContext renderContext) {
        this(renderContext,1,GL10.GL_TEXTURE_2D);
    }


    /**
       Create a Texture for managing a number of standard 2D texture images.
    */
    public Texture(RenderContext renderContext,int num_tex) {
        this(renderContext,num_tex,GL10.GL_TEXTURE_2D);
    }

        
    /**
       Create a Texture for managing a number of texture images
    */
    public Texture(RenderContext renderContext,int num_tex,int tex_dim) {
    	this.renderContext = renderContext;
        this.tex_dim = tex_dim;

        tex_ids = new int[num_tex];
        provider = new ImageProvider[num_tex];
    }


    /**
       Set the ImageProvider to be used for the texture image. 
    */
    public void setProvider(ImageProvider provider) {
        setProvider(0,provider);
    }


    /**
       Set the ImageProvider to be used for the texture image
    */
    public void setProvider(int num, ImageProvider ip) {
        this.provider[num] = ip;
    }


    /**
       Initialize the texture prior to using it for rendering.  This allocates
       a texture handle for the texture on the GPU.

       This will typically be called from a TextureNode or MeshNode.
    */
    public void onSurfaceCreated(RenderContext renderContext) {
    	this.renderContext = renderContext;
    	forceReload();
    }
        

    /**
    */
    public void onSurfaceChanged(int width, int height) {
        load(renderContext.gl10);
    }


    /**
     */
    public void forceReload() {
        for(int i=0; i<tex_ids.length; ++i)
            tex_ids[i] = 0; // force redefined...
        define(renderContext.gl10);
    }
        

    /**
       Define the texture
    */
    protected void define(GL10 gl) {
        if ( !isDefined() ) {
            IntBuffer tex_ids_buf = IntBuffer.wrap(tex_ids);
            gl.glGenTextures(tex_ids.length,tex_ids_buf);
            //chum.util.Log.d("Texture %s: define res=%d tex=%d",
            //this, res_ids[0], tex_ids[0]);
        }
    }


    /**
       @return true if the texture has been defined on the GPU
    */
    public boolean isDefined() {
        return tex_ids[0] > 0;
    }


    public void load(GL10 gl) {
        for(int i=0,n=tex_ids.length; i<n; ++i)
            load(gl,i);
    }
    
    
    /**
       Load the texture image data and filters onto the GPU
    */
    public void load(GL10 gl, int num) {
        define(gl);
        Bitmap bitmap = getBitmap(num);
        if ( bitmap == null ) return;
        
        Bitmap pot_bitmap = ensurePOT(bitmap);

        gl.glBindTexture(tex_dim, tex_ids[num]);

        gl.glTexParameterx(tex_dim, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
        gl.glTexParameterx(tex_dim, GL10.GL_TEXTURE_MAG_FILTER, magFilter);

        GLUtils.texImage2D(tex_dim, 0, pot_bitmap, 0);
        
        if (pot_bitmap != bitmap) pot_bitmap.recycle();
        recycleBitmap(bitmap, num);
    }

    
    protected Bitmap getBitmap(int num) {
        if ( provider[num] == null ) return null;
        return provider[num].getBitmap(renderContext);
    }
    
    
    protected void recycleBitmap(Bitmap bitmap, int num) {
        provider[num].recycleBitmap(bitmap);
    }
    
    
    /**
       Ensure the bitmap to be used for the texture mapping has
       power-of-two dimensions, if required for this render context.
       If it is not, generates a scaled version.
     */
    protected Bitmap ensurePOT(Bitmap bmp) {
    	double width_ln2 = Math.log(bmp.getWidth())/Math.log(2.0);
    	double height_ln2 = Math.log(bmp.getHeight())/Math.log(2.0);
    	
    	if ( renderContext.allowNPOT ||
    		 (width_ln2 - Math.floor(width_ln2) == 0 &&
    		  height_ln2 - Math.floor(height_ln2) == 0) ) {
    		return bmp; // usable as is
    	}

    	// width or height isn't a power of two
    	int scaledWidth = (int) Math.pow(2,Math.ceil(width_ln2));
    	int scaledHeight = (int) Math.pow(2,Math.ceil(height_ln2));
    	chum.util.Log.w("Creating POT scaled bitmap (%dx%d) from non-POT bitmap (%dx%d)",
    					scaledWidth, scaledHeight, bmp.getWidth(), bmp.getHeight()); 
    	return Bitmap.createScaledBitmap(bmp, scaledWidth, scaledHeight, false);
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
    
    
    
    public static interface ImageProvider {
        public Bitmap getBitmap(RenderContext renderContext);
        public void recycleBitmap(Bitmap bitmap);
    }


    public static class StaticProvider implements ImageProvider {
        public Bitmap bitmap;
        public StaticProvider(Bitmap bmp) { this.bitmap = bmp; }
        public Bitmap getBitmap(RenderContext renderContext) { return bitmap; }
        public void recycleBitmap(Bitmap b) {}
    }
    
    
    public static class ResourceProvider implements ImageProvider {
        public int res_id;
        public Bitmap bitmap;
        
        public ResourceProvider(int id) {
            this.res_id = id;
        }

        public Bitmap getBitmap(RenderContext renderContext) {
            if ( bitmap == null ) {
                Resources res = renderContext.appContext.getResources();
                bitmap = BitmapFactory.decodeResource(res,res_id);
            }
            return bitmap;
        }
        
        public void recycleBitmap(Bitmap b) {
            if ( bitmap != null ) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }
    
    public static class AssetProvider implements ImageProvider {
        public String asset;
        public Bitmap bitmap;
        
        public AssetProvider(String asset) {
            this.asset = asset;
        }

        public Bitmap getBitmap(RenderContext renderContext) {
            if ( bitmap == null ) {
                InputStream is;
                try {
                    chum.util.Log.d("AssetProvider: loading %s", asset);
                    is = renderContext.appContext.getAssets()
                        .open(asset,AssetManager.ACCESS_STREAMING);
                }
                catch (IOException e) {
                    chum.util.Log.w("AssetProvider: can't load %s: %s", asset, e);
                    return null;
                }

                try {
                    bitmap = BitmapFactory.decodeStream(is);
                    chum.util.Log.d("AssetProvider: loaded %s", asset);
                }
                finally {
                    try { is.close(); }
                    catch (IOException e) {}
                }
            }
            return bitmap;
        }
        
        public void recycleBitmap(Bitmap b) {
            if ( bitmap != null ) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }
    
}   