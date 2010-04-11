package chum.gl.render;

import chum.gl.Mesh;
import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.VertexAttribute;
import chum.gl.VertexAttributes.Usage;
import chum.util.Log;


import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;


/**
   A MeshNode renders a Mesh.  The MeshNode rendering code is separated from the Mesh class
   so that the Mesh class can hold the mesh data, and multiple MeshNodes can be used in the
   tree to render that same mesh at multiple locations.
*/
public class MeshNode extends RenderNode {
    
    /** The Mesh to be rendered */
    public Mesh mesh;

    /** The primitive type to be rendered */
    public int type = GL10.GL_TRIANGLES;

    /** The offset into the index list to render */
    public int offset;

    /** The number of indices to render.  0 means all */
    public int count;

    
    public MeshNode() {
        super();
    }

    public MeshNode(Mesh mesh) {
        super();
        this.mesh = mesh;
    }

    public MeshNode(Mesh mesh,int type) {
        super();
        this.mesh = mesh;
        this.type = type;
    }

    public MeshNode(Mesh mesh, int type, int offset, int count) {
        super();
        this.mesh = mesh;
        this.type = type;
        this.offset = offset;
        this.count = count;
    }


    /** When the surface is created, ensure that the mesh is setup to render */
    public void onSurfaceCreated(RenderContext renderContext) {
        super.onSurfaceCreated(renderContext);

        if ( mesh != null ) 
            mesh.onSurfaceCreated(renderContext);
    }

       
    /**
       Renders the mesh using the given primitive type. If indices are
       set for this mesh then getNumIndices() / #vertices per
       primitive primitives are rendered. If no indices are set then
       getNumVertices() / #vertices per primitive are rendered.
       
       This method is intended for use with OpenGL ES 1.x and will
       throw an IllegalStateException when OpenGL ES 2.0 is used.
    */
    public void renderPrefix(GL10 gl10) {
        if ( mesh == null )
            throw new IllegalStateException("can't render without a Mesh");

        mesh.checkManagedAndDirty();

        if ( renderContext.isGL20 ) {
            //render(shader,...);
        } else {
            render( this.type, this.offset, this.count );
        }
    }
    
    /**
       Renders the mesh using the given primitive type. offset specifies the
       offset into either the vertex buffer or the index buffer depending on
       whether indices are defined. count specifies the number of 
       vertices or indices to use thus count / #vertices per primitive primitives
       are rendered.
       
       This method is intended for use with OpenGL ES 1.x and will
       throw an IllegalStateException when OpenGL ES 2.0 is used.
       
       @param primitiveType the primitive type
       @param offset the offset into the vertex or index buffer
       @param count number of vertices or indices to use
    */
    protected void render( int primitiveType, int offset, int count ) {
        if( renderContext.isGL20 )
            throw new IllegalStateException( "can't use this render method with OpenGL ES 2.0" );
        
        if ( count == 0 )
            count = mesh.maxIndices > 0? mesh.getNumIndices(): mesh.getNumVertices();
    
        if( mesh.vertexBufferObjectHandle != 0 )
            renderVBO( primitiveType, offset, count );
        else
            renderVA( primitiveType, offset, count );
    }

    
    /** todo: don't disable/enable client state for every render -- share across meshes */
    protected void renderVBO( int primitiveType, int offset, int count ) {
        GL11 gl = renderContext.gl11;

        gl.glBindBuffer( GL11.GL_ARRAY_BUFFER, mesh.vertexBufferObjectHandle );
        
        int numAttributes = mesh.attributes.size();
        int type = mesh.useFixedPoint ? GL11.GL_FIXED : GL11.GL_FLOAT;
        int textureUnit = 0;
        
        for( int i = 0; i < numAttributes; i++ ) {
            VertexAttribute attribute = mesh.attributes.get( i );
            if( attribute.usage == Usage.Position ) {
                gl.glEnableClientState( GL11.GL_VERTEX_ARRAY );
                gl.glVertexPointer( attribute.numComponents, type,
                                    mesh.attributes.vertexSize, attribute.offset );
                continue;
            }
                
            if( attribute.usage == Usage.Color ) {
                gl.glEnableClientState( GL11.GL_COLOR_ARRAY );
                gl.glColorPointer( attribute.numComponents, type,
                                   mesh.attributes.vertexSize, attribute.offset );
                continue;
            }
                
            if( attribute.usage == Usage.Normal ) {
                gl.glEnableClientState( GL11.GL_NORMAL_ARRAY );
                gl.glNormalPointer( type, mesh.attributes.vertexSize, attribute.offset );
                continue;
            }
                
            if( attribute.usage == Usage.Texture ) {
                gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                gl.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                gl.glTexCoordPointer( attribute.numComponents, type,
                                      mesh.attributes.vertexSize, attribute.offset );
                textureUnit++;
                continue;
            }
        }

        if( mesh.maxIndices > 0 ) {
            gl.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, mesh.indexBufferObjectHandle );
            gl.glDrawElements(primitiveType, count, GL10.GL_UNSIGNED_SHORT, offset );
        } else {
            gl.glDrawArrays(primitiveType, offset, count);
        }
        
