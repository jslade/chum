package chum.gl;

import chum.f.Vec3;
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
        setString(str,Anchor.SOUTHWEST);
    }


    /**
       Populate the text with a new string, provided it fits within the maxGlyphs
       limit.  Uses the default anchor point (SOUTHWEST)
    */
    public void setString(String str) {
        setString(str,Anchor.SOUTHWEST);
    }
    
    /**
        Populate the text with a new string, provided it fits within the maxGlyphs
        limit.
     */
    public void setString(String str,Anchor anchor) {
        if ( font == null )
            throw new IllegalStateException("Can't call setString() without a font defined");
        if ( str.length() > maxGlyphs ) {
            //throw new IllegalArgumentException("string length exceeds maxGlyphs");
            chum.util.Log.d("string length exceeds maxGlyphs, truncating text (%d > %d)", str.length(), maxGlyphs);
            str = str.substring(0,maxGlyphs);
        }            

        if ( reusableGlyphs == null )
            reusableGlyphs = new Glyph[maxGlyphs];
        font.getGlyphs(str,reusableGlyphs);
        setGlyphs(reusableGlyphs,0,str.length(),anchor);
    }

    private Glyph[] reusableGlyphs;

 

    /**
       Populate the mesh with vertices corresponding to the given set of glyphs
    */
    public void setGlyphs(Glyph[] glyphs,Anchor anchor) {
        setGlyphs(glyphs,0,glyphs.length,anchor);
    }


    /**
       Populate the mesh with vertices corresponding to the given set of glyphs.

       todo: perhaps this would be cleaner / clearer if done with a MeshBuilder?
    */
    public void setGlyphs(Glyph[] glyphs, int offset, int count, Anchor anchor) {
        if ( count > maxGlyphs )
            throw new IllegalArgumentException("count exceeds maxGlyphs");

        // Count the number of lines ahead of time
        int numLines = 1;
        float maxHeight = 0; // height of tallest glyph from starting point
        float maxBase = 0; // largest descent of glyph below starting point
        for(int i=offset, j=offset+count; i<j; ++i) {
            Glyph glyph = glyphs[i];
            if ( glyph.ch == '\n' ) numLines++;
            if ( glyph.height > maxHeight ) maxHeight = glyph.height;
            if ( glyph.baseline > maxBase ) maxBase = glyph.baseline;
        }
        
        float lineHeight = maxHeight + maxBase; 
        float lineSpace = 0.1f * lineHeight; // todo: should be configurable?
        float lineOffset = lineHeight + lineSpace;
        float totalHeight = maxHeight; // total height doesn't actually include descent of bottom line
        if ( numLines > 1 ) totalHeight += (numLines - 1) * lineOffset;

        float x1 = 0;
        float y0 = totalHeight - maxHeight;
        int v = 0;
        int i = 0;
        short vert = 0;
        float maxWidth = 0;
        
        for (int g=0; g < count; ++g ) {
            Glyph glyph = glyphs[offset + g];
            if ( glyph.ch == '\n' ) {
                y0 -= lineOffset;
                x1 = 0;
                continue;
            }
            
            float y1 = y0 - glyph.baseline;
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
            if ( x1 > maxWidth ) maxWidth = x1;

            // Now the two triangles
            dynIndices[i++] = ll;
            dynIndices[i++] = lr;
            dynIndices[i++] = ur;
            dynIndices[i++] = ll;
            dynIndices[i++] = ur;
            dynIndices[i++] = ul;
        }

        maxWidth -= spacing;

        // Adjust vertices based on anchor point
        // default anchor point is SOUTHWEST
        delta.x = 0;
        delta.y = 0;
        switch(anchor) {
        case NORTHWEST: delta.y = -totalHeight; break;
        case WEST: delta.y = -0.5f*totalHeight; break;
        case SOUTH: delta.x = -0.5f*maxWidth; break;
        case CENTER: delta.x = -0.5f*maxWidth; delta.y = -0.5f*totalHeight; break;
        case NORTH: delta.x = -0.5f*maxWidth; delta.y = -totalHeight; break;            
        case SOUTHEAST: delta.x = -maxWidth; break;
        case EAST: delta.x = -maxWidth; delta.y = -0.5f*totalHeight; break;
        case NORTHEAST: delta.x = -maxWidth; delta.y = -totalHeight; break;            
        }
        if ( delta.y != 0 || delta.x != 0 ) {
            for(int j=0; j<v;j += 3) {
                dynVertices[j++] += delta.x;
                dynVertices[j++] += delta.y;
            }
        }   
        
        // Need to zero out the rest of the vertex and index data, because some devices
        // apparently don't use the count/offset properly when rendering array elements
        // TODO: Could unroll these loops for speed
        short vert0 = vert;
        for (int g=count; g<maxGlyphs; ++g){
            for (int gv=0; gv<20; ++gv) { // 20 = 4 verts, 5 floats per vert
                dynVertices[v++] = 0;
            }
            vert += 4;
            
            for (int gi=0; gi<6; ++gi) { // 6 = 6 indices per glyph quad
                dynIndices[i++] = vert0;
            }
        }
        
        this.setVertices(dynVertices,0,v);
        this.setIndices(dynIndices,0,i);
    }
              
    private final Vec3 delta = new Vec3();


    @Override
    public Texture getTexture() {
        if ( font != null ) return font.texture;
        else return null;
    }

}


