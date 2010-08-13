package chum.examples;

import chum.engine.*;
import chum.gl.*;
import chum.gl.render.*;


/**
   SkaterDroid is a mini game in which the Android tries to avoid obstacles on his skateboard.
   The droid can move up or down via a touch control.
*/
public class SkaterDroidExample extends GameActivity
{
    @Override
    protected GameTree createGameTree() {
        return (new GameTree() {

                // The logic tree consists of two nodes:
                // - a node to control the active animation (if any)
                // - a TouchNode to register touch events
                @Override
                protected GameNode createLogicTree() {
                    GameNode node = new GameNode();
                    node.addNode(new chum.examples.skater.StateNode().setName("state"));
                    node.addNode(new chum.examples.skater.TouchController().setName("touch"));
                    return node;
                }

                // The render tree consists of:
                // - the root node is an orthographic (2D) projection
                //   - ClearNode to clear the scene
                //   - A SpriteNode to draw the android
                //   - A SpriteBatch to draw the scrolling backgrounds
                //   - TextNode to display the elapsed time
                @Override
                protected RenderNode createRenderTree() {
                    Standard2DNode base = new chum.gl.render.Standard2DNode();
                    base.addNode(new ClearNode(Color.BLACK));

                    return base;
                }
            });
    }

    @Override
    protected void onSurfaceCreated(RenderContext renderContext) {
        
    }
                
    @Override
    public void onSurfaceChanged(int width, int height) {

    }


}

