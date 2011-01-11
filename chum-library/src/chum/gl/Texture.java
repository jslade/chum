package chum.gl;

import chum.gl.render.primitive.RenderPrimitive;

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

    /** The minimization filter */
    public int minFilter = GL10.GL_LINEAR;

    /** The magnification filter */
    public int magFilter = GL10.GL_LINEAR;

    /** The texture environment */
    public int texEnv = GL10.GL_MODULATE;

    /** The ImageProvider to (re)load the image as needed */
    public ImageProvider[] provider;
    
    /** The GPU-assigned texture ID(s) */
    protected final int[] tex_ids;
        
    protected final int tex_dim;

    /** The render primitives for allocating the texture in the GPU */
    protected final Allocate allocatePrim;

    /** The render primitives for loading the texture in the GPU */
    protected final Load[] loadPrim;
    
    /** The render primitives to bind the texture */
    protected final Bind[] bindPrim;

    protected final Unbind unbindPrim;
    
    
    
    /**
       Create a Texture for managing a single standard 2D texture image
    */
    public Texture() {
        this(1,GL10.GL_TEXTURE_2D);
    }


    /**
       Create a Texture for managing a number of standard 2D texture images.
    */
    public Texture(int num_tex) {
        this(num_tex,GL10.GL_TEXTURE_2D);
    }

        
    /**
       Create a Texture for managing a number of texture images
    */
    public Texture(int num_tex,int tex_dim) {
        this.tex_dim = tex_dim;
        tex_ids = new int[num_tex];
        provider = new ImageProvider[num_tex];

        allocatePrim = new Allocate();
        loadPrim = new Load[num_tex];
        bindPrim = new Bind[num_tex];
        unbindPrim = new Unbind();
        
        for( int i=0; i<num_tex; ++i ) {
            loadPrim[i] = new Load(i);
            bindPrim[i] = new Bind(i);
        }
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
        @return true if the texture has been defined on the GPU
     */
    public boolean isAllocated() {
        return tex_ids[0] > 0;
    }

    
    /**
       Allocate a new texture id
     */
    public void allocate(RenderContext renderContext) {
        if ( !isAllocated() )
            renderContext.add(allocatePrim);
    }


    /**
     */
    public void forceReload(RenderContext renderContext) {
        for(int i=0; i<tex_ids.length; ++i)
            tex_ids[i] = 0; // force re-allocated...
        load(renderContext);
    }
        

    public void load(RenderContext renderContext) {
        allocate(renderContext);
        for(int i=0,n=tex_ids.length; i<n; ++i)
            renderContext.add(loadPrim[i]);
    }
    
    
    /**
       Bind the texture, preparing it to be applied to following rendering
     */
    public void bind(RenderContext renderContext) {
        bind(renderContext,0);
    }


    /**
        Bind the texture, preparing it to be applied to following rendering
     */
    public void bind(RenderContext renderContext, int num) {
        renderContext.add(bindPrim[num]);
    }


    /**
       Unbind the texture
     */
    public void unbind(RenderContext renderContext) {
        unbind(renderContext,0);
    }


    /**
        Unbind the texture, so it will no longer be applied
     */
    public void unbind(RenderContext renderContext, int num) {
        renderContext.add(unbindPrim);
    }
 
 
    protected Bitmap getBitmap(RenderContext renderContext,int num) {
        if ( provider[num] == null ) return null;
        return provider[num].getBitmap(renderContext);
    }
    
    
    protected void recycleBitmap(Bitmap bitmap, int num) {
        provider[num].recycleBitmap(bitmap);
    }
    

    /**
       Allocate the texture -- this just allocates a texture id in
       the GPU for future reference.
     */
    protected class Allocate extends RenderPrimitive {
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            IntBuffer tex_ids_buf = IntBuffer.wrap(tex_ids);
            gl.glGenTextures(tex_ids.length,tex_ids_buf);
            chum.util.Log.d("Texture %s: define tex=%d", this, tex_ids[0]);
            for(int i=0; i<tex_ids.length;++i){
                if (tex_ids[i] == 0) {
                    chum.util.Log.e("Failed to generate tex id for %s [%d]:",this,i);
                    chum.util.Log.e("glError: %d",gl.glGetError());
                }
            }
        }
    }


    /**
       Load the texture image data and filters onto the GPU
    */
    protected class Load extends RenderPrimitive {
        
        int index = 0;
        
        public Load(int index) {
            super();
            this.index = index;
        }
        
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            Bitmap bitmap = getBitmap(renderContext,index);
            if ( bitmap == null ) return;
        
            bindPrim[index].render(renderContext,gl); //gl.glBindTexture(tex_dim, tex_ids[index]);

            gl.glTexParameterx(tex_dim, GL10.GL_TEXTURE_MIN_FILTER, minFilter);
            gl.glTexParameterx(tex_dim, GL10.GL_TEXTURE_MAG_FILTER, magFilter);

            Bitmap pot_bitmap = ensurePOT(renderContext,bitmap);
            GLUtils.texImage2D(tex_dim, 0, pot_bitmap, 0);
            if (pot_bitmap != bitmap) pot_bitmap.recycle();
            recycleBitmap(bitmap, index);
        }
        
        /**
            Ensure the bitmap to be used for the texture mapping has
            power-of-two dimensions, if required for this render context.
            If it is not, generates a scaled version.
         */
        protected Bitmap ensurePOT(RenderContext renderContext, Bitmap bmp) {
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

    }

    
    protected class Bind extends RenderPrimitive {
        int index = 0;

        public Bind(int index) {
            super();
            this.index = index;
        }
        
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            gl.glBindTexture(tex_dim, tex_ids[index]);
        }
    }

    
    protected class Unbind extends RenderPrimitive {
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            gl.glBindTexture(tex_dim, 0);
        }
    }

    
    /**
     * ImageProvider loads the image data for a texture on demand
     * @author jeremy
     *
     */
    
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
                if ( asset == null || asset.equals("") ) return null;
                
                InputStream is;
                try {
                    //chum.util.Log.d("AssetProvider: loading %s", asset);
                    is = renderContext.appContext.getAssets()
                        .open(asset,AssetManager.ACCESS_STREAMING);
                }
                catch (IOException e) {
                    chum.util.Log.w("AssetProvider: can't load %s: %s", asset, e);
                    return null;
                }

                try {
                    bitmap = BitmapFactory.decodeStream(is);
                    //chum.util.Log.d("AssetProvider: loaded %s", asset);
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