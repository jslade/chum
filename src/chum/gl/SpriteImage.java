package chum.gl;

import chum.gl.VertexAttributes.Usage;


import javax.microedition.khronos.opengles.GL10;


/**
   Sprites are special meshes designed specifically for 2D.  A sprite
   is just a quad (two triangles) together that reference a {link
   SpriteSheet}, which is a texture image containing the sprite images
   (e.g. all the frames of a character animation sequence).

   Sprites are generally drawn without lighting, and they're flat so no depth testing is
   needed.  As such, a {@link SpriteModeNode} is usually used to render all the sprites.
*/
public class Sprite extends Mesh {

    /** The SpriteImage */
    public SpriteSheet.ImageData image;

    /** The sprite location */
    public int x;
    
    /** The sprite location */
    public int y;

    /** The sprite width, in screen pixels */
    public int width;

    /** The sprite height, in screen pixels */
    public int height;


    /**
       Construct a Sprite instance.

       This is protected because Sprites should be allocated via Sprite.obtain(),
       allowing them to be reused from a pool
    */
    protected Sprite() {
        super(true,true,
              true, // always fixed-point for now
              4, // 4 vertices (two triangles)
              4, // 4 indices per glyph (two triangles as a strip)

              // Only uses Position and Texture, since Sprite is just
              // a portion of a SpriteSheet
              new VertexAttribute(Usage.Position),
              new VertexAttribute(Usage.Texture));

        this.type = GL10.GL_TRIANGLE_STRIP;
    }


    private static Sprite first_avail;
    private Sprite next_avail;
    private static Object sync = new Object();

    
    /**
       Obtain a Sprite instance from a pool
    */
    public static Sprite obtain() {
        synchronized(sync) {
            if ( first_avail == null )
                first_avail = new Sprite();
            Sprite sp = first_avail;
            first_avail = sp.next_avail;
            return sp;
        }
    }


    /**
       Return a Sprite instance to a pool
    */
    public void recycle() {
        synchronized(sync) {
            next_avail = first_avail;
            first_avail = this;
        }
    }


    /**
       Update the Sprite using the given ImageData
    */
    public void update(SpriteSheet.ImageData image) {
        update(image,0,0);
    }


    /**
       Update the Sprite using the given ImageData
    */
    public void update(SpriteSheet.ImageData image, int x, int y) {
        this.image = image;

        this.x = x;
        this.y = y;
        this.width = image.right - image.left;
        this.height = image.bottom - image.top;


        synchronized(verts_sync) {
            int v = 0;

            // lower left
            verts[v++] = 0;
            verts[v++] = 0;
            verts[v++] = 0;
            verts[v++] = image.u1;
            verts[v++] = image.v1;

            // lower right
            verts[v++] = width;
            verts[v++] = 0;
            verts[v++] = 0;
            verts[v++] = image.u2;
            verts[v++] = image.v1;
            
            // top right
            verts[v++] = width;
            verts[v++] = height;
            verts[v++] = 0;
            verts[v++] = image.u2;
            verts[v++] = image.v2;
            
            // top left
            verts[v++] = 0;
            verts[v++] = height;
            verts[v++] = 0;
            verts[v++] = image.u1;
            verts[v++] = image.v2;

            // Indices are set statically one time
        }
    }

    private Object verts_sync = new Object();
    private static int[] verts = new int[4 * (3+2)];
    private static short[] indices = { 0, 1, 2, 3 };

}


