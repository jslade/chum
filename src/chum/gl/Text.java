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

    /** The maximum number of characters */
    public int maxGlyphs;

    /** The font being displayed */
    public Font font;

    /** The spacing between characters */
    public float spacing;

    /** The raw vertex data, made accessible for updates */
    public float[] dynVertices;

    /** The raw index data, made accessible for updates */
    public short[] dynIndices;


    /**
       Construct the mesh to hold the given number of glyphs
    */
    public Text(int count) {
        this(count,1f);
    }


    /**
       Construct the mesh to hold the given number of glyphs,
       and the given spacing
    */
    public Text(int count, float spacing) {
        super(true,true,
              false, // always floating-point for now
              4 * count, // 4 vertices per glyph (two triangles)
              6 * count, // 6 indices per glyph (two triangles)

              // Only supports position and texture at present.
              // would it be helpful to specify per-vertex colors?
              // e.g. for gradients, etc?
              new VertexAttribute(Usage.Position),
              new VertexAttribute(Usage.Texture));

        this.type = GL10.GL_TRIANGLES;
        this.maxGlyphs = count;
        this.spacing = spacing;

        dynVertices = new float[maxVertices * 5]; // 3 + 2 per vertex
        dynIndices = new short[maxIndices];

        this.font = null;
    }



    /**
       Construct the mesh to hold the given string
    */
    public Text(String str) {
        this(str.length());
        setString(str);
    }


    /**
       Populate the text with a new string, provided it fits within the maxGlyphs
       limit
    */
    public void setString(String str) {
        if ( str.length() > maxGlyphs )
            throw new IllegalArgumentException("string length exceeds maxGlyphs");
        if ( font == null )
            throw new IllegalStateException("Can't call setString() without a font defined");

        if ( reusableGlyphs == null )
            reusableGlyphs = new Glyph[maxGlyphs];
        font.getGlyphs(str,reusableGlyphs);
        setGlyphs(reusableGlyphs,0,str.length());
    }

    private Glyph[] reusableGlyphs;

 

    /**
       Populate the mesh with vertices corresponding to the given set of glyphs
    */
    public void setGlyphs(Glyph[] glyphs) {
        setGlyphs(glyphs,0,glyphs.length);
    }


    /**
       Populate the mesh with vertices corresponding to the given set of glyphs.

       todo: perhaps this would be cleaner / clearer if done with a MeshBuilder?
    */
    public void setGlyphs(Glyph[] glyphs, int offset, int count) {
        if ( count > maxGlyphs )
            throw new IllegalArgumentException("count exceeds maxGlyphs");

        float x1 = 0;
        int v = 0;
        int i = 0;
        short vert = 0;

        for (int g=0; g < count; ++g ) {
            Glyph glyph = glyphs[offset + g];
            float y1 = (float)-glyph.baseline;
            float x2 = x1 + glyph.width;
            float y2 = y1 + glyph.height;

            float u1 = glyph.texU;
            float v1 = glyph.texV;
            float u2 = u1 + glyph.texWidth;
            float v2 = v1 - glyph.texHeight;

            // lower left
            dynVertices[v++] = x1;
            dynVertices[v++] = y1;
            dynVertices[v++] = 0;
            dynVertices[v++] = u1;
            dynVertices[v++] = v1;
            short ll = vert++;

            // lower right
            dynVertices[v++] = x2;
            dynVertices[v++] = y1;
            dynVertices[v++] = 0;
            dynVertices[v++] = u2;
            dynVertices[v++] = v1;
            short lr = vert++;
            
            // top right
            dynVertices[v++] = x2;
            dynVertices[v++] = y2;
            dynVertices[v++] = 0;
            dynVertices[v++] = u2;
            dynVertices[v++] = v2;
            short ur = vert++;
            
            // top left
            dynVertices[v++] = x1;
            dynVertices[v++] = y2;
            dynVertices[v++] = 0;
            dynVertices[v++] = u1;
            dynVertices[v++] = v2;
            short ul = vert++;

//             Log.d("Text glyph[%d] '%c' (%.3f,%.3f) (%.3f,%.3f) [%d,%d,%d,%d]",
//                   g, glyph.ch, x1, y1, x2, y2,
//                   ll,lr,ur,ul);
//             Log.d("    u,v = (%.3f,%.3f) (%.3f,%.3f)",
//                   u1, v1, u2, v2);

            x1 = x2 + spacing;

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
              

    @Override
    public Texture getTexture() {
        if ( font != null ) return font.texture;
        else return null;
    }

}


