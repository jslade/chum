package chum.examples;

import chum.engine.*;
import chum.engine.common.*;
import chum.fp.*;
import chum.gl.*;
import chum.gl.render.*;
import chum.input.TouchInputNode;
import chum.util.Log;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import javax.microedition.khronos.opengles.GL10;



/**
   Show some text animated in various ways.

   Each iteration starts out with a text string centered in the screen.  When the text is
   touched, one of a number of animations is chosen at random, and that animation sequence
   is started.  When it finishes, the interation starts again, with the text centered
   and waiting to be touched.
*/
public class AnimatedTextExample extends GameActivity
{
    // The node in the render tree for displaying the text
    TextNode textNode;

    // The center point of the screen -- used to reset the text
    // after each animation completes
    Vec3 center = new Vec3();

    // The bounding box of the text at it's center position,
    // used for testing when it gets 'touched'
    Mesh.Bounds textBounds;


    // The current animation
    GameSequence animation;


    // Some events that happen during the 'game'
    static final int TouchedText = 1;
    static final int AnimationStarted = 2;
    static final int AnimationEnded = 3;
    static final int ResetStarted = 4;
    static final int ResetEnded = 5;


    // The StateNode tracks the current game state, manages state
    // transitions
    static final int WaitForTouch = 1;
    static final int Animate = 2;
    static final int Reset = 3;
    int state = WaitForTouch;
    
    class StateNode extends GameNode {

        // Transitions between states are controlled by events
        @Override
        public boolean onGameEvent(GameEvent event) {
            switch(event.type) {
            case TouchedText:
                Log.d("Touched the text!");
                startAnimation();
                return true;

            case AnimationStarted:
                Log.d("AnimationStarted!");
                return true;

            case AnimationEnded:
                Log.d("AnimationEnded!");
                postUp(GameEvent.obtain(ResetStarted));
                return true;

            case ResetStarted:
                Log.d("ResetStarted!");
                reset();
                return true;

            case ResetEnded:
                Log.d("ResetEnded!");
                state = WaitForTouch;
                return true;


            case GameEvent.SEQUENCE_START:
                return true;

            case GameEvent.SEQUENCE_END:
                if ( event.object == animation )
                    endAnimation();
                return true;


            default:
                return false;
            }
        }


        // The reset() method just moves the text back to its centered position
        void reset() {
            // Get the text bounds, when it is at its center position (one time)
            if ( textBounds == null ) getTextBounds();

            textNode.setPosition(center);
            textNode.setScale(FP.ONE);
            textNode.setAngle(0);
            textNode.setColor(Color.BLACK);

            postUp(GameEvent.obtain(ResetEnded));
        }


        // Compute the bounding box of the Text, for determining when it 
        // gets touched.
        void getTextBounds() {
            // Now compute the mesh boundary, one time
            textBounds = Mesh.Bounds.obtain().update(textNode.text);
        }

