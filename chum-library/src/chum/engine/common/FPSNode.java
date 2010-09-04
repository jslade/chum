package chum.engine.common;

import chum.engine.GameController;
import chum.engine.GameEvent;
import chum.engine.GameNode;
import chum.gl.Font;
import chum.gl.Text;
import chum.gl.Font.Glyph;
import chum.util.Log;


/**
   FPSNode is a common helper node that logs the current FPS, either to the logger,
   to a TextNode, or both.
*/
public class FPSNode extends GameNode {

    /** The interval (int milliseconds) between updating the FPS */
    public long interval = 3000;

    /** The most recent FPS */
    public int fps = 0;

    public int count = 0;
    
    public long targetInterval2;

    public long targetInterval3;
    
    /** The longest frame in the last fps calculation period */
    public long longestFrame;
    
    /** The shortest frame in the last fps calculation period */
    public long shortestFrame;
    
    /** Number of frame that exceed targetInterval */
    public int longFrames;
    
    /** Number of frames that exceed targetInverval2 */
    public int longFrames2;
    
    /** Number of frames that exceed targetInterval3 */
    public int longFrames3; 
        
        
        
    /** Whether to log to the logger */
    public boolean toLogger = false;

    /** Separate thread for printing log messages */
    public LoggerThread loggerThread;
    
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
        
        targetInterval2 = gameController.targetInterval * 2;
        targetInterval3 = gameController.targetInterval * 3;        
        
        reset();
        postUpDelayed(GameEvent.obtain(0,this),interval); // kick off the cycle
        
        if ( toLogger && loggerThread == null ) {
            loggerThread = new LoggerThread();
            loggerThread.start();
        }
    }

    
    @Override
    public boolean updatePrefix(long frameDelta) {
        count++;
        if ( frameDelta > gameController.targetInterval ) {
            longFrames++;
            if ( frameDelta > targetInterval2 ) {
                longFrames2++;
                if (frameDelta > targetInterval3) {
                    longFrames3++;
                }
            }
            if ( frameDelta > longestFrame )
                longestFrame = frameDelta;
        }
        else if ( frameDelta < shortestFrame ) {
            shortestFrame = frameDelta;
        }
        return false;
    }
    
    
    @Override
    public boolean onGameEvent(GameEvent event) {
        if ( event.object == this ) {
            showFPS();
            return true;
        }

        return super.onGameEvent(event);
    }


    public void reset() {
        count = 0;
        longFrames = longFrames2 = longFrames3 = 0;
        longestFrame = shortestFrame = gameController.targetInterval;
    }
    
    
    public void showFPS() {
        fps = gameController.getFPS();
        if ( callback != null )
            callback.run(this, fps);

        if ( text != null ) {
            updateText(fps);
        }


        if ( toLogger ) {
            synchronized(loggerThread) {
                loggerThread.fps = fps;
                loggerThread.count = count;
                loggerThread.longFrames = longFrames;
                loggerThread.longFrames2 = longFrames2;
                loggerThread.longFrames3 = longFrames3;
                loggerThread.longestFrame = longestFrame;
                loggerThread.shortestFrame = shortestFrame;
                loggerThread.notify();
            }
        }

        reset();
        
        // Show it again in the future
        postUpDelayed(GameEvent.obtain(0,this),interval);
    }


    public void setText(chum.gl.Text text) {
        this.text = text;
    }


    protected void updateText(int fps) {
        //char hundreds = (char)(fps / 100);
        fps %= 100;
        char tens = (char)(fps / 10);
        char ones = (char)(fps % 10);
        
        Font font = text.font;
        if ( font == null ) return;

        if ( glyphs == null ) glyphs = new Glyph[2];
        glyphs[0] = font.getGlyph((char)('0' + tens));
        glyphs[1] = font.getGlyph((char)('0' + ones));
        
        text.setGlyphs(glyphs,0,2);
    }


    public interface Callback {
        public void run(FPSNode node, int fps);
    }
    
    
    public class LoggerThread extends Thread {
        
        public boolean running = true;

        @Override
        public void run() {
            while(running) {
                synchronized(this) {
                    try { this.wait(); }
                    catch(InterruptedException e) {}
                    
                    Log.d("FPS = %d #frames=%d #long=%d/%d/%d longest=%d shortest=%d",
                          fps, count,
                          longFrames, longFrames2, longFrames3,
                          longestFrame, shortestFrame);
                }
            }
        }

        int fps;
        int count;
        int longFrames, longFrames2, longFrames3;
        long longestFrame, shortestFrame;
    }

}
