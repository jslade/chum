package chum.gl;

import chum.gl.Font.Glyph;
import chum.gl.VertexAttributes.Usage;


import javax.microedition.khronos.opengles.GL10;


/**
   Special type of mesh meant for displaying text using texture-mapped fonts.

   The mesh is built as a set of triangles laid together, where
   every two triangles form a quad for displaying a single glyph.
   
   y|    a  b  c
    |  +--+--+--+
    |  |\ |\ |\ |
    |  | \| \| \|
    |  +--+--+--+
   -+-------------->
    |             x
   
   A triangle strip would be nice in terms of efficiency of the vertices,
   but it won't work because we have to specify two tex coords at each of the
   shared vertices.
*/
public class Text extends Mesh {

    /** The font being displayed */
    public final Font font;

    /** The maximum number of characters */
    public final int maxGlyphs;

    /** The raw vertex data, made accessible for updates */
    public int[] dynVertices;

    /** The raw index data, made accessible for updates */
    public short[] dynIndices;

    /**
       Construct the mesh to hold the given number of glyphs
    */
    public Text(Font font, int count) {
        super(true,true,
              
              // always fixed-point for now
              true, 
              
              // 4 vertices per glyph (two triangles)
              4 * count,

              // 6 indices per glyph (two triangles)
              6 * count,

              // Only supports position and texture at present.
              // would it be helpful to specify per-vertex colors?
              // e.g. for gradients, etc?
              new VertexAttribute(Usage.Position),
              new VertexAttribute(Usage.Texture));
        
        this.type = GL10.GL_TRIANGLES;
        this.font = font;
        this.maxGlyphs = count;

        dynVertices = new int[maxVertices * 5]; // 3 + 2 per vertex
        dynIndices = new short[maxIndices];
    }


    /**
       Populate the mesh with vertices corresponding to the given set of glyphs
    */
    public void setGlyphs(Glyph[] glyphs) {
        setGlyphs(glyphs,0,glyphs.length);
    }


    /**
       Populate the mesh with vertices corresponding to the given set of glyphs
    */
    public void setGlyphs(Glyph[] glyphs, int offset, int count) {
        if ( count > maxGlyphs )
            throw new IllegalArgumentException("count exceeds maxGlyphs");

        int x1 = 0;
        int y1 = 0;
        int v = 0;
        int i = 0;
        short vert = 0;

        for (int g=0; g < count; ++g ) {
            Glyph glyph = glyphs[offset + g];
            int x2 = x1 + glyph.width;
            int y2 = y1 + glyph.height;

            int u1 = glyph.texU;
            int v1 = glyph.texV;
            int u2 = u1 + glyph.texWidth;
            int v2 = v1 + glyph.texHeight;

            // lower left
            dynVertices[v++] = x1;
            dynVertices[v++] = y1;
            dynVertices[v++] = 0;
            dynVertices[v++] = u1;
            dynVertices[v++] = v1;
            short ll = vert++;

            // lower right;
            dynVertices[v++] = x2;
            dynVertices[v++] = y1;
            dynVertices[v++] = 0;
            dynVertices[v++] = u2;
            dynVertices[v++] = v1;
            short lr = vert++;
            
            // top right;
            dynVertices[v++] = x2;
            dynVertices[v++] = y2;
            dynVertices[v++] = 0;
            dynVertices[v++] = u2;
            dynVertices[v++] = v2;
            short ur = vert++;
            
            // top left;
            dynVertices[v++] = x1;
            dynVertices[v++] = y2;
            dynVertices[v++] = 0;
            dynVertices[v++] = u2;
            dynVertices[v++] = v2;
            short ul = vert++;

            x1 = x2;

            // Now the two triangles
            dynIndices[i++] = ll;
            dynIndices[i++] = lr;
            dynIndices[i++] = ur;
            dynIndices[i++] = ll;
            dynIndices[i++] = ur;
            dynIndices[i++] = ul;
        }

        this.setVertices(dynVertices,0,v);
        this.setIndices(dynIndices,0,i);
    }
              
              
}