        // Choose the next animation sequence and start it
        void startAnimation() {
            Vec3 from = new Vec3();
            Vec3 to = new Vec3();

            int which = gameController.random.nextInt(6);
            Log.d("Picking animation %d", which);

            switch(which) {
            case 0:
                // Scale the text down to nothing
                animation = textNode.animateScale(FP.ONE*2,0,1000);
                break;
            case 1:
                // Scale the text up, and fade out at the same time
                animation = new GameSequence.Parallel();
                animation.addNode(textNode.animateScale(FP.ONE,FP.intToFP(5),1000));
                animation.addNode(textNode.animateAlpha(FP.ONE,0,1000));
                break;
            case 2:
                // Rotate clockwise
                animation = textNode.animateAngle(0,FP.intToFP(360),1000);
                break;
            case 3:
                // Rotate counter-clockwise
                animation = textNode.animateAngle(FP.intToFP(360),0,1000);
                break;
            case 4:
                // Move the text to the top of the screen, and shrink it down
                // Once it's there, pause for a bit
                animation = new GameSequence.Series();
                GameSequence par = new GameSequence.Parallel();
                animation.addNode(par);
                to.set(center.x,FP.intToFP(renderContext.height-30),0);
                par.addNode(textNode.animatePosition(to,500));
                par.addNode(textNode.animateScale(FP.ONE,FP.floatToFP(0.5f),550));
                animation.addNode(new GameSequence(500));
                break;
            case 5:
                // Shake side to side
                animation = new GameSequence.Series();
                int offset = FP.intToFP(30);
                to.set(center);

                from.set(center);
                to.x = center.x - offset;
                animation.addNode(textNode.animatePosition(from,to,50));

                from.set(to);
                to.x = center.x + offset;
                animation.addNode(textNode.animatePosition(from,to,70));

                from.set(to);
                to.x = center.x - offset;
                animation.addNode(textNode.animatePosition(from,to,70));

                from.set(to);
                to.x = center.x + offset;
                animation.addNode(textNode.animatePosition(from,to,70));

                break;
            }

            // Make the animation a child if this node, so this node
            // gets the notification
            addNode(animation);

            postUp(GameEvent.obtain(AnimationStarted));
        }

        
        void endAnimation() {
            animation = null;
            postUp(GameEvent.obtain(AnimationEnded));
        }

    }


    // The TouchNode handles touch input events, specifically
    // looking for when the text gets touched.
    class TouchNode extends TouchInputNode {

        @Override
        protected void handle(View v, MotionEvent event) {
            if ( state != WaitForTouch ) return;
            if ( touchedText(event.getX(),event.getY()) ) {
                state = Animate;
                postUp(GameEvent.obtain(TouchedText));
            }
        }

        boolean touchedText(float eventX, float eventY) {
            // Get the screen coords into 'world' coords
            // The 2D view is essentially the same as the screen,
            // except that it uses FP values for everything
            touch.x = FP.floatToFP(eventX) - textNode.position.x;
            touch.y = FP.floatToFP(eventY) - textNode.position.y;

            return textBounds.contains(touch);
        }

        Vec3 touch = new Vec3();
    }
            

    @Override
    protected GameTree createGameTree() {
        return (new GameTree() {

                // The logic tree consists of two nodes:
                // - a node to control the active animation (if any)
                // - a TouchNode to register touch events
                @Override
                protected GameNode createLogicTree() {
                    GameNode node = new GameNode();
                    node.addNode(new StateNode().setName("state"));
                    node.addNode(new TouchNode().setName("touch"));
                    return node;
                }

                // The render tree consists of:
                // - the root node is an orthographic (2D) projection
                //   - ClearNode to clear the scene
                //   - "animation" node to be the root for the animations
                //   - ColorNode to set the current draw color
                //   - TextNode to display the text
                @Override
                protected RenderNode createRenderTree() {
                    Standard2DNode base = new chum.gl.render.Standard2DNode();
                    base.addNode(new ClearNode(Color.WHITE));

                    // Draw the text
                    textNode = new TextNode();
                    textNode.setName("text");
                    textNode.setColor(Color.BLACK);
                    textNode.setPosition(new Vec3());
                    base.addNode(textNode);

                    return base;
                }

                @Override
                public void onSurfaceCreated(RenderContext renderContext) {
                    super.onSurfaceCreated(renderContext);

                    Font font = new Font(renderContext,Typeface.DEFAULT_BOLD,30);
                    font.spacing = FP.intToFP(5);
                    textNode.setText(font.buildText("Touch me!"));
                    
                    // The text should be centered at its given location,
                    // not placed at its lower-left corner
                    Mesh.Transform xform = new Mesh.Transform();
                    xform.center(textNode.text);
                }
                
                @Override
                public void onSurfaceChanged(int width, int height) {
                    center.set(FP.intToFP(width)/2, FP.intToFP(height)/2, 0);

                    // The whole thing gets kicked off by the posting
                    // of this event:
                    tree.postDown(GameEvent.obtain(ResetStarted));
                }
            });
    }


}

