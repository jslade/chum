package chum.gl.render;

import chum.engine.GameController;
import chum.gl.Mesh;
import chum.gl.RenderContext;
import chum.gl.RenderNode;
import chum.gl.Texture;
import chum.gl.VertexAttribute;
import chum.gl.VertexAttributes.Usage;
import chum.gl.render.primitive.Blend;
import chum.gl.render.primitive.RenderPrimitive;

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
    public int type;

    /** The Texture to be applied */
    public Texture texture;

    /** The offset into the index list to render */
    public int offset;

    /** The number of indices to render.  0 means all */
    public int count;

    /** Whether blending should be enabled */
    public boolean blend;

    protected Blend.Enable enableBlend = new Blend.Enable();
    protected Blend.Disable disableBlend = new Blend.Disable();
    protected Renderer renderer = new Renderer();
    
    
    public MeshNode() {
        super();
    }

    public MeshNode(Mesh mesh) {
        this(mesh,
             mesh.type != 0 ? mesh.type : GL10.GL_TRIANGLES,
             mesh.getTexture());
    }

    public MeshNode(Mesh mesh,int type) {
        this(mesh,
             type,
             mesh.getTexture());
    }


    public MeshNode(Mesh mesh,int type,Texture tex) {
        super();
        setMesh(mesh,type);
        setTexture(tex);
    }


    /**
       Set the mesh to be rendered
       @param mesh The mesh to render
    */
    public void setMesh(Mesh mesh) {
        setMesh(mesh, mesh.type != 0 ? mesh.type : this.type);
    }


    /**
       Set the mesh to be rendered, as a specific primitive type
       @param mesh The mesh to render
       @param type The OpenGL primitive type to render
    */
    public void setMesh(Mesh mesh, int type) {
        this.mesh = mesh;
        this.type = type;

        if ( mesh == null )
            setTexture(null);
        else {
            Texture tex = mesh.getTexture();
            if ( tex != null ) setTexture(tex);
            else ;// keep the texture already set
        }
    }

    
    /**
       Set the texture to be mapped to the mesh
     */
    public void setTexture(Texture tex) {
        texture = tex;
    }

    
    @Override
    public void onSetup(GameController gc) {
        super.onSetup(gc);
        if ( mesh != null ) {
            if ( texture == null ) setTexture(mesh.getTexture());
        }
    }

    
    /** When the surface is created, ensure that the mesh is setup to render */
    @Override
    public void onSurfaceCreated(RenderContext renderContext) {
        super.onSurfaceCreated(renderContext);

        if ( mesh != null ) {
            mesh.checkManagedAndDirty(renderContext);
            if ( texture == null ) setTexture(mesh.getTexture());
        }
        
        if ( texture != null ) {
            texture.load(renderContext);
        }
    }

       
    /**
       Renders the mesh using the given primitive type. If indices are
       set for this mesh then getNumIndices() / #vertices per
       primitive primitives are rendered. If no indices are set then
       getNumVertices() / #vertices per primitive are rendered.
       
       This method is intended for use with OpenGL ES 1.x and will
       throw an IllegalStateException when OpenGL ES 2.0 is used.
    */
    @Override
    public boolean renderPrefix(RenderContext renderContext) {
        if ( mesh != null ) {
            // TODO: Need to change this so it doesn't happen every frame, since
            // most of the time it won't be dirty
            mesh.checkManagedAndDirty(renderContext); 

            if ( blend )
                renderContext.add(enableBlend);
            renderContext.add(renderer);
        }   
        return true;
    }

    
    /**
       Restore the previous drawing state after the mesh is drawn.
     */
    @Override
    public void renderPostfix(RenderContext renderContext) {
        if ( blend )
            renderContext.add(disableBlend);
    }


    protected class Renderer extends RenderPrimitive {
        
        @Override
        public void render(RenderContext renderContext, GL10 gl) {
            if ( renderContext.isGL20 ) {
                //render(shader,...);
            } else {
                render(renderContext,type,offset,count );
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
        protected void render(RenderContext renderContext, int primitiveType, int offset, int count) {
            if( renderContext.isGL20 )
                throw new IllegalStateException( "can't use this render method with OpenGL ES 2.0" );
            
            if ( count == 0 ) // TODO: belongs in checkManagedAndDirty()?
                count = mesh.maxIndices > 0? mesh.getNumIndices(): mesh.getNumVertices();

            // The texture is bound before hand, so any tex coords in the
            // vertices apply to this texture
            if ( texture != null )
                texture.bind(renderContext);

            if( mesh.vertexBufferObjectHandle != 0 )
                renderVBO(renderContext, primitiveType, offset, count);
            else
                renderVA(renderContext, primitiveType, offset, count);


            // Assume it is not necessary to unbind the texture afterward
            // if ( texture != null )
            //    texture.unbind();
        }


        /** TODO: don't disable/enable client state for every render -- share across meshes */
        protected void renderVBO(RenderContext renderContext, int primitiveType, int offset, int count ) {
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
                    gl.glEnable(GL10.GL_TEXTURE_2D);
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
                    gl.glDisable(GL10.GL_TEXTURE_2D);
                    gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                    gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                    textureUnit--;
                }		
            }

            mesh.vertices.position(0);
        }
        
        protected void renderVA(RenderContext renderContext, int primitiveType, int offset, int count ) {
            GL10 gl = renderContext.gl10;
        
            int numAttributes = mesh.attributes.size();
            int type = mesh.useFixedPoint ? GL11.GL_FIXED : GL11.GL_FLOAT;
            int textureUnit = 0;
        
            for( int i = 0; i < numAttributes; i++ ) {
                VertexAttribute attribute = mesh.attributes.get( i );
                switch(attribute.usage) {
                case  Usage.Position:
                    gl.glEnableClientState( GL11.GL_VERTEX_ARRAY );
                    mesh.vertices.position( attribute.offset );
                    gl.glVertexPointer( attribute.numComponents, type,
                                        mesh.attributes.vertexSize, mesh.vertices );
                    break;
                case  Usage.Color:
                    gl.glEnableClientState( GL11.GL_COLOR_ARRAY );
                    mesh.vertices.position( attribute.offset );
                    gl.glColorPointer( attribute.numComponents, type,
                                       mesh.attributes.vertexSize, mesh.vertices );
                    break;
                case Usage.Normal:
                    gl.glEnableClientState( GL11.GL_NORMAL_ARRAY );
                    mesh.vertices.position( attribute.offset );
                    gl.glNormalPointer( type, mesh.attributes.vertexSize, mesh.vertices );
                    break;
                case Usage.Texture:
                    gl.glEnable(GL10.GL_TEXTURE_2D);
                    gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                    gl.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                    mesh.vertices.position( attribute.offset );
                    gl.glTexCoordPointer( attribute.numComponents, type,
                                          mesh.attributes.vertexSize, mesh.vertices );
                    textureUnit++;
                    break;
                }
            }
        
            if( mesh.maxIndices > 0 ) {
                mesh.indices.position(offset);
                gl.glDrawElements( primitiveType, count, GL10.GL_UNSIGNED_SHORT, mesh.indices );
            } else
                gl.glDrawArrays( primitiveType, offset, count);
        
            textureUnit--;
        
            for( int i = 0; i < numAttributes; i++ ) {
                VertexAttribute attribute = mesh.attributes.get( i );
                switch(attribute.usage) {
                case Usage.Color:
                    gl.glDisableClientState( GL11.GL_COLOR_ARRAY );
                    break;
                case Usage.Normal:
                    gl.glDisableClientState( GL11.GL_NORMAL_ARRAY );
                    break;
                case Usage.Texture:
                    gl.glDisable(GL10.GL_TEXTURE_2D);
                    gl.glClientActiveTexture( GL11.GL_TEXTURE0 + textureUnit );
                    gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
                    textureUnit--;
                    break;
                }		
            }
        
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
}
