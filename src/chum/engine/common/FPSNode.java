package chum.engine.common;

import chum.engine.GameController;
import chum.engine.GameEvent;
import chum.engine.GameNode;
import chum.gl.Text;
import chum.gl.Font;
import chum.gl.Font.Glyph;
import chum.util.Log;


/**
   FPSNode is a common helper node that logs the current FPS, either to the logger,
   to a TextNode, or both
*/
public class FPSNode extends GameNode {

    /** The interval (int milliseconds) between updating the FPS */
    public long interval = 3000;

    /** The most recent FPS */
    public int fps = 0;

    /** Whether to log to the logger */
    public boolean toLogger = true;

    /** Callback to be executed on each update */
    public Callback callback;


    /** The Text to display in */
    public Text text;

    /** The Glyphs for updating the text */
    protected Glyph[] glyphs;

    

    public FPSNode() {
        super();
    }
        
    public FPSNode(Callback callback) {
        super();
        this.callback = callback;
    }
        

    @Override
    public void onSetup(GameController gameController) {
        super.onSetup(gameController);
        showFPS(); // kick off the cycle
    }

    

    @Override
    public boolean onGameEvent(GameEvent event) {
        if ( event.object == this ) {
            showFPS();
            return true;
        }

        return super.onGameEvent(event);
    }


    public void showFPS() {
        fps = gameController.getFPS();
        if ( callback != null )
            callback.run(this, fps);

        if ( text != null ) {
            updateText(fps);
        }


        if ( toLogger )
            Log.d("FPS = %d", fps);

        // Show it again in the future
        postUpDelayed(GameEvent.obtain(0,this),interval);
    }


    public void setText(chum.gl.Text text) {
        this.text = text;
    }


    protected void updateText(int fps) {
        char hundreds = (char)(fps / 100);
        fps %= 100;
        char tens = (char)(fps / 10);
        char ones = (char)(fps % 10);
        
        Font font = text.font;
        if ( font == null ) return;

        if ( glyphs == null ) glyphs = new Glyph[3];
        glyphs[0] = font.getGlyph((char)('0' + hundreds));
        glyphs[1] = font.getGlyph((char)('0' + tens));
        glyphs[2] = font.getGlyph((char)('0' + ones));
        
        text.setGlyphs(glyphs,0,3);
    }


    public interface Callback {
        public void run(FPSNode node, int fps);
    }

}
