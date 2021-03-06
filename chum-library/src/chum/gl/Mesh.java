package chum.gl;

import chum.f.M4;
import chum.f.Vec2;
import chum.f.Vec3;
import chum.f.Vec4;
import chum.fp.FP;
import chum.fp.Vec2FP;
import chum.fp.Vec3FP;
import chum.gl.VertexAttributes.Usage;
import chum.gl.render.primitive.RenderPrimitive;
import chum.util.Log;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


/**
 * A Mesh holds vertices composed of attributes specified by a
 * {@link VertexAttributes} instance. The vertices are held in either a
 * FloatBuffer or IntBuffer
 * 
 * Adapted from Mesh class from libgdx, originally written by mzechner
 * 
 * FIXME managed VBOs will leak two vbo handles per instance!
 */
public class Mesh {

    /** the vertex attributes */
    public VertexAttributes attributes;

    /** the maximum number of vertices */
    public int maxVertices;

    /** the maximum number of indices */
    public int maxIndices;

    /** the direct byte buffer that holds the vertices */
    public Buffer vertices;

    /** a view of the vertices buffer for manipulating floats */
    public FloatBuffer verticesFloat;

    /** a view of the vertices buffer for manipulating fixed point values */
    public IntBuffer verticesFixed;

    /** the direct short buffer that holds the indices */
    public ShortBuffer indices;

    /** the VBO handle */
    public int vertexBufferObjectHandle;

    /** the IBO handle */
    public int indexBufferObjectHandle;

    /** dirty flag - vertices */
    public boolean dirtyVertices = false;
    
    /** dirty flag - indices */
    public boolean dirtyIndices = false;
    
    /** managed? */
    public final boolean managed;

    /** static? */
    public final boolean isStatic;

    /** fixed point? */
    public final boolean useFixedPoint;

    /** The primitive type used to render this mesh */
    public int type;


    /** The render prim for allocating GPU buffers */
    protected Allocate allocater;
    
    /** The render prim for loading GPU buffers */
    protected LoadVertices vertexLoader;
    
    /** The render prim for loading GPU buffers */
    protected LoadIndices indexLoader;
    
    /** The render prim for disposing of GPU buffers */
    protected Dispose disposer;
    
    
    /**
     * Creates a new Mesh with the given attributes
     * 
     * @param graphics
     *            the graphics instance
     * @param managed
     *            whether this mesh should be managed or not.
     * @param useFixedPoint
     *            whether to use fixed point or floats
     * @param maxVertices
     *            the maximum number of vertices this mesh can hold
     * @param maxIndices
     *            the maximum number of indices this mesh can hold
     * @param attributes
     *            the {@link VertexAttribute}s.
     */
    public Mesh(boolean managed, boolean isStatic, boolean useFixedPoint,
            int maxVertices, int maxIndices, VertexAttribute... attributes) {
        this(managed, isStatic, useFixedPoint, maxVertices, maxIndices,
             new VertexAttributes(attributes));
    }


    /**
     * Creates a new Mesh with the given attributes
     * 
     * @param graphics
     *            the graphics instance
     * @param managed
     *            whether this mesh should be managed or not.
     * @param useFixedPoint
     *            whether to use fixed point or floats
     * @param maxVertices
     *            the maximum number of vertices this mesh can hold
     * @param maxIndices
     *            the maximum number of indices this mesh can hold
     * @param attributes
     *            the {@link VertexAttributes}.
     */
    public Mesh(boolean managed, boolean isStatic, boolean useFixedPoint,
            int maxVertices, int maxIndices, VertexAttributes attributes) {
        this.managed = managed;
        this.isStatic = isStatic;
        this.useFixedPoint = useFixedPoint;
        this.maxVertices = maxVertices;
        this.maxIndices = maxIndices;
        this.attributes = attributes;

        allocater = new Allocate();
        vertexLoader = new LoadVertices();
        indexLoader = new LoadIndices();
        disposer = new Dispose();
        
        createCPUBuffers();
    }


    /** Create buffers to hold the Mesh data on the CPU side */
    private void createCPUBuffers() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(maxVertices
                * this.attributes.vertexSize);
        buffer.order(ByteOrder.nativeOrder());
        vertices = buffer;
        verticesFixed = buffer.asIntBuffer();
        verticesFloat = buffer.asFloatBuffer();

