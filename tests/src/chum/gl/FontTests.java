package chum.gl;

import chum.gl.Font.Glyph;
import chum.util.Log;

import android.graphics.Typeface;
import android.test.AndroidTestCase;


/**

 */
public class FontTests extends AndroidTestCase {
    
    protected MockGL10 gl10;
    protected MockRenderContext renderContext;


    protected void setUp() {
        gl10 = new MockGL10();
        renderContext = new MockRenderContext(getContext(),gl10);
    }

    
    public void testCreateGlpyhs() {
        Glyph g1 = Glyph.obtain().set('a',0,0,10,10,0,0,1,1);
        g1.recycle();

        int before = Glyph.instanceCount();

        Glyph g2 = Glyph.obtain().set('b',10,0,20,20,0,0,1,1);
        g2.recycle();

        assertEquals(before,Glyph.instanceCount());
    }


    public void testLoadTypeface() {
        Font testFont = new Font(renderContext);
        testFont.loadFromTypeface(Typeface.MONOSPACE,20);

        Glyph space = testFont.getGlyph(' ');
        Log.d("testLoadTypeface: space = "+space);
        assertNotNull(space);
        assert(space.width != 0);

        Glyph big_a = testFont.getGlyph('A');
        Glyph lil_a = testFont.getGlyph('a');
        assertNotNull(big_a);
        assertNotNull(lil_a);
        Log.d("testLoadTypeface: A = "+big_a);
        Log.d("testLoadTypeface: a = "+lil_a);

        assert(big_a.x != lil_a.x);
        assert(big_a.texU != lil_a.texV);
    }


    public void testLoadFontAsset() {
        Font testFont = new Font(renderContext);
        testFont.loadFromAsset("fonts/Tattooz1.ttf");

        Glyph space = testFont.getGlyph(' ');
        Log.d("testLoaddFontAsset: space = "+space);
        assertNotNull(space);
        assert(space.width != 0);

        Glyph big_a = testFont.getGlyph('A');
        Glyph lil_a = testFont.getGlyph('a');
        assertNotNull(big_a);
        assertNotNull(lil_a);
        Log.d("testLoadFontAsset: A = "+big_a);
        Log.d("testLoadFontAsset: a = "+lil_a);

        assert(big_a.x != lil_a.x);
        assert(big_a.texU != lil_a.texV);
    }


    public void testBuildText() {
        Font testFont = new Font(renderContext,Typeface.DEFAULT,30);
        Text text = testFont.buildText("This is a test");
        assertEquals(testFont,text.font);
        assertEquals(14,text.maxGlyphs);
    }
}
