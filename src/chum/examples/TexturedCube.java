package chum.examples;

import chum.engine.*;
import chum.engine.common.*;
import chum.f.*;
import chum.gl.*;
import chum.gl.render.*;

import android.os.Bundle;


/**
   Draw a texture-mapped cube
*/
public class TexturedCube extends GameActivity
{
    MeshNode cubeNode;
    Mesh cube;
    Texture tex;
    RotateNode rot_x, rot_y, rot_z;

    
    /** Keep track of the original title string, so it can be updated (appended) */
    CharSequence origTitle;


    @Override
    public void setViewOptions() {
    	this.hideTitlebar = false;
    }
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        origTitle = getTitle();
    }


    @Override
    protected GameTree createGameTree() {
        return (new GameTree() {

                // The logic tree consists of two nodes:
                // - one to spin the pyramid
                // - one to periodically display the FPS
                protected GameNode createLogicTree() {
                    GameNode node = new GameNode();

                    node.addNode(new GameNode(){
                            public boolean update(long millis) {
                                spin(millis);
                                return true;
                            }
                        });

                    node.addNode(new FPSNode(){
                            public void showFPS() {
                                super.showFPS();
                                showFPSInTitle();
                            }
                        });

                    return node;
                }

                /**
                   The render tree consists of:
                   - the root node is an 3D projection
                   - camera node that doesn't move, at the default
                     location and looking to the origin
                   - ClearNode to clear the scene
                   - Rotate node for x-axis
                     - Rotate node for y-axis
                       - Rotate node for z-axis
                         - MeshNode to draw the pyramid
                */
                protected RenderNode createRenderTree() {
                    Standard3DNode base = new chum.gl.render.Standard3DNode();
                                
                    base.setPerspective(120f,0f,.5f,5f);
                    base.addNode(new CameraNode(new Vec3(0f,2f,-2f), Vec3.ORIGIN));
                    base.addNode(new ClearNode(new Color("#880000")));
                                        
                    cube = createCube();
                    cubeNode = new MeshNode(cube);

                    tex = new Texture();
                    tex.setResource(R.drawable.textured_cube);
                    cubeNode.texture = tex;

                    rot_x = new RotateNode(0,Vec3.X_AXIS);
                    rot_y = new RotateNode(0,Vec3.Y_AXIS);
                    rot_z = new RotateNode(0,Vec3.Z_AXIS);
                    base.addNode(rot_x);
                    rot_x.addNode(rot_y);
                    rot_y.addNode(rot_z);
                    rot_z.addNode(cubeNode);

                    // The first RotateNode has push=true, so that it pushes
                    // the modelview matrix onto the stack going in, the pops it off
                    // going out to restore the original transformation.
                    rot_x.push = true;

                    // Add an extra node displaying a 3D axis
                    // just to show orientation more clearly
                    //MeshNode axisNode = new MeshNode(chum.util.mesh.Axis.create3D(6f));
                    //rot_z.addNode(axisNode);

                    return base;
                }
            });
    }



    /**
       This gets called once to create the Mesh representing the cube shape.

       For the sake of simplicity, each face of the cube is defined by four vertices,
       each with tex coords mapping to a face of the cube texture.  Technically some of the
       vertices could be shared, but that just takes a bit more effort.
    */
    protected Mesh createCube() {
        MeshBuilder builder = new MeshBuilder(true,
                                              new VertexAttribute(VertexAttributes.Usage.Position),
                                              new VertexAttribute(VertexAttributes.Usage.Texture));
        builder.addVertex(new Vec3(-1f,-1f,-1f), new Vec2(.0f,.33f));
        builder.addVertex(new Vec3(1f,-1f,-1f), new Vec2(.33f,.33f));
        builder.addVertex(new Vec3(1f,-1f,1f), new Vec2(.33f,.66f));
        builder.addVertex(new Vec3(-1f,-1f,1f), new Vec2(.0f,.66f));
        builder.addIndex( 0, 1, 2, 0, 2, 3 ); // bottom = '1' (-y)

        builder.addVertex(new Vec3(-1f,1f,-1f), new Vec2(.66f,.33f));
        builder.addVertex(new Vec3(1f,1f,-1f), new Vec2(1f,.33f));
        builder.addVertex(new Vec3(1f,1f,1f), new Vec2(1f,.66f));
        builder.addVertex(new Vec3(-1f,1f,1f), new Vec2(.66f,.66f));
        builder.addIndex( 4, 6, 5, 4, 7, 6 ); // top = '3' (+y)

        builder.addVertex(new Vec3(-1f,-1f,-1f), new Vec2(.33f,.33f));
        builder.addVertex(new Vec3(1f,-1f,-1f), new Vec2(.66f,.33f));
        builder.addVertex(new Vec3(1f,1f,-1f), new Vec2(.66f,.66f));
        builder.addVertex(new Vec3(-1f,1f,-1f), new Vec2(.33f,.66f));
        builder.addIndex( 8, 10, 9, 8, 11, 10 ); // side = '2' (-z)

        builder.addVertex(new Vec3(-1f,-1f,1f), new Vec2(.66f,.66f));
        builder.addVertex(new Vec3(1f,-1f,1f), new Vec2(1f,.66f));
        builder.addVertex(new Vec3(1f,1f,1f), new Vec2(1f,1f));
        builder.addVertex(new Vec3(-1f,1f,1f), new Vec2(.66f,1f));
        builder.addIndex( 12, 13, 14, 12, 14, 15 ); // side = '4' (+z)

        builder.addVertex(new Vec3(-1f,-1f,-1f), new Vec2(.33f,.66f));
        builder.addVertex(new Vec3(-1f,1f,-1f), new Vec2(.66f,.66f));
        builder.addVertex(new Vec3(-1f,1f,1f), new Vec2(.66f,1f));
        builder.addVertex(new Vec3(-1f,-1f,1f), new Vec2(.33f,1f));
        builder.addIndex( 16, 18, 17, 16, 19, 18 ); // side = '5' (-x)

        builder.addVertex(new Vec3(1f,-1f,-1f), new Vec2(.33f,0f));
        builder.addVertex(new Vec3(1f,1f,-1f), new Vec2(.66f,0f));
        builder.addVertex(new Vec3(1f,1f,1f), new Vec2(.66f,.33f));
        builder.addVertex(new Vec3(1f,-1f,1f), new Vec2(.33f,.33f));
        builder.addIndex( 20, 21, 22, 20, 22, 23 ); // side = '6' (+x)

        return builder.build(true,false);
    }


    private static final float DX = 0.02f;
    private static final float DY = 0.03f;
    private static final float DZ = 0.04f;
    private static final float D360 = 360f;

    protected void spin(long millis) {
        rot_x.degrees += DX * millis;
        while ( rot_x.degrees > D360 ) rot_x.degrees -= D360;

        rot_y.degrees += DY * millis;
        while ( rot_y.degrees > D360 ) rot_y.degrees -= D360;

        rot_z.degrees += DZ * millis;
        while ( rot_z.degrees > D360 ) rot_z.degrees -= D360;
    }



    protected void showFPSInTitle() {
        gameController.uiHandler.post(new Runnable(){
                public void run() {
                    setTitle(""+origTitle + " -- "+gameController.getFPS()+"fps");
                }
            });
    }

}

