package chum.gl.render;

import chum.gl.*;


/**
 */
public class ColorNodeTests extends MockGLTestCase {
    

    protected void setUp() {
        super.setUp();
    }


    public void testWhite() {
        ColorNode colorNode = new ColorNode(new Color(Color.WHITE));

        colorNode.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        colorNode.update(0);
        assert(mockGL.contains("glColorx"));
    }

        
}
