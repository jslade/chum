package chum.examples;

import chum.engine.*;
import chum.engine.common.*;
import chum.fp.*;
import chum.gl.*;
import chum.gl.render.*;
import chum.util.Log;

import android.graphics.Typeface;
import android.os.Bundle;
import javax.microedition.khronos.opengles.GL10;



/**
   Draw random rectangles as fast as possible.  

   On every frame, the logic tree updates a single rectangle with new random coordinates,
   as well as a random color.  The render tree just clears the scene and draws that rectangle
   (which is really a mesh of two triangles).

   The draw rate should be limited by the hardware refresh cycle -- not likely to be more than
   60Hz.  The frames-per-second rate (FPS) is displayed in both the title bar, and in a
   Text object drawn on top of the rectangle.  This uses a special helper class called
   {@link FPSNode} that automatically updates the text on a regular basis.

   This example shows:
   - How to set up a standard 2D projection
   - How to update a mesh with dynamic vertices
   - How to display the FPS with a Text object
*/
public class RandomRectangles extends GameActivity
{
    ColorNode colorNode;
    MeshNode quadNode;
    int[] verts = {
        0, 0, 0,  // lower left
        0, 0, 0,  // lower right
        0, 0, 0,  // upper right
        0, 0, 0,  // upper left 
    };

    FPSNode fpsNode;
    TextNode fpsTextNode;

    
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
                // - one to generate the random color and rectangle position
                // - one to periodically display the FPS
                protected GameNode createLogicTree() {
                    GameNode node = new GameNode();

                    node.addNode(new GameNode(){
                            public boolean update(long millis) {
                                randomizeQuad();
                                randomizeColor();
                                return true;
                            }
                        });

                    node.addNode(fpsNode = new FPSNode(){
                            public void showFPS() {
                                super.showFPS();
                                showFPSInTitle();
                            }
                        });

                    return node;
                }

                // The render tree consists of:
                // - the root node is an orthographic (2D) projection
                //   - ClearNode to clear the scene
                //   - ColorNode to set the current draw color
                //   - MeshNode to draw the mesh (just a two-triangle quad)
                //   - TextNode to display the FPS
                protected RenderNode createRenderTree() {
                    Standard2DNode base = new chum.gl.render.Standard2DNode();
                    base.addNode(new ClearNode());

                    // Draw the rect
                    base.addNode(colorNode = new ColorNode(new Color()));

                    Mesh quad = createQuad();
                    base.addNode(quadNode = new MeshNode(quad));

                    // Draw the FPS text
                    fpsTextNode = new TextNode();
                    fpsTextNode.setPosition(new Vec3(10f,10f,0f));
                    fpsTextNode.setColor(Color.RED);
                    base.addNode(fpsTextNode);

                    return base;
                }

                
                public void onSurfaceCreated(RenderContext renderContext) {
                    super.onSurfaceCreated(renderContext);

                    Font font = new Font(renderContext,Typeface.DEFAULT,30);
                    fpsTextNode.setText(font.buildText("--"));
                    fpsNode.setText(fpsTextNode.text);
                }
            });
    }



    protected Mesh createQuad() {
        Mesh quad = new Mesh(true, false, true, 4, 4,
                             new VertexAttribute(VertexAttributes.Usage.Position));
        quad.setVertices(verts);

        short[] indices = { 0, 1, 2, 3 };
        quad.setIndices(indices);
        quad.type = GL10.GL_TRIANGLE_FAN;

        return quad;
    }


    protected void randomizeQuad() {
        int x2 = GameController.random.nextInt(FP.intToFP(renderContext.width)) + 1;
        int y2 = GameController.random.nextInt(FP.intToFP(renderContext.height)) + 1;
        int x1 = GameController.random.nextInt(x2);
        int y1 = GameController.random.nextInt(y2);
            
        verts[0]  = x1;
        verts[1]  = y1;
        verts[3]  = x2;
        verts[4]  = y1;
        verts[6]  = x2;
        verts[7]  = y2;
        verts[9]  = x1;
        verts[10] = y2;

        quadNode.mesh.setVertices(verts);
    }



    protected void randomizeColor() {
        colorNode.color.red = GameController.random.nextInt(FP.ONE+1);
        colorNode.color.green = GameController.random.nextInt(FP.ONE+1);
        colorNode.color.blue = GameController.random.nextInt(FP.ONE+1);
    }


    /**
       This method gets called from the game thread (from FPSNode), but
       updateing the application titlebar has to be done in the UI thread,
       because it involves changing the view.  So a Runnable has to be
       posted to the UI thread to do the update
    */
    protected void showFPSInTitle() {
        gameController.uiHandler.post(new Runnable(){
                public void run() {
                    setTitle(""+origTitle + " -- "+gameController.getFPS()+"fps");
                }
            });
    }

}