        buffer = ByteBuffer.allocateDirect(maxIndices * 2);
        buffer.order(ByteOrder.nativeOrder());
        indices = buffer.asShortBuffer();
    }


    public void checkManagedAndDirty(RenderContext renderContext) {
        if (managed) {
            if (renderContext.isGL11 &&
                (vertexBufferObjectHandle == 0)) {
                // TODO: need a way to handle case where the allocated buffer goes away
                // (state change, etc).  But don't want to do glIsBuffer every frame,
                // and this method may get called every from (from MeshNode)
                //renderContext.gl11.glIsBuffer(vertexBufferObjectHandle) == false)) {
                createGPUBuffers(renderContext);
                loadVertices(renderContext);
                loadIndices(renderContext);
            }
            // if( renderContext.isGL20 &&
            // (vertexBufferObjectHandle == 0 ||
            // renderContext.gl20.glIsBuffer( vertexBufferObjectHandle ) ==
            // false) ) {
            // createGPUBuffers();
            // fillGPUBuffers();
            // }
        }

        if (dirtyVertices) loadVertices(renderContext);
        if (dirtyIndices) loadIndices(renderContext);
    }


    private void createGPUBuffers(RenderContext renderContext) {
        if (renderContext.canUseVBO) renderContext.add(allocater);
    }


    /** Fill the VBO for the mesh */
    private void loadVertices(RenderContext renderContext) {
        if (renderContext.canUseVBO) renderContext.add(vertexLoader);
    }


    /** Fill the IBO for the mesh */
    private void loadIndices(RenderContext renderContext) {
        if (renderContext.canUseVBO) renderContext.add(indexLoader);
    }


    /** Frees all resources associated with this Mesh */
    public void dispose(RenderContext renderContext) {
        if (renderContext.canUseVBO) renderContext.add(disposer);
    }


    /**
        Allocate the GPU buffer(s) for the mesh
     */
    protected class Allocate extends RenderPrimitive {
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            if (renderContext.isGL20)
                ;// createGPUBuffers(renderContext.gl20);
            else if (renderContext.isGL11)
                createGPUBuffers(renderContext,renderContext.gl11);
        }

        /** Allocate a VBO and IBO for the mesh */
        private void createGPUBuffers(RenderContext renderContext,GL11 gl) {
            int needed = 1;
            if (maxIndices > 0) needed++;
            
            int[] handle = new int[needed];
            gl.glGenBuffers(needed, handle, 0);
            vertexBufferObjectHandle = handle[0];

            if ( maxIndices > 0) indexBufferObjectHandle = handle[1];
        }
    }


    /**
       Load the mesh vertex data into the GPU buffer
     */
    protected class LoadVertices extends RenderPrimitive {
        /** vertex data is double-buffered so it can be loaded in render thread,
            while it gets modified in the game thread */
        public ByteBuffer loadBuffer;

        
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            if (renderContext.isGL20)
                ;
         else if (renderContext.isGL11)
            loadVertices(renderContext.gl11);

         dirtyVertices = false;
     }

     // TODO: This complete copying on each re-load is perhaps a bit
     // wasteful -- or maybe it's the best solution for the general case?
     private void loadVertices(GL11 gl) {
         ByteBuffer verticesBytes = (ByteBuffer)vertices;
         if ( loadBuffer == null ||
              loadBuffer.limit() < verticesBytes.limit() ) {
             loadBuffer = ByteBuffer.allocateDirect(verticesBytes.limit());
             Log.d("loadBuffer: allocated "+loadBuffer.limit());
         }
         loadBuffer.position(0);
         loadBuffer.put(verticesBytes);
         loadBuffer.position(0);
         
         gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, vertexBufferObjectHandle);
         gl.glBufferData(GL11.GL_ARRAY_BUFFER, getNumVertices() * attributes.vertexSize,
                         loadBuffer, isStatic ? GL11.GL_STATIC_DRAW : GL11.GL_DYNAMIC_DRAW);
         gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
     }

     // private void fillGPUBuffers( GL20 gl ) {
     // gl.glBindBuffer( GL20.GL_ARRAY_BUFFER, vertexBufferObjectHandle );
     // gl.glBufferData( GL20.GL_ARRAY_BUFFER, getNumVertices() *
     // attributes.vertexSize,
     // vertices, isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW );
     // gl.glBindBuffer( GL20.GL_ARRAY_BUFFER, 0 );

 }


    /**
       Load the mesh index data into the GPU buffer.
       
       Unlike the vertex data, index data is not double-buffered.  The Mesh class assumes
       indices are static.
     */
    protected class LoadIndices extends RenderPrimitive {
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            if (renderContext.isGL20)
                ;
            else if (renderContext.isGL11)
                loadIndices(renderContext.gl11);

            dirtyIndices = false;
        }

        private void loadIndices(GL11 gl) {
            if (maxIndices > 0) {
                gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, indexBufferObjectHandle);
                gl.glBufferData(GL11.GL_ELEMENT_ARRAY_BUFFER, indices.limit() * 2, indices,
                                GL11.GL_STATIC_DRAW);
                                //isStatic ? GL11.GL_STATIC_DRAW : GL11.GL_DYNAMIC_DRAW);
                gl.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
            }
        }


        // private void fillGPUBuffers( GL20 gl ) {
        // if( maxIndices > 0 ) {
        // gl.glBindBuffer( GL20.GL_ELEMENT_ARRAY_BUFFER, indexBufferObjectHandle );
        // gl.glBufferData( GL20.GL_ELEMENT_ARRAY_BUFFER, getNumIndices() * 2,
        // indices, isStatic ? GL20.GL_STATIC_DRAW : GL20.GL_DYNAMIC_DRAW );
        // gl.glBindBuffer( GL20.GL_ELEMENT_ARRAY_BUFFER, 0 );
        // }
        // }
    }


    /**
       Release the GPU buffers when done with the mesh 
     */
    protected class Dispose extends RenderPrimitive {
        int handle[] = new int[1];
        
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            if (renderContext.isGL20)
                ;// dispose( renderContext.gl20 );
            else if (renderContext.isGL11)
                dispose(renderContext.gl11);
        }

        private void dispose(GL11 gl) {
            handle[0] = vertexBufferObjectHandle;
            gl.glDeleteBuffers(1, handle, 0);
            vertexBufferObjectHandle = 0;
            
            if (maxIndices > 0) {
                handle[0] = indexBufferObjectHandle;
                gl.glDeleteBuffers(1, handle, 0);
                indexBufferObjectHandle = 0;
            }
        }

        // private void dispose( GL20 gl ) {
        // ByteBuffer tmp = ByteBuffer.allocateDirect( 4 );
        // tmp.order( ByteOrder.nativeOrder() );
        // IntBuffer handle = tmp.asIntBuffer();
        // handle.put( vertexBufferObjectHandle );
        // handle.position(0);
        // gl.glDeleteBuffers( 1, handle );

        // if( maxIndices > 0 ) {
        // handle.clear();
        // handle.put( indexBufferObjectHandle );
        // handle.position(0);
        // gl.glDeleteBuffers( 1, handle );
        // }
        // }
    }

 
    

    /**
     * Sets the vertices of this Mesh. The attributes are assumed to be given in
     * float format. If this mesh is configured to use fixed point an
     * IllegalArgumentException will be thrown.
     * 
     * @param vertices
     *            the vertices.
     */
    public void setVertices(float[] vertices) {
        setVertices(vertices, 0, vertices.length);
    }


    /**
     * Sets the vertices of this Mesh. The attributes are assumed to be given in
     * float format. If this mesh is configured to use fixed point an
     * IllegalArgumentException will be thrown.
     * 
     * @param vertices
     *            the vertices.
     * @param offset
     *            the offset into the vertices array
     * @param count
     *            the number of floats to use
     */
    public void setVertices(float[] vertices, int offset, int count) {
        if (useFixedPoint)
            throw new IllegalArgumentException("can't set float vertices for fixed point mesh");

        verticesFloat.clear();
        verticesFloat.put(vertices, offset, count);
        verticesFloat.limit(count);
        verticesFloat.position(0);

        this.vertices.limit(verticesFloat.limit() * 4);
        this.vertices.position(0);

        dirtyVertices = true;
    }


    /**
     * Sets the vertices of this Mesh. The attributes are assumed to be given in
     * fixed point format. If this mesh is configured to use floats an
     * IllegalArgumentException will be thrown.
     * 
     * @param vertices
     *            the vertices.
     */
    public void setVertices(int[] vertices) {
        setVertices(vertices, 0, vertices.length);
    }


    /**
     * Sets the vertices of this Mesh. The attributes are assumed to be given in
     * fixed point format. If this mesh is configured to use floats an
     * IllegalArgumentException will be thrown.
     * 
     * @param vertices
     *            the vertices.
     * @param offset
     *            the offset into the vertices array
     * @param count
     *            the number of floats to use
     */
    public void setVertices(int[] vertices, int offset, int count) {
        if (!useFixedPoint)
            throw new IllegalArgumentException(
                                               "can't set fixed point vertices for float mesh");

        verticesFixed.clear();
        verticesFixed.put(vertices, offset, count);
        verticesFixed.limit(count);
        verticesFixed.position(0);

        this.vertices.limit(verticesFixed.limit() * 4);
        this.vertices.position(0);

        dirtyVertices = true;
    }


    /**
     * Sets the indices of this Mesh
     * 
     * @param indices
     *            the indices
     */
    public void setIndices(short[] indices) {
        setIndices(indices, 0, indices.length);
    }


    /**
     * Sets the indices of this Mesh
     * 
     * @param indices
     *            the indices
     * @param offset
     *            the offset into the indices array
     * @param count
     *            the number of indices to use
     */
    public void setIndices(short[] indices, int offset, int count) {
        this.indices.put(indices, offset, count);
        this.indices.position(0);
        dirtyIndices = true;
    }


    /**
     * @return the number of defined indices
     */
    public int getNumIndices() {
        return indices.limit();
    }


    /**
     * @return the number of defined vertices
     */
    public int getNumVertices() {
        return vertices.limit() / attributes.vertexSize;
    }


    /**
     * @return the size of a single vertex in bytes
     */
    public int getVertexSize() {
        return attributes.vertexSize;
    }


    /**
     * @return whether 16.16 fixed point is used
     */
    public boolean usesFixedPoint() {
        return useFixedPoint;
    }


    /**
     * @return the maximum number of vertices this mesh can hold
     */
    public int getMaxVertices() {
        return maxVertices;
    }


    /**
     * @return the maximum number of indices this mesh can hold
     */
    public int getMaxIndices() {
        return maxIndices;
    }


    /**
     * Returns the first {@link VertexAttribute} having the given {@link Usage}.
     * 
     * @param usage
     *            the Usage.
     * @return the VertexAttribute or null if no attribute with that usage was
     *         found.
     */
    public VertexAttribute getVertexAttribute(int usage) {
        return attributes.getByUsage(usage);
    }


    /**
     * @return the vertex attributes of this Mesh
     */
    public VertexAttributes getVertexAttributes() {
        return attributes;
    }


    /**
     * @return the backing ByteBuffer holding the vertices
     */
    public Buffer getVerticesBuffer() {
        return vertices;
    }


    /**
     * @return the backing shortbuffer holding the indices
     */
    public ShortBuffer getIndicesBuffer() {
        return indices;
    }


    /**
     * Returns getNumVertices() vertices in the float array
     * 
     * @param vertices
     *            the destination array
     */
    public void getVertices(float[] vertices) {
        if (useFixedPoint)
            throw new IllegalArgumentException(
                                               "can't get float vertices from fixed point mesh");

        verticesFloat.get(vertices);
        verticesFloat.position(0);
    }


    /**
     * Returns getNumVertices() vertices in the fixed point array
     * 
     * @param vertices
     *            the destination array
     */
    public void getVertices(int[] vertices) {
        if (!useFixedPoint)
            throw new IllegalArgumentException(
                                               "can't get fixed point vertices from float mesh");

        verticesFixed.get(vertices);
        verticesFixed.position(0);
    }


    /**
     * Returns getNumIndices() indices in the short array
     * 
     * @param indices
     *            the destination array
     */
    public void getIndices(short[] indices) {
        this.indices.get(indices);
        this.indices.position(0);
    }


    /**
     * Get the index at the given offset, which is an index into the vertices
     * buffer
     */
    public short getIndex(int offset) {
        return this.indices.get(offset);
    }


    /**
     * Populate the Vertex structure with the vertex info from the specified
     * vertex.
     * 
     * Note that extracting vertex info is fairly expensive.
     * 
     * @param vert
     *            the vertex number
     * @param data
     *            the structure to hold the vertex data
     */
    public void getVertex(int vert, Vertex data) {
        int per_vert = attributes.vertexSize / 4; // 4 = sizeof int or sizeof
        // float

        if (data.positionAttr != null) {
            int base = (vert * per_vert) + data.positionAttr.offset / 4;
            if (useFixedPoint) {
                data.position.x = FP.toFloat(verticesFixed.get(base++));
                data.position.y = FP.toFloat(verticesFixed.get(base++));
                data.position.z = FP.toFloat(verticesFixed.get(base++));
            } else {
                data.position.x = verticesFloat.get(base++);
                data.position.y = verticesFloat.get(base++);
                data.position.z = verticesFloat.get(base++);
            }
        }

        if (data.normalAttr != null) {
            int base = (vert * per_vert) + data.normalAttr.offset / 4;
            if (useFixedPoint) {
                data.normal.x = FP.toFloat(verticesFixed.get(base++));
                data.normal.y = FP.toFloat(verticesFixed.get(base++));
                data.normal.z = FP.toFloat(verticesFixed.get(base++));
            } else {
                data.normal.x = verticesFloat.get(base++);
                data.normal.y = verticesFloat.get(base++);
                data.normal.z = verticesFloat.get(base++);
            }
        }

        if (data.colorAttr != null) {
            int base = (vert * per_vert) + data.colorAttr.offset / 4;
            if (useFixedPoint) {
                data.color.red = FP.toFloat(verticesFixed.get(base++));
                data.color.green = FP.toFloat(verticesFixed.get(base++));
                data.color.blue = FP.toFloat(verticesFixed.get(base++));
                data.color.alpha = FP.toFloat(verticesFixed.get(base++));
            } else {
                data.color.red = verticesFloat.get(base++);
                data.color.green = verticesFloat.get(base++);
                data.color.blue = verticesFloat.get(base++);
                data.color.alpha = verticesFloat.get(base++);
            }
        }

        if (data.textureAttr != null) {
            for (int t = 0; t < data.textureAttr.length; ++t) {
                int base = (vert * per_vert) + data.textureAttr[t].offset / 4;
                if (useFixedPoint) {
                    data.texture[t].u = FP.toFloat(verticesFixed.get(base++));
                    data.texture[t].v = FP.toFloat(verticesFixed.get(base++));
                } else {
                    data.texture[t].u = verticesFloat.get(base++);
                    data.texture[t].v = verticesFloat.get(base++);
                }
            }
        }
    }


    /**
     * Populate the VertexFP structure with the vertex info from the specified
     * vertex.
     * 
     * Note that extracting vertex info is fairly expensive.
     * 
     * @param vert
     *            the vertex number
     * @param data
     *            the structure to hold the vertex data
     */
    public void getVertex(int vert, VertexFP data) {
        int per_vert = attributes.vertexSize / 4; // 4 = sizeof int or sizeof float

        if (data.positionAttr != null) {
            int base = (vert * per_vert) + data.positionAttr.offset / 4;
            if (useFixedPoint) {
                data.position.x = verticesFixed.get(base++);
                data.position.y = verticesFixed.get(base++);
                data.position.z = verticesFixed.get(base++);
            } else {
                data.position.x = FP.floatToFP(verticesFloat.get(base++));
                data.position.y = FP.floatToFP(verticesFloat.get(base++));
                data.position.z = FP.floatToFP(verticesFloat.get(base++));
            }
        }

        if (data.normalAttr != null) {
            int base = (vert * per_vert) + data.normalAttr.offset / 4;
            if (useFixedPoint) {
                data.normal.x = verticesFixed.get(base++);
                data.normal.y = verticesFixed.get(base++);
                data.normal.z = verticesFixed.get(base++);
            } else {
                data.normal.x = FP.floatToFP(verticesFloat.get(base++));
                data.normal.y = FP.floatToFP(verticesFloat.get(base++));
                data.normal.z = FP.floatToFP(verticesFloat.get(base++));
            }
        }

        if (data.colorAttr != null) {
            int base = (vert * per_vert) + data.colorAttr.offset / 4;
            if (useFixedPoint) {
                data.color.red = FP.toFloat(verticesFixed.get(base++));
                data.color.green = FP.toFloat(verticesFixed.get(base++));
                data.color.blue = FP.toFloat(verticesFixed.get(base++));
                data.color.alpha = FP.toFloat(verticesFixed.get(base++));
            } else {
                data.color.red = verticesFloat.get(base++);
                data.color.green = verticesFloat.get(base++);
                data.color.blue = verticesFloat.get(base++);
                data.color.alpha = verticesFloat.get(base++);
            }
        }

        if (data.textureAttr != null) {
            for (int t = 0; t < data.textureAttr.length; ++t) {
                int base = (vert * per_vert) + data.textureAttr[t].offset / 4;
                if (useFixedPoint) {
                    data.texture[t].u = verticesFixed.get(base++);
                    data.texture[t].v = verticesFixed.get(base++);
                } else {
                    data.texture[t].u = FP.floatToFP(verticesFloat.get(base++));
                    data.texture[t].v = FP.floatToFP(verticesFloat.get(base++));
                }
            }
        }
    }


    /**
     * Store the values from the Vertex structure into the specified vertex of
     * the mesh.
     * 
     * Note that updating vertex info is fairly expensive.
     * 
     * @param vert
     *            the vertex number
     * @param data
     *            the structure to hold the vertex data
     */
    public void putVertex(int vert, Vertex data) {
        int per_vert = attributes.vertexSize / 4; // 4 = sizeof int or sizeof float

        if (data.positionAttr != null) {
            int base = (vert * per_vert) + data.positionAttr.offset / 4;
            if (useFixedPoint) {
                verticesFixed.put(base, FP.floatToFP(data.position.x));
                verticesFixed.put(base + 1, FP.floatToFP(data.position.y));
                verticesFixed.put(base + 2, FP.floatToFP(data.position.z));
            } else {
                verticesFloat.put(base, data.position.x);
                verticesFloat.put(base + 1, data.position.y);
                verticesFloat.put(base + 2, data.position.z);
            }
        }

        if (data.normalAttr != null) {
            int base = (vert * per_vert) + data.normalAttr.offset / 4;
            if (useFixedPoint) {
                verticesFixed.put(base, FP.floatToFP(data.normal.x));
                verticesFixed.put(base + 1, FP.floatToFP(data.normal.y));
                verticesFixed.put(base + 2, FP.floatToFP(data.normal.z));
            } else {
                verticesFloat.put(base, data.normal.x);
                verticesFloat.put(base + 1, data.normal.y);
                verticesFloat.put(base + 2, data.normal.z);
            }
        }

        if (data.colorAttr != null) {
            int base = (vert * per_vert) + data.colorAttr.offset / 4;
            if (useFixedPoint) {
                verticesFixed.put(base, FP.floatToFP(data.color.red));
                verticesFixed.put(base + 1, FP.floatToFP(data.color.green));
                verticesFixed.put(base + 2, FP.floatToFP(data.color.blue));
                verticesFixed.put(base + 3, FP.floatToFP(data.color.alpha));
            } else {
                verticesFloat.put(base, data.color.red);
                verticesFloat.put(base + 1, data.color.green);
                verticesFloat.put(base + 2, data.color.blue);
                verticesFloat.put(base + 3, data.color.alpha);
            }
        }

        if (data.textureAttr != null) {
            for (int t = 0; t < data.textureAttr.length; ++t) {
                int base = (vert * per_vert) + data.textureAttr[t].offset / 4;
                if (useFixedPoint) {
                    verticesFixed.put(base, FP.floatToFP(data.texture[t].u));
                    verticesFixed.put(base + 1, FP.floatToFP(data.texture[t].v));
                } else {
                    verticesFloat.put(base, data.texture[t].u);
                    verticesFloat.put(base + 1, data.texture[t].v);
                }
            }
        }
        
        dirtyVertices = true;
    }


    /**
     * Store the values from the VertexFP structure into the specified vertex of
     * the mesh.
     * 
     * Note that updating vertex info is fairly expensive.
     * 
     * @param vert
     *            the vertex number
     * @param data
     *            the structure to hold the vertex data
     */
    public void putVertex(int vert, VertexFP data) {
        int per_vert = attributes.vertexSize / 4; // 4 = sizeof int or sizeof int

        if (data.positionAttr != null) {
            int base = (vert * per_vert) + data.positionAttr.offset / 4;
            if (useFixedPoint) {
                verticesFixed.put(base, data.position.x);
                verticesFixed.put(base + 1, data.position.y);
                verticesFixed.put(base + 2, data.position.z);
            } else {
                verticesFloat.put(base, FP.toFloat(data.position.x));
                verticesFloat.put(base + 1, FP.toFloat(data.position.y));
                verticesFloat.put(base + 2, FP.toFloat(data.position.z));
            }
        }

        if (data.normalAttr != null) {
            int base = (vert * per_vert) + data.normalAttr.offset / 4;
            if (useFixedPoint) {
                verticesFixed.put(base, data.normal.x);
                verticesFixed.put(base + 1, data.normal.y);
                verticesFixed.put(base + 2, data.normal.z);
            } else {
                verticesFloat.put(base, FP.toFloat(data.normal.x));
                verticesFloat.put(base + 1, FP.toFloat(data.normal.y));
                verticesFloat.put(base + 2, FP.toFloat(data.normal.z));
            }
        }

        if (data.colorAttr != null) {
            int base = (vert * per_vert) + data.colorAttr.offset / 4;
            if (useFixedPoint) {
                verticesFixed.put(base, FP.floatToFP(data.color.red));
                verticesFixed.put(base + 1, FP.floatToFP(data.color.green));
                verticesFixed.put(base + 2, FP.floatToFP(data.color.blue));
                verticesFixed.put(base + 3, FP.floatToFP(data.color.alpha));
            } else {
                verticesFloat.put(base, data.color.red);
                verticesFloat.put(base + 1, data.color.green);
                verticesFloat.put(base + 2, data.color.blue);
                verticesFloat.put(base + 3, data.color.alpha);
            }
        }

        if (data.textureAttr != null) {
            for (int t = 0; t < data.textureAttr.length; ++t) {
                int base = (vert * per_vert) + data.textureAttr[t].offset / 4;
                if (useFixedPoint) {
                    verticesFixed.put(base, data.texture[t].u);
                    verticesFixed.put(base + 1, data.texture[t].v);
                } else {
                    verticesFloat.put(base, FP.toFloat(data.texture[t].u));
                    verticesFloat.put(base + 1, FP.toFloat(data.texture[t].v));
                }
            }
        }

        dirtyVertices = true;
    }


    /**
     * Get the Texture (if any) associated with this Mesh. Used by the MeshNode
     * when rendering the mesh.
     */
    public Texture getTexture() {
        return null;
    }


    /**
     * Helper class for working with individual vertices in the mesh
     */
    public static class Vertex {

        /** The Position attribute (if any) */
        public VertexAttribute positionAttr;

        /** The position */
        public Vec3 position;

        /** The Normal attribute (if any) */
        public VertexAttribute normalAttr;

        /** The normal */
        public Vec3 normal;

        /** The Color attribute (if any) */
        public VertexAttribute colorAttr;

        /** The color */
        public Color color;

        /** The texture attributes (if any) */
        public VertexAttribute[] textureAttr;

        /** The texture coords */
        public Vec2[] texture;


        /** Create an empty instance */
        public Vertex() {

        }


        public Vertex(VertexAttributes attributes) {
            for (int i = 0, n = attributes.size(); i < n; ++i) {
                VertexAttribute attr = attributes.get(i);
                prep(attr);
            }
        }


        /**
         * Set up the structure to extract info for the given atribute.
         */
        public void prep(VertexAttribute attr) {
            if (attr == null)
                throw new IllegalArgumentException("attr can't be null");

            switch (attr.usage) {
            case Usage.Position:
                positionAttr = attr;
                position = new Vec3();
                break;
            case Usage.Normal:
                normalAttr = attr;
                normal = new Vec3();
                break;
            case Usage.Color:
                colorAttr = attr;
                color = new Color();
                break;
            case Usage.Texture:
                if (textureAttr == null) {
                    textureAttr = new VertexAttribute[1];
                    textureAttr[0] = attr;

                    texture = new Vec2[1];
                    texture[0] = new Vec2();
                } else {
                    VertexAttribute[] old_attrs = textureAttr;
                    textureAttr = new VertexAttribute[old_attrs.length + 1];
                    for (int a = 0; a < old_attrs.length; ++a)
                        textureAttr[a] = old_attrs[a];
                    textureAttr[textureAttr.length - 1] = attr;

                    Vec2[] old_tex = texture;
                    texture = new Vec2[old_tex.length + 1];
                    for (int t = 0; t < old_tex.length; ++t)
                        texture[t] = old_tex[t];
                    texture[texture.length - 1] = new Vec2();
                }
                break;
            }
        }
    }


    /**
     * Helper class for working with individual vertices in the mesh
     */
    public static class VertexFP {

        /** The Position attribute (if any) */
        public VertexAttribute positionAttr;

        /** The position */
        public Vec3FP position;

        /** The Normal attribute (if any) */
        public VertexAttribute normalAttr;

        /** The normal */
        public Vec3FP normal;

        /** The Color attribute (if any) */
        public VertexAttribute colorAttr;

        /** The color */
        public Color color;

        /** The texture attributes (if any) */
        public VertexAttribute[] textureAttr;

        /** The texture coords */
        public Vec2FP[] texture;


        /** Create an empty instance */
        public VertexFP() {

        }


        public VertexFP(VertexAttributes attributes) {
            for (int i = 0, n = attributes.size(); i < n; ++i) {
                VertexAttribute attr = attributes.get(i);
                prep(attr);
            }
        }


        /**
         * Set up the structure to extract info for the given atribute.
         */
        public void prep(VertexAttribute attr) {
            if (attr == null)
                throw new IllegalArgumentException("attr can't be null");

            switch (attr.usage) {
            case Usage.Position:
                positionAttr = attr;
                position = new Vec3FP();
                break;
            case Usage.Normal:
                normalAttr = attr;
                normal = new Vec3FP();
                break;
            case Usage.Color:
                colorAttr = attr;
                color = new Color();
                break;
            case Usage.Texture:
                if (textureAttr == null) {
                    textureAttr = new VertexAttribute[1];
                    textureAttr[0] = attr;

                    texture = new Vec2FP[1];
                    texture[0] = new Vec2FP();
                } else {
                    VertexAttribute[] old_attrs = textureAttr;
                    textureAttr = new VertexAttribute[old_attrs.length + 1];
                    for (int a = 0; a < old_attrs.length; ++a)
                        textureAttr[a] = old_attrs[a];
                    textureAttr[textureAttr.length - 1] = attr;

                    Vec2FP[] old_tex = texture;
                    texture = new Vec2FP[old_tex.length + 1];
                    for (int t = 0; t < old_tex.length; ++t)
                        texture[t] = old_tex[t];
                    texture[texture.length - 1] = new Vec2FP();
                }
                break;
            }
        }
    }


    /**
     * Helper class for computing bounding box and other info for a mesh.
     * 
     * All bounding info is currently only availabled via FP values.
     */
    public static class Bounds {

        /** The mesh */
        public Mesh mesh;

        /** The size of the mesh in all three dimensions */
        public Vec3 size = new Vec3();

        /** The min bounds (min x,y,z) of the mesh */
        public Vec3 minimum = new Vec3();

        /** The max bounds (max x,y,z) of the mesh */
        public Vec3 maximum = new Vec3();

        /** The center point of the mesh (midpoint between minimum and maximum) */
        public Vec3 center = new Vec3();

        /**
         * The 'center of mass' point of the mesh (geometric average of all
         * vertices)
         */
        public Vec3 centerMass = new Vec3();

        /** The radius of the mesh, for bounding-sphere tests */
        public float radius;


        /** Create an empty instance */
        protected Bounds() {
        }

        private Bounds next_avail;
        private static Bounds first_avail;
        private static Object sync = new Object();


        /** Obtain an instance from a pool */
        public static Bounds obtain() {
            synchronized (sync) {
                if (first_avail == null)
                    first_avail = new Bounds();
                Bounds b = first_avail;
                first_avail = b.next_avail;
                return b;
            }
        }


        /** Return an instance to the pool */
        public void recycle() {
            synchronized (sync) {
                next_avail = first_avail;
                first_avail = this;
            }
        }


        public Bounds update(Mesh mesh) {
            return this.update(mesh, 0, mesh.getNumVertices());
        }


        public Bounds update(Mesh mesh, int offset, int count) {
            this.mesh = mesh;
            update(offset, count);
            return this;
        }


        public void update() {
            update(0, mesh.getNumVertices());
        }


        /** Update the bounding info from the mesh's current geometry */
        public void update(int offset, int count) {
            if (mesh == null)
                throw new IllegalStateException("No mesh to update bounds");

            // Only need the position info for each vertex
            Vertex vert = new Vertex();
            vert.prep(mesh.getVertexAttribute(Usage.Position));

            // Extract each vertex, update the min/max,
            // and add all together for the centerMass
            centerMass.set(0, 0, 0);
            minimum.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
            maximum.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);

            for (int v = offset, vlast = offset + count; v < vlast; ++v) {
                mesh.getVertex(v, vert);
                centerMass.add(vert.position, centerMass);
                minimum.minimum(vert.position, minimum);
                maximum.maximum(vert.position, maximum);
            }

            // Divide centerMass total by num verts, for geometric average
            if (count > 0)
                centerMass.scale(1f / count, centerMass);

            // The size is just the delta between min and max
            maximum.delta(minimum, size);

            // The center is the midpoint between min and max
            minimum.add(maximum, center);
            center.scale(0.5f, center);

            // The radius is the length of the size vector
            radius = size.length();
        }


        /**
         * Apply a transformation matrix to the bounds
         */
        public void transform(M4 matrix) {
            Vec3 xmin = new Vec3();
            Vec3 xmax = new Vec3();
            matrix.multiply(center,center);
            matrix.multiply(minimum,xmin);
            matrix.multiply(maximum,xmax);
            
            if ( xmin.x < xmax.x ) {
                minimum.x = xmin.x;
                maximum.x = xmax.x;
            } else {
                minimum.x = xmax.x;
                maximum.x = xmin.x;
            }

            if ( xmin.y < xmax.y ) {
                minimum.y = xmin.y;
                maximum.y = xmax.y;
            } else {
                minimum.y = xmax.y;
                maximum.y = xmin.y;
            }

            if ( xmin.z < xmax.z ) {
                minimum.z = xmin.z;
                maximum.z = xmax.z;
            } else {
                minimum.z = xmax.z;
                maximum.z = xmin.z;
            }

            maximum.delta(minimum,size);
            
        }
        
        
        /**
         * Test whether the given point is within the bounding box of the mesh
         * -- does not check for inclusion within the actual volume of the mesh
         * itself, that's a much harder problem.
         */
        public boolean contains(Vec3 pt) {
            final float x = pt.x;
            if (x < minimum.x || x > maximum.x)
                return false;

            final float y = pt.y;
            if (y < minimum.y || y > maximum.y)
                return false;

            final float z = pt.z;
            if (z < minimum.z || z > maximum.z)
                return false;

            return true;
        }


        /**
         * Test whether the given points is within the bounds of the mesh, or
         * whether the line joining the points passes through the mesh.
         */
        public boolean contains(Vec3 pt1, Vec3 pt2) {
            if (contains(pt1))
                return true;
            if (contains(pt2))
                return true;

            // Doesn't contain either point, but perhaps the line passes
            // through?
            // Need essentially a ray-box intersection test

            if (pt1.x <= pt2.x) {
                if (pt2.x < minimum.x)
                    return false;
            } else {
                if (pt1.x > maximum.x)
                    return false;
            }

            if (pt1.y <= pt2.y) {
                if (pt2.y < minimum.y)
                    return false;
            } else {
                if (pt1.y > maximum.y)
                    return false;
            }

            if (pt1.z <= pt2.z) {
                if (pt2.z < minimum.z)
                    return false;
            } else {
                if (pt1.z > maximum.z)
                    return false;
            }

            return true;
        }


        @Override
        public String toString() {
            return String.format("(min=%s max=%s ctr=%s size=%s rad=%.3f)", minimum,
                                 maximum, center, size, radius);
        }

    }


    /**
     * Helper class for modifying the points in a mesh
     */
    public static class Transform {

        /** The matrix to use for the transformation */
        public M4 matrix;


        /** Construcct a new Transform */
        public Transform() {
            this(new M4());
            this.matrix.setIdentity();
        }


        /** Construcct a new Transform using the given matrix */
        public Transform(M4 matrix) {
            this.matrix = matrix;
        }


        /** Modify the matrix to translate the vertices of the mesh */
        public Transform translate(Vec3 delta) {
            matrix.translate(delta);
            return this;
        }


        /**
         * Modify the matrix to translate the vertices of the mesh around the
         * origin
         */
        public Transform rotate(Vec3 dir, int theta) {
            matrix.rotate(dir, theta);
            return this;
        }


        /**
         * Modify the matrix to translate the vertices of the mesh around the
         * origin
         */
        public Transform rotate(Vec4 q) {
            matrix.rotate(q);
            return this;
        }


        /** Modify the matrix to scale all the vertices of the mesh uniformly */
        public Transform scale(float scale) {
            matrix.scale(scale);
            return this;
        }


        /** Modify the matrix to scale the vertices in different do */
        public Transform scale(float x, float y, float z) {
            matrix.scale(x, y, z);
            return this;
        }


        /**
         * Apply the transform to a specific mesh
         */
        public void apply(Mesh mesh) {
            // Log.d("Mesh.Transform: apply to "+mesh);
            // Log.d("%s", matrix);

            // Need the position info for each vertex
            Vertex vert = new Vertex();
            vert.prep(mesh.getVertexAttribute(Usage.Position));

            // Also need to update normals if present
            VertexAttribute normalAttr = mesh.attributes.getByUsage(Usage.Normal);
            if (normalAttr != null)
                vert.prep(normalAttr);

            for (int v = 0, num = mesh.getNumVertices(); v < num; ++v) {
                mesh.getVertex(v, vert);
                matrix.multiply(vert.position, vert.position);
                if (vert.normal != null)
                    matrix.multiply(vert.normal, vert.normal);
                mesh.putVertex(v, vert);
            }
        }


        /**
         * Helper method that translates the mesh so that it is centered on the
         * origin
         */
        public void center(Mesh mesh) {
            center(mesh, Vec3.ORIGIN);
        }


        /**
         * Helper method that translates the mesh so that it is centered on the
         * given point
         */
        public void center(Mesh mesh, Vec3 pos) {
            Bounds bounds = Bounds.obtain().update(mesh);
            Vec3 delta = new Vec3();
            pos.delta(bounds.center, delta);

            matrix.setIdentity();
            matrix.translate(delta);

            apply(mesh);
        }

    }


    /**
     * Class to dump mesh vertex data out, for debugging
     */
    public static class Dumper {

        public static void dump(Mesh mesh) {
            String typeStr = "unknown";
            switch (mesh.type) {
            case GL10.GL_POINTS:
                typeStr = "GL_POINTS";
                break;
            case GL10.GL_LINES:
                typeStr = "GL_LINES";
                break;
            case GL10.GL_LINE_STRIP:
                typeStr = "GL_LINE_STRIP";
                break;
            case GL10.GL_LINE_LOOP:
                typeStr = "GL_LINE_LOOP";
                break;
            case GL10.GL_TRIANGLES:
                typeStr = "GL_TRIANGLES";
                break;
            case GL10.GL_TRIANGLE_FAN:
                typeStr = "GL_TRIANGLE_FAN";
                break;
            case GL10.GL_TRIANGLE_STRIP:
                typeStr = "GL_TRIANGLE_STRIP";
                break;
            }

            Log.d("Mesh dump %s: %d vertices, %d indices, type=%d(%s)", mesh, mesh
                    .getNumVertices(), mesh.getNumIndices(), mesh.type, typeStr);

            Vertex vert = new Vertex();
            for (int u = 0; u < Usage.Generic; ++u) {
                VertexAttribute attr = mesh.getVertexAttribute(u);
                if (attr != null)
                    vert.prep(attr);
            }
            Log.d("  vertex layout (%d bytes): p=%d n=%d c=%d t=%d",
                  mesh.getVertexSize(), vert.positionAttr == null ? -1
                          : vert.positionAttr.offset, vert.normalAttr == null ? -1
                          : vert.normalAttr.offset, vert.colorAttr == null ? -1
                          : vert.colorAttr.offset, vert.textureAttr == null ? -1
                          : vert.textureAttr[0].offset);

            // Extract each vertex, dump it out
            int numVerts = mesh.getNumVertices();
            for (int v = 0; v < numVerts; ++v) {
                mesh.getVertex(v, vert);
                Log.d("  vert[%d] p=%s n=%s c=%s t=%s", v, vert.position, vert.normal,
                      vert.color, vert.texture == null ? null : vert.texture[0]);
            }
            
            // Dump indices
            String indices = "";
            for (int i=0, n = mesh.getNumIndices(); i<n; ++i) {
                short index = mesh.indices.get(i);
                indices += String.format("%d, ", index);
            }
            Log.d("  indices(%d): %s", mesh.getNumIndices(), indices);
        }
    }

}