        textureUnit--;
        
        for( int i = 0; i < numAttributes; i++ ) {
            VertexAttribute attribute = mesh.attributes.get( i );
            if( attribute.usage == Usage.Color )
                gl.glDisableClientState( GL11.GL_COLOR_ARRAY );
            if( attribute.usage == Usage.Normal )
                gl.glDisableClientState( GL11.GL_NORMAL_ARRAY );
            if( attribute.usage == Usage.Texture ) {
                gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                textureUnit--;
            }		
        }

        mesh.vertices.position(0);
    }
        
    protected void renderVA( int primitiveType, int offset, int count ) {
        GL10 gl = renderContext.gl10;
        
        int numAttributes = mesh.attributes.size();
        int type = mesh.useFixedPoint ? GL11.GL_FIXED : GL11.GL_FLOAT;
        int textureUnit = 0;
        
        for( int i = 0; i < numAttributes; i++ ) {
            VertexAttribute attribute = mesh.attributes.get( i );
            if( attribute.usage == Usage.Position ) {
                gl.glEnableClientState( GL11.GL_VERTEX_ARRAY );
                mesh.vertices.position( attribute.offset );
                gl.glVertexPointer( attribute.numComponents, type,
                                    mesh.attributes.vertexSize, mesh.vertices );
                continue;
            }
            
            if( attribute.usage == Usage.Color ) {
                gl.glEnableClientState( GL11.GL_COLOR_ARRAY );
                mesh.vertices.position( attribute.offset );
                gl.glColorPointer( attribute.numComponents, type,
                                   mesh.attributes.vertexSize, mesh.vertices );
                continue;
            }
            
            if( attribute.usage == Usage.Normal ) {
                gl.glEnableClientState( GL11.GL_NORMAL_ARRAY );
                mesh.vertices.position( attribute.offset );
                gl.glNormalPointer( type, mesh.attributes.vertexSize, mesh.vertices );
                continue;
            }
            
            if( attribute.usage == Usage.Texture ) {
                gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                gl.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                mesh.vertices.position( attribute.offset );
                gl.glTexCoordPointer( attribute.numComponents, type,
                                      mesh.attributes.vertexSize, mesh.vertices );
                textureUnit++;
                continue;
            }
        }
        
        if( mesh.maxIndices > 0 )
            gl.glDrawElements( primitiveType, count, GL10.GL_UNSIGNED_SHORT, mesh.indices );
        else
            gl.glDrawArrays( primitiveType, offset, count);
        
        textureUnit--;
        
        for( int i = 0; i < numAttributes; i++ ) {
            VertexAttribute attribute = mesh.attributes.get( i );
            if( attribute.usage == Usage.Color )
                gl.glDisableClientState( GL11.GL_COLOR_ARRAY );
            if( attribute.usage == Usage.Normal )
                gl.glDisableClientState( GL11.GL_NORMAL_ARRAY );
            if( attribute.usage == Usage.Texture ) {
                gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                textureUnit--;
            }		
        }
        
        mesh.vertices.position(0);
    }
    
//     public void render( ShaderProgram shader, int primitiveType ) {
//         render( shader, primitiveType, 0, maxIndices > 0? getNumIndices(): getNumVertices() );
//     }
    
//     public void render( ShaderProgram shader, int primitiveType, int offset, int count ) {
//         if( !graphics.isGL20Available() )
//             throw new IllegalStateException( "can't use this render method with OpenGL ES 1.x" );
        
//         checkManagedAndDirty();
        
//         GL20 gl = graphics.getGL20();
//         gl.glBindBuffer( GL11.GL_ARRAY_BUFFER, vertexBufferObjectHandle );
        
//         int numAttributes = attributes.size();
//         int type = useFixedPoint?GL11.GL_FIXED:GL11.GL_FLOAT;
//         int textureUnit = 0;
        
//         for( int i = 0; i < numAttributes; i++ ) {
//             VertexAttribute attribute = attributes.get( i );
//             shader.enableVertexAttribute( attribute.alias );
//             shader.setVertexAttribute( attribute.alias, attribute.numComponents, type,
//                                        false, attributes.vertexSize, attribute.offset );
//         }
        
//         if( maxIndices > 0 ) {
//             gl.glBindBuffer( GL11.GL_ELEMENT_ARRAY_BUFFER, indexBufferObjectHandle );
//             gl.glDrawElements( primitiveType, count, GL10.GL_UNSIGNED_SHORT, offset );
//         } else {
//             gl.glDrawArrays( primitiveType, offset, count);
//         }
        
//         textureUnit--;
        
//         for( int i = 0; i < numAttributes; i++ ) {
//             VertexAttribute attribute = mesh.attributes.get( i );
//             shader.disableVertexAttribute( attribute.alias );
//         }
//     }	
    


}
