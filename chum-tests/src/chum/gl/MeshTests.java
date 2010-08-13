package chum.gl;

import android.test.AndroidTestCase;


/**
 */
public class MeshTests extends AndroidTestCase {

    protected void setUp() {

    }

    public void testCreateQuad() {
        Mesh quad = createQuadMesh();
        assertEquals(false,quad.usesFixedPoint());
        assertEquals("should have 4 vertices", 4, quad.getNumVertices());
        assertEquals("should have 4 indices", 4, quad.getNumIndices());

        assertEquals(1,quad.attributes.size());
        assertEquals(4*3,quad.getVertexSize());


        Mesh.Bounds bounds = Mesh.Bounds.obtain();
        bounds.update(quad);
        assertEquals("[0.000,0.000,-1.000]",bounds.minimum.toString());
        assertEquals("[1.000,1.000,1.000]",bounds.maximum.toString());
        assertEquals("[0.500,0.500,0.000]",bounds.center.toString());
        assertEquals("[1.000,1.000,2.000]",bounds.size.toString());
    }

    private Mesh createQuadMesh() {
        Mesh quad = new Mesh(true, true, false, 4, 4,
                             new VertexAttribute(VertexAttributes.Usage.Position));

        float[] verts = {
            0.0f, 0.0f, -1.0f,  // lower left
            1.0f, 0.0f, -1.0f,  // lower right
            1.0f, 1.0f, 1.0f,  // upper right
            0.0f, 1.0f, 1.0f,  // upper left 
        };
        quad.setVertices(verts);

        short[] indices = { 0, 1, 2, 3 };
        quad.setIndices(indices);
        return quad;
    }


    public void testFillMesh_GL10() {
        Mesh quad = createQuadMesh();
        assertEquals(true,quad.dirty);

        MockGL10 gl10 = new MockGL10();
        MockRenderContext gl10Context = new MockRenderContext(getContext(),gl10);
        assertEquals(false,gl10Context.isGL11);
        assertNull(gl10Context.gl11);

        quad.onSurfaceCreated(gl10Context);
        assertEquals(0,gl10.numCommands());
        assertEquals(false,quad.dirty);
    }


    public void testFillMesh_GL11() {
        Mesh quad = createQuadMesh();
        assertEquals(true,quad.dirty);

        MockGL11 gl11 = new MockGL11();
        MockRenderContext gl11Context = new MockRenderContext(getContext(),gl11);
        assertEquals(true,gl11Context.isGL11);
        assertNotNull(gl11Context.gl11);

        quad.onSurfaceCreated(gl11Context);
        assertEquals(false,quad.dirty);
        assertEquals(8,gl11.numCommands());
        assert(gl11.contains("glGenBuffers(1)",   // VBO
                             "glGenBuffers(1)",   // IBO

                             "glBindBuffer",      // Fill VBO
                             "glBufferData",
                             "glBindBuffer",

                             "glBindBuffer",      // Fill IBO
                             "glBufferData",
                             "glBindBuffer")
               );

        assertEquals(1,quad.vertexBufferObjectHandle);
        assertEquals(2,quad.indexBufferObjectHandle);
    }


        
}
