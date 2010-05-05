package chum.gl;

import chum.gl.VertexAttributes.Usage;
import chum.util.Log;

import javax.microedition.khronos.opengles.GL10;


/**
   Special type of mesh meant for displaying a bunch of static sprites.

   The mesh is built as a set of quads, with each given an arbitrary
   location in 2D space.  {@link SpriteBatchBuilder} is provided
   for specifying the Sprites and their locations.

   All the sprites are drawn together as a single operation (single
   mesh).  This is much more efficient than drawing a bunch of
   individual {@link Sprite}s, in which each on has to be placed +
   texture bound + verts and indices bound.

   As with rendering individual {@link Sprite}s, this mesh should be rendered as
   a child of a {@link SpriteModeNode} to get optimal settings.
*/
public class SpriteBatch extends Mesh {

    /** The maximum number of sprites */
    public int maxSprites;

    /** The sprites being displayed */
    public SpriteSheet sheet;


    /**
       Construct the mesh to hold the given number of sprites
    */
    public SpriteBatch(SpriteSheet sheet, int count) {
        super(true,true,
              true, // always fixed-point for now
              4 * count, // 4 vertices per sprite (two triangles)
              6 * count, // 6 indices per sprite (two triangles)

              new VertexAttribute(Usage.Position),
              new VertexAttribute(Usage.Texture));

        this.type = GL10.GL_TRIANGLES;

        this.sheet = sheet;
        this.maxSprites = count;
    }



    @Override
    public Texture getTexture() {
        return sheet;
    }
              
}


