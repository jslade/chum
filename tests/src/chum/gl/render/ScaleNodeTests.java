package chum.gl.render;

import chum.fp.*;
import chum.gl.*;


/**
 */
public class ScaleNodeTests extends MockGLTestCase {
    

    protected void setUp() {
        super.setUp();
    }


    public void testNoScale() {
        ScaleNode scale = new ScaleNode();
        assertEquals(FP.ONE,scale.scale);
        
        scale.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        scale.update(0);
        assertEquals(0,mockGL.numCommands());
    }

        
    public void testScale() {
        ScaleNode scale = new ScaleNode(FP.PI);

        scale.onSurfaceCreated(mockContext);
        assertEquals(0,mockGL.numCommands());

        scale.update(0);
        assertEquals(1,mockGL.numCommands());
        assert(mockGL.contains("glScalex"));

        scale.push = true;
        mockGL.clear();
        scale.update(0);
        assertEquals(3,mockGL.numCommands());
        assert(mockGL.contains("glPushMatrix",
                               "glScalex",
                               "glPopMatrix"));

    }

        
}
