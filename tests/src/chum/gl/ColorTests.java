package chum.gl;

import junit.framework.TestCase;


/**
 */
public class ColorTests extends TestCase {

    public void test_init_to_black() {
        Color c = new Color();
        assertTrue("should be black",c.equals(Color.BLACK));
    }


    public void test_set_string() {
        Color c = new Color();
        c.set("#ffffff");
        assertTrue("should be white",c.equals(Color.WHITE));

    }
        
}
