package chum.gl;

import android.graphics.Typeface;
import android.test.AndroidTestCase;


/**
 */
public class TextTests extends AndroidTestCase {

    protected MockGL10 gl10;
    protected MockRenderContext renderContext;


    protected void setUp() {
        gl10 = new MockGL10();
        renderContext = new MockRenderContext(getContext(),gl10);
    }

    
    public void testBuildText() {
        Font testFont = new Font(renderContext,Typeface.DEFAULT,30);
        Text text = testFont.buildText("This is a test");
    }
        
}
