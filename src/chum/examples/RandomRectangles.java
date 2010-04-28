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
*/
public class RandomRectangles extends GameActivity
{
    ColorNode colorNode;
    MeshNode quadNode;
    Mesh quad;
    int[] verts = {
        0,      0,      0,  // lower left
        FP.ONE, 0,      0,  // lower right
        FP.ONE, FP.ONE, 0,  // upper right
        0,      FP.ONE, 0,  // upper left 
    };

    FPSNode fpsNode;
    Text fpsText;

    
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
                // - the root node is an orthographic (2D) projection, with
                //   coordinates that range from [0,1] in both x and y.
                // 
                //   - ClearNode to clear the scene
                //   - ColorNode to set the current draw color
                //   - MeshNode to draw the mesh (just a two-triangle quad)
                //   - TextNode to display the FPS
                protected RenderNode createRenderTree() {
                    OrthographicProjection ortho = new OrthographicProjection() {
                            protected void setExtents(int width, int height) {
                                left = 0f;
                                right = 1f;
                                bottom = 0f;
                                top = 1f;
                            }
                        };

                    ortho.addNode(new ClearNode());

                    // Draw the rect
                    ortho.addNode(colorNode = new ColorNode(new Color()));

                    Mesh quad = createQuad();
                    ortho.addNode(quadNode = new MeshNode(quad,GL10.GL_TRIANGLE_FAN));

                    // Draw the FPS text
                    fpsText = new Text(3);
                    fpsNode.setText(fpsText);

                    TextNode fpsTextNode = new TextNode(fpsText);
                    fpsTextNode.setPosition(new Vec3(new Vec3(.1f,.1f,0f)));
                    fpsTextNode.setScale(FP.floatToFP(0.005f));
                    fpsTextNode.setColor(Color.WHITE);
                    ortho.addNode(fpsTextNode);

                    return ortho;
                }

                
                public void onSurfaceCreated(RenderContext renderContext) {
                    super.onSurfaceCreated(renderContext);
                    fpsText.font = new Font(renderContext,Typeface.DEFAULT,30);
                }
            });
    }



    protected Mesh createQuad() {
        quad = new Mesh(true, false, true, 4, 4,
                        new VertexAttribute(VertexAttributes.Usage.Position));
        quad.setVertices(verts);

        short[] indices = { 0, 1, 2, 3 };
        quad.setIndices(indices);
        return quad;
    }


    protected void randomizeQuad() {
        int x2 = GameController.random.nextInt(FP.ONE);
        int y2 = GameController.random.nextInt(FP.ONE);
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

        quad.setVertices(verts);
    }



    protected void randomizeColor() {
        colorNode.color.red = GameController.random.nextInt(FP.ONE+1);
        colorNode.color.green = GameController.random.nextInt(FP.ONE+1);
        colorNode.color.blue = GameController.random.nextInt(FP.ONE+1);
    }


    protected void showFPSInTitle() {
        gameController.uiHandler.post(new Runnable(){
                public void run() {
                    setTitle(""+origTitle + " -- "+gameController.getFPS()+"fps");
                }
            });
    }

}

