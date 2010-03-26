package chum.gl;

import junit.framework.TestCase;


/**
 */
public class GLColorTests extends TestCase {

    public void test_init_to_black() {
        GLColor c = new GLColor();
        assertTrue("should be black",c.equals(GLColor.BLACK));
    }


    public void test_set_string() {
        GLColor c = new GLColor();
        c.set("#ffffff");
        assertTrue("should be white",c.equals(GLColor.WHITE));

    }
        
}
