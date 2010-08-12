package chum.examples;

import chum.engine.*;
import chum.engine.common.*;
import chum.f.*;
import chum.gl.*;
import chum.gl.render.*;

import android.os.Bundle;



/**
   Draw a pyramid and spin it around various axes
*/
public class SpinningColorPyramid extends GameActivity
{
    MeshNode pyramidNode;
    Mesh pyramid;
    RotateNode rot_x, rot_y, rot_z;

    
    /** Keep track of the original title string, so it can be updated (appended) */
    CharSequence origTitle;


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
                    base.setPerspective(90f,0f,1f,20f);
                    base.addNode(new CameraNode(new Vec3(0f,2f,-10f), Vec3.ORIGIN));
                    base.addNode(new ClearNode(new Color("#111133")));
                                        
                    pyramid = createPyramid();
                    pyramidNode = new MeshNode(pyramid);

                    rot_x = new RotateNode(0,Vec3.X_AXIS);
                    rot_y = new RotateNode(0,Vec3.Y_AXIS);
                    rot_z = new RotateNode(0,Vec3.Z_AXIS);
                    base.addNode(rot_x);
                    rot_x.addNode(rot_y);
                    rot_y.addNode(rot_z);
                    rot_z.addNode(pyramidNode);

                    // The first RotateNode has push=true, so that it pushes
                    // the modelview matrix onto the stack going in, the pops it off
                    // going out to restore the original transformation.
                    rot_x.push = true;

                    // Add an extra node displaying a 3D axis
                    // just to show orientation more clearly
                    MeshNode axisNode = new MeshNode(chum.util.mesh.Axis.create3D(6f));
                    rot_z.addNode(axisNode);
                                 
                    return base;
                }
            });
    }



    /**
       This gets called once to create the Mesh representing the pyrimid shape.
       The mesh is comprised of 4 vertices forming 6 triangles.  Each vertex is
       colored differently, and the points between the vertices are a linear blend
       of the colors at the corners.
    */
    protected Mesh createPyramid() {
        MeshBuilder builder = new MeshBuilder(true,
                                              new VertexAttribute(VertexAttributes.Usage.Position),
                                              new VertexAttribute(VertexAttributes.Usage.Color));
        builder.addVertex(new Vec3(-3f,-2f,-3f), Color.BLACK);
        builder.addVertex(new Vec3(3f,-2f,-3f), Color.RED);
        builder.addVertex(new Vec3(3f,-2f,3f), Color.WHITE);
        builder.addVertex(new Vec3(-3f,-2f,3f), Color.BLUE);
        builder.addVertex(new Vec3(0f,4f,0f), Color.GREEN);

        builder.addIndex( 0, 1, 2,
                          0, 2, 3 ); // base
        builder.addIndex( 0, 4, 1,
                          1, 4, 2,
                          2, 4, 3,
                          3, 4, 0 ); // top
        
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

